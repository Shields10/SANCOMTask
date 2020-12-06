package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.AssetDetail;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.CustomerTemp;
import com.ppwallet.model.Token;
import com.ppwallet.model.Transaction;
import com.ppwallet.model.User;
import com.ppwallet.utilities.Utilities;

public class BatchDao extends HandleConnections {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String className = BatchDao.class.getSimpleName();

	public ConcurrentHashMap<String, CustomerTemp> getTempTableDetails(String batchDate) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ConcurrentHashMap<String, CustomerTemp> hashAllTempTable = null;
		try{
			connection = super.getConnection();	

			//Query 1 
 			query = "select document_number, othernames, surname, custemail, class, date_of_issue, date_of_expiry,"
 					+ " date_of_birth, gender, idnumber, passport_number,"
 					+ "  phone, physical_address, pin, cardnumber, expirydate  from edl_perso_temp_table where date_of_issue=?";

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, batchDate); 						 
			
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 hashAllTempTable = new ConcurrentHashMap<String, CustomerTemp>();
				 	while(rs.next()){
				 		
				 		CustomerTemp m_CustomerTemp=new CustomerTemp();
				 		m_CustomerTemp.setAssetNumber( StringUtils.trim(rs.getString("document_number"))    );
				 		m_CustomerTemp.setCustomerName( StringUtils.trim(rs.getString("othernames"))  + StringUtils.trim(rs.getString("surname"))   );
				 		m_CustomerTemp.setCustomerEmail(StringUtils.trim(rs.getString("custemail"))    );
				 		m_CustomerTemp.setAssetSubClass( StringUtils.trim(rs.getString("class"))    );
				 		m_CustomerTemp.setAssetDateOfIssue( StringUtils.trim(rs.getString("date_of_issue"))    );	
				 		m_CustomerTemp.setAssetDateOfExpiry(StringUtils.trim(rs.getString("date_of_expiry"))    );	
				 		m_CustomerTemp.setDateOfBirth( StringUtils.trim(rs.getString("date_of_birth"))    );	
				 		m_CustomerTemp.setGender( StringUtils.trim(rs.getString("gender"))    );	
				 		m_CustomerTemp.setNationalId( StringUtils.trim(rs.getString("idnumber"))    );	
				 		m_CustomerTemp.setPassportNo( StringUtils.trim(rs.getString("passport_number"))    );	
				 		m_CustomerTemp.setCustomerContact( StringUtils.trim(rs.getString("phone"))    );	
				 		m_CustomerTemp.setAssetAddress( StringUtils.trim(rs.getString("physical_address"))    );	
				 		m_CustomerTemp.setKRAPin( StringUtils.trim(rs.getString("pin"))    );	;
				 		m_CustomerTemp.setCardNumber( StringUtils.trim(rs.getString("cardnumber"))    );	
				 		m_CustomerTemp.setCardDateOfExpiry( StringUtils.trim(rs.getString("expirydate"))    );	
				 		
				 		hashAllTempTable.put(m_CustomerTemp.getNationalId()+","+m_CustomerTemp.getPassportNo(),    m_CustomerTemp);
				 		} // end of while	
				 	
				 	} //end of if rs!=null check
			 if(hashAllTempTable!=null)
				 if(hashAllTempTable.size()==0)
					 hashAllTempTable=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getTempTableDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getTempTableDetails  is  "+e.getMessage());			
		}finally{			
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return hashAllTempTable;
				
		
	}

	public ConcurrentHashMap<String, String> getAllCustomerData() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ConcurrentHashMap<String, String> hashAllCustomerData = null;
		try{
			connection = super.getConnection();			
 			query = "select relationshipno, nationalid, passportno from customer_details";
			pstmt = connection.prepareStatement(query);			 						 			
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 hashAllCustomerData = new ConcurrentHashMap<String, String>();
				 	while(rs.next()){
				 hashAllCustomerData.put(StringUtils.trim(rs.getString("nationalid") + ","+  StringUtils.trim(rs.getString("passportno"))  ), StringUtils.trim(rs.getString("relationshipno")));
				 		} // end of while
				 	} //end of if rs!=null check
			 if(hashAllCustomerData!=null) if(hashAllCustomerData.size()==0) hashAllCustomerData=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllCustomerData  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllCustomerData  is  "+e.getMessage());			
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return hashAllCustomerData;
	}

	public boolean insertCustomerDataforBatch(ArrayList<AssetDetail> arrAssetDetails,
		ArrayList<CustomerDetails> arrCustomer, ArrayList<Token> arrToken) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		//ArrayList<Token> arrToken  = null;	
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
			 // Step 1 : Enter into customer_details

			 										//		1				2			3               4         5
				query = "insert into customer_details (relationshipno, customerid, customerpwd, customername, nationalid, "
						+ "								 passportno, 	gender, 	custemail,   custcontact,   address, "
						+ "									krapin, 	dateofbirth, 	status, 	expiry, 	createdon ) "
						+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?,  ?,  ? ) ";
								// 1  2  3  4  5  6   7  8  9  10 11 12 13 14  15
			pstmt = connection.prepareStatement(query);
			for(int i=0; i<arrCustomer.size(); i++  ) {	
				pstmt.setString(1, ((CustomerDetails)arrCustomer.get(i)).getRelationshipNo() ); 
				pstmt.setString(2,  ""  );
				pstmt.setString(3,  ""    );
				pstmt.setString(4,   ((CustomerDetails)arrCustomer.get(i)).getCustomerName()     );
				pstmt.setString(5,  ((CustomerDetails)arrCustomer.get(i)).getNationalId()     );
				pstmt.setString(6, ((CustomerDetails)arrCustomer.get(i)).getPassportNo()   ); 
				pstmt.setString(7,  ((CustomerDetails)arrCustomer.get(i)).getGender()  );
				pstmt.setString(8,  ((CustomerDetails)arrCustomer.get(i)).getEmail() );
				pstmt.setString(9,  ((CustomerDetails)arrCustomer.get(i)).getContact() );
				pstmt.setString(10,  ((CustomerDetails)arrCustomer.get(i)).getAddress() );
				pstmt.setString(11,  ((CustomerDetails)arrCustomer.get(i)).getKraPIN() );
				pstmt.setString(12,  ((CustomerDetails)arrCustomer.get(i)).getDateOfBirth() );
				pstmt.setString(13,  ((CustomerDetails)arrCustomer.get(i)).getStatus() );
				pstmt.setString(14,  ((CustomerDetails)arrCustomer.get(i)).getExpiry() );
				pstmt.setString(15,  Utilities.getMYSQLCurrentTimeStampForInsert() );

				
				  
				pstmt.addBatch();
	            if ((i + 1) % 1000 == 0 || (i + 1) == arrCustomer.size()) {
	            	try {
	                 pstmt.executeBatch();
	            	}catch (Exception e) {
	            		throw new Exception (" failed query "+query+" "+e.getMessage());
	            	}
	            }
			}
					
			pstmt.clearBatch();
			pstmt.close();
			PPWalletEnvironment.setComment(2,className,"1. Executed query "+query);
			// Step 2 : Insert the data into the asset tables

															//		1				2				3               	4         				5
				query = "insert into customer_asset_details (relationshipno, asset_type, 		asset_number, 		asset_date_of_issue, asset_date_of_expiry, "
				+ "											 asset_subclass, 	asset_serial_no, 	custcontact,   	address,   				status, "
				+ "												createdon ) "
				+ "values (?, ?, ?, ?, ?, ?,  ?, ?, ?,  ?,  ?  ) ";
						// 1  2  3  4  5  6   7  8  9  10  11  
				pstmt = connection.prepareStatement(query);
				for(int i=0; i<arrAssetDetails.size(); i++  ) {
					pstmt.setString(1, ((AssetDetail)arrAssetDetails.get(i)).getRelationshipNo() ); 
					pstmt.setString(2,  ((AssetDetail)arrAssetDetails.get(i)).getAssetType()  );
					pstmt.setString(3,   ((AssetDetail)arrAssetDetails.get(i)).getAssetNumber()      );
					pstmt.setString(4,   ((AssetDetail)arrAssetDetails.get(i)).getDateOfIssue()     );
					pstmt.setString(5,  ((AssetDetail)arrAssetDetails.get(i)).getDateOfExpiry()     );
					pstmt.setString(6, ((AssetDetail)arrAssetDetails.get(i)).getSubClass()   ); 
					pstmt.setString(7,  ((AssetDetail)arrAssetDetails.get(i)).getSerialNoIfApplicable()  );
					pstmt.setString(8,  ((AssetDetail)arrAssetDetails.get(i)).getCustCOntact() );
					pstmt.setString(9,  ((AssetDetail)arrAssetDetails.get(i)).getAssetAddress() );
					pstmt.setString(10,  ((AssetDetail)arrAssetDetails.get(i)).getAssetStatus() );
					pstmt.setString(11,  Utilities.getMYSQLCurrentTimeStampForInsert());

					
					
					pstmt.addBatch();
					if ((i + 1) % 1000 == 0 || (i + 1) == arrAssetDetails.size()) {
						try {
						pstmt.executeBatch();
						}catch (Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					}
				}
				
				pstmt.clearBatch();
				pstmt.close();
				PPWalletEnvironment.setComment(2,className,"2. Executed query "+query);


				// Step 4 : Insert data (temporarily) into the temp_blockchain_token . 
				//TODO THIS CODE NEEDS TO BE REMOVED LATER
														//		1      2				3					4			5		6
				query = "insert into temp_blockchain_token (tokenid, cardno, 		dateofexpiry, 		user_relno, username,  usertype)  "
				+ "  values (?, ?, ?, ?, ?, ?) ";
						//   1  2  3  4  5  6   
				pstmt = connection.prepareStatement(query);
				for(int i=0; i<arrToken.size(); i++  ) {
					pstmt.setString(1,   ((Token)arrToken.get(i)).getTokenNo()  ); 
					pstmt.setString(2, Utilities.encryptString( ((Token)arrToken.get(i)).getCardNo())  );
					pstmt.setString(3, Utilities.encryptString(  ((Token)arrToken.get(i)).getDateOfExpiry()    )  );
					pstmt.setString(4,   ((Token)arrToken.get(i)).getRelationshipNo()     );
					pstmt.setString(5,  ((Token)arrToken.get(i)).getUserName()     );
					pstmt.setString(6, ((Token)arrToken.get(i)).getUserType()   ); 
					
					
					pstmt.addBatch();
					if ((i + 1) % 1000 == 0 || (i + 1) == arrToken.size()) {
						try {
						pstmt.executeBatch();
						}catch (Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					}
				}
				
				pstmt.clearBatch();
				pstmt.close();
				PPWalletEnvironment.setComment(2,className,"3. Executed query "+query);
				// Step 5 : Insert data into the wallet_details . 
				
				
													//		1         2				3		 		 4			5		    6            7         8          9
					query = "insert into wallet_details (walletid, relationshipno, walletdesc, 	usertype, 	status,   currbal,  currencyid, lastupdated, createdon)  "
					+ "  values (?, ?, ?, ?, ?, ? ,?, ?, ?)  ";
					         //  1  2  3  4  5  6  7  8  9
					pstmt = connection.prepareStatement(query);
					for(int i=0; i<arrToken.size(); i++  ) {
						SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMdd");  formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));
						String walletId = (formatter1.format(new Date()))+( RandomStringUtils.random(10, false, true)).toString();
						((Token)arrToken.get(i)).setWalletId(walletId);
						
					pstmt.setString(1, ((Token)arrToken.get(i)).getWalletId() ); 
					pstmt.setString(2, ((Token)arrToken.get(i)).getRelationshipNo() ); 
					pstmt.setString(3,  "My Wallet" );
					pstmt.setString(4,   ((Token)arrToken.get(i)).getUserType()  );
					pstmt.setString(5,   "P"      );
					pstmt.setFloat(6,  Float.parseFloat("0")     );
					pstmt.setString(7, "KES"   ); 
					pstmt.setString(8, Utilities.getMYSQLCurrentTimeStampForInsert()  ); 
					pstmt.setString(9, Utilities.getMYSQLCurrentTimeStampForInsert()   ); 
					
					pstmt.addBatch();
					if ((i + 1) % 1000 == 0 || (i + 1) == arrToken.size()) {
						try {
							pstmt.executeBatch();
						}catch (Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
							}
						}
				
					
					}
					
					pstmt.clearBatch();
					pstmt.close();				
					PPWalletEnvironment.setComment(2,className,"4. Executed query "+query);
					// Step 6 : Insert data into the wallet_customer_rel . 
													//			1      	2				3				4				5		    6       
					query = "insert into wallet_customer_rel (walletid, tokenid, 	asset_type, 	relationshipno,   status,  creadtedon )  "
					+ "  values (?, ?, ?, ?, ?, ?) ";
							//   1  2  3  4  5  6   
					pstmt = connection.prepareStatement(query);
					for(int i=0; i<arrToken.size(); i++  ) {
					//SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMdd");  formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));
					// walletId = (formatter1.format(new Date())) + (Utilities.generateCVV2(10)).toString();
					//((Token)arrToken.get(i)).setWalletId(walletId);
					
					pstmt.setString(1, ((Token)arrToken.get(i)).getWalletId() ); 
					pstmt.setString(2,  ((Token)arrToken.get(i)).getTokenNo()  );
					pstmt.setString(3,   ((Token)arrToken.get(i)).getAssetType()      );
					pstmt.setString(4,   ((Token)arrToken.get(i)).getRelationshipNo()       );
					pstmt.setString(5,  "P"     );			
					pstmt.setString(6,  Utilities.getMYSQLCurrentTimeStampForInsert()  );			
					
					pstmt.addBatch();
					if ((i + 1) % 1000 == 0 || (i + 1) == arrToken.size()) {
						try {
								pstmt.executeBatch();
							}catch (Exception e) {
								throw new Exception (" failed query "+query+" "+e.getMessage());
							}
						}
					}
					
					pstmt.clearBatch();
					pstmt.close();				
					PPWalletEnvironment.setComment(2,className,"5. Executed query "+query);
				// Step 7: commit all data into the tables
				connection.commit();
				 //if(arrToken!=null) if(arrToken.size()==0) arrToken=null;
				result = true;
		}catch(Exception e){
			//connection.rollback(); arrToken=null;
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method insertCustomerDataforBatch  is  "+e.getMessage());
			throw new Exception ("The exception in method insertCustomerDataforBatch  is  "+e.getMessage());
		}finally{
		if(connection!=null) {
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
		}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}

	
	public static synchronized boolean insertIntoCardVault(ArrayList<Token> arrToken) throws Exception{
		PPWalletEnvironment.setComment(3,className,"in insertIntoCardVault  ");

		boolean result = false;	
		CloseableHttpClient client  = null;
		CloseableHttpResponse jresponse = null;
		
		try {
			PPWalletEnvironment.setComment(3,className,"in insertIntoCardVault try block  ");

			String chainName = PPWalletEnvironment.getBlockChainName();
			String streamName = "cardvault"; 
				
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			  credsProvider.setCredentials(new AuthScope(PPWalletEnvironment.getMultiChainRPCIP(), Integer.parseInt(PPWalletEnvironment.getMultiChainRPCPort())),
			  new UsernamePasswordCredentials(PPWalletEnvironment.getMultiChainUser(), PPWalletEnvironment.getRPCAuthKey()));
			// Connect to Blockchain and create the Token blocks 
			// store encrypted card no, cvv2, DOE, customerid, cardalias
				PPWalletEnvironment.setComment(3,className,"storing encrypted card no. etc  ");

				 client = HttpClients.custom().setDefaultCredentialsProvider( credsProvider ).build();
				 
				PPWalletEnvironment.setComment(3,className," *** after Credentials ");
				 
				if(arrToken!=null) {
					PPWalletEnvironment.setComment(3,className,"Now inserting data to Blockchain  ");

					if(arrToken.size()>0) {
						 for(int i=0;i<arrToken.size();i++) {
								String jsonString = "{\"tokendetails\": "
										+ "{ \"tokenid\": \""+ ((Token)arrToken.get(i)).getTokenNo()   +"\", "
										+  "\"cardnumber\":\""+ Utilities.encryptString( ((Token)arrToken.get(i)).getCardNo())  +"\", "
										+  "\"cardname\":\""+ ((Token)arrToken.get(i)).getUserName() +"\", "
										+  "\"dateofexpiry\": \""+ Utilities.encryptString(  ((Token)arrToken.get(i)).getDateOfExpiry())    +"\", "
										+  "\"urelno\" : \""+ ((Token)arrToken.get(i)).getRelationshipNo() +"\", "
										+  "\"utype\":\""+  ((Token)arrToken.get(i)).getUserType() +"\" "
										+ "}}";
	
								String jsonHexValue = Utilities.asciiToHex(jsonString);
								 HttpPost jrequest = new HttpPost( PPWalletEnvironment.getMultiChainRPCURLPORT() );
								 jrequest.setEntity( new StringEntity(  "{\"method\":\"publish\",\"params\":[\""+streamName+"\",\""+((Token)arrToken.get(i)).getTokenNo()+"\",\""+jsonHexValue+"\"],\"id\":1,\"chain_name\":\""+chainName+"\"}"  ) );
								
									jresponse = client.execute( jrequest );
									HttpEntity entity = jresponse.getEntity();
							 		JsonObject responseJson =  JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject();
							 		//PPWalletEnvironment.setComment(3,className,"Response after Blockchain addition is "+responseJson.toString());	
							 		//Thread.sleep(2);
						 }	
							PPWalletEnvironment.setComment(3,className,"****** after json loop");

						 
					}
				}			
				 result = true;	
		}catch(Exception e){
			result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method insertIntoCardVault  is  "+e.getMessage());
			throw new Exception ("The exception in method insertIntoCardVault  is  "+e.getMessage());
		}finally{
			try {
				if(client!=null)				client.close();
				if(jresponse!=null)				 jresponse.close();
			}catch (Exception ee) {
				PPWalletEnvironment.setComment(1,className,"The exception in method insertIntoCardVault, finally block is  "+ee.getMessage());
			}
		}
		return result;
	}
	
	
	public static synchronized boolean insertWalletTransaction(String transactionCode, String walletId, String systemreference,
			String topUpAmount, String txncurrencyid, String txnmode, String transactionDatetime) throws Exception {
	        boolean result = false;	
			CloseableHttpClient client  = null;
			CloseableHttpResponse jresponse = null;
			
			try {
				PPWalletEnvironment.setComment(3,className,"in insertIntoWalletledger   ");

				String chainName = PPWalletEnvironment.getBlockChainName();
				String streamName = "walletledger"; 
					
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				  credsProvider.setCredentials(new AuthScope(PPWalletEnvironment.getMultiChainRPCIP(), Integer.parseInt(PPWalletEnvironment.getMultiChainRPCPort())),
				  new UsernamePasswordCredentials(PPWalletEnvironment.getMultiChainUser(), PPWalletEnvironment.getRPCAuthKey()));
				// Connect to Blockchain and create the Token blocks 
					PPWalletEnvironment.setComment(3,className,"storing encrypted card no. etc  ");

					 client = HttpClients.custom().setDefaultCredentialsProvider( credsProvider ).build();
					 
					PPWalletEnvironment.setComment(3,className," *** after Credentials ");
					 //txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime
						PPWalletEnvironment.setComment(3,className,"Now inserting data to Blockchain  ");

						//Transaction walletTxn = new Transaction();
									String wallettopupjsonString = "{\"transactiondetails\": "
											+ "{ \"txncode\": \""+ transactionCode   +"\", "
											+  "\"walletid\":\""+ walletId +"\", "
											+  "\"sysreference\":\""+ systemreference +"\", "
											+  "\"txnamount\": \""+ topUpAmount   +"\", "
									    	+  "\"txncurrencyid\": \""+ txncurrencyid   +"\", "
											+  "\"txnmode\" : \""+ txnmode +"\", "
											+  "\"txndatetime\":\""+  transactionDatetime +"\" "
											+ "}}";
		
									String jsonHexValue = Utilities.asciiToHex(wallettopupjsonString);
									 HttpPost jrequest = new HttpPost( PPWalletEnvironment.getMultiChainRPCURLPORT() );
									 jrequest.setEntity( new StringEntity(  "{\"method\":\"publish\",\"params\":[\""+streamName+"\",\""+ transactionCode +"\",\""+jsonHexValue+"\"],\"id\":1,\"chain_name\":\""+chainName+"\"}"  ) );
									
										jresponse = client.execute( jrequest );
										HttpEntity entity = jresponse.getEntity();
								 		JsonObject responseJson =  JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject();
								 		//PPWalletEnvironment.setComment(3,className,"Response after Blockchain addition is "+responseJson.toString());	
								 		//Thread.sleep(2);	 		
					        result = true;	
			}catch(Exception e){
				result = false;
				PPWalletEnvironment.setComment(1,className,"The exception in method insertIntoCardVault  is  "+e.getMessage());
				throw new Exception ("The exception in method insertIntowalletledger  is  "+e.getMessage());
			}finally{
				try {
					if(client!=null)				client.close();
					if(jresponse!=null)				 jresponse.close();
				}catch (Exception ee) {
					PPWalletEnvironment.setComment(1,className,"The exception in method insertIntowalletledger, finally block is  "+ee.getMessage());
				}
			}
			return result;
		
		
	}


	
}

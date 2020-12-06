package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ppwallet.PPWalletEnvironment;
//import com.ppwallet.model.CardDetails;
//import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.Transaction;
import com.ppwallet.model.Wallet;
import com.ppwallet.utilities.Utilities;

public class OpsManageWalletDao extends HandleConnections {
	private static final long serialVersionUID = 1L;
	private static String classname = OpsManageWalletDao.class.getSimpleName();
	
	//this method gets individual wallet details*/
	public ArrayList<Wallet> getWalletDetails(String relationshipNo) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Wallet> arrWallet = null;
		
		try{
			connection = super.getConnection();	 
			                     //  1         2           3          4            5        6           7           8
				query = "select walletid, relationshipno, walletdesc, usertype,  status, currbal, currencyid,  lastupdated "
						+ " from wallet_details   "
						+ "  where relationshipno=?   ";
		  
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			//pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arrWallet = new ArrayList<Wallet>();
				 	while(rs.next()){	 
				 		Wallet wallet=new Wallet();
				 		wallet.setWalletId( StringUtils.trim(rs.getString("walletid"))    );
				 		wallet.setUserType( StringUtils.trim(rs.getString("usertype"))  );
				 		wallet.setRelationshipNo( StringUtils.trim(rs.getString("relationshipno"))  );
				 		wallet.setWalletDesc(StringUtils.trim(rs.getString("walletdesc"))  );
				 		wallet.setCurrentBalance(StringUtils.trim(rs.getString("currbal"))  );
				 		wallet.setCurrencyId(StringUtils.trim(rs.getString("currencyid"))  );
				 		wallet.setStatus(StringUtils.trim(rs.getString("status"))  );
				 		wallet.setLastUpdated(StringUtils.trim(rs.getString("lastupdated"))  );
				 		arrWallet.add(wallet);
				 		} 		 	
				 	} 
			 if(arrWallet!=null)
				 if(arrWallet.size()==0)
					 arrWallet=null;
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getWalletDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getWalletDetails  is  "+e.getMessage());			
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
		return arrWallet;
		
	}

	public ArrayList<Transaction> getAllWalletTransactionForUser(String relationshipNo) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Transaction> arrTransaction = null;
		
		try {
			connection = super.getConnection();	
                          //   1         2         3             4          5             6         7 
			query = "select  txncode, walletid, sysreference, txnamount, txncurrencyid,  txnmode, txndatetime "
					+ " from wallet_txn_bc where walletid in (select walletid from wallet_details where relationshipno=? )  order by txndatetime desc limit 0, 1000 ";
		
		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, relationshipNo);
		//pstmt.setString(2, "A");
		rs = (ResultSet)pstmt.executeQuery();
		 if(rs!=null){
			 arrTransaction = new ArrayList<Transaction>();
			 	while(rs.next()){
			 		Transaction m_Transaction=new Transaction();
			 		m_Transaction.setTxnCode( StringUtils.trim(rs.getString("txncode"))  );
			 		m_Transaction.setWalletId(StringUtils.trim(rs.getString("walletid"))  );
			 		m_Transaction.setSystemReference(StringUtils.trim(rs.getString("sysreference"))  );
			 		m_Transaction.setTxnAmount(StringUtils.trim(rs.getString("txnamount"))  );
			 		m_Transaction.setTxnCurrencyId(StringUtils.trim(rs.getString("txncurrencyid"))  );
			 		m_Transaction.setTxnMode(StringUtils.trim(rs.getString("txnmode"))  );
			 		m_Transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndatetime"))  );
			 		arrTransaction.add(m_Transaction);
			 		} // end of while
				//PPWalletEnvironment.setComment(3,classname," Wallet Transaction size is " + arrTransaction.size() );

			 	} 
		 if(arrTransaction!=null)
			 if(arrTransaction.size()==0)
				 arrTransaction=null;
		
		}catch(Exception e) {
				PPWalletEnvironment.setComment(1,classname,"The exception in method getAllWalletTransactionForUser  is  "+e.getMessage());
				throw new Exception ("The exception in method getAllWalletTransactionForUser  is  "+e.getMessage());		
			
		}finally {
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close(); if(pstmt!=null) pstmt.close();
		}
		return arrTransaction;
	}
	
	public boolean updateWalletLedgers(String senderRelationship, String senderWalletId, String receiverWalletId,
			String payAmount, String payComments) throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			boolean result = false;
			String transactionDatetime = null;
			String currencyId= "404";
			String senderTxnMode = "D";
			String receiverTxnMode = "C";
			String senderTransactionCode = null;
			String receiverTransactionCode = null;
			String userType = "C";
			String systemreference =null;
			String senderRefNo = null;
			String receiverRefNo = null;

			try {	
				 transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
				 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");    	       	 		 	
				 senderTransactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
			
		    //***step 1 getsender the wallet ballance
					String senderpreviouswalletBalance = null;
					 query = "select currbal, relationshipno  from wallet_details where walletid=?  ";
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, senderWalletId);
						rs = (ResultSet)pstmt.executeQuery();	
						 if(rs!=null){
						 	while(rs.next()){	 			 			
					   senderpreviouswalletBalance = (StringUtils.trim(rs.getString("currbal"))    );
					   senderRefNo = (StringUtils.trim(rs.getString("relationshipno"))    );
						PPWalletEnvironment.setComment(3,classname,"The Sender wallet Previous ballance for walletid!" +senderWalletId +"for userrelationshipno"+ senderRelationship +"is "+ senderpreviouswalletBalance );
								} // end of while
							} //end of if rs!=null check
					pstmt.close();
					
					//***********Check if sender has sufficient balance in the wallet************changes here
					if(Float.parseFloat(senderpreviouswalletBalance) >= Float.parseFloat(payAmount)) {
						
						PPWalletEnvironment.setComment(3,classname,"Balance is enough to send money. Current Balance is"+ senderpreviouswalletBalance );
		
				
						//**********step  2 get receiver the wallet ballance
					String receiverpreviouswalletBalance = null;
					 query = "select currbal, relationshipno  from wallet_details where walletid=?  ";
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, receiverWalletId);
						rs = (ResultSet)pstmt.executeQuery();	
						 if(rs!=null){
						 	while(rs.next()){	 			 			
						 		receiverpreviouswalletBalance = (StringUtils.trim(rs.getString("currbal"))  );
						 		receiverRefNo = (StringUtils.trim(rs.getString("relationshipno"))    );
								PPWalletEnvironment.setComment(3,classname,"The Receiver wallet Previous ballance for walletid!" +receiverWalletId +"is "+ receiverpreviouswalletBalance );

								} // end of while
							} //end of if rs!=null check
					pstmt.close();
					
					float senderbalanceafterDebit = Float.parseFloat(senderpreviouswalletBalance) - Float.parseFloat(payAmount);
					float receiverrbalanceafterCredit = Float.parseFloat(receiverpreviouswalletBalance) + Float.parseFloat(payAmount);
					PPWalletEnvironment.setComment(3,classname,"The Sender wallet ballance after Debit is" + senderbalanceafterDebit );
					PPWalletEnvironment.setComment(3,classname,"The Receiver wallet ballance after Credit is" + receiverrbalanceafterCredit );

	          //*******Step 3: Update the Sender wallet Ledger
			     query = " update wallet_details set currbal= ?, lastupdated = ? where  walletid=? ";
						pstmt = connection.prepareStatement(query);
						pstmt.setFloat(1, senderbalanceafterDebit);
						pstmt.setString(2, transactionDatetime); 
						pstmt.setString(3, senderWalletId); 
						try {
							PPWalletEnvironment.setComment(3,classname,"update sender wallet amount" + senderbalanceafterDebit );

							pstmt.executeUpdate();
							}catch(Exception e) {
								throw new Exception (" failed query "+query+" "+e.getMessage());
							}					//connection.commit();
						pstmt.close();
						// *******Step 4: Update the Receiver wallet Ledger
			 query = " update wallet_details set currbal= ?, lastupdated = ? where walletid=? ";
					pstmt = connection.prepareStatement(query);
					pstmt.setFloat(1, receiverrbalanceafterCredit);
					pstmt.setString(2, transactionDatetime); 
					pstmt.setString(3, receiverWalletId);
					try {
						PPWalletEnvironment.setComment(3,classname,"update receiver wallet amount" + receiverrbalanceafterCredit );

						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query);
						}					//connection.commit();
					pstmt.close();	
					
					//********here is where the sender wallet has insufficient balance
					 // Step 3: Record the wallet transaction in the Blockchain table
					
					// **********Step 5: Record the wallet transaction in the Blockchain table
					//*********5.1*****Sender Wallet
					senderTransactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
					receiverTransactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
					//systemreference = (senderWalletId)+( RandomStringUtils.random(10, false, true)).toString();
					systemreference =  PPWalletEnvironment.getCodeWalletToWalletP2P() +"-"+ senderTransactionCode+"-" ;
												// 1		2			3			4			5				6		7
			query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
						+ "values (?, ?, ?, ?, ?, ?,  ?) ";
								// 1  2  3  4  5  6   7
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, senderTransactionCode); 			
				pstmt.setString(2, senderWalletId); 				
				pstmt.setString(3, systemreference+"C");							
				pstmt.setFloat(4, Float.parseFloat(payAmount));  
				pstmt.setString(5, currencyId);
				pstmt.setString(6, senderTxnMode); // Debit as it is a payment by the sender
				pstmt.setString(7, transactionDatetime);
				try {
					PPWalletEnvironment.setComment(3,classname,"Executed Sender wallet_tnx_bc amount"+"senderTransactioncode is"+ senderTransactionCode +"senderWalletId is"+senderWalletId 
							+"systemreference is"+systemreference +"payAmount is"+payAmount +"currencyId is"+ currencyId+ "transactionDatetime"+ transactionDatetime);

					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query);
					}					
					pstmt.close();	
				if(pstmt!=null)			
					pstmt=null;
		
				// *********5.2*****Receiver Wallet 
				systemreference =  PPWalletEnvironment.getCodeWalletToWalletP2P() +"-"+ receiverTransactionCode+"-" ;
				PPWalletEnvironment.setComment(3,classname,"systemreference for Receiver wallet_tnx_bc is"  + systemreference );
                                                  //   1		 2			 3			 4			 5				6		 7
				query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
						+ "values (?, ?, ?, ?, ?, ?,  ?) ";
								// 1  2  3  4  5  6   7
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, receiverTransactionCode); 			
				pstmt.setString(2, receiverWalletId); 				
				pstmt.setString(3, systemreference+"C");							
				pstmt.setFloat(4, Float.parseFloat(payAmount));  
				pstmt.setString(5, currencyId);
				pstmt.setString(6, receiverTxnMode); // Debit as it is a payment by the sender
				pstmt.setString(7, transactionDatetime);
				try {
					PPWalletEnvironment.setComment(3,classname,"Executed receiver wallet_tnx_bc amount"+ payAmount   );

					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query);
					}					
					pstmt.close();	
				if(pstmt!=null)			
					pstmt=null;
				
	// ****** Step 6: Calculate the Loyalty and put the accrued value into the Loyalty Table
				String loyaltyPaymentMode = PPWalletEnvironment.getCodeWalletToWalletP2P();// Wallet to Wallet P2P
				String pointsConversion = null; // We are putting 1 point = 1 float currency. The conversion in the table is used for converting points to fiat currency, not the other way round
				String previousPoinsBalance = null;
				boolean firstime = false;
				//here we have assume the pointsConversion to be 1 later will be changed based on the Loyalty Rules
				 query = "select pointsconversion from loyalty_rules where paymode=? and status = ? ";
					
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, loyaltyPaymentMode);
					pstmt.setString(2, "A");
					rs = (ResultSet)pstmt.executeQuery();
					 if(rs!=null){
						 	while(rs.next()){	 			 			
						 		pointsConversion = (StringUtils.trim(rs.getString("pointsconversion"))  );
						 		} // end of while
						 	} //end of if rs!=null check
					 pstmt.close();
					 if(pointsConversion!=null && senderRefNo.equals(receiverRefNo)==false ) {
						 
						 /// check the sender relationship and receiver relationship no. If both are same then do not proceed
						 	// *******Step 6.2 Get the Loyalty pointbalance for the sender user
							query = "select pointbalance balance from loyalty_points_bc where walletid=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where walletid = ? ) ";
									pstmt = connection.prepareStatement(query);
									pstmt.setString(1, senderWalletId);
									pstmt.setString(2, senderWalletId);
									rs = (ResultSet)pstmt.executeQuery();
									 if(rs!=null){
										while(rs.next()){	 			 			
											previousPoinsBalance = (StringUtils.trim(rs.getString("balance"))  );
											} // end of while
									} //end of if rs!=null check
										 pstmt.close();
								 if(previousPoinsBalance==null) {
									 firstime  =true;
										PPWalletEnvironment.setComment(3,classname,"No previous record present for walletid: "+senderWalletId);
									 previousPoinsBalance = Float.toString(Float.parseFloat(payAmount) * Float.parseFloat( pointsConversion ));
								  }else {
									 PPWalletEnvironment.setComment(3,classname,"Previus Balance for walletid : "+senderWalletId+" and userid"+senderRelationship+  " is "  +previousPoinsBalance);
								  }
								 
					//***********Step 6.3: Insert loyalty points for the sender user  senderRelationship
						  //                                      1         	2           3         4            5           6               7			8
						 query = "insert into loyalty_points_bc (walletid, relationshipno,  usertype, paymode, txnreference, pointaccrued, pointbalance, txndatetime) "
										+ "values (?, ?, ?, ?, ?, ?, ?, ?) ";
										//		   1  2  3  4  5  6  7  8
								pstmt = connection.prepareStatement(query);
								pstmt.setString(1, (senderWalletId)); 
								pstmt.setString(2, ( senderRelationship)); 
								pstmt.setString(3, (userType)); 
								pstmt.setString(4, loyaltyPaymentMode); 						// 
								pstmt.setString(5, senderTransactionCode);	
								pstmt.setFloat(6, Float.parseFloat(payAmount) );  // 
								
								if(firstime) {
									pstmt.setFloat(7, (Float.parseFloat(payAmount) ));
								}else {
									pstmt.setFloat(7, (Float.parseFloat(payAmount)  ) + Float.parseFloat(previousPoinsBalance ) );
								}
			
								pstmt.setString(8, transactionDatetime);
								try {
									pstmt.executeUpdate();
									}catch(Exception e) {
										throw new Exception (" failed query "+query+" "+e.getMessage());
									}						 
				 
			 }

		//****** lastly commit all transactions
			connection.commit();	result = true;
			//**insufficient balance in sender wallet
			}else {
				PPWalletEnvironment.setComment(3,classname,"Insufficient balance in your account.  Your balance is" + senderpreviouswalletBalance );
				result = false;
                    
			}
		
			}catch(Exception e){
				result = false;
				connection.rollback();
				PPWalletEnvironment.setComment(1,classname,"The exception in method updateWalletLedgers  is  "+e.getMessage());
				throw new Exception ("The exception in method updateWalletLedgers  is  "+e.getMessage());
			}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					PPWalletEnvironment.setComment(1,classname,"SQL Exception is  "+e.getMessage());
				}
				if(pstmt!=null) pstmt.close();
				
				if(result) {
					// call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet
				// inserting block data for sender
					try {
					insertGenericWalletTransactions(senderTransactionCode, senderWalletId, systemreference, payAmount, currencyId,senderTxnMode, transactionDatetime );
						}catch (Exception e) {
					}
					// inserting block data for receriver
					try {
					insertGenericWalletTransactions(receiverTransactionCode, receiverWalletId, systemreference, payAmount, currencyId, receiverTxnMode,transactionDatetime );	
						}catch (Exception e) {
					}
				}
			}
			return result;
	}	

	
	
	
	public ArrayList<Transaction> getAllTransactionsForWallet(String walletId) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Transaction> arrTransaction = null;
		try{
			connection = super.getConnection();	

				query = "select  txncode, walletid, sysreference, txnamount, txncurrencyid,  txnmode,txndatetime "
						+ " from wallet_txn_bc where walletid=? order by txndatetime desc  ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, walletId);
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 arrTransaction = new ArrayList<Transaction>();
				 
				 	while(rs.next()){
				 		Transaction m_Transaction=new Transaction();
				 		
				 		m_Transaction.setTxnCode( StringUtils.trim(rs.getString("txncode"))  );
				 		m_Transaction.setWalletId(StringUtils.trim(rs.getString("walletid"))  );
				 		m_Transaction.setSystemReference(StringUtils.trim(rs.getString("sysreference"))  );
				 		m_Transaction.setTxnAmount(StringUtils.trim(rs.getString("txnamount"))  );
				 		m_Transaction.setTxnCurrencyId(StringUtils.trim(rs.getString("txncurrencyid"))  );
				 		m_Transaction.setTxnMode(StringUtils.trim(rs.getString("txnmode"))  );
				 		m_Transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndatetime"))  );
				 		
				 		arrTransaction.add(m_Transaction);
				 		} 			 	
				 	} 
			 
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllTransactionsForWallet  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllTransactionsForWallet  is  "+e.getMessage());			
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
		
		return arrTransaction;
	}
	
	
	

	//**********************BlockChain******************************************************************************************************
	public static synchronized boolean insertGenericWalletTransactions(String txnCode, String walletId,
			String systemReference, String txnAmount, String currencyId,String txnmode, String transactionDatetime)  throws Exception  {
		    boolean result = false;	
			CloseableHttpClient client  = null;
			CloseableHttpResponse jresponse = null;
			try {
				PPWalletEnvironment.setComment(3,classname,"in insertGenericWalletTransactions Blockchain   ");

				String chainName = PPWalletEnvironment.getBlockChainName();
				String streamName = "walletledger"; 
					
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				  credsProvider.setCredentials(new AuthScope(PPWalletEnvironment.getMultiChainRPCIP(), Integer.parseInt(PPWalletEnvironment.getMultiChainRPCPort())),
				  new UsernamePasswordCredentials(PPWalletEnvironment.getMultiChainUser(), PPWalletEnvironment.getRPCAuthKey()));
				// Connect to Blockchain and create the Token blocks 
					PPWalletEnvironment.setComment(3,classname,"storing encrypted t. etc  ");

					 client = HttpClients.custom().setDefaultCredentialsProvider( credsProvider ).build();
					 
					PPWalletEnvironment.setComment(3,classname," *** after Credentials ");
					 //txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime
						PPWalletEnvironment.setComment(3,classname,"Now inserting data to Blockchain  ");

									String senderwalletjsonString = "{\"txndetails\": "
											+ "{ \"txncode\": \""+ txnCode   +"\", "
											+  "\"walletid\":\""+ walletId +"\", "
											+  "\"sysreference\":\""+ systemReference +"\", "
											+  "\"txnamount\": \""+ txnAmount   +"\", "
									    	+  "\"txncurrencyid\": \""+ currencyId   +"\", "
											+  "\"txnmode\" : \""+ txnmode +"\", "
											+  "\"txndatetime\":\""+  transactionDatetime +"\" "
											+ "}}";
		
									String jsonHexValue = Utilities.asciiToHex(senderwalletjsonString);
									 HttpPost jrequest = new HttpPost( PPWalletEnvironment.getMultiChainRPCURLPORT() );
									 jrequest.setEntity( new StringEntity(  "{\"method\":\"publish\",\"params\":[\""+streamName+"\",\""+ txnCode +"\",\""+jsonHexValue+"\"],\"id\":1,\"chain_name\":\""+chainName+"\"}"  ) );
									
										jresponse = client.execute( jrequest );
										HttpEntity entity = jresponse.getEntity();
								 		JsonObject responseJson =  JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject();
								 		PPWalletEnvironment.setComment(3,classname,"Response after Blockchain addition is "+responseJson.toString());	
					        result = true;	
			}catch(Exception e){
				result = false;
				PPWalletEnvironment.setComment(1,classname,"The exception in method insertGenericWalletTransactions  is  "+e.getMessage());
				throw new Exception ("The exception in method insertGenericWalletTransactions  is  "+e.getMessage());
			}finally{
				try {
					if(client!=null)				client.close();
					if(jresponse!=null)				 jresponse.close();
				}catch (Exception ee) {
					PPWalletEnvironment.setComment(1,classname,"The exception in method insertGenericWalletTransactions, finally block is  "+ee.getMessage());
				}
			}
			return result;
			
		}

	public ArrayList<Wallet> getRegisteredActiveWallets() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Wallet> arrWallet = null;
		try {
			connection = super.getConnection();	
            //  1         2           3          4        5        6           7              8
			query = "select a.relationshipno relationshipno, a.customername customername, a.custemail custemail, a.custcontact custcontact,  "
					+ " b.walletid walletid,     b.relationshipno relationshipno   from customer_details a, wallet_details b where a.relationshipno=b.relationshipno and a.status=? and b.status=?  ";
  			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "A");
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arrWallet = new ArrayList<Wallet>();
				 	while(rs.next()){	 
				 		Wallet wallet=new Wallet();
				 		wallet.setWalletId( StringUtils.trim(rs.getString("walletid"))    );
				 		wallet.setUserType( StringUtils.trim(rs.getString("usertype"))  );
				 		wallet.setRelationshipNo( StringUtils.trim(rs.getString("relationshipno"))  );
				 		wallet.setWalletDesc(StringUtils.trim(rs.getString("walletdesc"))  );
				 		wallet.setCurrentBalance(StringUtils.trim(rs.getString("currbal"))  );
				 		wallet.setCurrencyId(StringUtils.trim(rs.getString("currencyid"))  );
				 		wallet.setStatus(StringUtils.trim(rs.getString("status"))  );
				 		wallet.setLastUpdated(StringUtils.trim(rs.getString("lastupdated"))  );
				 		arrWallet.add(wallet);
				 		} 		 	
				 	} 
			 if(arrWallet!=null)
				 if(arrWallet.size()==0)
					 arrWallet=null;
			
			
		}catch(Exception e) {
			PPWalletEnvironment.setComment(1,classname,"The exception in method getRegisteredActiveWallets  is  "+e.getMessage());
			throw new Exception ("The exception in method getRegisteredActiveWallets  is  "+e.getMessage());			
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
	
		return arrWallet;
	}
	
}

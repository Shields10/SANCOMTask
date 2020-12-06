package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import com.ppwallet.model.BankDetails;
import com.ppwallet.model.CardDetails;
import com.ppwallet.model.MpesaDetails;
import com.ppwallet.model.Wallet;
import com.ppwallet.utilities.Utilities;

public class PaymentDao extends HandleConnections {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String className = PaymentDao.class.getSimpleName();

	/*
	 * public ArrayList<CardDetails> getCardDetails(String userId) throws Exception{
	 * PreparedStatement pstmt=null; Connection connection = null; ResultSet
	 * rs=null; String query = null; //Wallet wallet = null; ArrayList<CardDetails>
	 * arrCardDetails = null; try{ connection = super.getConnection();
	 * 
	 * query =
	 * "select tokenid, userid, cardnumber, cardalias from cardtoken_bc where userid=?  "
	 * ;
	 * 
	 * pstmt = connection.prepareStatement(query); pstmt.setString(1, userId);
	 * //pstmt.setString(2, "A"); rs = (ResultSet)pstmt.executeQuery();
	 * 
	 * if(rs!=null){ arrCardDetails = new ArrayList<CardDetails>();
	 * 
	 * while(rs.next()){ CardDetails m_CardDetails=new CardDetails();
	 * m_CardDetails.setTokenNumber(( StringUtils.trim(rs.getString("tokenid")) ));
	 * m_CardDetails.setUserId( StringUtils.trim(rs.getString("userid")) );
	 * m_CardDetails.setCardNumber(
	 * Utilities.decryptString((StringUtils.trim(rs.getString("cardnumber"))) ));
	 * m_CardDetails.setCardAlias((StringUtils.trim(rs.getString("cardalias")) ));
	 * arrCardDetails.add(m_CardDetails); } // end of while
	 * 
	 * } //end of if rs!=null check // validate the password
	 * if(arrCardDetails!=null) if(arrCardDetails.size()==0) arrCardDetails=null;
	 * 
	 * }catch(Exception e){ StencilEnvironment.setComment(1,
	 * classname,"The exception in method getCardDetails  is  "+e.getMessage());
	 * throw new Exception
	 * ("The exception in method getCardDetails  is  "+e.getMessage()); }finally{
	 * if(connection!=null) try { super.close(); } catch (SQLException e) {
	 * e.printStackTrace(); } if(rs!=null) rs.close(); if(pstmt!=null)
	 * pstmt.close(); } return arrCardDetails; }
	 */

	public String initiatePaymentViaBank(String walletId, String bankId, String bankCode, String bankName,
			String bankAccountNo, String bankAccountName, String topUpAmount, String relationshipNo)
			throws Exception {
		PreparedStatement pstmt = null;
		Connection connection = null;
		String query = null;
		String transactionCode = null;
		ResultSet rs = null;
		String transactionDatetime = null;
		String txnMode = "C";
		String userType = "C";
		String txnCurrencyId = "404";
		boolean result = false;
		String systemReference = null;
		float balanceafterTopup = 0;
		try {
			PPWalletEnvironment.setComment(3, className, "in initiate Payment by Bank");

			connection = super.getConnection();
			connection.setAutoCommit(false);
			transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();

			// *** It is assumed that we have already got the successful response. For
			// unsuccessful response, we are currently not storing any transaction as of
			// now.
			// *** for production we have to store both the successful and unsuccessful
			// transactions with in BC or in Database separately for audit/PCI compliance
			// purpose

			// *** Generate a dummy transaction code, ideally it should come from the ARQC
			// of the 0110 message from the Switch/Gateway response

			// *** Step 1: Get the Transaction Code and the SYstem reference from the Bank -
			// THIS IS THE POINT OF INTEGRATION WITH THE BANK

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyMMddHHmmssSSS");
			transactionCode = formatter1.format(new java.util.Date()) + Utilities.genAlphaNumRandom(9);

			// **********get the wallet balance
			String previouscurrentBalance = null;
			query = "select currbal  from wallet_details where walletid=?  ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, walletId);
			rs = (ResultSet) pstmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					previouscurrentBalance = (StringUtils.trim(rs.getString("currbal")));
					PPWalletEnvironment.setComment(3, className, "The pointbalance   is " + previouscurrentBalance);
					PPWalletEnvironment.setComment(3, className, "The topUpAmount   is " + topUpAmount);
				} // end of while
			} // end of if rs!=null check
			pstmt.close();

			// TODO check the systemreference again for validity with the Operations user
			// 1 2 3 4 5 6 7
			systemReference = PPWalletEnvironment.getCodeWalletTopUpFromBank() + "-" + transactionCode + "-";
			PPWalletEnvironment.setComment(3, className, "The systemreference  is " + systemReference);
		//                                       1         2             3          4          5             6         7
			query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime)  "
					+ " values (?, ?, ?, ?, ?, ?, ? )  ";
			// 1 2 3 4 5 6 7
			// systemreference = (bank_id + bankcode + topUpAmount)+(
			// RandomStringUtils.random(10, false, true)).toString();
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionCode);
			pstmt.setString(2, walletId);
			pstmt.setString(3, systemReference + "C");
			pstmt.setFloat(4, Float.parseFloat(topUpAmount));
			pstmt.setString(5, txnCurrencyId);
			pstmt.setString(6, txnMode);
			pstmt.setString(7, transactionDatetime);
			try {
				PPWalletEnvironment.setComment(3, className, "transactionCode is " + transactionCode+ "walletId is "+  walletId +
						"systemReference is "+ systemReference+"C" + "topUpAmount IS "+topUpAmount+ " txnMode"+txnMode);

				pstmt.executeUpdate();
			} catch (Exception e) {
				throw new Exception(" failed query " + query + " " + e.getMessage());
			}
			pstmt.close();
			PPWalletEnvironment.setComment(3, className, "inserted into wallet_txn_bc topUpAmount " + topUpAmount);


			// *** Step 2: Update the Wallet Ledger, ***** (IMP: Consider updating the
			// Blockchain wallet ledger also)

			// *******update wallet with new ballance
			 balanceafterTopup = Float.parseFloat(previouscurrentBalance) + Float.parseFloat(topUpAmount);

			PPWalletEnvironment.setComment(3, className,
					"The new balance  for walletid is" + walletId + " is" + balanceafterTopup);

			query = "update wallet_details set currbal =  ? , lastupdated = UTC_TIMESTAMP  where walletid=?  ";
			pstmt = connection.prepareStatement(query);
			pstmt.setFloat(1, balanceafterTopup); //
			pstmt.setString(2, walletId);
			try {
				pstmt.executeUpdate();
			} catch (Exception e) {
				throw new Exception(" failed query " + query + " " + e.getMessage());
			}
			pstmt.close();
			PPWalletEnvironment.setComment(3, className, "The currbal after topup  is " + balanceafterTopup);
			
			//Record retail transaction in topup ledger
				                          	//	1		    2 			   3	        	4			   5	    	6			7		8	      9              10	   	
			query = "insert into topup_txn_bc (txncode, cust_walletid, referencecode, relationshipno, sysreference, txnamount, topupmode, location, txncurrencyid, txndatetime) "
			+ "values (?, ?, ?, ?, ?, ?, ?, ? , ? ,? ) ";
			//		   1  2  3  4  5  6  7  8   9  10 
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionCode); 						 
			pstmt.setString(2, walletId); 						 
			pstmt.setString(3, "Bankcode");	
			pstmt.setString(4, relationshipNo); 						 
			pstmt.setString(5, systemReference+userType);  
			pstmt.setFloat(6, Float.parseFloat(topUpAmount));
			pstmt.setString(7, "B" );
			pstmt.setString(8, "");  
			pstmt.setString(9, txnCurrencyId );
			pstmt.setString(10, transactionDatetime);
			
			try {
			pstmt.executeUpdate();
			}catch(Exception e) {
			throw new Exception (" failed query "+query+" "+e.getMessage());
			}
			
			pstmt.close();

			String pointsConversion = null;
			String previousPoinsBalance = null;
			boolean firstime = false;
			String pointsaccrued = null;

			// *** Step 3: Calculate the Loyalty and put the accrued value into the Loyalty
			// Table
			query = "select pointsconversion from loyalty_rules where paymode=? and status = ? ";

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, PPWalletEnvironment.getCodeWalletTopUpFromBank());
			pstmt.setString(2, "A");
			rs = (ResultSet) pstmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					pointsConversion = (StringUtils.trim(rs.getString("pointsconversion")));
				} // end of while
			} // end of if rs!=null check
			pstmt.close();

			if(pointsConversion!=null) {
				
				//***Get loyalty balance for wallet
				 query = "select pointbalance balance from loyalty_points_bc where relationshipno=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where relationshipno = ? ) ";
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, relationshipNo);
					pstmt.setString(2, relationshipNo);
					rs = (ResultSet)pstmt.executeQuery();		
					 if(rs!=null){
						 	while(rs.next()){	 			 			
						 		previousPoinsBalance = (StringUtils.trim(rs.getString("balance"))  );
							PPWalletEnvironment.setComment(3,className,"The pointbalance   is " + previousPoinsBalance );
						 		
						 		} // end of while
						 	} //end of if rs!=null check
					 pstmt.close();
				 if(previousPoinsBalance==null) {
					 firstime  = true;
					 PPWalletEnvironment.setComment(3,className,"No previous record present for walletid: "+walletId+" for userType    "+userType);
				 }else {
					 PPWalletEnvironment.setComment(3,className,"Previus Balance for walletid : "+walletId+" is "+previousPoinsBalance+" for userType    "+userType);
				 }
				 
				// Step 4.3: Insert loyalty points for the sender user  
				 								//		1		2			3			   4			5			6				7          8
			 query = "insert into loyalty_points_bc (walletid, usertype, relationshipno, paymode, txnreference, pointaccrued, pointbalance, txndatetime) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5  6  7  8
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, (walletId)); 					
					pstmt.setString(2, userType); 		
					pstmt.setString(3, relationshipNo); // 
					pstmt.setString(4, PPWalletEnvironment.getCodeWalletTopUpFromBank() );	
					pstmt.setString(5, systemReference+userType ); // systemreference reference generated by the system
					pstmt.setFloat(6, Float.parseFloat(topUpAmount) );  // pointaccrued
					if(firstime) {
						pstmt.setFloat(7, Float.parseFloat(topUpAmount)    );  // pointbalance
					}else {
						pstmt.setFloat(7, Float.parseFloat(topUpAmount)  + Float.parseFloat(previousPoinsBalance )    );  // pointbalance
					}
					pstmt.setString(8,transactionDatetime );	

						try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
					}	
						
						
				}
			connection.commit();
			result = true;

		} catch (Exception e) {
			connection.rollback();
			transactionCode = null;
			PPWalletEnvironment.setComment(1, className,
					"The exception in method initiatePaymentViaBank  is  " + e.getMessage());
			throw new Exception("The exception in method initiatePaymentViaBank  is  " + e.getMessage());
		} finally {
			if (connection != null)
				try {
					super.close();
				} catch (SQLException e) {
					PPWalletEnvironment.setComment(1, className, "SQL Exception is  " + e.getMessage());
				}			
			if (result) {
				/// call the Blockchain method here and pass the values within the method. Here
				/// we are inserting in the walletledger stream of Blockchain having chainame
				/// ppwallet
				// WalletDao.class.getConstructor().newInstance();
				WalletDao.insertGenericWalletTransactions(transactionCode, walletId, systemReference, topUpAmount,txnCurrencyId, txnMode, transactionDatetime);
			}
			if (pstmt != null)    pstmt.close();if(txnMode!=null)  txnMode=null;
			if(transactionDatetime!=null) transactionDatetime=null; if(userType!=null) userType=null; if(txnCurrencyId!=null) txnCurrencyId=null;
			if(systemReference!=null) systemReference=null;
			
		}
		return transactionCode;
	}
	public boolean addBankDetails(String bankCode, String branchCode, String relationshipNo, String bankName,
		String bankAccountNo, String bankAccountName) throws Exception {
		PreparedStatement pstmt = null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try {
			connection = super.getConnection();
			connection.setAutoCommit(false);
			
			
			// 1 2 3 4 5 6 7
			query = "insert into customer_bankdetails (bankcode, branchcode, relationshipno, bankname, bankaccountno, bankaccountname, createdon)  "
					+ " values (?, ?, ?, ?, ?, ?, ? )  ";
			// 1 2 3 4 5 6 7
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, bankCode);
			pstmt.setString(2, branchCode);
			pstmt.setString(3, relationshipNo);
			pstmt.setString(4, bankName);
			pstmt.setString(5, bankAccountNo);
			pstmt.setString(6, bankAccountName);
			pstmt.setString(7, Utilities.getMYSQLCurrentTimeStampForInsert());
			PPWalletEnvironment.setComment(3, className, "inserted bank data to the bankdetails table  ");

			try {
				pstmt.executeUpdate();
			} catch (Exception e) {
				throw new Exception(" failed query " + query + " " + e.getMessage());
			}

			connection.commit();
			result = true;

		} catch (Exception e) {
			throw new Exception("The exception in method addBankDetails  is  " + e.getMessage());
		} finally {
			if (connection != null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (pstmt != null) pstmt.close();
		}
		return result;
	}
	
	public ArrayList<BankDetails> getBankDetails(String relationshipno) throws Exception {
		PreparedStatement pstmt = null;
		Connection connection = null;
		ResultSet rs = null;
		String query = null;
		ArrayList<BankDetails> arrBankDetails = null;
		try {
			PPWalletEnvironment.setComment(3,className,"in getBankDetails" );

			connection = super.getConnection();

			               // 1           2            3             4          5                6
			query = "select bankcode, branchcode, relationshipno, bankname,  bankaccountno, bankaccountname  "
					+ " from customer_bankdetails   " + "  where relationshipno=?   ";

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipno);
			rs = (ResultSet) pstmt.executeQuery();

			if (rs != null) {
				arrBankDetails = new ArrayList<BankDetails>();
				while (rs.next()) {
					BankDetails bankdetails = new BankDetails();
					bankdetails.setBankCode(StringUtils.trim(rs.getString("bankcode")));
					bankdetails.setBranchCode(StringUtils.trim(rs.getString("branchcode")));
					bankdetails.setRelationshipNo(StringUtils.trim(rs.getString("relationshipno")));
					bankdetails.setBankName(StringUtils.trim(rs.getString("bankname")));
					bankdetails.setBankAccountNo(StringUtils.trim(rs.getString("bankaccountno")));
					bankdetails.setBankAccountName(StringUtils.trim(rs.getString("bankaccountname")));
					arrBankDetails.add(bankdetails);
				} // end of while
				// PPWalletEnvironment.setComment(3,className,"arrBankDetails is "+
				// arrBankDetails.size());

			} // end of if rs!=null check
			// validate the password
			if (arrBankDetails != null)
				if (arrBankDetails.size() == 0)
					arrBankDetails = null;

		} catch (Exception e) {
			PPWalletEnvironment.setComment(1, className,
					"The exception in method getWalletDetails  is  " + e.getMessage());
			throw new Exception("The exception in method getBankDetails  is  " + e.getMessage());
		} finally {
			if (connection != null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
		}
		return arrBankDetails;
	}
	
	public ArrayList<MpesaDetails> getMpesaDetails(String relationshipNo) throws Exception{
	PreparedStatement pstmt = null;
	Connection connection = null;
	ResultSet rs = null;
	String query = null;
	ArrayList<MpesaDetails> arrMpesaDetails = null;
	try {
		connection = super.getConnection();

		                   // 1              2            3          4        5         6
		query = "select fullname, mpesanumber, idnumber,  status, createdon  "
		+ " from customer_mpesa_details  where relationshipno=?   ";
		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, relationshipNo);
		rs = (ResultSet) pstmt.executeQuery();
		
		if (rs != null) {
			arrMpesaDetails = new ArrayList<MpesaDetails>();
			while (rs.next()) {
				MpesaDetails m_mpesaDetails = new MpesaDetails();
				m_mpesaDetails.setOtherName( StringUtils.trim(rs.getString("fullname")));
				m_mpesaDetails.setMpesaNumber( StringUtils.trim(rs.getString("mpesanumber")));
				m_mpesaDetails.setIdNumber( StringUtils.trim(rs.getString("idnumber")));
				m_mpesaDetails.setStatus( StringUtils.trim(rs.getString("status")));
				arrMpesaDetails.add(m_mpesaDetails);
			} // end of while
			PPWalletEnvironment.setComment(3,className,"arrMpesaDetails is"+  arrMpesaDetails.size() );

		} // end of if rs!=null check
		// validate the password
		if (arrMpesaDetails != null)
			if (arrMpesaDetails.size() == 0)
				arrMpesaDetails = null;
	
	}catch(Exception e) {
		
	}finally {
		if (connection != null)
			try {
				super.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (rs != null)			rs.close();
		if (pstmt != null)			pstmt.close();
		
	}		
		return arrMpesaDetails;
	}

	public boolean initiatePaymentViaMpesa(String walletId, String mpesaNumber, String mpesaTopupAmount,
		String relationshipNo, String mpesaTxnCode) throws Exception {
		boolean result = false;
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		ResultSet rs = null;
		String transactionDatetime = null;
		String txnMode = "C";
		String userType = "C";
		String txnCurrencyId = "404";
		String systemReference = null;
		float newWalletBalance = 0;
		String transactionCode = null;
		
		try {
			connection = super.getConnection();
			 connection.setAutoCommit(false);
			 transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
			 SimpleDateFormat formatter1 = new SimpleDateFormat("yyMMddHHmmssSSS");
			 transactionCode = formatter1.format(new java.util.Date()) + Utilities.genAlphaNumRandom(9);
			
		    //**********get the wallet ballance
	 			String previousWalletBalance = null;
			 query = "select currbal  from wallet_details where walletid=?  ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, walletId);
					rs = (ResultSet)pstmt.executeQuery();	
					 if(rs!=null){
					 	while(rs.next()){	 			 			
					 	previousWalletBalance = (StringUtils.trim(rs.getString("currbal"))  );
					PPWalletEnvironment.setComment(3,className,"The pointbalance   is " + previousWalletBalance );
							} // end of while
						} //end of if rs!=null check
				pstmt.close();
				PPWalletEnvironment.setComment(3,className,"The Previous currbal is " +previousWalletBalance );

			//****calculate the new balance
				newWalletBalance  = Float.parseFloat(previousWalletBalance) + Float.parseFloat(mpesaTopupAmount);
				
				
		 //***update wallet ballance
				query = "update wallet_details set currbal =  ? , lastupdated = ?  where walletid=?  ";
				pstmt = connection.prepareStatement(query);
				pstmt.setFloat(1, newWalletBalance);  
				pstmt.setString(2, transactionDatetime);  	
				pstmt.setString(3, walletId);
				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}
				if(pstmt!=null)  pstmt.close();		if(rs!=null)  rs.close();
				PPWalletEnvironment.setComment(3,className,"The currbal after Topup is " +newWalletBalance );
				
				//****update wallet Transaction GL: IMP: Cobnsider updating the Blockchain also
				//    //systemreference = (bank_id + bankcode + topUpAmount)+( RandomStringUtils.random(10, false, true)).toString();
				systemReference =  PPWalletEnvironment.getCodeWalletTopUpFromMPESA() +"-"+ transactionCode+"-" ;
					PPWalletEnvironment.setComment(3,className,"systemreference is " +systemReference+userType  );
     
				 //     1         2          3              4        5             6           7
			 query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime)  "
							+ " values (?, ?, ?, ?, ?, ?, ? )  ";
							//		    1  2  3  4  5  6  7  

					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, transactionCode); 		
					pstmt.setString(2, walletId); 					
					pstmt.setString(3,systemReference+userType );	
					pstmt.setFloat(4, Float.parseFloat(mpesaTopupAmount));
					pstmt.setString(5, txnCurrencyId);
					pstmt.setString(6, txnMode);                      
					pstmt.setString(7,  transactionDatetime);           
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}					
					pstmt.close();
					PPWalletEnvironment.setComment(3,className,"Updating the wallet ledger  weallet Id  " +walletId + "and mpesaTopupAmount " +  mpesaTopupAmount );
					
					
					//Record retail transaction in topup ledger
														//	1		    2 			   3	       	4			   5	    	6			7		8	      9              10	   	
					query = "insert into topup_txn_bc (txncode, cust_walletid, referencecode, relationshipno, sysreference, txnamount, topupmode, location, txncurrencyid, txndatetime) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ? , ? ,? ) ";
					//		   1  2  3  4  5  6  7  8   9  10 
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, transactionCode); 						 
					pstmt.setString(2, walletId); 						 
					pstmt.setString(3, mpesaTxnCode);	
					pstmt.setString(4, relationshipNo); 						 
					pstmt.setString(5, systemReference+userType);  
					pstmt.setFloat(6, Float.parseFloat(mpesaTopupAmount));
					pstmt.setString(7, "P" );
					pstmt.setString(8, "");  
					pstmt.setString(9, txnCurrencyId );
					pstmt.setString(10, transactionDatetime);
					
					try {
					pstmt.executeUpdate();
					}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
					}
					
					pstmt.close();

			//*****	Calculate the Loyalty and put the accrued value into the Loyalty Table

					String pointsConversion = null;
					String previousPoinsBalance = null;
					boolean firstime = false;
					//String pointsaccrued =null;
					 
				 query = "select pointsconversion from loyalty_rules where paymode=? and status = ? ";
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, PPWalletEnvironment.getCodeWalletTopUpFromMPESA() );
					pstmt.setString(2, "A");
					rs = (ResultSet)pstmt.executeQuery();
					 if(rs!=null){
						 	while(rs.next()){	 			 			
						 		pointsConversion = (StringUtils.trim(rs.getString("pointsconversion"))  );
						 		} // end of while
						 	} //end of if rs!=null check
					 pstmt.close();
				
					if(pointsConversion!=null) {
						
					//***Get loyalty balance for wallet
					 query = "select pointbalance balance from loyalty_points_bc where relationshipno=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where relationshipno = ? ) ";
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, relationshipNo);
						pstmt.setString(2, relationshipNo);
						rs = (ResultSet)pstmt.executeQuery();		
						 if(rs!=null){
							 	while(rs.next()){	 			 			
							 		previousPoinsBalance = (StringUtils.trim(rs.getString("balance"))  );
								PPWalletEnvironment.setComment(3,className,"The pointbalance   is " + previousPoinsBalance );
							 		
							 		} // end of while
							 	} //end of if rs!=null check
						 pstmt.close();
					 if(previousPoinsBalance==null) {
						 firstime  = true;
						 PPWalletEnvironment.setComment(3,className,"No previous record present for walletid: "+walletId+" for userType    "+userType);
					 }else {
						 PPWalletEnvironment.setComment(3,className,"Previus Balance for walletid : "+walletId+" is "+previousPoinsBalance+" for userType    "+userType);
					 }
					 
					// Step 4.3: Insert loyalty points for the sender user  
					 								//		1		2			3			   4			5			6				7          8
				 query = "insert into loyalty_points_bc (walletid, usertype, relationshipno, paymode, txnreference, pointaccrued, pointbalance, txndatetime) "
								+ "values (?, ?, ?, ?, ?, ?, ?, ?) ";
								//		   1  2  3  4  5  6  7  8
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, (walletId)); 					
						pstmt.setString(2, userType); 		
						pstmt.setString(3, relationshipNo); // 
						pstmt.setString(4, PPWalletEnvironment.getCodeWalletTopUpFromMPESA() );	
						pstmt.setString(5, systemReference+userType ); // systemreference reference generated by the system
						pstmt.setFloat(6, Float.parseFloat(mpesaTopupAmount) );  // pointaccrued
						if(firstime) {
							pstmt.setFloat(7, Float.parseFloat(mpesaTopupAmount)    );  // pointbalance
						}else {
							pstmt.setFloat(7, Float.parseFloat(mpesaTopupAmount)  + Float.parseFloat(previousPoinsBalance )    );  // pointbalance
						}
						pstmt.setString(8,transactionDatetime );	

							try {
							pstmt.executeUpdate();
							}catch(Exception e) {
								throw new Exception (" failed query "+query+" "+e.getMessage());
						}	
							
							
					}
					connection.commit();
					result = true;
			
		}catch(Exception e) {
			
		}finally {	
			if (connection != null)
			try {
				super.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(result) {
				 /// call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet
				//WalletDao.class.getConstructor().newInstance();
				WalletDao.insertGenericWalletTransactions(transactionCode, walletId, systemReference+userType, mpesaTopupAmount, txnCurrencyId, txnMode, transactionDatetime  );	
		}
		if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; if(txnMode!=null)  txnMode=null;
			if(userType!=null)  userType=null;	if(txnCurrencyId!=null)  txnCurrencyId=null;if(systemReference!=null)  systemReference=null;
			if(newWalletBalance!=0)  newWalletBalance=0;
		
		}
	
		return result;
	}

	/* This Method returns all banks registered to a specific Customer*/
	public ArrayList<BankDetails> getCustomerRegisteredBanks(String myRelationshipNo) throws Exception {
		// TODO Auto-generated method stub
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<BankDetails> custRegisteredBanks = null;

		try {
			connection = super.getConnection();	
			
			query = "select bankaccountname,bankcode,branchcode,bankname,bankaccountno from customer_bankdetails where relationshipno = ?";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, myRelationshipNo);
			rs = (ResultSet) pstmt.executeQuery();
			
			if(rs!=null) {
				custRegisteredBanks = new ArrayList<BankDetails>();
				while(rs.next()) {	
					BankDetails bDetails= new BankDetails();

					
					bDetails.setBankAccountName(rs.getString(StringUtils.trim("bankaccountname")));
					bDetails.setBankCode(rs.getString(StringUtils.trim("bankcode")));
					bDetails.setBranchCode(rs.getString(StringUtils.trim("branchcode")));
					bDetails.setBankName(rs.getString(StringUtils.trim("bankname")));
					bDetails.setBankAccountNo(rs.getString(StringUtils.trim("bankaccountno")));
					
					custRegisteredBanks.add(bDetails);
					
				}//while end 
			}//rd end
			if (custRegisteredBanks != null)
				if (custRegisteredBanks.size() == 0)
					custRegisteredBanks = null;
		
		}catch (Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getCustomerRegisteredBanks in payments dao  is  "+e.getMessage());
			throw new Exception ("The exception in method getCustomerRegisteredBanks  is  "+e.getMessage());	
		} finally {
			if(connection!=null) 
				try {
					super.close();

				}catch(SQLException e) {
					e.printStackTrace();
				}
				if(rs!= null) rs.close();
				if(pstmt!=null) pstmt.close();
		}

		return custRegisteredBanks;
	}
	
	
	
	/* =======================Add Mpesa number and return registerted number dao logic START============================= */
	
	public boolean addMpesaDetails(String relationshipNo, String mpesaNumber, String surname,String otherName,
			String nationalIdNumber) throws Exception{
		PreparedStatement pstmt = null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try {
			connection = super.getConnection();
			connection.setAutoCommit(false);
			                                                 // 1              2          3             4        5       6
				query = "insert into customer_mpesa_details (relationshipno, fullname, mpesanumber, idnumber, status, createdon)  "
						+ " values (?, ?, ?, ?, ?, ? )  ";
				                 // 1  2  3  4  5  6
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, relationshipNo);
				pstmt.setString(2, surname+" "+ otherName);
				pstmt.setString(3, mpesaNumber);
				pstmt.setString(4, nationalIdNumber);
				pstmt.setString(5, "A");
				pstmt.setString(6, Utilities.getMYSQLCurrentTimeStampForInsert());
				try {
					pstmt.executeUpdate();
				} catch (Exception e) {
					throw new Exception(" failed query " + query + " " + e.getMessage());
				}
				connection.commit();
				result = true;
				PPWalletEnvironment.setComment(3, className, "inserted mpesa data to the mpesa details table  ");

	
		}catch(Exception e) {
			
		}finally {
			if (connection != null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (pstmt != null)
				pstmt.close();			
		}
		return result;
	}
	
	
	public ArrayList<MpesaDetails> getCustomerRegMpesaNos(String myRelationshipNo) throws Exception {
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<MpesaDetails> custRegisteredNumbers = null;

		try {
			connection = super.getConnection();	
			
			query = "select mpesanumber,createdon from customer_mpesa_details where relationshipno = ?";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, myRelationshipNo);
			rs = (ResultSet) pstmt.executeQuery();
			
			if(rs!=null) {
				custRegisteredNumbers = new ArrayList<MpesaDetails>();
				while(rs.next()) {	
					
					MpesaDetails nDetails= new MpesaDetails();
					nDetails.setMpesaNumber(rs.getString(StringUtils.trim("mpesanumber")));
					nDetails.setDateCreated(Utilities.formartDateMpesa(rs.getString(StringUtils.trim("createdon"))));
				
					custRegisteredNumbers.add(nDetails);
					
				}//while end 
			}//rs end
			if (custRegisteredNumbers != null)
				if (custRegisteredNumbers.size() == 0)
					custRegisteredNumbers = null;
		
		}catch (Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getCustomerRegMpesaNos in payments dao  is  "+e.getMessage());
			throw new Exception ("The exception in method getCustomerRegMpesaNos  is  "+e.getMessage());	
		} finally {
			if(connection!=null) 
				try {
					super.close();

				}catch(SQLException e) {
					e.printStackTrace();
				}
				if(rs!= null) rs.close();
				if(pstmt!=null) pstmt.close();
		
		}
		
		return custRegisteredNumbers;
	}
	
	
	
	public String initiatePaymentViaToken(String walletId, String tokenId,
		    String topUpAmount, String relationshipNo) throws Exception {
		boolean result = false;
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		ResultSet rs = null;
		String transactionDatetime = null;
		String txnMode = "C";
		String userType = "C";
		String txnCurrencyId = "404";
		String systemReference = null;
		float newWalletBalance = 0;
		String transactionCode = null;
		//String responseCode = null;
		
		try {
			connection = super.getConnection();
			 connection.setAutoCommit(false);
			 transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
			 SimpleDateFormat formatter1 = new SimpleDateFormat("yyMMddHHmmssSSS");
			 transactionCode = formatter1.format(new java.util.Date()) + Utilities.genAlphaNumRandom(9);
			//responseCode = formatter1.format(new java.util.Date()) + Utilities.genAlphaNumRandom(9);
			//WE SHALL SEND THE DETAILS TO THE CARD ISSUER  AND WAIT FOR THE RESPONSE 
			 
			 //if response is success
			 //
		 
		    //**********get the wallet ballance
	 			String previousWalletBalance = null;
			 query = "select currbal  from wallet_details where walletid=?  ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, walletId);
					rs = (ResultSet)pstmt.executeQuery();	
					 if(rs!=null){
					 	while(rs.next()){	 			 			
					 	previousWalletBalance = (StringUtils.trim(rs.getString("currbal"))  );
					PPWalletEnvironment.setComment(3,className,"The pointbalance   is " + previousWalletBalance );
							} // end of while
						} //end of if rs!=null check
				pstmt.close();
				PPWalletEnvironment.setComment(3,className,"The Previous currbal is " +previousWalletBalance );

			//****calculate the new balance
				newWalletBalance  = Float.parseFloat(previousWalletBalance) + Float.parseFloat(topUpAmount);
				
				
		 //***update wallet ballance
				query = "update wallet_details set currbal =  ? , lastupdated = ?  where walletid=?  ";
				pstmt = connection.prepareStatement(query);
				pstmt.setFloat(1, newWalletBalance);  
				pstmt.setString(2, transactionDatetime);  	
				pstmt.setString(3, walletId);
				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}
				if(pstmt!=null)  pstmt.close();		if(rs!=null)  rs.close();
				PPWalletEnvironment.setComment(3,className,"The currbal after Topup is " +newWalletBalance );
				
				//****update wallet Transaction GL: IMP: Cobnsider updating the Blockchain also
				//    //systemreference = (bank_id + bankcode + topUpAmount)+( RandomStringUtils.random(10, false, true)).toString();
				systemReference =  PPWalletEnvironment.getCodeTokenWalletTopup() +"-"+ transactionCode+"-"+userType ;
					PPWalletEnvironment.setComment(3,className,"systemreference is " +systemReference+userType  );
     
				 //     1         2          3              4        5             6           7
			 query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime)  "
							+ " values (?, ?, ?, ?, ?, ?, ? )  ";
							//		    1  2  3  4  5  6  7  

					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, transactionCode); 		
					pstmt.setString(2, walletId); 					
					pstmt.setString(3,systemReference);	
					pstmt.setFloat(4, Float.parseFloat(topUpAmount));
					pstmt.setString(5, txnCurrencyId);
					pstmt.setString(6, txnMode);                      
					pstmt.setString(7,  transactionDatetime);           
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}					
					pstmt.close();
					PPWalletEnvironment.setComment(3,className,"Updating the wallet ledger  weallet Id  " +walletId + "and mpesaTopupAmount " +  topUpAmount );
					
					
					//Record retail transaction in topup ledger
														//	1		    2 			   3	       	4			   5	    	6			7		8	      9              10	   	
					query = "insert into topup_txn_bc (txncode, cust_walletid, referencecode, relationshipno, sysreference, txnamount, topupmode, location, txncurrencyid, txndatetime) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ? , ? ,? ) ";
					//		   1  2  3  4  5  6  7  8   9  10 
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, transactionCode); 						 
					pstmt.setString(2, walletId); 						 
					pstmt.setString(3, transactionCode);	
					pstmt.setString(4, relationshipNo); 						 
					pstmt.setString(5, systemReference+userType);  
					pstmt.setFloat(6, Float.parseFloat(topUpAmount));
					pstmt.setString(7, "T" );
					pstmt.setString(8, "");  
					pstmt.setString(9, txnCurrencyId );
					pstmt.setString(10, transactionDatetime);
					
					try {
					pstmt.executeUpdate();
					}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
					}
					
					pstmt.close();

			//*****	Calculate the Loyalty and put the accrued value into the Loyalty Table

					String pointsConversion = null;
					String previousPoinsBalance = null;
					boolean firstime = false;
					//String pointsaccrued =null;
					 
				 query = "select pointsconversion from loyalty_rules where paymode=? and status = ? ";
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, PPWalletEnvironment.getCodeTokenWalletTopup() );
					pstmt.setString(2, "A");
					rs = (ResultSet)pstmt.executeQuery();
					 if(rs!=null){
						 	while(rs.next()){	 			 			
						 		pointsConversion = (StringUtils.trim(rs.getString("pointsconversion"))  );
						 		} // end of while
						 	} //end of if rs!=null check
					 pstmt.close();
				
					if(pointsConversion!=null) {
						
					//***Get loyalty balance for wallet
					 query = "select pointbalance balance from loyalty_points_bc where relationshipno=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where relationshipno = ? ) ";
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, relationshipNo);
						pstmt.setString(2, relationshipNo);
						rs = (ResultSet)pstmt.executeQuery();		
						 if(rs!=null){
							 	while(rs.next()){	 			 			
							 		previousPoinsBalance = (StringUtils.trim(rs.getString("balance"))  );
								PPWalletEnvironment.setComment(3,className,"The pointbalance   is " + previousPoinsBalance );
							 		
							 		} // end of while
							 	} //end of if rs!=null check
						 pstmt.close();
					 if(previousPoinsBalance==null) {
						 firstime  = true;
						 PPWalletEnvironment.setComment(3,className,"No previous record present for walletid: "+walletId+" for userType    "+userType);
					 }else {
						 PPWalletEnvironment.setComment(3,className,"Previus Balance for walletid : "+walletId+" is "+previousPoinsBalance+" for userType    "+userType);
					 }
					 
					// Step 4.3: Insert loyalty points for the sender user  
					 								//		1		2			3			   4			5			6				7          8
				 query = "insert into loyalty_points_bc (walletid, usertype, relationshipno, paymode, txnreference, pointaccrued, pointbalance, txndatetime) "
								+ "values (?, ?, ?, ?, ?, ?, ?, ?) ";
								//		   1  2  3  4  5  6  7  8
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, (walletId)); 					
						pstmt.setString(2, userType); 		
						pstmt.setString(3, relationshipNo); // 
						pstmt.setString(4, PPWalletEnvironment.getCodeTokenWalletTopup() );	
						pstmt.setString(5, systemReference+userType ); // systemreference reference generated by the system
						pstmt.setFloat(6, Float.parseFloat(topUpAmount) );  // pointaccrued
						if(firstime) {
							pstmt.setFloat(7, Float.parseFloat(topUpAmount)    );  // pointbalance
						}else {
							pstmt.setFloat(7, Float.parseFloat(topUpAmount)  + Float.parseFloat(previousPoinsBalance )    );  // pointbalance
						}
						pstmt.setString(8,transactionDatetime );	

							try {
							pstmt.executeUpdate();
							}catch(Exception e) {
								throw new Exception (" failed query "+query+" "+e.getMessage());
						}	
							
							
					}
					connection.commit();
					result = true;
			
		}catch(Exception e) {
			
		}finally {	
			if (connection != null)
			try {
				super.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if(result) {
				 /// call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet
				//WalletDao.class.getConstructor().newInstance();
				WalletDao.insertGenericWalletTransactions(transactionCode, walletId, systemReference+userType, topUpAmount, txnCurrencyId, txnMode, transactionDatetime  );	
		}
		if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; if(txnMode!=null)  txnMode=null;
			if(userType!=null)  userType=null;	if(txnCurrencyId!=null)  txnCurrencyId=null;if(systemReference!=null)  systemReference=null;
			if(newWalletBalance!=0)  newWalletBalance=0;
		
		}
	
		return transactionCode;
	}
	

	
	
	
	
	public boolean recordAuthorizedPaymentViaCard(String userType,String cardName, String cardNumber,String dateOfExpiry,String relationshipNo,String referenceNoFromIPG,
			String txnAmount,String tokenId,String cardAlias) throws Exception {
		boolean result = false;
		PreparedStatement pstmt = null;
		Connection connection = null;
		String query = null;
		ResultSet rs = null;
		String transactionDateTime = null;
		String txnMode = "C";
		String txnCurrencyId = "404";
		String systemReference = null;
		float balanceafterTopup = 0;
		String transactionCode=null;
		String walletId = null;
		String responseCode=null;
		
		try {
			PPWalletEnvironment.setComment(3, className, "CardNumber is " + cardNumber);

			
			connection = super.getConnection();
			connection.setAutoCommit(false);
			transactionDateTime = Utilities.getMYSQLCurrentTimeStampForInsert();
			
		
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyMMddHHmmssSSS");
			transactionCode=formatter1.format(new java.util.Date()) + Utilities.genAlphaNumRandom(9);// our system generated transaction
			
			
			//If response is success the Credit the wallet and proceed
			
				//get one wallet to topup
			
				// **********get the wallet balance
				String previouscurrentBalance = null;
				
				query = "select  walletid, currbal from wallet_details   where relationshipno = ? order by currbal desc  limit 1";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, relationshipNo);
				rs = (ResultSet) pstmt.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						previouscurrentBalance = (StringUtils.trim(rs.getString("currbal")));
						walletId = (StringUtils.trim(rs.getString("walletid")));
						PPWalletEnvironment.setComment(3, className, "The pointbalance   is " + previouscurrentBalance);
						PPWalletEnvironment.setComment(3, className, "The topUpAmount   is " + txnAmount);
					} // end of while
				} // end of if rs!=null check
				pstmt.close();

				systemReference = PPWalletEnvironment.getCodeTokenWalletTopup() + "-" + referenceNoFromIPG + "-";
				PPWalletEnvironment.setComment(3, className, "The systemreference  is " + systemReference);		
			//                                       1         2             3          4          5             6         7
				query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime)  "
						+ " values (?, ?, ?, ?, ?, ?, ? )  ";
				// 1 2 3 4 5 6 7
				// systemreference = (bank_id + bankcode + topUpAmount)+(
				// RandomStringUtils.random(10, false, true)).toString();
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, referenceNoFromIPG); 
				pstmt.setString(2, walletId);
				pstmt.setString(3, systemReference + "C");
				pstmt.setString(4, txnAmount);
				pstmt.setString(5, txnCurrencyId);
				pstmt.setString(6, txnMode);
				pstmt.setString(7, transactionDateTime);
				try {
					PPWalletEnvironment.setComment(3, className, "transactionCode is " + referenceNoFromIPG+ "walletId is "+  walletId +
							"systemReference is "+ systemReference+"C" + "topUpAmount IS "+txnAmount+ " txnMode"+txnMode);

					pstmt.executeUpdate();
				} catch (Exception e) {
					throw new Exception(" failed query " + query + " " + e.getMessage());
				}
				pstmt.close();
				PPWalletEnvironment.setComment(3, className, "inserted into wallet_txn_bc topUpAmount " + txnAmount);


				// ** Step 2: Update the Wallet Ledger, **** (IMP: Consider updating the
				// Blockchain wallet ledger also)
				// *******update wallet with new ballance
				 balanceafterTopup = Float.parseFloat(previouscurrentBalance) + Float.parseFloat(txnAmount);

				PPWalletEnvironment.setComment(3, className,
						"The new balance  for walletid is" + walletId + " is" + balanceafterTopup);

				query = "update wallet_details set currbal =  ? , lastupdated = UTC_TIMESTAMP  where walletid=?  ";
				pstmt = connection.prepareStatement(query);
				pstmt.setFloat(1, balanceafterTopup); //
				pstmt.setString(2, walletId);
				try {
					pstmt.executeUpdate();
				} catch (Exception e) {
					throw new Exception(" failed query " + query + " " + e.getMessage());
				}
				pstmt.close();
				PPWalletEnvironment.setComment(3, className, "The currbal after topup  is " + balanceafterTopup);
				
				//Record card transaction in topup ledger
					                          	//	1		    2 			   3	        	4			   5	    	6			7		8	      9              10	   	
				query = "insert into topup_txn_bc (txncode, cust_walletid, referencecode, relationshipno, sysreference, txnamount, topupmode, location, txncurrencyid, txndatetime) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ? , ? ,? ) ";
				//		   1  2  3  4  5  6  7  8   9  10 
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, referenceNoFromIPG); 						 
				pstmt.setString(2, walletId); 						 
				pstmt.setString(3, transactionCode);	// our system generated code
				pstmt.setString(4, relationshipNo); 						 
				pstmt.setString(5, systemReference+userType);  
				pstmt.setString(6, txnAmount);
				pstmt.setString(7, "T" );
				pstmt.setString(8, "");  
				pstmt.setString(9, txnCurrencyId );
				pstmt.setString(10, transactionDateTime);
				
				try {
				pstmt.executeUpdate();
				}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
				}
				
				pstmt.close();
		
				/*
				 *  Push the card details here
				 */
						//		     				 						1		 2		  		3			 4		   5	 6			7			8				9		
							query = "insert into card_tokenization_bc (tokenid, relationshipno, usertype, cardnumber, card_type, card_alias, cardname, dateofexpiry,  createdon) "
										+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
										//		   1  2  3  4  5  6  7	8  9 	  
								pstmt = connection.prepareStatement(query);
								pstmt.setString(1, (tokenId)); 					// tokenid
								pstmt.setString(2, relationshipNo); 						// relationshipno
								pstmt.setString(3, userType);							// usertype
								pstmt.setString(4, Utilities.encryptString(cardNumber));  // cardnumber
								pstmt.setString(5,"E" ); 								//cardtype
								pstmt.setString(6, cardAlias );					 //  cardalias
								pstmt.setString(7, Utilities.encryptString(cardName));   // cardname
								pstmt.setString(8, Utilities.encryptString(dateOfExpiry));   // dateofexpiry
								pstmt.setString(9, transactionDateTime ); //  createdon
								try {
									pstmt.executeUpdate();
									}catch(Exception e) {
										throw new Exception (" failed query "+query+" "+e.getMessage());
									}
								
								pstmt.close();
				
				
								 query = "insert into temp_blockchain_token (tokenid, cardno, dateofexpiry, user_relno, cardname, usertype, createdon) "
											+ "values (?, ?, ?, ?, ?, ?, ?) ";
											//		   1  2  3  4  5  6  7	  
									pstmt = connection.prepareStatement(query);
									pstmt.setString(1, (tokenId)); 					// tokenid
									pstmt.setString(2, Utilities.encryptString(cardNumber)); 						// userid
									pstmt.setString(3,  Utilities.encryptString(dateOfExpiry) );							// cardnumber
									pstmt.setString(4, relationshipNo);  // relationship
									pstmt.setString(5, cardName);// cardname
									pstmt.setString(6, userType);// userType
									pstmt.setString(7, transactionDateTime);// userType
									try {
										pstmt.executeUpdate();
										}catch(Exception e) {
											throw new Exception (" failed query "+query+" "+e.getMessage());
										}
								connection.commit();
								
								result = true;
									}catch(Exception e) {
										connection.rollback();
										result = false;
										PPWalletEnvironment.setComment(1,className,"The exception in method recordAuthorizedPaymentViaCard  is  "+e.getMessage());
										throw new Exception ("The exception in method recordAuthorizedPaymentViaCard  is  "+e.getMessage());
										
									}finally {	
										if (connection != null)
										try {
											super.close();
										} catch (SQLException e) {
											e.printStackTrace();
										}
										if(result) {
									 /// call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream and cardvault stream of Blockchain having chainame ppwallet
//									WalletDao.insertGenericWalletTransactions(referenceNoFromIPG, walletId, systemReference, txnAmount, txnCurrencyId, txnMode, transactionDateTime);
//									PaymentDao.insertIntoCardVault(tokenId, cardNumber, cardName, dateOfExpiry, relationshipNo, userType);
											}
	if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDateTime!=null)  transactionDateTime=null; if(txnMode!=null)  txnMode=null;
		if(userType!=null)  userType=null;	if(txnCurrencyId!=null)  txnCurrencyId=null;if(systemReference!=null)  systemReference=null;
		if(balanceafterTopup!=0)  balanceafterTopup=0;
	
	}

	return result;
}
	
	

	public static synchronized boolean insertIntoCardVault(String tokenId, String cardNumber, String cardName, String dateOfExpiry, String relationshipNo, String userType) throws Exception{
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
				 
				
					PPWalletEnvironment.setComment(3,className,"Now inserting data to Blockchain  ");
					//PPWalletEnvironment.setComment(3,className,"Data entering into the blockchain:-  "+ tokenId+"||"+cardNumber+"||"+cardName+"||"+dateOfExpiry+"||"+relationshipNo+"||"+userType);

						 
								String jsonString = "{\"tokendetails\": "
										+ "{ \"tokenid\": \""+ tokenId  +"\", "
										+  "\"cardnumber\":\""+ Utilities.encryptString(cardNumber)  +"\", "
										+  "\"cardname\":\""+ Utilities.encryptString(cardName)+"\", "
										+  "\"dateofexpiry\": \""+ Utilities.encryptString(dateOfExpiry) +"\", "
										+  "\"urelno\" : \""+ relationshipNo +"\", "
										+  "\"utype\":\""+ userType +"\" "
										+  "\"createdon\":\""+ Utilities.getMYSQLCurrentTimeStampForInsert()+"\" "
										+ "}}";
	
								PPWalletEnvironment.setComment(3,className,"Data entering into the blockchain:-  "+jsonString );
								String jsonHexValue = Utilities.asciiToHex(jsonString);
								 HttpPost jrequest = new HttpPost( PPWalletEnvironment.getMultiChainRPCURLPORT() );
								 jrequest.setEntity( new StringEntity(  "{\"method\":\"publish\",\"params\":[\""+streamName+"\",\""+ tokenId+"\",\""+jsonHexValue+"\"],\"id\":1,\"chain_name\":\""+chainName+"\"}"  ) );
								
							 		
							 		jresponse = client.execute( jrequest );
									HttpEntity entity = jresponse.getEntity();
							 		JsonObject responseJson =  JsonParser.parseString(EntityUtils.toString(entity)).getAsJsonObject();
							 		PPWalletEnvironment.setComment(3,className,"Response after Blockchain addition is "+responseJson.toString());	
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
	
	
	
	
	
	
	
	
	
	
	
	
	
		
}

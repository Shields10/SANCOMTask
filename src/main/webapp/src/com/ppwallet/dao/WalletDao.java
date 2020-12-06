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
import com.ppwallet.model.CardDetails;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.Transaction;
import com.ppwallet.model.Wallet;
import com.ppwallet.utilities.Utilities;

public class WalletDao extends HandleConnections {
	private static final long serialVersionUID = 1L;
	private static String className = WalletDao.class.getSimpleName();
	
	//this method gets individual wallet details*/
	public ArrayList<Wallet> getWalletDetails(String relationshipNo) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Wallet> arrWallet = null;
		
		try{
			connection = super.getConnection();	
			                    //  1         2           3          4        5        6           7              8
				query = "select walletid, relationshipno, walletdesc, usertype,  status, currbal, currencyid,  lastupdated "
						+ " from wallet_details   "
						+ "  where relationshipno=?  ";
		  
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
				 	PPWalletEnvironment.setComment(2,className,"Array wallet size is "+arrWallet.size());
				 	} 
			 if(arrWallet!=null)
				 if(arrWallet.size()==0)
					 arrWallet=null;
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getWalletDetails  is  "+e.getMessage());
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
	
	
	//second phase addition
	
	public ArrayList<CardDetails> getCardDetails(String relationshipNo) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<CardDetails> arrCardDetails= null;
		
		
		try{
			connection = super.getConnection();	
			                    //   1         2        	3         4                
				 query = "select tokenid, card_alias, cardnumber, dateofexpiry from card_tokenization_bc  where relationshipno=? and card_type=? ";
		  
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, relationshipNo);
				pstmt.setString(2, "E");
				rs = (ResultSet)pstmt.executeQuery();
				
				 if(rs!=null){
					 arrCardDetails = new ArrayList<CardDetails>();
					 
					 	while(rs.next()){
							CardDetails m_CardDetails=new CardDetails();
					 		m_CardDetails.setTokenNumber(( StringUtils.trim(rs.getString("tokenid"))    ));
					 		m_CardDetails.setCardAlias(( StringUtils.trim(rs.getString("card_alias"))    ));
					 		m_CardDetails.setCardNumber (Utilities.maskCardNumber( Utilities.decryptString((StringUtils.trim(rs.getString("cardnumber")))  )));
					 		m_CardDetails.setDateOfExpiry( Utilities.decryptString((StringUtils.trim(rs.getString("dateofexpiry")))  ));
							arrCardDetails.add(m_CardDetails);
					 		} // end of while
					 	
					 	} //end of if rs!=null check
				 // validate the password
				 if(arrCardDetails!=null)
					 if(arrCardDetails.size()==0)
						 arrCardDetails=null;
				
			}catch(Exception e){
				PPWalletEnvironment.setComment(1,className,"The exception in method getCardDetails  is  "+e.getMessage());
				throw new Exception ("The exception in method getCardDetails  is  "+e.getMessage());			
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
		
		return arrCardDetails;
		
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
				PPWalletEnvironment.setComment(3,className," Wallet Transaction size is " + arrTransaction.size() );

			 	} 
		 if(arrTransaction!=null)
			 if(arrTransaction.size()==0)
				 arrTransaction=null;
		
		}catch(Exception e) {
				PPWalletEnvironment.setComment(1,className,"The exception in method getAllWalletTransactionForUser  is  "+e.getMessage());
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
	
	//mobile
	
	public ArrayList<Transaction> getAllWalletTransactionForUserJSON(String walletId,String startDate, String endDate) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		
		ArrayList<Transaction> arrTransaction = null;

		
		try {
			connection = super.getConnection();	
	                      //   1         2         3             4          5             6         7 
			query = "select  txncode, walletid, sysreference, txnamount, txncurrencyid,  txnmode, txndatetime "
					+ " from wallet_txn_bc where txndatetime BETWEEN ? AND ? AND walletid in (select walletid from wallet_details where walletid =? )  order by txndatetime desc limit 0, 1000 ";
			
			
		
		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, startDate);
		pstmt.setString(2, endDate);
		pstmt.setString(3, walletId);
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
			 		m_Transaction.setTxnDateTime (Utilities.mobileformartDate(StringUtils.trim(rs.getString("txndatetime"))));
			 		arrTransaction.add(m_Transaction);
			 		
		 
			 		PPWalletEnvironment.setComment(2,className,"Arraylist size is  " +arrTransaction.size() );
			 		
			 		} // end of while
			 	
			 	} 
		 if(arrTransaction!=null)
			 if(arrTransaction.size()==0)
				 arrTransaction=null;
		
		}catch(Exception e) {
				PPWalletEnvironment.setComment(1,className,"The exception in method getAllWalletTransactionForUser  is  "+e.getMessage());
				throw new Exception ("The exception in method getAllWalletTransactionForUser  is  "+e.getMessage());		
			
		}finally {
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
	
	public ArrayList<Transaction> getAllTransactionsForWalletGraph(String walletId) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String currentDate= null;
		String lastSevenDaysDate= null;
		String query = null;
		ArrayList<Transaction> arrTransaction = null;
		try{
			connection = super.getConnection();	
			
			currentDate = Utilities.getMYSQLCurrentTimeStampForInsert();
			lastSevenDaysDate = Utilities.getDateCalculate(Utilities.getMYSQLCurrentTimeStampForInsert(), -7, "yyyy-MM-dd HH:mm:ss");

				query = "select  txncode, walletid, sysreference, txnamount, txncurrencyid,  txnmode,txndatetime "
						+ " from wallet_txn_bc where walletid=? and txndatetime between ? and ? order by txndatetime desc  ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, walletId);
			pstmt.setString(2, lastSevenDaysDate);
			pstmt.setString(3, currentDate);
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
			 if(arrTransaction!=null)
				 if(arrTransaction.size()==0)
					 arrTransaction=null;
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllTransactionsForWallet  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllTransactionsForWallet  is  "+e.getMessage());			
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close(); if (currentDate!=null) currentDate=null; if (lastSevenDaysDate!=null) lastSevenDaysDate=null;
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
						PPWalletEnvironment.setComment(3,className,"The Sender wallet Previous ballance for walletid!" +senderWalletId +"for userrelationshipno"+ senderRelationship +"is "+ senderpreviouswalletBalance );
								} // end of while
							} //end of if rs!=null check
					pstmt.close();
					
					//***********Check if sender has sufficient balance in the wallet************changes here
					if(Float.parseFloat(senderpreviouswalletBalance) >= Float.parseFloat(payAmount)) {
						
						PPWalletEnvironment.setComment(3,className,"Balance is enough to send money. Current Balance is"+ senderpreviouswalletBalance );
		
				
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
								PPWalletEnvironment.setComment(3,className,"The Receiver wallet Previous ballance for walletid!" +receiverWalletId +"is "+ receiverpreviouswalletBalance );

								} // end of while
							} //end of if rs!=null check
					pstmt.close();
					
					float senderbalanceafterDebit = Float.parseFloat(senderpreviouswalletBalance) - Float.parseFloat(payAmount);
					float receiverrbalanceafterCredit = Float.parseFloat(receiverpreviouswalletBalance) + Float.parseFloat(payAmount);
					PPWalletEnvironment.setComment(3,className,"The Sender wallet ballance after Debit is" + senderbalanceafterDebit );
					PPWalletEnvironment.setComment(3,className,"The Receiver wallet ballance after Credit is" + receiverrbalanceafterCredit );

	          //*******Step 3: Update the Sender wallet Ledger
			     query = " update wallet_details set currbal= ?, lastupdated = ? where  walletid=? ";
						pstmt = connection.prepareStatement(query);
						pstmt.setFloat(1, senderbalanceafterDebit);
						pstmt.setString(2, transactionDatetime); 
						pstmt.setString(3, senderWalletId); 
						try {
							PPWalletEnvironment.setComment(3,className,"update sender wallet amount" + senderbalanceafterDebit );

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
						PPWalletEnvironment.setComment(3,className,"update receiver wallet amount" + receiverrbalanceafterCredit );

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
					PPWalletEnvironment.setComment(3,className,"Executed Sender wallet_tnx_bc amount"+"senderTransactioncode is"+ senderTransactionCode +"senderWalletId is"+senderWalletId 
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
				PPWalletEnvironment.setComment(3,className,"systemreference for Receiver wallet_tnx_bc is"  + systemreference );
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
					PPWalletEnvironment.setComment(3,className,"Executed receiver wallet_tnx_bc amount"+ payAmount   );

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
							query = "select pointbalance balance from loyalty_points_bc where relationshipno=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where relationshipno = ? ) ";
									pstmt = connection.prepareStatement(query);
									pstmt.setString(1, senderRelationship);
									pstmt.setString(2, senderRelationship);
									rs = (ResultSet)pstmt.executeQuery();
									 if(rs!=null){
										while(rs.next()){	 			 			
											previousPoinsBalance = (StringUtils.trim(rs.getString("balance"))  );
											} // end of while
									} //end of if rs!=null check
										 pstmt.close();
								 if(previousPoinsBalance==null) {
									 firstime  =true;
										PPWalletEnvironment.setComment(3,className,"No previous record present for walletid: "+senderWalletId);
									 previousPoinsBalance = Float.toString(Float.parseFloat(payAmount) * Float.parseFloat( pointsConversion ));
								  }else {
									 PPWalletEnvironment.setComment(3,className,"Previus Balance for walletid : "+senderWalletId+" and userid"+senderRelationship+  " is "  +previousPoinsBalance);
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
				PPWalletEnvironment.setComment(3,className,"Insufficient balance in your account.  Your balance is" + senderpreviouswalletBalance );
				result = false;
                    
			}
		
			}catch(Exception e){
				result = false;
				connection.rollback();
				PPWalletEnvironment.setComment(1,className,"The exception in method updateWalletLedgers  is  "+e.getMessage());
				throw new Exception ("The exception in method updateWalletLedgers  is  "+e.getMessage());
			}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
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
					if (rs != null)	rs.close(); if (pstmt != null) pstmt.close();if(transactionDatetime!=null)  transactionDatetime=null; if(receiverTransactionCode!=null)  receiverTransactionCode=null;
					if(userType!=null)  userType=null; if(senderTransactionCode!=null)  senderTransactionCode=null; if(receiverRefNo!=null)  receiverRefNo=null;
					if(systemreference!=null)  systemreference=null; if(senderRefNo!=null)  senderRefNo=null; 
			
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
			 if(arrTransaction!=null)
				 if(arrTransaction.size()==0)
					 arrTransaction=null;
			 
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllTransactionsForWallet  is  "+e.getMessage());
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
				PPWalletEnvironment.setComment(3,className,"in insertGenericWalletTransactions Blockchain   ");

				String chainName = PPWalletEnvironment.getBlockChainName();
				String streamName = "walletledger"; 
					
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				  credsProvider.setCredentials(new AuthScope(PPWalletEnvironment.getMultiChainRPCIP(), Integer.parseInt(PPWalletEnvironment.getMultiChainRPCPort())),
				  new UsernamePasswordCredentials(PPWalletEnvironment.getMultiChainUser(), PPWalletEnvironment.getRPCAuthKey()));
				// Connect to Blockchain and create the Token blocks 
					PPWalletEnvironment.setComment(3,className,"storing encrypted t. etc  ");

					 client = HttpClients.custom().setDefaultCredentialsProvider( credsProvider ).build();
					 
					PPWalletEnvironment.setComment(3,className," *** after Credentials ");
					 //txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime
						PPWalletEnvironment.setComment(3,className,"Now inserting data to Blockchain  ");

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
								 		PPWalletEnvironment.setComment(3,className,"Response after Blockchain addition is "+responseJson.toString());	
					        result = true;	
			}catch(Exception e){
				result = false;
				PPWalletEnvironment.setComment(1,className,"The exception in method insertGenericWalletTransactions  is  "+e.getMessage());
				throw new Exception ("The exception in method insertGenericWalletTransactions  is  "+e.getMessage());
			}finally{
				try {
					if(client!=null)				client.close();
					if(jresponse!=null)				 jresponse.close();
				}catch (Exception ee) {
					PPWalletEnvironment.setComment(1,className,"The exception in method insertGenericWalletTransactions, finally block is  "+ee.getMessage());
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
			PPWalletEnvironment.setComment(1,className,"The exception in method getRegisteredActiveWallets  is  "+e.getMessage());
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
	

	public ArrayList<CustomerDetails> getRegisteredActiveUserWallets() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<CustomerDetails> arrCustWallet = null;
		try {
			connection = super.getConnection();	                    
                                //  1                             2                             3                         4      
			query = "select a.relationshipno relationshipno, a.customername customername, a.custemail custemail, a.custcontact custcontact,  "
					+ " b.walletid walletid  from customer_details a, wallet_details b where a.relationshipno=b.relationshipno and a.status=? and b.status=?  ";
  			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "A");
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arrCustWallet = new ArrayList<CustomerDetails>();
				 	while(rs.next()){	 
				 		CustomerDetails userWallet=new CustomerDetails();
				 		userWallet.setRelationshipNo( StringUtils.trim(rs.getString("relationshipno"))    );
				 		userWallet.setWalletId( StringUtils.trim(rs.getString("walletid"))    );
				 		userWallet.setCustomerName( StringUtils.trim(rs.getString("customername"))    );
				 		userWallet.setEmail( StringUtils.trim(rs.getString("custemail"))    );
				 		userWallet.setContact( StringUtils.trim(rs.getString("custcontact"))    );
				 		arrCustWallet.add(userWallet);
				 		} 		 	
				 	} 
			 if(arrCustWallet!=null)
				 if(arrCustWallet.size()==0)
					 arrCustWallet=null;	
			
		}catch(Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getRegisteredActiveWallets  is  "+e.getMessage());
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
		return arrCustWallet;
	}
	
	
	
	
	
/*this methode returns details for specific registered customer*/
	public ArrayList<CustomerDetails> getAllCustDetailsForRegistration(String walletId, String custEmail,
			String custName, String custPhoneNumber) throws Exception{
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<CustomerDetails> arrCustomerDetails = null;
		try {
			PPWalletEnvironment.setComment(3,className,"in  getAllCustDetailsForRegistration searched Receiverwalletid is "+
		walletId+ "searched custEmail is  "+custEmail+ "searched custName  "+ custName + "phoneNumber is  "+ custPhoneNumber            );

			connection = super.getConnection();	                    
                                //  1                             2                             3                         4      
			query = "select a.relationshipno relationshipno, a.customername customername, a.custemail custemail, a.custcontact custcontact,  "
					+ " b.walletid walletid from customer_details a, wallet_details b where a.relationshipno=b.relationshipno and a.status=? and b.status=?   ";
			if(walletId.equals("")==false) {	
				query+= " and b.walletid = ? and  ";
			}else {
					if(custEmail.equals("")==false) {
						query+= " and a.custemail like '%"+custEmail+"%' and  ";
					}
					if(custName.equals("")==false) {
						query+= " and a.customername like '%"+custName+"%' and  ";
					}
					if(custPhoneNumber.equals("")==false) {
						query+= " and a.custcontact like '%"+custPhoneNumber+"%' and  ";
					}
			}
			query+= "  1=1 ";
			
			PPWalletEnvironment.setComment(1,className," Final query is" + query);

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "A");
			pstmt.setString(2, "A");
			if(walletId.equals("")==false) {	
				pstmt.setString(3, walletId);
			}
		
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arrCustomerDetails = new ArrayList<CustomerDetails>();
				 	while(rs.next()){	 
				 		CustomerDetails userWallet=new CustomerDetails();
				 		userWallet.setRelationshipNo( StringUtils.trim(rs.getString("relationshipno"))    );
				 		userWallet.setWalletId( StringUtils.trim(rs.getString("walletid"))    );
				 		userWallet.setCustomerName( StringUtils.trim(rs.getString("customername"))    );
				 		userWallet.setEmail( StringUtils.trim(rs.getString("custemail"))    );
				 		userWallet.setContact( StringUtils.trim(rs.getString("custcontact"))    );
				 		arrCustomerDetails.add(userWallet);
				 		} 
					//PPWalletEnvironment.setComment(3,classname," Total search Results is" + arrCustomerDetails.size());

				 	} 
			 if(arrCustomerDetails!=null)
				 if(arrCustomerDetails.size()==0)
					 arrCustomerDetails=null;	
			
		}catch(Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllCustDetailsForRegistration  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllCustDetailsForRegistration  is  "+e.getMessage());			
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
	
		return arrCustomerDetails;	
		}

	public boolean insertReceiverWalletForRegistration(String receiverWalletId, String senderRegNo, String receiverRelationNo) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		boolean result = false;
		try{

			connection = super.getConnection();	
			 connection.setAutoCommit(false);
			  //                                                       1             2                     3                  4
			 query = "insert into customer_receiver_wallet_rel (relationshipno, receiverwalletid, receiver_relationshipno,  status) "
						+ "values (?, ?, ?, ?) ";
						//		   1  2  3  4
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, (senderRegNo)); 
				pstmt.setString(2, ( receiverWalletId)); 
				pstmt.setString(3, ( receiverRelationNo)); 				
				pstmt.setString(4, ("A")); 

				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}	
			connection.commit();
			result = true;

		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method insertReceiverWalletForRegistration  is  "+e.getMessage());
			throw new Exception ("The exception in method insertReceiverWalletForRegistration  is  "+e.getMessage());			
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
		return result;
	}

	/*this method  returns registered wallets by customer to send money to */
	
	public ArrayList<CustomerDetails> getAllRegisteredWalletsForASender(String relationshipNo) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<CustomerDetails> arrRegisteredrReceiverDetails = null;
		try {
			connection = super.getConnection();	                    
			query = " select a.relationshipno receiverrelationshipno , a.customername receivercustomername, a.custemail receiveremail, a.custcontact receivercontact, b.walletid receiverwalletid "
					+ " from customer_details a, wallet_customer_rel b where a.relationshipno=b.relationshipno and b.walletid in "
					+ " (select receiverwalletid from customer_receiver_wallet_rel where relationshipno = ? and status=?)";

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			pstmt.setString(2, "A");
			
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arrRegisteredrReceiverDetails = new ArrayList<CustomerDetails>();
				 	while(rs.next()){	 
				 		CustomerDetails m_CustomerDetails=new CustomerDetails();
				 		m_CustomerDetails.setRelationshipNo( StringUtils.trim(rs.getString("receiverrelationshipno"))    );
				 		m_CustomerDetails.setWalletId( StringUtils.trim(rs.getString("receiverwalletid"))    );
				 		m_CustomerDetails.setCustomerName( StringUtils.trim(rs.getString("receivercustomername"))    );
				 		m_CustomerDetails.setEmail( StringUtils.trim(rs.getString("receiveremail"))    );
				 		m_CustomerDetails.setContact( StringUtils.trim(rs.getString("receivercontact"))    );
				 		arrRegisteredrReceiverDetails.add(m_CustomerDetails);
				 		} 
					PPWalletEnvironment.setComment(3,className," arrRegisteredrReceiverDetails " + arrRegisteredrReceiverDetails.size()  );

				 	} 
			 if(arrRegisteredrReceiverDetails!=null)
				 if(arrRegisteredrReceiverDetails.size()==0)
					 arrRegisteredrReceiverDetails=null;
			
			
		}catch(Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllRegisteredWalletsForASender  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllRegisteredWalletsForASender  is  "+e.getMessage());			
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
	
		return arrRegisteredrReceiverDetails;
	}
	
	
	//Eric
	// Customer wallet top up using QR code

	public String initiateTopupViaMerch(String custWalletId, String billerCode,  String currencyId, String location, String topUpAmount, String relationshipNo ) 
			throws Exception {
			PreparedStatement pstmt = null;
			Connection connection = null;
			String query = null;
			String transactionCode = null;
			ResultSet rs = null;
			String transactionDatetime = null;
			String txnMode = "C";
			String userType = "C";
//			String txnCurrencyId = "404";
			String merchWalletId = null;
			boolean result = false;
			String systemReference = null;
			float merchCommission = 0;
		try {
			PPWalletEnvironment.setComment(3, className, "in initiate Payment by Merchant");

			connection = super.getConnection();
			connection.setAutoCommit(false);
			transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyMMddHHmmssSSS");
			transactionCode = formatter1.format(new java.util.Date()) + Utilities.genAlphaNumRandom(9);

//			get the wallet ballance
			
			String previouscurrentBalance = null;
			query = "select currbal  from wallet_details where walletid=?  ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, custWalletId);
			rs = (ResultSet) pstmt.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					previouscurrentBalance = (StringUtils.trim(rs.getString("currbal")));
					PPWalletEnvironment.setComment(3, className, "The BAL   is " + previouscurrentBalance);
					PPWalletEnvironment.setComment(3, className, "The topUpAmount   is " + topUpAmount);
				} // end of while
			} // end of if rs!=null check
			pstmt.close(); if(rs!=null) rs.close();

			// 1 2 3 4 5 6 7
			systemReference = PPWalletEnvironment.getCodeWalletTopUpFromMerchant() + "-" + transactionCode + "-";
			PPWalletEnvironment.setComment(3, className, "The systemreference  is " + systemReference);
			
			
			
//			get new wallet balance
			
			float balanceafterTopup = Float.parseFloat(previouscurrentBalance) + Float.parseFloat(topUpAmount);

			PPWalletEnvironment.setComment(3, className,
					"The new balance  for walletid is" + custWalletId + " is" + balanceafterTopup);
			 
			query = "select walletid merchwalletid, currbal currbal from wallet_details where relationshipno=? and usertype=?  ";
					
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, billerCode); 
					pstmt.setString(2, "M"); 
					rs = (ResultSet)pstmt.executeQuery();	
					 if(rs!=null){
					 	while(rs.next()){	 			 			
					 		merchWalletId = (StringUtils.trim(rs.getString("merchwalletid"))    );
			        PPWalletEnvironment.setComment(3,className,"The biller wallet id  " +merchWalletId  );
									} // end of while
								} //end of if rs!=null check
					 if( pstmt!=null)		pstmt.close();	if(rs!=null) rs.close();			 
				 
//				Get the MSF Value for the Cash Out
					 
		          String msfFixed = null;
		          String msfVariable = null;
		          
//	            Get the MSF for the Cash Out
				query = "select a.planid planid, a.plan_fee_fixed plan_fee_fixed, a.plan_fee_var plan_fee_var  " + 
						"   from merch_sys_msf_plan a, merch_msf_plan_relation b where a.plan_type=? and   a.planid=b.planid and b.billercode=? and b.status=?";
				
				
				
				
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, "C"); // plan type is C = Cash out
				pstmt.setString(2, billerCode); 
				pstmt.setString(3, "A"); // Status is A = Active
				PPWalletEnvironment.setComment(3,className,"The biller is  " +billerCode  );
				rs = (ResultSet)pstmt.executeQuery();	
				 if(rs!=null){
				 	while(rs.next()){	 			 			
				 		msfFixed = (StringUtils.trim(rs.getString("plan_fee_fixed"))    );
				 		msfVariable = (StringUtils.trim(rs.getString("plan_fee_var"))    );
		        PPWalletEnvironment.setComment(3,className,"The plan_fee_fixed for  billerCode " +billerCode  +" is "+ msfFixed + "for plan type B ");
		        PPWalletEnvironment.setComment(3,className,"The msfVariable for  billerCode " +billerCode  +" is "+ msfVariable + "for plan type B ");
								} // end of while
							} //end of if rs!=null check
				 PPWalletEnvironment.setComment(3,className,"The biller is  " +billerCode  );
				if( pstmt!=null)	pstmt.close();
				if( rs!=null)	rs.close();
	
				if( Float.parseFloat(msfFixed)>0  ) {
					merchCommission =  Float.parseFloat(msfFixed);
					
	//				TODO confirm if I should record debit
					
			        PPWalletEnvironment.setComment(3,className,"The merchCommission is  " +merchCommission  +" and businessCreditAmount is ");
	
				}else {
					merchCommission = (Float.parseFloat(msfVariable))*(Float.parseFloat(topUpAmount));
			        PPWalletEnvironment.setComment(3,className,"The merchCreditAmount is  " +merchCommission  + " and businessCreditAmount is ");
	
				}
					
		
			
//			insert into custimer wallet ledger
			
		//                                       1         2             3          4          5             6         7
			query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime)  "
					+ " values (?, ?, ?, ?, ?, ?, ? )  ";
							//  1  2  3  4  5  6  7
			// systemreference = (bank_id + bankcode + topUpAmount)+(
			// RandomStringUtils.random(10, false, true)).toString();
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionCode);
			pstmt.setString(2, custWalletId);
			pstmt.setString(3, systemReference + "C");
			pstmt.setFloat(4, Float.parseFloat(topUpAmount));
			pstmt.setString(5, currencyId);
			pstmt.setString(6, txnMode);
			pstmt.setString(7, transactionDatetime);
			try {
				PPWalletEnvironment.setComment(3, className, "transactionCode is " + transactionCode+ "walletId is "+  custWalletId +
						"systemReference is "+ systemReference+"C" + "topUpAmount IS "+topUpAmount+ " txnMode"+txnMode);

				pstmt.executeUpdate();
			} catch (Exception e) {
				throw new Exception(" failed query " + query + " " + e.getMessage());
			}
			pstmt.close();
			PPWalletEnvironment.setComment(3, className, "inserted into wallet_txn_bc topUpAmount " + topUpAmount);


		

//			update customer  wallet with new ballance
			

			query = "update wallet_details set currbal =  ? , lastupdated = UTC_TIMESTAMP  where walletid=?  ";
			pstmt = connection.prepareStatement(query);
			pstmt.setFloat(1, balanceafterTopup); //
			pstmt.setString(2, custWalletId);
			try {
				pstmt.executeUpdate();
			} catch (Exception e) {
				throw new Exception(" failed query " + query + " " + e.getMessage());
			}
			pstmt.close();
			PPWalletEnvironment.setComment(3, className, "The currbal after topup  is " + balanceafterTopup);
			
			//merch commission bc
			

//			insert into the wallet_txn_merch_commission_bc
		//												  			 1		2			3			4				5				6			7			8		   9
			query = "insert into wallet_txn_merch_commission_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, commisionvalue, txncurrencyid, txnmode, txndatetime) "
					+ "values (?, ?, ?, ?, ?, ?, ?,?,?) ";
							// 1  2  3  4  5  6  7 8 9
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, transactionCode); 
				pstmt.setString(2, PPWalletEnvironment.getCodeWalletTopUpFromMerchant());
				pstmt.setString(3, custWalletId);
				pstmt.setString(4, merchWalletId);
				pstmt.setString(5, systemReference +"M");							
				pstmt.setFloat(6, merchCommission);  //get commission from msf
				pstmt.setString(7, currencyId);
				pstmt.setString(8, "D"); // debit
				pstmt.setString(9, transactionDatetime);
			  	try {
					pstmt.executeUpdate();
						}catch(Exception e) {
									throw new Exception (" failed query "+query);
									}					
			  	if( pstmt!=null)				pstmt.close();		
			  	PPWalletEnvironment.setComment(3,className,"Executed customer wallet_txn_business_bc amount "+" Transactioncode is"+ transactionCode  +" customerWalletId is" + custWalletId + " merchantWalletId is "+merchWalletId 
						+"systemreference is  " +systemReference +"M" +" merchCommission  is  "+merchCommission +"currencyId is  "+ currencyId+ "transactionDatetime"+ transactionDatetime);
			
			//Record retail transaction in topup ledger
   	      									//	1		    2 			   3	        	4			   5	    	6			7		8	      9              10	   	
			query = "insert into topup_txn_bc (txncode, cust_walletid, referencecode, relationshipno, sysreference, txnamount, topupmode, location, txncurrencyid, txndatetime) "
			+ "values (?, ?, ?, ?, ?, ?, ?, ? , ? ,? ) ";
			//		   1  2  3  4  5  6  7  8   9  10 
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, transactionCode); 						 
			pstmt.setString(2, custWalletId); 						 
			pstmt.setString(3, billerCode);	
			pstmt.setString(4, relationshipNo); 						 
			pstmt.setString(5, systemReference+userType);  
			pstmt.setFloat(6, Float.parseFloat(topUpAmount));
			pstmt.setString(7, "M" );
			pstmt.setString(8, location);  
			pstmt.setString(9, currencyId );
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
//			String pointsaccrued = null;

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

			if (pointsConversion != null) {

				// ***Get loyalty balance for wallet
				query = "select pointbalance balance from loyalty_points_bc where relationshipno=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where relationshipno = ? ) ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, relationshipNo);
				pstmt.setString(2, relationshipNo);
				rs = (ResultSet) pstmt.executeQuery();
				if (rs != null) {
					while (rs.next()) {
						previousPoinsBalance = (StringUtils.trim(rs.getString("balance")));
						PPWalletEnvironment.setComment(3, className, "The pointbalance   is " + previousPoinsBalance);
					} // end of while
				} // end of if rs!=null check
				pstmt.close();
				if (previousPoinsBalance == null) {
					firstime = true;
					PPWalletEnvironment.setComment(3, className,
							"No previous record present for walletid: " + custWalletId + " for userType    " + userType);
				} else {
					PPWalletEnvironment.setComment(3, className, "Previus Balance for walletid : " + custWalletId + " is "+ previousPoinsBalance + " for userType " + userType);
				}

				// Step 4.3: Insert loyalty points for the sender user
				//PPWalletEnvironment.setComment(3, className, "above insert into loyalty_points_bc : " + walletId + "pointaccrued" + topUpAmount+ " Point balance  "+ Float.parseFloat(topUpAmount) + Float.parseFloat(previousPoinsBalance) );

				                               //         1          2               3           4          5              6             7         8      9
				query = "insert into loyalty_points_bc (walletid, relationshipno, usertype,  paymode, txnreference, pointaccrued, pointbalance, status, txndatetime) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ? ,? ) ";
				                 //1  2  3  4  5  6  7  8  9
				PPWalletEnvironment.setComment(3, className,"query "+query);
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, (custWalletId));
				pstmt.setString(2, relationshipNo); //
				pstmt.setString(3, userType);
				pstmt.setString(4, PPWalletEnvironment.getCodeWalletTopUpFromMerchant());
				pstmt.setString(5, systemReference+userType); // systemreference reference generated by the system
				pstmt.setFloat(6, Float.parseFloat(topUpAmount)); // pointaccrued
				if (firstime) {
					pstmt.setFloat(7, Float.parseFloat(topUpAmount)); // pointbalance
				} else {
					pstmt.setFloat(7, Float.parseFloat(topUpAmount) + Float.parseFloat(previousPoinsBalance)); // pointbalance
				}
				pstmt.setString(8, "U");
				pstmt.setString(9, transactionDatetime);

				PPWalletEnvironment.setComment(3, className,"topUpAmount "+topUpAmount+" walletId "+custWalletId+" relationshipNo "+relationshipNo);
				try {
					pstmt.executeUpdate();
				} catch (Exception e) {
					throw new Exception(" failed query " + query + " " + e.getMessage());
				}
				PPWalletEnvironment.setComment(3, className, "Inserting into loyalty_points_bc : " + custWalletId + "pointaccrued" + topUpAmount+ " Point balance  "+ Float.parseFloat(topUpAmount) + Float.parseFloat(previousPoinsBalance) );

			}
			connection.commit();
			result = true;

		} catch (Exception e) {
			connection.rollback();
			transactionCode = null;
			PPWalletEnvironment.setComment(1, className,
					"The exception in method initiateTopupViaMerch  is  " + e.getMessage());
			throw new Exception("The exception in method initiateTopupViaMerch is " + e.getMessage());
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
				WalletDao.insertGenericWalletTransactions(transactionCode, custWalletId, systemReference, topUpAmount,currencyId, txnMode, transactionDatetime);
				//WalletDao.insertGenericWalletTransactions(transactionCode, merchWalletId, systemReference +"M", String.valueOf(merchCommission), currencyId, txnMode, transactionDatetime  );

			}
			
			if (pstmt != null)    pstmt.close();if(txnMode!=null)  txnMode=null;
			if(transactionDatetime!=null) transactionDatetime=null; if(userType!=null) userType=null; if(currencyId!=null) currencyId=null;
			if(query!=null) query=null; if(merchWalletId!=null) merchWalletId=null; if(systemReference!=null) systemReference=null;
			if(merchCommission!=0) merchCommission=0;if(rs!=null) rs.close();
			
		}
		return transactionCode;
	}
}

package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.RetailPayTransaction;
import com.ppwallet.utilities.Utilities;

public class RetailPayDao extends HandleConnections {
	
	private static final long serialVersionUID = 1L;
	private static String className = RetailPayDao.class.getSimpleName();
	
	public String retailPaymentWithWallet(String relationshipNo, String billpaymode, String custWalletId, String billerCode,
		 String currencyId, String retailAmount, String payComment, String location) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		boolean result= false;
		String query = null;
		String transactionDatetime = null;
		String transactionCode = null;
		String systemReference = null;
		String customerTxnmode = "D";
		String merchantTxnmode = "C";
		String merchWalletId = null;
		String merchCurrentBallance = null;
		float merchCreditAmount = 0;
		float businessCreditAmount = 0;
		
		try{
			 transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
			 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS"); 
			 transactionCode = (formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9));
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
		//*****step 1 *****get the existing balance
				String custwalletBalance = null;
				 query = "select currbal from wallet_details where walletid=? ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, custWalletId);
					rs = (ResultSet)pstmt.executeQuery();	
					 if(rs!=null){
					 	while(rs.next()){	 			 			
			custwalletBalance = (StringUtils.trim(rs.getString("currbal"))    );
			PPWalletEnvironment.setComment(3,className,"The customer wallet balance for id" +custWalletId  +"is "+ custwalletBalance );
									} // end of while
								} //end of if rs!=null check
					 if( pstmt!=null)			pstmt.close();
					 if( rs!=null)			rs.close();
						
		  //********check if wallet has sufficient funds to pay the bill		
	    if(Float.parseFloat(custwalletBalance) >= Float.parseFloat(retailAmount)) { 
			//******Step 2******Deduct the retail pay Amount from the wallet
			
				 float remainingCustomerbalance;
				 remainingCustomerbalance = Float.parseFloat(custwalletBalance) - Float.parseFloat(retailAmount);
				 
			query = "select walletid merchwalletid, currbal currbal from wallet_details where relationshipno=? and usertype=?  ";
					
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, billerCode); 
					pstmt.setString(2, "M"); 
					rs = (ResultSet)pstmt.executeQuery();	
					 if(rs!=null){
					 	while(rs.next()){	 			 			
					 		merchWalletId = (StringUtils.trim(rs.getString("merchwalletid"))    );
					 		merchCurrentBallance = (StringUtils.trim(rs.getString("currbal"))    );
			        PPWalletEnvironment.setComment(3,className,"The biller wallet balance for id  " +merchWalletId  +"is "+ merchCurrentBallance );
									} // end of while
								} //end of if rs!=null check
					 if( pstmt!=null)		pstmt.close();				 
				 
				 
				  //*****Step 3*****Get the MSF Value for the Retail Pay
		          String msfFixed = null;
		          String msfVariable = null;
				   //get the MSF for the Retail Pay
				query = "select a.planid planid, a.plan_fee_fixed plan_fee_fixed, a.plan_fee_var plan_fee_var  " + 
						"   from merch_sys_msf_plan a, merch_msf_plan_relation b where a.plan_type=? and   a.planid=b.planid and b.billercode=? and b.status=?";
							
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, "R"); // plan type is R = Retail pay
				pstmt.setString(2, billerCode); 
				pstmt.setString(3, "A"); // Status is A = Active
				rs = (ResultSet)pstmt.executeQuery();	
				 if(rs!=null){
				 	while(rs.next()){	 			 			
				 		msfFixed = (StringUtils.trim(rs.getString("plan_fee_fixed"))    );
				 		msfVariable = (StringUtils.trim(rs.getString("plan_fee_var"))    );
		        PPWalletEnvironment.setComment(3,className,"The plan_fee_fixed for  billerCode " +billerCode  +" is "+ msfFixed + "for plan type B ");
		        PPWalletEnvironment.setComment(3,className,"The msfVariable for  billerCode " +billerCode  +" is "+ msfVariable + "for plan type B ");
								} // end of while
							} //end of if rs!=null check
				if( pstmt!=null)	pstmt.close();
				if( rs!=null)	rs.close();

				if( Float.parseFloat(msfFixed)>0  ) {
					merchCreditAmount = Float.parseFloat(retailAmount) - Float.parseFloat(msfFixed);
					businessCreditAmount = Float.parseFloat(retailAmount) - merchCreditAmount;
			        PPWalletEnvironment.setComment(3,className,"The merchCreditAmount is  " +merchCreditAmount  +" and businessCreditAmount is "+ businessCreditAmount + " for msfFixed ");

				}else {
					merchCreditAmount = Float.parseFloat(retailAmount) - (Float.parseFloat(msfVariable))*(Float.parseFloat(retailAmount));
					businessCreditAmount = Float.parseFloat(retailAmount) - merchCreditAmount;		
			        PPWalletEnvironment.setComment(3,className,"The merchCreditAmount is  " +merchCreditAmount  + " and businessCreditAmount is "+ businessCreditAmount + " for msfVariable ");

				}

				//****Step 4.1: Update the customer wallet details balance
				query = " update wallet_details set currbal= ?, lastupdated = ? where  walletid=? ";
				pstmt = connection.prepareStatement(query);
				pstmt.setFloat(1, remainingCustomerbalance);
				pstmt.setString(2, transactionDatetime); 
				pstmt.setString(3, custWalletId); 
					try {
			    pstmt.executeUpdate();
					}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
					  	}		
					if( pstmt!=null)	pstmt.close();
					
			PPWalletEnvironment.setComment(3,className,"update customer wallet id "+ custWalletId+" with  amount" + remainingCustomerbalance );

			   
					//****Step 4.2: Update the merchant wallet details balance
					query = " update wallet_details set currbal= ?, lastupdated = ? where  walletid=? ";
					pstmt = connection.prepareStatement(query);
					pstmt.setFloat(1, (     Float.parseFloat(merchCurrentBallance) + merchCreditAmount) );
					pstmt.setString(2, transactionDatetime); 
					pstmt.setString(3, merchWalletId); 
						try {
				    pstmt.executeUpdate();
						}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
						  	}		
						if( pstmt!=null)	pstmt.close();					
					
						
				PPWalletEnvironment.setComment(3,className,"update merchant wallet id "+ merchWalletId+" with  amount  " + (     Float.parseFloat(merchCurrentBallance) + merchCreditAmount) );
	
					
          // Step 4.3 insert into customer wallet ledger of DB
					systemReference =  PPWalletEnvironment.getCodeWalletToMerchantRetailPay() +"-"+ transactionCode+"-" ;
		                                        // 1		2			3			4			5				6		7
			query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
											+ "values (?, ?, ?, ?, ?, ?,  ?) ";
													// 1  2  3  4  5  6   7	
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, transactionCode); 			
				pstmt.setString(2, custWalletId); 				
				pstmt.setString(3, systemReference +"C");							
				pstmt.setFloat(4, Float.parseFloat(retailAmount));  
				pstmt.setString(5, currencyId);
				pstmt.setString(6, customerTxnmode); // Debit 
				pstmt.setString(7, transactionDatetime);
		      	try {
					pstmt.executeUpdate();
						}catch(Exception e) {
									throw new Exception (" failed query "+query);
									}					
		      	if( pstmt!=null)		pstmt.close();	
				PPWalletEnvironment.setComment(3,className,"Executed customer wallet_tnx_bc amount"+"Transactioncode is "+ transactionCode +" customerWalletId is  "+custWalletId 
				+"systemreference is  "+systemReference +"C" +"  Bill pay amount is "+retailAmount + " currencyId is "+ currencyId+ " transactionDatetime  "+ transactionDatetime);

				
				// ************ Step 4.4 insert into the merchant wallet ledger
				query = "insert into wallet_txn_merch_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
						+ "values (?, ?, ?, ?, ?, ?,  ?) ";
								// 1  2  3  4  5  6   7	
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, transactionCode); 			
					pstmt.setString(2, merchWalletId); 				
					pstmt.setString(3, systemReference +"M");							
					pstmt.setFloat(4, merchCreditAmount);  
					pstmt.setString(5, currencyId);
					pstmt.setString(6, merchantTxnmode); // Debit 
					pstmt.setString(7, transactionDatetime);
				  	try {
						pstmt.executeUpdate();
							}catch(Exception e) {
										throw new Exception (" failed query "+query);
										}					
				  	if( pstmt!=null) pstmt.close();	
	
					PPWalletEnvironment.setComment(3,className,"Executed wallet_txn_merch_bc amount "+" Transactioncode is"+ transactionCode +"merchantWalletId is  "+merchWalletId 
					+"systemreference is  " +systemReference +"M" +"  Bill pay amount is  "+retailAmount +"currencyId is  "+ currencyId+ "transactionDatetime"+ transactionDatetime);

					// ************ Step 4.5 insert into the business wallet ledger
				//												   1		2			3			4				5				6			7			8		   9
					query = "insert into wallet_txn_business_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
							+ "values (?, ?, ?, ?, ?, ?, ?,?,?) ";
									// 1  2  3  4  5  6  7 8 9
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, transactionCode); 
						pstmt.setString(2, PPWalletEnvironment.getCodeWalletToMerchantRetailPay());
						pstmt.setString(3, custWalletId);
						pstmt.setString(4, merchWalletId);
						pstmt.setString(5, systemReference +"M");							
						pstmt.setFloat(6, businessCreditAmount);  
						pstmt.setString(7, currencyId);
						pstmt.setString(8, "C"); // Credit 
						pstmt.setString(9, transactionDatetime);
					  	try {
							pstmt.executeUpdate();
								}catch(Exception e) {
											throw new Exception (" failed query "+query);
											}					
					  	if( pstmt!=null)				pstmt.close();		
					  	PPWalletEnvironment.setComment(3,className,"Executed customer wallet_txn_business_bc amount "+" Transactioncode is"+ transactionCode  +" customerWalletId is" + custWalletId + " merchantWalletId is "+merchWalletId 
								+"systemreference is  " +systemReference +"M" +" businessCreditAmount  is  "+businessCreditAmount +"currencyId is  "+ currencyId+ "transactionDatetime"+ transactionDatetime);

					
				  //*****Step 5*****Record retail transaction in retail ledger
					                     	      //	1		 2			3		4			5				6			7			8		   9			10      	11
				 query = "insert into retailpay_txn_bc (txncode, paytype, assetid, custrelno, billercode, custreference, tnmamount, txncurrencyid,location, paycomment, txndatetime) "
								+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
								//		   1  2  3  4  5  6  7  8  9  10 11
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, transactionCode); 						 
						pstmt.setString(2, "W"); 						 
						pstmt.setString(3, custWalletId);							 
						pstmt.setString(4, relationshipNo);  
						pstmt.setString(5, billerCode);
						pstmt.setString(6, systemReference +"C");
						pstmt.setFloat(7, Float.parseFloat(retailAmount));  
						pstmt.setString(8, currencyId);
						pstmt.setString(9, location);
						pstmt.setString(10, payComment);
						pstmt.setString(11, transactionDatetime);
						
						try {
							pstmt.executeUpdate();
							}catch(Exception e) {
								throw new Exception (" failed query "+query+" "+e.getMessage());
							}
			
						pstmt.close();
						//commit all transaction
						PPWalletEnvironment.setComment(3,className,"Now retailpay_txn_bc" );
						
						 // Step 6.1: Get the Loyalty Rules based on the type of transaction and conversion
						String pointsConversion = null;
						String previousPoinsBalance = null;
						boolean firstime = false;
						String pointsaccrued =null;
						 
						 query = "select pointsconversion from loyalty_rules where paymode=? and status = ? ";
							
							pstmt = connection.prepareStatement(query);
							pstmt.setString(1, PPWalletEnvironment.getCodeWalletToMerchantRetailPay());
							pstmt.setString(2, "A");
							rs = (ResultSet)pstmt.executeQuery();
							 if(rs!=null){
								 	while(rs.next()){	 			 			
								 		pointsConversion = (StringUtils.trim(rs.getString("pointsconversion"))  );
								 		} // end of while
								 	} //end of if rs!=null check 
							 pstmt.close();
							 if(pointsConversion!=null) {
								 //TODO change walletid to relno
								 query = "select pointbalance balance from loyalty_points_bc where relationshipno=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where relationshipno = ? ) ";
									pstmt = connection.prepareStatement(query);
									pstmt.setString(1, relationshipNo);
									pstmt.setString(2, relationshipNo);
									rs = (ResultSet)pstmt.executeQuery();
									 if(rs!=null){
										 	while(rs.next()){	 			 			
										 		previousPoinsBalance = (StringUtils.trim(rs.getString("balance"))  );
										 		} // end of while
										 	} //end of if rs!=null check
									 pstmt.close();
								PPWalletEnvironment.setComment(3,className,"Points Balance for walletid   "+ custWalletId + "  is " + previousPoinsBalance );
								 pointsaccrued = Float.toString((Float.parseFloat(retailAmount) * 1));

									PPWalletEnvironment.setComment(3,className,"pointsaccrued   "+ pointsaccrued );

								
								  if(previousPoinsBalance==null) { // For the first time 
									  firstime =true;
								  PPWalletEnvironment.setComment(3,	className,"***LOYALTY FIRST TIME***No previous record present for user: " +custWalletId); 
								 
								  }else {
									  PPWalletEnvironment.setComment(3,	className," Previous Point balance for user : " +custWalletId+ " is  " +previousPoinsBalance); 
								  }	 

									//***** Step 6.2****: Insert loyalty points for the sender user		
							 // systemreference = (walletId)+( RandomStringUtils.random(10, false, true)).toString(); 
										                             //	  	1		2				3			4			5			6				7        8        9
								query = "insert into loyalty_points_bc (walletid, relationshipno, usertype, paymode, txnreference, pointaccrued, pointbalance, status, txndatetime) "
											+ "values (?, ?, ?, ?, ?, ?, ?, ?,?) ";
											//		   1  2  3  4  5  6  7  8 9
										pstmt = connection.prepareStatement(query);
										pstmt.setString(1, (custWalletId)); 	
										pstmt.setString(2, relationshipNo);
										pstmt.setString(3, "C"); 						// 
										pstmt.setString(4, PPWalletEnvironment.getCodeWalletToMerchantRetailPay());	
										pstmt.setString(5,systemReference +"C" ); // transaction reference generated by the system
										pstmt.setFloat(6, Float.parseFloat(pointsaccrued));  // pointaccrued
										if(firstime)
										pstmt.setFloat(7, Float.parseFloat(pointsaccrued));  // pointaccrued
										else {
											pstmt.setFloat(7, Float.parseFloat(pointsaccrued) + Float.parseFloat(previousPoinsBalance )    );  // pointbalance
										}
										pstmt.setString(8,"U" );	
										pstmt.setString(9,transactionDatetime );	
										try {
										pstmt.executeUpdate();
										}catch(Exception e) {
											throw new Exception (" failed query "+query+" "+e.getMessage());
										}							 
							 }
							 PPWalletEnvironment.setComment(3,	className,"inserting into loyalyty  pointsaccrued " + pointsaccrued ); 
	
					    connection.commit();
						result = true;
						}else {
							throw new Exception ("Retail amount greater than balance, hence transaction not allowed");
						}
									
	    }catch(Exception e){
	    	transactionCode = null;
			connection.rollback();
			
			PPWalletEnvironment.setComment(1,className,"The exception in method retailPaymentWithWallet  is  "+e.getMessage());
			throw new Exception ("The exception in method retailPaymentWithWallet  is  "+e.getMessage());
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
				}
			 /// *****Step 7***** call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet
				if(result) {
					WalletDao.insertGenericWalletTransactions(transactionCode, merchWalletId, systemReference +"M", String.valueOf(merchCreditAmount), currencyId, merchantTxnmode, transactionDatetime  );
					WalletDao.insertGenericWalletTransactions(transactionCode, custWalletId, systemReference +"C", retailAmount, currencyId, customerTxnmode, transactionDatetime  );	
				}
				if(pstmt!=null) pstmt.close(); 	if(systemReference!=null);  systemReference=null;	if(customerTxnmode!=null);  customerTxnmode=null;	
				if(merchantTxnmode!=null);  merchantTxnmode=null;	if(merchWalletId!=null);  merchWalletId=null;	if(merchCurrentBallance!=null);  merchCurrentBallance=null;
				if(businessCreditAmount!=0);  businessCreditAmount=0; if(businessCreditAmount!=0);  businessCreditAmount=0;  if(transactionDatetime!=null);  transactionDatetime=null;
				if(rs!=null) rs.close();
			}
			return transactionCode;		 
	}
	

	public ArrayList<RetailPayTransaction> getRetailPayTransactionsForUser(String relationshipNo) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<RetailPayTransaction> arrRetailPayTransaction = null;
		String currentDate = null;
		String lastDaysDate = null;
		
		try{
			connection = super.getConnection();	
			currentDate = Utilities.getMYSQLCurrentTimeStampForInsert();
			lastDaysDate = Utilities.getDateCalculate(Utilities.getMYSQLCurrentTimeStampForInsert(), -5, "yyyy-MM-dd HH:mm:ss");
			
			PPWalletEnvironment.setComment(3,className," currentDate "+currentDate + " lastDaysDate "+lastDaysDate);
			
				query = "select txncode txncode, paytype paytype, assetid  assetid, billercode billercode, custreference custreference, "
						+ " tnmamount tnmamount, txncurrencyid txncurrencyid, txndatetime txndatetime from "
						+ " retailpay_txn_bc where custrelno=? and txndatetime between ? and ? order by txndatetime desc ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			pstmt.setString(2, lastDaysDate);
			pstmt.setString(3, currentDate);
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arrRetailPayTransaction = new ArrayList<RetailPayTransaction>();
				 	while(rs.next()){	
				 		RetailPayTransaction retailPayTransaction = new RetailPayTransaction();
				 		retailPayTransaction.setTxnCode(StringUtils.trim(rs.getString("txncode")));
				 		retailPayTransaction.setPayType(StringUtils.trim(rs.getString("paytype")));
				 		retailPayTransaction.setAssetId( StringUtils.trim(rs.getString("assetid")));
				 		retailPayTransaction.setBillerCode(StringUtils.trim(rs.getString("billercode")));	
				 		retailPayTransaction.setReferenceNo(StringUtils.trim(rs.getString("custreference")));	
				 		retailPayTransaction.setCurrencyId(StringUtils.trim(rs.getString("txncurrencyid")));	
				 		retailPayTransaction.setRetailAmount(StringUtils.trim(rs.getString("tnmamount")));	
				 		retailPayTransaction.setTxnDateTime(StringUtils.trim(rs.getString("txndatetime")));	
				 		arrRetailPayTransaction.add(retailPayTransaction);

				 		} // end of while
				 	} //end of if rs!=null check
			 if(arrRetailPayTransaction!=null)
				 if(arrRetailPayTransaction.size()==0)
					 arrRetailPayTransaction=null;
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getBillPayTransactionsForUser  is  "+e.getMessage());
			throw new Exception ("The exception in method getBillPayTransactionsForUser  is  "+e.getMessage());			
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
				if(currentDate!=null);  currentDate=null; if(lastDaysDate!=null);  lastDaysDate=null;
			}
		return arrRetailPayTransaction;
	}

}

package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.utilities.Utilities;

public class CashOutDao extends HandleConnections  {
	
	
	private static final long serialVersionUID = 1L;
	private static String className = CashOutDao.class.getSimpleName();
	
	public String cashOutTransaction(String relationshipNo, String custWalletId, String billerCode,
		 String currencyId, String cashoutAmount, String location) throws Exception{
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
//		String merchCurrentBallance = null;
		float merchCommission = 0;
//		float businessCreditAmount = 0;
		
		try{
			 transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
			 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS"); 
			 transactionCode = (formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9));
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
	 
//			step 1 get the existing balance
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
					 
						
//		check if wallet has sufficient funds to pay the bill
					 
	    if(Float.parseFloat(custwalletBalance) >= Float.parseFloat(cashoutAmount)) { 
	    	
//	    	Step 2 Deduct the cashout Amount from the wallet
			
				 float remainingCustomerbalance;
				 remainingCustomerbalance = Float.parseFloat(custwalletBalance) - Float.parseFloat(cashoutAmount);
				 
			query = "select walletid merchwalletid, currbal currbal from wallet_details where relationshipno=? and usertype=?  ";
					
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, billerCode); 
					pstmt.setString(2, "M"); 
					rs = (ResultSet)pstmt.executeQuery();	
					 if(rs!=null){
					 	while(rs.next()){	 			 			
					 		merchWalletId = (StringUtils.trim(rs.getString("merchwalletid"))    );
//					 		merchCurrentBallance = (StringUtils.trim(rs.getString("currbal"))    );
			        PPWalletEnvironment.setComment(3,className,"The biller wallet id  " +merchWalletId  );
									} // end of while
								} //end of if rs!=null check
					 if( pstmt!=null)		pstmt.close();				 
				 
//				 Step 3 Get the MSF Value for the Cash Out
					 
		          String msfFixed = null;
		          String msfVariable = null;
		          
//		          get the MSF for the Cash Out
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
//					TODO confirm if I should record debit
//					businessCreditAmount = Float.parseFloat(retailAmount) - merchCreditAmount;
			        PPWalletEnvironment.setComment(3,className,"The merchCommission is  " +merchCommission  +" and businessCreditAmount is ");

				}else {
					merchCommission = (Float.parseFloat(msfVariable))*(Float.parseFloat(cashoutAmount));
//					businessCreditAmount = Float.parseFloat(retailAmount) - merchCreditAmount;		
			        PPWalletEnvironment.setComment(3,className,"The merchCreditAmount is  " +merchCommission  + " and businessCreditAmount is ");

				}
				
//				Step 4.1: Update the customer wallet details balance
				
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

			   
//			TODO confirm if the remaining amount is credited to the merchant
					
					
//          Step 4.2 insert into customer wallet ledger of DB
					systemReference =  PPWalletEnvironment.getCodeWalletCashOutFromMerhant() +"-"+ transactionCode+"-" ;
		                                        // 1		2			3			4			5				6		7
			query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
											+ "values (?, ?, ?, ?, ?, ?,  ?) ";
													// 1  2  3  4  5  6   7	
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, transactionCode); 			
				pstmt.setString(2, custWalletId); 				
				pstmt.setString(3, systemReference +"C");							
				pstmt.setFloat(4, Float.parseFloat(cashoutAmount));  
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
				+"systemreference is  "+systemReference +"C" +"  Bill pay amount is "+cashoutAmount + " currencyId is "+ currencyId+ " transactionDatetime  "+ transactionDatetime);

				
				
				
					// ************ Step 4.3 insert into the wallet_txn_merch_commission_bc
				//												  			 1		2			3			4				5				6			7			8		   9
					query = "insert into wallet_txn_merch_commission_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, commisionvalue, txncurrencyid, txnmode, txndatetime) "
							+ "values (?, ?, ?, ?, ?, ?, ?,?,?) ";
									// 1  2  3  4  5  6  7 8 9
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, transactionCode); 
						pstmt.setString(2, PPWalletEnvironment.getCodeWalletCashOutFromMerhant());
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

					
				  //*****Step 5*****Record retail transaction in retail ledger
					                     	      //	1		 2			3		4			5				6			7			8		   9			10      
				 query = "insert into cashout_txn_bc (txncode, paytype, assetid, custrelno, billercode, custreference, tnmamount, txncurrencyid,location,  txndatetime) "
								+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
								//		   1  2  3  4  5  6  7  8  9  10 
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, transactionCode); 						 
						pstmt.setString(2, "W"); 						 
						pstmt.setString(3, custWalletId);							 
						pstmt.setString(4, relationshipNo);  
						pstmt.setString(5, billerCode);
						pstmt.setString(6, systemReference +"C");
						pstmt.setFloat(7, Float.parseFloat(cashoutAmount));  
						pstmt.setString(8, currencyId);
						pstmt.setString(9, location);
						pstmt.setString(10, transactionDatetime);
						
						try {
							pstmt.executeUpdate();
							}catch(Exception e) {
								throw new Exception (" failed query "+query+" "+e.getMessage());
							}
			
						pstmt.close();
						//commit all transaction
						PPWalletEnvironment.setComment(3,className,"Now commiting all Transactions" );
						
						 // Step 6.1: Get the Loyalty Rules based on the type of transaction and conversion
//							no loyalty
	
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
			 // Step call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet
				if(result) {
//					WalletDao.insertGenericWalletTransactions(transactionCode, merchWalletId, systemReference +"M", String.valueOf(merchCommission), currencyId, merchantTxnmode, transactionDatetime  );
					WalletDao.insertGenericWalletTransactions(transactionCode, custWalletId, systemReference +"C", cashoutAmount, currencyId, customerTxnmode, transactionDatetime  );	
				}
				if(pstmt!=null) pstmt.close(); 	if(systemReference!=null);  systemReference=null;	if(customerTxnmode!=null);  customerTxnmode=null;	
				if(merchantTxnmode!=null);  merchantTxnmode=null;	if(merchWalletId!=null);  merchWalletId=null; if(query!=null);  query=null;	
				if(merchCommission!=0);  merchCommission=0;  
				if(transactionDatetime!=null);  transactionDatetime=null;
				if(rs!=null) rs.close();
			}
			return transactionCode;		 
	}

}

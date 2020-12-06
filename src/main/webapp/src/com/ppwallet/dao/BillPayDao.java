package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.BillPayTransaction;
import com.ppwallet.model.BillerDetail;
import com.ppwallet.model.Merchant;
import com.ppwallet.utilities.Utilities;

public class BillPayDao extends HandleConnections{
	private static final long serialVersionUID = 1L;
	private static String className = BillPayDao.class.getSimpleName();

	public ArrayList<Merchant> getRegisteredActiveMerchants() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Merchant> arr_Merchant = null;
		
		try{
			connection = super.getConnection();	

				query = "select  a.merchantname merchantname, a.billercode billercode, a.companyname companyname, a.compregistration compregistration, a.mcccategoryid mcccategoryid, "
						+ " b.mcccategoryname mcccategoryname from merch_details a, merch_mcc_group b where a.status=? and a.mcccategoryid=b.mcccategoryid ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "A");
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arr_Merchant = new ArrayList<Merchant>();
				 	while(rs.next()){	
				 		Merchant m_Merchant = new Merchant();
				 		m_Merchant.setMerchantName(  StringUtils.trim(rs.getString("merchantname")));
				 		m_Merchant.setBillerCode(  StringUtils.trim(rs.getString("billercode")));
						m_Merchant.setCompanyName( StringUtils.trim(rs.getString("companyname")));
				 		m_Merchant.setCompanyRegistration( StringUtils.trim(rs.getString("compregistration")));	
				 		m_Merchant.setMccCategoryName( StringUtils.trim(rs.getString("mcccategoryname")));	
				 		m_Merchant.setMccCategoryId(  StringUtils.trim(rs.getString("mcccategoryid")));	
				 		arr_Merchant.add(m_Merchant);
				 		} // end of while
				 	} //end of if rs!=null check
			 if(arr_Merchant!=null)
				 if(arr_Merchant.size()==0)
					 arr_Merchant=null;
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getRegisteredActiveMerchants  is  "+e.getMessage());
			throw new Exception ("The exception in method getRegisteredActiveMerchants  is  "+e.getMessage());			
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
		return arr_Merchant;
	}

	public ArrayList<BillerDetail> getRegisteredBillerDetailsActive(String relationshipno) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<BillerDetail> arrBiller = null;
		
		try{
			connection = super.getConnection();	

				query = "select a.seqno seqno, a.billercode billercode, a.custrefno custrefno, a.currencyid currencyid, a.status status,  "
						+ "b.merchantname merchantname, b.companyname companyname, b.merchantid merchantid  from biller_details a, merch_details b where a.relationshipno=? and "
						+ " a.status=? and a.billercode=b.billercode";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipno);
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arrBiller = new ArrayList<BillerDetail>();
				 	while(rs.next()){	
				 		BillerDetail m_BillerDetail = new BillerDetail();
				 		m_BillerDetail.setSequenceNo(StringUtils.trim(rs.getString("seqno")));
				 		m_BillerDetail.setBillerCode(StringUtils.trim(rs.getString("billercode")));
				 		m_BillerDetail.setCustRefNo( StringUtils.trim(rs.getString("custrefno")));
				 		m_BillerDetail.setMerchantName(StringUtils.trim(rs.getString("merchantname")));
				 		m_BillerDetail.setMerchantId(StringUtils.trim(rs.getString("merchantid")));	
				 		m_BillerDetail.setCompanyName(StringUtils.trim(rs.getString("companyname")));
				 		m_BillerDetail.setCurrencyId(StringUtils.trim(rs.getString("currencyid")));	
				 		m_BillerDetail.setStatus(StringUtils.trim(rs.getString("status")));	
				 		arrBiller.add(m_BillerDetail);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrBiller!=null)
				 if(arrBiller.size()==0)
					 arrBiller=null;
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getRegisteredBillerDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getRegisteredBillerDetails  is  "+e.getMessage());			
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
				if(relationshipno!=null) relationshipno=null;
			}
		return arrBiller;
	}
	
	
	
	

	public boolean registerCustomerBiller(String relationshipno, String currencyId, String billerCode, String referenceNo) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;

		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 PPWalletEnvironment.setComment(3,className,"inside registerCustomerBiller billerCode is  "+billerCode);
			 								//		1			2			3			4			5		6	
			 query = "insert into biller_details (relationshipno, billercode, custrefno, currencyid, status, createdon) "
							+ "values (?, ?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5, 6	  
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, relationshipno); 				
					pstmt.setString(2, billerCode); 						
					pstmt.setString(3, referenceNo);				
					pstmt.setString(4, currencyId);  
					pstmt.setString(5, "A");
					pstmt.setString(6, Utilities.getMYSQLCurrentTimeStampForInsert() );
					
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}

					connection.commit();	result = true;
		}catch(Exception e){
			connection.rollback();
			result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method registerCustomerBiller  is  "+e.getMessage());
			throw new Exception ("The exception in method registerCustomerBiller  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}
	
	
	public ArrayList<BillerDetail> getRegisteredBillerDetails(String relationshipno) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<BillerDetail> arrBiller = null;
		
		try{
			connection = super.getConnection();	

				query = "select a.seqno seqno, a.billercode billercode, a.custrefno custrefno, a.currencyid currencyid, a.status status,  "
						+ "b.merchantname merchantname, b.companyname companyname, b.merchantid merchantid  from biller_details a, merch_details b where a.relationshipno=? and "
						+ "a.billercode=b.billercode";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipno);
			
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arrBiller = new ArrayList<BillerDetail>();
				 	while(rs.next()){	
				 		BillerDetail m_BillerDetail = new BillerDetail();
				 		m_BillerDetail.setSequenceNo(StringUtils.trim(rs.getString("seqno")));
				 		m_BillerDetail.setBillerCode(StringUtils.trim(rs.getString("billercode")));
				 		m_BillerDetail.setCustRefNo( StringUtils.trim(rs.getString("custrefno")));
				 		m_BillerDetail.setMerchantName(StringUtils.trim(rs.getString("merchantname")));
				 		m_BillerDetail.setMerchantId(StringUtils.trim(rs.getString("merchantid")));	
				 		m_BillerDetail.setCurrencyId(StringUtils.trim(rs.getString("currencyid")));
				 		m_BillerDetail.setCompanyName(StringUtils.trim(rs.getString("companyname")));
				 		m_BillerDetail.setStatus(StringUtils.trim(rs.getString("status")));	
				 		arrBiller.add(m_BillerDetail);
				 		} // end of while
					PPWalletEnvironment.setComment(3,className,"Total  arrBiller: in dao "+arrBiller.size()    );

				 	
				 	} //end of if rs!=null check
			 if(arrBiller!=null)
				 if(arrBiller.size()==0)
					 arrBiller=null;
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getRegisteredBillerDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getRegisteredBillerDetails  is  "+e.getMessage());			
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
		return arrBiller;
	}
	
	
	
	public boolean payBillWithWallet(String relationshipNo, String billpaymode, String custWalletId, String billerCode,
			String referenceNo, String currencyId, String billAmount, String billDescription) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		boolean result= false;
		String query = null;
		String transactionDatetime = null;
		String transactionCode = null;
		String systemReference = null;
		String customerTxnmode = "D";
		String billerTxnmode = "C";
		String billerWalletId = null;
		String billerCurrentBallance = null;
		float billerCreditAmount = 0;
		float businessCreditAmount = 0;
		
		try{
			 transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
			 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS"); 
			 transactionCode = (formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9));
			 //returnCode = transactionCode;
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
		//*****step 1 *****get the existing balance
				String custwalletBalance = null;
				 query = "select currbal from wallet_details where walletid=?  ";
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
	    if(Float.parseFloat(custwalletBalance) >= Float.parseFloat(billAmount)) { // THIS IS WRONG, THE CHECK HAS TO HAPPEN AT THE FRONT END 
			//******Step 2******Deduct the bill pay Amount from the wallet
			
				 float remainingCustomerbalance;
				 remainingCustomerbalance = Float.parseFloat(custwalletBalance) - Float.parseFloat(billAmount);
				 //get the wallet ballance of the maechant
			query = "select walletid merchwalletid, currbal currbal from wallet_details where relationshipno=? and usertype=?  ";
					
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, billerCode); 
					pstmt.setString(2, "M"); 
					rs = (ResultSet)pstmt.executeQuery();	
					 if(rs!=null){
					 	while(rs.next()){	 			 			
					 		billerWalletId = (StringUtils.trim(rs.getString("merchwalletid"))    );
					 		billerCurrentBallance = (StringUtils.trim(rs.getString("currbal"))    );
			        PPWalletEnvironment.setComment(3,className,"The biller wallet balance for id  " +billerWalletId  +"is "+ billerCurrentBallance );
									} // end of while
								} //end of if rs!=null check
					 if( pstmt!=null)		pstmt.close();				 
				 
				 
				  //*****Step 3*****Get the MSF Value for the Bill Pay
		          String msfFixed = null;
		          String msfVariable = null;
				   //get the MSF for the Bill Pay
				query = "select a.planid planid, a.plan_fee_fixed plan_fee_fixed, a.plan_fee_var plan_fee_var  " + 
						"   from merch_sys_msf_plan a, merch_msf_plan_relation b where a.plan_type=? and   a.planid=b.planid and b.billercode=? and b.status=?";
							
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, "B"); // plan type is B = Bill Pay
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
					billerCreditAmount = Float.parseFloat(billAmount) - Float.parseFloat(msfFixed);
					businessCreditAmount = Float.parseFloat(billAmount) - billerCreditAmount;
			        PPWalletEnvironment.setComment(3,className,"The billerCreditAmount is  " +billerCreditAmount  +" and businessCreditAmount is "+ businessCreditAmount + " for msfFixed ");

				}else {
					billerCreditAmount = Float.parseFloat(billAmount) - (Float.parseFloat(msfVariable))*(Float.parseFloat(billAmount));
					businessCreditAmount = Float.parseFloat(billAmount) - billerCreditAmount;		
			        PPWalletEnvironment.setComment(3,className,"The billerCreditAmount is  " +billerCreditAmount  + " and businessCreditAmount is "+ businessCreditAmount + " for msfVariable ");

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
					pstmt.setFloat(1, (     Float.parseFloat(billerCurrentBallance) + billerCreditAmount) );
					pstmt.setString(2, transactionDatetime); 
					pstmt.setString(3, billerWalletId); 
						try {
				    pstmt.executeUpdate();
						}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
						  	}		
						if( pstmt!=null)	pstmt.close();					
					
						
				PPWalletEnvironment.setComment(3,className,"update merchant wallet id "+ billerWalletId+" with  amount  " + (     Float.parseFloat(billerCurrentBallance) + billerCreditAmount) );
	
					
          // Step 4.3 insert into customer wallet ledger of DB
					systemReference =  PPWalletEnvironment.getCodeWalletToMerchantBillPay() +"-"+ transactionCode+"-" ;
		                                        // 1		2			3			4			5				6		7
			query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
											+ "values (?, ?, ?, ?, ?, ?,  ?) ";
													// 1  2  3  4  5  6   7	
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, transactionCode); 			
				pstmt.setString(2, custWalletId); 				
				pstmt.setString(3, systemReference +"C");							
				pstmt.setFloat(4, Float.parseFloat(billAmount));  
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
				+"systemreference is  "+systemReference +"C" +"  Bill pay amount is "+billAmount + " currencyId is "+ currencyId+ " transactionDatetime  "+ transactionDatetime);

				
				// ************ Step 4.4 insert into the merchant wallet ledger
				query = "insert into wallet_txn_merch_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
						+ "values (?, ?, ?, ?, ?, ?,  ?) ";
								// 1  2  3  4  5  6   7	
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, transactionCode); 			
					pstmt.setString(2, billerWalletId); 				
					pstmt.setString(3, systemReference +"M");							
					pstmt.setFloat(4, billerCreditAmount);  
					pstmt.setString(5, currencyId);
					pstmt.setString(6, billerTxnmode); // Debit 
					pstmt.setString(7, transactionDatetime);
				  	try {
						pstmt.executeUpdate();
							}catch(Exception e) {
										throw new Exception (" failed query "+query);
										}					
				  	if( pstmt!=null)				pstmt.close();	
	
					PPWalletEnvironment.setComment(3,className,"Executed customer wallet_tnx_bc amount "+" Transactioncode is"+ transactionCode +"merchantWalletId is  "+billerWalletId 
					+"systemreference is  " +systemReference +"M" +"  Bill pay amount is  "+billAmount +"currencyId is  "+ currencyId+ "transactionDatetime"+ transactionDatetime);

					// ************ Step 4.5 insert into the business wallet ledger
				//												   1		2			3			4				5				6			7			8		   9
					query = "insert into wallet_txn_business_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
							+ "values (?, ?, ?, ?, ?, ?, ?,?,?) ";
									// 1  2  3  4  5  6  7 8 9
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, transactionCode); 
						pstmt.setString(2, PPWalletEnvironment.getCodeWalletToMerchantBillPay());
						pstmt.setString(3, custWalletId);
						pstmt.setString(4, billerWalletId);
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
					  	PPWalletEnvironment.setComment(3,className,"Executed customer wallet_txn_business_bc amount "+" Transactioncode is"+ transactionCode  +" customerWalletId is" + custWalletId + " merchantWalletId is "+billerWalletId 
								+"systemreference is  " +systemReference +"M" +" businessCreditAmount  is  "+businessCreditAmount +"currencyId is  "+ currencyId+ "transactionDatetime"+ transactionDatetime);

					
	//***********************Step 5Record biller transaction in biller ledger *************************
					                     	      //	1		 2			3		4			5				6			7			8		  9			      
				 query = "insert into billpay_txn_bc (txncode, paytype, assetid, custrelno, billercode, custreference, tnmamount, txncurrencyid, txndatetime) "
								+ "values (?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
								//		   1  2  3  4  5  6  7  8  9 
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, transactionCode); 						 
						pstmt.setString(2, "W"); 						 
						pstmt.setString(3, custWalletId);							 
						pstmt.setString(4, relationshipNo);  
						pstmt.setString(5, billerCode);
						pstmt.setString(6, systemReference +"C");
						pstmt.setFloat(7, Float.parseFloat(billAmount));  
						pstmt.setString(8, currencyId);
						pstmt.setString(9, transactionDatetime);
						
						try {
							pstmt.executeUpdate();
							}catch(Exception e) {
								throw new Exception (" failed query "+query+" "+e.getMessage());
							}
			
						pstmt.close();
						//commit all transaction
						PPWalletEnvironment.setComment(3,className,"Now commiting all Transactions" );
						
						
						
						 // Step 6.1: Get the Loyalty Rules based on the type of transaction and conversion
						String pointsConversion = null;
						String previousPointsBalance = null;
						boolean firstime = false;
						String pointsaccrued =null;
						 
						 query = "select pointsconversion from loyalty_rules where paymode=? and status = ? ";
							
							pstmt = connection.prepareStatement(query);
							pstmt.setString(1, PPWalletEnvironment.getCodeWalletToMerchantBillPay());
							pstmt.setString(2, "A");
							rs = (ResultSet)pstmt.executeQuery();
							 if(rs!=null){
								 	while(rs.next()){	 			 			
								 		pointsConversion = (StringUtils.trim(rs.getString("pointsconversion"))  );
								 		} // end of while
								 	} //end of if rs!=null check
							 pstmt.close();
							 if(pointsConversion!=null) {
								 
								 query = "select pointbalance balance from loyalty_points_bc where relationshipno=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where relationshipno = ? ) ";
									pstmt = connection.prepareStatement(query);
									pstmt.setString(1, relationshipNo);
									pstmt.setString(2, relationshipNo);
									rs = (ResultSet)pstmt.executeQuery();
									 if(rs!=null){
										 	while(rs.next()){	 			 			
										 		previousPointsBalance = (StringUtils.trim(rs.getString("balance"))  );
										 		} // end of while
										 	} //end of if rs!=null check
									 pstmt.close();
								PPWalletEnvironment.setComment(3,className,"Points Balance for All Wallet is   " + " is " + previousPointsBalance );
								 pointsaccrued = Float.toString((Float.parseFloat(billAmount) * 1));

									PPWalletEnvironment.setComment(3,className,"pointsaccrued   "+ pointsaccrued );

								
								  if(previousPointsBalance==null) { // For the first time 
									  firstime =true;
								  PPWalletEnvironment.setComment(3,	className,"***LOYALTY FIRST TIME***No previous record present for user: " +custWalletId); 
								 
								  }else {
									  PPWalletEnvironment.setComment(3,	className," Previous Point balance for user : " +custWalletId+ " is  " +previousPointsBalance); 
								  }	 

									//***** Step 6.2****: Insert loyalty points for the sender user		
							 // systemreference = (walletId)+( RandomStringUtils.random(10, false, true)).toString(); 
										                             //	  	1		2				3			4			5			6				7        8  
								query = "insert into loyalty_points_bc (walletid, relationshipno, usertype, paymode, txnreference, pointaccrued, pointbalance, txndatetime) "
											+ "values (?, ?, ?, ?, ?, ?, ?, ?) ";
											//		   1  2  3  4  5  6  7  8
										pstmt = connection.prepareStatement(query);
										pstmt.setString(1, (custWalletId)); 	
										pstmt.setString(2, relationshipNo);
										pstmt.setString(3, "C"); 						// 
										pstmt.setString(4, PPWalletEnvironment.getCodeWalletToMerchantBillPay());	
										pstmt.setString(5,systemReference +"C" ); // transaction reference generated by the system
										pstmt.setFloat(6, Float.parseFloat(pointsaccrued));  // pointaccrued
										if(firstime)
										pstmt.setFloat(7, Float.parseFloat(pointsaccrued));  // pointaccrued
										else {
											pstmt.setFloat(7, Float.parseFloat(pointsaccrued) + Float.parseFloat(previousPointsBalance )    );  // pointbalance
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

						}else {
							throw new Exception ("Bill amount greater than balance, hence transaction not allowed");
						}
									
	    }catch(Exception e){
	    	transactionCode = null;
			connection.rollback();
			
			PPWalletEnvironment.setComment(1,className,"The exception in method payBillWithWallet  is  "+e.getMessage());
			throw new Exception ("The exception in method payBillWithWallet  is  "+e.getMessage());
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
				}
			if(result) {
				
				 /// *****Step 7***** call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet

				WalletDao.insertGenericWalletTransactions(transactionCode, billerWalletId, systemReference +"M", String.valueOf(billerCreditAmount), currencyId, billerTxnmode, transactionDatetime  );
				WalletDao.insertGenericWalletTransactions(transactionCode, custWalletId, systemReference +"C", billAmount, currencyId, customerTxnmode, transactionDatetime  );
				
			}
			
				if(pstmt!=null) pstmt.close(); 	if(systemReference!=null);  systemReference=null;	if(customerTxnmode!=null);  customerTxnmode=null;	
				if(billerTxnmode!=null);  billerTxnmode=null; if(custWalletId!=null);  custWalletId=null;	if(billerWalletId!=null);  billerWalletId=null;	if(billerCurrentBallance!=null);  billerCurrentBallance=null;
				if(businessCreditAmount!=0);  businessCreditAmount=0; if(businessCreditAmount!=0);  businessCreditAmount=0; if(billAmount!=null) billAmount=null; 
				if(transactionDatetime!=null);  transactionDatetime=null; if(transactionCode!=null);  transactionCode=null; if(currencyId!=null) currencyId=null;
			
			}
			return result;		 
	}
			
	public ArrayList<BillPayTransaction> getBillPayTransactionsForUser(String relationshipno) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<BillPayTransaction> arrBillPayTransaction = null;
		
		try{
			connection = super.getConnection();	

				query = "select a.txncode txncode, a.paytype paytype, a.assetid  assetid, a.billercode billercode, a.custreference custreference, "
						+ " a.tnmamount tnmamount, a.txncurrencyid txncurrencyid, a.txndatetime txndatetime, b.companyname companyname from "
						+ " billpay_txn_bc a, merch_details b  where a.custrelno=? and  a.billercode=b.billercode order by txndatetime desc ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipno);
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arrBillPayTransaction = new ArrayList<BillPayTransaction>();
				 	while(rs.next()){	
				 		BillPayTransaction m_BillPayTransaction = new BillPayTransaction();
				 		m_BillPayTransaction.setTxnCode(StringUtils.trim(rs.getString("txncode")));
				 		m_BillPayTransaction.setPayType(StringUtils.trim(rs.getString("paytype")));
				 		m_BillPayTransaction.setAssetId( StringUtils.trim(rs.getString("assetid")));
				 		m_BillPayTransaction.setBillerCode(StringUtils.trim(rs.getString("billercode")));	
				 		m_BillPayTransaction.setReferenceNo(StringUtils.trim(rs.getString("custreference")));	
				 		m_BillPayTransaction.setMerchantCompany(StringUtils.trim(rs.getString("companyname")));	
				 		m_BillPayTransaction.setCurrencyId(StringUtils.trim(rs.getString("txncurrencyid")));	
				 		m_BillPayTransaction.setBillAmount(StringUtils.trim(rs.getString("tnmamount")));	
				 		m_BillPayTransaction.setTxnDateTime(StringUtils.trim(rs.getString("txndatetime")));	
				 		arrBillPayTransaction.add(m_BillPayTransaction);

				 		} // end of while
				 	} //end of if rs!=null check
			 if(arrBillPayTransaction!=null) if(arrBillPayTransaction.size()==0) arrBillPayTransaction=null;
			
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
			}
		return arrBillPayTransaction;
	}

	public ArrayList<Merchant> getBillerDetails(String relationshipno)throws Exception {
		// TODO Auto-generated method stub
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Merchant> billerDetailsArray = null;
		
		try {
			connection = super.getConnection();	
			
			query="select a.merchantname merchantname, a.merchantemail merchantemail, a.merchantcontact merchantcontact, a.address1 address1, a.city city, a.companyname companyname from merch_details a, biller_details " + 
					"where a.billercode = b.billercode  and b.relationshipno=?";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipno);
			rs = (ResultSet)pstmt.executeQuery();
			
			
			if(rs!=null){
				billerDetailsArray = new ArrayList<Merchant>();
				 	while(rs.next()){	
				 		
				 		
				 		Merchant m_Merchant = new Merchant();
				 		m_Merchant.setMerchantName(StringUtils.trim(rs.getString("merchantname")));
				 		m_Merchant.setEmail(StringUtils.trim(rs.getString("merchantemail")));
				 		m_Merchant.setContact(StringUtils.trim(rs.getString("merchantcontact")));
				 		m_Merchant.setAddress1(StringUtils.trim(rs.getString("address1")));
				 		m_Merchant.setCity(StringUtils.trim(rs.getString("city")));
				 		m_Merchant.setCompanyName(StringUtils.trim(rs.getString("companyname")));
				 		billerDetailsArray.add(m_Merchant);

				 		} // end of while
				 	} 
			 if(billerDetailsArray!=null) if(billerDetailsArray.size()==0) billerDetailsArray=null;		
			 
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getBillerDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getBillerDetails  is  "+e.getMessage());			
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

		return billerDetailsArray;
	}
	
	
	public boolean payBillWithToken(String relationshipNo, String billPayMode, String billerCode,
			String referenceNo, String currencyId, String billAmount, String billDescription, String tokenId, String cardAlias, String cardDateOfExpiry, String cvv2) throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			boolean result= false;
			String query = null;
			String transactionDatetime = null;
			String systemReference = null;
			String customerTxnmode = "D";
			String billerTxnmode = "C";
			String billerWalletId = null;
			String billerCurrentBallance = null;
			float billerCreditAmount = 0;
			float businessCreditAmount = 0;
			String transactionCode = null;
			boolean success = true;
			String custWalletId=null;
			//String responseCode = null;   //From the CARD ISSUER

			
			try{
				 transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
				 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS"); 
				 transactionCode = (formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9));
				 //returnCode = transactionCode;
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
				 
				 // Send the card details and the billpay Amount and wait for the respond
				 // IF RESPONSE IS SUCCES THEN PROCEED
				 //success shall be returned
				 //
				 //
				 //
				 
			//*****step 1 *****get the existing balance
					
					// **********get the wallet balance
				
				 String custwalletBalance = null;
					query = "select  walletid, currbal from wallet_details   where relationshipno = ? order by currbal desc  limit 1";
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, relationshipNo);
					rs = (ResultSet) pstmt.executeQuery();
					if (rs != null) {
						while (rs.next()) {
							custwalletBalance = (StringUtils.trim(rs.getString("currbal")));
							custWalletId = (StringUtils.trim(rs.getString("walletid")));
							PPWalletEnvironment.setComment(3, className, "The Wallet balance  is " + custwalletBalance);
						
						} // end of while
					} // end of if rs!=null check
					pstmt.close();
					 if( rs!=null)	rs.close();
							
			  //********check if wallet has sufficient funds to pay the bill		
		    if(success ) {  // meaning the payment has gone successfull and the Customer card has been debited and ceredited to the Business account
		                	
		    	
				//******Step 2******Deduct the bill pay Amount from the Token    // For the internal CARDS  BUT THE EXTERNAL WILL WAIT FOR A RESPONSE
				
		 
					 //get the wallet ballance of the maechant
				query = "select walletid merchwalletid, currbal currbal from wallet_details where relationshipno=? and usertype=?  ";
						
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, billerCode); 
						pstmt.setString(2, "M"); 
						rs = (ResultSet)pstmt.executeQuery();	
						 if(rs!=null){
						 	while(rs.next()){	 			 			
						 		billerWalletId = (StringUtils.trim(rs.getString("merchwalletid"))    );
						 		billerCurrentBallance = (StringUtils.trim(rs.getString("currbal"))    );
				        PPWalletEnvironment.setComment(3,className,"The biller wallet balance for id  " +billerWalletId  +"is "+ billerCurrentBallance );
										} // end of while
									} //end of if rs!=null check
						 if( pstmt!=null)		pstmt.close();				 
					 
					 
					  //*****Step 3*****Get the MSF Value for the Bill Pay
			          String msfFixed = null;
			          String msfVariable = null;
					   //get the MSF for the Bill Pay
					query = "select a.planid planid, a.plan_fee_fixed plan_fee_fixed, a.plan_fee_var plan_fee_var  " + 
							"   from merch_sys_msf_plan a, merch_msf_plan_relation b where a.plan_type=? and   a.planid=b.planid and b.billercode=? and b.status=?";
								
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, "B"); // plan type is B = Bill Pay
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
						billerCreditAmount = Float.parseFloat(billAmount) - Float.parseFloat(msfFixed);
						businessCreditAmount = Float.parseFloat(billAmount) - billerCreditAmount;
				        PPWalletEnvironment.setComment(3,className,"The billerCreditAmount is  " +billerCreditAmount  +" and businessCreditAmount is "+ businessCreditAmount + " for msfFixed ");

					}else {
						billerCreditAmount = Float.parseFloat(billAmount) - (Float.parseFloat(msfVariable))*(Float.parseFloat(billAmount));
						businessCreditAmount = Float.parseFloat(billAmount) - billerCreditAmount;		
				        PPWalletEnvironment.setComment(3,className,"The billerCreditAmount is  " +billerCreditAmount  + " and businessCreditAmount is "+ businessCreditAmount + " for msfVariable ");

					}

						//****Step 4.2: Update the merchant wallet details balance
						query = " update wallet_details set currbal= ?, lastupdated = ? where  walletid=? ";
						pstmt = connection.prepareStatement(query);
						pstmt.setFloat(1, (     Float.parseFloat(billerCurrentBallance) + billerCreditAmount) );
						pstmt.setString(2, transactionDatetime); 
						pstmt.setString(3, billerWalletId); 
							try {
					    pstmt.executeUpdate();
							}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
							  	}		
							if( pstmt!=null)	pstmt.close();					
						
							
					PPWalletEnvironment.setComment(3,className,"update merchant wallet id "+ billerWalletId+" with  amount  " + (     Float.parseFloat(billerCurrentBallance) + billerCreditAmount) );
		
						
	          // Step 4.3 insert into customer wallet ledger of DB
						systemReference =  PPWalletEnvironment.getCodeTokenToMerchantBillPay() +"-"+ transactionCode+"-" ;
		
						
					// ************ Step 4.4 insert into the merchant wallet ledger
					query = "insert into wallet_txn_merch_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
							+ "values (?, ?, ?, ?, ?, ?,  ?) ";
									// 1  2  3  4  5  6   7	
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, transactionCode); 			
						pstmt.setString(2, billerWalletId); 				
						pstmt.setString(3, systemReference +"M");							
						pstmt.setFloat(4, billerCreditAmount);  
						pstmt.setString(5, currencyId);
						pstmt.setString(6, billerTxnmode); // Debit 
						pstmt.setString(7, transactionDatetime);
					  	try {
							pstmt.executeUpdate();
								}catch(Exception e) {
											throw new Exception (" failed query "+query);
											}					
					  	if( pstmt!=null)				pstmt.close();	
		
						PPWalletEnvironment.setComment(3,className,"Executed customer wallet_tnx_bc amount "+" Transactioncode is"+ transactionCode +"merchantWalletId is  "+billerWalletId 
						+"systemreference is  " +systemReference +"M" +"  Bill pay amount is  "+billAmount +"currencyId is  "+ currencyId+ "transactionDatetime"+ transactionDatetime);

						// ************ Step 4.5 insert into the business wallet ledger
					//												   1		2			3			4				5				6			7			8		   9
						query = "insert into wallet_txn_business_bc (txncode, paytype, custwalletid, merchwalletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime) "
								+ "values (?, ?, ?, ?, ?, ?, ?,?,?) ";
										// 1  2  3  4  5  6  7 8 9
							pstmt = connection.prepareStatement(query);
							pstmt.setString(1, transactionCode); 
							pstmt.setString(2, PPWalletEnvironment.getCodeTokenToMerchantBillPay());
							pstmt.setString(3, custWalletId);
							pstmt.setString(4, billerWalletId);
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
						  	PPWalletEnvironment.setComment(3,className,"Executed customer wallet_txn_business_bc amount "+" Transactioncode is"+ transactionCode  +" customerWalletId is" + custWalletId + " merchantWalletId is "+billerWalletId 
									+"systemreference is  " +systemReference +"M" +" businessCreditAmount  is  "+businessCreditAmount +"currencyId is  "+ currencyId+ "transactionDatetime"+ transactionDatetime);

						
		//***********************Step 5Record biller transaction in biller ledger *************************
						                     	      //	1		 2			3		4			5				6			7			8		  9			      
					 query = "insert into billpay_txn_bc (txncode, paytype, assetid, custrelno, billercode, custreference, tnmamount, txncurrencyid, txndatetime) "
									+ "values (?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
									//		   1  2  3  4  5  6  7  8  9 
							pstmt = connection.prepareStatement(query);
							pstmt.setString(1, transactionCode); 						 
							pstmt.setString(2, "T"); 						 
							pstmt.setString(3, custWalletId);							 
							pstmt.setString(4, relationshipNo);  
							pstmt.setString(5, billerCode);
							pstmt.setString(6, systemReference +"C");
							pstmt.setFloat(7, Float.parseFloat(billAmount));  
							pstmt.setString(8, currencyId);
							pstmt.setString(9, transactionDatetime);
							
							try {
								pstmt.executeUpdate();
								}catch(Exception e) {
									throw new Exception (" failed query "+query+" "+e.getMessage());
								}
				
							pstmt.close();
							//commit all transaction
							PPWalletEnvironment.setComment(3,className,"Now commiting all Transactions" );
							
							 // Step 6.1: Get the Loyalty Rules based on the type of transaction and conversion
							String pointsConversion = null;
							String previousPointsBalance = null;
							boolean firstime = false;
							String pointsaccrued =null;
							 
							 query = "select pointsconversion from loyalty_rules where paymode=? and status = ? ";
								
								pstmt = connection.prepareStatement(query);
								pstmt.setString(1, PPWalletEnvironment.getCodeTokenToMerchantBillPay());
								pstmt.setString(2, "A");
								rs = (ResultSet)pstmt.executeQuery();
								 if(rs!=null){
									 	while(rs.next()){	 			 			
									 		pointsConversion = (StringUtils.trim(rs.getString("pointsconversion"))  );
									 		} // end of while
									 	} //end of if rs!=null check
								 pstmt.close();
								 if(pointsConversion!=null) {
									 
									 query = "select pointbalance balance from loyalty_points_bc where relationshipno=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where relationshipno = ? ) ";
										pstmt = connection.prepareStatement(query);
										pstmt.setString(1, relationshipNo);
										pstmt.setString(2, relationshipNo);
										rs = (ResultSet)pstmt.executeQuery();
										 if(rs!=null){
											 	while(rs.next()){	 			 			
											 		previousPointsBalance = (StringUtils.trim(rs.getString("balance"))  );
											 		} // end of while
											 	} //end of if rs!=null check
										 pstmt.close();
									PPWalletEnvironment.setComment(3,className,"Points Balance for All Wallet is   " + " is " + previousPointsBalance );
									 pointsaccrued = Float.toString((Float.parseFloat(billAmount) * 1));

										PPWalletEnvironment.setComment(3,className,"pointsaccrued   "+ pointsaccrued );

									
									  if(previousPointsBalance==null) { // For the first time 
										  firstime =true;
									  PPWalletEnvironment.setComment(3,	className,"***LOYALTY FIRST TIME***No previous record present for user: " +custWalletId); 
									 
									  }else {
										  PPWalletEnvironment.setComment(3,	className," Previous Point balance for user : " +custWalletId+ " is  " +previousPointsBalance); 
									  }	 

										//***** Step 6.2****: Insert loyalty points for the sender user		
								 // systemreference = (walletId)+( RandomStringUtils.random(10, false, true)).toString(); 
											                             //	  	1		2				3			4			5			6				7        8  
									query = "insert into loyalty_points_bc (walletid, relationshipno, usertype, paymode, txnreference, pointaccrued, pointbalance, txndatetime) "
												+ "values (?, ?, ?, ?, ?, ?, ?, ?) ";
												//		   1  2  3  4  5  6  7  8
											pstmt = connection.prepareStatement(query);
											pstmt.setString(1, (custWalletId)); 	
											pstmt.setString(2, relationshipNo);
											pstmt.setString(3, "C"); 						// 
											pstmt.setString(4, PPWalletEnvironment.getCodeTokenToMerchantBillPay());	
											pstmt.setString(5,systemReference +"C" ); // transaction reference generated by the system
											pstmt.setFloat(6, Float.parseFloat(pointsaccrued));  // pointaccrued
											if(firstime)
											pstmt.setFloat(7, Float.parseFloat(pointsaccrued));  // pointaccrued
											else {
												pstmt.setFloat(7, Float.parseFloat(pointsaccrued) + Float.parseFloat(previousPointsBalance )    );  // pointbalance
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

							}else {
								throw new Exception ("Bill unsuccessful... Please Check you card ballance");
							}
										
		    }catch(Exception e){
		    	transactionCode = null;
				connection.rollback();
				
				PPWalletEnvironment.setComment(1,className,"The exception in method payBillWithWallet  is  "+e.getMessage());
				throw new Exception ("The exception in method payBillWithWallet  is  "+e.getMessage());
			}finally{
				if(connection!=null)
					try {
						super.close();
					} catch (SQLException e) {
						PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
					}
				if(result) {
					
					 /// ***Step 7*** call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet

					WalletDao.insertGenericWalletTransactions(transactionCode, billerWalletId, systemReference +"M", String.valueOf(billerCreditAmount), currencyId, billerTxnmode, transactionDatetime  );
					WalletDao.insertGenericWalletTransactions(transactionCode, custWalletId, systemReference +"C", billAmount, currencyId, customerTxnmode, transactionDatetime  );
					
				}
				
					if(pstmt!=null) pstmt.close(); 	if(systemReference!=null);  systemReference=null;	if(customerTxnmode!=null);  customerTxnmode=null;	
					if(billerTxnmode!=null);  billerTxnmode=null; if(custWalletId!=null);  custWalletId=null;	if(billerWalletId!=null);  billerWalletId=null;	if(billerCurrentBallance!=null);  billerCurrentBallance=null;
					if(businessCreditAmount!=0);  businessCreditAmount=0; if(businessCreditAmount!=0);  businessCreditAmount=0; if(billAmount!=null) billAmount=null; 
					if(transactionDatetime!=null);  transactionDatetime=null; if(transactionCode!=null);  transactionCode=null; if(currencyId!=null) currencyId=null;
				
				}
				return result;		 
		}
}

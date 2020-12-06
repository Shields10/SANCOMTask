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
import com.ppwallet.model.Loyalty;
import com.ppwallet.model.LoyaltyRules;
import com.ppwallet.utilities.Utilities;

public class LoyaltyDao extends HandleConnections {
     /**
	 * 
	 */
private static final long serialVersionUID = 1L;
private static String className = LoyaltyDao.class.getSimpleName();

public ArrayList<LoyaltyRules> getLoyaltyRules() throws Exception {
		
	PreparedStatement pstmt=null;
	Connection connection = null;
	ResultSet rs=null;
	String query = null;
	ArrayList<LoyaltyRules> arrLoyaltyRules = null;		
	try {
		connection = super.getConnection();
		query = "select paymode, rulesdesc, pointsconversion, cryptoconversion, status from loyalty_rules where usertype=?  ";

		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, "C"); // Later we can allocate rules for specific users, such as vip users
		rs = (ResultSet)pstmt.executeQuery();
		 if(rs!=null){
			 arrLoyaltyRules = new ArrayList<LoyaltyRules>();
			 
			 	while(rs.next()){	 
					LoyaltyRules m_LoyaltyRules=new LoyaltyRules();					
			 		m_LoyaltyRules.setPayMode( StringUtils.trim(rs.getString("paymode"))    );
			 		m_LoyaltyRules.setRuleDesc( StringUtils.trim(rs.getString("rulesdesc"))  );
			 		m_LoyaltyRules.setPointsConvertRatio( StringUtils.trim(rs.getString("pointsconversion")  ));
			 		m_LoyaltyRules.setCryptoConvertRatio(StringUtils.trim(rs.getString("cryptoconversion")  ));
					arrLoyaltyRules.add(m_LoyaltyRules);
			 		} // end of while
			 	
			 	} //end of if rs!=null check
		 if(arrLoyaltyRules!=null)
			 if(arrLoyaltyRules.size()==0)
				 arrLoyaltyRules=null;
	
	}catch(Exception e) {
		PPWalletEnvironment.setComment(1,className,"The exception in method getLoyaltyRules  is  "+e.getMessage());
		throw new Exception ("The exception in method getLoyaltyRules  is  "+e.getMessage());			
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
	return arrLoyaltyRules;
	}

public ArrayList<Loyalty> getLoyaltyTransactionsForUser(String relationshipNo) throws Exception {
	PreparedStatement pstmt=null;
	Connection connection = null;
	ResultSet rs=null;
	String query = null;
	ArrayList<Loyalty> arrLoyalty = null;

	try {
		connection = super.getConnection();	
		 
		query = "select sequenceid, walletid, paymode, txnreference, pointaccrued, status, pointbalance, txndatetime from loyalty_points_bc where walletid in"
				+ " (select walletid from wallet_details where relationshipno= ?) order by txndatetime desc limit 0, 1000 ";

		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, relationshipNo);
		rs = (ResultSet)pstmt.executeQuery();

		 if(rs!=null){
			 arrLoyalty = new ArrayList<Loyalty>();
			 
			 	while(rs.next()){	 
				Loyalty m_Loyalty=new Loyalty();					
			 		m_Loyalty.setSequenceId(  StringUtils.trim(rs.getString("sequenceid")) );
			 		m_Loyalty.setWalletId( StringUtils.trim(rs.getString("walletid")) );
			 		m_Loyalty.setPayMode( StringUtils.trim(rs.getString("paymode")) );
			 		m_Loyalty.setTxnReference ( StringUtils.trim(rs.getString("txnreference")) );
			 		m_Loyalty.setPointAccrued( StringUtils.trim(rs.getString("pointaccrued")) );
			 		m_Loyalty.setPointBalance( StringUtils.trim(rs.getString("pointbalance")) );
			 		m_Loyalty.setTxnDate( StringUtils.trim(rs.getString("txndatetime")) );
			 		m_Loyalty.setStatus( StringUtils.trim(rs.getString("status")) );
			 		arrLoyalty.add(m_Loyalty);	
			 		}
			 	
			 	} 
		 if(arrLoyalty!=null)
			 if(arrLoyalty.size()==0)
				 arrLoyalty=null;
		
	}catch(Exception e) {
		PPWalletEnvironment.setComment(1,className,"The exception in method getLoyaltyTransactionsForUser  is  "+e.getMessage());
		throw new Exception ("The exception in method getLoyaltyTransactionsForUser  is  "+e.getMessage());			
		
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
	return arrLoyalty;
   }


public ArrayList<Loyalty> getUnclaimedLoyaltyTransactionsForUser(String relationshipNo) throws Exception {
	PreparedStatement pstmt=null;
	Connection connection = null;
	ResultSet rs=null;
	String query = null;
	ArrayList<Loyalty> arrLoyalty = null;

	try {
		connection = super.getConnection();	
		 
		query = "select sequenceid, walletid, paymode, txnreference, pointaccrued, pointbalance, txndatetime from loyalty_points_bc where walletid in (select walletid from wallet_details where relationshipno= ?) and status = ? order by txndatetime desc limit 0, 1000 ";

		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, relationshipNo);
		pstmt.setString(2, "U");
		rs = (ResultSet)pstmt.executeQuery();

		 if(rs!=null){
			 arrLoyalty = new ArrayList<Loyalty>();
			 
			 	while(rs.next()){	 
				Loyalty m_Loyalty=new Loyalty();					
			 		m_Loyalty.setSequenceId(  StringUtils.trim(rs.getString("sequenceid")) );
			 		m_Loyalty.setWalletId( StringUtils.trim(rs.getString("walletid")) );
			 		m_Loyalty.setPayMode( StringUtils.trim(rs.getString("paymode")) );
			 		m_Loyalty.setTxnReference ( StringUtils.trim(rs.getString("txnreference")) );
			 		m_Loyalty.setPointAccrued( StringUtils.trim(rs.getString("pointaccrued")) );
			 		m_Loyalty.setPointBalance( StringUtils.trim(rs.getString("pointbalance")) );
			 		m_Loyalty.setTxnDate( StringUtils.trim(rs.getString("txndatetime")) );
			 		arrLoyalty.add(m_Loyalty);	
			 		}
			 	
			 	} 
		 if(arrLoyalty!=null)
			 if(arrLoyalty.size()==0)
				 arrLoyalty=null;
		
	}catch(Exception e) {
		PPWalletEnvironment.setComment(1,className,"The exception in method getLoyaltyTransactionsForUser  is  "+e.getMessage());
		throw new Exception ("The exception in method getLoyaltyTransactionsForUser  is  "+e.getMessage());			
		
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
	return arrLoyalty;
   }

/*=============this method handles individual point redemption===================*/

public boolean updateLoyaltyRewardsForAPoint (String relationshipNo, String seqno, String claimedWalletId, String pointAccrued, String payMode, String walletBalance, String txnRef  ) throws Exception {
	PreparedStatement pstmt=null;
	Connection connection = null;
	boolean result= false;
	ResultSet rs=null;
	String query = null;
	String transactionDatetime = null;
	String transactionCode =  null;
	String totalLoyaltyBalance = null;
	String systemReference =null;
	float cashValueAfterConversion =  0;
	try {
		
		transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
		 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");    	       	 		 	
		 transactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
		 connection = super.getConnection();	
		 connection.setAutoCommit(false);
			
		float pointConversion	 = 0;
		 
			query = "select pointsconversion from loyalty_rules where paymode=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, payMode);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	 			 			
					pointConversion = Float.parseFloat( StringUtils.trim(rs.getString("pointsconversion"))  );
					} // end of while
				PPWalletEnvironment.setComment(3,className,"step 1   pointConversion is  "+pointConversion   );
			} 
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();		
			

			query = "select pointbalance from loyalty_points_bc where sequenceid  = (select max(sequenceid) from loyalty_points_bc where relationshipno = ?) ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	 			 			
					totalLoyaltyBalance = ( StringUtils.trim(rs.getString("pointbalance"))  );
					} // end of while
				PPWalletEnvironment.setComment(3,className,"step 1   totalLoyaltyBalance is  "+totalLoyaltyBalance   );
			} 
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();			
			
			//*** Calculate the cash value
			
			cashValueAfterConversion = pointConversion * Float.parseFloat(pointAccrued);
			
			//**** Update the wallet of the customer
			
			query = " update wallet_details set currbal= ?, lastupdated = ? where  walletid=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setFloat(1, Float.parseFloat(walletBalance) + cashValueAfterConversion );
			pstmt.setString(2, transactionDatetime); 
			pstmt.setString(3, claimedWalletId); 
				try {
				PPWalletEnvironment.setComment(3,className,"update  wallet with  amount" + Float.parseFloat(walletBalance) + cashValueAfterConversion );

				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
			  	}					//connection.commit();
				if(pstmt!=null) pstmt.close();
				
				
				
				//update previous transaction from U to C
				
				query = "update loyalty_points_bc set status = 'C' where txnreference = ?";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, txnRef);
				try {
					pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception("failed to update individual loyalty point transactions ");
				}
				pstmt.close();
				
				
				
				
				systemReference =  PPWalletEnvironment.getCodeLoyaltyRedeemRate() +"-"+ transactionCode+"-" ;
			//											1		2			3			4			5				6		7
				 query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime)  "
							+ " values (?, ?, ?, ?, ?, ?, ? )  ";
							//		    1  2  3  4  5  6  7  
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, transactionCode); 					// tokenid
					pstmt.setString(2, claimedWalletId); 						// userid
					pstmt.setString(3,systemReference + "C" );							// usertype
					pstmt.setFloat(4,  (cashValueAfterConversion));  // 
					pstmt.setString(5, "404");
					pstmt.setString(6, "C");                       // Credit as we are topping up
					pstmt.setString(7,  transactionDatetime);                       // Credit as we are topping up
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}					
					pstmt.close();	 
				//  Insert loyalty points for the sender user  
				 								//		1		2			3			   4			5			6				7          8       9
			 query = "insert into loyalty_points_bc (walletid, usertype, relationshipno, paymode, txnreference, pointaccrued, pointbalance, status,  txndatetime) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5  6  7  8  9';
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, (claimedWalletId)); 					
					pstmt.setString(2, "C"); 		
					pstmt.setString(3, relationshipNo); // 
					pstmt.setString(4,payMode );	
					pstmt.setString(5, transactionCode); // transaction reference generated by the system
					pstmt.setFloat(6, -Float.parseFloat(pointAccrued) );  // pointaccrued
					
					pstmt.setFloat(7,  Float.parseFloat(totalLoyaltyBalance ) -  Float.parseFloat(pointAccrued)     );  // pointbalance
					pstmt.setString(8,"C" );	
					pstmt.setString(9,transactionDatetime );	

						try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
					}	
			
					connection.commit();
					result = true;
	}catch(Exception e) {
		result = false;
		connection.rollback();
		PPWalletEnvironment.setComment(1,className,"The exception in method updateLoyaltyRewardsForAPoint  is  "+e.getMessage());
		throw new Exception ("The exception in method updateLoyaltyRewardsForAPoint  is  "+e.getMessage());
		
	}finally {
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
		if(result) {
				// call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet
				
		//*****Step 9****** inserting block data for wallet Transaction
				try {
					PPWalletEnvironment.setComment(1,className,"Finally Entring Transaction in Blockchain  is  ");

					WalletDao.insertGenericWalletTransactions(transactionCode, claimedWalletId, systemReference + "C" , String.valueOf(cashValueAfterConversion) , "404", payMode, transactionDatetime);
					}catch (Exception e) {
				}
			}
			if(pstmt!=null) pstmt.close(); if(rs!=null) rs.close(); if(transactionDatetime!=null) transactionDatetime = null; if(transactionCode!=null) transactionCode =null;
			if(totalLoyaltyBalance!=null) totalLoyaltyBalance= null; if(cashValueAfterConversion!=0) cashValueAfterConversion = 0; if(systemReference!=null) systemReference = null;			
	}
	 
	return result;
}


public String getLoyaltyBalanceForSelectedWallet(String WalletId) {
	PreparedStatement pstmt=null;
	Connection connection = null;
	ResultSet rs=null;
	String query = null;
	String PointsBalance = null;
	try {
		 connection = super.getConnection();	
			query = "select pointbalance balance from loyalty_points_bc where walletid=? and sequenceid = (select max(sequenceid) from  loyalty_points_bc where walletid = ? ) ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, WalletId);
			pstmt.setString(2, WalletId);
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				while(rs.next()){	 			 			
					PointsBalance = (StringUtils.trim(rs.getString("balance"))  );
					} // end of while
			} //end of if rs!=null check
				 pstmt.close();
		 if(PointsBalance==null) {
				PPWalletEnvironment.setComment(3,className,"No Pont Balance Remaining:");
			
		  }else {
			 PPWalletEnvironment.setComment(3,className,"Previus Balance for walletid");
		  }
			PPWalletEnvironment.setComment(3,className,"step 2");
	
	}catch(Exception e) {
		
	}
	return PointsBalance;
}


public boolean updateLoyaltyRewardsForAllPoint(String relationshipNo, String walletId, String walletBalance, String payMode) throws Exception{
	PreparedStatement pstmt=null;
	Connection connection = null;
	boolean result= false;
	
	ResultSet rs=null;
	String query = null;
	String transactionDatetime = null;
	String transactionCode =  null;
	String totalLoyaltyBalance = null;
	
	float  newTotalLoyaltyBalance = 0;//this is the new value of the points after deducting points to be claimed for specific wallet
	
	
	String newLoyaltyPointBal=null;
	
	String  ttlUnclaimedPointsForWall = null;//this stores value for total unclaimed points for specific wallet

	String systemreference =null;
	float cashValueAfterConversion =  0;
	try {	
		 transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
		 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");  
		 
		 transactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
		 
		 connection = super.getConnection();	
		 connection.setAutoCommit(false);
			
		float pointConversion	 = 0;
		 //******get the point conversion for the paymode
			query = "select pointsconversion from loyalty_rules where paymode=? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, payMode);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	 			 			
					pointConversion = Float.parseFloat( StringUtils.trim(rs.getString("pointsconversion"))  );
					} // end of while
				PPWalletEnvironment.setComment(3,className,"step 1   pointConversion is  "+pointConversion   );
			} 
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();		
			
         
			//*****Get the current point balance accumulated by the customer for all wallets for all transactions
			query = "select pointbalance from loyalty_points_bc where sequenceid  = (select max(sequenceid) from loyalty_points_bc where relationshipno = ?) ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	 			 			
					totalLoyaltyBalance = ( StringUtils.trim(rs.getString("pointbalance"))  );
					} // end of while
				PPWalletEnvironment.setComment(3,className,"step 1   totalLoyaltyBalance is  "+totalLoyaltyBalance   );
			} 
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();	
			
			
		
		   //*****Get the unclaimed point balance for the specific wallet******************************************************
			//  = "select pointbalance from loyalty_points_bc where sequenceid  = (select max(sequenceid) from loyalty_points_bc where relationshipno = ?) ";
			query = "select  sum(pointaccrued) from loyalty_points_bc where walletid = ? and status = ?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, walletId);
			pstmt.setString(2, "U");
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){	 			 			
					//totalLoyaltyBalanceForClaimedWal = ( StringUtils.trim(rs.getString("pointbalance"))  );
					ttlUnclaimedPointsForWall = ( StringUtils.trim(rs.getString("sum(pointaccrued)")));
					} // end of while
				//PPWalletEnvironment.setComment(3,className,"step 1   totalLoyaltyBalance is  "+totalLoyaltyBalance   );
				PPWalletEnvironment.setComment(3,className,"step 1   totalLoyaltyBalance is  "+  ttlUnclaimedPointsForWall );
			} 
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();	
						
			
			//deduct points gotten from above process from the total points
			
			if(Float.parseFloat( ttlUnclaimedPointsForWall) <=  Float.parseFloat(totalLoyaltyBalance)) {
				
				newTotalLoyaltyBalance = Float.parseFloat(totalLoyaltyBalance) - Float.parseFloat(ttlUnclaimedPointsForWall) ;
			
			} else {
				throw new Exception("total unclaimed point for wallet is more than total points balance");
			}
			
			newTotalLoyaltyBalance = Float.parseFloat(totalLoyaltyBalance) - Float.parseFloat(ttlUnclaimedPointsForWall) ;

	

			if(Float.parseFloat(totalLoyaltyBalance) > 0) {

				//*** Calculate the cash value point conversion
				
				cashValueAfterConversion = Float.parseFloat(ttlUnclaimedPointsForWall) * (pointConversion);
				PPWalletEnvironment.setComment(3,className,"cashValueAfterConversion is  "+cashValueAfterConversion   );

				//**** Update the wallet of the customer
				
				query = " update wallet_details set currbal= ?, lastupdated = ? where  walletid=? ";
				pstmt = connection.prepareStatement(query);
				pstmt.setFloat(1, Float.parseFloat(walletBalance) + cashValueAfterConversion );
				pstmt.setString(2, transactionDatetime); 
				pstmt.setString(3, walletId); 
					try {
					PPWalletEnvironment.setComment(3,className,"update  wallet with  amount" + Float.parseFloat(walletBalance) + cashValueAfterConversion );

					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
				  	}					//connection.commit();
					if(pstmt!=null) pstmt.close();
					
			
			//update all transactions related to that specific wallet. 
			query = "update loyalty_points_bc set status = 'C' where walletid = ?";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, walletId);
			try {
				pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception("failed to update individual loyalty point transactions ");
			}
			pstmt.close();
			
			

			 systemreference =  PPWalletEnvironment.getCodeLoyaltyRedeemRate() +"-"+ transactionCode+"-" ;
		//											1		2			3			4			5				6		7
			 query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime)  "
						+ " values (?, ?, ?, ?, ?, ?, ? )  ";
						//		    1  2  3  4  5  6  7  
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, transactionCode); 					// tokenid
				pstmt.setString(2, walletId); 						// userid
				pstmt.setString(3,systemreference + "C" );							// usertype
				pstmt.setFloat(4,  (cashValueAfterConversion));  // 
				pstmt.setString(5, "404");
				pstmt.setString(6, "C");                       // Credit as we are topping up
				pstmt.setString(7,  transactionDatetime);                       // Credit as we are topping up
				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}					
				pstmt.close();
		
					//  Insert loyalty points for the user  
					 								//		1		2			3			   4			5			6			7          8          9
				query = "insert into loyalty_points_bc (walletid, usertype, relationshipno, paymode, txnreference, pointaccrued, status, pointbalance, txndatetime) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
						//		   1  2  3  4  5  6  7  8  9
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, (walletId)); 					
				pstmt.setString(2, "C"); 		
				pstmt.setString(3, relationshipNo); // 
				pstmt.setString(4,payMode );	
				pstmt.setString(5, transactionCode); // transaction reference generated by the system
				pstmt.setFloat(6, -Float.parseFloat(ttlUnclaimedPointsForWall) );  // pointaccrued
				pstmt.setString(7, "C" );  // Claimed
			//	pstmt.setFloat(8,  Float.parseFloat(totalLoyaltyBalance ) - Float.parseFloat(ttlUnclaimedPointsForWall) );  // pointbalance
				pstmt.setFloat(8,  newTotalLoyaltyBalance);  //inserting new  point balance
				pstmt.setString(9,transactionDatetime );	

					try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
				}	
		
				connection.commit();
				result = true;	
	
			
			}else if(Float.parseFloat(totalLoyaltyBalance) < 0) {
				PPWalletEnvironment.setComment(1,className, "Total loyalty points is negative value" + Float.parseFloat(totalLoyaltyBalance));
	
			} else {
				PPWalletEnvironment.setComment(3,className, "No Loyalty Points to convert");

			}
			
	}catch(Exception e) {
		result = false;
		connection.rollback();
		PPWalletEnvironment.setComment(1,className,"The exception in method updateLoyaltyRewardsForAllPoint  is  "+e.getMessage());
		throw new Exception ("The exception in method updateLoyaltyRewardsForAllPoint  is  "+e.getMessage());
		
	}finally {
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close(); if(transactionDatetime!=null) transactionDatetime = null; if(transactionCode!=null) transactionCode = null; 
			if(totalLoyaltyBalance!=null) totalLoyaltyBalance = null; if(systemreference!=null) systemreference = null;
			 if(cashValueAfterConversion!=0) cashValueAfterConversion = 0; 
			
			if(result) {
		// call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet
	
		//*****Step 9****** inserting block data for wallet Transaction
				try {
					PPWalletEnvironment.setComment(1,className,"Finally Entering Transaction in Blockchain  is  ");

					WalletDao.insertGenericWalletTransactions(transactionCode, walletId, systemreference + "C" , String.valueOf(cashValueAfterConversion) , "404", payMode, transactionDatetime);
					}catch (Exception e) {
				}
			}
	}
	 
	return result;
	
}


/*=============this method handles individual point redemption===================*/

public boolean updateLoyaltyRewardsForAPointMobile (String relationshipNo, String seqno, String claimedWalletId, String pointAccrued, String payMode, String walletBalance  ) throws Exception {
PreparedStatement pstmt=null;
Connection connection = null;
boolean result= false;
ResultSet rs=null;
String query = null;
String transactionDatetime = null;
String transactionCode =  null;
String totalLoyaltyBalance = null;
String systemReference =null;
float cashValueAfterConversion =  0;
try {

transactionDatetime = Utilities.getMYSQLCurrentTimeStampForInsert();
SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMddHHmmssSSS");    	       	 		 	
transactionCode = formatter1.format(new java.util.Date())+Utilities.genAlphaNumRandom(9);
connection = super.getConnection();	
connection.setAutoCommit(false);

float pointConversion	 = 0;

query = "select pointsconversion from loyalty_rules where paymode=? ";
pstmt = connection.prepareStatement(query);
pstmt.setString(1, payMode);
rs = (ResultSet)pstmt.executeQuery();
if(rs!=null){
	while(rs.next()){	 			 			
		pointConversion = Float.parseFloat( StringUtils.trim(rs.getString("pointsconversion"))  );
		} // end of while
	PPWalletEnvironment.setComment(3,className,"step 1   pointConversion is  "+pointConversion   );
} 
if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();		


query = "select pointbalance from loyalty_points_bc where sequenceid  = (select max(sequenceid) from loyalty_points_bc where relationshipno = ?) ";
pstmt = connection.prepareStatement(query);
pstmt.setString(1, relationshipNo);
rs = (ResultSet)pstmt.executeQuery();
if(rs!=null){
	while(rs.next()){	 			 			
		totalLoyaltyBalance = ( StringUtils.trim(rs.getString("pointbalance"))  );
		} // end of while
	PPWalletEnvironment.setComment(3,className,"step 1   totalLoyaltyBalance is  "+totalLoyaltyBalance   );
} 
if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();			

//*** Calculate the cash value

cashValueAfterConversion = pointConversion * Float.parseFloat(pointAccrued);

//**** Update the wallet of the customer

query = " update wallet_details set currbal= ?, lastupdated = ? where  walletid=? ";
pstmt = connection.prepareStatement(query);
pstmt.setFloat(1, Float.parseFloat(walletBalance) + cashValueAfterConversion );
pstmt.setString(2, transactionDatetime); 
pstmt.setString(3, claimedWalletId); 
	try {
	PPWalletEnvironment.setComment(3,className,"update  wallet with  amount" + Float.parseFloat(walletBalance) + cashValueAfterConversion );

	pstmt.executeUpdate();
	}catch(Exception e) {
		throw new Exception (" failed query "+query+" "+e.getMessage());
  	}					//connection.commit();
	if(pstmt!=null) pstmt.close();
	
	
	
	//update previous transaction from U to C
	
	query = "update loyalty_points_bc set status = 'C' where sequenceid = ?";
	pstmt = connection.prepareStatement(query);
	pstmt.setString(1, seqno);
	try {
		pstmt.executeUpdate();
	}catch(Exception e) {
		throw new Exception("failed to update individual loyalty point transactions ");
	}
	pstmt.close();
	
	
	
	
	systemReference =  PPWalletEnvironment.getCodeLoyaltyRedeemRate() +"-"+ transactionCode+"-" ;
//											1		2			3			4			5				6		7
	 query = "insert into wallet_txn_bc (txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime)  "
				+ " values (?, ?, ?, ?, ?, ?, ? )  ";
				//		    1  2  3  4  5  6  7  
		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, transactionCode); 					// tokenid
		pstmt.setString(2, claimedWalletId); 						// userid
		pstmt.setString(3,systemReference + "C" );							// usertype
		pstmt.setFloat(4,  (cashValueAfterConversion));  // 
		pstmt.setString(5, "404");
		pstmt.setString(6, "C");                       // Credit as we are topping up
		pstmt.setString(7,  transactionDatetime);                       // Credit as we are topping up
		try {
			pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					
		pstmt.close();	 
	//  Insert loyalty points for the sender user  
	 								//		1		2			3			   4			5			6				7          8       9
 query = "insert into loyalty_points_bc (walletid, usertype, relationshipno, paymode, txnreference, pointaccrued, pointbalance, status,  txndatetime) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
				//		   1  2  3  4  5  6  7  8  9';
		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, (claimedWalletId)); 					
		pstmt.setString(2, "C"); 		
		pstmt.setString(3, relationshipNo); // 
		pstmt.setString(4,payMode );	
		pstmt.setString(5, transactionCode); // transaction reference generated by the system
		pstmt.setFloat(6, -Float.parseFloat(pointAccrued) );  // pointaccrued
		
		pstmt.setFloat(7,  Float.parseFloat(totalLoyaltyBalance ) -  Float.parseFloat(pointAccrued)     );  // pointbalance
		pstmt.setString(8,"C" );	
		pstmt.setString(9,transactionDatetime );	

			try {
			pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
		}	

		connection.commit();
		result = true;
}catch(Exception e) {
result = false;
connection.rollback();
PPWalletEnvironment.setComment(1,className,"The exception in method updateLoyaltyRewardsForAPoint  is  "+e.getMessage());
throw new Exception ("The exception in method updateLoyaltyRewardsForAPoint  is  "+e.getMessage());

}finally {
if(connection!=null)
try {
	super.close();
} catch (SQLException e) {
	PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
}
if(result) {
	// call the Blockchain method here and pass the values within the method. Here we are inserting in the walletledger stream of Blockchain having chainame ppwallet
	
//*****Step 9****** inserting block data for wallet Transaction
	try {
		PPWalletEnvironment.setComment(1,className,"Finally Entering Transaction in Blockchain  is  ");

		WalletDao.insertGenericWalletTransactions(transactionCode, claimedWalletId, systemReference + "C" , String.valueOf(cashValueAfterConversion) , "404", payMode, transactionDatetime);
		}catch (Exception e) {
	}
}
if(pstmt!=null) pstmt.close(); if(rs!=null) rs.close(); if(transactionDatetime!=null) transactionDatetime = null; if(transactionCode!=null) transactionCode =null;
if(totalLoyaltyBalance!=null) totalLoyaltyBalance= null; if(cashValueAfterConversion!=0) cashValueAfterConversion = 0; if(systemReference!=null) systemReference = null;			
}

return result;
}



}



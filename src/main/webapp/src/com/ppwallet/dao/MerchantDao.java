package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.MerchClientDetails;
import com.ppwallet.model.MccGroup;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.SystemMsfPlans;
import com.ppwallet.utilities.Utilities;


public class MerchantDao extends HandleConnections{
	
	private static final long serialVersionUID = 2L;
	private static String className = MerchantDao.class.getSimpleName();
	
	//merch retrop
	public ConcurrentHashMap<String, String> getMerchantCategories() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		//Wallet wallet = null;
		ConcurrentHashMap<String, String> hash_Categories = null;
		try{
			connection = super.getConnection();	

			query = "select mcccategoryid, mcccategoryname  from merch_mcc_group  ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 hash_Categories = new ConcurrentHashMap<String, String>();
				 	while(rs.next()){	 			 			
				 		hash_Categories.put(StringUtils.trim(rs.getString("mcccategoryid")) , StringUtils.trim(rs.getString("mcccategoryname")) );
				 		} // end of while
				 	//arr_Product.add(m_Product);
				 	} //end of if rs!=null check
			 // validate the password
			 if(hash_Categories!=null)
				 if(hash_Categories.size()==0)
					 hash_Categories=null;
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getMerchantCategories  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchantCategories  is  "+e.getMessage());			
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
		return hash_Categories;
		
	}
	
	
	public boolean registerMerchant(String nationalId,String userId, String userPwd, String userName, String userEmail,
			String userContact, String address1, String address2, String postCode, String companyName,
			String registrationNo, String mccCode, String billerCode, String city, ArrayList<String> arrMerchFile) throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			//ResultSet rs=null;
			String query = null;
			boolean result = false;

			try{
				
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
				 
				 
				 //Query 1
				 								//		  1			   2			3			   4				5				6			7			
				 query = "insert into merch_details (billercode, merchantid, merchantpwd, merchantname,  nationalid, merchantemail,  merchantcontact, "
				 									+ "address1,    address2,  pincode, city, companyname, compregistration,  msf_plan_id,  mcccategoryid, status, expiry, createdon ) "
								+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?,  ?,  ?,  ?,  ?,  ?, ?, ?, ?,  ?) ";
								//		   1  2  3  4  5  6  7	8	9  10  11  12  13  14 15 16 17  18
				 		
			 
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, billerCode); 						
					pstmt.setString(2, userId); 
					pstmt.setString(3, Utilities.encryptString(userPwd));						
					pstmt.setString(4, userName);						
					pstmt.setString(5, nationalId);					
					pstmt.setString(6, (userEmail));						 
					pstmt.setString(7, userContact);						
					pstmt.setString(8, address1);						
					pstmt.setString(9, address2);						
					pstmt.setString(10, postCode);					
					pstmt.setString(11, city);			
					pstmt.setString(12, companyName);							
					pstmt.setString(13, registrationNo);						
					pstmt.setInt(14, 0);	
					pstmt.setString(15, mccCode);
					pstmt.setString(16, "V");
					pstmt.setString(17, "9999-12-31");
					pstmt.setString(18, Utilities.getMYSQLCurrentTimeStampForInsert());
						
					
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}										
					
		
					pstmt.close();
					
					if(arrMerchFile!=null) {
					
									//Query 2					//       1				2		
								query= "insert into merch_kyc_docs (billercode, document_location ) " 
																+ " values (?,?) ";
								pstmt = connection.prepareStatement(query);
								for (int i = 0; i < arrMerchFile.size(); i++) {
				                pstmt.setString(1, billerCode );
								pstmt.setString(2, arrMerchFile.get(i) );
				                pstmt.addBatch();
				                if ((i + 1) % 10 == 0 || (i + 1) == arrMerchFile.size()) {
				                	try {
				                     pstmt.executeBatch();
				                	}catch (Exception e) {
				                		throw new Exception (" failed query "+query+" "+e.getMessage());
				                	}
				                }
				            }
						
					}			   					
					
						// finally commit the connection
						connection.commit();
						result = true;
			}catch(Exception e){
				connection.rollback();
				result = false;
				PPWalletEnvironment.setComment(1,className,"The exception in method registerMerchant  is  "+e.getMessage());
				throw new Exception ("The exception in method registerMerchant  is  "+e.getMessage());
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
	
	
	public Merchant getMerchantProfile(String billerCode) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		Merchant merchant = null;
		
		try{
			connection = super.getConnection();	
			//Query 1 
			query = "select billercode, merchantid, merchantname, nationalid, merchantemail, merchantcontact , address1, "
					+ "address2, pincode, city, companyname, compregistration, mcccategoryid, status  from merch_details where billercode=? ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, billerCode);
			rs = (ResultSet)pstmt.executeQuery();

			 if(rs!=null){
				 merchant =new Merchant();
				 	while(rs.next()){	
				 		
				 		
				 		merchant.setBillerCode(StringUtils.trim(rs.getString("billercode")));
				 		merchant.setMerchantId(StringUtils.trim(rs.getString("merchantid")));
				 		merchant.setMerchantName(StringUtils.trim(rs.getString("merchantname")));
				 		merchant.setNationalId(StringUtils.trim(rs.getString("nationalid")));
				 		merchant.setEmail(StringUtils.trim(rs.getString("merchantemail")));
				 		merchant.setContact(StringUtils.trim(rs.getString("merchantcontact")));
				 		merchant.setAddress1(StringUtils.trim(rs.getString("address1")));
				 		merchant.setAdress2(StringUtils.trim(rs.getString("address2")));
				 		merchant.setPinCode(StringUtils.trim(rs.getString("pincode")));
				 		merchant.setCity(StringUtils.trim(rs.getString("city")));
				 		merchant.setCompanyName(StringUtils.trim(rs.getString("companyname")));
				 		merchant.setCompanyRegistration(StringUtils.trim(rs.getString("compregistration")));
				 		merchant.setMccCategoryId(StringUtils.trim(rs.getString("mcccategoryid")));
				 		merchant.setStatus(StringUtils.trim(rs.getString("status")));
				 		
				 		PPWalletEnvironment.setComment(2,className,"Biller code  is  "+merchant.getBillerCode());
				 		
				 	}
			 }
				 		
			 rs.close(); pstmt.close();
			//Query 2
				 		query= "select document_location from merch_kyc_docs where billercode=?  ";
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, merchant.getBillerCode());
						rs = (ResultSet)pstmt.executeQuery();
						ArrayList<String> docArray = null;
						if(rs!=null){
							docArray = new ArrayList<String>();
							while(rs.next()){	
								docArray.add(StringUtils.trim(rs.getString("document_location")));
							}
							if(docArray.size()>0) {
							PPWalletEnvironment.setComment(3,className,"Total Documents are   "+docArray.size());
							if(merchant!=null)	merchant.setDocumentArray(docArray);
							}else {
								docArray = null;
							}
						}
				 		
			 
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getFullMerchantProfile  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchantFullProfile  is  "+e.getMessage());			
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
		
		
		return merchant;
	}
	
	public boolean updateMerchant(String billerCode, String nationalId,String merchantid, String userPwd, String userName, String userEmail,
			String userContact, String address1, String address2, String postCode, String companyName,
			String registrationNo, String mccCode, String city, ArrayList<String> arrMerchFile) throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			//ResultSet rs=null;
			String query = null;
			boolean result = false;

			try{
				
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
				 		 
		/*
		 * query =
		 * "update customer_details set  customername=?, custemail=?, custcontact=?, address1=?, "
		 * + "address2=?, pincode=? where customerid=? ";
		 */
				 PPWalletEnvironment.setComment(2, className,"mccCode is "+mccCode);
				 
				 //Query 1
				 if(userPwd.equals("")) {
					 //query with no password             // 1            //  2            3             4                 5
				 	query = "update merch_details set  merchantname=?, nationalid=?, merchantemail=?, merchantcontact=?, address1=?, "
					 		+ "address2=?, pincode=?, city=?, companyname=?, compregistration=?, mcccategoryid=? where billercode=? ";
				 	//          6           7           8        9               10                 11                   12
				 }else {
					//query with no password              1               2              3              4                  5              6
					 query = "update merch_details set merchantpwd=?, merchantname=?, nationalid=?, merchantemail=?, merchantcontact=?, address1=?, "
						 		+ "address2=?, pincode=?, city=?, companyname=?, compregistration=?, mcccategoryid=? where billercode=? ";
					 //                7          8          9       10               11              12
					 
				 }
			 
				pstmt = connection.prepareStatement(query);
				if(userPwd.equals("")) {
					pstmt.setString(1, userName); 
					pstmt.setString(2, nationalId); 
					pstmt.setString(3, userEmail); 
					pstmt.setString(4, userContact); 
					pstmt.setString(5, address1);
					pstmt.setString(6, address2);
					pstmt.setString(7, postCode);
					pstmt.setString(8, city);
					pstmt.setString(9, companyName);
					pstmt.setString(10, registrationNo);
					pstmt.setString(11, mccCode );
					pstmt.setString(12, billerCode );
					
				}else {
					pstmt.setString(1,Utilities.encryptString(userPwd));
					pstmt.setString(2, userName); 
					pstmt.setString(3, nationalId); 
					pstmt.setString(4, userEmail); 
					pstmt.setString(5, userContact); 
					pstmt.setString(6, address1);
					pstmt.setString(7, address2);
					pstmt.setString(8, postCode);
					pstmt.setString(9, city);
					pstmt.setString(10, companyName);
					pstmt.setString(11, registrationNo);
					pstmt.setString(12, mccCode);
					pstmt.setString(13, billerCode);
				}
						
					
				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}										
					
		
					pstmt.close();
					
					if(arrMerchFile!=null) {
				
								//Query 2					//       1				2		
						query= "insert into merch_kyc_docs (billercode, document_location ) " 
														+ " values (?,?) ";
						pstmt = connection.prepareStatement(query);
						for (int i = 0; i < arrMerchFile.size(); i++) {
		                pstmt.setString(1, billerCode );
						pstmt.setString(2, arrMerchFile.get(i) );
		                pstmt.addBatch();
		                if ((i + 1) % 10 == 0 || (i + 1) == arrMerchFile.size()) {
		                	try {
		                     pstmt.executeBatch();
		                	}catch (Exception e) {
		                		throw new Exception (" failed query "+query+" "+e.getMessage());
		                	}
		                }
		            }
						
					}			   					
					
						// finally commit the connection
						connection.commit();
						result = true;
			}catch(Exception e){
				connection.rollback();
				result = false;
				PPWalletEnvironment.setComment(1,className,"The exception in method registerMerchant  is  "+e.getMessage());
				throw new Exception ("The exception in method registerMerchant  is  "+e.getMessage());
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
	
	public ArrayList<SystemMsfPlans> getMerchantMsfPlan(String billerCode) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<SystemMsfPlans> arrMerchMsfPlan = null;
		try{

			connection = super.getConnection();	
			query = "select planid, plan_name, plan_fee_fixed, plan_fee_var, plan_type, desposit_fee, set_up_fee, monthly_fee, "
					+ " annual_fee, statement_fee, late_payment_fee, plan_cycle, status, "
					+ " created_on  from merch_sys_msf_plan where planid in ( select planid from merch_msf_plan_relation where billercode = ? and status = ?  ) ";
			PPWalletEnvironment.setComment(3, className, "query is " +  query );

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, billerCode);
			pstmt.setString(2, "A"); 
			rs = pstmt.executeQuery();
			PPWalletEnvironment.setComment(3, className, " rs  is "+  rs );

			 if(rs!=null){
				 arrMerchMsfPlan =new  ArrayList<SystemMsfPlans>();
				 while(rs.next()){	
						PPWalletEnvironment.setComment(3, className, "in arrMerchMsfPlan  " );
						SystemMsfPlans m_merchMsfPlan = new SystemMsfPlans();
				 		m_merchMsfPlan.setPlanId( StringUtils.trim(rs.getString("planid")));
				 		m_merchMsfPlan.setPlanName( StringUtils.trim(rs.getString("plan_name")));
				 		m_merchMsfPlan.setPlanFeeFixed( StringUtils.trim(rs.getString("plan_fee_fixed")));
				 		m_merchMsfPlan.setPlanFeeVar( StringUtils.trim(rs.getString("plan_fee_var")));
				 		m_merchMsfPlan.setPlanType(StringUtils.trim(rs.getString("plan_type")));
				 		m_merchMsfPlan.setPlanDepositFee( StringUtils.trim(rs.getString("desposit_fee")));
				 		m_merchMsfPlan.setPlanSetUpFee( StringUtils.trim(rs.getString("set_up_fee")));
				 		m_merchMsfPlan.setPlanMonthlyFee( StringUtils.trim(rs.getString("monthly_fee")));
				 		m_merchMsfPlan.setPlanAnnualFee( StringUtils.trim(rs.getString("annual_fee")));
				 		m_merchMsfPlan.setPlanStatementFee( StringUtils.trim(rs.getString("statement_fee")));
				 		m_merchMsfPlan.setPlanLatePaymentFee(StringUtils.trim(rs.getString("late_payment_fee")));
				 		m_merchMsfPlan.setPlanCycle( StringUtils.trim(rs.getString("plan_cycle")));
				 		m_merchMsfPlan.setStatus( StringUtils.trim(rs.getString("status")));
				 		arrMerchMsfPlan.add(m_merchMsfPlan );
				 		
				 		PPWalletEnvironment.setComment(3,className,"Total msf Plans For the billerCode "+  billerCode   +"is "+arrMerchMsfPlan.size());	
					} // end of while						 	
			 	} //end of if rs!=null check
		 		PPWalletEnvironment.setComment(3,className,"after billercode "+  billerCode);	

			 if(arrMerchMsfPlan!=null)
				  if(arrMerchMsfPlan.size()==0)
					  arrMerchMsfPlan=null;			  
			 		
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getMerchantMsfPlan  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchantMsfPlan  is  "+e.getMessage());			
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
		
		
		return arrMerchMsfPlan;
	}
	
	

	
	//merch retrop


	//get merchant details table
	public Merchant getMerchantDetails(String billerCode) throws Exception  {
		//PPWalletEnvironment.setComment(2,className,"We are in merchant details ");

		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		Merchant mMerch = null;
		
		try {
			connection = super.getConnection();	

			query = "select merchantid merchid , city mcity, companyname mcompname , billercode mbillercode, "
					+ " expiry expirydate from merch_details where billercode=? and status=?   ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, billerCode);
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){
					 mMerch=new Merchant();				 
					mMerch.setMerchantId( rs.getString(StringUtils.trim("merchid"))   );
					mMerch.setCity( rs.getString(StringUtils.trim("mcity")) );
					mMerch.setCompanyName( rs.getString(StringUtils.trim("mcompname")) );
					mMerch.setBillerCode( rs.getString(StringUtils.trim("mbillercode")) ); 
					//PPWalletEnvironment.setComment(2,className,rs.getString(StringUtils.trim("mbillercode")));
					
				}//whileloop
			}//rs
					 		 				
		} catch(Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getMerchDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchantDetails  is  "+e.getMessage());		
		}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
				//if(mMerch!= null) mMerch = null;
				//if(query!= null) query = null;
			}
		return mMerch;
	}
	
	public Merchant getMobileMerchantProfile(String userId) throws Exception{                       //    TODO *******************changed this from getMerchantProfile to getMobileMerchantProfile
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		Merchant merchant = null;
		try{
			connection = super.getConnection();	
			//Query 1 
			query = "select billercode, merchantid, merchantname, nationalid, merchantemail, merchantcontact , address1, "
					+ "address2, pincode, city, companyname, compregistration, mcccategoryid, status  from merch_details where merchantid=? ";
			 
//		    TODO *******************changed merchantid to billercode
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, userId);
			rs = (ResultSet)pstmt.executeQuery();

			 if(rs!=null){
				 merchant =new Merchant();
				 	while(rs.next()){	
				 		
				 		
				 		merchant.setBillerCode(StringUtils.trim(rs.getString("billercode")));
				 		merchant.setMerchantId(StringUtils.trim(rs.getString("merchantid")));
				 		merchant.setMerchantName(StringUtils.trim(rs.getString("merchantname")));
				 		merchant.setNationalId(StringUtils.trim(rs.getString("nationalid")));
				 		merchant.setEmail(StringUtils.trim(rs.getString("merchantemail")));
				 		merchant.setContact(StringUtils.trim(rs.getString("merchantcontact")));
				 		merchant.setAddress1(StringUtils.trim(rs.getString("address1")));
				 		merchant.setAdress2(StringUtils.trim(rs.getString("address2")));
				 		merchant.setPinCode(StringUtils.trim(rs.getString("pincode")));
				 		merchant.setCity(StringUtils.trim(rs.getString("city")));
				 		merchant.setCompanyName(StringUtils.trim(rs.getString("companyname")));
				 		merchant.setCompanyRegistration(StringUtils.trim(rs.getString("compregistration")));
				 		merchant.setMccCategoryId(StringUtils.trim(rs.getString("mcccategoryid")));
				 		merchant.setStatus(StringUtils.trim(rs.getString("status")));
				 		
				 		PPWalletEnvironment.setComment(2,className,"Biller code  is  "+merchant.getBillerCode());
				 		
				 	}
			 }
				 		
			 rs.close(); pstmt.close();
			//Query 2
				 		query= "select document_location from merch_kyc_docs where billercode=?  ";
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, merchant.getBillerCode());
						rs = (ResultSet)pstmt.executeQuery();
						ArrayList<String> docArray = null;
						if(rs!=null){
							docArray = new ArrayList<String>();
							while(rs.next()){	
								docArray.add(StringUtils.trim(rs.getString("document_location")));
							}
							if(docArray.size()>0) {
							PPWalletEnvironment.setComment(3,className,"Total Documents are   "+docArray.size());
							if(merchant!=null)	merchant.setDocumentArray(docArray);
							}else {
								docArray = null;
							}
						}
				 		
			 
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getFullMerchantProfile  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchantFullProfile  is  "+e.getMessage());			
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
		
		
		return merchant;
	}

	/**
	 * public Merchant getFullMerchantPersonalProfile(String userId, String userType) throws Exception {
		PPWalletEnvironment.setComment(2,className,"We are in merchant personal profile ");
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		Merchant mPrfMerch = null;
		
		try {
			connection = super.getConnection();	

			query = "select billercode billercode, merchantid merchid , merchantname merchantname, merchantcontact merchantcontact ,nationalid nationalid, merchantemail merchantemail, city city,  "
					+ "address1 address1, address2 address2, companyname companyname, compregistration compregistration, mcccategoryid mcccategoryid ,"
					+ "pincode pincode, status status from merch_details where merchantid=? and status=?   ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, userId);
			pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){
					mPrfMerch=new Merchant();	
					mPrfMerch.setBillerCode(rs.getString(StringUtils.trim("billercode")));
					mPrfMerch.setMerchantId(rs.getString(StringUtils.trim("merchid")));
					mPrfMerch.setMerchantName(rs.getString(StringUtils.trim("merchantname")));
					mPrfMerch.setContact(rs.getString(StringUtils.trim("merchantcontact")));
					mPrfMerch.setNationalId(rs.getString(StringUtils.trim("nationalid"))); 
					mPrfMerch.setEmail(rs.getString(StringUtils.trim("merchantemail"))); 
					mPrfMerch.setCity(rs.getString(StringUtils.trim("city"))); 
					mPrfMerch.setAddess1(rs.getString(StringUtils.trim("address1"))); 
					mPrfMerch.setAdress2(rs.getString(StringUtils.trim("address2"))); 
					mPrfMerch.setPinCode(rs.getString(StringUtils.trim("nationalid"))); 
					mPrfMerch.setStatus(rs.getString(StringUtils.trim("status"))); 
					
					
					mPrfMerch.setCompnayName(rs.getString(StringUtils.trim("companyname"))); 
					mPrfMerch.setCompanyRegistration(rs.getString(StringUtils.trim("compregistration"))); 
					mPrfMerch.setMerchantCategory(rs.getString(StringUtils.trim("mcccategoryid"))); 
					//PPWalletEnvironment.setComment(2,className,rs.getString(StringUtils.trim("billercode")));
					
				}//whileloop
			}//rs
			 rs.close(); pstmt.close();
				//Query 2
			 query= "select document_location from merch_kyc_docs where billercode=?  ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, billerCode);
				rs = (ResultSet)pstmt.executeQuery();
				ArrayList<String> docArray = null;
				if(rs!=null){
					mMerch = new Merchant();
					docArray = new ArrayList<String>();
					while(rs.next()){	
						docArray.add(StringUtils.trim(rs.getString("document_location")));
					}
					if(docArray.size()>0) {
					PPWalletEnvironment.setComment(3,className,"Total Documents are   "+docArray.size());
					if(mMerch!=null)	
					mMerch.setKycLocation(docArray);
					}else {
						PPWalletEnvironment.setComment(3,className," No documents");

						docArray = null;
					}
				}
			
			
			
			
			
			
			
		} catch(Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getFullMerchantPersonalProfile  is  "+e.getMessage());
			throw new Exception ("The exception in method getFullMerchantPersonalProfile  is  "+e.getMessage());		
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
		return mPrfMerch;
	}
	 * @param merchConfirmPass 
	 * @param merchPass 
	 * 
	 * */
	

public boolean MerchantUpdatePersonalProfile(String userId, String userType, String billerCode, String merchantName, String merchContact, String merchPwd) throws Exception {
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		
		try {
			PPWalletEnvironment.setComment(3,className," Merch Pwd is  "+ merchPwd);

			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			
		 if(merchPwd.equals("")) {
                                              //		1	          	2	              3                   4					
			 	query = "update merch_details set merchantname = ?, merchantcontact =?, createdon =? where billercode =?   ";

		 }else {
			                              //      1                   2                 3                 4                 5          
			 query = "update merch_details set merchantname = ?, merchantpwd =?,  merchantcontact =?, createdon =? where billercode =?  ";
		 }
		 
		 if(merchPwd.equals("")) {
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, merchantName);
			pstmt.setString(2, merchContact);
			pstmt.setString(3, Utilities.getMYSQLCurrentTimeStampForInsert());
			pstmt.setString(4, billerCode);
		 }else {
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, merchantName);
			pstmt.setString(2, Utilities.encryptString(merchPwd));
			pstmt.setString(3, merchContact);
			pstmt.setString(4, Utilities.getMYSQLCurrentTimeStampForInsert());
			pstmt.setString(5, billerCode);
			
			PPWalletEnvironment.setComment(3,className," Merch encripted  Pwd is  "+ Utilities.encryptString(merchPwd));

		 
		 }
		
			try {
				pstmt.executeUpdate();
				}catch(Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}					//connection.commit();
			pstmt.close();
			connection.commit();
			result = true;
	
		} catch(Exception e){
			result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method MerchantUpdatePersonalProfile  is  "+e.getMessage());
			throw new Exception ("The exception in method MerchantUpdatePersonalProfile  is  "+e.getMessage());
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


public boolean MerchantUpdateContactProfile(String userId, String userType, String billerCode, String merchEmail,
	String merchCity, String address1, String address2, String postalCode) throws Exception{
	PPWalletEnvironment.setComment(3,className,"We are in MerchantUpdateContactProfile ");
	
	PreparedStatement pstmt=null;
	Connection connection = null;
	String query = null;
	boolean result = false;
	
	try {
		PPWalletEnvironment.setComment(3,className,"userId " + userId  + "userType "+ userType + "billerCode "+ billerCode +  "merchEmail "+merchEmail
				+ " merchCity" + merchCity + "address1 "+ address1 + "address2 " + address2 + "postalCode "+ postalCode   );

		
		 connection = super.getConnection();
		 connection.setAutoCommit(false);
		                              //		1	    	2	         3         4          5            6                 7				
	 query = "update merch_details set merchantemail = ?, city =?, address1 = ?, address2= ? , pincode=?, createdon = ? where billercode = ?   ";
	 
		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, merchEmail);
		pstmt.setString(2, merchCity);
		pstmt.setString(3, address1);
		pstmt.setString(4, address2);
		pstmt.setString(5, postalCode);
		pstmt.setString(6, Utilities.getMYSQLCurrentTimeStampForInsert());
		pstmt.setString(7, billerCode);
		try {
			PPWalletEnvironment.setComment(2,className,"We are in pst executed ");
			pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					//connection.commit();
		pstmt.close();
		connection.commit();
		result = true;
		PPWalletEnvironment.setComment(2,className,"result is  "+result  );


	} catch(Exception e){
		result = false;
		PPWalletEnvironment.setComment(1,className,"The exception in method MerchantUpdatePersonalProfile  is  "+e.getMessage());
		throw new Exception ("The exception in method MerchantUpdatePersonalProfile  is  "+e.getMessage());
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


public boolean MerchantUpdateCompanyProfile(String userId, String userType, String billerCode, String companyName,
		String mccCategory, String compRegNo) throws Exception {
	PPWalletEnvironment.setComment(3,className,"We are in MerchantUpdateCompanyProfile ");
	
	PreparedStatement pstmt=null;
	Connection connection = null;
	String query = null;
	boolean result = false;
	
	try {
		PPWalletEnvironment.setComment(3,className,"userId " + userId  + "userType "+ userType + "billerCode "+ billerCode +  "companyName "+companyName
				+ " mccCategory" + mccCategory + "compRegNo "+ compRegNo  );

		 connection = super.getConnection();
		 connection.setAutoCommit(false);
		                              //	1	    	   2	              3                    4                       5       		
	 query = "update merch_details set companyname = ?,  mcccategoryid =?, compregistration = ?, createdon = ? where billercode = ?   ";
	 
		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, companyName);
		pstmt.setString(2, mccCategory);
		pstmt.setString(3, compRegNo);
		pstmt.setString(4, Utilities.getMYSQLCurrentTimeStampForInsert());
		pstmt.setString(5, billerCode);
		try {
			PPWalletEnvironment.setComment(3,className,"We are in pst executed ");
			pstmt.executeUpdate();
			}catch(Exception e) {
				throw new Exception (" failed query "+query+" "+e.getMessage());
			}					//connection.commit();
		pstmt.close();
		connection.commit();
		result = true;
		PPWalletEnvironment.setComment(3,className,"result is  "+result  );


	} catch(Exception e){
		result = false;
		PPWalletEnvironment.setComment(1,className,"The exception in method MerchantUpdateCompanyProfile  is  "+e.getMessage());
		throw new Exception ("The exception in method MerchantUpdateCompanyProfile  is  "+e.getMessage());
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




public ArrayList<SystemMsfPlans> getMobileMerchantMsfPlan(String billerCode) throws Exception{
	PreparedStatement pstmt=null;
	Connection connection = null;
	ResultSet rs=null;
	String query = null;
	ArrayList<SystemMsfPlans> arrMerchMsfPlan = null;
	try{

		connection = super.getConnection();	
		query = "select planid, plan_name, plan_fee_fixed, plan_fee_var, plan_type, desposit_fee, set_up_fee, monthly_fee, "
				+ " annual_fee, statement_fee, late_payment_fee, plan_cycle, status, "
				+ " created_on  from merch_sys_msf_plan where planid in ( select planid from merch_msf_plan_relation where billercode = ? and status = ?  ) ";
		PPWalletEnvironment.setComment(3, className, "query is " +  query );

		pstmt = connection.prepareStatement(query);
		pstmt.setString(1, billerCode);
		pstmt.setString(2, "A"); 
		rs = pstmt.executeQuery();
		PPWalletEnvironment.setComment(3, className, " rs  is "+  rs );

		 if(rs!=null){
			 arrMerchMsfPlan =new  ArrayList<SystemMsfPlans>();
			 while(rs.next()){	
					PPWalletEnvironment.setComment(3, className, "in arrMerchMsfPlan  " );
					SystemMsfPlans m_merchMsfPlan = new SystemMsfPlans();
			 		m_merchMsfPlan.setPlanId( StringUtils.trim(rs.getString("planid")));
			 		m_merchMsfPlan.setPlanName( StringUtils.trim(rs.getString("plan_name")));
			 		m_merchMsfPlan.setPlanFeeFixed( StringUtils.trim(rs.getString("plan_fee_fixed")));
			 		m_merchMsfPlan.setPlanFeeVar( StringUtils.trim(rs.getString("plan_fee_var")));
			 		m_merchMsfPlan.setPlanType(StringUtils.trim(rs.getString("plan_type")));
			 		m_merchMsfPlan.setPlanDepositFee( StringUtils.trim(rs.getString("desposit_fee")));
			 		m_merchMsfPlan.setPlanSetUpFee( StringUtils.trim(rs.getString("set_up_fee")));
			 		m_merchMsfPlan.setPlanMonthlyFee( StringUtils.trim(rs.getString("monthly_fee")));
			 		m_merchMsfPlan.setPlanAnnualFee( StringUtils.trim(rs.getString("annual_fee")));
			 		m_merchMsfPlan.setPlanStatementFee( StringUtils.trim(rs.getString("statement_fee")));
			 		m_merchMsfPlan.setPlanLatePaymentFee(StringUtils.trim(rs.getString("late_payment_fee")));
			 		m_merchMsfPlan.setPlanCycle( StringUtils.trim(rs.getString("plan_cycle")));
			 		m_merchMsfPlan.setStatus( StringUtils.trim(rs.getString("status")));
			 		arrMerchMsfPlan.add(m_merchMsfPlan );
			 		
			 		PPWalletEnvironment.setComment(3,className,"Total msf Plans For the billerCode "+  billerCode   +"is "+arrMerchMsfPlan.size());	
				} // end of while						 	
		 	} //end of if rs!=null check
	 		PPWalletEnvironment.setComment(3,className,"after billercode "+  billerCode);	

		 if(arrMerchMsfPlan!=null)
			  if(arrMerchMsfPlan.size()==0)
				  arrMerchMsfPlan=null;			  
		 		
	}catch(Exception e){
		PPWalletEnvironment.setComment(1,className,"The exception in method getMerchantMsfPlan  is  "+e.getMessage());
		throw new Exception ("The exception in method getMerchantMsfPlan  is  "+e.getMessage());			
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
	
	
	return arrMerchMsfPlan;
}



//





}

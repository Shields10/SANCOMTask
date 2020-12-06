package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.User;

public class OpsProfileDao extends HandleConnections{
	private static final long serialVersionUID = 1L;
	private static String classname = OpsProfileDao.class.getSimpleName();


	public ArrayList<Merchant> getAllMerchantsBrief() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Merchant> arrMerchant = null;
		try{
			connection = super.getConnection();	

			query = "select merchantid, merchantname, billercode, merchantemail, nationalid, merchantcontact,address1, address2, pincode, city, companyname, compregistration, msf_plan_id,"
					+ " mcccategoryid, status, createdon, expiry from merch_details order by  createdon ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrMerchant = new ArrayList<Merchant>();
				 	while(rs.next()){	 
				 		Merchant m_Merchant=new Merchant();
				 		m_Merchant.setMerchantId( StringUtils.trim(rs.getString("merchantid"))    );
				 		m_Merchant.setMerchantName( StringUtils.trim(rs.getString("merchantname"))  );
				 		m_Merchant.setBillerCode( StringUtils.trim(rs.getString("billercode"))  );
				 		m_Merchant.setEmail( StringUtils.trim(rs.getString("merchantemail"))    );
				 		m_Merchant.setNationalId( StringUtils.trim(rs.getString("nationalid"))    );
				 		m_Merchant.setContact( StringUtils.trim(rs.getString("merchantcontact"))  );
				 		m_Merchant.setAddress1(StringUtils.trim(rs.getString("address1"))  );
				 		m_Merchant.setAdress2( StringUtils.trim(rs.getString("address2"))  );
				 		m_Merchant.setPinCode(StringUtils.trim(rs.getString("pincode"))  );
				 		m_Merchant.setCity( StringUtils.trim(rs.getString("city"))  );
				 		m_Merchant.setCompanyName( StringUtils.trim(rs.getString("companyname"))  );
						m_Merchant.setCompanyRegistration( StringUtils.trim(rs.getString("compregistration"))  );
				 		m_Merchant.setMsfPlanId( StringUtils.trim(rs.getString("msf_plan_id"))  );
				 		m_Merchant.setMccCategoryId(StringUtils.trim(rs.getString("mcccategoryid"))  );
				 		m_Merchant.setStatus(StringUtils.trim(rs.getString("status"))  );
				 		m_Merchant.setCreatedOn( StringUtils.trim(rs.getString("createdon"))  );
				 		m_Merchant.setExpiryDate( StringUtils.trim(rs.getString("expiry"))  );
				 		arrMerchant.add(m_Merchant);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrMerchant!=null) if(arrMerchant.size()==0) arrMerchant=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllMerchantsBrief  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllMerchantsBrief  is  "+e.getMessage());			
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
		return arrMerchant;
	}


	public ArrayList<Merchant> getAllMerchantsDetails() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Merchant> arrMerchantDetails = null;
		try{
			connection = super.getConnection();	

			query = "select billercode, merchantid, merchantpwd, merchantname, nationalid, merchantemail, merchantcontact, address1,"
					+ " address2, pincode, city, companyname, companyregistration, msf_plan_id, mcccategoryid, status, expiry, createdon";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrMerchantDetails = new ArrayList<Merchant>();
				 	while(rs.next()){	 
				 		Merchant m_Merchant=new Merchant();
				 		m_Merchant.setBillerCode( StringUtils.trim(rs.getString("billercode"))  );
				 		m_Merchant.setMerchantId( StringUtils.trim(rs.getString("merchantid"))  );
				 		m_Merchant.setPassword( StringUtils.trim(rs.getString("merchantpwd"))  );
				 		m_Merchant.setMerchantName( StringUtils.trim(rs.getString("merchantname"))  );
				 		m_Merchant.setNationalId( StringUtils.trim(rs.getString("nationalid"))    );
				 		m_Merchant.setEmail( StringUtils.trim(rs.getString("merchantemail"))    );
				 		m_Merchant.setContact( StringUtils.trim(rs.getString("merchantcontact"))  );
				 		m_Merchant.setAddress1(StringUtils.trim(rs.getString("address1"))  );
				 		m_Merchant.setAdress2( StringUtils.trim(rs.getString("address2"))  );
				 		m_Merchant.setPinCode( StringUtils.trim(rs.getString("pincode"))  );
				 		m_Merchant.setCity (StringUtils.trim(rs.getString("city"))  );
				 		m_Merchant.setCompanyName(StringUtils.trim(rs.getString("companyname"))  );
				 		m_Merchant.setCompanyRegistration( StringUtils.trim(rs.getString("companyregistration"))  );
				 		m_Merchant.setMsfPlanId( StringUtils.trim(rs.getString("msf_plan_id"))  );
				 		m_Merchant.setMccCategoryId( StringUtils.trim(rs.getString("mcccategoryid"))  );
				 		m_Merchant.setStatus(StringUtils.trim(rs.getString("status"))  );
				 		m_Merchant.setExpiryDate( StringUtils.trim(rs.getString("expiry"))  );
				 		m_Merchant.setCreatedOn( StringUtils.trim(rs.getString("createdon"))  );
				 		arrMerchantDetails.add(m_Merchant);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrMerchantDetails!=null) if(arrMerchantDetails.size()==0) arrMerchantDetails=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllMerchantsDetails()   is  "+e.getMessage());
			throw new Exception ("The exception in method getAllMerchantsDetails()   is  "+e.getMessage());			
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
		return arrMerchantDetails;
	
	}


	public boolean addNewMerchant(String merchantId, String merchantName, String merchPassword, String documentNo,
			String walletId, String email, String contact, String address1, String address2, String pinCode,
			String city, String billerCode, String companyName, String companyRegNo, String msfPlanId, String mccId,
			String status, String expiryDate) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 										//		1				2				3				4				5
			 query = "insert into merch_sys_msf_plan 	(merchantid, 	merchantpwd, 	merchantname, 	document_number, 	walletid, "
			 		+ "									merchantemail,	merchantcontact, 	address1, 	address2, 			pincode,"
			 		+ "									city,			billercode, 	companyname, 	compregistration, 	msf_plan_id,"
			 		+ "									mcccategoryid, 	status, 		expiry, 		createdon) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, UTC_TIMESTAMP) ";
							//		   1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1,  merchantId); 					
					pstmt.setString(2,  merchPassword); 					
					pstmt.setString(3,  merchantName); 					
					pstmt.setString(4,  documentNo); 					
					pstmt.setString(5,  walletId); 					
					pstmt.setString(6,  email); 					
					pstmt.setString(7,  contact); 					
					pstmt.setString(8,  address1); 					
					pstmt.setString(9,  address2);
					pstmt.setString(10, pinCode);
					pstmt.setString(11, city); 					
					pstmt.setString(12, billerCode); 					
					pstmt.setString(13, companyName); 					
					pstmt.setString(14, companyRegNo); 					
					pstmt.setInt(15, Integer.parseInt(msfPlanId)); 					
					pstmt.setInt(16, Integer.parseInt(mccId)); 					
					pstmt.setString(17, status); 					
					pstmt.setString(18, expiryDate); 					

					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method addNewMerchant  is  "+e.getMessage());
			throw new Exception ("The exception in method addNewMerchant  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,classname,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}

//*****************************************************************************************************************************************************************
	public User getSpecificOpsUser(String userId) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		User m_User = null;
		try{
			connection = super.getConnection();	
			query = "select adminid, accesstype, adminname, adminemail, admincontact, status, createdon, expiry from admin_details where  adminid=?";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, userId);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 
				 	while(rs.next()){	 
				 		m_User = new User();
				 		m_User.setUserId( StringUtils.trim(rs.getString("adminid"))    );
				 		m_User.setUserAccess( StringUtils.trim(rs.getString("accesstype"))  );
				 		m_User.setUserName( StringUtils.trim(rs.getString("adminname"))  );
				 		m_User.setEmailId( StringUtils.trim(rs.getString("adminemail"))    );
				 		m_User.setContact( StringUtils.trim(rs.getString("admincontact"))    );
				 		m_User.setUserStatus( StringUtils.trim(rs.getString("status"))  );
				 		m_User.setCreatedOn( StringUtils.trim(rs.getString("createdon"))  );
				 		m_User.setExpiryDate( StringUtils.trim(rs.getString("expiry"))  );
				 		} // end of while			 	
				 	} //end of if rs!=null check
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getSpecificOpsUser  is  "+e.getMessage());
			throw new Exception ("The exception in method getSpecificOpsUser  is  "+e.getMessage());			
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
		return m_User;
	}


	public boolean updateSpecificOpsUser(String userId,  String userPwd, String userName, String userAccess, String emailId,
			String userContact, String userStatus, String expiryDate) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
			 if(userPwd.equals("") == false) {
				 
									//					1			2				3				4			5			6			7          8
				query = "update admin_details set accesstype=?, adminname=?, adminemail=?, admincontact=?, status=?, expiry=?, adminpwd=? where adminid=?	";
								//		   1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18
				 
			 }else {
				 
								//					1			2				3				4			5			6			7
			query = "update admin_details set accesstype=?, adminname=?, adminemail=?, admincontact=?, status=?, expiry=?,  where adminid=?	";
							//		   1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18
				 
			 }
			 
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1,  userAccess); 					
					pstmt.setString(2,  userName); 					
					pstmt.setString(3,  emailId); 					
					pstmt.setString(4,  userContact); 					
					pstmt.setString(5,  userStatus); 					
					pstmt.setString(6,  expiryDate); 					
					pstmt.setString(7,  userId); 	
					 if(userPwd.equals("") == false) {
						 pstmt.setString(8,  userPwd); 	 
					 }
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method updateSpecificOpsUser  is  "+e.getMessage());
			throw new Exception ("The exception in method updateSpecificOpsUser  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,classname,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close();
		}
		return result;	
	}


	public ArrayList<User> getAllOperationUsers() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<User> arrAllOpsUser = null;
		try{
			connection = super.getConnection();	

			query = "select adminid, accesstype, adminname, adminemail, admincontact, status, expiry from admin_details order by createdon";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrAllOpsUser = new ArrayList<User>();
				 	while(rs.next()){	 
				 		User m_User=new User();
				 		m_User.setUserId(StringUtils.trim(rs.getString("adminid"))  );
				 		//m_User.setUserPassword( StringUtils.trim(rs.getString("adminpwd"))  );
				 		m_User.setUserAccess( StringUtils.trim(rs.getString("accesstype"))  );
				 		m_User.setUserName( StringUtils.trim(rs.getString("adminname"))  );
				 		m_User.setEmailId( StringUtils.trim(rs.getString("adminemail"))    );
				 		m_User.setContact( StringUtils.trim(rs.getString("admincontact"))    );
				 		m_User.setUserStatus( StringUtils.trim(rs.getString("status"))  );
				 		m_User.setExpiryDate( StringUtils.trim(rs.getString("expiry"))  );
				 		arrAllOpsUser.add(m_User);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrAllOpsUser!=null) if(arrAllOpsUser.size()==0) arrAllOpsUser=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllOperationUsers()   is  "+e.getMessage());
			throw new Exception ("The exception in method getAllOperationUsers()   is  "+e.getMessage());			
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
		return arrAllOpsUser;
	}


	public boolean addSpecificOpsUser(String userId, String password, String userName, String userAccess,
			String emailId, String userContact, String userStatus, String expiryDate) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
	    	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");     	        
			
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 									//		1			2			3				4			5	
			 query = "insert into admin_details 	(adminid, 	adminpwd, 	accesstype, 	adminname, 	adminemail, "
			 		+ "							admincontact,	status, 	expiry,  		createdon) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5  6  7  8  9  
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1,  userId); 					
					pstmt.setString(2,  password); 					
					pstmt.setString(3,  userAccess); 					
					pstmt.setString(4,  userName); 					
					pstmt.setString(5,  emailId); 					
					pstmt.setString(6,  userContact); 					
					pstmt.setString(7,  userStatus); 					
					pstmt.setString(8,  expiryDate); 					
					pstmt.setString(9,  formatter1.format(new java.util.Date())); 					
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method addSpecificOpsUser  is  "+e.getMessage());
			throw new Exception ("The exception in method addSpecificOpsUser  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,classname,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}




}

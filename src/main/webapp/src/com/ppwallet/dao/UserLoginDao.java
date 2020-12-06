package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.User;
import com.ppwallet.utilities.Utilities;

public class UserLoginDao extends HandleConnections{
	private static final long serialVersionUID = 1L;
	private static String className = UserLoginDao.class.getSimpleName();
	
	public User validateUser(String userid, String password, String usertype) throws Exception{
		// IMPORTANT NOTE: THIS METHOD NEEDS TO BE RETROFILLED FOR ALL USER TYPES FROM VARIOUS CODE SOURCES
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		User user = null;
		try{
			connection = super.getConnection();	
			 
			if(usertype.equalsIgnoreCase("C")) {
				// User type is Bank
				query = "select relationshipno userelno ,  customerid userid , customerpwd userpwd, customername username , custemail useremail, custcontact usercontact, "
						+ " expiry expirydate from customer_details where customerid=? and status=? or ?";
			}else if(usertype.equalsIgnoreCase("M")) {
				// User type is Buyer or Seller
				query = "select merchantid userid, merchantpwd userpwd, merchantname username, merchantemail useremail, merchantcontact usercontact, address1 mAddress, city mCity, status mStatus, billercode mBillercode,"
				+ " expiry expirydate from merch_details where merchantid=? and status=? ";
			}else if(usertype.equalsIgnoreCase("O")) {
				// User type is Buyer or Seller
				query = "select adminid userid, adminpwd userpwd, adminname username, accesstype accesstype, adminemail useremail, admincontact usercontact, "
						+ " expiry expirydate from admin_details where adminid=? and status=? ";
			}else if(usertype.equalsIgnoreCase("B")) {
				// User type is Buyer or Seller
				query = "select batchjobid userid, batchjobpwd userpwd, batchjobname username, batchjobemail useremail, batchjobcontact usercontact, "
						+ " expiry expirydate from batchjob_details where batchjobid=? and status=? ";
			}
			pstmt = connection.prepareStatement(query);
			if(usertype.equalsIgnoreCase("C")) {
				pstmt.setString(1, userid);
				pstmt.setString(2, "A");
				pstmt.setString(3, "P");
			}
			pstmt.setString(1, userid);
			pstmt.setString(2, "A");
			
			rs = (ResultSet)pstmt.executeQuery();

			 if(rs!=null){
				 user=new User();
				 	while(rs.next()){
				 		 
				 		if(usertype.equalsIgnoreCase("C")) {
				 			user.setRelationshipNo(StringUtils.trim(rs.getString("userelno")));
				 		}
				 		if(usertype.equalsIgnoreCase("M")) {
				 			user.setBillerCode( StringUtils.trim(rs.getString("mBillercode"))    );
				 		}
				 		if(usertype.equalsIgnoreCase("O")) {
	 					user.setUserAccess(StringUtils.trim(rs.getString("accesstype")));
				 			}
				 			user.setUserId(  StringUtils.trim(rs.getString("userid"))    );
				 			user.setUserPassword( StringUtils.trim(rs.getString("userpwd"))  );
				 			user.setUserName(StringUtils.trim(rs.getString("username"))  );
				 			user.setEmailId(StringUtils.trim(rs.getString("useremail"))  );
				 			user.setContact(StringUtils.trim(rs.getString("usercontact"))  );
				 			user.setExpiryDate(StringUtils.trim(rs.getString("expirydate"))  );
			 				user.setUserType(usertype );
				 		
				 		} // end of while
				 	} //end of if rs!=null check
			 // validate the password
			 if(user.getUserPassword().equals(Utilities.encryptString(password))==false) {
				 user=null;
				 throw new Exception("password does not match");
			 }
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method validateUser  is  "+e.getMessage());
			throw new Exception ("The exception in method validateUser  is  "+e.getMessage());			
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
		return user;	
		}
	
	
	public CustomerDetails getFullCustomerProfile(String relationshipNo, String userType) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		CustomerDetails m_CustomerDetails = null;
		try{
			connection = super.getConnection();	
			 
			if(userType.equalsIgnoreCase("C")) {
				
				 //                     1              2          3            4          5                             
				query = "select relationshipno , customerid, customername , custemail, custcontact,"    
						       + " address,  status, passportno "
						       + "from customer_details where relationshipno=? ";
			}
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			rs = (ResultSet)pstmt.executeQuery();

			 if(rs!=null){
				 m_CustomerDetails=new CustomerDetails();
				 	while(rs.next()){	 			 			
				 		m_CustomerDetails.setRelationshipNo( StringUtils.trim(rs.getString("relationshipno"))    );
				 		m_CustomerDetails.setCustomerId( StringUtils.trim(rs.getString("customerid"))    );
				 		m_CustomerDetails.setCustomerName(StringUtils.trim(rs.getString("customername"))  );
				 		m_CustomerDetails.setEmail(StringUtils.trim(rs.getString("custemail"))  );
				 		m_CustomerDetails.setContact(StringUtils.trim(rs.getString("custcontact"))  );
				 		m_CustomerDetails.setAddress(StringUtils.trim(rs.getString("address"))  );
				 		m_CustomerDetails.setStatus(StringUtils.trim(rs.getString("status"))  );
				 		m_CustomerDetails.setPassportNo(StringUtils.trim(rs.getString("passportno"))  );
				 		} // end of while
				 	} //end of if rs!=null check
			 // validate the password
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getFullCustomerProfile  is  "+e.getMessage());
			throw new Exception ("The exception in method getFullCustomerProfile  is  "+e.getMessage());			
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
		return m_CustomerDetails;
	}


	public boolean updateCustomerProfile(String relationshipNo, String custPwd, String custName, String custEmail,
			String custContact, String address) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 if(custPwd.trim().equals("")) {
			 										//		1				2		3				4				5
			 query = " update customer_details set customername=? , custemail=?, custcontact = ?, address=? where relationshipno=?";
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1,  custName); 					
					pstmt.setString(2,  custEmail); 					
					pstmt.setString(3,  custContact); 
					pstmt.setString(4,  address);
					pstmt.setString(5,  relationshipNo);
			 }else {
					//										1				2				3				4			5					6
					query = " update customer_details set customername=? , custemail=?, custcontact = ?, address=?, customerpwd=? where relationshipno=?";
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1,  custName); 					
					pstmt.setString(2,  custEmail); 					
					pstmt.setString(3,  custContact); 
					pstmt.setString(4,  address);
					pstmt.setString(5,  Utilities.encryptString(custPwd) );
					pstmt.setString(6,  relationshipNo);
									 
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
			PPWalletEnvironment.setComment(1,className,"The exception in method updateCustomerProfile  is  "+e.getMessage());
			throw new Exception ("The exception in method updateCustomerProfile  is  "+e.getMessage());
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



}

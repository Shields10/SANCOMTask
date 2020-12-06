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

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.Token;
import com.ppwallet.utilities.Utilities;

public class CustomerDao extends HandleConnections {
	
	private static String className = CustomerDao.class.getSimpleName();
	private static final long serialVersionUID = 1L;
	

	public CustomerDetails getFullCustomerProfile(String userId, String userType) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		CustomerDetails custDtls = null;
		try{
			connection = super.getConnection();	
			 
			if(userType.equalsIgnoreCase("C")) {
				
				 //                     1                  2          3            4                                        
				query = "select relationshipno ,  customername , custemail, custcontact,"    
						       + " address,       krapin,        status "
						       + "from customer_details where customerid=? ";
			}
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, userId);
			rs = (ResultSet)pstmt.executeQuery();

			 if(rs!=null){
				 custDtls=new CustomerDetails();
				 	while(rs.next()){
				 		custDtls.setRelationshipNo( StringUtils.trim(rs.getString("relationshipno")));
				 		custDtls.setCustomerName( StringUtils.trim(rs.getString("customername")));
				 		custDtls.setEmail( StringUtils.trim(rs.getString("custemail"))); 
				 		custDtls.setContact( StringUtils.trim(rs.getString("custcontact"))); 
				 		custDtls.setAddress( StringUtils.trim(rs.getString("address"))); 
				 		custDtls.setKraPIN( StringUtils.trim(rs.getString("krapin")));
				 		String status = StringUtils.trim(rs.getString("status"));
				 		if(status.contains("A")){
				 			custDtls.setStatus("Active");
				 		}else if(status.contains("I")) {
				 			custDtls.setStatus("Inactive");
				 		}else if(status.contains("P")) {
				 			custDtls.setStatus("Pending");
				 		}
				 		custDtls.setStatus( StringUtils.trim(rs.getString("status"))); 
				 		custDtls.setUserType(userType); 
				 		custDtls.setCustomerId(userId);
				 		
				 		
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
		return custDtls;
	}
	
	
	public boolean updateCustomerProfile(String custId, String custPwd, String custName,
			String custEmail, String custContact, String address, String postCode) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 if(custPwd.equals("")) {
				 //                                      1                 2                3         4
				 query = "update customer_details set  customername=?, custemail=?, custcontact=?, address=? "
					 		                          + "where customerid=? ";
			 }else {
				 //                                      1                 2                3         4            5
				 query = "update customer_details set customerpwd=?, customername=?, custemail=?, custcontact=?, address=? "
					 		+ " where customerid=? ";
			 }
			 
					pstmt = connection.prepareStatement(query);
				if(custPwd.equals("")) {	
					pstmt.setString(1, custName); 						 
					pstmt.setString(2, custEmail);						 
					pstmt.setString(3, custContact);						 
					pstmt.setString(4, address);					 
					pstmt.setString(5, custId);	
				}else {
					pstmt.setString(1, Utilities.encryptString(custPwd)); 						 
					pstmt.setString(2, custName); 						 
					pstmt.setString(3, custEmail);						 
					pstmt.setString(4, custContact);						 
					pstmt.setString(5, address);					 
					pstmt.setString(6, custId);							
				}
											 
				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}

					connection.commit();
					result = true;
		}catch(Exception e){
			result = false;
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


	public boolean registerCustomer(String nationalId, String userId, String userPwd, String userName, String userEmail,
			String userContact, String address1, String relationshipNo,  ArrayList<String> arrCustFile, String dateOfBith,
			String passportNo, String gender, String kraPin)  throws Exception  {
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		//ResultSet rs=null;
		String query = null;
		boolean result = false;

		try{
			
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
	
			 //Query 1
			 
				 	//query with national id              1             2            3             4                5       6           	7
				 query = "insert into customer_details (relationshipno, customerid, customerpwd, customername, nationalid, passportno,  gender, "
														+ "custemail,    custcontact,  address, krapin, dateofbirth, status,  expiry,  createdon ) "
														+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?,  ?,  ?,  ?,  ?,  ?, ?) ";
														//		   1  2  3  4  5  6  7	8	9  10  11  12  13  14 15
						 	
			
											
				
				 
				 	pstmt = connection.prepareStatement(query);
					pstmt.setString(1,relationshipNo ); 						
					pstmt.setString(2, userId); 
					pstmt.setString(3, Utilities.encryptString(userPwd));						
					pstmt.setString(4, userName);						
					pstmt.setString(5, nationalId);					
					pstmt.setString(6, passportNo);						 
					pstmt.setString(7, gender);						
					pstmt.setString(8, userEmail);						
					pstmt.setString(9, userContact);						
					pstmt.setString(10, address1);					
					pstmt.setString(11, kraPin);			
					pstmt.setString(12, dateOfBith);							
					pstmt.setString(13, "P");						
					pstmt.setString(14, "9999-12-31");
					pstmt.setString(15, Utilities.getMYSQLCurrentTimeStampForInsert());
						
					
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}										
					
		
					pstmt.close();
				
					
					
				if(arrCustFile!=null) {
			
							//Query 2					//       1				2		
					query= "insert into customer_kyc_docs (relationshipno, document_location ) " 
													+ " values (?,?) ";
					pstmt = connection.prepareStatement(query);
					for (int i = 0; i < arrCustFile.size(); i++) {
	                pstmt.setString(1, relationshipNo );
					pstmt.setString(2, arrCustFile.get(i) );
	                pstmt.addBatch();
	                if ((i + 1) % 10 == 0 || (i + 1) == arrCustFile.size()) {
	                	try {
	                     pstmt.executeBatch();
	                	}catch (Exception e) {
	                		throw new Exception (" failed query "+query+" "+e.getMessage());
	                	}
	                }
	            }
					
				}
				
				//create wallet 
				
				
				
												//		1         2				3		 		 4			5		    6            7         8          9
				query = "insert into wallet_details (walletid, relationshipno, walletdesc, 	usertype, 	status,   currbal,  currencyid, lastupdated, createdon)  "
				+ "  values (?, ?, ?, ?, ?, ? ,?, ?, ?)  ";
				//  1  2  3  4  5  6  7  8  9
				pstmt = connection.prepareStatement(query);
				
				SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMdd");  formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));
				String walletId = (formatter1.format(new Date()))+( RandomStringUtils.random(10, false, true)).toString();
				
				pstmt.setString(1,  walletId); 
				pstmt.setString(2, relationshipNo ); 
				pstmt.setString(3,  relationshipNo +"-" );
				pstmt.setString(4,   "C" );
				pstmt.setString(5,   "A"      );
				pstmt.setFloat(6,  Float.parseFloat("0")     );
				pstmt.setString(7, "404"); 
				pstmt.setString(8, Utilities.getMYSQLCurrentTimeStampForInsert()  ); 
				pstmt.setString(9, Utilities.getMYSQLCurrentTimeStampForInsert()   ); 
				
				
				
				try {
					pstmt.executeUpdate();
				}catch (Exception e) {
					throw new Exception (" failed query "+query+" "+e.getMessage());
				}
				
				
				
				
				pstmt.close();	
				
				PPWalletEnvironment.setComment(2,className,"4. Executed query "+query);
			
				
				
					// finally commit the connection
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback();
			result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method registerCustomer  is  "+e.getMessage());
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


	public boolean registerPersoCustomer(String userId, String userPwd, String passportNo, String userPwd2, String idNo,
			String dateOfBirth) throws Exception{
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 if(idNo.trim().length()>0) {
				 //                                      1                 2              
				 query = "update customer_details set  customerpwd=?, customerid=? "
					 		                          + "where nationalid=? ";
			 }else {
				 //                                      1                 2        
				 query = "update customer_details set customerpwd=?, customerid=? "
					 		+ " where passportno=? ";
			 }
			 
					pstmt = connection.prepareStatement(query);
				if(idNo.trim().length()>0) {	
					pstmt.setString(1, Utilities.encryptString(userPwd)); 						 
					pstmt.setString(2, userId);						 
					pstmt.setString(3, idNo);						 
					
				}else {
					pstmt.setString(1, Utilities.encryptString(userPwd)); 						 
					pstmt.setString(2, userId); 						 
					pstmt.setString(3, passportNo);							
				}
											 
				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}

					connection.commit();
					result = true;
		}catch(Exception e){
			result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method registerPersoCustomer  is  "+e.getMessage());
			throw new Exception ("The exception in method registerPersoCustomer  is  "+e.getMessage());
		}finally{
		if(connection!=null) {
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
		}
			if(pstmt!=null) pstmt.close();
		
		}
		return result;
	}
		
	}

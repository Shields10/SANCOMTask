package com.ppwallet.dao;



import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.utilities.Utilities;

public class MerchMobileDao extends HandleConnections {
	
	private static final long serialVersionUID = 2L;
	private static String className = MerchMobileDao.class.getSimpleName();

	public boolean registerMerchant(String nationalId, String userId, String userPwd, String userName, String userEmail,
			String userContact, String address1, String address2, String postCode, String companyName,
			String registrationNo, String mccCode, String billerCode, String city, ArrayList<String> arrMerchFile)  throws Exception {
		// TODO Auto-generated method stub
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		
		//ResultSet rs=null;
		String query = null;
		boolean result = false;

		try{

			connection = super.getConnection();
			connection.setAutoCommit(false);

			 //Query 1
			 								//		  1			   2			3			   4				5				6			      7			
			 query = "insert into merch_details (billercode,   merchantid,   merchantpwd,   merchantname,   nationalid,     merchantemail,      merchantcontact, "
			 									+ "address1,    address2,    pincode, 	        city,        companyname,     compregistration,   msf_plan_id,      mcccategoryid, status, expiry, createdon ) "
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
							PPWalletEnvironment.setComment(3, className, "arrMerchFile is  "+ arrMerchFile.get(i));

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
	

}

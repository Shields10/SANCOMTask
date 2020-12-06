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
import com.ppwallet.model.CallLog;
import com.ppwallet.model.DisputeTracker;
import com.ppwallet.model.Disputes;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.Token;
import com.ppwallet.model.Transaction;
import com.ppwallet.utilities.Utilities;

public class OpsMerchantManageDao extends HandleConnections{
	private static final long serialVersionUID = 1L;
	private static String className = OpsMerchantManageDao.class.getSimpleName();


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
				 		m_Merchant.setMerchantId( StringUtils.trim(rs.getString("merchantid"))   );
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
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllMerchantsBrief  is  "+e.getMessage());
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
	

	public boolean opsEditMerchDetails(String planID, String planDesc, String planDepFee, String planSetupFee,
			String planMonthlyFee, String planAnnualFee, String planStatFee, String planLatePayment, String planCycle,
			String planStatus) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 									//		1			  	2			3			4					5
			 query = " update mcc_group set mcccategoryname=?, mccfromrange=?, mcctorange=?, mccgeneric=?  where mcccategoryid = ?";

					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, planID); 					
					pstmt.setInt(2, Integer.parseInt(planDesc));	
					pstmt.setInt(3, Integer.parseInt(planDepFee));	
					pstmt.setInt(4, Integer.parseInt(planSetupFee));	
					pstmt.setInt(5, Integer.parseInt(planMonthlyFee));	
					pstmt.setString(6, planAnnualFee); 					
					pstmt.setInt(7, Integer.parseInt(planStatFee));	
					pstmt.setInt(8, Integer.parseInt(planLatePayment));	
					pstmt.setInt(9, Integer.parseInt(planCycle));	
					pstmt.setInt(10, Integer.parseInt(planStatus));	
					
					
					
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method updateMCCList  is  "+e.getMessage());
			throw new Exception ("The exception in method updateMCCList  is  "+e.getMessage());
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
	
	

	public ArrayList<Merchant> getAllMerchantsDetails() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Merchant> arrMerchantDetails = null;
		try{
			connection = super.getConnection();	

			query = "select billercode, merchantid, merchantpwd, merchantname, nationalid, merchantemail, merchantcontact, address1," 
			+ "address2, pincode, city, companyname, compregistration, msf_plan_id, mcccategoryid, expiry, status createdon from merch_details where status = '?' OR  status ='?'";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "A"); 
			pstmt.setString(2, "I"); 
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
				 		m_Merchant.setCompanyName( StringUtils.trim(rs.getString("companyname"))  );
				 		m_Merchant.setCompanyRegistration( StringUtils.trim(rs.getString("compregistration"))  );
				 		m_Merchant.setMsfPlanId( StringUtils.trim(rs.getString("msf_plan_id"))  );
				 		m_Merchant.setMccCategoryId(StringUtils.trim(rs.getString("mcccategoryid"))  );
				 		
				 		m_Merchant.setExpiryDate( StringUtils.trim(rs.getString("expiry"))  );
				 		m_Merchant.setCreatedOn( StringUtils.trim(rs.getString("createdon"))  );
				 		m_Merchant.setStatus(StringUtils.trim(rs.getString("status"))  );
				 		arrMerchantDetails.add(m_Merchant);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrMerchantDetails!=null) if(arrMerchantDetails.size()==0) arrMerchantDetails=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllMerchantsDetails()   is  "+e.getMessage());
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
		
		
	
	
	
	
	
		
		public ArrayList<CallLog> getAllMerchantCallLogs() throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			ArrayList<CallLog> arrMerchantCallLogs = null;
			try{
				connection = super.getConnection();	

				query = "select usertype, referenceno, operatorid, calldescription, comment, calledon from call_record_log where usertype =?  ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, "M");
				rs = (ResultSet)pstmt.executeQuery();			
				 if(rs!=null){
					 arrMerchantCallLogs = new ArrayList<CallLog>();
					 	while(rs.next()){	 
					 		CallLog m_CallLog=new CallLog();
					 		m_CallLog.setUserType( StringUtils.trim(rs.getString("usertype"))  );
					 		m_CallLog.setReferenceNo( StringUtils.trim(rs.getString("referenceno"))  );
					 		m_CallLog.setUserId( StringUtils.trim(rs.getString("operatorid"))  );
					 		m_CallLog.setCallDescription( StringUtils.trim(rs.getString("calldescription"))  );
					 		m_CallLog.setComment( StringUtils.trim(rs.getString("comment"))    );
					 		m_CallLog.setCalledOn( StringUtils.trim(rs.getString("calledon"))    );
					 		
					 		arrMerchantCallLogs.add(m_CallLog);
					 		} // end of while
					 	
					 	} //end of if rs!=null check
				 if(arrMerchantCallLogs!=null) if(arrMerchantCallLogs.size()==0) arrMerchantCallLogs=null;
			}catch(Exception e){
				PPWalletEnvironment.setComment(1,className,"The exception in method getAllMerchantCallLogs()   is  "+e.getMessage());
				throw new Exception ("The exception in method getAllMerchantCallLogs()   is  "+e.getMessage());			
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
			return arrMerchantCallLogs;
	
	}
		
		public boolean addNewCallLog(String userType, String referenceNo, String userId, String callDescription,
				String comment, String calledOn) throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			String query = null;
			boolean result = false;
			try{
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
				 						     	//		1			2			3			 4			5           6
				 query = "insert into call_record_log (usertype, referenceno, operatorid, calldescription, comment, calledon) "
								+ "values (?, ?, ?, ?, ?, ?) ";
								//		   1  2  3  4  5  6
				
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, userType); 					
						pstmt.setString(2, referenceNo); 	
						pstmt.setString(3, userId);
						pstmt.setString(4, callDescription);
						pstmt.setString(5, comment);
						pstmt.setString(6,  Utilities.getMYSQLCurrentTimeStampForInsert());
						pstmt.executeUpdate();
						connection.commit();
						result = true;
					
			}catch(Exception e){
				connection.rollback(); 
				result = false;
				PPWalletEnvironment.setComment(1,className,"The exception in method addNewMerchCallLog  is  "+e.getMessage());
				throw new Exception ("The exception in method addNewMerchCallLog  is  "+e.getMessage());
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
					  PPWalletEnvironment.setComment(3, className," userId"+userId+ " userPwd"+userPwd+"userName "+ userName+ "userEmail "+userEmail+"userEmail"+"userContact"+userContact
	                    		+" address1"+address1+" address2"+address2+"postCode "+postCode+"companyName"+companyName+"registrationNo"+registrationNo+"mccCode"+mccCode+"billerCode"+billerCode+"nationalId"+nationalId
	                    		+"city"+city);   
					 
					 //Query 1
					 							   	//		  1			   2			3			   4				5				6			7			
					 query = "insert into merch_details (billercode, merchantid, merchantpwd, merchantname,   nationalid, 		merchantemail,     merchantcontact, "
					 									+ "address1,    address2,  pincode, 	city,     companyname, compregistration,   msf_plan_id,  mcccategoryid, status, expiry, createdon ) "
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


		public Merchant showSpecificMerchant(String billerCode) throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			Merchant m_Merchant = null;
			try{
				connection = super.getConnection();	

								// 1			2					3				4					5
				query = "select billercode, merchantid,		 merchantname, 		nationalid, 		merchantemail, "
						+ " 	address1, 	address2, 		pincode, 			city,				companyname,"
						+ "		compregistration, 			mcccategoryid, 		status, 			expiry,"
						+ "		merchantcontact	"
						+ " from merch_details where billercode =?  ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, billerCode);
				rs = (ResultSet)pstmt.executeQuery();			
				 if(rs!=null){
					 m_Merchant  = new Merchant();
					 	while(rs.next()){	 
					 		m_Merchant.setBillerCode( StringUtils.trim(rs.getString("billercode"))  );
					 		m_Merchant.setMerchantId(StringUtils.trim(rs.getString("merchantid"))  );
					 		m_Merchant.setMerchantName( StringUtils.trim(rs.getString("merchantname"))  );
					 		m_Merchant.setNationalId( StringUtils.trim(rs.getString("nationalid"))  );
					 		m_Merchant.setEmail( StringUtils.trim(rs.getString("merchantemail"))    );
					 		m_Merchant.setAddress1(StringUtils.trim(rs.getString("address1"))  );
					 		m_Merchant.setAdress2( StringUtils.trim(rs.getString("address2"))    );
					 		m_Merchant.setPinCode( StringUtils.trim(rs.getString("pincode"))    );
					 		m_Merchant.setCompanyName( StringUtils.trim(rs.getString("companyname"))  );
					 		m_Merchant.setCompanyRegistration(StringUtils.trim(rs.getString("compregistration"))    );
					 		m_Merchant.setMccCategoryId(StringUtils.trim(rs.getString("mcccategoryid"))  );
					 		m_Merchant.setStatus( StringUtils.trim(rs.getString("status"))    );
					 		m_Merchant.setExpiryDate( StringUtils.trim(rs.getString("expiry"))    );	
					 		m_Merchant.setContact( StringUtils.trim(rs.getString("merchantcontact"))    );
					 		m_Merchant.setCity( StringUtils.trim(rs.getString("city"))    );
					 		} // end of while
				 
					 	} //end of if rs!=null check
			}catch(Exception e){
				PPWalletEnvironment.setComment(1,className,"The exception in method showSpecificMerchant()   is  "+e.getMessage());
				throw new Exception ("The exception in method showSpecificMerchant()   is  "+e.getMessage());			
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
		
			return m_Merchant;		
		
		}


		public ArrayList<String> getAllKYCDocsForMerchant(String billerCode) throws  Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			ArrayList<String> arrKYCDocsPath = null;
			try{
				connection = super.getConnection();	

				query = "select document_location from merch_kyc_docs where billercode =?  ";
				pstmt = connection.prepareStatement(query);
				pstmt.setString(1, billerCode);
				rs = (ResultSet)pstmt.executeQuery();			
				 if(rs!=null){
					 arrKYCDocsPath  = new ArrayList<String>();
					 	while(rs.next()){	 
					 		arrKYCDocsPath.add( StringUtils.trim(rs.getString("document_location"))  );
					 		} // end of while
					 	} //end of if rs!=null check
				 
				 if(arrKYCDocsPath!=null)
					 if(arrKYCDocsPath.size()==0)
						 arrKYCDocsPath=null;
			}catch(Exception e){
				PPWalletEnvironment.setComment(1,className,"The exception in method getAllKYCDocsForMerchant()   is  "+e.getMessage());
				throw new Exception ("The exception in method getAllKYCDocsForMerchant()   is  "+e.getMessage());			
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
			return arrKYCDocsPath;		
		}

//update spec ********************************************************************************************************
		public boolean updateSpecificMerchant(String billerCode, String password, String merchantName,
				String nationalId, String email, String address1, String address2, String city, String pinCode,
				String contact, String companyName, String compRegistration, String mccId, String status,
				String expiry, String verifyMerchFlag) throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			String query = null;
			ResultSet rs=null;
			//boolean hasWallet = false;
			boolean result = false;
			try{
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
				 
				 
				if(password.trim().length()>0) {
					//query 1
				 									//		1			  	2			3					4					5
				 query = " update merch_details set   merchantname=?, nationalid=?, merchantemail=? ,	merchantcontact=?, "
				 		+ "							address1=?,		address2 = ?, 		pincode=?, 			city=?, 		companyname=?,			"
				 		+ "							compregistration = ?,  mcccategoryid=?, 	status=?, expiry=?, "
				 		+ "							merchantpwd=?	where billercode = ?";
				}else {
					 query = " update merch_details set  merchantname=?, nationalid=?, merchantemail=? ,	merchantcontact=?, "
						 		+ "							address1=?,		address2 = ?, 		pincode=?, 	city=?, 	companyname=?,			"
						 		+ "							compregistration = ?,  mcccategoryid=?, 	status=?, expiry=? "
						 		+ "								where billercode = ?";
					
				}
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, merchantName); 					
						pstmt.setString(2, nationalId);	
						pstmt.setString(3, email);	
						pstmt.setString(4, contact);	
						pstmt.setString(5, address1);	
						pstmt.setString(6, address2); 					
						pstmt.setString(7, pinCode);	
						pstmt.setString(8, city);	
						pstmt.setString(9, companyName);	
						pstmt.setString(10, compRegistration);	
						pstmt.setInt(11, Integer.parseInt(mccId));	
						pstmt.setString(12, status);	
						pstmt.setString(13,expiry);	
						
						if(password.trim().length()>0) {
						pstmt.setString(14,  Utilities.encryptString(password));
						pstmt.setString(15,billerCode);	
						}else {
							pstmt.setString(14,billerCode);	
						}
						try {
							pstmt.executeUpdate();
							}catch(Exception e) {
								throw new Exception (" failed query "+query+" "+e.getMessage());
							}
						pstmt.close();	
						
						//query 2  create wallet if flag is Y
		
					if(verifyMerchFlag.equals("Y")) {
						SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMdd");  formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));
						 String walletId = (formatter1.format(new Date()))+( RandomStringUtils.random(6, false, true)).toString();	
						//System.out.println("walletId is  "+walletId);
					//((Token)arrToken.get(i)).setWalletId(walletId);
						 //                              1           2              3         4           5        6            7            8          9                             
				 query = "insert into wallet_details (walletid, relationshipno, walletdesc, usertype, 	status,   currbal,  currencyid, lastupdated, createdon)  "
						+ "  values (?, ?, ?, ?, ?, ? ,?, ?, ?)  ";
						         //  1  2  3  4  5  6  7  8  9
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, walletId   ); 
						pstmt.setString(2, billerCode  ); 
						pstmt.setString(3, billerCode+ "-1"  );
						pstmt.setString(4,   "M"  );
						pstmt.setString(5,   "A"      );
						pstmt.setFloat(6,  Float.parseFloat("0")     );
						pstmt.setString(7, "KES"   ); 
						pstmt.setString(8, Utilities.getMYSQLCurrentTimeStampForInsert()  ); 
						pstmt.setString(9, Utilities.getMYSQLCurrentTimeStampForInsert()   ); 
						
						PPWalletEnvironment.setComment(3,className," Update merchant wallet details with new wallet id"+walletId );

						try {
							pstmt.executeUpdate();
						}catch (Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
							
							}
						 pstmt.close();	
						
	
					}else {
				
						PPWalletEnvironment.setComment(3,className,"Merchant already have an existing wallet WalletId ");

					}
																			 						 
					
						connection.commit();
						result = true;
			}catch(Exception e){
				connection.rollback(); result = false;
				PPWalletEnvironment.setComment(1,className,"The exception in method updateSpecificMerchant  is  "+e.getMessage());
				throw new Exception ("The exception in method updateSpecificMerchant  is  "+e.getMessage());
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



		/*public ArrayList<Transaction> getAllTransactionDetails () throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			ArrayList<Transaction> arrTransactionDetails = null;
			
			try{
				 connection = super.getConnection();
				
				 			//		1		2			3			 4				5           6		7
				 query = "select txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode ,txndatetime  from wallet_txn_bc "
								+ "values (?, ?, ?, ?, ?, ?, ?) ";
								//		   1  2  3  4  5  6  7
				
						pstmt = connection.prepareStatement(query);
						 if(rs!=null){
							 arrTransactionDetails = new ArrayList<Transaction>();
							 	while(rs.next()){	 
							 		Transaction m_Transaction=new Transaction();
							 		m_Transaction.setTxnCode( StringUtils.trim(rs.getString("txncode"))  );
							 		m_Transaction.setWalletId( StringUtils.trim(rs.getString("walletid"))  );
							 		m_Transaction.setSystemReference( StringUtils.trim(rs.getString("sysreference"))  );
							 		m_Transaction.setTxnAmount( StringUtils.trim(rs.getString("txnamount"))  );
							 		m_Transaction.setTxnCurrencyId( StringUtils.trim(rs.getString("txncurrencyid"))    );
							 		m_Transaction.setTxnMode( StringUtils.trim(rs.getString("txnmode"))    );
							 		m_Transaction.setTxnDateTime( StringUtils.trim(rs.getString("txndatetime"))    );
							 		
							 		arrTransactionDetails.add(m_Transaction);
							 	} //end of if rs!=null check
								 if(arrTransactionDetails!=null) if(arrTransactionDetails.size()==0) arrTransactionDetails=null;
						 }catch(Exception e){
								PPWalletEnvironment.setComment(1,classname,"The exception in method getAllTransactionDetails ()   is  "+e.getMessage());
								throw new Exception ("The exception in method getAllTransactionDetails()   is  "+e.getMessage());			
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
							return arrTransactionDetails;
			
			
			
		}
}*/
		
		public Disputes getDisputeDetail(String disputeId) throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			Disputes m_Disputes = null;
			try{
				 connection = super.getConnection();	
				 
				 query = "select a.disputeid disputeid, a.transactionid transactionid, a.reasonid reasonid,"
					  		+ " a.raisedbyuser raisedbyuser, a.usertype usertype, a.status status, "
					  		+ "a.raisedondate raisedondate, a.usercomment usercomment, b.reasondesc reasondesc from dispute_details a,  "
					  		+ "dispute_reason b where a.disputeid=? and a.reasonid=b.reasonid  ";
				 
				 
				 
				 pstmt = connection.prepareStatement(query);
				 pstmt.setString(1, disputeId);
				 rs = pstmt.executeQuery();
					  if(rs!=null){	
						 	while(rs.next()){	
						 		m_Disputes = new Disputes();
						 		m_Disputes.setDisputeId( StringUtils.trim(rs.getString("disputeid"))   );
						 		m_Disputes.setTransactionId(StringUtils.trim(rs.getString("transactionid"))   );
						 		m_Disputes.setReasonId(StringUtils.trim(rs.getString("reasonid"))   );
						 		m_Disputes.setRaisedbyUserId(StringUtils.trim(rs.getString("raisedbyuser"))   );
						 		String userType = StringUtils.trim(rs.getString("usertype"));
						 		if(userType.equalsIgnoreCase("M")) {
						 			m_Disputes.setUserType("Merchant");
						 		}else if(userType.equalsIgnoreCase("C")) {
						 			m_Disputes.setUserType("Customer");
						 		}
						 		m_Disputes.setStatus(StringUtils.trim(rs.getString("status"))   );
						 		m_Disputes.setUserComment(StringUtils.trim(rs.getString("usercomment"))   );
						 		m_Disputes.setRaisedOn(StringUtils.trim(rs.getString("raisedondate")));
						 		m_Disputes.setReasonDesc(StringUtils.trim(rs.getString("reasondesc")));
						 		
						 		
						 		
						 	
					 		} // end of while						 	
					 	} //end of if rs!=null check			
			}catch(Exception e){
				PPWalletEnvironment.setComment(1,className,"The exception in method getDisputeDetail  is  "+e.getMessage());
				throw new Exception ("The exception in method getDisputeDetail  is  "+e.getMessage());
			}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
				}
				if(pstmt!=null) pstmt.close();
			}
			return m_Disputes;

		}
		
		public ArrayList<DisputeTracker> getAllDisputeTrackers(String disputeId) throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			ArrayList<DisputeTracker> aryDisputeTracker = null;
			try{
				 connection = super.getConnection();	

					  query = "select trackid, disputeid, updaterid, updatertype, updatercomment, lastupdate "
					  		+ " from dispute_tracking where disputeid=?	";
					 

						 pstmt = connection.prepareStatement(query);
						 
							 pstmt.setInt(1, Integer.parseInt(disputeId));
				 
						 rs = pstmt.executeQuery();
						  if(rs!=null){	
							  aryDisputeTracker = new ArrayList<DisputeTracker>();
							 	while(rs.next()){	
							 		DisputeTracker m_DisputeTracker = new DisputeTracker();
							 		m_DisputeTracker.setTrackingid( StringUtils.trim(rs.getString("trackid"))   );
							 		m_DisputeTracker.setDisputeId(StringUtils.trim(rs.getString("disputeid"))   );
//							 		String updaterid = StringUtils.trim(rs.getString("updaterid"));
//							 		PPWalletEnvironment.setComment(3,className,"updaterid  "+updaterid);
							 		m_DisputeTracker.setUpdaterId(StringUtils.trim(rs.getString("updaterid"))   );
							 		
							 		String updaterType = StringUtils.trim(rs.getString("updatertype"));
							 		if(StringUtils.equalsIgnoreCase(updaterType, "M")) {
							 			m_DisputeTracker.setUpdaterType("Merchant");
							 		}else if(StringUtils.equalsIgnoreCase(updaterType, "C")){
										m_DisputeTracker.setUpdaterType("Customer");
									}
							 		m_DisputeTracker.setUpdaterComment(StringUtils.trim(rs.getString("updatercomment"))   );
							 		m_DisputeTracker.setLastUpdated(StringUtils.trim(rs.getString("lastupdate"))   );
							 		aryDisputeTracker.add(m_DisputeTracker);
						 		} // end of while						 	
						 	} //end of if rs!=null check
						  if(aryDisputeTracker!=null)
							  if(aryDisputeTracker.size()==0)
								  aryDisputeTracker=null;			  
				
			}catch(Exception e){
				PPWalletEnvironment.setComment(1,className,"The exception in method getAllDisputeTrackers  is  "+e.getMessage());
				throw new Exception ("The exception in method getAllDisputeTrackers  is  "+e.getMessage());
			}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
				}
				if(pstmt!=null) pstmt.close();
			}
			return aryDisputeTracker;
		}
		
		public ArrayList<Disputes> getAllDisputes(String refNo) throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			ArrayList<Disputes> aryDisputes = null;
			try{
				 connection = super.getConnection();	
				  
				  
				  query = "select a.disputeid disputeid, a.transactionid transactionid, a.reasonid reasonid,"
					  		+ " a.raisedbyuser raisedbyuser, a.usertype usertype, a.status status, "
					  		+ "a.raisedondate raisedondate, b.reasondesc reasondesc from dispute_details a,  "
							  + "dispute_reason b where a.referenceno=? and a.reasonid=b.reasonid order by a.disputeid desc ";
				  
					
				 pstmt = connection.prepareStatement(query);
				 pstmt.setString(1, refNo);
				 rs = pstmt.executeQuery();
				  if(rs!=null){	
					  aryDisputes = new ArrayList<Disputes>();
					 	while(rs.next()){	
					 		Disputes m_Disputes = new Disputes();
					 		m_Disputes.setDisputeId( StringUtils.trim(rs.getString("disputeid"))   );
					 		m_Disputes.setTransactionId(StringUtils.trim(rs.getString("transactionid"))   );
					 		m_Disputes.setReasonId(StringUtils.trim(rs.getString("reasonid"))   );
					 		m_Disputes.setRaisedbyUserId(StringUtils.trim(rs.getString("raisedbyuser"))   );
					 		String userType = StringUtils.trim(rs.getString("usertype"));
					 		
					 		if(userType.equalsIgnoreCase("M")) {
					 			m_Disputes.setUserType("Merchant");
					 		}if (userType.equalsIgnoreCase("C")) {
								m_Disputes.setUserType("Customer");
							}
					 		
//					 		m_Disputes.setStatus(StringUtils.trim(rs.getString("status"))   );
					 		
					 		String status = StringUtils.trim(rs.getString("status"));
					 		if(status.equalsIgnoreCase("A")) {
					 			m_Disputes.setStatus("Active");
					 		}else if (status.equalsIgnoreCase("C")) {
								m_Disputes.setStatus("Closed");
							}else if(status.equalsIgnoreCase("P")) {
								m_Disputes.setStatus("In Progress");
							}
					 		
					 		m_Disputes.setRaisedOn(StringUtils.trim(rs.getString("raisedondate"))   );
					 		m_Disputes.setReasonDesc(StringUtils.trim(rs.getString("reasondesc")));
					 		aryDisputes.add(m_Disputes);
				 		} // end of while						 	
				 	} //end of if rs!=null check
				  if(aryDisputes!=null)
					  if(aryDisputes.size()==0)
						  aryDisputes=null;			  
				
			}catch(Exception e){
				PPWalletEnvironment.setComment(1,className,"The exception in method getAllDisputes  is  "+e.getMessage());
				throw new Exception ("The exception in method getAllDisputes  is  "+e.getMessage());
			}finally{
			if(connection!=null)
				try {
					super.close();
				} catch (SQLException e) {
					PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
				}
				if(pstmt!=null) pstmt.close();
			}
			return aryDisputes;
		}
		
		public boolean updateDisputeStatus(String disputeid, String status) throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			String query = null;
			boolean result = false;
			try{
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
				
				 query = "update dispute_details set status=? where disputeid=? ";
								//		               1               2   
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, status );
						pstmt.setInt(2, Integer.parseInt(disputeid));
//						pstmt.executeUpdate();
						try {
						    pstmt.executeUpdate();
								}catch(Exception e) {
								throw new Exception (" failed query "+query+" "+e.getMessage());
								  	}	
						connection.commit();			 	
						result = true;
			}catch(Exception e){
				connection.rollback();
				result = false;
				PPWalletEnvironment.setComment(1,className,"The exception in method updateDisputeStatus  is  "+e.getMessage());
				throw new Exception ("The exception in method updateDisputeStatus  is  "+e.getMessage());
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
		


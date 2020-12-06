package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.CallLog;
import com.ppwallet.model.Card;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.DisputeTracker;
import com.ppwallet.model.Disputes;
import com.ppwallet.model.Transaction;
import com.ppwallet.model.Wallet;
import com.ppwallet.utilities.Utilities;


public class OpsSystemManageCustomerDao extends HandleConnections{
	private static final long serialVersionUID = 1L;
	private static String classname = OpsSystemManageCustomerDao.class.getSimpleName();

	
	public ArrayList<DisputeTracker> getAllDisputeDetails(String userType) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<DisputeTracker> arrDispdetails = null;
		try{
			connection = super.getConnection();	

		    query =	"select a.disputeid disputeid, a.usertype usertype, a.usercomment usercomment, a.status status, a.raisedondate raisedondate, "
		    		+ " a.transactionid transactionid, a.reasonid reasonid, a.raisedbyuser raisedbyuser from dispute_details a where usertype=? order by status ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "C");
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrDispdetails = new ArrayList<DisputeTracker>();
				 	while(rs.next()){	 
				 		DisputeTracker m_DisDetails=new DisputeTracker();
				 		m_DisDetails.setDisputeId( StringUtils.trim(rs.getString("disputeid"))    );
				 		m_DisDetails.setUserType(StringUtils.trim(rs.getString("usertype")) );
				 		m_DisDetails.setUserComment( StringUtils.trim(rs.getString("usercomment"))    );
				 		m_DisDetails.setStatus( StringUtils.trim(rs.getString("status"))            );
				 		m_DisDetails.setRaisedOn( StringUtils.trim(rs.getString("raisedondate"))    );
				 		m_DisDetails.setTransactionId( StringUtils.trim(rs.getString("transactionid"))  );
				 		m_DisDetails.setReasonId( StringUtils.trim(rs.getString("reasonid"))    );
				 		m_DisDetails.setRaisedbyUserId( StringUtils.trim(rs.getString("raisedbyuser"))    );
				 		
				 		
				 		arrDispdetails.add(m_DisDetails);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 
			 
				
			 if(arrDispdetails!=null)
				 if(arrDispdetails.size()==0) 
					 arrDispdetails=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(3,classname,"The exception in method getAllDisputeDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllDisputeDetails  is  "+e.getMessage());			
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
		return arrDispdetails;
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
					pstmt.executeUpdate();
					connection.commit();			 	
					result = true;
		}catch(Exception e){
			connection.rollback();
			result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method updateDisputeStatus  is  "+e.getMessage());
			throw new Exception ("The exception in method updateDisputeStatus  is  "+e.getMessage());
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
	
	public ArrayList<Card> 	getAllCards() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Card> arrCardDetails = null;
		try{
			connection = super.getConnection();	

			query = " select cardno, templateId, product_id, bin, cycleday, doe, cvv2, blockcode_id, createdon from card_details ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrCardDetails = new ArrayList<Card>();
				 	while(rs.next()){	 
				 		Card m_CardDetails=new Card();
				 		m_CardDetails.setCardNumber( StringUtils.trim(rs.getString("cardno"))    );
				 		m_CardDetails.setTemplateId( StringUtils.trim(rs.getString("templateId"))  );
				 		m_CardDetails.setProductId( StringUtils.trim(rs.getString("product_id"))    );
				 		m_CardDetails.setBin( StringUtils.trim(rs.getString("bin"))    );
				 		m_CardDetails.setCycleDay( StringUtils.trim(rs.getString("cycleday"))    );
				 		m_CardDetails.setDoe( StringUtils.trim(rs.getString("doe"))    );
				 		m_CardDetails.setCvv2( StringUtils.trim(rs.getString("cvv2"))    );
				 		m_CardDetails.setBlockCodeId( StringUtils.trim(rs.getString("blockcode_id"))    );
						m_CardDetails.setCreatedOn( StringUtils.trim(rs.getString("createdon"))    );
				 		arrCardDetails.add(m_CardDetails);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrCardDetails!=null)
				 if(arrCardDetails.size()==0) 
					 arrCardDetails=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllCards  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllCards  is  "+e.getMessage());			
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
		return arrCardDetails;
	}

	public ArrayList<CustomerDetails> getAllPendingCustomers() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<CustomerDetails> arrPendingCust = null;
		try{
			connection = super.getConnection();	

			query = "select relationshipno, customerid, nationalid, customername, custemail, custcontact, address, krapin, "
					+ "status, createdon, expiry from customer_details where status = ?";
		
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "I");
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrPendingCust = new ArrayList<CustomerDetails>();
				 	while(rs.next()){	 
				 		CustomerDetails c_PendingCustomer=new CustomerDetails();
				 		c_PendingCustomer.setRelationshipNo( StringUtils.trim(rs.getString("relationshipno"))    );
				 		c_PendingCustomer.setCustomerId( StringUtils.trim(rs.getString("customerid"))    );
				 		c_PendingCustomer.setNationalId( StringUtils.trim(rs.getString("nationalid"))    );
				 		c_PendingCustomer.setCustomerName( StringUtils.trim(rs.getString("customername"))    );
				 		c_PendingCustomer.setEmail( StringUtils.trim(rs.getString("custemail"))    );
				 		c_PendingCustomer.setContact( StringUtils.trim(rs.getString("custcontact"))    );
				 		c_PendingCustomer.setAddress( StringUtils.trim(rs.getString("address"))    );
				 		c_PendingCustomer.setKraPIN(StringUtils.trim(rs.getString("krapin"))    );
				 		c_PendingCustomer.setStatus( StringUtils.trim(rs.getString("status"))    );
				 		c_PendingCustomer.setCreatedOn( StringUtils.trim(rs.getString("createdon"))    );
				 		c_PendingCustomer.setExpiry( StringUtils.trim(rs.getString("expiry"))    );
						arrPendingCust.add(c_PendingCustomer);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrPendingCust!=null)
				 if(arrPendingCust.size()==0) 
					 arrPendingCust=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllPendingCustomers  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllPendingCustomers  is  "+e.getMessage());			
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
		return arrPendingCust;
	}

	public boolean verifyPendingCust(String relNO, String status)throws Exception {
		PreparedStatement pstmt=null; Connection connection = null; String query = null;
		  
		  
		  boolean result = false; 
		  try{
			  connection = super.getConnection();
			  connection.setAutoCommit(false);
			
			  PPWalletEnvironment.setComment(3, classname,  "relNO"+ relNO + "status" + status);
			  									// 	1			 2 	 
			  query = "update customer_details set status=? where  relationshipno = ? ";
							
		  
		  pstmt = connection.prepareStatement(query); 
		  
		
		  pstmt.setString(1, status);
		  pstmt.setString(2, relNO);
		  pstmt.executeUpdate(); 
		  connection.commit();
		  result = true;
		  
		  }catch(Exception e){
			  connection.rollback(); result = false;
		  PPWalletEnvironment.setComment(1, classname,"The exception in method verifyPendingCust  is  "+e.getMessage());
		  throw new Exception  ("The exception in method verifyPendingCust  is  "+e.getMessage());
		  }finally{
		  if(connection!=null) 
			  try { super.close(); } 
		  catch (SQLException e) {
		  PPWalletEnvironment.setComment(1,classname,"SQL Exception is  "+e.getMessage()); } 
		  if(pstmt!=null) pstmt.close(); } 
		return result;
	}
	
	
	public ArrayList<CustomerDetails> getAllCustomers() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<CustomerDetails> arrAllCust = null;
		try{
			connection = super.getConnection();	

			query = " select relationshipno, customerid, customerpwd, customername, nationalid, passportno, gender, custemail, custcontact, address, krapin, dateofbirth, status, expiry, createdon  from customer_details ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrAllCust = new ArrayList<CustomerDetails>();
				 	while(rs.next()){	 
				 		CustomerDetails c_CustomerDetails=new CustomerDetails();
				 		c_CustomerDetails.setRelationshipNo( StringUtils.trim(rs.getString("relationshipno"))    );
				 		c_CustomerDetails.setCustomerId( StringUtils.trim(rs.getString("customerid"))    );
				 		c_CustomerDetails.setPassword( StringUtils.trim(rs.getString("customerpwd"))    );
				 		c_CustomerDetails.setCustomerName( StringUtils.trim(rs.getString("customername"))    );
				 		c_CustomerDetails.setNationalId( StringUtils.trim(rs.getString("nationalid"))  );
				 		c_CustomerDetails.setPassportNo(StringUtils.trim(rs.getString("passportno"))    );
				 		c_CustomerDetails.setGender( StringUtils.trim(rs.getString("gender"))    );	 		
				 		c_CustomerDetails.setEmail( StringUtils.trim(rs.getString("custemail"))    );
				 		c_CustomerDetails.setContact( StringUtils.trim(rs.getString("custcontact"))    );
				 		c_CustomerDetails.setAddress( StringUtils.trim(rs.getString("address"))    );
				 		c_CustomerDetails.setKraPIN(StringUtils.trim(rs.getString("krapin"))    );
				 		c_CustomerDetails.setDateOfBirth(StringUtils.trim(rs.getString("dateofbirth"))    );
				 		c_CustomerDetails.setStatus( StringUtils.trim(rs.getString("status"))    );
				 		c_CustomerDetails.setExpiry( StringUtils.trim(rs.getString("expiry"))    );
				 		c_CustomerDetails.setCreatedOn( StringUtils.trim(rs.getString("createdon"))    );
				 		arrAllCust.add(c_CustomerDetails);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrAllCust!=null)
				 if(arrAllCust.size()==0) 
					 arrAllCust=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllCustomers  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllCustomers  is  "+e.getMessage());			
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
		return arrAllCust;
	}
	
	
	
	//**************Get Customer Wallets
	public ArrayList<Wallet> getAllCustomerWallet(String custRelationshipNo) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Wallet> arrAllCustWallets = null;
		try{
			connection = super.getConnection();	

			query = " select walletid, relationshipno, currbal,  lastupdated  from waLLet_details  where walletid in (select walletid from wallet_customer_rel where relationshipno =?  and status  =?) ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1,custRelationshipNo );
			pstmt.setString(2,"A" );

			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrAllCustWallets = new ArrayList<Wallet>();
				 	while(rs.next()){	 
				 		Wallet m_Wallet = new Wallet();
				 		m_Wallet.setWalletId(StringUtils.trim(rs.getString("walletid"))    );
				 		m_Wallet.setCurrentBalance(StringUtils.trim(rs.getString("currbal"))    );
				 		m_Wallet.setRelationshipNo(StringUtils.trim(rs.getString("relationshipno"))    );
				 		m_Wallet.setLastUpdated(StringUtils.trim(rs.getString("lastupdated"))    );
				 		arrAllCustWallets.add(m_Wallet);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrAllCustWallets!=null)
				 if(arrAllCustWallets.size()==0) 
					 arrAllCustWallets=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllCustomers  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllCustomers  is  "+e.getMessage());			
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
		return arrAllCustWallets;
	}
	
	
	
	
	
	public boolean updateCustomerDetails(String relationshipNo, String customerId, String Password,
			String customerName, String nationalId, String passportNo, String gender, String email, String contact,
			String address, String pinCode, String dateOfBirth, String status, String expiry, String createdOn) throws Exception {
		
	
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			/* PPWalletEnvironment.setComment(3,classname, "customerId " +  customerId  +  "Password  " + Password 
					 + "customerName "+ customerName  +"nationalId "+ nationalId + "passportNo " + passportNo  +"gender " + gender+ "email " + email
					 + "contact " + contact + "address  "+address  +"pinCode "+ pinCode + "dateOfBirth "+  dateOfBirth+  "expiry "+ expiry +  "status "+ status + 
					 "createdOn "+ createdOn+ "relationshipNo "+ relationshipNo);*/
			 
			 									//		1			  	2			3			4				5			6				7           8           9          10         11            12       13          14                  15
			 query = " update customer_details set customerid = ?,customerpwd = ?, customername= ? , nationalid = ? , passportno = ?, gender = ?, custemail = ? , custcontact = ? , address = ? , krapin=?, dateofbirth = ?, status = ?, expiry = ?, createdon = ? where relationshipno = ?";

					pstmt = connection.prepareStatement(query);
					
					
					pstmt.setString(1, customerId);	
					pstmt.setString(2, Password);
					pstmt.setString(3, customerName);	
					pstmt.setString(4, nationalId);
					pstmt.setString(5, passportNo);
					pstmt.setString(6, gender);
					pstmt.setString(7, email);	
					pstmt.setString(8, contact);	
					pstmt.setString(9, address);	
					pstmt.setString(10, pinCode);	
					pstmt.setString(11, dateOfBirth);
					pstmt.setString(12, status);
					pstmt.setString(13, expiry);
					pstmt.setString(14, Utilities.getMYSQLCurrentTimeStampForInsert());
					pstmt.setString(15, relationshipNo);
					
					
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method updateCustomerDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method updateCustomerDetails  is  "+e.getMessage());
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
	

	public ArrayList<DisputeTracker> getDisputeTrackerDetails(String disputeId) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<DisputeTracker> arrDisputeTracker = null;
		try{
			connection = super.getConnection();	

			query = " select    b.trackid trackid, b.updaterid updaterid,  b.updatertype updatertype, b.updatercomment updatercomment,   " + 
					"		      b.lastupdate lastupdate from dispute_details a, dispute_tracking b where b.disputeid=?   ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1,disputeId );
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrDisputeTracker = new ArrayList<DisputeTracker>();
				 	while(rs.next()){	 
				 		DisputeTracker m_DisputeTracker=new DisputeTracker();
				 		m_DisputeTracker.setTrackingId(StringUtils.trim(rs.getString("trackid"))    );
				 		m_DisputeTracker.setUpdaterId( StringUtils.trim(rs.getString("updaterid"))  );
				 		m_DisputeTracker.setUpdaterType( StringUtils.trim(rs.getString("updatertype"))    );
				 		m_DisputeTracker.setUserComment( StringUtils.trim(rs.getString("updatercomment"))    );
				 		arrDisputeTracker.add(m_DisputeTracker);
				 		
						PPWalletEnvironment.setComment(2,classname,"dispute tracke array stuff  "+ arrDisputeTracker);

				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrDisputeTracker!=null)
				 if(arrDisputeTracker.size()==0) 
					 arrDisputeTracker=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getDisputeTrackerDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getDisputeTrackerDetails  is  "+e.getMessage());			
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
		return arrDisputeTracker;
	}
	
	public boolean addNewDispute(String transactionId, String userId, String userType, String comment, String reasonId, String refNo, String status, String disputeId, String raisedOn) throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			String query = null;
			boolean result = false;
			try{
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
				 
				 // query  2                              1               2          3            4          5              6        7        8			9
				query = "insert into dispute_details (disputeid, transactionid, reasonid, raisedbyuser, usertype, referenceno, usercomment, status, raisedondate ) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?, UTC_TIMESTAMP) ";
						//		   1  2  3  4	5  6  7 8		9
				pstmt = connection.prepareStatement(query);
				if(transactionId.length()==0)
					pstmt.setInt(1, 0);
				else
				pstmt.setInt(1, Integer.parseInt(disputeId));
				pstmt.setInt(2, Integer.parseInt(transactionId));
				pstmt.setString(3, reasonId);
				pstmt.setString(4, userId);
				pstmt.setString(5, userType);
				pstmt.setString(6, refNo);
				pstmt.setString(7, comment);
				pstmt.setString(8, status);
				pstmt.setString(9, raisedOn);
				pstmt.executeUpdate();
				connection.commit();			 	
				result = true;
				
			}catch(Exception e){
				connection.rollback();
				result = false;
				PPWalletEnvironment.setComment(1,classname,"The exception in method addNewDispute  is  "+e.getMessage());
				throw new Exception ("The exception in method addNewDispute  is  "+e.getMessage());
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


	public ArrayList<CallLog> getAllCustomerCallLogs() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<CallLog> arrCustCallLogs = null;
		try{
			connection = super.getConnection();	

			query = "select usertype, referenceno, operatorid, calldescription, comment, calledon from call_record_log where usertype =? ";
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "C");
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrCustCallLogs = new ArrayList<CallLog>();
				 	while(rs.next()){	 
				 		CallLog m_CallLog=new CallLog();
				 		m_CallLog.setUserType( StringUtils.trim(rs.getString("usertype"))  );
				 		m_CallLog.setReferenceNo( StringUtils.trim(rs.getString("referenceno"))  );
				 		m_CallLog.setUserId( StringUtils.trim(rs.getString("operatorid"))  );
				 		m_CallLog.setCallDescription( StringUtils.trim(rs.getString("calldescription"))  );
				 		m_CallLog.setComment( StringUtils.trim(rs.getString("comment"))    );
				 		m_CallLog.setCalledOn( StringUtils.trim(rs.getString("calledon"))    );
				 		
				 		arrCustCallLogs.add(m_CallLog);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrCustCallLogs!=null) if(arrCustCallLogs.size()==0) arrCustCallLogs=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllCustomerCallLogs()   is  "+e.getMessage());
			throw new Exception ("The exception in method getAllCustomerCallLogs()   is  "+e.getMessage());			
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
		return arrCustCallLogs;
	}


	


	public ArrayList<Wallet> getWalletDetails(String relationshipNo) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Wallet> arrWallet = null;
		
		try{
			connection = super.getConnection();	
			                    //  1         2           3          4        5        6           7              8
				query = "select walletid, relationshipno, walletdesc, usertype,  status, currbal, currencyid,  lastupdated "
						+ " from wallet_details   "
						+ "  where relationshipno=?   ";
		  
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, relationshipNo);
			//pstmt.setString(2, "A");
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 arrWallet = new ArrayList<Wallet>();
				 	while(rs.next()){	 
				 		Wallet wallet=new Wallet();
				 		wallet.setWalletId( StringUtils.trim(rs.getString("walletid"))    );
				 		wallet.setUserType( StringUtils.trim(rs.getString("usertype"))  );
				 		wallet.setRelationshipNo( StringUtils.trim(rs.getString("relationshipno"))  );
				 		wallet.setWalletDesc(StringUtils.trim(rs.getString("walletdesc"))  );
				 		wallet.setCurrentBalance(StringUtils.trim(rs.getString("currbal"))  );
				 		wallet.setCurrencyId(StringUtils.trim(rs.getString("currencyid"))  );
				 		wallet.setStatus(StringUtils.trim(rs.getString("status"))  );
				 		wallet.setLastUpdated(StringUtils.trim(rs.getString("lastupdated"))  );
				 		arrWallet.add(wallet);
				 		} 	
				 	PPWalletEnvironment.setComment(2,classname,"Array wallet size is "+arrWallet.size());
				 	} 
			 if(arrWallet!=null)
				 if(arrWallet.size()==0)
					 arrWallet=null;
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getWalletDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getWalletDetails  is  "+e.getMessage());			
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
		return arrWallet;
	}


	public ArrayList<Transaction> getAllTransactionsForWallet(String walletId) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Transaction> arrTransaction = null;
		try{
			connection = super.getConnection();	

				query = "select  txncode, walletid, sysreference, txnamount, txncurrencyid,  txnmode,txndatetime "
						+ " from wallet_txn_bc where walletid=? order by txndatetime desc limit 100  ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, walletId);
			rs = (ResultSet)pstmt.executeQuery();
			 if(rs!=null){
				 arrTransaction = new ArrayList<Transaction>();
				 
				 	while(rs.next()){
				 		Transaction m_Transaction=new Transaction();
				 		
				 		m_Transaction.setTxnCode( StringUtils.trim(rs.getString("txncode"))  );
				 		m_Transaction.setWalletId(StringUtils.trim(rs.getString("walletid"))  );
				 		m_Transaction.setSystemReference(StringUtils.trim(rs.getString("sysreference"))  );
				 		m_Transaction.setTxnAmount(StringUtils.trim(rs.getString("txnamount"))  );
				 		m_Transaction.setTxnCurrencyId(StringUtils.trim(rs.getString("txncurrencyid"))  );
				 		m_Transaction.setTxnMode(StringUtils.trim(rs.getString("txnmode"))  );
				 		m_Transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndatetime"))  );
				 		
				 		arrTransaction.add(m_Transaction);
				 		} 			 	
				 	} 
			 if(arrTransaction!=null)
				 if(arrTransaction.size()==0)
					 arrTransaction=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllTransactionsForWallet  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllTransactionsForWallet  is  "+e.getMessage());			
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
		
		return arrTransaction;
	}
	
	
}
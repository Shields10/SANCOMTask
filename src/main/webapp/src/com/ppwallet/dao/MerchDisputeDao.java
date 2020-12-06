package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.DisputeReasons;
import com.ppwallet.model.DisputeTracker;
import com.ppwallet.model.Disputes;
import com.ppwallet.utilities.Utilities;

public class MerchDisputeDao extends HandleConnections{
	
	private static final long serialVersionUID = 1L;
	private static String className = MerchDisputeDao.class.getSimpleName();
	
	public ArrayList<DisputeReasons> getAllDisputeReasons(String userType) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<DisputeReasons> aryReasons = null;
		try{
			
			 connection = super.getConnection();	 
			  query = "select reasonid, reasondesc, status from dispute_reason where usertype=? order by reasonid ";
					 pstmt = connection.prepareStatement(query);
					 pstmt.setString(1, userType);
					 rs = pstmt.executeQuery();
					  if(rs!=null){	
						  aryReasons = new ArrayList<DisputeReasons>();
						 	while(rs.next()){	
						 		DisputeReasons m_DisputeReasons = new DisputeReasons();
						 		m_DisputeReasons.setDisputeReasonId( StringUtils.trim(rs.getString("reasonid"))   ); 
						 		m_DisputeReasons.setDisputeReasonDesc(StringUtils.trim(rs.getString("reasondesc"))   );
						 		m_DisputeReasons.setStatus(StringUtils.trim(rs.getString("status"))   );
						 		aryReasons.add(m_DisputeReasons);
						 		
						 		PPWalletEnvironment.setComment(3,className," reasondesc is "+ m_DisputeReasons.getDisputeReasonDesc());
					 		} // end of while						 	
					 	} //end of if rs!=null check
					  if(aryReasons!=null)
						  if(aryReasons.size()==0)
							  aryReasons=null;			  
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllDisputeReasons  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllDisputeReasons  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close(); if(rs!=null) rs.close();
		}

		return aryReasons;	
		
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
				 		
//				 		m_Disputes.setStatus(StringUtils.trim(rs.getString("status"))   );
				 		
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
			if(pstmt!=null) pstmt.close(); if(rs!=null) rs.close();
		}
		return aryDisputes;
	}
	
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
	public boolean addCommentOnADispute(String disputeId, String userId, String userType, String comment) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);

			 query = "insert into dispute_tracking (disputeid, updaterid, updatertype, updatercomment, lastupdate ) "
							+ "values (?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5
					pstmt = connection.prepareStatement(query);
					pstmt.setInt(1, Integer.parseInt(disputeId));
					pstmt.setString(2, userId);
					pstmt.setString(3, userType);
					
					pstmt.setString(4, comment);
					pstmt.setString(5, Utilities.getMYSQLCurrentTimeStampForInsert());
					pstmt.executeUpdate();
					connection.commit();			 	
					  result = true;
		}catch(Exception e){
			connection.rollback();
			result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method addCommentOnADispute  is  "+e.getMessage());
			throw new Exception ("The exception in method addCommentOnADispute  is  "+e.getMessage());
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
//						 		String updaterid = StringUtils.trim(rs.getString("updaterid"));
//						 		PPWalletEnvironment.setComment(3,className,"updaterid  "+updaterid);
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
	
	public boolean addNewDispute(String transactionId, String userId, String userType, String comment,
			String reasonId, String refNo, String status) throws Exception{
			PreparedStatement pstmt=null;
			Connection connection = null;
			String query = null;
			boolean result = false;
			try{
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
				 
				 // query  2                              1               2          3            4          5              6        7        8
				query = "insert into dispute_details (transactionid, reasonid, raisedbyuser, usertype, referenceno, usercomment, status, raisedondate ) "
						+ "values (?, ?, ?, ?, ?, ?, ?, ?) ";
						//		   1  2  3  4	5  6  7 8
				pstmt = connection.prepareStatement(query);
				if(transactionId.length()==0)
					pstmt.setInt(1, 0);
				else
				pstmt.setString(1, transactionId);
				pstmt.setString(2, reasonId);
				pstmt.setString(3, userId);
				pstmt.setString(4, userType);
				pstmt.setString(5, refNo);
				pstmt.setString(6, comment);
				pstmt.setString(7, status);
				pstmt.setString(8, Utilities.getMYSQLCurrentTimeStampForInsert());
				pstmt.executeUpdate();
				connection.commit();			 	
				result = true;
					
					
						
			}catch(Exception e){
				connection.rollback();
				result = false;
				PPWalletEnvironment.setComment(1,className,"The exception in method addNewDispute  is  "+e.getMessage());
				throw new Exception ("The exception in method addNewDispute  is  "+e.getMessage());
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
	
	
	
	
              //mobile********************************************************************************
	
	
	public ArrayList<DisputeReasons> getAllMobileDisputeReasons(String userType) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<DisputeReasons> aryReasons = null;
		try{
			
			 connection = super.getConnection();	 
			  query = "select reasonid, reasondesc, status from dispute_reason where usertype=? order by reasonid ";
					 pstmt = connection.prepareStatement(query);
					 pstmt.setString(1, userType);
					 rs = (ResultSet)pstmt.executeQuery();
					  if(rs!=null){	
						  aryReasons = new ArrayList<DisputeReasons>();
						 	while(rs.next()){	
						 		DisputeReasons m_DisputeReasons = new DisputeReasons();
						 		m_DisputeReasons.setDisputeReasonId( StringUtils.trim(rs.getString("reasonid"))   ); 
						 		m_DisputeReasons.setDisputeReasonDesc(StringUtils.trim(rs.getString("reasondesc"))   );
						 		m_DisputeReasons.setStatus(StringUtils.trim(rs.getString("status"))   );
						 		aryReasons.add(m_DisputeReasons);
						 		
						 		PPWalletEnvironment.setComment(3,className," reasondesc is "+ m_DisputeReasons.getDisputeReasonDesc());
					 		} // end of while						 	
					 	} //end of if rs!=null check
					  if(aryReasons!=null)
						  if(aryReasons.size()==0)
							  aryReasons=null;			  
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllMobileDisputeReasons  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllMobileDisputeReasons  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close();
		}

		return aryReasons;	
		
	}
	

	public boolean addMobileNewDispute(String transactionId, String userId, String userType, String comment,
		String reasonId, String refNo, String status) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
			 // query  2                              1               2          3            4          5              6        7        8
			query = "insert into dispute_details (transactionid, reasonid, raisedbyuser, usertype, referenceno, usercomment, status, raisedondate ) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?) ";
					//		   1  2  3  4	5  6  7 8
			pstmt = connection.prepareStatement(query);
			if(transactionId.length()==0)
				pstmt.setInt(1, 0);
			else
			pstmt.setString(1, transactionId);
			pstmt.setString(2, reasonId);
			pstmt.setString(3, userId);
			pstmt.setString(4, userType);
			pstmt.setString(5, refNo);
			pstmt.setString(6, comment);
			pstmt.setString(7, status);
			pstmt.setString(8, Utilities.getMYSQLCurrentTimeStampForInsert());
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
			PPWalletEnvironment.setComment(1,className,"The exception in method addMobileNewDispute  is  "+e.getMessage());
			throw new Exception ("The exception in method addMobileNewDispute  is  "+e.getMessage());
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
	
	
	public ArrayList<Disputes> getAllMobileDisputes(String refNo) throws Exception{
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
				 		
				 		m_Disputes.setStatus(StringUtils.trim(rs.getString("status"))   );
				 		
					/*
					 * String status = StringUtils.trim(rs.getString("status"));
					 * if(status.equalsIgnoreCase("A")) { }else if (status.equalsIgnoreCase("C")) {
					 * }else if(status.equalsIgnoreCase("P")) { }
					 */
				 		
				 		m_Disputes.setRaisedOn(StringUtils.trim(rs.getString("raisedondate"))   );
				 		m_Disputes.setReasonDesc(StringUtils.trim(rs.getString("reasondesc")));
				 		aryDisputes.add(m_Disputes);
			 		} // end of while						 	
			 	} //end of if rs!=null check
			  if(aryDisputes!=null)
				  if(aryDisputes.size()==0)
					  aryDisputes=null;			  
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllMobileDisputes  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllMobileDisputes  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();
		}
		return aryDisputes;
	}
	
	public Disputes getMobileDisputeDetail(String disputeId) throws Exception{
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
			PPWalletEnvironment.setComment(1,className,"The exception in method getMobileDisputeDetail  is  "+e.getMessage());
			throw new Exception ("The exception in method getMobileDisputeDetail  is  "+e.getMessage());
		}finally{
		if(connection!=null)
			try {
				super.close();
			} catch (SQLException e) {
				PPWalletEnvironment.setComment(1,className,"SQL Exception is  "+e.getMessage());
			}
			if(pstmt!=null) pstmt.close();	if(rs!=null) rs.close();
		}
		return m_Disputes;

	}
		
	public boolean addMobileCommentOnADispute(String disputeId, String userId, String userType, String comment) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
		//	PPWalletEnvironment.setComment(3,className,"Tunafika hapa");

			 connection = super.getConnection();
			 connection.setAutoCommit(false);

			 query = "insert into dispute_tracking (disputeid, updaterid, updatertype, updatercomment, lastupdate ) "
							+ "values (?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5
					pstmt = connection.prepareStatement(query);
					pstmt.setInt(1, Integer.parseInt(disputeId));
					pstmt.setString(2, userId);
					pstmt.setString(3, userType);
					
					pstmt.setString(4, comment);
					pstmt.setString(5, Utilities.getMYSQLCurrentTimeStampForInsert());
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
					}
					connection.commit();			 	
					  result = true;
						//PPWalletEnvironment.setComment(3,className,"result is here");

		}catch(Exception e){
			connection.rollback();
			result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method addMobileCommentOnADispute  is  "+e.getMessage());
			throw new Exception ("The exception in method addMobileCommentOnADispute  is  "+e.getMessage());
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
	
	public ArrayList<DisputeTracker> getAllMobileDisputeTrackers(String disputeId) throws Exception {
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
//						 		String updaterid = StringUtils.trim(rs.getString("updaterid"));
//						 		PPWalletEnvironment.setComment(3,className,"updaterid  "+updaterid);
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
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllMobileDisputeTrackers  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllMobileDisputeTrackers  is  "+e.getMessage());
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

	
}

package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.AuditTrail;

public class SystemUtilsDao extends HandleConnections{
	private static final long serialVersionUID = 1L;
	private static String className = SystemUtilsDao.class.getSimpleName();

	public boolean addAuditTrail(String userId, String userType, String moduleCode, String comment) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 								//		1		2			3			4						
			 query = "insert into audit_trail 	(userid, usertype, modulecode, comment, trailtime) "
							+ "values (?, ?, ?, ?, UTC_TIMESTAMP) ";
							//		   1  2  3  4  	  
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, userId); 					
					pstmt.setString(2, userType); 					
					pstmt.setString(3, moduleCode);					
					pstmt.setString(4, comment);					
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,className,"The exception in method addAuditTrail  is  "+e.getMessage());
			throw new Exception ("The exception in method addAuditTrail  is  "+e.getMessage());
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
	
	public ArrayList<AuditTrail> getAllAuditTrails() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<AuditTrail> arrAudittrail = null;
		try{
			connection = super.getConnection();	

			query = "select trailid, userid, usertype, modulecode, comment, trailtime from audit_trail order by  trailtime ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrAudittrail = new ArrayList<AuditTrail>();
				 	while(rs.next()){	 
				 		AuditTrail m_AuditTrail=new AuditTrail();
				 		m_AuditTrail.setTraiId( StringUtils.trim(rs.getString("trailid"))    );
				 		m_AuditTrail.setUserId( StringUtils.trim(rs.getString("userid"))  );
				 		m_AuditTrail.setUserType( StringUtils.trim(rs.getString("usertype"))  );
				 		m_AuditTrail.setModuleCode( StringUtils.trim(rs.getString("modulecode"))    );
				 		m_AuditTrail.setComment( StringUtils.trim(rs.getString("comment"))    );
				 		m_AuditTrail.setTrailTime( StringUtils.trim(rs.getString("trailtime"))    );
				 		
				 		arrAudittrail.add(m_AuditTrail);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrAudittrail!=null) if(arrAudittrail.size()==0) arrAudittrail=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getAllAuditTrails  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllAuditTrails  is  "+e.getMessage());			
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
		return arrAudittrail;
	}

}

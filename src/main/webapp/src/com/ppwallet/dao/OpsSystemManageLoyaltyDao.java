package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.Loyalty;
import com.ppwallet.model.LoyaltyRules;
import com.ppwallet.utilities.Utilities;

public class OpsSystemManageLoyaltyDao extends HandleConnections {
	private static final long serialVersionUID = 1L;
	private static String classname = OpsSystemManageLoyaltyDao.class.getSimpleName();

	public ArrayList<LoyaltyRules> getAllLoyaltyRules() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<LoyaltyRules> arrLoyaltyRules = null;
		try{
			connection = super.getConnection();	

		    query =	"select paymode, rulesdesc, pointsconversion, cryptoconversion, status from loyalty_rules";
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrLoyaltyRules = new ArrayList<LoyaltyRules>();
				 	while(rs.next()){	 
				 		LoyaltyRules m_LoyaltyRules=new LoyaltyRules();
				 		m_LoyaltyRules.setPayMode( StringUtils.trim(rs.getString("paymode"))   ) ;
				 		m_LoyaltyRules.setRuleDesc(StringUtils.trim(rs.getString("rulesdesc")) );
				 		m_LoyaltyRules.setPointsConvertRatio( StringUtils.trim(rs.getString("pointsconversion"))    );
				 		m_LoyaltyRules.setCryptoConvertRatio( StringUtils.trim(rs.getString("cryptoconversion"))            );
				 		m_LoyaltyRules.setStatus( StringUtils.trim(rs.getString("status"))    );				 		
				 		arrLoyaltyRules.add(m_LoyaltyRules);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 
			 
				
			 if(arrLoyaltyRules!=null)
				 if(arrLoyaltyRules.size()==0) 
					 arrLoyaltyRules=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(3,classname,"The exception in method getAllLoyaltyRules  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllLoyaltyRules  is  "+e.getMessage());			
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
		return arrLoyaltyRules;
	}
	
	public boolean editLoyaltyRule(String payMode, String rulesDesc, String pointsConversion, String cryptoConversion,
			String status) throws Exception {	
	
		PreparedStatement pstmt=null; Connection connection = null; String query = null;
		  
		  
		  boolean result = false; 
		  try{
			  connection = super.getConnection();
			  connection.setAutoCommit(false);
			  PPWalletEnvironment.setComment(3, classname,  "Before inserting into db"); 
			  PPWalletEnvironment.setComment(3, classname,  "payMode"+ payMode + "rulesDesc" + rulesDesc +" pointsConversion" + pointsConversion + "cryptoConversion" + cryptoConversion + "status" + status);
			  									// 	1			 2 				3 				4			          5		 
			  query = "update loyalty_rules set rulesdesc=?, pointsconversion=?, cryptoconversion=?, status= ? where  paymode=? ";
		 
		  pstmt = connection.prepareStatement(query); 
		  
		  pstmt.setString(1, rulesDesc);
		  pstmt.setString(2, pointsConversion);
		  pstmt.setString(3, cryptoConversion); 
		  pstmt.setString(4, status);
		  pstmt.setString(5, payMode);
		  pstmt.executeUpdate(); 
		  connection.commit();
		  result = true;
		  
		  }catch(Exception e){
			  connection.rollback(); result = false;
		  PPWalletEnvironment.setComment(1, classname,"The exception in method editLoyaltyRule  is  "+e.getMessage());
		  throw new Exception  ("The exception in method editLoyaltyRule  is  "+e.getMessage());
		  }finally{
		  if(connection!=null) 
			  try { super.close(); } 
		  catch (SQLException e) {
		  PPWalletEnvironment.setComment(1,classname,"SQL Exception is  "+e.getMessage()); } 
		  if(pstmt!=null) pstmt.close(); } 
		  return result; 
		 
	}

	public boolean addNewLoyaltyRule(String payMode, String rulesDesc, String pointsConversion, String cryptoConversion,
			String status)throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 PPWalletEnvironment.setComment(3, classname, "Before inserting the values");
			  PPWalletEnvironment.setComment(3, classname,  "payMode"+ payMode + "rulesDesc" + rulesDesc +" pointsConversion" + pointsConversion + "cryptoConversion" + cryptoConversion + "status" + status);
			 							//		   1			2			3					4          5
			 query = "insert into loyalty_rules (paymode, rulesdesc,  pointsconversion, cryptoconversion, status) "
					 + "values (?, ?, ?, ?, ?) ";
				//		  		 1  2 3  4  5
			 		
			 	pstmt = connection.prepareStatement(query);
				pstmt.setString(1, payMode); 					
				pstmt.setString(2, rulesDesc);
				pstmt.setString(3, pointsConversion);
			    pstmt.setString(4, cryptoConversion);
			    pstmt.setString(5, status);
				pstmt.executeUpdate();
				connection.commit();
				result = true;

	}catch(Exception e){
		connection.rollback(); 
		result = false;
		PPWalletEnvironment.setComment(1,classname,"The exception in method addNewLoyaltyRule  is  "+e.getMessage());
		throw new Exception ("The exception in method addNewLoyaltyRule  is  "+e.getMessage());
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

	public ArrayList<Loyalty> getLoyaltyTransactionsForUser(String custRelationshipNo) throws Exception {

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
			pstmt.setString(1, custRelationshipNo);
			rs = (ResultSet)pstmt.executeQuery();

			 if(rs!=null){
				 arrLoyalty = new ArrayList<Loyalty>();
				 
				 	while(rs.next()){	 
					Loyalty m_Loyalty=new Loyalty();					
				 		m_Loyalty.setSequenceId( StringUtils.trim(rs.getString("sequenceid")) );
				 		m_Loyalty.setWalletId( StringUtils.trim(rs.getString("walletid")) );
				 		m_Loyalty.setPayMode( StringUtils.trim(rs.getString("paymode")) );
				 		m_Loyalty.setTxnReference( StringUtils.trim(rs.getString("txnreference")) );
				 		m_Loyalty.setPointAccrued(StringUtils.trim(rs.getString("pointaccrued")) );
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
			PPWalletEnvironment.setComment(1,classname,"The exception in method getLoyaltyTransactionsForUser  is  "+e.getMessage());
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

	}
	

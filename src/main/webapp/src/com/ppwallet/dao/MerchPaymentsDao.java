package com.ppwallet.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.MerchCashoutTransactions;
import com.ppwallet.model.ChartOfAccounts;
import com.ppwallet.model.MerchRetailPayTransactions;
import com.ppwallet.model.SystemMsfPlans;
import com.ppwallet.model.MerchTopUpTransactions;

public class MerchPaymentsDao extends HandleConnections {
	
	private static final long serialVersionUID = 2L;

	private static String className = MerchPaymentsDao.class.getSimpleName();

	public ChartOfAccounts getChartOfAccounts(String coaVal) throws Exception  {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ChartOfAccounts mCoa = null;
		
		
		try {
			connection = super.getConnection();	

			query = "select code mcode , name mname, description mdescription "
					+ "  from chart_of_accounts where code=?  ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, coaVal);
			rs = (ResultSet)pstmt.executeQuery();
			if(rs!=null){
				while(rs.next()){
					mCoa=new ChartOfAccounts();		
					
					mCoa.setCode( rs.getString(StringUtils.trim("mcode"))   );
					mCoa.setName( rs.getString(StringUtils.trim("mname")) );
					mCoa.setDescription( rs.getString(StringUtils.trim("mdescription")) );
					
				}//whileloop
			}//rs
			
		} catch(Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getChartOfAccounts  is  "+e.getMessage());
			throw new Exception ("The exception in method getChartOfAccounts  is  "+e.getMessage());		
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
		return mCoa;
		
	
	}

	public ArrayList<MerchCashoutTransactions> getMerchCashoutTransactions(String billerCode) throws Exception {
		PPWalletEnvironment.setComment(3, className, "We are inside getMerchCashoutTransactions  " );

		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<MerchCashoutTransactions> arrCashoutTxn = null;
		try{
			
			connection = super.getConnection();	
			query = "select txncode, paytype, assetid, custrelno, billercode, custreference, tnmamount, txncurrencyid, "
					+ " location, txndatetime  from cashout_txn_bc where billercode = ?  order by txndatetime desc  ";

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, billerCode);
			//pstmt.setString(2, "A"); 
			rs = pstmt.executeQuery();
			//PPWalletEnvironment.setComment(3, className, " rs  is "+  rs );

			 if(rs!=null){
				 arrCashoutTxn =new  ArrayList<MerchCashoutTransactions>();
				 while(rs.next()){	
					 
						PPWalletEnvironment.setComment(3, className, "in arrMerchMsfPlan  " );
						
						MerchCashoutTransactions m_CashoutTxn = new MerchCashoutTransactions();
						
						m_CashoutTxn.setTxnCode( StringUtils.trim(rs.getString("txncode")));
						m_CashoutTxn.setPayType( StringUtils.trim(rs.getString("paytype")));
						m_CashoutTxn.setAssetId(  StringUtils.trim(rs.getString("assetid")));
						m_CashoutTxn.setCustRelationshipNo( StringUtils.trim(rs.getString("custrelno")));
						m_CashoutTxn.setBillerCode( StringUtils.trim(rs.getString("billercode")));
						m_CashoutTxn.setCustReference( StringUtils.trim(rs.getString("custreference")));
						m_CashoutTxn.setTxnAmount( StringUtils.trim(rs.getString("tnmamount")));
						m_CashoutTxn.setTxnCurrencyId( StringUtils.trim(rs.getString("txncurrencyid")));
						m_CashoutTxn.setLocation( StringUtils.trim(rs.getString("location")));
						m_CashoutTxn.setTxndatetime( StringUtils.trim(rs.getString("txndatetime")));
						
						arrCashoutTxn.add(m_CashoutTxn );
				 		
					} // end of while	
			 		PPWalletEnvironment.setComment(3,className,"Total arrCashoutTxns For the billerCode "+  billerCode   +"is "+arrCashoutTxn.size());	

			 	} //end of if rs!=null check
		 		PPWalletEnvironment.setComment(3,className,"after billercode "+  billerCode);	

			 if(arrCashoutTxn!=null)
				  if(arrCashoutTxn.size()==0)
					  arrCashoutTxn=null;			  
			 		
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getMerchCashoutTransactions  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchCashoutTransactions  is  "+e.getMessage());			
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
		
		
		return arrCashoutTxn;
	}

	public ArrayList<MerchTopUpTransactions> getMerchTopUpTransactions(String billerCode) throws Exception {
		PPWalletEnvironment.setComment(3, className, "We are inside getMerchTopUpTransactions " );

		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<MerchTopUpTransactions> arrTopupTxn = null;
		try{
			
			connection = super.getConnection();	
			query = "select txncode, cust_walletid, referencecode, relationshipno, sysreference, txnamount, topupmode, location, "
					+ " txncurrencyid, txndatetime  from topup_txn_bc where referencecode = ? and topupmode = ?  order by txndatetime desc  ";

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, billerCode);
			pstmt.setString(2, "M"); 
			rs = pstmt.executeQuery();

			 if(rs!=null){
				 arrTopupTxn =new  ArrayList<MerchTopUpTransactions>();
				 while(rs.next()){	
					 						
					 MerchTopUpTransactions m_TopupTxn = new MerchTopUpTransactions();
						
						m_TopupTxn.setTxnCode( StringUtils.trim(rs.getString("txncode")));
						m_TopupTxn.setCustWallelId( StringUtils.trim(rs.getString("cust_walletid")));
						m_TopupTxn.setReferenceCode(  StringUtils.trim(rs.getString("referencecode")));
						m_TopupTxn.setRelationshipNo( StringUtils.trim(rs.getString("relationshipno")));
						m_TopupTxn.setSysReference( StringUtils.trim(rs.getString("sysreference")));
						m_TopupTxn.setTxnAmount( StringUtils.trim(rs.getString("txnamount")));
						m_TopupTxn.setTopupMode( StringUtils.trim(rs.getString("topupmode")));
						m_TopupTxn.setLocation( StringUtils.trim(rs.getString("location")));
						m_TopupTxn.setTxnCurrencyId( StringUtils.trim(rs.getString("txncurrencyid")));
						m_TopupTxn.setTxndatetime( StringUtils.trim(rs.getString("txndatetime")));
						arrTopupTxn.add(m_TopupTxn );
				 		
					} // end of while	
			 		PPWalletEnvironment.setComment(3,className,"Total getMerchTopUpTransactions For the billerCode "+  billerCode   +"is "+arrTopupTxn.size());	

			 	} //end of if rs!=null check
		 		PPWalletEnvironment.setComment(3,className,"after billercode "+  billerCode);	

			 if(arrTopupTxn!=null)
				  if(arrTopupTxn.size()==0)
					  arrTopupTxn=null;			  
			 		
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getMerchTopUpTransactions  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchTopUpTransactions  is  "+e.getMessage());			
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
	
		return arrTopupTxn;
			
	}

	public ArrayList<MerchRetailPayTransactions> getMerchRetailPayTransactions(String billerCode) throws Exception{
		
		PPWalletEnvironment.setComment(3, className, "We are in getMerchRetailPayTransactions " );

		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<MerchRetailPayTransactions> arrRetailPayTxn = null;
		try{
			
			connection = super.getConnection();	
			query = "select txncode, assetid, custrelno, billercode, custreference, tnmamount, paytype, txncurrencyid, "
					+ " location, paycomment, txndatetime  from retailpay_txn_bc where billercode = ?  order by txndatetime desc  ";

			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, billerCode);
			rs = pstmt.executeQuery();

			 if(rs!=null){
				 arrRetailPayTxn =new  ArrayList<MerchRetailPayTransactions>();
				 while(rs.next()){	
					 						
					 MerchRetailPayTransactions m_RetailPayTxn = new MerchRetailPayTransactions();
						
					 m_RetailPayTxn.setTxnCode( StringUtils.trim(rs.getString("txncode")));
					 m_RetailPayTxn.setAssetId( StringUtils.trim(rs.getString("assetid")));
					 m_RetailPayTxn.setCustRelationshipNo(  StringUtils.trim(rs.getString("custrelno")));
					 m_RetailPayTxn.setBillerCode( StringUtils.trim(rs.getString("billercode")));
					 m_RetailPayTxn.setCustRef( StringUtils.trim(rs.getString("custreference")));
					 m_RetailPayTxn.setCustRelationshipNo( StringUtils.trim(rs.getString("custrelno")));
					 m_RetailPayTxn.setTxnAmount( StringUtils.trim(rs.getString("tnmamount")));
					 m_RetailPayTxn.setPayType( StringUtils.trim(rs.getString("paytype")));
					 m_RetailPayTxn.setTxnCurrencyId( StringUtils.trim(rs.getString("txncurrencyid")));
					 m_RetailPayTxn.setLocation( StringUtils.trim(rs.getString("location")));
					 m_RetailPayTxn.setPayComment( StringUtils.trim(rs.getString("paycomment")));
					 m_RetailPayTxn.setTxnDateTime( StringUtils.trim(rs.getString("txndatetime")));
					 arrRetailPayTxn.add(m_RetailPayTxn );
				 		
					} // end of while	
			 		PPWalletEnvironment.setComment(3,className,"Total getMerchRetailPayTransactions For the billerCode "+  billerCode   +"is "+arrRetailPayTxn.size());	

			 	} //end of if rs!=null check
		 		PPWalletEnvironment.setComment(3,className,"after billercode "+  billerCode);	

			 if(arrRetailPayTxn!=null)
				  if(arrRetailPayTxn.size()==0)
					  arrRetailPayTxn=null;			  
			 		
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className,"The exception in method getMerchTopUpTransactions  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchTopUpTransactions  is  "+e.getMessage());			
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
	
		return arrRetailPayTxn;
	}

}

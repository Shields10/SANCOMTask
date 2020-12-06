package com.ppwallet.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.MerchBillPaymentTransactions;
import com.ppwallet.model.MerchClientDetails;
import com.ppwallet.model.Merchant;

public class MerchBillPaymentDao extends HandleConnections {
	
	private static final long serialVersionUID = 2L;
	private static String className = MerchBillPaymentDao.class.getSimpleName();

	
	public ArrayList<MerchBillPaymentTransactions> getMerchTransactions(String billerCode) throws Exception{
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;		
		ArrayList<MerchBillPaymentTransactions> arrBillpayTransDetails = null;
		
		try {
		
			connection = super.getConnection();	
			query = "select txncode, paytype, assetid, custrelno,  billercode , custreference , tnmamount ,  txncurrencyid, txndatetime " +
								" from billpay_txn_bc where billercode=? order by txndatetime desc limit 1000";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, billerCode);
			rs = (ResultSet)pstmt.executeQuery();
			
			if(rs!=null){
				arrBillpayTransDetails = new ArrayList<MerchBillPaymentTransactions>();
				while(rs.next()){
					MerchBillPaymentTransactions	m_BillPaymentTransactions = new MerchBillPaymentTransactions();
					m_BillPaymentTransactions.setTransactionCode(rs.getString(StringUtils.trim("txncode")) );
					m_BillPaymentTransactions.setPayType(rs.getString(StringUtils.trim("paytype")) );
					m_BillPaymentTransactions.setRelationshipId(rs.getString(StringUtils.trim("custrelno")) );
					m_BillPaymentTransactions.setAssetId(rs.getString(StringUtils.trim("assetid")) );
					m_BillPaymentTransactions.setBillerCode(rs.getString(StringUtils.trim("billercode")) );
					m_BillPaymentTransactions.setCustReference(rs.getString(StringUtils.trim("custreference")) );
					m_BillPaymentTransactions.setTransAmount(rs.getString(StringUtils.trim("tnmamount")) );
					m_BillPaymentTransactions.setTransCurrencyId(rs.getString(StringUtils.trim("txncurrencyid")) );
					m_BillPaymentTransactions.setDateTime(rs.getString(StringUtils.trim("txndatetime")) );
					arrBillpayTransDetails.add(m_BillPaymentTransactions);
					
					}
				} 
				 if(arrBillpayTransDetails!=null)
					  if(arrBillpayTransDetails.size()==0)
						  arrBillpayTransDetails=null;		
			
		} catch(Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getMerchTransactions  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchTransactions  is  "+e.getMessage());		
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
		
		return arrBillpayTransDetails;
		
	}

	
	public ArrayList<MerchClientDetails> getMerchBillerDetails(String billerCode) throws Exception  {
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<MerchClientDetails> arrBillerDetails = null;
				
			try {
						
						connection = super.getConnection();	
			
						query = "select a.customername customername, a.custemail custemail, a.relationshipno relationshipno , a.custcontact custcontact, "
								+ "a.address address , b.billercode billercode " + 
								"from customer_details a, biller_details b where b.billercode=? and   a.relationshipno=b.relationshipno ";
						
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, billerCode);
						rs = (ResultSet)pstmt.executeQuery();
						
						if(rs!=null){
							arrBillerDetails = new ArrayList<MerchClientDetails>();
							while(rs.next()){
								MerchClientDetails	mBdetails = new MerchClientDetails();
								mBdetails.setCustName(rs.getString(StringUtils.trim("customername")) );
								mBdetails.setEmail(rs.getString(StringUtils.trim("custemail	")) );
								mBdetails.setContact(rs.getString(StringUtils.trim("custcontact")) );
								mBdetails.setRelationshipNo(rs.getString(StringUtils.trim("relationshipno")) );
								mBdetails.setBillerCode(rs.getString(StringUtils.trim("billercode")) );
								mBdetails.setaddress(rs.getString(StringUtils.trim("address")) );
								arrBillerDetails.add(mBdetails);
								}
							PPWalletEnvironment.setComment(3,className,"Total number of arrBillerDetails "+arrBillerDetails.size());

						}
							if(arrBillerDetails!=null)
								if(arrBillerDetails.size()==0)
									arrBillerDetails=null;
						
						
					} catch(Exception e) {
						PPWalletEnvironment.setComment(1,className,"The exception in method getMerchBillerDetails  is  "+e.getMessage());
						throw new Exception ("The exception in method getMerchBillerDetails  is  "+e.getMessage());		
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

		return arrBillerDetails;
	}


	
	
	public ArrayList<MerchBillPaymentTransactions> getTransactions(String billerCode, String startDate, String endDate) throws Exception {
		// TODO Auto-generated method stub
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;

		PPWalletEnvironment.setComment(3,className,"Dates in billpay dao:" +startDate +":"+ endDate  );
		ArrayList<MerchBillPaymentTransactions> arrTransactions = null;

		try {
			connection = super.getConnection();	
			
			query = "select txncode, paytype, assetid, custrelno, billercode, custreference, tnmamount, txncurrencyid, "
					+ "txndatetime from billpay_txn_bc where txndatetime between  ? AND ? AND billercode = ?" ;
			
			pstmt = connection.prepareStatement(query);
		
			pstmt.setString(1, startDate);
			pstmt.setString(2, endDate);
			pstmt.setString(3, billerCode);
			
			rs = (ResultSet)pstmt.executeQuery();
			
			if(rs!=null){
				arrTransactions = new ArrayList<MerchBillPaymentTransactions>();

				while(rs.next()){
					MerchBillPaymentTransactions	m_BillPaymentTransactions = new MerchBillPaymentTransactions();
					m_BillPaymentTransactions.setTransactionCode(rs.getString(StringUtils.trim("txncode")) );
					m_BillPaymentTransactions.setPayType(rs.getString(StringUtils.trim("paytype")) );
					m_BillPaymentTransactions.setRelationshipId(rs.getString(StringUtils.trim("custrelno")) );
					m_BillPaymentTransactions.setAssetId(rs.getString(StringUtils.trim("assetid")) );
					m_BillPaymentTransactions.setCustReference(rs.getString(StringUtils.trim("custreference")) );
					m_BillPaymentTransactions.setTransAmount(rs.getString(StringUtils.trim("tnmamount")) );
					m_BillPaymentTransactions.setTransCurrencyId(rs.getString(StringUtils.trim("txncurrencyid")) );
					m_BillPaymentTransactions.setDateTime(rs.getString(StringUtils.trim("txndatetime")) );
					arrTransactions.add(m_BillPaymentTransactions);
					}
				} 
			 if(arrTransactions!=null)
				  if(arrTransactions.size()==0)
					  arrTransactions=null;	
			

			
		}catch(Exception e) {
			PPWalletEnvironment.setComment(1,className,"The exception in method getMerchBillerDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getTransactions  is  "+e.getMessage());		

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
		
		
		return arrTransactions;
	}


	
	
	
	
	
	
	
	
	
}

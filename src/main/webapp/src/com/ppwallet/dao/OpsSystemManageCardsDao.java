package com.ppwallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.CardBIN;
import com.ppwallet.model.CardProduct;

public class OpsSystemManageCardsDao extends HandleConnections{
	private static final long serialVersionUID = 1L;
	private static String classname = OpsSystemManageCardsDao.class.getSimpleName();

	public ArrayList<CardBIN> getAllBINforCards() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<CardBIN> arrCardBIN = null;
		try{
			connection = super.getConnection();	

			query = "select binid, currencyid, cardtype, issuingbank, issuingbankaccountno, issuingbankaccountname, issuingbankroutingcode, "
					+ "bankswiftcode, interchangeratevar,interchangeratefixed, issuingbankinterchangeshare, settlementcutofftime, binstatus  "
					+ " from bin_master_issuing order by  binid ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrCardBIN = new ArrayList<CardBIN>();
				 	while(rs.next()){	 
				 		CardBIN m_CardBIN=new CardBIN();
				 		m_CardBIN.setBIN( StringUtils.trim(rs.getString("binid"))    );
				 		m_CardBIN.setCurrencyId( StringUtils.trim(rs.getString("currencyid"))  );
				 		m_CardBIN.setCardType( StringUtils.trim(rs.getString("cardtype"))  );
				 		m_CardBIN.setIssuingBankName(StringUtils.trim(rs.getString("issuingbank"))  );
				 		m_CardBIN.setIssuingAccountNo( StringUtils.trim(rs.getString("issuingbankaccountno"))  );
				 		m_CardBIN.setIssuingBankAccountName( StringUtils.trim(rs.getString("issuingbankaccountname"))  );
				 		m_CardBIN.setIssuingBankRoutingCode(StringUtils.trim(rs.getString("issuingbankroutingcode"))  );
				 		m_CardBIN.setIssuingBankSwiftCode( StringUtils.trim(rs.getString("bankswiftcode"))  );
				 		m_CardBIN.setInterchangeRateVariable( StringUtils.trim(rs.getString("interchangeratevar"))  );
				 		m_CardBIN.setInterchangeRateFixed( StringUtils.trim(rs.getString("interchangeratefixed"))  );
				 		m_CardBIN.setBankInterchageShare( StringUtils.trim(rs.getString("issuingbankinterchangeshare"))  );
				 		m_CardBIN.setBankSettlementCustoffTime( StringUtils.trim(rs.getString("settlementcutofftime"))  );
				 		m_CardBIN.setBinStatus( StringUtils.trim(rs.getString("binstatus"))  );
				 		arrCardBIN.add(m_CardBIN);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrCardBIN!=null) if(arrCardBIN.size()==0) arrCardBIN=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllBINforCards  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllBINforCards  is  "+e.getMessage());			
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
		return arrCardBIN;
	}

	public boolean addNewBINforCards(String strBIN, String currencyId, String cardType, String issuingBankName,
			String issuingAccountNo, String issuingBankAccountName, String issuingBankRoutingCode,
			String issuingBankSwiftCode, String interchangeRateVariable, String interchangeRateFixed,
			String bankInterchageShare, String bankSettlementCustoffTime, String binStatus) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 											//			1				2				3						4							5							6
			 query = "insert into bin_master_issuing 	(		binid, 			currencyid,  		cardtype, 			issuingbank, 			issuingbankaccountno,  		issuingbankaccountname,  "
			 		+ "								issuingbankroutingcode,	bankswiftcode,	interchangeratevar, interchangeratefixed, 		issuingbankinterchangeshare, 	settlementcutofftime,"
			 		+ "										binstatus	) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5  6  7  8  9  10 11 12 13
					pstmt = connection.prepareStatement(query);
					pstmt.setInt(1, Integer.parseInt(strBIN)); 					
					pstmt.setString(2, currencyId); 
					pstmt.setString(3, cardType); 
					pstmt.setString(4, issuingBankName); 
					pstmt.setString(5, issuingAccountNo); 
					pstmt.setString(6, issuingBankAccountName); 
					pstmt.setString(7, issuingBankRoutingCode); 
					pstmt.setString(8, issuingBankSwiftCode); 
					pstmt.setFloat(9, Float.parseFloat(interchangeRateVariable)); 
					pstmt.setFloat(10, Float.parseFloat(interchangeRateFixed)); 
					pstmt.setFloat(11, Float.parseFloat(bankInterchageShare)); 
					pstmt.setString(12, bankSettlementCustoffTime); 
					pstmt.setString(13, binStatus); 
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method addNewBINforCards  is  "+e.getMessage());
			throw new Exception ("The exception in method addNewBINforCards  is  "+e.getMessage());
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

	public boolean updateSpecBINforCards(String strBIN, String currencyId, String cardType, String issuingBankName,
			String issuingAccountNo, String issuingBankAccountName, String issuingBankRoutingCode,
			String issuingBankSwiftCode, String interchangeRateVariable, String interchangeRateFixed,
			String bankInterchageShare, String bankSettlementCustoffTime, String binStatus) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 											//		1			  		2							3							4							5	
			 query = " update bin_master_issuing set 		currencyid=?, 		cardtype = ?,     		issuingbank = ?,     		issuingbankaccountno = ?,		issuingbankaccountname=?,"
			 		+ "								issuingbankroutingcode = ?, bankswiftcode = ?, 		interchangeratevar = ?, 		interchangeratefixed = ?,	issuingbankinterchangeshare=?,"
			 		+ "								settlementcutofftime = ?, binstatus = ? "
			 		+ "								 where binid = ?";

					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, currencyId); 
					pstmt.setString(2, cardType); 
					pstmt.setString(3, issuingBankName); 
					pstmt.setString(4, issuingAccountNo); 
					pstmt.setString(5, issuingBankAccountName); 
					pstmt.setString(6, issuingBankRoutingCode); 
					pstmt.setString(7, issuingBankSwiftCode); 
					pstmt.setFloat(8, Float.parseFloat(interchangeRateVariable)); 
					pstmt.setFloat(9, Float.parseFloat(interchangeRateFixed)); 
					pstmt.setFloat(10, Float.parseFloat(bankInterchageShare)); 
					pstmt.setString(11, bankSettlementCustoffTime); 
					pstmt.setString(12, binStatus); 
					pstmt.setString(13, strBIN); 
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method updateSpecBINforCards  is  "+e.getMessage());
			throw new Exception ("The exception in method updateSpecBINforCards  is  "+e.getMessage());
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

	public ArrayList<CardProduct> getAllCardProducts() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<CardProduct> arrCardProduct = null;
		try{
			connection = super.getConnection();	

			query = "select product_id, product_name, brand_name, product_start_date, product_end_date, bin_allocated, product_type, "
					+ "billing_cycle, suspense_account_no, product_interchange_fixed, product_interchange_var, product_status   "
					+ " from card_product order by  product_id ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrCardProduct = new ArrayList<CardProduct>();
				 	while(rs.next()){	 
				 		CardProduct m_CardProduct=new CardProduct();
				 		m_CardProduct.setProductId( StringUtils.trim(rs.getString("product_id"))    );
				 		m_CardProduct.setProductName( StringUtils.trim(rs.getString("product_name"))  );
				 		m_CardProduct.setBrandName( StringUtils.trim(rs.getString("brand_name"))  );
				 		m_CardProduct.setProductStartDate(StringUtils.trim(rs.getString("product_start_date"))  );
				 		m_CardProduct.setProductEndDate( StringUtils.trim(rs.getString("product_end_date"))  );
				 		m_CardProduct.setAllocatedBIN( StringUtils.trim(rs.getString("bin_allocated"))  );
				 		m_CardProduct.setProductType(StringUtils.trim(rs.getString("product_type"))  );
				 		m_CardProduct.setBillingCycle( StringUtils.trim(rs.getString("billing_cycle"))  );
				 		m_CardProduct.setSuspenseAccountNo( StringUtils.trim(rs.getString("suspense_account_no"))  );
				 		m_CardProduct.setProductInterchangeFixed( StringUtils.trim(rs.getString("product_interchange_fixed"))  );
				 		m_CardProduct.setProductInterchangeVariable( StringUtils.trim(rs.getString("product_interchange_var"))  );
				 		m_CardProduct.setProductStatus( StringUtils.trim(rs.getString("product_status"))  );
				 		arrCardProduct.add(m_CardProduct);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrCardProduct!=null) if(arrCardProduct.size()==0) arrCardProduct=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllCardProducts  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllCardProducts  is  "+e.getMessage());			
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
		return arrCardProduct;
	}

	public boolean updateCardProduct(String productID, String productName, String brandName, String productStartDate,
			String productEndDate, String productType, String billingCycle, String suspenseAccountNo,
			String productInterchangeFixed, String productInterchangeVariable, String productStatus) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);

			 											//		1			  		2							3							4							5	
			 query = " update card_product set 	product_name=?,	brand_name = ?,	product_start_date = ?,  product_end_date = ?, product_type = ?, billing_cycle = ?, suspense_account_no = ?, product_interchange_fixed = ?,  product_interchange_var = ?,  product_status = ? where product_id = ?";	 		
			 pstmt = connection.prepareStatement(query);
				pstmt.setString(1, productName); 					
				pstmt.setString(2, brandName);
				pstmt.setString(3, productStartDate);
				pstmt.setString(4, productEndDate);
				pstmt.setString(5, productType);
				pstmt.setString(6, billingCycle);
				pstmt.setString(7, suspenseAccountNo);
				pstmt.setFloat(8, Float.parseFloat(productInterchangeFixed));
				pstmt.setFloat(9, Float.parseFloat(productInterchangeVariable));
				
				pstmt.setString(10, productStatus);
				pstmt.setInt(11, Integer.parseInt(productID));
				try {
					pstmt.executeUpdate();
					}catch(Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}
				connection.commit();
				result = true;		

		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method updateCardProduct  is  "+e.getMessage());
			throw new Exception ("The exception in method updateCardProduct  is  "+e.getMessage());
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

	public boolean addCardProduct(String productName, String brandName, String productStartDate, String productEndDate,
			String allocatedBIN, String productType, String billingCycle, String suspenseAccountNo,
			String productInterchangeFixed, String productInterchangeVariable, String productStatus) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 								//			1				2				3								4							5				6			6
			 query = "insert into card_product 	(	product_name,  		brand_name, 	product_start_date, 		product_end_date,  		bin_allocated,  product_type,"
			 		+ "								billing_cycle,	suspense_account_no, product_interchange_fixed, product_interchange_var, 	product_status, created_on_utc"
			 		+ "											) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  UTC_TIMESTAMP) ";
							//		   1  2  3  4  5  6  7  8  9  10 11 
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, productName); 					
					pstmt.setString(2, brandName);
					pstmt.setString(3, productStartDate);
					pstmt.setString(4, productEndDate);
					pstmt.setInt(5, Integer.parseInt(allocatedBIN));
					pstmt.setString(6, productType);
					pstmt.setString(7, billingCycle);
					pstmt.setString(8, suspenseAccountNo);
					pstmt.setFloat(9, Float.parseFloat(productInterchangeFixed));
					pstmt.setFloat(10, Float.parseFloat(productInterchangeVariable));
					pstmt.setString(11, productStatus);

					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method addCardProduct  is  "+e.getMessage());
			throw new Exception ("The exception in method addCardProduct  is  "+e.getMessage());
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
	

}

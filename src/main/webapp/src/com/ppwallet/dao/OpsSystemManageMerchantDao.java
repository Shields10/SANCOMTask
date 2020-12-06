package com.ppwallet.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.model.DisputeReasons;
import com.ppwallet.model.DisputeTracker;
import com.ppwallet.model.Disputes;
import com.ppwallet.model.MCC;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.MerchantInstitution;
import com.ppwallet.model.SystemMsfPlans;
import com.ppwallet.model.Transaction;
import com.ppwallet.utilities.Utilities;

public class OpsSystemManageMerchantDao extends HandleConnections{
	private static final long serialVersionUID = 1L;
	private static String classname = OpsSystemManageMerchantDao.class.getSimpleName();


	public ArrayList<MCC> getAllMCCList() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<MCC> arrMCC = null;
		try{
			connection = super.getConnection();	

			query = "select mcccategoryid, mcccategoryname, mccgeneric, mccfromrange, mcctorange from merch_mcc_group order by  mcccategoryid ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrMCC = new ArrayList<MCC>();
				 	while(rs.next()){	 
				 		MCC m_MCC=new MCC();
				 		m_MCC.setMCCCategoryId( StringUtils.trim(rs.getString("mcccategoryid"))    );
				 		m_MCC.setMCCCategoryName( StringUtils.trim(rs.getString("mcccategoryname"))  );
				 		m_MCC.setMCCGeneric( StringUtils.trim(rs.getString("mccgeneric"))  );
				 		m_MCC.setMCCFromRange( StringUtils.trim(rs.getString("mccfromrange"))  );
				 		m_MCC.setMCCToRange( StringUtils.trim(rs.getString("mcctorange"))  );
				 		arrMCC.add(m_MCC);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrMCC!=null) if(arrMCC.size()==0) arrMCC=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllMCCList  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllMCCList  is  "+e.getMessage());			
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return arrMCC;
	}

	
	public ArrayList<Disputes> getAllMerchantDisputes(String refNo) throws Exception {
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
				 		m_Disputes.setReferenceNo(StringUtils.trim(rs.getString("referenceno"))   );
				 		m_Disputes.setDisputeId( StringUtils.trim(rs.getString("disputeid"))   );
				 		m_Disputes.setTransactionId(StringUtils.trim(rs.getString("transactionid"))   );
				 		m_Disputes.setReasonId(StringUtils.trim(rs.getString("reasonid"))   );
				 		m_Disputes.setRaisedbyUserId(StringUtils.trim(rs.getString("raisedbyuser"))   );
				 		String userType = StringUtils.trim(rs.getString("usertype"));
				 		
				 		if(userType.equalsIgnoreCase("M")) { m_Disputes.setUserType("Merchant");
				 		}if (userType.equalsIgnoreCase("C")) { m_Disputes.setUserType("Customer");
						}
				 		
//				 		m_Disputes.setStatus(StringUtils.trim(rs.getString("status"))   );
				 		
				 		String status = StringUtils.trim(rs.getString("status"));
				 		if(status.equalsIgnoreCase("A")) { m_Disputes.setStatus("Active");
				 		}else if (status.equalsIgnoreCase("C")) { m_Disputes.setStatus("Closed");
						}else if(status.equalsIgnoreCase("P")) { m_Disputes.setStatus("In Progress");
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
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllDisputes  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllDisputes  is  "+e.getMessage());
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return aryDisputes;
	}

	
	
	public boolean updateMCCList(String MCCCategoryId, String MCCCategoryDescription, String mCCCatFromRange, String mCCCatToRange, String mCCGenericId) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 									//		1			  	2			3			4					5
			 query = " update merch_mcc_group set mcccategoryname=?, mccfromrange=?, mcctorange=?, mccgeneric=?  where mcccategoryid = ?";

					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, MCCCategoryDescription); 					
					pstmt.setInt(2, Integer.parseInt(mCCCatFromRange));	
					pstmt.setInt(3, Integer.parseInt(mCCCatToRange));	
					pstmt.setInt(4, Integer.parseInt(mCCGenericId));	
					pstmt.setInt(5, Integer.parseInt(MCCCategoryId));	
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method updateMCCList  is  "+e.getMessage());
			throw new Exception ("The exception in method updateMCCList  is  "+e.getMessage());
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}


	public boolean addNewMCC(String MCCCategoryId, String MCCCategoryDescription, String mCCCatFromRange, String mCCCatToRange, String mCCGenericId) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 							//		1						2				3				4				5
			 query = "insert into merch_mcc_group (mcccategoryid, mcccategoryname, mccfromrange, mcctorange, mccgeneric) "
							+ "values (?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5
			 
					pstmt = connection.prepareStatement(query);
					pstmt.setInt(1, Integer.parseInt(MCCCategoryId)); 					
					pstmt.setString(2, MCCCategoryDescription); 	
					pstmt.setInt(3, Integer.parseInt(mCCCatFromRange));
					pstmt.setInt(4, Integer.parseInt(mCCCatToRange));
					pstmt.setInt(5, Integer.parseInt(mCCGenericId));
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
			PPWalletEnvironment.setComment(1,classname,"The exception in method addNewMCC  is  "+e.getMessage());
			throw new Exception ("The exception in method addNewMCC  is  "+e.getMessage());
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}


	public MerchantInstitution getInsitutionDetails() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		MerchantInstitution m_MerchantInstitution = null;
		try{
			connection = super.getConnection();	

			query = "select institution_id,		institution_name, 	address_line1, 	address_line2, 		address_city,  " + 
					"	 		address_pincode,  institution_taxid, 	currency_id, 	interchange_fee_var, 	merchant_service_fee, " + 
					" 		statement_cycle, 	institution_bin,  institution_regno,  interchange_fee_fixed from acq_institution  ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 	while(rs.next()){	 
				 		m_MerchantInstitution=new MerchantInstitution();
				 		m_MerchantInstitution.setInstitutionId( StringUtils.trim(rs.getString("institution_id"))    );
				 		m_MerchantInstitution.setInstitutionName( StringUtils.trim(rs.getString("institution_name"))  );
				 		m_MerchantInstitution.setAddressLine1( StringUtils.trim(rs.getString("address_line1"))  );
				 		m_MerchantInstitution.setAddressLine2( StringUtils.trim(rs.getString("address_line2"))  );
				 		m_MerchantInstitution.setAddressCity( StringUtils.trim(rs.getString("address_city"))  );
				 		m_MerchantInstitution.setAddressPincode( StringUtils.trim(rs.getString("address_pincode"))  );
				 		m_MerchantInstitution.setInstitutionTaxId(StringUtils.trim(rs.getString("institution_taxid"))  );
				 		m_MerchantInstitution.setCurrencyId( StringUtils.trim(rs.getString("currency_id"))  );
				 		m_MerchantInstitution.setInterchangeFeeVariable( StringUtils.trim(rs.getString("interchange_fee_var"))  );
				 		m_MerchantInstitution.setMerchantServiceFee( StringUtils.trim(rs.getString("merchant_service_fee"))  );
				 		m_MerchantInstitution.setStatementCycle( StringUtils.trim(rs.getString("statement_cycle"))  );
				 		m_MerchantInstitution.setInstitutionBin( StringUtils.trim(rs.getString("institution_bin"))  );
				 		m_MerchantInstitution.setRegistrationNo( StringUtils.trim(rs.getString("institution_regno"))  );
				 		m_MerchantInstitution.setInterchangeFeeFixed( StringUtils.trim(rs.getString("interchange_fee_fixed"))  );
				 		} // end of while
				 	} //end of if rs!=null check
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getInsitutionDetails  is  "+e.getMessage());
			throw new Exception ("The exception in method getInsitutionDetails  is  "+e.getMessage());			
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return m_MerchantInstitution;
	}


	public boolean addNewInstitution(String institutionName, String addressLine1, String addressLine2,
			String addressCity, String addressPincode, String institutionTaxId, String currencyId,
			String merchantServiceFee, String interchangeFeeVariable, String interchangeFeeFixed, String statementCycle,
			String institutionBin, String registrationNo) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 											//			1				2				3					4						5						6
			 query = "insert into acq_institution 		(institution_name, 	address_line1,  address_line2, 		address_city, 			address_pincode,  		institution_regno,  "
			 		+ "										institution_taxid,	currency_id,	interchange_fee_var, interchange_fee_fixed, merchant_service_fee, 	statement_cycle,"
			 		+ "										institution_bin	) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5  6  7  8  9  10 11 12 13
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, institutionName); 					
					pstmt.setString(2, addressLine1); 
					pstmt.setString(3, addressLine2); 
					pstmt.setString(4, addressCity); 
					pstmt.setString(5, addressPincode); 
					pstmt.setString(6, registrationNo); 
					pstmt.setString(7, institutionTaxId); 
					pstmt.setString(8, currencyId); 
					pstmt.setFloat(9, Float.parseFloat(interchangeFeeVariable)); 
					pstmt.setFloat(10, Float.parseFloat(interchangeFeeFixed)); 
					pstmt.setFloat(11, Float.parseFloat(merchantServiceFee)); 
					pstmt.setString(12, statementCycle); 
					pstmt.setString(13, institutionBin); 
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method addNewInstitution  is  "+e.getMessage());
			throw new Exception ("The exception in method addNewInstitution  is  "+e.getMessage());
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}


	public boolean editMerchInstitution(String institutionId, String institutionName, String addressLine1,
			String addressLine2, String addressCity, String addressPincode, String institutionTaxId, String currencyId,
			String merchantServiceFee, String interchangeFeeVariable, String interchangeFeeFixed, String statementCycle,
			String institutionBin, String registrationNo) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 											//		1			  		2							3							4
			 query = " update acq_institution		 set institution_name=?, address_line1 = ?,     		address_line2 = ?,     		address_city = ?,"
			 		+ "									   address_pincode = ?, institution_regno = ?, 		institution_taxid = ?, 		currency_id = ?,"
			 		+ "									   interchange_fee_var = ?, interchange_fee_fixed = ?, merchant_service_fee = ?, statement_cycle = ?,"
			 		+ "									   institution_bin = ? where institution_id = ?";

					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, institutionName); 					
					pstmt.setString(2, addressLine1);
					pstmt.setString(3, addressLine2);
					pstmt.setString(4, addressCity);
					pstmt.setString(5, addressPincode);
					pstmt.setString(6, registrationNo);
					pstmt.setString(7, institutionTaxId);
					pstmt.setString(8, currencyId);
					pstmt.setFloat(9, Float.parseFloat(interchangeFeeVariable));
					pstmt.setFloat(10, Float.parseFloat(interchangeFeeFixed));
					pstmt.setFloat(11, Float.parseFloat(merchantServiceFee));
					pstmt.setString(12, statementCycle);
					pstmt.setString(13, institutionBin);
					pstmt.setString(14, institutionId);
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method editMerchInstitution  is  "+e.getMessage());
			throw new Exception ("The exception in method editMerchInstitution  is  "+e.getMessage());
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}


	public ArrayList<SystemMsfPlans> getAllMsfPlanLists() throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<SystemMsfPlans> arrSystemMsfPlans = null;
	
		try{
			PPWalletEnvironment.setComment(3, classname, "now in getAllMsfPlanLists dao" );

			connection = super.getConnection();	

			query = "select planid, plan_name,   plan_fee_fixed,  plan_fee_var,  plan_type, desposit_fee, set_up_fee, monthly_fee,  annual_fee,"
					+ " statement_fee,  late_payment_fee,   plan_cycle,	status, created_on	from merch_sys_msf_plan order by  planid ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrSystemMsfPlans = new ArrayList<SystemMsfPlans>();
				 	while(rs.next()){	 
				 		SystemMsfPlans m_SystemMsfPlans=new SystemMsfPlans();
				 		m_SystemMsfPlans.setPlanId( StringUtils.trim(rs.getString("planid"))   );
				 		m_SystemMsfPlans.setPlanName( StringUtils.trim(rs.getString("plan_name"))  );
				 		m_SystemMsfPlans.setPlanFeeFixed( StringUtils.trim(rs.getString("plan_fee_fixed"))  );
				 		m_SystemMsfPlans.setPlanFeeVar(StringUtils.trim(rs.getString("plan_fee_var"))  );
				 		m_SystemMsfPlans.setPlanType( StringUtils.trim(rs.getString("plan_type"))  );
				 		m_SystemMsfPlans.setPlanDepositFee( StringUtils.trim(rs.getString("desposit_fee"))  );
				 		m_SystemMsfPlans.setPlanSetUpFee( StringUtils.trim(rs.getString("set_up_fee"))  );
				 		m_SystemMsfPlans.setPlanMonthlyFee( StringUtils.trim(rs.getString("monthly_fee"))  );
				 		m_SystemMsfPlans.setPlanAnnualFee( StringUtils.trim(rs.getString("annual_fee"))  );
				 		m_SystemMsfPlans.setPlanStatementFee( StringUtils.trim(rs.getString("statement_fee"))  );
				 		m_SystemMsfPlans.setPlanLatePaymentFee( StringUtils.trim(rs.getString("late_payment_fee"))  );
				 		m_SystemMsfPlans.setPlanCycle( StringUtils.trim(rs.getString("plan_cycle"))  );
				 		m_SystemMsfPlans.setPlanCreatedOn( StringUtils.trim(rs.getString("created_on"))  );
				 		m_SystemMsfPlans.setStatus( StringUtils.trim(rs.getString("status"))  );
				 		arrSystemMsfPlans.add(m_SystemMsfPlans);
				 		} // end of while
				 	} //end of if rs!=null check
				PPWalletEnvironment.setComment(3, classname, "setPlanCreatedOn is here" );
			 if(arrSystemMsfPlans!=null) if(arrSystemMsfPlans.size()==0) arrSystemMsfPlans=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllMsfPlanLists  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllMsfPlanLists  is  "+e.getMessage());			
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return arrSystemMsfPlans;
	}


	public boolean addMerchantSystemMSFPlan(String planName, String depositFee, String setUpFee, String monthlyFee,
			String annualFee, String statementFee, String latePaymentFee, String cycleDate, String status,
			String planFeeFixed, String planFeeVar, String planType, String createdOn) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
				PPWalletEnvironment.setComment(3,classname,"planName "+ planName  + "depositFee "+ depositFee + "setUpFee " + setUpFee  +"monthlyFee " 
						+ monthlyFee+ "annualFee " + annualFee + "statementFee " + statementFee + "latePaymentFee  "+latePaymentFee + "cycleDate "
						+ cycleDate + "status "+  status + "planFeeFixed " +  planFeeFixed  +  "planFeeVar  " + planFeeVar + "planType "+ planType  + "createdOn "+ createdOn );		
				              							//	1		      	2				 3				 4				   5				    6		 7				
			 query = "insert into merch_sys_msf_plan 	(plan_name,      plan_fee_fixed,   plan_fee_var,   plan_type,   	 desposit_fee, 	  set_up_fee, "
			 		                                   + "monthly_fee,   annual_fee,       statement_fee,  late_payment_fee,  plan_cycle,     status,      created_on) "

							+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5  6  7  8  9  10 11 12 13 plan_fee_var
					pstmt = connection.prepareStatement(query);
					
					pstmt.setString(1, planName); 
					pstmt.setFloat(2, Float.parseFloat(planFeeFixed)); 
					pstmt.setFloat(3, Float.parseFloat(planFeeVar)); 
					pstmt.setString(4, planType); 
					pstmt.setFloat(5, Float.parseFloat(depositFee)); 					
					pstmt.setFloat(6, Float.parseFloat(setUpFee)); 					
					pstmt.setFloat(7, Float.parseFloat(monthlyFee)); 					
					pstmt.setFloat(8, Float.parseFloat(annualFee)); 					
					pstmt.setFloat(9, Float.parseFloat(statementFee)); 					
					pstmt.setFloat(10, Float.parseFloat(latePaymentFee)); 					
					pstmt.setString(11, cycleDate); 					
					pstmt.setString(12, status); 
					pstmt.setString(13, Utilities.getMYSQLCurrentTimeStampForInsert()); 
					try {
						PPWalletEnvironment.setComment(3,classname,"after query ");
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method addMerchantSystemMSFPlan  is  "+ e.getMessage());
			throw new Exception ("The exception in method addMerchantSystemMSFPlan  is "+ e.getMessage());
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}

	

	public boolean editMerchantSystemMSFPlan(String planId, String planName, String planFeeFixed, String planFeeVar,
			String planType, String depositFee, String setUpFee, String monthlyFee, String annualFee,
			String statementFee, String latePaymentFee, String cycleDate, String planCreatedOn, String status) throws Exception  {
		
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 
 //PPWalletEnvironment.setComment(3,classname,"planName "+ planName  + "planFeeFixed " +  planFeeFixed  +  "planFeeVar  " + planFeeVar + "planType "+ planType 
				//	 +"depositFee "+ depositFee + "setUpFee " + setUpFee  +"monthlyFee " + monthlyFee+ "annualFee " + annualFee + "statementFee " + statementFee + "latePaymentFee  "+latePaymentFee 
				// +"cycleDate "+ cycleDate + "status "+  status +  "createdOn "+ planCreatedOn );		
			 									//		1			  		2					3					4				5
			 query = " update merch_sys_msf_plan set plan_name = ?, plan_fee_fixed = ?, plan_fee_var = ?, plan_type = ?, desposit_fee = ?, "
			 		+ "							set_up_fee = ?,   monthly_fee = ?, 	annual_fee = ?, statement_fee = ?, late_payment_fee = ?,"
			 		+ "							 plan_cycle = ?, 	status = ?, 	created_on = ? where 	planid = ?";

					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, planName); 
					pstmt.setFloat(2, Float.parseFloat(planFeeFixed)); 
					pstmt.setFloat(3, Float.parseFloat(planFeeVar)); 
					pstmt.setString(4, planType); 
					pstmt.setFloat(5, Float.parseFloat(depositFee)); 					
					pstmt.setFloat(6, Float.parseFloat(setUpFee)); 					
					pstmt.setFloat(7, Float.parseFloat(monthlyFee)); 					
					pstmt.setFloat(8, Float.parseFloat(annualFee)); 					
					pstmt.setFloat(9, Float.parseFloat(statementFee)); 					
					pstmt.setFloat(10, Float.parseFloat(latePaymentFee)); 					
					pstmt.setString(11, cycleDate); 					
					pstmt.setString(12, status); 
					pstmt.setString(13,  Utilities.getMYSQLCurrentTimeStampForInsert()); 
					pstmt.setInt(14, Integer.parseInt(planId)); 		

					try {
						PPWalletEnvironment.setComment(3,classname,"planCreatedOn  is " + planCreatedOn);
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method editMerchantSystemMSFPlan  is  "+e.getMessage());
			throw new Exception ("The exception in method editMerchantSystemMSFPlan  is  "+e.getMessage());
		}finally{
			
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
			if(pstmt!=null) pstmt.close();
		
		}
		return result;
	
	}


	public ArrayList<SystemMsfPlans> getAllMSFPlanIDs() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<SystemMsfPlans> arrMSFPlan = null;
		try{
			connection = super.getConnection();	

			query = "select planid, plan_name from merch_sys_msf_plan where status=? order by  plan_name ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "A"); 
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrMSFPlan = new ArrayList<SystemMsfPlans>();
				 	while(rs.next()){	 
				 		SystemMsfPlans m_MSFPlan=new SystemMsfPlans();
				 		//m_MSFPlan.setPlanId( StringUtils.trim(rs.getString("planid"))    );
				 		m_MSFPlan.setPlanName( StringUtils.trim(rs.getString("plan_name"))  );
				 		arrMSFPlan.add(m_MSFPlan);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrMSFPlan!=null) if(arrMSFPlan.size()==0) arrMSFPlan=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllMSFPlanIDs is  "+e.getMessage());
			throw new Exception ("The exception in method getAllMSFPlanIDs is  "+e.getMessage());			
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return arrMSFPlan;
	}
	
	public ArrayList<DisputeReasons> getAllDisputeReasons(String userType) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<DisputeReasons> arrDisputeReasons = null;
		try{
			connection = super.getConnection();	

			if(userType.trim().length()==0) {
			query = "select reasonid, reasondesc, usertype, paymode, status  from dispute_reason order by usertype";
			}else {
				query = "select reasonid, reasondesc, usertype, paymode, status  from dispute_reason where userType = ? ";
			}
			
			pstmt = connection.prepareStatement(query);
			if(userType.trim().length()>0) {
					pstmt.setString(1, userType); 
			}
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrDisputeReasons = new ArrayList<DisputeReasons>();
				 	while(rs.next()){	 
				 		DisputeReasons m_DispReason=new DisputeReasons();
				 		m_DispReason.setDisputeReasonId( StringUtils.trim(rs.getString("reasonid"))    );
				 		m_DispReason.setDisputeReasonDesc( StringUtils.trim(rs.getString("reasondesc"))  );
				 		m_DispReason.setUserType( StringUtils.trim(rs.getString("usertype"))  );
				 		m_DispReason.setPayMode( StringUtils.trim(rs.getString("paymode"))  );
				 		m_DispReason.setStatus( StringUtils.trim(rs.getString("status"))    );
				 		arrDisputeReasons.add(m_DispReason);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrDisputeReasons!=null) if(arrDisputeReasons.size()==0) arrDisputeReasons=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllDisputeReasons is  "+e.getMessage());
			throw new Exception ("The exception in method getAllDisputeReasons is  "+e.getMessage());			
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return arrDisputeReasons;
	}

	public ArrayList<DisputeTracker> getAllDisputes(String userType) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<DisputeTracker> arrDisputes = null;
		try{
			connection = super.getConnection();	

			query = "select disputeid, reasonid, raisedbyuser, usertype, status, raisedondate, referenceno from dispute_details where userType= ? order by raisedondate desc";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, userType); 
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrDisputes = new ArrayList<DisputeTracker>();
				 	while(rs.next()){	 
				 		DisputeTracker m_DispReason=new DisputeTracker();
				 		
				 		m_DispReason.setDisputeId( StringUtils.trim(rs.getString("disputeid"))    );
				 		m_DispReason.setReasonId( StringUtils.trim(rs.getString("reasonid"))  );
				 		m_DispReason.setRaisedbyUserId( StringUtils.trim(rs.getString("raisedbyuser"))  );
				 		m_DispReason.setUserType(StringUtils.trim(rs.getString("usertype"))    );
				 		m_DispReason.setStatus( StringUtils.trim(rs.getString("status"))    );	
				 		m_DispReason.setRaisedOn(StringUtils.trim(rs.getString("raisedondate"))    );
				 		m_DispReason.setRefNo(StringUtils.trim(rs.getString("referenceno")) );
				 		arrDisputes.add(m_DispReason);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrDisputes!=null) if(arrDisputes.size()==0) arrDisputes=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllDisputes is  "+e.getMessage());
			throw new Exception ("The exception in method getAllDisputes is  "+e.getMessage());			
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
		return arrDisputes;
	}

	
	public boolean addDisputeReasonCode(String reasonID, String reasonDesc, String disputeUserType, String status, String payMode) throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 										//		1		2			3	     	4			
			 query = "insert into dispute_reason 		(reasonid, reasondesc, 	usertype,	status, paymode) "
							+ "values (?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5  
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, reasonID); 
					pstmt.setString(2, reasonDesc); 					
					pstmt.setString(3, disputeUserType); 	
					pstmt.setString(4, status); 
					pstmt.setString(5, payMode);
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method addDisputeReasonCode  is  "+e.getMessage());
			throw new Exception ("The exception in method addDisputeReasonCode  is  "+e.getMessage());
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
			if(pstmt!=null) pstmt.close();
		}
		return result;

		}
	
	public ArrayList<DisputeTracker> getAllDisputeCodes(String userType) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<DisputeTracker> arrDisputes = null;
		try{
			connection = super.getConnection();	

			query = "select disputeid, reasonid, raisedbyuser, usertype, status, raisedondate from dispute_details where userType= ? ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, userType); 
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrDisputes = new ArrayList<DisputeTracker>();
				 	while(rs.next()){	 
				 		DisputeTracker m_DisputeTracker=new DisputeTracker();
				 		
				 		m_DisputeTracker.setDisputeId( StringUtils.trim(rs.getString("disputeid"))    );
				 		m_DisputeTracker.setReasonId( StringUtils.trim(rs.getString("reasonid"))  );
				 		m_DisputeTracker.setRaisedbyUserId( StringUtils.trim(rs.getString("raisedbyuser"))  );
				 		m_DisputeTracker.setUserType(StringUtils.trim(rs.getString("usertype"))    );
				 		m_DisputeTracker.setStatus( StringUtils.trim(rs.getString("status"))    );	
				 		m_DisputeTracker.setRaisedOn(StringUtils.trim(rs.getString("raisedondate"))    );
				 		arrDisputes.add(m_DisputeTracker);
				 		} // end of while
				 	
				 	} //end of if rs!=null check
			 if(arrDisputes!=null) if(arrDisputes.size()==0) arrDisputes=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllDisputeCodes is  "+e.getMessage());
			throw new Exception ("The exception in method getAllDisputeCodes is  "+e.getMessage());			
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return arrDisputes;
	}
	

	public boolean editDisputeReasonCode(String reasonId, String reasonDesc, String disputeUserType, String status, String payMode) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 									//		     1			  	   2			3	    4                 5
			 query = " update dispute_reason set reasondesc = ?, 	usertype = ?, 	status = ?,  paymode=?   where 	reasonid = ?";

					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, reasonDesc);					
					pstmt.setString(2, disputeUserType);
					pstmt.setString(3, status);
					pstmt.setInt(4, Integer.parseInt(reasonId) ); 
					pstmt.setString(5, payMode);
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method editDisputeReasonCode  is  "+e.getMessage());
			throw new Exception ("The exception in method editDisputeReasonCode  is  "+e.getMessage());
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
	public boolean addNewDispute(String raisedByuserId, String reasonId, String systemReference, String transactionId, String userComment, String userType, String status, String raisedOn) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		
		try{
			PPWalletEnvironment.setComment(3, classname, "Passed");
			 connection = super.getConnection();
			 connection.setAutoCommit(false); 
			  
			 										//		1			2				3			4			5   		 6                7     		 8			
			 query = "insert into dispute_details 	(	transactionid, 	reasonid, 	raisedbyuser, 	usertype,  referenceno,	usercomment, 	status, 	raisedondate) "
							+ "values (?, ?, ?, ?, ?, ?, ?, ?) ";
							//		   1  2  3  4  5  6  7  8 
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1,  (transactionId)); 					
					pstmt.setInt (2, Integer.parseInt(reasonId) ); 					
					pstmt.setString(3, raisedByuserId); 					
					pstmt.setString(4, userType); 					
					pstmt.setString(5, systemReference); 					
					pstmt.setString(6, userComment); 					
					pstmt.setString(7, status); 					
					pstmt.setString(8,raisedOn); 		
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
			PPWalletEnvironment.setComment(1,classname,"The exception in method addNewDispute  is  "+e.getMessage());
			throw new Exception ("The exception in method addNewDispute  is  "+e.getMessage());
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	
		}

	public boolean editDispute(String reasonId, String reasonDesc, String disputeUserType, String status) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 									//		     1			  	   2			3					  4
			 query = " update dispute_reason set reasondesc = ?, 	usertype = ?, 	status = ?,  where 	reasonid = ?";

					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, reasonDesc);					
					pstmt.setString(2, disputeUserType);
					pstmt.setString(3, status);
					pstmt.setInt(4, Integer.parseInt(reasonId) ); 
					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method editDispute  is  "+e.getMessage());
			throw new Exception ("The exception in method editDispute  is  "+e.getMessage());
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
			if(pstmt!=null) pstmt.close();
		}
		return result;
	}
	


	public ArrayList<Merchant> getAllPendingMerchants() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<Merchant> arrMerchants = null;
		try{
			connection = super.getConnection();	

			query = "select billercode, merchantid, merchantname, nationalid, merchantemail, merchantcontact,  status from merch_details where status = ? order by createdon desc ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "V"); 
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrMerchants = new ArrayList<Merchant>();
				 	while(rs.next()){	 
				 		Merchant m_Merchant=new Merchant();
				 		
				 		m_Merchant.setBillerCode( StringUtils.trim(rs.getString("billercode"))    );
				 		m_Merchant.setMerchantId( StringUtils.trim(rs.getString("merchantid"))    );
				 		m_Merchant.setMerchantName( StringUtils.trim(rs.getString("merchantname"))  );
				 		m_Merchant.setNationalId( StringUtils.trim(rs.getString("nationalid"))  );
				 		m_Merchant.setEmail( StringUtils.trim(rs.getString("merchantemail"))  );
				 		m_Merchant.setContact( StringUtils.trim(rs.getString("merchantcontact"))    );
				 		m_Merchant.setStatus( StringUtils.trim(rs.getString("status"))    );	
				 		arrMerchants.add(m_Merchant);
				 		} // end of while
				//	PPWalletEnvironment.setComment(3,classname," pending merchants are "+ arrMerchants.size());

				 	
				 	} //end of if rs!=null check
			 if(arrMerchants!=null) if(arrMerchants.size()==0) arrMerchants=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllPendingMerchants is  "+e.getMessage());
			throw new Exception ("The exception in method getAllPendingMerchants is  "+e.getMessage());			
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return arrMerchants;
	}
	
	public boolean verifyPendingMerch(String billerCode, String merchantId, String nationalId, String status) throws Exception {
		
		PreparedStatement pstmt=null; Connection connection = null; String query = null;
	
		  boolean result = false; 
		  try{
			
			  connection = super.getConnection();
			  connection.setAutoCommit(false);
			
			  PPWalletEnvironment.setComment(3, classname,  "billerCode"+ billerCode + "status" + status);
			// Step 1 : update the merchant status .
			                             // 	1		        	 2 	 
			  query = "update merch_details set status=? where  billercode = ? ";
					  pstmt = connection.prepareStatement(query); 
					  pstmt.setString(1, status);
					  pstmt.setString(2, billerCode);
					  try {
							pstmt.executeUpdate();
						} catch (Exception e) {
							throw new Exception(" failed query " + query + " " + e.getMessage());
						}
						pstmt.close();		
						
						
			// Step 2 : Insert data into the wallet_details
					SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMdd");  formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));
					 String walletId = (formatter1.format(new Date()))+( RandomStringUtils.random(6, false, true)).toString();	
					//System.out.println("walletId is  "+walletId);
				//((Token)arrToken.get(i)).setWalletId(walletId);
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
					
					PPWalletEnvironment.setComment(3,classname," update merch_details billerCode"+billerCode );

					try {
						pstmt.executeUpdate();
					}catch (Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
						
						}
					 pstmt.close();	
										  
				// Step 3 : Insert data into the wallet_merchant_rel . 
								                     //			1       	2			3			4			5		    6       
					query = "insert into wallet_merchant_rel (walletid, billercode, merchant_id, national_id,  status,  creadtedon )  "
					       + "  values (?, ?, ?, ?, ?, ?) ";
					               //   1  2  3  4  5  6   
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1, walletId  ); 
					pstmt.setString(2,  billerCode  );
					pstmt.setString(3,   merchantId   );
					pstmt.setString(4,   nationalId  ); 
					pstmt.setString(5,  "A"     );			
					pstmt.setString(6,  Utilities.getMYSQLCurrentTimeStampForInsert()  );	
					PPWalletEnvironment.setComment(3,classname," insert into wallet_merchant_rel"+billerCode );
					try {
						pstmt.executeUpdate();
					}catch (Exception e) {
						throw new Exception (" failed query "+query+" "+e.getMessage());
					}
					pstmt.close();	 
			//Lastly commit 
					  connection.commit();
					  result = true;
		  
		  }catch(Exception e){
			  connection.rollback();
			  result = false;
		  PPWalletEnvironment.setComment(1, classname,"The exception in method verifyPendingMerch  is  "+e.getMessage());
		  throw new Exception  ("The exception in method verifyPendingMerch  is  "+e.getMessage());
		  }finally{
				if(connection!=null) {
					try {
						super.close();
					} catch (SQLException e) {
						e.printStackTrace();
						}
					} 
		  if(pstmt!=null) pstmt.close(); 
		  }

		return result;
	}
	


	 public boolean updateDispute(String referenceNo, String status) throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			String query = null;
			boolean result = false;
			try{
				 connection = super.getConnection();
				 connection.setAutoCommit(false);
				 									//		1		  	2		
				 query = " update dispute_details set status=? where referenceno=?";
				 
				 
						pstmt = connection.prepareStatement(query);
						pstmt.setString(1, status);	
						pstmt.setString(2, referenceNo);
						try {
							pstmt.executeUpdate();
							}catch(Exception e) {
								throw new Exception (" failed query "+query+" "+e.getMessage());
							}
						connection.commit();
						result = true;
			}catch(Exception e){
				connection.rollback(); result = false;
				throw new Exception ("The exception in method updateDispute  is  "+e.getMessage());
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


	public ConcurrentHashMap<String, String> getMerchantCategories() throws Exception{

		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		//Wallet wallet = null;
		ConcurrentHashMap<String, String> hash_Categories = null;
		try{
			connection = super.getConnection();	

			query = "select mcccategoryid, mcccategoryname  from merch_mcc_group  ";
			
			pstmt = connection.prepareStatement(query);
			rs = (ResultSet)pstmt.executeQuery();
			
			 if(rs!=null){
				 hash_Categories = new ConcurrentHashMap<String, String>();
				 	while(rs.next()){	 			 			
				 		hash_Categories.put(StringUtils.trim(rs.getString("mcccategoryid")) , StringUtils.trim(rs.getString("mcccategoryname")) );
				 		} // end of while
				 	//arr_Product.add(m_Product);
				 	} //end of if rs!=null check
			 // validate the password
			 if(hash_Categories!=null)
				 if(hash_Categories.size()==0)
					 hash_Categories=null;
			
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getMerchantCategories  is  "+e.getMessage());
			throw new Exception ("The exception in method getMerchantCategories  is  "+e.getMessage());			
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return hash_Categories;
		
		
	}


	public ArrayList<SystemMsfPlans> getAllMSF() throws Exception {
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<SystemMsfPlans> arrSystemMsfPlans = null;
	
		try{
		//	PPWalletEnvironment.setComment(3, classname, "now in getAllMsfPlanLists dao" );

			connection = super.getConnection();	

			query = "select planid, plan_name,   plan_fee_fixed,  plan_fee_var,  plan_type, desposit_fee, set_up_fee, monthly_fee,  annual_fee,"
					+ " statement_fee,  late_payment_fee,   plan_cycle,	status, created_on	from merch_sys_msf_plan where status = ? order by  planid ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, "A");
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrSystemMsfPlans = new ArrayList<SystemMsfPlans>();
				 	while(rs.next()){	 
				 		SystemMsfPlans m_SystemMsfPlans=new SystemMsfPlans();
				 		m_SystemMsfPlans.setPlanId( StringUtils.trim(rs.getString("planid"))   );
				 		m_SystemMsfPlans.setPlanName( StringUtils.trim(rs.getString("plan_name"))  );
				 		m_SystemMsfPlans.setPlanFeeFixed( StringUtils.trim(rs.getString("plan_fee_fixed"))  );
				 		m_SystemMsfPlans.setPlanFeeVar(StringUtils.trim(rs.getString("plan_fee_var"))  );
				 		m_SystemMsfPlans.setPlanType( StringUtils.trim(rs.getString("plan_type"))  );
				 		m_SystemMsfPlans.setPlanDepositFee( StringUtils.trim(rs.getString("desposit_fee"))  );
				 		m_SystemMsfPlans.setPlanSetUpFee( StringUtils.trim(rs.getString("set_up_fee"))  );
				 		m_SystemMsfPlans.setPlanMonthlyFee( StringUtils.trim(rs.getString("monthly_fee"))  );
				 		m_SystemMsfPlans.setPlanAnnualFee( StringUtils.trim(rs.getString("annual_fee"))  );
				 		m_SystemMsfPlans.setPlanStatementFee( StringUtils.trim(rs.getString("statement_fee"))  );
				 		m_SystemMsfPlans.setPlanLatePaymentFee( StringUtils.trim(rs.getString("late_payment_fee"))  );
				 		m_SystemMsfPlans.setPlanCycle( StringUtils.trim(rs.getString("plan_cycle"))  );
				 		m_SystemMsfPlans.setPlanCreatedOn( StringUtils.trim(rs.getString("created_on"))  );
				 		m_SystemMsfPlans.setStatus( StringUtils.trim(rs.getString("status"))  );
				 		arrSystemMsfPlans.add(m_SystemMsfPlans);
				 		} // end of while
				 	} //end of if rs!=null check
				PPWalletEnvironment.setComment(3, classname, "setPlanCreatedOn is here" );
			 if(arrSystemMsfPlans!=null) if(arrSystemMsfPlans.size()==0) arrSystemMsfPlans=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllMsfPlanLists  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllMsfPlanLists  is  "+e.getMessage());			
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return arrSystemMsfPlans;
		
	}


	public boolean addMSFToAMerchant(String billerCode, String planIds) throws Exception{
		PreparedStatement pstmt=null;
		 Connection connection = null; 
		 String query = null;
		 boolean result = false;
		 String totalPlanIds[] = null;
		 
		 try{ 
			 connection = super.getConnection();
			 connection.setAutoCommit(false); 
		 
			 totalPlanIds = planIds.split(",");
			 
															//		1			2		3        4       
					query = "insert into merch_msf_plan_relation (billercode, planid, status, createdon ) "
					+ "values (?, ?, ?, ? ) ";
							// 1  2  3  4  
					pstmt = connection.prepareStatement(query);
					for(int i=0; i<totalPlanIds.length; i++  ) {	
						pstmt.setString(1, billerCode); 
						pstmt.setInt(2, Integer.parseInt( totalPlanIds[i] ) );
						pstmt.setString(3,  "A"    );
						pstmt.setString(4,   Utilities.getMYSQLCurrentTimeStampForInsert()      );
						pstmt.addBatch();
						if ((i + 1) % 1000 == 0 || (i + 1) == totalPlanIds.length) {
							try {
							pstmt.executeBatch();
								}catch (Exception e) {
									throw new Exception (" failed query "+query+" "+e.getMessage());
								}
							}
					}
					
					pstmt.clearBatch();
					pstmt.close();
					PPWalletEnvironment.setComment(2,classname,"1. Executed query "+query);			 

		  connection.commit(); result =  true; 
		  }catch(Exception e){ 
			  connection.rollback(); 
			  result = false;
			  PPWalletEnvironment.setComment(1, classname,"The exception in method addMSFToAMerchant  is  "+e.getMessage()); 
			  throw new Exception ("The exception in method addMSFToAMerchant  is  "+e.getMessage());
		 }finally{
				if(connection!=null) {
					try {
						super.close();
					} catch (SQLException e) {
						e.printStackTrace();
						}
					}
			 if(pstmt!=null) pstmt.close(); if(totalPlanIds!=null) totalPlanIds = null;
			 } 
		 return result; 
	 }


	public ArrayList<SystemMsfPlans> getAllMSFPlansForAMerchant(String billerCode) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		ResultSet rs=null;
		String query = null;
		ArrayList<SystemMsfPlans> arrSystemMsfPlans = null;
	
		try{
			connection = super.getConnection();	

			query = " select a.billercode billercode, a.planid planid, a.status status, a.createdon createdon , b.plan_name plan_name, " + 
					"  b.plan_type plan_type, b.plan_fee_fixed plan_fee_fixed, b.plan_fee_var plan_fee_var from merch_msf_plan_relation  a, merch_sys_msf_plan b where a.billercode=? and a.planid=b.planid ";
			
			pstmt = connection.prepareStatement(query);
			pstmt.setString(1, billerCode);
			rs = (ResultSet)pstmt.executeQuery();			
			 if(rs!=null){
				 arrSystemMsfPlans = new ArrayList<SystemMsfPlans>();
				 	while(rs.next()){	 
				 		SystemMsfPlans m_SystemMsfPlans=new SystemMsfPlans();
				 		m_SystemMsfPlans.setBillerCode( StringUtils.trim(rs.getString("billercode"))   );
				 		m_SystemMsfPlans.setStatus(StringUtils.trim(rs.getString("status"))   );
				 		m_SystemMsfPlans.setPlanCreatedOn( StringUtils.trim(rs.getString("createdon"))  );
				 		m_SystemMsfPlans.setPlanName( StringUtils.trim(rs.getString("plan_name"))  );
				 		m_SystemMsfPlans.setPlanType( StringUtils.trim(rs.getString("plan_type"))  );
				 		m_SystemMsfPlans.setPlanFeeFixed( StringUtils.trim(rs.getString("plan_fee_fixed"))  );
				 		m_SystemMsfPlans.setPlanFeeVar(StringUtils.trim(rs.getString("plan_fee_var"))  );
				 		m_SystemMsfPlans.setPlanId(StringUtils.trim(rs.getString("planid"))  );

				 		arrSystemMsfPlans.add(m_SystemMsfPlans);
				 		} // end of while
				 	} //end of if rs!=null check
			 if(arrSystemMsfPlans!=null) if(arrSystemMsfPlans.size()==0) arrSystemMsfPlans=null;
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,classname,"The exception in method getAllMSFPlansForAMerchant  is  "+e.getMessage());
			throw new Exception ("The exception in method getAllMSFPlansForAMerchant  is  "+e.getMessage());			
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
				if(rs!=null) rs.close();
				if(pstmt!=null) pstmt.close();
			}
		return arrSystemMsfPlans;
		
	}


	public boolean updateMSFStatusForBiller(String billerCode, String planId, String status) throws Exception{
		PreparedStatement pstmt=null;
		Connection connection = null;
		String query = null;
		boolean result = false;
		try{
			 connection = super.getConnection();
			 connection.setAutoCommit(false);
			 									//			1		  		2				3			
			 query = " update merch_msf_plan_relation set status=? where billercode=? and planid=?";
					pstmt = connection.prepareStatement(query);
					pstmt.setString(1,status); 					
					pstmt.setString(2, billerCode);	
					pstmt.setInt(3, Integer.parseInt(planId));	

					try {
						pstmt.executeUpdate();
						}catch(Exception e) {
							throw new Exception (" failed query "+query+" "+e.getMessage());
						}
					connection.commit();
					result = true;
		}catch(Exception e){
			connection.rollback(); result = false;
			PPWalletEnvironment.setComment(1,classname,"The exception in method updateMSFStatusForBiller  is  "+e.getMessage());
			throw new Exception ("The exception in method updateMSFStatusForBiller  is  "+e.getMessage());
		}finally{
			if(connection!=null) {
				try {
					super.close();
				} catch (SQLException e) {
					e.printStackTrace();
					}
				}
			if(pstmt!=null) pstmt.close();
		}
		return result;	
		}


public ArrayList<Transaction> getAllTransactions() throws Exception {
			PreparedStatement pstmt=null;
			Connection connection = null;
			ResultSet rs=null;
			String query = null;
			ArrayList<Transaction> arrTransactions = null;
			try{
				connection = super.getConnection();	

				query = "select txncode, walletid, sysreference, txnamount, txncurrencyid, txnmode, txndatetime from wallet_txn_bc ";
				
				pstmt = connection.prepareStatement(query);
				rs = (ResultSet)pstmt.executeQuery();			
				 if(rs!=null){
					 arrTransactions = new ArrayList<Transaction>();
					 	while(rs.next()){	 
					 		Transaction m_Transaction=new Transaction();
					 		
					 		m_Transaction.setTxnCode( StringUtils.trim(rs.getString("txncode"))    );
					 		m_Transaction.setWalletId( StringUtils.trim(rs.getString("walletid"))  );
					 		m_Transaction.setSystemReference (StringUtils.trim(rs.getString("sysreference"))  );
					 		m_Transaction.setTxnAmount(StringUtils.trim(rs.getString("txnamount"))    );
					 		m_Transaction.setTxnCurrencyId( StringUtils.trim(rs.getString("txncurrencyid"))    );	
					 		m_Transaction.setTxnMode( StringUtils.trim(rs.getString("txnmode"))    );	
					 		m_Transaction.setTxnDateTime( StringUtils.trim(rs.getString("txndatetime"))    );	
					 		arrTransactions.add(m_Transaction);
					 		} // end of while
					 	
					 	} //end of if rs!=null check
				 if(arrTransactions!=null) if(arrTransactions.size()==0) arrTransactions=null;
			}catch(Exception e){
				PPWalletEnvironment.setComment(1,classname,"The exception in method getAllTransactions is  "+e.getMessage());
				throw new Exception ("The exception in method getAllTransactions is  "+e.getMessage());			
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
	
	



	


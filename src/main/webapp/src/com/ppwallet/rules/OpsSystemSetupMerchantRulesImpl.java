package com.ppwallet.rules;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.DisputeDao;
import com.ppwallet.dao.MerchDisputeDao;
import com.ppwallet.dao.OpsMerchantManageDao;
import com.ppwallet.dao.OpsSystemManageMerchantDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.dao.UserLoginDao;
import com.ppwallet.model.DisputeReasons;
import com.ppwallet.model.DisputeTracker;
import com.ppwallet.model.Disputes;
import com.ppwallet.model.MCC;
import com.ppwallet.model.MerchantInstitution;
import com.ppwallet.model.SystemMsfPlans;
import com.ppwallet.model.User;
import com.ppwallet.utilities.Utilities;

public class OpsSystemSetupMerchantRulesImpl implements Rules {
	private static String className = OpsSystemSetupMerchantRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session = request.getSession(false);
		switch (rules) {
		case Rules.OPS_SYS_MERCH_MCCMANAGE_PAGE:
			try {
				PPWalletEnvironment.setComment(3, className, "Beginning: ");
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "sysmerch"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_MCCMANAGE_PAGE); // set the last rule for left menu
																						// selection
				ArrayList<MCC> arrAllMCC = (ArrayList<MCC>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMCCList();
				PPWalletEnvironment.setComment(3, className, "after MCCList: " + arrAllMCC);
				request.setAttribute("allMCClist", arrAllMCC);
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchMCCManagePage()).forward(request, response);
				} finally {
					if (arrAllMCC != null)	arrAllMCC = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;

		case Rules.OPS_SYS_MERCH_EDITMCC:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "sysmerch"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_MCCMANAGE_PAGE); // set the last rule for left menu
																						// selection
				String MCCCategoryId = null; String MCCCategoryDescription = null; String MCCCatFromRange = null;
				String MCCCatToRange = null; String MCCGenericId = null;

				String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				String moduleCode = "M"; // M = Merchants Acquiring
				ArrayList<MCC> arrAllMCC = null;

				if (request.getParameter("hdnmcccatid") != null) MCCCategoryId = StringUtils.trim(request.getParameter("hdnmcccatid"));
				if (request.getParameter("mcccatdesc") != null) MCCCategoryDescription = StringUtils.trim(request.getParameter("mcccatdesc"));
				if (request.getParameter("mccfromrange") != null) MCCCatFromRange = StringUtils.trim(request.getParameter("mccfromrange"));
				if (request.getParameter("mcctorange") != null) MCCCatToRange = StringUtils.trim(request.getParameter("mcctorange"));
				if (request.getParameter("mcccgeneric") != null) MCCGenericId = StringUtils.trim(request.getParameter("mcccgeneric"));

				if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().updateMCCList(MCCCategoryId, MCCCategoryDescription, MCCCatFromRange, MCCCatToRange, MCCGenericId)) {
					// call the audit trail here
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,"Edit MCC Id " + MCCCategoryId);
					arrAllMCC = (ArrayList<MCC>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMCCList();
					request.setAttribute("allMCClist", arrAllMCC);
				} else {
					throw new Exception("Problem with the addition of BIN");
				}
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchMCCManagePage()).forward(request, response);
				} finally {
					if (arrAllMCC != null) arrAllMCC = null;	if (MCCCategoryId != null) MCCCategoryId = null;	if (MCCCategoryDescription != null) MCCCategoryDescription = null;
					if (MCCCatFromRange != null) MCCCatFromRange = null;	if (MCCCatToRange != null) MCCCatToRange = null;	if (MCCGenericId != null) MCCGenericId = null;
					if (userId != null) userId = null;	if (userType != null) userType = null;	if (moduleCode != null) moduleCode = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;

		case Rules.OPS_SYS_MERCH_ADDMCC:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "sysmerch"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_MCCMANAGE_PAGE); // set the last rule for left menu
																						// selection

				PPWalletEnvironment.setComment(3, className, "Before declaring");

				String MCCCategoryId = null;	String MCCCategoryDescription = null; 	String MCCCatFromRange = null;
				String MCCCatToRange = null;	String MCCGenericId = null;

				String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				String moduleCode = "M"; // M = Merchants Acquiring
				ArrayList<MCC> arrAllMCC = null;

				if (request.getParameter("mcccateid") != null) MCCCategoryId = StringUtils.trim(request.getParameter("mcccateid"));
				if (request.getParameter("mcccatdesc") != null) MCCCategoryDescription = StringUtils.trim(request.getParameter("mcccatdesc"));
				if (request.getParameter("mccfromrange") != null) MCCCatFromRange = StringUtils.trim(request.getParameter("mccfromrange"));
				if (request.getParameter("mcctorange") != null) MCCCatToRange = StringUtils.trim(request.getParameter("mcctorange"));
				if (request.getParameter("mcccgeneric") != null) MCCGenericId = StringUtils.trim(request.getParameter("mcccgeneric"));

				if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().addNewMCC(MCCCategoryId,MCCCategoryDescription, MCCCatFromRange, MCCCatToRange, MCCGenericId)) {
					// call the audit trail here
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Added MCC CatId" + MCCCategoryId, 0, 48));
					arrAllMCC = (ArrayList<MCC>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMCCList();
					request.setAttribute("allMCClist", arrAllMCC);
				} else {
					throw new Exception("Problem with the addition of MCC");
				}
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchMCCManagePage()).forward(request,response);
				} finally {
					if (arrAllMCC != null)arrAllMCC = null;
					if (MCCCategoryId != null) MCCCategoryId = null;
					if (MCCCategoryDescription != null) MCCCategoryDescription = null;
					if (MCCCatFromRange != null) MCCCatFromRange = null;
					if (MCCCatToRange != null) MCCCatToRange = null;
					if (MCCGenericId != null) MCCGenericId = null;
					if (userId != null) userId = null;
					if (userType != null) userType = null;
					if (moduleCode != null) moduleCode = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;


		case Rules.OPS_SYS_MERCH_INST_CREATE_NEW_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "sysmerch");
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_INST_CREATE_NEW_PAGE); // set the last rule for
																							// left menu selection
				// ** get the Institution details
				MerchantInstitution m_MerchantInstitution = null;
				m_MerchantInstitution = (MerchantInstitution) OpsSystemManageMerchantDao.class.getConstructor()
						.newInstance().getInsitutionDetails();
				request.setAttribute("merchinstitution", m_MerchantInstitution);
				response.setContentType("text/html");
				try {

					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchNewInstitutionPage()).forward(request,
							response);
				} finally {
					if (m_MerchantInstitution != null)
						m_MerchantInstitution = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;

		case Rules.OPS_SYS_MERCH_ADD_NEW_INST:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "sysmerch");
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_INST_CREATE_NEW_PAGE); // set the last rule for
																							// left menu selection
				// ** get the Institution details
				MerchantInstitution m_MerchantInstitution = null;
				String institutionName = null;  String addressLine1 = null;	String addressLine2 = null;	String addressCity = null;	String addressPincode = null;	String institutionTaxId = null;
				String currencyId = null;	String merchantServiceFee = null;	String interchangeFeeVariable = null;	String interchangeFeeFixed = null;	String statementCycle = null;
				String institutionBin = null;	String registrationNo = null;
				PPWalletEnvironment.setComment(3, className, "Starting");
				
				
				if (request.getParameter("instname") != null)	institutionName = StringUtils.trim(request.getParameter("instname"));
				if (request.getParameter("address1") != null)	addressLine1 = StringUtils.trim(request.getParameter("address1"));
				if (request.getParameter("address2") != null)	addressLine2 = StringUtils.trim(request.getParameter("address2"));
				if (request.getParameter("city") != null)		addressCity = StringUtils.trim(request.getParameter("city"));
				if (request.getParameter("postcode") != null)	addressPincode = StringUtils.trim(request.getParameter("postcode"));
				if (request.getParameter("taxno") != null)		institutionTaxId = StringUtils.trim(request.getParameter("taxno"));
				if (request.getParameter("hdncurrency") != null)	currencyId = StringUtils.trim(request.getParameter("hdncurrency"));
				if (request.getParameter("merchservfee") != null)	merchantServiceFee = StringUtils.trim(request.getParameter("merchservfee"));
				if (request.getParameter("varichrate") != null)		interchangeFeeVariable = StringUtils.trim(request.getParameter("varichrate"));
				if (request.getParameter("fixichrate") != null)		interchangeFeeFixed = StringUtils.trim(request.getParameter("fixichrate"));
				if (request.getParameter("statecycle") != null)		statementCycle = StringUtils.trim(request.getParameter("statecycle"));
				if (request.getParameter("acqinstbin") != null)		institutionBin = StringUtils.trim(request.getParameter("acqinstbin"));
				if (request.getParameter("regno") != null)		registrationNo = StringUtils.trim(request.getParameter("regno"));

				if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().addNewInstitution(institutionName,addressLine1, addressLine2, addressCity, addressPincode, institutionTaxId, currencyId,
				merchantServiceFee, interchangeFeeVariable, interchangeFeeFixed, statementCycle, institutionBin, registrationNo) == false) {
					PPWalletEnvironment.setComment(3, className, "Problem inserting new merch inst");
				}
				String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				String moduleCode = "M"; // M = Merchants Acquiring
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,	StringUtils.substring("Added Merch Int " + institutionName, 0, 48));
				m_MerchantInstitution = (MerchantInstitution) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getInsitutionDetails();
				response.setContentType("text/html");
				
				try {
					request.setAttribute("merchinstitution", m_MerchantInstitution);
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchNewInstitutionPage()).forward(request,
							response);
				} finally {
					if (m_MerchantInstitution != null)	m_MerchantInstitution = null;	if (institutionName != null)	institutionName = null;
					if (addressLine1 != null)	addressLine1 = null;	if (addressLine2 != null)addressLine2 = null;
					if (addressCity != null)	addressCity = null;		if (addressPincode != null)	addressPincode = null;
					if (institutionTaxId != null)	institutionTaxId = null;	if (currencyId != null)	currencyId = null;
					if (merchantServiceFee != null)	merchantServiceFee = null;	if (interchangeFeeVariable != null)	interchangeFeeVariable = null;
					if (interchangeFeeFixed != null)	interchangeFeeFixed = null;	if (statementCycle != null)	statementCycle = null;
					if (institutionBin != null)	institutionBin = null;	if (registrationNo != null)	registrationNo = null;

				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}

			break;

		case Rules.OPS_SYS_MERCH_EDIT_INST:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "sysmerch");
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_INST_CREATE_NEW_PAGE); // set the last rule for
																							// left menu selection
				// ** get the Institution details
				MerchantInstitution m_MerchantInstitution = null;
				String institutionName = null; String addressLine1 = null; String addressLine2 = null; String addressCity = null;
				String addressPincode = null; String institutionTaxId = null; String currencyId = null; String merchantServiceFee = null;  String interchangeFeeVariable = null;
				String interchangeFeeFixed = null; String statementCycle = null; String institutionBin = null; String registrationNo = null;  String institutionId = null;
				
				if (request.getParameter("hdninstitutionid") != null) institutionId = StringUtils.trim(request.getParameter("hdninstitutionid"));
				if (request.getParameter("instname") != null) institutionName = StringUtils.trim(request.getParameter("instname"));
				if (request.getParameter("address1") != null) addressLine1 = StringUtils.trim(request.getParameter("address1"));
				if (request.getParameter("address2") != null) addressLine2 = StringUtils.trim(request.getParameter("address2"));
				if (request.getParameter("city") != null) addressCity = StringUtils.trim(request.getParameter("city"));
				if (request.getParameter("postcode") != null) addressPincode = StringUtils.trim(request.getParameter("postcode"));
				if (request.getParameter("taxno") != null) institutionTaxId = StringUtils.trim(request.getParameter("taxno"));
				if (request.getParameter("hdncurrency") != null) currencyId = StringUtils.trim(request.getParameter("hdncurrency"));
				if (request.getParameter("merchservfee") != null) merchantServiceFee = StringUtils.trim(request.getParameter("merchservfee"));
				if (request.getParameter("varichrate") != null) interchangeFeeVariable = StringUtils.trim(request.getParameter("varichrate"));
				if (request.getParameter("fixichrate") != null) interchangeFeeFixed = StringUtils.trim(request.getParameter("fixichrate"));
				if (request.getParameter("statecycle") != null) statementCycle = StringUtils.trim(request.getParameter("statecycle"));
				if (request.getParameter("acqinstbin") != null) institutionBin = StringUtils.trim(request.getParameter("acqinstbin"));
				if (request.getParameter("regno") != null) registrationNo = StringUtils.trim(request.getParameter("regno"));

				if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().editMerchInstitution(institutionId,
						institutionName, addressLine1, addressLine2, addressCity, addressPincode, institutionTaxId,
						currencyId, merchantServiceFee, interchangeFeeVariable, interchangeFeeFixed, statementCycle,
						institutionBin, registrationNo)) {
					String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
					String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
					String moduleCode = "M"; // M = Merchants Acquiring
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,
							StringUtils.substring("Edited Merch institution Id " + institutionId, 0, 48));
					m_MerchantInstitution = (MerchantInstitution) OpsSystemManageMerchantDao.class.getConstructor()
							.newInstance().getInsitutionDetails();
				}
				response.setContentType("text/html");
				try {
					request.setAttribute("merchinstitution", m_MerchantInstitution);

					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchNewInstitutionPage()).forward(request,
							response);
				} finally {
					if (m_MerchantInstitution != null) m_MerchantInstitution = null;
					if (institutionName != null) institutionName = null;
					if (addressLine1 != null) addressLine1 = null;
					if (addressLine2 != null) addressLine2 = null;
					if (addressCity != null) addressCity = null;
					if (addressPincode != null) addressPincode = null;
					if (institutionTaxId != null) institutionTaxId = null;
					if (currencyId != null) currencyId = null;
					if (merchantServiceFee != null) merchantServiceFee = null;
					if (interchangeFeeVariable != null) interchangeFeeVariable = null;
					if (interchangeFeeFixed != null) interchangeFeeFixed = null;
					if (statementCycle != null) statementCycle = null;
					if (institutionBin != null) institutionBin = null;
					if (registrationNo != null) registrationNo = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;

		case Rules.OPS_SYS_MERCH_SETMSFPLAN_PAGE:
			try {
				 PPWalletEnvironment.setComment(3, className, "in : OPS_SYS_MERCH_SETMSFPLAN_PAGE");

				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "sysmerch"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_SETMSFPLAN_PAGE); // set the last rule for left
																						// menu selection
				ArrayList<SystemMsfPlans> arrAllSystemMsfPlans = (ArrayList<SystemMsfPlans>) OpsSystemManageMerchantDao.class .getConstructor().newInstance().getAllMsfPlanLists();
			 PPWalletEnvironment.setComment(3, className, "after MSFPlanLists: "+arrAllSystemMsfPlans);
				request.setAttribute("allmsfplanlist", arrAllSystemMsfPlans);
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchSetMSFPlanPage()).forward(request, response);
				} finally {
					if (arrAllSystemMsfPlans != null)
						arrAllSystemMsfPlans = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;

		case Rules.OPS_SYS_MERCH_ADD_MSFPLAN:
			try {
				PPWalletEnvironment.setComment(3, className, "in up OPS_SYS_MERCH_ADD_MSFPLAN");

				//PPWalletEnvironment.setComment(3, className, "0: " + Rules.OPS_SYS_MERCH_ADD_MSFPLAN);
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "sysmerch"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_SETMSFPLAN_PAGE); // set the last rule for left
																						// menu selection
				ArrayList<SystemMsfPlans> arrAllSystemMsfPlans = null;
				PPWalletEnvironment.setComment(3, className, "1: " + Rules.OPS_SYS_MERCH_ADD_MSFPLAN);
				String planName = null; String depositFee = null;	String setUpFee = null; String monthlyFee = null; String annualFee = null;
				String statementFee = null; String latePaymentFee = null; String cycleDate = null; String status = null; String planFeeFixed = null;
				String planFeeVar = null; String planType = null; String createdOn = null;

				String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				String moduleCode = "M"; // M = Merchants Acquiring

				//PPWalletEnvironment.setComment(3, className, "2" + Rules.OPS_SYS_MERCH_ADD_MSFPLAN);

				//if (request.getParameter("planid") != null) planId = StringUtils.trim(request.getParameter("planid"));
				if (request.getParameter("planname") != null) planName = StringUtils.trim(request.getParameter("planname"));
				if (request.getParameter("feefixed") != null) planFeeFixed = StringUtils.trim(request.getParameter("feefixed"));
				if (request.getParameter("feevar") != null) planFeeVar = StringUtils.trim(request.getParameter("feevar"));
				if (request.getParameter("plantype") != null) planType = StringUtils.trim(request.getParameter("plantype"));
				if (request.getParameter("depofee") != null) depositFee = StringUtils.trim(request.getParameter("depofee"));
				if (request.getParameter("setup") != null) setUpFee = StringUtils.trim(request.getParameter("setup"));
				if (request.getParameter("monthlylfee") != null) monthlyFee = StringUtils.trim(request.getParameter("monthlylfee"));
				if (request.getParameter("annualfee") != null) annualFee = StringUtils.trim(request.getParameter("annualfee"));
				if (request.getParameter("statementfee") != null) statementFee = StringUtils.trim(request.getParameter("statementfee"));
				if (request.getParameter("latepay") != null) latePaymentFee = StringUtils.trim(request.getParameter("latepay"));
				if (request.getParameter("pcycle") != null) cycleDate = StringUtils.trim(request.getParameter("pcycle"));
				if (request.getParameter("status") != null) status = StringUtils.trim(request.getParameter("status"));
				if (request.getParameter("created") != null) createdOn = StringUtils.trim(request.getParameter("created"));

				PPWalletEnvironment.setComment(3, className, "3 " + Rules.OPS_SYS_MERCH_ADD_MSFPLAN);

				if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().addMerchantSystemMSFPlan(planName, depositFee, setUpFee, monthlyFee, annualFee, statementFee, 
						latePaymentFee, cycleDate, status, planFeeFixed, planFeeVar, planType, createdOn)) {
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Added MSF Plan " + planName, 0, 48));
				}
				arrAllSystemMsfPlans = (ArrayList<SystemMsfPlans>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMsfPlanLists();
				request.setAttribute("allmsfplanlist", arrAllSystemMsfPlans);
				response.setContentType("text/html");
				PPWalletEnvironment.setComment(3, className, "4 " + Rules.OPS_SYS_MERCH_ADD_MSFPLAN);
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchSetMSFPlanPage()).forward(request,
							response);
				} finally {
					if (arrAllSystemMsfPlans != null)
						arrAllSystemMsfPlans = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;

		case Rules.OPS_SYS_MERCH_EDIT_MSFPLAN:
			try {
				PPWalletEnvironment.setComment(3, className, "in up OPS_SYS_MERCH_EDIT_MSFPLAN");

				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "sysmerch"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_SETMSFPLAN_PAGE); // set the last rule for left
																						// menu selection
				ArrayList<SystemMsfPlans> arrAllSystemMsfPlans = null;
				String planId = null; String planName= null; String planFeeFixed= null; String planFeeVar= null; String planType= null;
				String depositFee= null; String setUpFee = null; String monthlyFee = null; String annualFee = null; String statementFee = null; String latePaymentFee = null; 
				String cycleDate = null; String status = null; String planCreatedOn = null;
				
				String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				String moduleCode = "M"; // M = Merchants Acquiring

				PPWalletEnvironment.setComment(3, className, " passed here" + Rules.OPS_SYS_MERCH_EDIT_MSFPLAN);
				
				if (request.getParameter("planid") != null) planId = StringUtils.trim(request.getParameter("planid"));
				if (request.getParameter("planname") != null) planName = StringUtils.trim(request.getParameter("planname"));
				if (request.getParameter("feefixed") != null) planFeeFixed = StringUtils.trim(request.getParameter("feefixed"));
				if (request.getParameter("feevar") != null) planFeeVar = StringUtils.trim(request.getParameter("feevar"));
				if (request.getParameter("plantype") != null) planType = StringUtils.trim(request.getParameter("plantype"));
				if (request.getParameter("depofee") != null) depositFee = StringUtils.trim(request.getParameter("depofee"));
				if (request.getParameter("setup") != null) setUpFee = StringUtils.trim(request.getParameter("setup"));
				if (request.getParameter("monthlylfee") != null) monthlyFee = StringUtils.trim(request.getParameter("monthlylfee"));
				if (request.getParameter("annualfee") != null) annualFee = StringUtils.trim(request.getParameter("annualfee"));
				if (request.getParameter("statementfee") != null) statementFee = StringUtils.trim(request.getParameter("statementfee"));
				if (request.getParameter("latepay") != null) latePaymentFee = StringUtils.trim(request.getParameter("latepay"));
				if (request.getParameter("pcycle") != null) cycleDate = StringUtils.trim(request.getParameter("pcycle"));
				if (request.getParameter("selstatus") != null) status = StringUtils.trim(request.getParameter("selstatus"));
				if (request.getParameter("created") != null) planCreatedOn = StringUtils.trim(request.getParameter("created"));
				
				PPWalletEnvironment.setComment(3, className, "in OPS_SYS_MERCH_EDIT_MSFPLAN");
				 PPWalletEnvironment.setComment(3,className,"planId "+ planId  +"planName "+ planName  + "planFeeFixed " +  planFeeFixed  +  "planFeeVar  " + planFeeVar + "planType "+ planType 
						 +"depositFee "+ depositFee + "setUpFee " + setUpFee  +"monthlyFee " + monthlyFee+ "annualFee " + annualFee + "statementFee " + statementFee + "latePaymentFee  "+latePaymentFee 
						 +"cycleDate "+ cycleDate + "status "+  status +  "createdOn "+ planCreatedOn );
				if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().editMerchantSystemMSFPlan(planId, planName, planFeeFixed, planFeeVar, planType, depositFee, setUpFee, monthlyFee, annualFee, 
						statementFee, latePaymentFee, cycleDate, planCreatedOn,status)) {
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Edited MSF PlanId " + planId, 0, 48));
				}
				arrAllSystemMsfPlans = (ArrayList<SystemMsfPlans>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMsfPlanLists();
				request.setAttribute("allmsfplanlist", arrAllSystemMsfPlans);
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchSetMSFPlanPage()).forward(request,
							response);
				} finally {
					if (arrAllSystemMsfPlans != null)
						arrAllSystemMsfPlans = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;


		case Rules.OPS_MERCH_DISPUTE_REASONS_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opsdisp"); // going to manage merchant menu
				request.setAttribute("lastrule", Rules.OPS_MERCH_DISPUTE_REASONS_PAGE); // set the last rule for left
																						// menu selection
				String userType = "";
				ArrayList<DisputeReasons> arrAllDispReasons = (ArrayList<DisputeReasons>) OpsSystemManageMerchantDao.class .getConstructor().newInstance().getAllDisputeReasons(userType);
				request.setAttribute("allDisputeReasons", arrAllDispReasons);
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchDisputeReasons()).forward(request,
							response);
				} finally {
					if (arrAllDispReasons != null)
						arrAllDispReasons = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;

		case Rules.OPS_MERCH_DISPUTE_REASONS_ADD:
			try {
				if (session.getAttribute("SESS_USER") == null)
					throw new Exception("Session has expired, please log in again");

				String reasonDesc = null; String reasonID = null; String status = null; 
				String userType = null; String userId = null; String payMode = null;

				userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				userType = ((User) session.getAttribute("SESS_USER")).getUserType();

				if (request.getParameter("reasonid") != null) reasonID = StringUtils.trim(request.getParameter("reasonid"));
				if (request.getParameter("reasondesc") != null)  reasonDesc = StringUtils.trim(request.getParameter("reasondesc"));
				if (request.getParameter("hdnusertype") != null) userType = StringUtils.trim(request.getParameter("hdnusertype"));
				if (request.getParameter("hdnstatus") != null) status = StringUtils.trim(request.getParameter("hdnstatus"));
				if (request.getParameter("paymode") != null) payMode = StringUtils.trim(request.getParameter("paymode"));
				

				if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().addDisputeReasonCode(reasonID, reasonDesc,userType,status, payMode) == false) {
					throw new Exception("Problem in inserting dispute for Customer");
				}

				String moduleCode = "O"; // M = Customer Acquiring

				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Ops ID:" + userId, 0, 48));
				//request.setAttribute("alldisputes", (ArrayList<DisputeTracker>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllDisputeCodes());
				
				request.setAttribute("allDisputeReasons", (ArrayList<DisputeReasons>) OpsSystemManageMerchantDao.class .getConstructor().newInstance().getAllDisputeReasons(""));
				response.setContentType("text/html");
				ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchDisputeReasons()).forward(request, response);
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}

			break;
		case Rules.OPS_MERCH_DISPUTE_REASONS_EDIT:
			try {
				if (session.getAttribute("SESS_USER") == null)
					throw new Exception("Session has expired, please log in again");

				String reasonDesc = null; String reasonID = null; String status = null; 
				String userType = null; String userId = null; String payMode = null;

				userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				userType = ((User) session.getAttribute("SESS_USER")).getUserType();

				if (request.getParameter("hdnreasonid") != null) reasonID = StringUtils.trim(request.getParameter("hdnreasonid"));
				if (request.getParameter("reasondesc") != null)  reasonDesc = StringUtils.trim(request.getParameter("reasondesc"));
				if (request.getParameter("hdnusertype") != null) userType = StringUtils.trim(request.getParameter("hdnusertype"));
				if (request.getParameter("hdnstatus") != null) status = StringUtils.trim(request.getParameter("hdnstatus"));
				if (request.getParameter("paymode_e") != null) payMode = StringUtils.trim(request.getParameter("paymode_e"));
				

				if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().editDisputeReasonCode(reasonID, reasonDesc,userType,status, payMode) == false) {
					throw new Exception("Problem in inserting dispute for Customer");
				}

				String moduleCode = "O"; // M = Customer Acquiring

				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Ops ID:" + userId, 0, 48));
				//request.setAttribute("alldisputes", (ArrayList<DisputeTracker>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllDisputeCodes());
				
				request.setAttribute("allDisputeReasons", (ArrayList<DisputeReasons>) OpsSystemManageMerchantDao.class .getConstructor().newInstance().getAllDisputeReasons(""));
				
				try {
					response.setContentType("text/html");
				ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchDisputeReasons()).forward(request, response);
				}finally {
							// flush all objects here
						}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}

			break;			
		case Rules.OPS_MERCH_VIEW_MERCH_DISPUTE_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opsdisp"); // going to manage merchant menu
				request.setAttribute("lastrule", Rules.OPS_MERCH_VIEW_MERCH_DISPUTE_PAGE);
				if (session.getAttribute("SESS_USER") == null)
					throw new Exception("Session has expired, please log in again");
				String billerCode = null;

				billerCode = ((User) session.getAttribute("SESS_USER")).getBillerCode();
				request.setAttribute("allmerchantdisputes", (ArrayList<DisputeTracker>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllDisputes("M"));

				ctx.getRequestDispatcher(PPWalletEnvironment.getViewDisputePageOps()).forward(request, response);

			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}

			break;
			
			
		case Rules.OPS_MERCH_RAISE_MERCHANT_DISPUTE_PAGE:
			try {
				PPWalletEnvironment.setComment(3, className, "Start");
				 if (session.getAttribute("SESS_USER") == null) throw new
				 Exception("Session has expired, please log in again");
				  
				 String reasonId = null; String transactionId = null;
				 String userComment = null; String status = null; String raisedOn = null;
				 String userId = null; String userType = null; String disputeId = null; String systemReference = null; String m_userType = null;
				 
				 
				 userId = ((User) session.getAttribute("SESS_USER")).getUserId(); // raisedbyuser
				 m_userType =((User) session.getAttribute("SESS_USER")).getUserType();// usertype
		
				  if (request.getParameter("addtransid") != null) transactionId = StringUtils.trim(request.getParameter("addtransid")); 
				  if (request.getParameter("hdndispreasonid") != null) reasonId = StringUtils.trim(request.getParameter("hdndispreasonid")); 
				  if (request.getParameter("refno") != null) systemReference = StringUtils.trim(request.getParameter("refno")); // actually biller code from jsp
				  if (request.getParameter("usercomment") != null) userComment = StringUtils.trim(request.getParameter("usercomment")); 
				  if (request.getParameter("hdnselstatus") != null) status = StringUtils.trim(request.getParameter("hdnselstatus")); 
				  if (request.getParameter("usertype") != null) userType = StringUtils.trim(request.getParameter("usertype")); 
				  
				  raisedOn = Utilities.getMYSQLCurrentTimeStampForInsert();
				  
				  if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().addNewDispute(userId, reasonId, systemReference, transactionId, userComment, userType, status, raisedOn) == false) { throw new Exception("Problem in inserting dispute for Customer"); }
				
				String moduleCode = "O"; // M = Merchants Acquiring
				 
						 
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, m_userType, moduleCode,StringUtils.substring("Raised Dispute for merchant "+systemReference, 0, 48));
				request.setAttribute("allmerchantdisputes", (ArrayList<DisputeTracker>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllDisputes("M"));
				try {
				response.setContentType("text/html");
				ctx.getRequestDispatcher(PPWalletEnvironment.getViewDisputePageOps()).forward(request, response);
				}finally {
					// flush all objects here
				}


			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}

			break;

		
		case Rules.OPS_RAISE_DISPUTE_PAGE:
			try {
				PPWalletEnvironment.setComment(3, className, "Beginning:");
				
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opsdisp"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_RAISE_DISPUTE_PAGE); // set the last rule for left menu
				
			//	request.setAttribute("disputereasons", (ArrayList<DisputeTracker>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllDisputes());
				
				request.setAttribute("allDisputeReasons", (ArrayList<DisputeReasons>) OpsSystemManageMerchantDao.class .getConstructor().newInstance().getAllDisputeReasons("M"));
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getMerchRaiseDisputePageOps()).forward(request,
							response);
				} finally {
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;
			
		case Rules.OPS_EDIT_DISPUTE_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opsdisp"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_EDIT_DISPUTE_PAGE); // set the last rule for left menu
				
				
				String disputeId = null; String reasonID = null; String raisedBy = null; String status = null; String raisedOn = null;	String referenceNo=null;

				String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				
				String moduleCode = "M"; // M = Merchants Acquiring
				ArrayList<Disputes> arrAllDisputes = null;

				if (request.getParameter("hdnrefno") != null) referenceNo = StringUtils.trim(request.getParameter("hdnrefno"));
				if (request.getParameter("dispid") != null) disputeId = StringUtils.trim(request.getParameter("dispid"));
				if (request.getParameter("reasonid") != null) reasonID = StringUtils.trim(request.getParameter("reasonid"));
				if (request.getParameter("raisedby") != null) raisedBy = StringUtils.trim(request.getParameter("raisedby"));
				if (request.getParameter("hdnselstatus") != null) status = StringUtils.trim(request.getParameter("hdnselstatus"));
				if (request.getParameter("raisedon") != null) raisedOn = StringUtils.trim(request.getParameter("raisedon"));
				PPWalletEnvironment.setComment(3, className, "the values"+ referenceNo +"|"+ status);

				if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().updateDispute(referenceNo, status)) {
					// call the audit trail here
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,"Edit Dispute" + disputeId);
					//arrAllDisputes = (ArrayList<Disputes>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMerchantDisputes(referenceNo);
					request.setAttribute("allmerchantdisputes", (ArrayList<DisputeTracker>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllDisputes("M"));
					
				//	request.setAttribute("allmerchantdisputes", arrAllDisputes);
				} else {
					throw new Exception("Problem with editing dispute");
				}
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getViewDisputePageOps()).forward(request, response);
				} finally {
					if (arrAllDisputes != null) arrAllDisputes = null;	if (disputeId != null) disputeId = null;	if (reasonID != null) reasonID = null;
					if (raisedBy != null) raisedBy = null;	if (status != null) status = null;	if (raisedOn != null) raisedOn = null; if (referenceNo != null) referenceNo = null; 
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;
			
		case Rules.OPS_SYS_MERCH_VIEW_MSF:
			try {
				if (session.getAttribute("SESS_USER") == null)
					throw new Exception("Session has expired, please log in again");
				
				request.setAttribute("lastaction", "opsmerch"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_VIEW_MSF); // set the last rule for left menu

				String billerCode = null;

				billerCode = ((User) session.getAttribute("SESS_USER")).getBillerCode();
				request.setAttribute("allmsf", (ArrayList<SystemMsfPlans>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMSF());

				ctx.getRequestDispatcher(PPWalletEnvironment.getOpsViewMSF()).forward(request, response);

			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}

			break;
			
		case Rules.OPS_MERCH_ALLOCATE_MSF_PLANS:
			
			try {
				if (session.getAttribute("SESS_USER") == null)
					throw new Exception("Session has expired, please log in again");
				
				String billerCode = null;  String planIds = null;
				String moduleCode = "M"; // M = Merchants Acquiring
				String userId = ((User) session.getAttribute("SESS_USER")).getUserId(); String userType =  ((User) session.getAttribute("SESS_USER")).getUserType();
				
				request.setAttribute("lastaction", "opsmerch"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_VIEW_MSF); // set the last rule for left menu

				if (request.getParameter("billercode") != null) billerCode = StringUtils.trim(request.getParameter("billercode"));
				if (request.getParameter("hdnplanid") != null) planIds = StringUtils.trim(request.getParameter("hdnplanid"));
				if(planIds.equals("")) {
					throw new Exception ("Plan ids are sent blank here");
				}
				
				
				boolean result = (boolean)OpsSystemManageMerchantDao.class.getConstructor().newInstance().addMSFToAMerchant(billerCode, planIds);
				if(result)
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, "O", moduleCode,StringUtils.substring("Adding Plans for merchant "+billerCode+" by Ops ID:" + userId, 0, 48));
	
				try {
					response.setContentType("text/html");
					request.setAttribute("allmsfplansforbiller", (ArrayList<SystemMsfPlans>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMSFPlansForAMerchant(billerCode));
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsViewMSFPlansForBiller()).forward(request, response);

				}finally {
					if(moduleCode!=null) moduleCode = null;
				}
				
			}catch(Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
		
		
		break;
		case Rules.OPS_MERCH_UPDATE_PLANSTATUS_FOR_BILLER:
			
			try {
				if (session.getAttribute("SESS_USER") == null)
					throw new Exception("Session has expired, please log in again");
				
				String billerCode = null;  String planId = null;  String status = null;
				String moduleCode = "M"; // M = Merchants Acquiring
				String userId = ((User) session.getAttribute("SESS_USER")).getUserId(); String userType =  ((User) session.getAttribute("SESS_USER")).getUserType();
				
				request.setAttribute("lastaction", "opsmerch"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_SYS_MERCH_VIEW_MSF); // set the last rule for left menu

				if (request.getParameter("hdnbillercode") != null) billerCode = StringUtils.trim(request.getParameter("hdnbillercode"));
				if (request.getParameter("hdnplanid") != null) planId = StringUtils.trim(request.getParameter("hdnplanid"));

				if (request.getParameter("hdnstatus") != null) status = StringUtils.trim(request.getParameter("hdnstatus"));
				
				if(status.equals("")) {
					throw new Exception ("status is sent blank here");
				}
				
				
				boolean result = (boolean)OpsSystemManageMerchantDao.class.getConstructor().newInstance().updateMSFStatusForBiller(billerCode, planId, status);
				if(result)
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, "O", moduleCode,StringUtils.substring("Updating Plan status for merchant "+billerCode+" by Ops ID:" + userId, 0, 48));
	
				try {
					response.setContentType("text/html");
					request.setAttribute("allmsfplansforbiller", (ArrayList<SystemMsfPlans>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMSFPlansForAMerchant(billerCode));
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsViewMSFPlansForBiller()).forward(request, response);

				}finally {
					if(moduleCode!=null) moduleCode = null;
				}
				
			}catch(Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
		
		
		break;
		
		case Rules.OPS_VIEW_SPECIFIC_PAGE:
			try {
				if(session.getAttribute("SESS_USER")==null)
					throw new Exception ("Session has expired, please log in again");
				
					String disputeId = null;	
					if(request.getParameter("hdnreqid")!=null)	disputeId = StringUtils.trim( request.getParameter("hdnreqid") );

					request.setAttribute("showdispute", (Disputes)OpsMerchantManageDao.class.getConstructor().newInstance().getDisputeDetail(disputeId));
//					request.setAttribute("disputethreads", (ArrayList<DisputeTracker>)OpsMerchantManageDao.class.getConstructor().newInstance().getAllDisputeTrackers(disputeId));
					try{
						ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSpecificDisputePage()).forward(request, response);
					}finally {
						if(disputeId!=null) disputeId=null; 
					}
				

				
			}catch(Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			
			break;
			
		case Rules.OPS_UPDATE_DISPUTE_STATUS:
			try {
				if(session.getAttribute("SESS_USER")==null)
					throw new Exception ("Session has expired, please log in again");
				
				String disputeid = null;	String status = null;
				String userId = null; String userType = null;
				
//				relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
				
				if(request.getParameter("hdndispid")!=null)		disputeid = StringUtils.trim( request.getParameter("hdndispid") );
				if(request.getParameter("hdnstatus")!=null)		status = StringUtils.trim( request.getParameter("hdnstatus") );
				
				PPWalletEnvironment.setComment(2, className, "status is : "+status);
				PPWalletEnvironment.setComment(2, className, "id is : "+disputeid);

				if( OpsMerchantManageDao.class.getConstructor().newInstance().updateDisputeStatus(disputeid, status) == false) {
					throw new Exception ("Problem in updating dispute status");
				}
				
				//audit trail
				String moduleCode = "O"; //M = Customer Acquiring
				userId = ((User)session.getAttribute("SESS_USER")).getUserId();
				userType = ((User)session.getAttribute("SESS_USER")).getUserType();
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Operator updated dispute status "+userId, 0, 48) );
				request.setAttribute("allmerchantdisputes", (ArrayList<DisputeTracker>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllDisputes("M"));
				try {
					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getViewDisputePageOps()).forward(request, response);
				}finally {
					if(disputeid!=null) disputeid=null; if(status!=null) status=null;  if(userType!=null) userType=null; if(userId!=null) userId=null;
				}
				
			}catch(Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;
			
		case Rules.OPS_ADD_DISPUTE_COMMENT:
			try {
				if(session.getAttribute("SESS_USER")==null)
					throw new Exception ("Session has expired, please log in again");

				String disputeId = null;  String comment = null; String userType = null; String userId = null; 
			
				if(request.getParameter("hdndispid")!=null)	disputeId = StringUtils.trim( request.getParameter("hdndispid") );
				if(request.getParameter("hdncomment")!=null)	comment = StringUtils.trim( request.getParameter("hdncomment") );
				PPWalletEnvironment.setComment(3, className, "dispute comment is : "+comment);
				if ( DisputeDao.class.getConstructor().newInstance().addCommentOnADispute(disputeId, 
				((User)session.getAttribute("SESS_USER")).getUserId(), ((User)session.getAttribute("SESS_USER")).getUserType(), comment ) == false ) {
					throw new Exception ("Problem in adding a new comment on the disputeid : "+disputeId);
				}
				//audit trail
				String moduleCode = "O"; //M = Customer Acquiring
				userId = ((User)session.getAttribute("SESS_USER")).getUserId();
				userType = ((User)session.getAttribute("SESS_USER")).getUserType();
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Operator added dispute comment "+userId, 0, 48) );
				
				request.setAttribute("showdispute", (Disputes)OpsMerchantManageDao.class.getConstructor().newInstance().getDisputeDetail(disputeId));

				request.setAttribute("disputethreads", (ArrayList<DisputeTracker>)OpsMerchantManageDao.class.getConstructor().newInstance().getAllDisputeTrackers(disputeId));
			
				try {
					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSpecificDisputePage()).forward(request, response);
				}finally {
					if(disputeId!=null) disputeId=null; if(comment!=null) comment=null;  if(userType!=null) userType=null; if(userId!=null) userId=null;
				}
				
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;
		
		
		}

	}
	

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		switch (rulesaction) {
		case Rules.JSON_OPS_LOGIN_VALIDATE:
		case Rules.JSON_CUST_LOGIN_VALIDATE:
			PrintWriter jsonOutput_1 = null;
			try {
				String userId = null; String userPwd = null; String userType = null; String privateKey = null;
				boolean allow = true;

				if (jsonObj.get("userid") != null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if (jsonObj.get("userpwd") != null) userPwd = jsonObj.get("userpwd").toString().replaceAll("\"", "");
				if (jsonObj.get("usertype") != null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				if (jsonObj.get("pvtkey") != null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");

				if (!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className,
							"Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect " + privateKey);
				}

				User user = null;
				user = (User) UserLoginDao.class.getConstructor().newInstance().validateUser(userId, userPwd, userType);
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); // Json Object
				if (user != null && allow) {
					/*
					 * m_Transaction.setSequenceNo( StringUtils.trim(rs.getString("sequenceno")) );
					 * m_Transaction.setTxnCode( StringUtils.trim(rs.getString("txncode")) );
					 * m_Transaction.setWalletId(StringUtils.trim(rs.getString("walletid")) );
					 * m_Transaction.setSystemReference(StringUtils.trim(rs.getString("sysreference"
					 * )) ); m_Transaction.setTxnAmount(StringUtils.trim(rs.getString("tnmamount"))
					 * );
					 * m_Transaction.setTxnCurrencyId(StringUtils.trim(rs.getString("txncurrencyid")
					 * ) ); m_Transaction.setTxnMode(StringUtils.trim(rs.getString("txnmode")) );
					 * m_Transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndatetime")) );
					 * String[] txnCodeArray = new String[arr_Transaction.size()]; String[]
					 * txnSysRefArray = new String [arr_Transaction.size()]; String[] txnAmountArray
					 * = new String[arr_Transaction.size()]; String[] txnCurrencyIdArray = new
					 * String [arr_Transaction.size()]; String[] txnModeArray = new
					 * String[arr_Transaction.size()]; String[] txnDateTimeArray = new
					 * String[arr_Transaction.size()]; String[] txnWalletIdArray = new
					 * String[arr_Transaction.size()]; for(int i=0;i<arr_Transaction.size();i++){
					 * txnCodeArray[i]= ((Transaction)arr_Transaction.get(i)).getTxnCode();
					 * txnWalletIdArray[i]= ((Transaction)arr_Transaction.get(i)).getWalletId();
					 * txnSysRefArray[i]=
					 * ((Transaction)arr_Transaction.get(i)).getSystemReference();
					 * txnAmountArray[i]= ((Transaction)arr_Transaction.get(i)).getTxnAmount();
					 * txnCurrencyIdArray[i]=
					 * ((Transaction)arr_Transaction.get(i)).getTxnCurrencyId(); txnModeArray[i]=
					 * ((Transaction)arr_Transaction.get(i)).getTxnMode(); txnDateTimeArray[i]=
					 * Utilities.getUTCtoYourTimeZoneConvertor(((Transaction)arr_Transaction.get(i))
					 * .getTxnDateTime(), "BET") ; } obj.add("txncode",
					 * gson.toJsonTree(txnCodeArray)); obj.add("txnwalletid",
					 * gson.toJsonTree(txnWalletIdArray)); obj.add("txnsysref",
					 * gson.toJsonTree(txnSysRefArray)); obj.add("txnamount",
					 * gson.toJsonTree(txnAmountArray)); obj.add("txncurrencyid",
					 * gson.toJsonTree(txnWalletIdArray)); obj.add("txnmode",
					 * gson.toJsonTree(txnModeArray)); obj.add("txndatetime", gson.toJsonTree(
					 * txnDateTimeArray));
					 */

					obj.add("userid", gson.toJsonTree(user.getUserId()));
					obj.add("username", gson.toJsonTree(user.getUserName()));
					obj.add("useremail", gson.toJsonTree(user.getEmailId()));
					obj.add("usertype", gson.toJsonTree(user.getUserType()));
					obj.add("error", gson.toJsonTree("false"));
				} else {
					obj.add("error", gson.toJsonTree("true"));
				}
				// PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE
				// String is "+gson.toJson(obj));
				jsonOutput_1.print(gson.toJson(obj));
				jsonOutput_1.close();
			} catch (Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE " + e.getMessage());
			} finally {
				if (jsonOutput_1 != null)
					jsonOutput_1.close();
			}
			break;

		case Rules.JSON_GET_KEY:
			PrintWriter jsonOutput_2 = null;
			try {
				jsonOutput_2 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); // Json Object

				obj.add("pkey", gson.toJsonTree(PPWalletEnvironment.getAPIKeyPrivate()));
				obj.add("error", gson.toJsonTree("false"));
				// PPWalletEnvironment.setComment(3, className, " JSON JSON_GET_KEY String is
				// "+gson.toJson(obj));
				jsonOutput_2.print(gson.toJson(obj));
				jsonOutput_2.close();
			} catch (Exception e) {

			} finally {
				if (jsonOutput_2 != null)
					jsonOutput_2.close();

			}

			break;

		}

	}

	@Override
	public void callException(HttpServletRequest request, HttpServletResponse response, ServletContext ctx,
			HttpSession session, Exception e, String msg) throws Exception {
		try {
			if (session != null)
				session.invalidate();
			PPWalletEnvironment.setComment(1, className, "Error is " + msg);
			request.setAttribute("errormsg", msg);
			response.setContentType("text/html");
			ctx.getRequestDispatcher(PPWalletEnvironment.getErrorPage()).forward(request, response);
		} catch (Exception e1) {
			PPWalletEnvironment.setComment(1, className,
					"Problem in forwarding to Error Page, error : " + e1.getMessage());
		}
	}

	@Override
	public void performUploadOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			List<FileItem> multiparts, ServletContext ctx) throws Exception {

	}

}

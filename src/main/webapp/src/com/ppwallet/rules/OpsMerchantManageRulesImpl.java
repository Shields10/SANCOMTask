package com.ppwallet.rules;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

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
import com.ppwallet.dao.OpsMerchantManageDao;
import com.ppwallet.dao.OpsSystemManageCardsDao;
import com.ppwallet.dao.OpsSystemManageCustomerDao;
import com.ppwallet.dao.OpsSystemManageLoyaltyDao;
import com.ppwallet.dao.OpsSystemManageMerchantDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.dao.UserLoginDao;
import com.ppwallet.model.AuditTrail;
import com.ppwallet.model.CallLog;
import com.ppwallet.model.CardBIN;
import com.ppwallet.model.LoyaltyRules;
import com.ppwallet.model.MCC;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.SystemMsfPlans;
import com.ppwallet.model.User;
import com.ppwallet.model.Wallet;
import com.ppwallet.utilities.Utilities;

public class OpsMerchantManageRulesImpl implements Rules {
	private static String className = OpsMerchantManageRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(false);
		switch (rules){
		case Rules.OPS_MERCH_SET_MERCHANTS_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opsmerch");
				request.setAttribute("lastrule", Rules.OPS_MERCH_SET_MERCHANTS_PAGE); 
				
				ArrayList<Merchant> arrAllMerchants = (ArrayList<Merchant>) OpsMerchantManageDao.class.getConstructor().newInstance().getAllMerchantsBrief();
				PPWalletEnvironment.setComment(3, className, "after merchantdetails: " + arrAllMerchants);
				request.setAttribute("allMerchantsbrief", arrAllMerchants);
				request.setAttribute("mccvalues", (ConcurrentHashMap<String,String>)OpsSystemManageMerchantDao.class.getConstructor().newInstance().getMerchantCategories());
				
				response.setContentType("text/html");
				ctx.getRequestDispatcher(PPWalletEnvironment.getOpsMerchManagePage()).forward(request,response);
				
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}			
		break;
		
		
		case Rules.OPS_MERCHANT_EDIT:
			 try { 
				  request.setAttribute("langPref",  "en"); 
				  request.setAttribute("lastaction", "opsmerch"); 
				  request.setAttribute("lastrule", Rules.OPS_MERCHANT_EDIT);
				  
				  String planID = null;  String planDesc = null; String planDepFee = null; String planSetupFee = null;
				  String planMonthlyFee = null;  String planAnnualFee = null; String planStatFee = null; String planLatePayment = null;
				  String planCycle = null;  String planStatus = null; 
				  
				  String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				  String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				  String moduleCode = "M"; ArrayList<Merchant> arrMerchantDetails = null;
				  
				  if (request.getParameter("editplanid") != null) planID = StringUtils.trim(request.getParameter("editplanid")); 
				  if (request.getParameter("editplandesc") != null) planDesc = StringUtils.trim(request.getParameter("editplandesc")); 
				  if (request.getParameter("editplandepfee") != null) planDepFee = StringUtils.trim(request.getParameter("editplandepfee")); 
			      if (request.getParameter("editplsetupfee") != null) planSetupFee =  StringUtils.trim(request.getParameter("editplsetupfee"));
			      if (request.getParameter("editplmonthfee") != null) planMonthlyFee = StringUtils.trim(request.getParameter("editplmonthfee")); 
				  if (request.getParameter("editplannualfee") != null) planAnnualFee = StringUtils.trim(request.getParameter("editplannualfee")); 
				  
				  if (request.getParameter("editplatepayfee") != null) planStatFee = StringUtils.trim(request.getParameter("editplatepayfee")); 
			      if (request.getParameter("editplatepayfee") != null) planLatePayment =  StringUtils.trim(request.getParameter("editplatepayfee"));
				  if (request.getParameter("editplcydate") != null) planCycle = StringUtils.trim(request.getParameter("editplcydate")); 
			      if (request.getParameter("seleditstatus") != null) planStatus =  StringUtils.trim(request.getParameter("seleditstatus"));
			      
			      
			      
				  PPWalletEnvironment.setComment(3, className,  "Plan ID to be edited"+ planID );

				  
				  if (OpsMerchantManageDao.class.getConstructor().newInstance().opsEditMerchDetails(planID,planDesc, planDepFee, planSetupFee, planMonthlyFee, planAnnualFee, planStatFee, planLatePayment,planCycle,planStatus)) {
				  
				  SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Edit Merchant" + userId, 0,  48));
				  arrMerchantDetails = (ArrayList<Merchant>) OpsSystemManageMerchantDao.class.getConstructor().newInstance(). getAllPendingMerchants(); 
				  request.setAttribute("allmerchantdetails", arrMerchantDetails); 
				  } else { throw new Exception("Problem with the edit of status"); }
				  response.setContentType("text/html"); 
				  try {
				  ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchantDetailsPage()).forward(  request,response);
				  
				  } finally {
					  if (arrMerchantDetails != null)arrMerchantDetails =  null; 
					  if (planID != null) planID = null;
					  if (planDesc != null) planDesc = null;
					  if (planDepFee != null) planDepFee = null; 
					  if (planSetupFee != null) planSetupFee = null; 
					  if (planMonthlyFee != null)  planMonthlyFee = null; 
					  if (planAnnualFee != null) planAnnualFee = null;
					  
					  if (planDesc != null) planDesc = null;
					  if (planStatFee != null) planStatFee = null; 
					  if (planLatePayment != null) planLatePayment = null; 
					  if (planCycle != null)  planCycle = null; 
					  if (planStatus != null) planStatus = null;
				  } 
				  
				  } catch  (Exception e) { callException(request, response, ctx, session, e,  e.getMessage()); 
				  
				  } 
			 break;
		
		
		case Rules.OPS_MERCH_VIEW_PENDING_MERCHANTS_PAGE:
			try {
				//PPWalletEnvironment.setComment(3,className,"passing  OPS_MERCH_VIEW_PENDING_MERCHANTS_PAGE  ");

				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "dash"); // going to dashboard
				request.setAttribute("lastrule", Rules.OPS_MERCH_VIEW_PENDING_MERCHANTS_PAGE); //  set the last rule for left menu selection
				
				ArrayList<Merchant> arrMerchantDetails  = (ArrayList<Merchant>)OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllPendingMerchants();
				if(arrMerchantDetails!=null)
					//PPWalletEnvironment.setComment(2,className,"arrMerchantDetails size  is  "+arrMerchantDetails.size());
				
				request.setAttribute("allmerchantdetails", arrMerchantDetails);
				response.setContentType("text/html");
			
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchantDetailsPage()).forward(request,response);
				} finally {
					if(arrMerchantDetails!=null) arrMerchantDetails=null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
		break;
		
		
		///from****************
		case Rules.OPS_MERCH_VERIFY_PENDING_MERCHANTS:
			 try { 
			  request.setAttribute("langPref",  "en"); request.setAttribute("lastaction", "dash"); // going to profile
			  request.setAttribute("lastrule", Rules.OPS_MERCH_VIEW_PENDING_MERCHANTS_PAGE);
			  
			  PPWalletEnvironment.setComment(3, className, "at OPS_MERCH_VERIFY_PENDING_MERCHANTS");
			  
			  String billerCode = null;  String status = null; String merchantId = null; String nationalId = null;
			  
			  String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
			  String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
			  String moduleCode = "M"; ArrayList<Merchant> arrMerchantDetails = null;
			  
			  //if (request.getParameter("billercode") != null) billerCode = StringUtils.trim(request.getParameter("billercode")); 
			 // if (request.getParameter("merchid") != null) merchantId = StringUtils.trim(request.getParameter("merchid")); 
			 // if (request.getParameter("nationalid") != null) nationalId = StringUtils.trim(request.getParameter("nationalid")); 
		      //if (request.getParameter("selstatus") != null) status =  StringUtils.trim(request.getParameter("selstatus"));
			  PPWalletEnvironment.setComment(3, className,  "billerCode"+ billerCode + "status" + status);

			  
			  if (OpsSystemManageMerchantDao.class.getConstructor().newInstance().verifyPendingMerch(billerCode,merchantId, nationalId, status)) {
			  
			  SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Edit billerCode" + billerCode, 0,  48));
			  arrMerchantDetails = (ArrayList<Merchant>) OpsSystemManageMerchantDao.class.getConstructor().newInstance(). getAllPendingMerchants(); 
			  request.setAttribute("allmerchantdetails", arrMerchantDetails); 
			  } else { throw new Exception("Problem with the edit of status"); }
			  response.setContentType("text/html"); 
			  try {
			  ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchantDetailsPage()).forward(  request,response);
			  
			  } finally {
				  if (arrMerchantDetails != null)arrMerchantDetails =  null; 
				  if (billerCode != null) billerCode = null;
				  if (merchantId != null) merchantId = null;
				  if (nationalId != null) nationalId = null; 
				  if (userId != null) userId = null; 
				  if (userType != null)  userType = null; 
				  if (moduleCode != null) moduleCode = null;
			  } 
			  
			  } catch  (Exception e) { callException(request, response, ctx, session, e,  e.getMessage()); 
			  
			  } 
			  break;
		
		
		
		
		case Rules.OPS_MERCHANT_DOWNLOAD_KYC_DOC:
			String fileName = null; 
			  PrintWriter out_download1= null;
		  
			  try { 		  
		  
				  if(request.getParameter("hdnfilename")!=null) fileName = request.getParameter("hdnfilename").trim();
				  PPWalletEnvironment.setComment(3, className, "Before Download the fileName "+fileName);
				  out_download1 = response.getWriter();
				  String filePath = PPWalletEnvironment.getFileDownloadPath();
				  response.setContentType("APPLICATION/OCTET-STREAM");
				  response.setHeader("Content-Disposition", "attachment; filename=\"" +fileName + "\"");
			  
				  FileInputStream fileInputStream = new FileInputStream(filePath +"/"+ fileName);
				  PPWalletEnvironment.setComment(3, className, "Total path is : "+filePath +"/"+ fileName);
				  int i; 
				  while ((i = fileInputStream.read()) != -1)
				  { 
					  out_download1.write(i);
				  }
				  fileInputStream.close();
			  
			  
			  	}catch
			  		(Exception e) { 
			  			callException(request, response, ctx, session, e, e.getMessage()); 
			  		}finally {
			  			if (out_download1!=null)	out_download1.close();
			  		}
			  break;
		
		
		case Rules.OPS_MERCH_VIEW_CALL_LOGS:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opsmerch"); 
				request.setAttribute("lastrule", "Merchant Call Logs");
				
				ArrayList <CallLog> arrMerchantCallLogs= (ArrayList<CallLog>)OpsMerchantManageDao.class.getConstructor().newInstance().getAllMerchantCallLogs();
				PPWalletEnvironment.setComment(2,className," ---> 1 before setting to request the arrMerchantCallLogs object   is  "+arrMerchantCallLogs);
				request.setAttribute("allmerchcalllogs", arrMerchantCallLogs);
				PPWalletEnvironment.setComment(2,className," ---> 2  after setting to request the arrMerchantCallLogs object    ");
				if(arrMerchantCallLogs!=null) {
					PPWalletEnvironment.setComment(2,className,"arrMerchantCallLogs size  is  "+arrMerchantCallLogs.size());
				}
				try {
					response.setContentType("text/html");
					PPWalletEnvironment.setComment(3,className," before submitting to jsp  "+PPWalletEnvironment.getOpsSysMerchCallLog());
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchCallLog()).forward(request,response);
				}catch (Exception e) {
					
				}finally {
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;
			
		case Rules.OPS_MERCH_ADD_CALL_LOGS:
			 
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opscust"); // going to profile
				request.setAttribute("lastrule", "Merchant Call Logs"); // set the last rule for left menu
																						// selection

				PPWalletEnvironment.setComment(3, className, "Before declaring");

				String UserType = null;	String ReferenceNo = null; 	String UserName = null;
				String CallDescription= null;	String Comment = null; String CalledOn = null;

				String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				String moduleCode = "M"; // 
				ArrayList<CallLog> arrMerchantCallLogs = null;

				if (request.getParameter("usertype") != null) UserType = StringUtils.trim(request.getParameter("usertype"));
				if (request.getParameter("referenceno") != null) ReferenceNo = StringUtils.trim(request.getParameter("referenceno"));
				if (request.getParameter("username") != null) UserName = StringUtils.trim(request.getParameter("username"));
				if (request.getParameter("calldescription") != null) CallDescription = StringUtils.trim(request.getParameter("calldescription"));
				if (request.getParameter("comment") != null) Comment = StringUtils.trim(request.getParameter("comment"));
				if (request.getParameter("calledon") != null) CalledOn = StringUtils.trim(request.getParameter("calledon"));

				if (OpsMerchantManageDao.class.getConstructor().newInstance().addNewCallLog(UserType,ReferenceNo, userId, CallDescription, Comment, CalledOn)) {
					// call the audit trail here
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Added UserId" + userId, 0, 48));
					arrMerchantCallLogs= (ArrayList<CallLog>)OpsMerchantManageDao.class.getConstructor().newInstance().getAllMerchantCallLogs();
					request.setAttribute("allmerchcalllogs", arrMerchantCallLogs);
				} else {
					throw new Exception("Problem with the addition of MCC");
				}
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchCallLog()).forward(request,response);
				} finally {
					if (arrMerchantCallLogs != null)arrMerchantCallLogs = null;
					if (UserType != null) UserType = null;
					if (ReferenceNo != null) ReferenceNo = null;
					if (UserName != null) UserName = null;
					if (CallDescription != null) CallDescription = null;
					if (Comment != null) Comment = null;
					if (userId != null) userId = null;
					if (userType != null) userType = null;
					if (moduleCode != null) moduleCode = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;
			
		case Rules.OPS_MERCH_SHOWSPECIFIC_MERCH_PAGE:
			try{
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opsmerch"); // going to profile
				request.setAttribute("lastrule", "Set Merchants"); // set the last rule for left menu
				
				
				String billerCode = null; String verifyMerchFlag = "";
				if (request.getParameter("hdnbillercode") != null) billerCode = StringUtils.trim(request.getParameter("hdnbillercode"));
				if (request.getParameter("hdnmerchverify") != null) verifyMerchFlag = StringUtils.trim(request.getParameter("hdnmerchverify"));
				request.setAttribute("verifymerchantflag", verifyMerchFlag );

				request.setAttribute("allmcclist", (ArrayList<MCC>)OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMCCList());
				
				request.setAttribute("specificmerchant", (Merchant)OpsMerchantManageDao.class.getConstructor().newInstance().showSpecificMerchant(billerCode));
				request.setAttribute("kycdocslist", (ArrayList<String>)OpsMerchantManageDao.class.getConstructor().newInstance().getAllKYCDocsForMerchant(billerCode));
			//added	
				//ArrayList<Merchant> arrAllMerchants = (ArrayList<Merchant>) OpsMerchantManageDao.class.getConstructor().newInstance().getAllMerchantsBrief();
				//PPWalletEnvironment.setComment(3, className, "after merchantdetails: " + arrAllMerchants);
				//request.setAttribute("allMerchantsbrief", arrAllMerchants);
				
				try {
					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsEditSpecificMerchanPage()).forward(request,response);
					//ctx.getRequestDispatcher(PPWalletEnvironment.getOpsMerchManagePage()).forward(request,response);

				}finally {
					
					if (billerCode != null)billerCode = null;
				}
				
			}catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			
		break;
		//edit spec*******************************************
		case Rules.OPS_MERCH_EDITSPECIFIC_MERCH_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opsmerch"); // going to profile
				request.setAttribute("lastrule", "Set Merchants"); // set the last rule for left menu
				
				String billerCode = null;  String merchantId = null;  String password =null;  String merchantName = null;  String nationalId = null;  String email = null;  String address1 = null;  String address2 = null;  
				String pinCode = null;  String companyName = null;  String compRegistration = null;  String mccId =null;  String status = null;  String expiry =null;  String contact =null;		String city = null;
				String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				String moduleCode = "M";  String verifyMerchFlag = "";
				

				if (request.getParameter("hdnbillercode") != null) billerCode = StringUtils.trim(request.getParameter("hdnbillercode"));
				if (request.getParameter("mpassword") != null) password = StringUtils.trim(request.getParameter("mpassword"));
				if (request.getParameter("merchantname") != null) merchantName = StringUtils.trim(request.getParameter("merchantname"));
				if (request.getParameter("mnationalid") != null) nationalId = StringUtils.trim(request.getParameter("mnationalid"));
				if (request.getParameter("memail") != null) email = StringUtils.trim(request.getParameter("memail"));
				if (request.getParameter("maddress1") != null) address1 = StringUtils.trim(request.getParameter("maddress1"));
				if (request.getParameter("maddress2") != null) address2 = StringUtils.trim(request.getParameter("maddress2"));
				if (request.getParameter("mpincode") != null) pinCode = StringUtils.trim(request.getParameter("mpincode"));
				if (request.getParameter("mcompanyname") != null) companyName = StringUtils.trim(request.getParameter("mcompanyname"));
				if (request.getParameter("ncompanyreg") != null) compRegistration = StringUtils.trim(request.getParameter("ncompanyreg"));
				if (request.getParameter("hdnmcccode") != null) mccId = StringUtils.trim(request.getParameter("hdnmcccode"));
				if (request.getParameter("hdnstatus") != null) status = StringUtils.trim(request.getParameter("hdnstatus"));
				if (request.getParameter("mexpiry") != null) expiry = StringUtils.trim(request.getParameter("mexpiry"));
				if (request.getParameter("mcontact") != null) contact = StringUtils.trim(request.getParameter("mcontact"));
				if (request.getParameter("city") != null) city = StringUtils.trim(request.getParameter("city"));
				if (request.getParameter("hdnverifymerchflag") != null) verifyMerchFlag = StringUtils.trim(request.getParameter("hdnverifymerchflag"));
				
				
				boolean result  = (boolean)OpsMerchantManageDao.class.getConstructor().newInstance().updateSpecificMerchant( billerCode, password, merchantName, nationalId,  email, address1, address2, city,  pinCode, contact, companyName, compRegistration,  mccId, status, expiry, verifyMerchFlag  );
				
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Updated merchant of billercode: " + billerCode, 0, 48));

				request.setAttribute("allmcclist", (ArrayList<MCC>)OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllMCCList());
				
				request.setAttribute("specificmerchant", (Merchant)OpsMerchantManageDao.class.getConstructor().newInstance().showSpecificMerchant(billerCode));
				request.setAttribute("kycdocslist", (ArrayList<String>)OpsMerchantManageDao.class.getConstructor().newInstance().getAllKYCDocsForMerchant(billerCode));

				ArrayList<Merchant> arrAllMerchants = (ArrayList<Merchant>) OpsMerchantManageDao.class.getConstructor().newInstance().getAllMerchantsBrief();
				PPWalletEnvironment.setComment(3, className, "after merchantdetails: " + arrAllMerchants);
				request.setAttribute("allMerchantsbrief", arrAllMerchants);
				
				try {
					response.setContentType("text/html");
					//ctx.getRequestDispatcher(PPWalletEnvironment.getOpsEditSpecificMerchanPage()).forward(request,response);
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsMerchManagePage()).forward(request,response);

				
				}finally {
					if (billerCode != null)billerCode = null; if (merchantId != null)merchantId = null; if (password != null)password = null;
					if (merchantName != null)merchantName = null; if (nationalId != null)nationalId = null; if (email != null)email = null;
					if (address1 != null)address1 = null; if (address2 != null)address2 = null; if (pinCode != null)pinCode = null;
					if (companyName != null)companyName = null; if (compRegistration != null)compRegistration = null; if (mccId != null)mccId = null;
					if (status != null)status = null; if (expiry != null)expiry = null; if (contact != null)contact = null;if (city != null)city = null;
					
				}
				
			}catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			
		break;
			
		}
	}
		
		//========== delete from here onwards when finished
					
		
			
			
			
			
				
				
 

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		switch (rulesaction){
		case Rules.JSON_OPS_MERCH_GETSPCMERCH:	
			PrintWriter jsonOutput_1 = null;
			try {
				String walletId = null;	String userPwd = null;	String userType = null; String privateKey = null;	boolean allow = true; String userId = null;
						
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("userpwd")!=null) userPwd = jsonObj.get("userpwd").toString().replaceAll("\"", "");
				if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect "+privateKey);
				}

				User user=null;
				user=(User)UserLoginDao.class.getConstructor().newInstance().validateUser(userId, userPwd, userType);
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				if(user!=null && allow) {
					/*
					 * 		m_Transaction.setSequenceNo( StringUtils.trim(rs.getString("sequenceno"))    );
					 		m_Transaction.setTxnCode( StringUtils.trim(rs.getString("txncode"))  );
					 		m_Transaction.setWalletId(StringUtils.trim(rs.getString("walletid"))  );
					 		m_Transaction.setSystemReference(StringUtils.trim(rs.getString("sysreference"))  );
					 		m_Transaction.setTxnAmount(StringUtils.trim(rs.getString("tnmamount"))  );
					 		m_Transaction.setTxnCurrencyId(StringUtils.trim(rs.getString("txncurrencyid"))  );
					 		m_Transaction.setTxnMode(StringUtils.trim(rs.getString("txnmode"))  );
					 		m_Transaction.setTxnDateTime(StringUtils.trim(rs.getString("txndatetime"))  );		
					String[] txnCodeArray = new String[arr_Transaction.size()];
					String[] txnSysRefArray = new String [arr_Transaction.size()];	String[] txnAmountArray = new String[arr_Transaction.size()];
					String[] txnCurrencyIdArray = new String [arr_Transaction.size()];	String[] txnModeArray = new String[arr_Transaction.size()];
					String[] txnDateTimeArray = new String[arr_Transaction.size()];		String[] txnWalletIdArray = new String[arr_Transaction.size()];
					  for(int i=0;i<arr_Transaction.size();i++){
						  txnCodeArray[i]= ((Transaction)arr_Transaction.get(i)).getTxnCode();
						  txnWalletIdArray[i]= ((Transaction)arr_Transaction.get(i)).getWalletId();		  	txnSysRefArray[i]= ((Transaction)arr_Transaction.get(i)).getSystemReference();
						  txnAmountArray[i]= ((Transaction)arr_Transaction.get(i)).getTxnAmount();		  	txnCurrencyIdArray[i]= ((Transaction)arr_Transaction.get(i)).getTxnCurrencyId();
						  txnModeArray[i]= ((Transaction)arr_Transaction.get(i)).getTxnMode();		  		txnDateTimeArray[i]= Utilities.getUTCtoYourTimeZoneConvertor(((Transaction)arr_Transaction.get(i)).getTxnDateTime(), "BET") ;
					  }
					  obj.add("txncode", gson.toJsonTree(txnCodeArray));	 	obj.add("txnwalletid", gson.toJsonTree(txnWalletIdArray));
					  obj.add("txnsysref", gson.toJsonTree(txnSysRefArray));	 obj.add("txnamount", gson.toJsonTree(txnAmountArray));	 	obj.add("txncurrencyid", gson.toJsonTree(txnWalletIdArray));
					  obj.add("txnmode", gson.toJsonTree(txnModeArray));	 obj.add("txndatetime", gson.toJsonTree(  txnDateTimeArray));
					 */
					
					obj.add("userid", gson.toJsonTree(user.getUserId()));
					obj.add("username", gson.toJsonTree(user.getUserName()));
					obj.add("useremail", gson.toJsonTree(user.getEmailId()));
					obj.add("usertype", gson.toJsonTree(user.getUserType()));
					obj.add("error", gson.toJsonTree("false"));
				}else{
					obj.add("error", gson.toJsonTree("true"));
				  }
				//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
				jsonOutput_1.print(gson.toJson(obj));
				jsonOutput_1.close();
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE "+e.getMessage());
			}finally {
				if(jsonOutput_1!=null)
					jsonOutput_1.close();
			}
		break;
		
		case Rules.JSON_GET_KEY:
			PrintWriter jsonOutput_2 = null;
			try {
				jsonOutput_2 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				obj.add("pkey", gson.toJsonTree(PPWalletEnvironment.getAPIKeyPrivate()));
				obj.add("error", gson.toJsonTree("false"));
				//PPWalletEnvironment.setComment(3, className, " JSON JSON_GET_KEY String is "+gson.toJson(obj));
				jsonOutput_2.print(gson.toJson(obj));
				jsonOutput_2.close();
			}catch(Exception e) {
				
			}finally {
				if(jsonOutput_2!=null)
					jsonOutput_2.close();

			}
			
		break;
				
		}
		

	}

	@Override
	public void callException(HttpServletRequest request, HttpServletResponse response, ServletContext ctx,
			HttpSession session, Exception e, String msg) throws Exception {
			try {
				if(session!=null) 	session.invalidate();
					PPWalletEnvironment.setComment(1, className, "Error is "+msg);
					request.setAttribute("errormsg", msg);
					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getErrorPage()).forward(request, response);
				} catch (Exception e1) {
					PPWalletEnvironment.setComment(1, className, "Problem in forwarding to Error Page, error : "+e1.getMessage());
				}
	}

	@Override
	public void performUploadOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			List<FileItem> multiparts, ServletContext ctx) throws Exception {

		HttpSession session	= request.getSession(false);
		switch (rulesaction){
		
	
			case Rules.OPS_REGISTRATION_MERCHANT_ADD:
				try {
					
					String userId = null; String userPwd = null; String userName = null; String userEmail = null; String userContact = null;
					String address1 = null; String address2 = null; String postCode = null;	String companyName = null; String registrationNo = null;
					String mccCode = null;	String billerCode = null; String nationalId= null; String city= null; 
					ArrayList<String> arrMerchFile = new ArrayList<String>();
					int i = 1; FileItem item =null;
						try {
				                Iterator<FileItem> iterator = multiparts.iterator();
				                if(iterator!=null) {
				                	//System.out.println("inside doPost iterator is "+iterator);
				                }
				                while (iterator.hasNext()) {
				                	PPWalletEnvironment.setComment(2, className,"inside loop i= "+i);
				                    item = (FileItem) iterator.next();

				                    if (!item.isFormField()) {
				                        String fileName = item.getName();
				                        if(fileName != null && !"".equals(fileName)){
					                        PPWalletEnvironment.setComment(2, className,"fileName is "+fileName);
					                        File path = new File(PPWalletEnvironment.getFileUploadPath());
					                        File uploadedFile = new File(path + File.separator + fileName);
					                        PPWalletEnvironment.setComment(2, className,"filepath is "+uploadedFile.getAbsolutePath());
				                        
				                        item.write(uploadedFile);
				                      
				                        arrMerchFile.add(uploadedFile.getAbsolutePath());
				                        }
				                    }else {
				                    	
				                    	if(item.getFieldName().equals("regmerchid"))              	userId = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchpwd")) 		        userPwd = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchname")) 	            userName = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchemail")) 	        userEmail =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchcontact")) 	        userContact =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchaddr1")) 	        address1 = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchaddr2")) 	        address2 = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchpcode")) 	        postCode =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchcompname")) 	        companyName =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchregno")) 	        registrationNo = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("hdnmccid")) 			        mccCode =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchnationalid")) 		nationalId = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchcity")) 			    city = StringUtils.trim( item.getString() );
										billerCode = Utilities.generateCVV2(12);				                    	
				                    }
				                    i++;
				                   // PPWalletEnvironment.setComment(3, className," userId"+userId+ " userPwd"+userPwd+"userName "+ userName+ "userEmail "+userEmail+"userEmail"+"userContact"+userContact
				                    		// +" address1"+address1+" address2"+address2+"postCode "+postCode+"companyName"+companyName+"registrationNo"+registrationNo+"mccCode"+mccCode+"billerCode"+billerCode+"nationalId"+nationalId
				                    	//	+"city"+city);  
				                }
				             } catch (Exception e) {
				            	 throw new Exception("Error Message is "+e.getMessage());
				            	 
				            }
				            finally{
			            		 if (item != null) {
			            			 item.delete();
								}
			            	 }
				       
					
					
					if(arrMerchFile!=null)
						if(arrMerchFile.size()==0)
							arrMerchFile = null;
					

					// Now register merchant
	    		    boolean success = false;
	    		    success = (boolean)OpsMerchantManageDao.class.getConstructor().newInstance().registerMerchant(nationalId, userId, userPwd, userName, userEmail, userContact, 
	    		    		address1, address2, postCode, companyName, registrationNo, mccCode, billerCode, city, arrMerchFile);
	    		    
	    		    //consult audit trail on merchant self registration
					String moduleCode = "M"; //M = Merchants Acquiring
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, "M", moduleCode, StringUtils.substring("Merchant self registered "+userId, 0, 48) );
	    		    
					if(success==false)
						throw new Exception ("merchant registration failed");
					ArrayList<Merchant> arrMerchantDetails  = (ArrayList<Merchant>)OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllPendingMerchants();
					if(arrMerchantDetails!=null)
						//PPWalletEnvironment.setComment(2,className,"arrMerchantDetails size  is  "+arrMerchantDetails.size());
					
					request.setAttribute("allmerchantdetails", arrMerchantDetails);
					response.setContentType("text/html");
				
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchantDetailsPage()).forward(request,response);
					} finally {
						if(arrMerchantDetails!=null) arrMerchantDetails=null;
					}
					
				}catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
				break;	
		
				
	/*		case Rules.MERCHANT_UPDATE_PROFILE:
					try {
						String userId = null; String userPwd = null; String userName = null; String userEmail = null; String userContact = null;
						String address1 = null; String address2 = null; String postCode = null;	String companyName = null; String registrationNo = null;
						String mccCode = null;	String billerCode = null; String nationalId= null; String city= null; ArrayList<String> arrMerchFile = new ArrayList<String>();
						int i = 1; FileItem item = null;

					            try {
					                
					                Iterator<FileItem> iterator = multiparts.iterator();
					                if(iterator!=null) {
					                	//System.out.println("inside doPost iterator is "+iterator);
					                }
					                while (iterator.hasNext()) {
					                	PPWalletEnvironment.setComment(2, className,"inside loop i= "+i);
					                    item = (FileItem) iterator.next();

					                    if (!item.isFormField()) {
					                    	String fileName = item.getName();
					                    	 if(fileName != null && !"".equals(fileName)){
						                        PPWalletEnvironment.setComment(2, className,"fileName is "+fileName);
						                        File path = new File(PPWalletEnvironment.getFileUploadPath());
						                        File uploadedFile = new File(path + File.separator + fileName);
						                        PPWalletEnvironment.setComment(2, className,"filepath is "+uploadedFile.getAbsolutePath());
					                        
						                        item.write(uploadedFile);
						                        //TODO should clod the File objects here after writing
						                        arrMerchFile.add(uploadedFile.getAbsolutePath());
					                    	 }else {
					                    		 arrMerchFile = null;
					                    	 }
					                    }else {
					                    	
											if(item.getFieldName().equals("updmerchuserid")) 		    userId = StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("hdnbillercode")) 		    billerCode = StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchpwd")) 		        userPwd = StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchname")) 	            userName = StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchemail")) 	        userEmail =StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchcontact")) 	        userContact =StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchaddr1")) 	        address1 = StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchaddr2")) 	        address2 = StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchpcode")) 	        postCode =StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchcompname")) 	        companyName =StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchregno")) 	        registrationNo = StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmccid")) 			        mccCode =StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchnationalid")) 		nationalId = StringUtils.trim( item.getString() );
											if(item.getFieldName().equals("updmerchcity")) 			    city = StringUtils.trim( item.getString() );
											
											
											
					                    }
					                    i++;
					                }
					             } catch (Exception e) {
					            	 throw new Exception("Error Message is "+e.getMessage());
					            }
					            finally {
					            	if (item != null) {
				            			 item.delete();
									}
					            }
					       
						
						
						if(arrMerchFile!=null)
							if(arrMerchFile.size()==0)
								arrMerchFile = null;
						

						// update merchant profile
		    		    boolean success = false;
		    		    PPWalletEnvironment.setComment(2, className,"mccCode is "+mccCode);
		    		    success = (boolean)OpsMerchantManageDao.class.getConstructor().newInstance().updateMerchant(billerCode, nationalId, userId, userPwd, userName, userEmail, userContact,
		    		    		address1, address2, postCode, companyName, registrationNo, mccCode, city, arrMerchFile); 
		    		    if(success == true) {
		    		    	
							String userType = ((User)session.getAttribute("SESS_USER")).getUserType();
							String moduleCode = "M"; //M = Merchants Acquiring
							SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Merchant updated his profile "+userId, 0, 48) );
		    		    }
		    		   
						if(success==false)
							throw new Exception ("profile update failed");
		    		    response.setContentType("text/html");
		    		    request.setAttribute("merchfullprofile", (Merchant)OpsMerchantManageDao.class.getConstructor().newInstance().getMerchantProfile(userId));
						request.setAttribute("mccvalues", (ConcurrentHashMap<String,String>)OpsMerchantManageDao.class.getConstructor().newInstance().getMerchantCategories());
						
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getMerchantProfilePage()).forward(request, response);
						}finally {
							if(arrMerchFile!=null) arrMerchFile = null; 
						}
						
					}catch (Exception e) {
						callException(request, response, ctx, session,e, e.getMessage());
					}
					
					break; */
			
			
			}
	}
}
		
		



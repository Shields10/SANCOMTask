package com.ppwallet.rules;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.OpsManageWalletDao;
import com.ppwallet.dao.OpsSystemManageCustomerDao;
import com.ppwallet.dao.OpsSystemManageLoyaltyDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.dao.UserLoginDao;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.Loyalty;
import com.ppwallet.model.LoyaltyRules;
import com.ppwallet.model.User;
import com.ppwallet.model.Wallet;

public class OpsLoyaltyRulesImpl implements Rules {
	private static String className = OpsLoyaltyRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session = request.getSession(false);
		switch (rules) {
		case Rules.OPS_VIEW_LOYALTY_RULES_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opslyt"); // going to Manage Loyalty
				request.setAttribute("lastrule", Rules.OPS_VIEW_LOYALTY_RULES_PAGE); // set the last rule for left menu
																						// selection
				ArrayList<LoyaltyRules> arrLoyaltyRules = (ArrayList<LoyaltyRules>) OpsSystemManageLoyaltyDao.class
						.getConstructor().newInstance().getAllLoyaltyRules();
				request.setAttribute("allloyaltyrules", arrLoyaltyRules);
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getLoyaltyRulesPage()).forward(request, response);
				} finally {
					if (arrLoyaltyRules != null)
						arrLoyaltyRules = null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;

     	case Rules.OPS_ADD_LOYALTY_RULE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opslyt"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_VIEW_LOYALTY_RULES_PAGE); // set the last rule for left menu
																				// selection

				PPWalletEnvironment.setComment(3, className, "Before declaring");

				String payMode = null;
				String rulesDesc = null;
				String pointsConversion = null;
				String cryptoConversion = null;
				String status = null;

				String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				String moduleCode = "C";
				ArrayList<LoyaltyRules> arrLoyaltyRules = null;

				if (request.getParameter("paymode") != null) payMode = StringUtils.trim(request.getParameter("paymode"));
				if (request.getParameter("rulesdesc") != null) rulesDesc = StringUtils.trim(request.getParameter("rulesdesc"));
				if (request.getParameter("pointsconversion") != null) pointsConversion = StringUtils.trim(request.getParameter("pointsconversion"));
				if (request.getParameter("cryptoconversion") != null) cryptoConversion = StringUtils.trim(request.getParameter("cryptoconversion"));
				if (request.getParameter("status") != null) status = StringUtils.trim(request.getParameter("status"));
				  PPWalletEnvironment.setComment(3, className,  "payMode"+ payMode + "rulesDesc" + rulesDesc +" pointsConversion" + pointsConversion + "cryptoConversion" + cryptoConversion + "status" + status);	

		      	if (OpsSystemManageLoyaltyDao.class.getConstructor().newInstance().addNewLoyaltyRule(payMode, rulesDesc, pointsConversion, cryptoConversion,status)) {

					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Added payMode" + rulesDesc, 0, 48));
					arrLoyaltyRules = (ArrayList<LoyaltyRules>) OpsSystemManageLoyaltyDao.class.getConstructor().newInstance().getAllLoyaltyRules();
					request.setAttribute("allloyaltyrules", arrLoyaltyRules);
				} else {
					throw new Exception("Problem with the addition of PayMode");
				}
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getLoyaltyRulesPage()).forward(request, response);
				} finally {
					if (arrLoyaltyRules != null) arrLoyaltyRules = null;
					if (payMode != null) payMode = null;
					if (rulesDesc != null) rulesDesc = null;
					if (userId != null) userId = null;
					if (userType != null) userType = null;
					if (moduleCode != null) moduleCode = null;
					
			}
			}
				catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;    

	
		  case Rules.OPS_EDIT_LOYALTY_RULE:
		  try { request.setAttribute("langPref",  "en"); request.setAttribute("lastaction", "opslyt"); // going to profile
		  request.setAttribute("lastrule", Rules.OPS_VIEW_LOYALTY_RULES_PAGE);
		  
		  PPWalletEnvironment.setComment(3, className, "Before declaring");
		  
		  String payMode = null; String rulesDesc = null; String pointsConversion = null; String cryptoConversion = null; String status = null;
		  
		  String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
		  String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
		  String moduleCode = "C"; ArrayList<LoyaltyRules> arrLoyaltyRules = null;
		  
		  if (request.getParameter("paymode") != null) payMode = StringUtils.trim(request.getParameter("paymode")); 
		  if  (request.getParameter("ruledesc") != null) rulesDesc = StringUtils.trim(request.getParameter("ruledesc")); 
		  if (request.getParameter("pointsconversion") != null) pointsConversion =  StringUtils.trim(request.getParameter("pointsconversion")); 
		  if (request.getParameter("cryptoconversion") != null) cryptoConversion = StringUtils.trim(request.getParameter("cryptoconversion")); 
		  if (request.getParameter("status") != null) status =  StringUtils.trim(request.getParameter("status"));
		  PPWalletEnvironment.setComment(3, className,  "payMode"+ payMode + "rulesDesc" + rulesDesc +" pointsConversion" + pointsConversion + "cryptoConversion" + cryptoConversion + "status" + status);

		  
		  if (OpsSystemManageLoyaltyDao.class.getConstructor().newInstance().editLoyaltyRule(payMode,rulesDesc, pointsConversion, cryptoConversion,  status)) {
		  
		  SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Edit payMode" + payMode, 0,  48)); arrLoyaltyRules = (ArrayList<LoyaltyRules>)
		  OpsSystemManageLoyaltyDao.class.getConstructor().newInstance(). getAllLoyaltyRules(); 
		  request.setAttribute("allloyaltyrules", arrLoyaltyRules); } else { throw new Exception("Problem with the edit of PayMode"); }
		  response.setContentType("text/html"); try {
		  ctx.getRequestDispatcher(PPWalletEnvironment.getLoyaltyRulesPage()).forward(  request,response);
		  
		  } finally {
			  if (arrLoyaltyRules != null)arrLoyaltyRules =  null; 
			  if (payMode != null) payMode = null; 
			  if (rulesDesc != null) rulesDesc =  null; 
			  if (pointsConversion != null) pointsConversion = null;   
			  if (cryptoConversion != null) cryptoConversion = null;
			  if (status != null) status = null; 
			  if (userId != null) userId = null; 
			  if (userType != null)  userType = null; 
			  if (moduleCode != null) moduleCode = null;
		  } 
		  
		  } catch  (Exception e) { callException(request, response, ctx, session, e,  e.getMessage()); 
		  
		  } 
		  break;
		 
		
 			case Rules.OPS_VIEW_CUSTOMER_LOYALTY_PAGE:
 				try {
 					request.setAttribute("langPref", "en");
 					request.setAttribute("lastaction", "opslyt"); // going to manage customer menu
 					request.setAttribute("lastrule", Rules.OPS_VIEW_CUSTOMER_LOYALTY_PAGE); //  set the last rule for left menu selection
 					ArrayList <CustomerDetails> arrAllCustomers = (ArrayList<CustomerDetails>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllCustomers();
 					PPWalletEnvironment.setComment(2,className," ---> 1 before setting to request the arrAllCustomers object   is  "+arrAllCustomers);
 					request.setAttribute("allcustomers", arrAllCustomers);
 					PPWalletEnvironment.setComment(2,className," ---> 2  after setting to request the arrAllCustomers object    ");
 					if(arrAllCustomers!=null) {
 						PPWalletEnvironment.setComment(2,className,"arrAllCustomers size  is  "+arrAllCustomers.size());
 					}
 					try {
 						response.setContentType("text/html");
 						PPWalletEnvironment.setComment(3,className," before submitting to jsp  "+PPWalletEnvironment.getOpsViewCustomersPage());
 						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerLoyaltyPage()).forward(request,response);
 					}catch (Exception e) {
 					}finally {
 					}
 						
 					
 				} catch (Exception e) {
 					callException(request, response, ctx, session, e, e.getMessage());
 				}
 				break;
 				
 				
 			case Rules.OPS_VIEW_CUSTOMER_LOYALTY_CLAIM_PAGE:
 				try {
 					String custRelationshipNo = null;
 					request.setAttribute("langPref", "en");
 					request.setAttribute("lastaction", "opslyt"); // going to manage customer menu
 					request.setAttribute("lastrule", Rules.OPS_VIEW_CUSTOMER_LOYALTY_CLAIM_PAGE); //  set the last rule for left menu selection
 				   if (request.getParameter("hdncustrelno") != null) custRelationshipNo = StringUtils.trim(request.getParameter("hdncustrelno")); 
 				// Get Loyallty transaction details for the user
					request.setAttribute("loyaltypointsdetails", (ArrayList<Loyalty>)OpsSystemManageLoyaltyDao.class.getConstructor().newInstance().getLoyaltyTransactionsForUser (custRelationshipNo));
 					ArrayList <Wallet> arrAllWallets = (ArrayList<Wallet>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllCustomerWallet(custRelationshipNo);
 					request.setAttribute("arrcustomerwallets", arrAllWallets);
 				
 					try {
 						response.setContentType("text/html");
 						PPWalletEnvironment.setComment(3,className," before submitting to jsp  "+PPWalletEnvironment.getOpsViewCustomersPage());
 						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerClaimLoyaltyPage()).forward(request,response);
 					}catch (Exception e) {
 					}finally {
 					}
 						
 					
 				} catch (Exception e) {
 					callException(request, response, ctx, session, e, e.getMessage());
 				}
 				break;
 				
 				
 		/*	 case Rules.OPS_CUST_CLAIM_ALL_REWARD:
 				 try{
 						String langType = null;	boolean success; String relationshipno = null; String accruedWalletId = null; String pointAccrued = null;	
 						String payMode = null;	 String walletBalance = null;  String walletId = null; ArrayList<Wallet> ArrWallets = null;  					String custRelationshipNo = null;


 					   if(request.getParameter("hdnwalletid")!=null) 		 		accruedWalletId = StringUtils.trim(request.getParameter("hdnwalletid"));
 	 				   if (request.getParameter("hdncustrelno") != null) custRelationshipNo = StringUtils.trim(request.getParameter("hdncustrelno")); 

 						//relationshipno =  ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
 						payMode = PPWalletEnvironment.getCodeLoyaltyRedeemRate();
 						walletId = accruedWalletId.substring(0, accruedWalletId.indexOf(",", 0));
 						walletBalance = accruedWalletId.substring( (accruedWalletId.indexOf(",", 0))+1, accruedWalletId.length());
 						
 						PPWalletEnvironment.setComment(3,className,"  Claimedwalletid is " +accruedWalletId );
 						
 						success = (boolean)OpsSystemManageLoyaltyDao.class.getConstructor().newInstance().updateLoyaltyRewardsForAllPoint(relationshipno, walletId , walletBalance, payMode);
 						
 						//request.setAttribute("loyaltypointsdetails", (ArrayList<Loyalty>)LoyaltyDao.class.getConstructor().newInstance().getUnclaimedLoyaltyTransactionsForUser (   ((User)session.getAttribute("SESS_USER")).getRelationshipno() ));
 						
 						request.setAttribute("loyaltypointsdetails", (ArrayList<Loyalty>)OpsSystemManageLoyaltyDao.class.getConstructor().newInstance().getLoyaltyTransactionsForUser (   ((User)session.getAttribute("SESS_USER")).getRelationshipNo() ));

 						ArrWallets = (ArrayList<Wallet>)OpsManageWalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
 						request.setAttribute("arrWallets",ArrWallets );
 						request.setAttribute("lastaction", "opslyt");	
 						request.setAttribute("lastrule", Rules.OPS_VIEW_CUSTOMER_LOYALTY_CLAIM_PAGE);
 						response.setContentType("text/html");
 						
 						try {
 							ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerClaimLoyaltyPage()).forward(request, response);

 						}finally {
 							if(relationshipno!=null) relationshipno=null; if(accruedWalletId!=null) accruedWalletId=null; if(pointAccrued!=null) pointAccrued=null;
 							if(payMode!=null) payMode=null; if(walletBalance!=null) walletBalance=null; if(walletId!=null) walletId=null; if(ArrWallets!=null) ArrWallets=null; 
 							
 						}
 						
 						

 				 }catch (Exception e){
 						callException(request, response, ctx, session,e, e.getMessage());

 				 }
 			
 				 
 				 break;	*/
 				
		}
	}
	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		switch (rulesaction) {
		case Rules.JSON_OPS_LOGIN_VALIDATE:
		case Rules.JSON_CUST_LOGIN_VALIDATE:

			try {
				String userId = null;
				String userPwd = null;
				String userType = null;
				String privateKey = null;
				boolean allow = true;
				PrintWriter jsonOutput_1 = null;
				if (jsonObj.get("userid") != null)
					userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if (jsonObj.get("userpwd") != null)
					userPwd = jsonObj.get("userpwd").toString().replaceAll("\"", "");
				if (jsonObj.get("usertype") != null)
					userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				if (jsonObj.get("pvtkey") != null)
					privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");

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
					 * .getTxnDateTime(), "BET") ; } 
					 * obj.add("txncode",
					 * gson.toJsonTree(txnCodeArray)); 
					 * obj.add("txnwalletid",
					 * gson.toJsonTree(txnWalletIdArray)); 
					 * obj.add("txnsysref",
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
				try {
					// PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE
					// String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if (jsonOutput_1 != null) jsonOutput_1.close();
					if (userId != null) userId = null;
					if (userPwd != null) userPwd = null;
					if (userType != null) userType = null;
					if (privateKey != null) privateKey = null;
					if (gson != null) gson = null;
				}

			} catch (Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE " + e.getMessage());
			}
			break;

		case Rules.JSON_GET_KEY:

			try {
				PrintWriter jsonOutput_2 = null;
				jsonOutput_2 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); // Json Object

				obj.add("pkey", gson.toJsonTree(PPWalletEnvironment.getAPIKeyPrivate()));
				obj.add("error", gson.toJsonTree("false"));
				try {
					// PPWalletEnvironment.setComment(3, className, " JSON JSON_GET_KEY String is
					// "+gson.toJson(obj));
					jsonOutput_2.print(gson.toJson(obj));
				} finally {
					if (jsonOutput_2 != null) {
						jsonOutput_2.close();
						jsonOutput_2 = null;
					}
					if (gson != null)
						gson = null;
				}
			} catch (Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_GET_KEY " + e.getMessage());
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

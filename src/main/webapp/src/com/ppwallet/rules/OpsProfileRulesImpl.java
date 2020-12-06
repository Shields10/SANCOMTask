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
import com.ppwallet.dao.OpsProfileDao;
import com.ppwallet.dao.OpsSystemManageCardsDao;
import com.ppwallet.dao.OpsSystemManageMerchantDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.dao.UserLoginDao;
import com.ppwallet.model.CardBIN;
import com.ppwallet.model.DisputeReasons;
import com.ppwallet.model.Disputes;
import com.ppwallet.model.MCC;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.MerchantInstitution;
import com.ppwallet.model.SystemMsfPlans;
import com.ppwallet.model.User;

public class OpsProfileRulesImpl implements Rules {
	private static String className = OpsProfileRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(false);
		switch (rules){
		/*
		 * case Rules.OPS_MERCH_DISPUTE_REASONS_EDIT: try {
		 * request.setAttribute("langPref", "en"); request.setAttribute("lastaction",
		 * "opsdisp"); // going to profile request.setAttribute("lastrule",
		 * Rules.OPS_MERCH_DISPUTE_REASONS_PAGE); // set the last rule for left menu
		 * selection String reasonId = null; String reasonDesc = null; String
		 * disputeUserType = null; String status = null;
		 * if(request.getParameter("hdnreasonid")!=null) reasonId =
		 * StringUtils.trim(request.getParameter("hdnreasonid"));
		 * if(request.getParameter("reasondesc_e")!=null) reasonDesc =
		 * StringUtils.trim(request.getParameter("reasondesc_e"));
		 * if(request.getParameter("hdnstatus")!=null) disputeUserType =
		 * StringUtils.trim(request.getParameter("hdnstatus"));
		 * if(request.getParameter("hdnusertype")!=null) status =
		 * StringUtils.trim(request.getParameter("hdnusertype"));
		 * 
		 * boolean result =
		 * (boolean)OpsSystemManageMerchantDao.class.getConstructor().newInstance().
		 * editDisputeReasonCode(reasonId, reasonDesc, disputeUserType, status);
		 * 
		 * SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(
		 * ((User)session.getAttribute("SESS_USER")).getUserId() ,
		 * ((User)session.getAttribute("SESS_USER")).getUserType() , "O",
		 * StringUtils.substring("Edited Dispute Reason Code "+reasonId, 0, 48) );
		 * 
		 * request.setAttribute("alldisputereasons",
		 * (ArrayList<DisputeReasons>)OpsSystemManageMerchantDao.class.getConstructor().
		 * newInstance().getAllDisputeReasons(""));
		 * 
		 * try { response.setContentType("text/html");
		 * ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysMerchDisputeReasons()).
		 * forward(request, response); } finally {
		 * 
		 * }
		 * 
		 * }catch(Exception e){ callException(request, response, ctx, session, e,
		 * e.getMessage()); }
		 * 
		 * break;
		 */
			case Rules.OPS_VIEW_PROFILE_PAGE:
				try {
					PPWalletEnvironment.setComment(2,className," ---> 1 View Profile  ");
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "opsprf"); // going to profile
					request.setAttribute("lastrule", Rules.OPS_VIEW_PROFILE_PAGE); //  set the last rule for left menu selection
					request.setAttribute("opsuser", (User)OpsProfileDao.class.getConstructor().newInstance().getSpecificOpsUser( ((User)session.getAttribute("SESS_USER")).getUserId()   ));
					try {
						response.setContentType("text/html");
						ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysProfilePage()).forward(request,response);
					} finally {
						
					}

				}catch(Exception e){
					callException(request, response, ctx, session, e, e.getMessage());			
				}				
				
			break;
			 case Rules.OPS_ADMIN_UPDATE_PROFILE:
					try {
						request.setAttribute("langPref", "en");
						request.setAttribute("lastaction", "opsprf"); // going to profile
						request.setAttribute("lastrule", Rules.OPS_VIEW_PROFILE_PAGE); //  set the last rule for left menu selection
						
						String userId = ""; String userAccess = ""; String userName = ""; String emailId = "";
						String userContact = ""; String userStatus = null; String expiryDate = ""; String userpwd = null;

						if(request.getParameter("hdnuserid")!=null)			userId = StringUtils.trim(request.getParameter("hdnuserid"));
						if(request.getParameter("useraccess")!=null)			userAccess = StringUtils.trim(request.getParameter("useraccess"));
						if(request.getParameter("username")!=null)			userName = StringUtils.trim(request.getParameter("username"));
						if(request.getParameter("useremailid")!=null)			emailId = StringUtils.trim(request.getParameter("useremailid"));
						if(request.getParameter("usercontact")!=null)			userContact = StringUtils.trim(request.getParameter("usercontact"));
						if(request.getParameter("userstatus")!=null)			userStatus = StringUtils.trim(request.getParameter("userstatus"));
						if(request.getParameter("expirydate")!=null)			expiryDate = StringUtils.trim(request.getParameter("expirydate"));
						if(request.getParameter("userpwd")!=null)			userpwd = StringUtils.trim(request.getParameter("userpwd"));
						
						boolean result = (boolean)OpsProfileDao.class.getConstructor().newInstance().updateSpecificOpsUser(userId,userpwd, userName,  userAccess, emailId, userContact,  userStatus, expiryDate);
						
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail( ((User)session.getAttribute("SESS_USER")).getUserId() ,
								((User)session.getAttribute("SESS_USER")).getUserType()  , "A", StringUtils.substring("Updated Profile of userid "+userId, 0, 48) );
						
						request.setAttribute("opsuser", (User)OpsProfileDao.class.getConstructor().newInstance().getSpecificOpsUser( ((User)session.getAttribute("SESS_USER")).getUserId()   ));
						try {
							response.setContentType("text/html");
							ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysProfilePage()).forward(request,response);
						} finally {
							if(userId!=null) userId=null;  if(userAccess!=null) userAccess=null;  if(userName!=null) userName=null;  if(emailId!=null) emailId=null;  if(userContact!=null) userContact=null;
							if(userStatus!=null) userStatus=null;  if(expiryDate!=null) expiryDate=null;  
						}
					}catch(Exception e){
						callException(request, response, ctx, session, e, e.getMessage());			
					}				
			 break;
			 case Rules.OPS_MANAGE_OPS_USER_PAGE:
					try {
						request.setAttribute("langPref", "en");
						request.setAttribute("lastaction", "opsprf"); // going to profile
						request.setAttribute("lastrule", Rules.OPS_MANAGE_OPS_USER_PAGE); //  set the last rule for left menu selection
						request.setAttribute("allopsusers", (ArrayList <User>)OpsProfileDao.class.getConstructor().newInstance().getAllOperationUsers(  ));
						try {
							response.setContentType("text/html");
							ctx.getRequestDispatcher(PPWalletEnvironment.getOpsManageAllOpsUsersPage()).forward(request,response);
						} finally {
							
						}

					}catch(Exception e){
						callException(request, response, ctx, session, e, e.getMessage());			
					}					 
				 
			
			 break;
			 case Rules.OPS_ADD_OPS_USER:
					try {
						request.setAttribute("langPref", "en");
						request.setAttribute("lastaction", "opsprf"); // going to profile
						request.setAttribute("lastrule", Rules.OPS_MANAGE_OPS_USER_PAGE); //  set the last rule for left menu selection
						
						String userId = null; String userAccess = null; String userName = null; String emailId = null;
						String userContact = null; String userStatus = null; String expiryDate = null;  String password = null;

						if(request.getParameter("adduserid")!=null)			userId = StringUtils.trim(request.getParameter("adduserid"));
						if(request.getParameter("adduserpwd")!=null)			password = StringUtils.trim(request.getParameter("adduserpwd"));
						if(request.getParameter("adduseraccess")!=null)			userAccess = StringUtils.trim(request.getParameter("adduseraccess"));
						if(request.getParameter("adduseremail")!=null)			emailId = StringUtils.trim(request.getParameter("adduseremail"));
						if(request.getParameter("addusercontact")!=null)			userContact = StringUtils.trim(request.getParameter("addusercontact"));
						if(request.getParameter("adduserstatus")!=null)			userStatus = StringUtils.trim(request.getParameter("adduserstatus"));
						if(request.getParameter("adduserexpiry")!=null)			expiryDate = StringUtils.trim(request.getParameter("adduserexpiry"));
						if(request.getParameter("addusername")!=null)			userName = StringUtils.trim(request.getParameter("addusername"));
						
						boolean result = (boolean)OpsProfileDao.class.getConstructor().newInstance().addSpecificOpsUser(userId, password, userName,  userAccess, emailId, userContact,  userStatus, expiryDate);
						
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail( ((User)session.getAttribute("SESS_USER")).getUserId() ,
								((User)session.getAttribute("SESS_USER")).getUserType()  , "A", StringUtils.substring("Updated Profile of userid "+userId, 0, 48) );
						request.setAttribute("allopsusers", (ArrayList <User>)OpsProfileDao.class.getConstructor().newInstance().getAllOperationUsers(  ));
						try {
							response.setContentType("text/html");
							ctx.getRequestDispatcher(PPWalletEnvironment.getOpsManageAllOpsUsersPage()).forward(request,response);
						} finally {
							
						}

					}catch(Exception e){
						callException(request, response, ctx, session, e, e.getMessage());			
					}					 
				 				 
				 
			 break;
			 
			 case Rules.OPS_EDIT_OPS_USER:
					try {
						request.setAttribute("langPref", "en");
						request.setAttribute("lastaction", "opsprf"); // going to profile
						request.setAttribute("lastrule", Rules.OPS_MANAGE_OPS_USER_PAGE); //  set the last rule for left menu selection
						
						String userId = null; String userAccess = null; String userName = null; String emailId = null;
						String userContact = null; String userStatus = null; String expiryDate = null;  String password = null;

						if(request.getParameter("edituserid")!=null)			userId = StringUtils.trim(request.getParameter("edituserid"));
						if(request.getParameter("edituserpwd")!=null)			password = StringUtils.trim(request.getParameter("edituserpwd"));
						if(request.getParameter("edituseraccess")!=null)			userAccess = StringUtils.trim(request.getParameter("edituseraccess"));
						if(request.getParameter("edituseremail")!=null)			emailId = StringUtils.trim(request.getParameter("edituseremail"));
						if(request.getParameter("editusercontact")!=null)			userContact = StringUtils.trim(request.getParameter("editusercontact"));
						if(request.getParameter("edituserstatus")!=null)			userStatus = StringUtils.trim(request.getParameter("edituserstatus"));
						if(request.getParameter("edituserexpiry")!=null)			expiryDate = StringUtils.trim(request.getParameter("edituserexpiry"));
						if(request.getParameter("editusername")!=null)			userName = StringUtils.trim(request.getParameter("editusername"));
						PPWalletEnvironment.setComment(3, className, "OPS_MANAGE_OPS_USER_PAGE ");
						boolean result = (boolean)OpsProfileDao.class.getConstructor().newInstance().updateSpecificOpsUser(userId, password, userName,  userAccess, emailId, userContact,  userStatus, expiryDate);
						
						if(result) {
							SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail( ((User)session.getAttribute("SESS_USER")).getUserId() ,
									((User)session.getAttribute("SESS_USER")).getUserType()  , "A", StringUtils.substring("Updated Profile of userid "+userId, 0, 48) );
						}
							
						request.setAttribute("allopsusers", (ArrayList <User>)OpsProfileDao.class.getConstructor().newInstance().getAllOperationUsers(  ));
						try {
							response.setContentType("text/html");
							ctx.getRequestDispatcher(PPWalletEnvironment.getOpsManageAllOpsUsersPage()).forward(request,response);
						} finally {
							
						}

					}catch(Exception e){
						callException(request, response, ctx, session, e, e.getMessage());			
					}					 
				 						 
				 
			 break;
			
			
		}
		
		
	}

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
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

	}

}

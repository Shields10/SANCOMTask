package com.ppwallet.rules;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.ppwallet.dao.PaymentDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.dao.UserLoginDao;
import com.ppwallet.model.BankDetails;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.MpesaDetails;
import com.ppwallet.model.User;

import nl.captcha.Captcha;

public class UserProfileRulesImpl implements Rules {
	
	private static String className = UserProfileRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(false);
		switch (rules){
			case Rules.DEFAULT_LOGIN_MODULE:
				
				try {
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getLoginPage()).forward(request, response);
					} finally {
						// TODO: handle finally clause
					}
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
			break;
			case Rules.LOGOUT_MODULE:
							
				try {
					if(session.getAttribute("SESS_USER")!=null) 				session.invalidate();
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getLoginPage()).forward(request, response);
					} finally {
						// TODO: handle finally clause
					}
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
			break;		
			case Rules.LOGIN_VALIDATE:
				
				try{
					String userid=null; String userpwd = null; String usertype = null;
					User user = null; String langPref = null; String relNo=null;

				    String passline = null;		    

					if(request.getParameter("username")!=null)	userid = request.getParameter("username").trim();
					if(request.getParameter("userpwd")!=null)	userpwd = request.getParameter("userpwd").trim();
					if(request.getParameter("hdnusertype")!=null)	usertype = request.getParameter("hdnusertype").trim();
					if(request.getParameter("passline")!=null)	passline = request.getParameter("passline").trim();
					if(request.getParameter("hdnlang")!=null)	langPref = request.getParameter("hdnlang").trim();
				    if ((passline!=null) ) {
				    	Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
				        if (captcha.isCorrect(passline)==false) throw new Exception ("Security code is incorrect");
				    }				
					
					user=(User)UserLoginDao.class.getConstructor().newInstance().validateUser(userid, userpwd, usertype);
					
					if(user!=null) {			
						user.setLangPref(langPref); // setting the language preference of the user in the session
						session.setAttribute("SESS_USER", user);
						
						relNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
//						PPWalletEnvironment.setComment(3, className, "Session is "+relNo);
						
						
					}else {
						throw new Exception ("LOGIN_REQUEST : userid and/or password not valid");	
						//TODO in such case show a error page where the user restriction is displayed, not the generic error page
					}
					request.setAttribute("langPref", langPref);
					request.setAttribute("lastaction", "prf"); // going to profile
					request.setAttribute("lastrule", "Dashboard"); //  set the last rule for left menu selection
					response.setContentType("text/html");
				
				// check user expiry date functions
				/*
				 *  if expiry date is less than today's date then show the user a "expiry" screen
				 *  if expiry date is "0000-00-00" then ignore
				 * 
				 */
					boolean proceed = true;
			       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			        Date expiryDate = sdf.parse(user.getExpiryDate());
			        Date todayDate = new Date();
			        if((user.getExpiryDate().trim().equals("0000-00-00")) == false) {
			        	if(todayDate.after(expiryDate)) {
							PPWalletEnvironment.setComment(1, className, "User Expired "+user.getUserId());
							if(session!=null) 	session.invalidate();
							proceed = false;
			        		ctx.getRequestDispatcher(PPWalletEnvironment.getUserExpiryDatePage()).forward(request, response);
			        	}		        		
			        }
				if(user.getUserType().equals("C") && proceed == true) {
				ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerDashboardPage()).forward(request, response);
				}else if(user.getUserType().equals("M") && proceed == true) {
					ctx.getRequestDispatcher(PPWalletEnvironment.getMerchantDashboardPage()).forward(request, response);
				}else if(user.getUserType().equals("O") && proceed == true) {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOperationsDashboardPage()).forward(request, response);
				}else if(user.getUserType().equals("B") && proceed == true) {
					ctx.getRequestDispatcher(PPWalletEnvironment.getBatchJobDashboardPage()).forward(request, response);
				}
				
				try {
					
				}finally {
					if(userid!=null) userid=null;if(userpwd!=null) userpwd=null;if(usertype!=null) usertype=null;if(passline!=null) passline=null;
				}
				}catch(Exception e){
					callException(request, response, ctx, session,e, e.getMessage());
				}
			break;
			case Rules.PROFILE_USER_DASHBOARD:
				try {
					 User user = null;
					user = (User)session.getAttribute("SESS_USER");
					request.setAttribute("langPref", "en");	
					request.setAttribute("lastaction", "custprf"); // going to profile
					request.setAttribute("lastrule", "Dashboard"); //  set the last rule for left menu selection
					response.setContentType("text/html");
					if(user.getUserType().equals("C")) {
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerDashboardPage()).forward(request, response);
						}else if(user.getUserType().equals("M")) {
							ctx.getRequestDispatcher(PPWalletEnvironment.getMerchantDashboardPage()).forward(request, response);
						}else if(user.getUserType().equals("O")) {
							ctx.getRequestDispatcher(PPWalletEnvironment.getOperationsDashboardPage()).forward(request, response);
						}
				}catch(Exception e){
					callException(request, response, ctx, session,e, e.getMessage());
				}
				break;
				
			case Rules.PROFILE_USER_VIEWPROFILE:								
				try {
					 User user = null;
					user = (User)session.getAttribute("SESS_USER");
					request.setAttribute("langPref", "en");	
					request.setAttribute("lastaction", "custprf"); // going to profile
					request.setAttribute("lastrule", Rules.PROFILE_USER_VIEWPROFILE); //  set the last rule for left menu selection
					response.setContentType("text/html");
					if(user.getUserType().equals("C")) {
						request.setAttribute("customerfullprofile", (CustomerDetails)UserLoginDao.class.getConstructor().newInstance().getFullCustomerProfile(user.getRelationshipNo(), user.getUserType()));
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerProfileViewPage()).forward(request, response);
						}else if(user.getUserType().equals("M")) {
							//request.setAttribute("merchantfullprofile", (Merchant)UserLoginDao.class.getConstructor().newInstance().getFullMerchantProfile(user.getUserId(), user.getUserType()));
							// get Merchant Full Profile
							//ctx.getRequestDispatcher(PPWalletEnvironment.getMerchantDashboardPage()).forward(request, response);
						}else if(user.getUserType().equals("O")) {
							//ctx.getRequestDispatcher(PPWalletEnvironment.getOperationsDashboardPage()).forward(request, response);
						}				
					} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
			break;

			case Rules.PROFILE_USER_EDITPROFILE_UPDATE: 
				
				try {
					 User user = null;
					String custId = null; String custPwd = null; String custName = null; String custEmail = null; String custContact = null; String address = null;
					String relationshipNo = null;
					boolean success = false;
					
					if(request.getParameter("hdnuserid")!=null)	custId = StringUtils.trim(request.getParameter("hdnuserid"));
					if(request.getParameter("custpwd")!=null)	custPwd = StringUtils.trim(request.getParameter("custpwd"));
					if(request.getParameter("custusername")!=null)	custName = StringUtils.trim(request.getParameter("custusername"));
					if(request.getParameter("custusermail")!=null)	custEmail = StringUtils.trim(request.getParameter("custusermail"));
					if(request.getParameter("custusercontact")!=null)	custContact = StringUtils.trim(request.getParameter("custusercontact"));
					if(request.getParameter("custuseraddress")!=null)	address = StringUtils.trim(request.getParameter("custuseraddress"));
					if(request.getParameter("hdnrelationshipno")!=null)	relationshipNo = StringUtils.trim(request.getParameter("hdnrelationshipno"));
					
				//	PPWalletEnvironment.setComment(3,className,"our values  "+custId +"|"+custPwd+"|"+custName+"|"+custEmail+"|"+custContact+"|"+address+"|"+postCode+"|"+userType);

				 user = (User)session.getAttribute("SESS_USER");
				 request.setAttribute("langPref", "en");
				 request.setAttribute("lastaction", "custprf"); // going to profile
				 request.setAttribute("lastrule", Rules.PROFILE_USER_VIEWPROFILE); // set the last rule for
				 response.setContentType("text/html");
				 
					success = (boolean)UserLoginDao.class.getConstructor().newInstance().updateCustomerProfile(relationshipNo, custPwd, custName , custEmail, custContact, address );
					if(success == false) throw new Exception ("Update of customer profile failed");
					request.setAttribute("customerfullprofile", (CustomerDetails)UserLoginDao.class.getConstructor().newInstance().getFullCustomerProfile(user.getRelationshipNo(), 
							user.getUserType()));
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerProfileViewPage()).forward(request, response);
					} finally {
						if(user!=null) user=null; if(custId!=null) custId=null;if(custPwd!=null) custPwd=null;if(custName!=null) custName=null;if(custEmail!=null) custEmail=null;
						if(custContact!=null) custContact=null;if(address!=null) address=null; 
					}
					
					} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
			break;
			
	
			case Rules.PROFILE_USER_REGISTER_BANKPAGE: 
				try {
					 User user = null;String myRelationshipNo = null;
					user = (User)session.getAttribute("SESS_USER");
					 myRelationshipNo = user.getRelationshipNo();
					request.setAttribute("langPref", "en");	
					request.setAttribute("lastaction", "custprf"); // going to profile
					request.setAttribute("lastrule", Rules.PROFILE_USER_REGISTER_BANKPAGE); //  set the last rule for left menu selection
					request.setAttribute("custregisteredbankdetails", (ArrayList<BankDetails>)PaymentDao.class.getConstructor().newInstance().getCustomerRegisteredBanks(myRelationshipNo)); // retur cust reg banks
					
					try {
						response.setContentType("text/html");
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerRegisterBankPage()).forward(request, response);
					}finally {
						if(user!= null)user=null;  if(myRelationshipNo!=null)myRelationshipNo=null;
					}
				}catch(Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());			
				} 
				break;	
			case Rules.PROFILE_REGISTERBANK_DETAILS: 
				try {
					 User user = null;String bankCode = null; String branchCode = null;   String bankName = null; 
					String bankAccountNo = null; String bankAccountName = null; boolean success = false; String relationshipNo;
					
					if(request.getParameter("bankcode")!=null)	        bankCode = StringUtils.trim(request.getParameter("bankcode"));
					if(request.getParameter("branchcode")!=null)	    branchCode = StringUtils.trim(request.getParameter("branchcode"));
					if(request.getParameter("bankname")!=null)	        bankName = StringUtils.trim(request.getParameter("bankname"));
					if(request.getParameter("accountnumber")!=null)  	bankAccountNo = StringUtils.trim(request.getParameter("accountnumber"));
					if(request.getParameter("bankaccountname")!=null)	bankAccountName = StringUtils.trim(request.getParameter("bankaccountname"));
					relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					
					//************validate user bank details.. here we shall have BANK API to validate data before storing in our database
					
					user = (User)session.getAttribute("SESS_USER");
					request.setAttribute("langPref", "en");	
					request.setAttribute("lastaction", "custprf"); // going to profile
					request.setAttribute("lastrule", PROFILE_REGISTERBANK_DETAILS); //  set the last rule for left menu selection
					response.setContentType("text/html");
					success = (boolean)PaymentDao.class.getConstructor().newInstance().addBankDetails(bankCode, branchCode, relationshipNo, bankName, bankAccountNo, bankAccountName);
					
					request.setAttribute("custregisteredbankdetails", (ArrayList<BankDetails>)PaymentDao.class.getConstructor().newInstance().getCustomerRegisteredBanks(relationshipNo)); // retur cust reg banks

					PPWalletEnvironment.setComment(3,className,"success  is  "+ success);

					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerRegisterBankPage()).forward(request, response);
				}catch(Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());			
				}
				break;	
			case Rules.PROFILE_USER_REGISTER_MPESAPAGE:        
				try {
					 String user=null;  String myRelationshipNo = null;
					
					myRelationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					request.setAttribute("langPref", "en");	
					request.setAttribute("lastaction", "custprf"); // going to profile
					request.setAttribute("lastrule", PROFILE_USER_REGISTER_MPESAPAGE); //  set the last rule for left menu selection
					request.setAttribute("custregmpesanos", (ArrayList<MpesaDetails>)PaymentDao.class.getConstructor().newInstance().getCustomerRegMpesaNos(myRelationshipNo)); // retur cust reg banks
					response.setContentType("text/html");
					
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerRegisterMpesaPage()).forward(request, response);
					}finally {
						if(user!= null)user=null;  
					}
				}catch(Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());			
				} 
				break;	
				
				
				
			case Rules.PROFILE_USER_REGISTER_MPESANUMBER:
				try {
					 String mpesaNumber = null; String nationalIdNumber = null; String surname = null; 
					boolean success = false; String otherName = null; String relationshipNo=null;
			
					if(request.getParameter("mpesanumber")!=null)	mpesaNumber = StringUtils.trim(request.getParameter("mpesanumber"));
					if(request.getParameter("surname")!=null)	    surname = StringUtils.trim(request.getParameter("surname"));
					if(request.getParameter("othername")!=null)	    otherName = StringUtils.trim(request.getParameter("othername"));
					if(request.getParameter("idnumber")!=null)	    nationalIdNumber = StringUtils.trim(request.getParameter("idnumber"));		
					relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					
					request.setAttribute("langPref", "en");	
					request.setAttribute("lastaction", "custprf"); // going to profile
					request.setAttribute("lastrule", PROFILE_USER_REGISTER_MPESAPAGE); //  set the last rule for left menu selection
					response.setContentType("text/html");
					success = (boolean)PaymentDao.class.getConstructor().newInstance().addMpesaDetails(relationshipNo, mpesaNumber, surname,otherName, nationalIdNumber  );
					if(!success) throw new Exception ("Failed to add Mpesa Details ");
					else
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo , ((User)session.getAttribute("SESS_USER")).getUserType(), "C", StringUtils.substring(" Added Mpesa details "+mpesaNumber, 0, 48) );					
					request.setAttribute("custregmpesanos", (ArrayList<MpesaDetails>)PaymentDao.class.getConstructor().newInstance().getCustomerRegMpesaNos(relationshipNo)); // retur cust reg banks
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerRegisterMpesaPage()).forward(request, response);
				}catch(Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());			
				}
				break;		
		}	
	}

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		switch (rulesaction){
		case Rules.JSON_OPS_LOGIN_VALIDATE:	case Rules.JSON_CUST_LOGIN_VALIDATE:
			
			
			try {
				String userId = null;	String userPwd = null;	String userType = null; String privateKey = null;	boolean allow = true;
				PrintWriter jsonOutput_1 = null;		
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
					obj.add("relationshipno", gson.toJsonTree(user.getRelationshipNo()));
					obj.add("billercode", gson.toJsonTree(user.getBillerCode()));

					
					obj.add("error", gson.toJsonTree("false"));
				}else{
					obj.add("error", gson.toJsonTree("true"));
					
				  }
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userPwd!=null) userPwd=null;if(userType!=null) userType=null;
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
				}
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE "+e.getMessage());
			}
		break;
		
		case Rules.JSON_GET_KEY:
			
			try {
				PrintWriter jsonOutput_2 = null;
				jsonOutput_2 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				obj.add("pkey", gson.toJsonTree(PPWalletEnvironment.getAPIKeyPrivate()));
				obj.add("error", gson.toJsonTree("false"));
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_GET_KEY String is "+gson.toJson(obj));
					jsonOutput_2.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_2!=null) {jsonOutput_2.close(); jsonOutput_2=null;} if(gson!=null) gson=null;
				}
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_GET_KEY "+e.getMessage());
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

	}

}

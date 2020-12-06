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
import com.ppwallet.dao.OpsMerchantManageDao;
import com.ppwallet.dao.OpsSystemManageCustomerDao;
import com.ppwallet.dao.OpsSystemManageMerchantDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.dao.UserLoginDao;
import com.ppwallet.dao.OpsManageWalletDao;
import com.ppwallet.model.CallLog;
import com.ppwallet.model.Card;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.DisputeTracker;
import com.ppwallet.model.Disputes;
import com.ppwallet.model.MCC;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.Transaction;
import com.ppwallet.model.User;
import com.ppwallet.model.Wallet;
import com.ppwallet.utilities.Utilities;

public class OpsCustomerManageRulesImpl implements Rules {
	private static String className = OpsCustomerManageRulesImpl.class.getSimpleName();
	
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(false);
		switch (rules){

		case Rules.OPS_CUST_VIEW_PENDING_CUSTOMERS: 
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opscust"); 
				request.setAttribute("lastrule", Rules.OPS_CUST_VIEW_PENDING_CUSTOMERS);
				ArrayList <CustomerDetails> arrPendingCustomers= (ArrayList<CustomerDetails>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllPendingCustomers();
				PPWalletEnvironment.setComment(2,className," ---> 1 before setting to request the arrarrDisputedetails object   is  "+arrPendingCustomers);
				request.setAttribute("allpendingcustomers", arrPendingCustomers);
				PPWalletEnvironment.setComment(2,className," ---> 2  after setting to request the arrDisputedetails object    ");
				if(arrPendingCustomers!=null) {
					PPWalletEnvironment.setComment(2,className,"arrDisputedetails size  is  "+arrPendingCustomers.size());
				}
				try {
					response.setContentType("text/html");
					PPWalletEnvironment.setComment(3,className," before submitting to jsp  "+PPWalletEnvironment.getOpsSysPendingCustomersPage());
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysPendingCustomersPage()).forward(request,response);
				}catch (Exception e) {
					
				}finally {
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;
			
		case Rules.OPS_CUST_VERIFY_PENDING_CUSTOMERS:
			 try { request.setAttribute("langPref",  "en"); request.setAttribute("lastaction", "dash"); // going to profile
			  request.setAttribute("lastrule", Rules.OPS_CUST_VIEW_PENDING_CUSTOMERS);
			  
			  PPWalletEnvironment.setComment(3, className, "at OPS_CUST_VERIFY_PENDING_CUSTOMERS");
			  
			  String RelNO = null;  String status = null;
			  
			  String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
			  String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
			  String moduleCode = "C"; ArrayList<CustomerDetails> arrPendingCustomers = null;
			  
			  if (request.getParameter("relationshipno") != null) RelNO = StringUtils.trim(request.getParameter("relationshipno")); 
		      if (request.getParameter("selstatus") != null) status =  StringUtils.trim(request.getParameter("selstatus"));
			  PPWalletEnvironment.setComment(3, className,  "relno"+ RelNO + "status" + status);

			  
			  if (OpsSystemManageCustomerDao.class.getConstructor().newInstance().verifyPendingCust(RelNO, status)) {
			  
			  SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("verify Customer" + RelNO, 0,  48));
			  arrPendingCustomers = (ArrayList<CustomerDetails>) OpsSystemManageCustomerDao.class.getConstructor().newInstance(). getAllPendingCustomers(); 
			  request.setAttribute("allpendingcustomers", arrPendingCustomers); 
			  } else { throw new Exception("Problem with the edit of status"); }
			  response.setContentType("text/html"); 
			  try {
			  ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysPendingCustomersPage()).forward(  request,response);
			  
			  } finally {
				  if (arrPendingCustomers != null)arrPendingCustomers =  null; 
				  if (RelNO != null) RelNO = null; 
				  if (userId != null) userId = null; 
				  if (userType != null)  userType = null; 
				  if (moduleCode != null) moduleCode = null;
			  } 
			  
			  } catch  (Exception e) { callException(request, response, ctx, session, e,  e.getMessage()); 
			  
			  } 
			  break;
		
			
			
		case Rules.OPS_CUST_VIEW_CARDS_PAGE: 
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opscust"); // going to manage customer menu
				request.setAttribute("lastrule", Rules.OPS_CUST_VIEW_CARDS_PAGE); //  set the last rule for left menu selection
				ArrayList <Card> arrCardDetails = (ArrayList<Card>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllCards();
				PPWalletEnvironment.setComment(2,className," ---> 1 before setting to request the arrCardDetails object   is  "+arrCardDetails);
				request.setAttribute("allcarddetails", arrCardDetails);
				PPWalletEnvironment.setComment(2,className," ---> 2  after setting to request the arrCardDetails object    ");
				if(arrCardDetails!=null) {
					PPWalletEnvironment.setComment(2,className,"arrCardDetails size  is  "+arrCardDetails.size());
				}
				try {
					response.setContentType("text/html");
					PPWalletEnvironment.setComment(3,className," before submitting to jsp  "+PPWalletEnvironment.getOpsViewCardsPage());
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsViewCardsPage()).forward(request,response);
				}catch (Exception e) {
				}finally {
				}
					
				
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;
		
			
		case Rules.OPS_CUST_VIEW_CUSTOMERS_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opscust"); // going to manage customer menu
				request.setAttribute("lastrule", Rules.OPS_CUST_VIEW_CUSTOMERS_PAGE); //  set the last rule for left menu selection
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
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsViewCustomersPage()).forward(request,response);
				}catch (Exception e) {
				}finally {
				}
					
				
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;
			
		case Rules.OPS_CUST_EDIT_CUSTDETAILS_PAGE:
			try {
			request.setAttribute("langPref", "en");
			request.setAttribute("lastaction", "opscust"); 
			request.setAttribute("lastrule", Rules.OPS_CUST_EDIT_CUSTDETAILS_PAGE);
		String relationshipNo = null; String customerId = null;  String Password = null;	String customerName = null;  String nationalId = null;  String passportNo= null;
		String gender= null; String email = null; String contact = null;  String address = null;  String pinCode = null; String dateOfBirth=null; String status=null; String expiry=null; String createdOn=null;
			
			String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
			String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
			String moduleCode = "C"; // C =Customer
			ArrayList<CustomerDetails> arrAllCust = null;
			
			if (request.getParameter("relationshipno") != null) relationshipNo = StringUtils.trim(request.getParameter("relationshipno"));
			if (request.getParameter("customerid") != null) customerId = StringUtils.trim(request.getParameter("customerid"));
			if (request.getParameter("customerpwd") != null) Password = StringUtils.trim(request.getParameter("customerpwd"));
			if (request.getParameter("customername") != null) customerName = StringUtils.trim(request.getParameter("customername"));
			if (request.getParameter("nationalid") != null) nationalId = StringUtils.trim(request.getParameter("nationalid"));
			if (request.getParameter("passportno") != null) passportNo = StringUtils.trim(request.getParameter("passportno"));
			if (request.getParameter("gender") != null) gender = StringUtils.trim(request.getParameter("gender"));
			if (request.getParameter("custemail") != null) email = StringUtils.trim(request.getParameter("custemail"));
			if (request.getParameter("custcontact") != null) contact = StringUtils.trim(request.getParameter("custcontact"));
			if (request.getParameter("address") != null) address = StringUtils.trim(request.getParameter("address"));
			if (request.getParameter("krapin") != null) pinCode = StringUtils.trim(request.getParameter("krapin"));
			if (request.getParameter("dateofbirth") != null) dateOfBirth = StringUtils.trim(request.getParameter("dateofbirth"));
			if (request.getParameter("status") != null) status = StringUtils.trim(request.getParameter("status"));
			if (request.getParameter("expiry") != null) expiry = StringUtils.trim(request.getParameter("expiry"));
			if (request.getParameter("createdon") != null) createdOn = StringUtils.trim(request.getParameter("createdon"));
			
			//PPWalletEnvironment.setComment(2,className," inside 2 OPS_CUST_EDIT_CUSTDETAILS_PAGE");

			if (OpsSystemManageCustomerDao.class.getConstructor().newInstance().updateCustomerDetails(relationshipNo, customerId, Password, customerName, nationalId, passportNo, gender, email, contact, address, pinCode, dateOfBirth, status, expiry, createdOn)) {
				// call the audit trail here
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, "Edit CustomerId " + customerId);
				arrAllCust = (ArrayList<CustomerDetails>) OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllCustomers();
				request.setAttribute("allCustomers", arrAllCust);
			} else {
				throw new Exception("Problem with the updating customer");
			}
			ArrayList <CustomerDetails> arrAllCustomers = (ArrayList<CustomerDetails>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllCustomers();
			request.setAttribute("allcustomers", arrAllCustomers);		
			response.setContentType("text/html");
			try {
				ctx.getRequestDispatcher(PPWalletEnvironment.getOpsViewCustomersPage()).forward(request,
						response);
			} finally {
					
					 if (arrAllCust != null) arrAllCust = null; if (relationshipNo != null)
						 relationshipNo = null; if (customerId != null) customerId = null; if
					  (Password != null) Password = null; if (customerName != null) customerName =
					  null; if (nationalId != null) nationalId = null; if (passportNo != null)
					  passportNo = null; if (gender != null) gender = null; if (email != null)
					  email = null; if (contact != null) contact = null; if (address != null)
					  address = null; if (pinCode != null) pinCode = null; if (dateOfBirth != null)
					  dateOfBirth = null; if (status != null) status = null; if (createdOn != null)
					  createdOn = null; if (userId != null) userId = null; if (userType != null)
					  userType = null; if (moduleCode != null) moduleCode = null;
					 
		
			}
		} catch (Exception e) {
			callException(request, response, ctx, session, e, e.getMessage());
		}
		break;
		case Rules.OPS_CUST_WALLETS_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opscust"); // going to manage customer menu
				request.setAttribute("lastrule", Rules.OPS_CUST_VIEW_CUSTOMERS_PAGE); //  set the last rule for left menu selection
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
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsViewCustomersPage()).forward(request,response);
				}catch (Exception e) {
				}finally {
				}
					
				
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;		
	/*	case Rules.OPS_CUST_WALLETS_PAGE:
			 try {
				 	request.setAttribute("langPref", "en");				
					request.setAttribute("lastaction", "opscust");	
					request.setAttribute("lastrule", Rules.OPS_CUST_VIEW_CUSTOMERS_PAGE);
					ArrayList<Wallet>  arrWallet = null;
					arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
					
					request.setAttribute("walletlist", arrWallet);
					response.setContentType("text/html");
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getOpsViewCustomersPage()).forward(request, response);
						} finally {
							if(arrWallet!=null) arrWallet=null; 
						}
				}catch (Exception e) {
					callException(request, response, ctx, session,e, e.getMessage());
				}
			 break;
			 */
			
			
		
			case Rules.OPS_CUST_VIEW_CARD_DISPUTES_PAGE: 
				try {
					if(session.getAttribute("SESS_USER")==null)
						throw new Exception ("Session has expired, please log in again");

					PPWalletEnvironment.setComment(3, className, "Beginning:");	String userId = null;	String userType = null;	String relationshipNo = null;
					String moduleCode = "C";
					
					userId = ((User) session.getAttribute("SESS_USER")).getUserId(); 
					userType = ((User) session.getAttribute("SESS_USER")).getUserType();
					relationshipNo= ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Ops ID:" + userId, 0, 48));
					request.setAttribute("alldisputes", (ArrayList<DisputeTracker>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllDisputeDetails(relationshipNo));

					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysCustomerDisputesPage()).forward(request, response);
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
				break;
				
				
			case Rules.OPS_VIEW_RAISE_CUST_DISPUTE_PAGE:
				try {
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "opsdisp"); // going to manage disputes menu
					request.setAttribute("lastrule", Rules.OPS_VIEW_RAISE_CUST_DISPUTE_PAGE); //  set the last rule for left menu selection
					SimpleDateFormat formatter1 = new SimpleDateFormat("yyMMdd");
					formatter1.setTimeZone(TimeZone.getTimeZone("UTC")); 
					String relationshipNo = formatter1.format(new Date()) + (RandomStringUtils.random(10, false,true)).toString();
					request.setAttribute("allcustomerdisputes", (ArrayList<DisputeTracker>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllDisputeDetails(relationshipNo));
					PPWalletEnvironment.setComment(2,className," ---> 2  after setting to request the arrAllCustomers object    ");
					try {
						response.setContentType("text/html");
						PPWalletEnvironment.setComment(3,className," before submitting to jsp  "+PPWalletEnvironment.getOpsViewCustomersPage());
						ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysViewCustomerDisputesPage()).forward(request,response);
					}catch (Exception e) {
					}finally {
					}
						
					
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
				break;
				
				
			case Rules.OPS_CUST_RAISE_CUST_DISPUTE_PAGE:
				try {
					PPWalletEnvironment.setComment(3, className, "Start");
					 if (session.getAttribute("SESS_USER") == null) throw new
					 Exception("Session has expired, please log in again");
					  
					 String customerID = null; String reasonId = null; String transactionId = null;
					 String Comment = null; String status = null; String raisedOn = null;
					 String userId = null; String userType = null; String disputeId = null; String m_userType = null;
					 
					 userId = ((User) session.getAttribute("SESS_USER")).getUserId(); 
					 m_userType = ((User) session.getAttribute("SESS_USER")).getUserType();
					 
					 SimpleDateFormat formatter1 = new SimpleDateFormat("yyMMdd");
					 formatter1.setTimeZone(TimeZone.getTimeZone("UTC")); 
					 String refNo = formatter1.format(new Date()) + (RandomStringUtils.random(10, false,true)).toString();
					 String relationshipNo = formatter1.format(new Date()) + (RandomStringUtils.random(10, false,true)).toString();
					 
					  if (request.getParameter("adddisputeid") != null) disputeId = StringUtils.trim(request.getParameter("adddisputeid")); 
					  if (request.getParameter("addtransid") != null) transactionId = StringUtils.trim(request.getParameter("addtransid"));
					  if(request.getParameter("addreasonid") != null) reasonId = StringUtils.trim(request.getParameter("addreasonid"));
					  if(request.getParameter("addcustid") != null) customerID = StringUtils.trim(request.getParameter("addcustid")); 
					  if (request.getParameter("addrefno") != null) refNo =  StringUtils.trim(request.getParameter("addrefno"));
					  if(request.getParameter("addcomment") != null) Comment = StringUtils.trim(request.getParameter("addcomment"));
					  if(request.getParameter("selstatus") != null) status = StringUtils.trim(request.getParameter("selstatus")); 
					  if(request.getParameter("addraisedondate") != null) raisedOn = StringUtils.trim(request.getParameter("addraisedondate")); 
					  if(request.getParameter("addusertype") != null) raisedOn = StringUtils.trim(request.getParameter("addusertype"));
					  
					  if (OpsSystemManageCustomerDao.class.getConstructor().newInstance().addNewDispute(disputeId, customerID, reasonId, refNo, transactionId, Comment, userType, status, raisedOn) == false) { throw new Exception("Problem in inserting dispute for Customer"); }
					
					  PPWalletEnvironment.setComment(3, className, "Start");
					  
					String moduleCode = "C"; // C = Customers Acquiring
					userId = ((User) session.getAttribute("SESS_USER")).getUserId(); userType =  ((User) session.getAttribute("SESS_USER")).getUserType();
							 
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, m_userType, moduleCode,StringUtils.substring("Ops ID:" + m_userType, 0, 48));
					request.setAttribute("alldisputes", (ArrayList<DisputeTracker>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllDisputeDetails(relationshipNo));

					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysRaiseCustomerDisputesPage()).forward(request, response);
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}

				break;
				
				
			case Rules.OPS_CUST_VIEW_CALL_LOGS_PAGE:
				
				try {
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "opscust"); 
					request.setAttribute("lastrule", "Customer Call Logs");
					
					ArrayList <CallLog> arrCustCallLogs= (ArrayList<CallLog>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllCustomerCallLogs();
					//PPWalletEnvironment.setComment(2,className," ---> 1 before setting to request the arrCustCallLogs object   is  "+arrCustCallLogs);
					request.setAttribute("allcustcalllogs", arrCustCallLogs);
					PPWalletEnvironment.setComment(2,className," ---> 2  after setting to request the arrCustCallLogs object    ");
					if(arrCustCallLogs!=null) {
						//PPWalletEnvironment.setComment(2,className,"arrCustCallLogs size  is  "+arrCustCallLogs.size());
					}
					try {
						response.setContentType("text/html");
						PPWalletEnvironment.setComment(3,className," before submitting to jsp  "+PPWalletEnvironment.getOpsSysCustCallLog());
						ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysCustCallLog()).forward(request,response);
					}catch (Exception e) {
						
					}finally {
					}
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
				break;
				
			
				
		 case Rules.OPS_CUST_ADD_CALL_LOGS:
			 
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "opscust"); // going to profile
				request.setAttribute("lastrule", "Customer Call Logs"); // set the last rule for left menu
																						// selection

				String UserType = null;	String ReferenceNo = null; 	String UserName = null;
				String CallDescription= null;	String Comment = null; String CalledOn = null;

				String userId = ((User) session.getAttribute("SESS_USER")).getUserId();
				String userType = ((User) session.getAttribute("SESS_USER")).getUserType();
				String moduleCode = "C"; // 
				ArrayList<CallLog> arrCustCallLogs = null;

				if (request.getParameter("usertype") != null) UserType = StringUtils.trim(request.getParameter("usertype"));
				if (request.getParameter("referenceno") != null) ReferenceNo = StringUtils.trim(request.getParameter("referenceno"));
				if (request.getParameter("username") != null) UserName = StringUtils.trim(request.getParameter("username"));
				if (request.getParameter("calldescription") != null) CallDescription = StringUtils.trim(request.getParameter("calldescription"));
				if (request.getParameter("comment") != null) Comment = StringUtils.trim(request.getParameter("comment"));
				if (request.getParameter("calledon") != null) CalledOn = StringUtils.trim(request.getParameter("calledon"));

				if (OpsMerchantManageDao.class.getConstructor().newInstance().addNewCallLog(UserType,ReferenceNo, userId, CallDescription, Comment, CalledOn)) {
					// call the audit trail here
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode,StringUtils.substring("Added UserId" + userId, 0, 48));
				 arrCustCallLogs= (ArrayList<CallLog>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllCustomerCallLogs();
					request.setAttribute("allcustcalllogs", arrCustCallLogs);
				} else {
					throw new Exception("Problem with the addition of MCC");
				}
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysCustCallLog()).forward(request,response);
				} finally {
					if (arrCustCallLogs != null)arrCustCallLogs = null;
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
 
			
		/*
		 * case Rules.OPS_CUST_SHOW_EACH_DISPUTE_PAGE: try {
		 * request.setAttribute("langPref", "en"); request.setAttribute("lastaction",
		 * "opsdisp"); request.setAttribute("lastrule",
		 * Rules.OPS_CUST_VIEW_CARD_DISPUTES_PAGE); String disputeId = null;
		 * if(request.getParameter("hdndisputeid")!=null) disputeId =
		 * StringUtils.trim(request.getParameter("hdndisputeid"));
		 * 
		 * request.setAttribute("specificdispute",
		 * (Disputes)OpsSystemManageCustomerDao.class.getConstructor().newInstance().
		 * getSpecificDisputeDetails(disputeId));
		 * 
		 * //PPWalletEnvironment.setComment(2,
		 * className," ---> 1 before setting to request the arrarrDisputedetails object   is  "
		 * +arrDisputeDetails);
		 * request.setAttribute("alldisputedetails",(ArrayList<DisputeTracker>)
		 * OpsSystemManageCustomerDao.class.getConstructor().newInstance().
		 * getDisputeTrackerDetails(disputeId));
		 * 
		 * 
		 * try { response.setContentType("text/html");
		 * PPWalletEnvironment.setComment(3,className," before submitting to jsp  "
		 * +PPWalletEnvironment.getOpsSysDisputeandTrackerPage());
		 * ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysDisputeandTrackerPage()
		 * ).forward(request,response); }catch (Exception e) {
		 * 
		 * }finally { }
		 * 
		 * 
		 * } catch (Exception e) { callException(request, response, ctx, session, e,
		 * e.getMessage()); } break;
		 */		
			
			case Rules.OPS_CUST_UPDATE_DISPUTE_STATUS:
				try {
					if(session.getAttribute("SESS_USER")==null)
						throw new Exception ("Session has expired, please log in again");
					
					String disputeid = null;	String status = null;
					String relationshipNo = null;	String userId = null; String userType = null;
					
					relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					
					if(request.getParameter("hdndispid")!=null)		disputeid = StringUtils.trim( request.getParameter("hdndispid") );
					if(request.getParameter("hdnstatus")!=null)		status = StringUtils.trim( request.getParameter("hdnstatus") );
					
					PPWalletEnvironment.setComment(2, className, "status is : "+status);
					PPWalletEnvironment.setComment(2, className, "id is : "+disputeid);

					if( OpsSystemManageCustomerDao.class.getConstructor().newInstance().updateDisputeStatus(disputeid, status) == false) {
						throw new Exception ("Problem in updating dispute status");
					}
					
					//audit trail
					String moduleCode = "C"; //M = Customer Acquiring
					userId = ((User)session.getAttribute("SESS_USER")).getUserId();
					userType = ((User)session.getAttribute("SESS_USER")).getUserType();
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Customer updated dispute status "+userId, 0, 48) );
					
					request.setAttribute("alldisputedetails", (ArrayList<DisputeTracker>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllDisputeDetails(relationshipNo));
					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsUpdateCustDisputePage()).forward(request, response);
					}catch(Exception e){
						callException(request, response, ctx, session,e, e.getMessage());
					}finally {
						
					}
					
					break;
				case Rules.OPS_CUST_SHOW_WALLETS_FOR_A_CUSTOMER:
					try {
						String relationshipNo = null;	String customerName = null;
						if(request.getParameter("hdnrelationshipno")!=null)		relationshipNo = StringUtils.trim( request.getParameter("hdnrelationshipno") );
						if(request.getParameter("hdncustomername")!=null)		customerName = StringUtils.trim( request.getParameter("hdncustomername") );
						PPWalletEnvironment.setComment(2, className, "relationshipNo : "+relationshipNo + "Name is "+ customerName);
						try {
							request.setAttribute("walletlist", (ArrayList<Wallet>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getWalletDetails(relationshipNo) ) ;
							request.setAttribute("customername", customerName) ;
							request.setAttribute("relationshipno", relationshipNo) ;
							response.setContentType("text/html");
							ctx.getRequestDispatcher(PPWalletEnvironment.getOpsShowWalletForACustomerPage()).forward(request, response);
							
						}finally {
							if(relationshipNo!=null)	relationshipNo = null;
						}
						
					}catch (Exception e) {
						callException(request, response, ctx, session,e, e.getMessage());
					}
					
				break;
					
					
			}	
		
		
		}

		
	

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {

		switch (rulesaction){
		case Rules.JSON_OPS_LOGIN_VALIDATE:	case Rules.JSON_CUST_LOGIN_VALIDATE:
			PrintWriter jsonOutput_1 = null;
			try {
				String userId = null;	String userPwd = null;	String userType = null; String privateKey = null;	boolean allow = true;
						
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("userpwd")!=null) userPwd = jsonObj.get("userpwd").toString().replaceAll("\"", "");
				if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(3, className, "Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect "+privateKey);
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
		case Rules.JSON_OPS_CUST_GETLASTHUNDRED_TXNS_FORWALLET:
			try {
				PrintWriter jsonOutput_3 = null;	String privateKey = null;	String walletId = null;	boolean allow = true;  
				if(jsonObj.get("apikey")!=null) privateKey = jsonObj.get("apikey").toString().replaceAll("\"", "");
				if(jsonObj.get("walletid")!=null) walletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
				
				//PPWalletEnvironment.setComment(3, className, "privateKey is "+privateKey   + "walletId " +walletId );

				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPublic())) {
					allow = false;
				//	PPWalletEnvironment.setComment(3, className, "Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect "+privateKey);
				}
				
				try {
					ArrayList<Transaction> arrTransactions = (ArrayList<Transaction>)OpsSystemManageCustomerDao.class.getConstructor().newInstance().getAllTransactionsForWallet(walletId);
				if(arrTransactions !=null) {
				//	PPWalletEnvironment.setComment(3, className, "arrTransactions is  "+ arrTransactions.size());

				}
					

					jsonOutput_3	= response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Json Object
					
					String[] txnCodeArray  =null;		String[] txnAmountArray  =null;  String[] txnCurrencyIdArray  =null;  String[] txnTxnModeArray  =null;  String[] txnDateTimeArray  =null;
					String[] txnSysRefArray  =null;
					if( allow && arrTransactions!=null) {
					//	PPWalletEnvironment.setComment(3, className, "in the loop arrTransactions   ");

						txnCodeArray = new String[arrTransactions.size()];	txnAmountArray = new String[arrTransactions.size()];	txnCurrencyIdArray = new String[arrTransactions.size()];	
						txnTxnModeArray = new String[arrTransactions.size()];	txnDateTimeArray = new String[arrTransactions.size()];	txnSysRefArray = new String[arrTransactions.size()];
						for(int i=0;i<arrTransactions.size();i++){
							txnCodeArray [i] =		((Transaction)arrTransactions.get(i)).getTxnCode();
							txnAmountArray[i] =		((Transaction)arrTransactions.get(i)).getTxnAmount();
							txnCurrencyIdArray[i] =	((Transaction)arrTransactions.get(i)).getTxnCurrencyId();
							txnTxnModeArray[i] =	((Transaction)arrTransactions.get(i)).getTxnMode();
							txnDateTimeArray[i] =  	((Transaction)arrTransactions.get(i)).getTxnDateTime() ;
							txnSysRefArray[i] =	((Transaction)arrTransactions.get(i)).getSystemReference();
						}
						  obj.add("txncode", gson.toJsonTree(txnCodeArray));	 	
						   obj.add("txnamount", gson.toJsonTree(txnAmountArray));	 	
						   obj.add("txncurrencyid", gson.toJsonTree(txnCurrencyIdArray));
						  obj.add("txnmode", gson.toJsonTree(txnTxnModeArray));	 
						  obj.add("txndatetime", gson.toJsonTree(  txnDateTimeArray));
						  obj.add("txnsysref", gson.toJsonTree(  txnSysRefArray));
						  obj.add("error", gson.toJsonTree("false"));
						
						
					}else {
						obj.add("error", gson.toJsonTree("true"));
						PPWalletEnvironment.setComment(1, className, "Rules JSON_OPS_CUST_GETLASTHUNDRED_TXNS_FORWALLET no transaction for this wallet ");
					}
					//PPWalletEnvironment.setComment(3, className, "after else  " + gson.toJson(obj));

					jsonOutput_3.print(gson.toJson(obj));
					jsonOutput_3.close();
					
				}finally {
					if(jsonOutput_3!=null)
						jsonOutput_3.close();
				}
				
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_CUST_GETLASTHUNDRED_TXNS_FORWALLET "+e.getMessage());
			}
		break;
		}

	}



	@Override
	public void callException(HttpServletRequest request, HttpServletResponse response, ServletContext ctx,
			HttpSession session, Exception e, String msg) throws Exception {
		try {
			if(session!=null) 	session.invalidate();
				PPWalletEnvironment.setComment(2, className, "Error is "+msg);
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
		// TODO Auto-generated method stub

	}

}

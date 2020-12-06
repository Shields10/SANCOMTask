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
import com.ppwallet.dao.DisputeDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.model.DisputeReasons;
import com.ppwallet.model.DisputeTracker;
import com.ppwallet.model.Disputes;
import com.ppwallet.model.User;



public class CustDisputeRulesImpl implements Rules {
	private static String className = CustDisputeRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		// TODO Auto-generated method stub
		HttpSession session	= request.getSession(false);
		
		switch (rules){
		
		
		case Rules.CUST_VIEW_DISPUTE:
			try{
				if(session.getAttribute("SESS_USER")==null)
					throw new Exception ("Session has expired, please log in again");
				String relationshipNo = null;
				
				relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
				request.setAttribute("alldisputes", (ArrayList<Disputes>)DisputeDao.class.getConstructor().newInstance().getAllDisputes(relationshipNo));
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustViewDisputePage()).forward(request, response);
				}finally {
					if(relationshipNo!=null) relationshipNo=null;
				}
				
				
				
			}catch(Exception e){
				callException(request, response, ctx, session,e, e.getMessage());
			}
			
			break;
		
		
		
		
		case Rules.CUST_RAISE_DISPUTE:
			try {
				if(session.getAttribute("SESS_USER")==null)
					throw new Exception ("Session has expired, please log in again");
				String userType = null;
				userType = ((User)session.getAttribute("SESS_USER")).getUserType();
				request.setAttribute("dsptreason", (ArrayList<DisputeReasons>)DisputeDao.class.getConstructor().newInstance().getAllDisputeReasons(userType));
//								response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustRaiseDisputePage()).forward(request, response);
				}finally {
					if(userType!=null) userType=null;
				}
				
						
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
		break;
		
		
		
		case Rules.CUST_ADD_NEW_DISPUTE:
			try{
				if(session.getAttribute("SESS_USER")==null)
					throw new Exception ("Session has expired, please log in again");
				
				String comment = "";	String reasonId = null; String transactionId = "";	String status = null; String userId = null; String userType = null; String relationshipNo= null; 
				
				userId = ((User)session.getAttribute("SESS_USER")).getUserId();
				userType = ((User)session.getAttribute("SESS_USER")).getUserType();
				relationshipNo= ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
		
				
				if(request.getParameter("dsptcomment")!=null)	comment = StringUtils.trim( request.getParameter("dsptcomment") );
				if(request.getParameter("hdnreasonid")!=null)	reasonId = StringUtils.trim( request.getParameter("hdnreasonid") );
				if(request.getParameter("inputtransactionid")!=null)		transactionId = StringUtils.trim( request.getParameter("inputtransactionid") );
				if(request.getParameter("hdnstatus")!=null)		status = StringUtils.trim( request.getParameter("hdnstatus") );

				if( DisputeDao.class.getConstructor().newInstance().addNewDispute(transactionId, userId, userType, comment, reasonId, relationshipNo, status)
					 == false) {
					throw new Exception ("Problem in inserting dispute for Customer");
				}
				
				String moduleCode = "C"; //M = Customer Acquiring
				
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Customer raised dispute "+userId, 0, 48) );
				request.setAttribute("alldisputes", (ArrayList<Disputes>)DisputeDao.class.getConstructor().newInstance().getAllDisputes(relationshipNo));	
				
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustViewDisputePage()).forward(request, response);
				}finally {
					if(comment!=null) comment=null; if(reasonId!=null) reasonId=null; if(status!=null) status=null;
					if(userType!=null) userType=null; if(relationshipNo!=null) relationshipNo=null; 
				}
				
			}catch(Exception e){
				callException(request, response, ctx, session,e, e.getMessage());
			}
			
		break;
		
		case Rules.CUST_SHOW_SPECIFIC_DISPUTE_PAGE:
			
			try{
				if(session.getAttribute("SESS_USER")==null)
					throw new Exception ("Session has expired, please log in again");
				
					String disputeId = null;	
					if(request.getParameter("hdnreqid")!=null)	disputeId = StringUtils.trim( request.getParameter("hdnreqid") );

					request.setAttribute("showdispute", (Disputes)DisputeDao.class.getConstructor().newInstance().getDisputeDetail(disputeId));
					request.setAttribute("disputethreads", (ArrayList<DisputeTracker>)DisputeDao.class.getConstructor().newInstance().getAllDisputeTrackers(disputeId));
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustSpecificDisputePage()).forward(request, response);
					}finally {
						if(disputeId!=null) disputeId=null;
					}
					
				
			}catch(Exception e){
				callException(request, response, ctx, session,e, e.getMessage());
			}finally {
				
			}
		
			break;
			
			
		case Rules.CUST_ADD_COMMENT_FOR_A_DISPUTE:
			try{
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
				String moduleCode = "C"; //M = Customer Acquiring
				userId = ((User)session.getAttribute("SESS_USER")).getUserId();
				userType = ((User)session.getAttribute("SESS_USER")).getUserType();
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Customer added dispute comment "+userId, 0, 48) );
				
				request.setAttribute("showdispute", (Disputes)DisputeDao.class.getConstructor().newInstance().getDisputeDetail(disputeId));

				request.setAttribute("disputethreads", (ArrayList<DisputeTracker>)DisputeDao.class.getConstructor().newInstance().getAllDisputeTrackers(disputeId));
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustSpecificDisputePage()).forward(request, response);
				}finally {
					if(disputeId!=null) disputeId=null; if(comment!=null) comment=null;  if(userType!=null) userType=null; if(userId!=null) userId=null;
				}
				
			}catch(Exception e){
				callException(request, response, ctx, session,e, e.getMessage());
			}finally {
				
			}
			
		break;
		
		case Rules.CUST_UPDATE_DISPUTE_STATUS:
			
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

				if( DisputeDao.class.getConstructor().newInstance().updateDisputeStatus(disputeid, status) == false) {
					throw new Exception ("Problem in updating dispute status");
				}
				
				//audit trail
				String moduleCode = "C"; //M = Customer Acquiring
				userId = ((User)session.getAttribute("SESS_USER")).getUserId();
				userType = ((User)session.getAttribute("SESS_USER")).getUserType();
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Customer updated dispute status "+userId, 0, 48) );
				
				request.setAttribute("alldisputes", (ArrayList<Disputes>)DisputeDao.class.getConstructor().newInstance().getAllDisputes(relationshipNo));
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustViewDisputePage()).forward(request, response);
				}finally {
					if(disputeid!=null) disputeid=null; if(status!=null) status=null;  if(userType!=null) userType=null; if(userId!=null) userId=null;if(relationshipNo!=null) relationshipNo=null;
				}
				
				}catch(Exception e){
					callException(request, response, ctx, session,e, e.getMessage());
				}
				
				break;
		}
	}

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		// TODO Auto-generated method stub
		
		switch (rulesaction){
		
		case Rules.JSON_CUST_RAISE_DISPUTE:
			
			try {
				String userType = null; String privateKey = null;
				PrintWriter jsonOutput_1 = null;	boolean allow = false; ArrayList<DisputeReasons> aryDispute = null;	
				if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_RAISE_DISPUTE: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				aryDispute = DisputeDao.class.getConstructor().newInstance().getAllDisputeReasons(userType);
				if(aryDispute!=null){
					int count = aryDispute.size();
					String[] reasonIdArray = new String [count];
					String[] reasonDescriptionArray = new String [count];
					
					
					  // put some value pairs into the JSON object .
					 for(int i=0;i<aryDispute.size();i++){
						 reasonIdArray[i]= ((DisputeReasons)aryDispute.get(i)).getDisputeReasonId();
						 reasonDescriptionArray[i]= ((DisputeReasons)aryDispute.get(i)).getDisputeReasonDesc();
						 
					 }
					 
					  obj.add("reasonid", gson.toJsonTree(reasonIdArray));
					  obj.add("reasondescription", gson.toJsonTree(reasonDescriptionArray));
					  
					  obj.add("error", gson.toJsonTree("false"));
					
				} else {
					
					 obj.add("error", gson.toJsonTree("true"));
				}
				
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close();if(userType!=null) userType=null;
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
				}
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_RAISE_DISPUTE "+e.getMessage());
			}
			
			
		break;
		
			case Rules.JSON_CUST_ADD_NEW_DISPUTE:	
				try {
					String comment = "";	String reasonId = null; String transactionId = "";	String status = null; String userId = null; String userType = null; String relationshipNo= null; String privateKey = null;
					PrintWriter jsonOutput_1 = null;	boolean allow = false; 	
					if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
					if(jsonObj.get("comment")!=null) comment = jsonObj.get("comment").toString().replaceAll("\"", "");
					if(jsonObj.get("reasonid")!=null) reasonId = jsonObj.get("reasonid").toString().replaceAll("\"", "");
					if(jsonObj.get("status")!=null) status = jsonObj.get("status").toString().replaceAll("\"", "");
					if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
					if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
					if(jsonObj.get("transactionid")!=null) transactionId = jsonObj.get("transactionid").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_RAISE_DISPUTE: pvt key is incorrect "+privateKey);
					}
					jsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Json Object

					allow = DisputeDao.class.getConstructor().newInstance().addNewDispute(transactionId, userId, userType, comment, reasonId, relationshipNo, status);
					
					if(allow) {
						obj.add("error", gson.toJsonTree("false"));
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					try {
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(comment!=null) comment=null;if(userType!=null) userType=null;
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
						if(reasonId!=null) reasonId=null;	if(transactionId!=null) transactionId=null;
						if(relationshipNo!=null) relationshipNo=null; 
					}
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE "+e.getMessage());
				}
				break;
				
			case Rules.JSON_CUST_VIEW_DISPUTE:
				
				try {
					PrintWriter jsonOutput_1 = null; String relationshipNo = null; String privateKey = null;	boolean allow = true; ArrayList<Disputes> aryDisputes = null;
					if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_VIEW_DISPUTE: pvt key is incorrect "+privateKey);
					}
					jsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Json Object
					aryDisputes = DisputeDao.class.getConstructor().newInstance().getAllDisputes(relationshipNo);
					if(aryDisputes!=null){
						int count = aryDisputes.size();
						String[] dsptIdArray = new String [count];	String[] txnidArray = new String[count];
						String[] reasonIdArray = new String [count];	String[] raisedbyArray = new String[count];
						String[] userTypeArray = new String [count];	String[] statusArray = new String[count];
						String[] dateArray = new String [count];	String[] reasondescArray = new String[count];
						
						 // put some value pairs into the JSON object .
						  for(int i=0;i<aryDisputes.size();i++){
//							  merchIdArray[i]= ((Customer)vecCustomer.get(i)).getMerchantId();
							   dsptIdArray[i]= ((Disputes)aryDisputes.get(i)).getDisputeId();
							   txnidArray[i]= ((Disputes)aryDisputes.get(i)).getTransactionId();
							   reasonIdArray[i]= ((Disputes)aryDisputes.get(i)).getReasonId();
							   raisedbyArray[i]= ((Disputes)aryDisputes.get(i)).getRaisedbyUserId();
							   userTypeArray[i]= ((Disputes)aryDisputes.get(i)).getUserType();
							   
							   statusArray[i]= ((Disputes)aryDisputes.get(i)).getStatus();
							   dateArray[i]= ((Disputes)aryDisputes.get(i)).getRaisedOn();
							   reasondescArray[i]= ((Disputes)aryDisputes.get(i)).getReasonDesc();
							  
							  
						  }
						  
						  obj.add("dsptid", gson.toJsonTree(dsptIdArray));  obj.add("txnid", gson.toJsonTree(txnidArray));
						  obj.add("resonid", gson.toJsonTree(reasonIdArray));  obj.add("raisedby", gson.toJsonTree(raisedbyArray));
						  obj.add("usertype", gson.toJsonTree(userTypeArray));  obj.add("status", gson.toJsonTree(statusArray));
						  obj.add("date", gson.toJsonTree(dateArray));  obj.add("resondesc", gson.toJsonTree(reasondescArray));
						  obj.add("error", gson.toJsonTree("false"));
						
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					try {
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(relationshipNo!=null) relationshipNo=null;if(aryDisputes!=null) aryDisputes=null;
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
		
					}
					
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_VIEW_DISPUTE "+e.getMessage());
				}
				
				
				
				break;
				
				
			case Rules.JSON_CUST_ADD_COMMENT_FOR_A_DISPUTE:			
				
				try {
					String disputeId = null; String comment = null; String userType = null; String userId = null; String privateKey =null; boolean allow = true;
					
					PrintWriter jsonOutput_1 = null;
					if(jsonObj.get("dsptid")!=null) disputeId = jsonObj.get("dsptid").toString().replaceAll("\"", "");
					if(jsonObj.get("comment")!=null) comment = jsonObj.get("comment").toString().replaceAll("\"", "");
					if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
					if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					
					PPWalletEnvironment.setComment(3, className, " "+disputeId +" "+comment +" "+userType+ " "+userId +" "+privateKey);
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_ADD_COMMENT_FOR_A_DISPUTE: pvt key is incorrect "+privateKey);
					}
					
					jsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject();
					
					if (DisputeDao.class.getConstructor().newInstance().addCommentOnADispute(disputeId, userId, userType, comment) == false) {
						allow = false;
					}
					if (allow == true) {
						obj.add("error", gson.toJsonTree("false"));
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					try {
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
					
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(disputeId!=null) disputeId=null;
						if(userType!=null) userType=null;if(privateKey!=null) privateKey=null; if(gson!=null) gson=null;if(comment!=null) comment=null;
					 
					}
					
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_ADD_COMMENT_FOR_A_DISPUTE "+e.getMessage());
				}
				
				
				break;
					
					
				
				
			case Rules.JSON_CUST_SHOW_SPECIFIC_DISPUTE_PAGE:
				
				try {
					PrintWriter jsonOutput_1 = null; String privateKey = null; boolean allow = true; String disputeId = null; Disputes disputes = null;
					ArrayList<DisputeTracker> aryDisputeThreads = null;
					if(jsonObj.get("disputeid")!=null) disputeId = jsonObj.get("disputeid").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					PPWalletEnvironment.setComment(2, className, "disputeId is "+ disputeId);
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_SHOW_SPECIFIC_DISPUTE_PAGE: pvt key is incorrect "+privateKey);
					}
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Google Json Object
					jsonOutput_1 = response.getWriter();
					
//					aryDisputeDtls = (ArrayList<Disputes>)CustomerOperationsDao.class.newInstance().viewCustomerBillersJSON(customerid);
					disputes = (Disputes)DisputeDao.class.getConstructor().newInstance().getDisputeDetail(disputeId);
					aryDisputeThreads = (ArrayList<DisputeTracker>)DisputeDao.class.getConstructor().newInstance().getAllDisputeTrackers(disputeId);
					
					if(disputes != null && allow) {
						obj.add("disputeid", gson.toJsonTree(disputes.getDisputeId()));    obj.add("reasonid", gson.toJsonTree(disputes.getReasonId()));
						obj.add("raisedby", gson.toJsonTree(disputes.getRaisedbyUserId()));     obj.add("disputestatus", gson.toJsonTree(disputes.getStatus()));
						obj.add("date", gson.toJsonTree(disputes.getRaisedOn()));         obj.add("txnid", gson.toJsonTree(disputes.getTransactionId()));
						obj.add("reasondesc", gson.toJsonTree(disputes.getReasonDesc()));   obj.add("usertype", gson.toJsonTree(disputes.getUserType()));
						obj.add("usercommet", gson.toJsonTree(disputes.getUserComment()));
						
						if(aryDisputeThreads != null) {
							obj.add("ithasthread", gson.toJsonTree("true"));
							int count = aryDisputeThreads.size();
							String[] trackIdArray = new String [count];	    String[] disputeIdArray = new String[count];
							String[] userTypeArray = new String [count];	String[] updaterCommentArray = new String[count];
							String[] updaterIdArray = new String[count];    String[] lastUpdate = new String[count];
							
							for(int i=0;i<aryDisputeThreads.size();i++){
								trackIdArray[i]= ((DisputeTracker)aryDisputeThreads.get(i)).getTrackingid();
								disputeIdArray[i]= ((DisputeTracker)aryDisputeThreads.get(i)).getDisputeId();
								updaterIdArray[i]= ((DisputeTracker)aryDisputeThreads.get(i)).getUpdaterId();
								userTypeArray[i]= ((DisputeTracker)aryDisputeThreads.get(i)).getUpdaterType();
								updaterCommentArray[i]= ((DisputeTracker)aryDisputeThreads.get(i)).getUpdaterComment();
								lastUpdate[i]= ((DisputeTracker)aryDisputeThreads.get(i)).getLastUpdated();
							}
							obj.add("trackid", gson.toJsonTree(trackIdArray));  obj.add("disputeidary", gson.toJsonTree(disputeIdArray));							 
							obj.add("updateid", gson.toJsonTree(updaterIdArray));  obj.add("usertypeary", gson.toJsonTree(userTypeArray));							 
							obj.add("updatercomment", gson.toJsonTree(updaterCommentArray));  obj.add("lastUpdate", gson.toJsonTree(lastUpdate));
							PPWalletEnvironment.setComment(2, className, "updaterIdArray "+ ((DisputeTracker)aryDisputeThreads.get(0)).getUpdaterName());
							 
						}else {
							obj.add("ithasthread", gson.toJsonTree("false"));
						}
						
						obj.add("error", gson.toJsonTree("false"));
						
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					
					PPWalletEnvironment.setComment(2, className, "obj is "+ obj);
					
					try {
						
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
						
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(disputeId!=null)disputeId=null;if(disputes!=null) disputes=null;if(aryDisputeThreads!=null) aryDisputeThreads=null;
					if(privateKey!=null) privateKey=null; if(gson!=null) gson=null;
						 
					}
					
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_SHOW_SPECIFIC_DISPUTE_PAGE "+e.getMessage());
				}
				break;
				
				
			case Rules.JSON_CUST_UPDATE_DISPUTE_STATUS:	
				
				
				try {
					String disputeid = null;	String status = null; String privateKey = null; boolean allow = true;
					PrintWriter jsonOutput_1 = null;
					
					
					if(jsonObj.get("status")!=null) status = jsonObj.get("status").toString().replaceAll("\"", "");
					if(jsonObj.get("disputeid")!=null) disputeid = jsonObj.get("disputeid").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect "+privateKey);
					}
					
					jsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Json Object
					
					PPWalletEnvironment.setComment(1, className, "id is "+disputeid +" status "+status);
					
					if( DisputeDao.class.getConstructor().newInstance().updateDisputeStatus(disputeid, status) == false) {
						allow = false;
					}
					if(allow == true) {
						obj.add("error", gson.toJsonTree("false"));
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					try {
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
						
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(disputeid!=null) disputeid=null;if(status!=null) status=null;
						if(privateKey!=null) privateKey=null; if(gson!=null) gson=null;
						
						
					}
				
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_UPDATE_DISPUTE_STATUS "+e.getMessage());
				}
				break;
		}
		
	}

	@Override
	public void callException(HttpServletRequest request, HttpServletResponse response, ServletContext ctx,
			HttpSession session, Exception e, String msg) throws Exception {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
	}

}

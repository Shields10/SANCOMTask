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
import com.ppwallet.dao.LoyaltyDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.dao.WalletDao;
import com.ppwallet.model.Loyalty;
import com.ppwallet.model.LoyaltyRules;
import com.ppwallet.model.User;
import com.ppwallet.model.Wallet;

public class LoyaltyRulesImpl implements Rules {

	private static String className = LoyaltyRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(true);

		switch (rules){
		
		
		
		case Rules.CUST_VIEW_REWARDS_PAGE:
			try {
				String langType = null;	
				request.setAttribute("loyaltypointsdetails", (ArrayList<Loyalty>)LoyaltyDao.class.getConstructor().newInstance().getLoyaltyTransactionsForUser (   ((User)session.getAttribute("SESS_USER")).getRelationshipNo()   ));
				//request.setAttribute("loyaltyrules",   (ArrayList<LoyaltyRules>)CustLoyaltyDao.class.getConstructor().newInstance().getLoyaltyRules (((User)session.getAttribute("SESS_USER")).getUserId()) );
			 	request.setAttribute("langPref", "en");				
				request.setAttribute("lastaction", "rwd");	request.setAttribute("lastrule", "View Rewards");
				response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerLoyaltyViewPage()).forward(request, response);
			}catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
		break;	
	
			case Rules.CUST_CLAIM_REWARDS_PAGE:
					try {
						  ArrayList<Wallet> ArrWallets = null;
						String langType = null;	
				
						// Get Loyallty transaction details for the user
						request.setAttribute("loyaltypointsdetails", (ArrayList<Loyalty>)LoyaltyDao.class.getConstructor().newInstance().getLoyaltyTransactionsForUser (   ((User)session.getAttribute("SESS_USER")).getRelationshipNo() ));
						ArrWallets = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
						request.setAttribute("arrWallets",ArrWallets );
					 	request.setAttribute("langPref", "en");				
						request.setAttribute("lastaction", "rwd");	
						request.setAttribute("lastrule", Rules.CUST_CLAIM_REWARDS_PAGE);
						response.setContentType("text/html");
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerLoyaltyClaimPage()).forward(request, response);

						}finally {
							if(ArrWallets!=null) ArrWallets=null;
						}
					}catch (Exception e) {
						callException(request, response, ctx, session, e, e.getMessage());
					}
				break;	
			case Rules.CUST_VIEW_REWARDS_RULES_PAGE:
				try {
					if(session.getAttribute("SESS_USER")==null)
						throw new Exception ("Session has expired, please log in again");
					
					// Get Loyalty transaction details for the user
					request.setAttribute("rewardsruledetails", (ArrayList<LoyaltyRules>)LoyaltyDao.class.getConstructor().newInstance().getLoyaltyRules());
					
					request.setAttribute("lastaction", "rwd");	
					request.setAttribute("lastrule", "Reward Rules");
					response.setContentType("text/html");
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerLoyaltyRulesViewPage()).forward(request, response);
				}catch (Exception e) {
					callException(request, response, ctx, session,e, e.getMessage());
				}
			
			break;
			
			
			/*===========================This handles individual reward claim ====================*/
			
			
			case Rules.CUST_CLAIM_EACH_REWARD:
				try {
					PPWalletEnvironment.setComment(3,className,"in case rule CUST_CLAIM_EACH_REWARD");
				 	request.setAttribute("langPref", "en");				

					boolean success; String relationshipNo = null; String seqno = null;  String claimedWalletId = null;
			         String walletId = null; String pointAccrued = null;	String payMode = null; String txnRef=null;
			         String walletBalance = null; ArrayList<Wallet> ArrWallets = null; 
			         
					if(request.getParameter("hdnseqno")!=null) 		 seqno = StringUtils.trim(request.getParameter("hdnseqno"));
					if(request.getParameter("hdnwalletid")!=null) 		 		claimedWalletId = StringUtils.trim(request.getParameter("hdnwalletid"));
					if(request.getParameter("hdnpointaccured")!=null) 		 		pointAccrued = StringUtils.trim(request.getParameter("hdnpointaccured"));
					if(request.getParameter("hdnpaymode")!=null) 	payMode = StringUtils.trim(request.getParameter("hdnpaymode"));
					if(request.getParameter("hdntxnref")!=null) 	txnRef = StringUtils.trim(request.getParameter("hdntxnref"));
					
					walletId = claimedWalletId.substring(0, claimedWalletId.indexOf(",", 0));
					walletBalance = claimedWalletId.substring( (claimedWalletId.indexOf(",", 0))+1, claimedWalletId.length());
					PPWalletEnvironment.setComment(3,className,"  Claimedwalletid is " +claimedWalletId );
					PPWalletEnvironment.setComment(3,className,"  seqno is  " +seqno );	
				 if(session.getAttribute("SESS_USER")==null) throw new Exception
				  ("Session has expired, please log in again");
				 
					PPWalletEnvironment.setComment(3,className,"after session");

					// Get Loyalty transaction details for the user
					relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					PPWalletEnvironment.setComment(3,className,"relationshipno is "+relationshipNo  );
					success = (boolean)LoyaltyDao.class.getConstructor().newInstance().updateLoyaltyRewardsForAPoint(relationshipNo, seqno, walletId , pointAccrued, payMode, walletBalance,txnRef);
					 if(success == false) throw new Exception ("Failed  Claim Rewards..");
					else
						PPWalletEnvironment.setComment(3,className,"success is  "  + success  );

					//SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipno, userType, moduleCode, StringUtils.substring("Redeem Loyaltypoints "+relationshipno, 0, 48) );
					// *******Step 1:Get the pointsConversion
					// *******Step 2:Calculate Loyalty -  Convert the points to money
					//********Step 3: Update the loyalty_points_bc balance and Record transaction
					//********Step 4: update wallet ledger 
					//********Step 5: Update wallet details
					//request.setAttribute("loyaltypointsdetails", (ArrayList<Loyalty>)LoyaltyDao.class.getConstructor().newInstance().getUnclaimedLoyaltyTransactionsForUser (   ((User)session.getAttribute("SESS_USER")).getRelationshipno() ));
					ArrWallets = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
					request.setAttribute("arrWallets",ArrWallets );
					request.setAttribute("loyaltypointsdetails", (ArrayList<Loyalty>)LoyaltyDao.class.getConstructor().newInstance().getLoyaltyTransactionsForUser (   ((User)session.getAttribute("SESS_USER")).getRelationshipNo() ));
					request.setAttribute("lastaction", "rwd");	
					request.setAttribute("lastrule", CUST_CLAIM_EACH_REWARD);
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerLoyaltyClaimPage()).forward(request, response);

					}finally {
						if(relationshipNo!=null) relationshipNo=null; if(seqno!=null) seqno=null; if(claimedWalletId!=null) claimedWalletId=null;
						if(walletId!=null) walletId=null; if(pointAccrued!=null) pointAccrued=null; if(payMode!=null) payMode=null; if(walletBalance!=null) walletBalance=null;
						if(ArrWallets!=null) ArrWallets=null;
						
					}
					
				}catch (Exception e) {
					callException(request, response, ctx, session,e, e.getMessage());
				}
			
			break;
			
			
			
			 case Rules.CUST_CLAIM_ALL_REWARD:
			 try{
						boolean success; String relationshipNo = null; String accruedWalletId = null; String pointAccrued = null;	
					String payMode = null;	 String walletBalance = null;  String walletId = null; ArrayList<Wallet> ArrWallets = null;

					if(request.getParameter("hdnwalletid")!=null) 		 		accruedWalletId = StringUtils.trim(request.getParameter("hdnwalletid"));
					
					relationshipNo =  ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					payMode = PPWalletEnvironment.getCodeLoyaltyRedeemRate();
					walletId = accruedWalletId.substring(0, accruedWalletId.indexOf(",", 0));
					walletBalance = accruedWalletId.substring( (accruedWalletId.indexOf(",", 0))+1, accruedWalletId.length());
					
					PPWalletEnvironment.setComment(3,className,"  Claimedwalletid is " +accruedWalletId );
					
					success = (boolean)LoyaltyDao.class.getConstructor().newInstance().updateLoyaltyRewardsForAllPoint(relationshipNo, walletId , walletBalance, payMode);
					
					//request.setAttribute("loyaltypointsdetails", (ArrayList<Loyalty>)LoyaltyDao.class.getConstructor().newInstance().getUnclaimedLoyaltyTransactionsForUser (   ((User)session.getAttribute("SESS_USER")).getRelationshipno() ));
					
					request.setAttribute("loyaltypointsdetails", (ArrayList<Loyalty>)LoyaltyDao.class.getConstructor().newInstance().getLoyaltyTransactionsForUser (   ((User)session.getAttribute("SESS_USER")).getRelationshipNo() ));

					ArrWallets = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
					request.setAttribute("arrWallets",ArrWallets );
					request.setAttribute("lastaction", "rwd");	
					request.setAttribute("lastrule", Rules.CUST_CLAIM_REWARDS_PAGE);
					response.setContentType("text/html");
					
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerLoyaltyClaimPage()).forward(request, response);

					}finally {
						if(relationshipNo!=null) relationshipNo=null; if(accruedWalletId!=null) accruedWalletId=null; if(pointAccrued!=null) pointAccrued=null;
						if(payMode!=null) payMode=null; if(walletBalance!=null) walletBalance=null; if(walletId!=null) walletId=null; if(ArrWallets!=null) ArrWallets=null; 
						
					}
					
					

			 }catch (Exception e){
					callException(request, response, ctx, session,e, e.getMessage());

			 }
		
			 
			 break;
			 
			 
			 
			 
			case Rules.CUST_DISPLAY_SELECTED_WALLETPOINTS:
				try {

					String langType = null;	String WallPointsBalance; String relationshipNo = null; 
			         String selectedwalletId = null;
					
				if(request.getParameter("selectedwalletid")!=null) 		 selectedwalletId = StringUtils.trim(request.getParameter("selectedwalletid"));
				PPWalletEnvironment.setComment(3,className,"selectedwalletid is  "  + selectedwalletId  );
				//relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
				WallPointsBalance = (String)LoyaltyDao.class.getConstructor().newInstance().getLoyaltyBalanceForSelectedWallet(selectedwalletId  );
				   PPWalletEnvironment.setComment(3,className,"point balance  for wallet" + selectedwalletId + "is  "  + WallPointsBalance  );

					request.setAttribute("lastaction", "rwd");	
					request.setAttribute("lastrule", CUST_CLAIM_EACH_REWARD);
					request.setAttribute("Walletpointbalance",WallPointsBalance );

					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerLoyaltyClaimPage()).forward(request, response);
					} finally {
						if(selectedwalletId!=null) selectedwalletId=null; 
					}
				}catch(Exception e) {
					callException(request, response, ctx, session,e, e.getMessage());

				}
				break;
	
		}
		
	}

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		// TODO Auto-generated method stub
		switch (rulesaction)
		{
		
		case Rules.JSON_CUST_VIEW_REWARDS_RULES:
			
			try {
				 String privateKey = null;
				PrintWriter jsonOutput_1 = null;	boolean allow =true ; 	ArrayList<LoyaltyRules> arrLoyaltyRules = null;
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_VIEW_REWARDS_RULES: pvt key is incorrect "+privateKey);
				}

				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				arrLoyaltyRules = LoyaltyDao.class.getConstructor().newInstance().getLoyaltyRules();
				
				if(arrLoyaltyRules!=null){
					int count = arrLoyaltyRules.size();
					String[] paymodeArray = new String [count];
					String[] ruleDescArray = new String [count];
					String[] pointsConversionArray = new String [count];
					String[] cryptoConversionArray = new String [count];
					 // put some value pairs into the JSON object .
					 for(int i=0;i<arrLoyaltyRules.size();i++){
						 paymodeArray[i]= ((LoyaltyRules)arrLoyaltyRules.get(i)).getPayMode();
						 ruleDescArray[i]= ((LoyaltyRules)arrLoyaltyRules.get(i)).getRuleDesc();
						 pointsConversionArray[i]= ((LoyaltyRules)arrLoyaltyRules.get(i)).getPointsConvertRatio();
						 cryptoConversionArray[i]= ((LoyaltyRules)arrLoyaltyRules.get(i)).getCryptoConvertRatio();
						 
					 }
					 
					 obj.add("paymode", gson.toJsonTree(paymodeArray));
					  obj.add("rulesdesc", gson.toJsonTree(ruleDescArray));
					  obj.add("pointsconversion", gson.toJsonTree(pointsConversionArray));
					  obj.add("cryptoconversion", gson.toJsonTree(cryptoConversionArray));
					  obj.add("error", gson.toJsonTree("false"));
					 
				}
				
				else {
					obj.add("error", gson.toJsonTree("true"));
				}
	
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					//close all objects here
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(arrLoyaltyRules!=null) arrLoyaltyRules =null; 
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
					
					
				}
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_VIEW_REWARDS_RULES "+e.getMessage());
			}
			
			break;
			
		case Rules.JSON_CUST_VIEW_REWARDS_PAGE:
			try {
				 String privateKey = null; String relationshipNo= null; ArrayList<Loyalty> arrloyaltyTxn= null;
					PrintWriter jsonOutput_1 = null;	boolean allow = true; 	ArrayList<LoyaltyRules> arrLoyaltyRules = null; ArrayList<Wallet>  arrWallet = null;
					
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_VIEW_REWARDS_PAGE: pvt key is incorrect "+privateKey);
					}

					
					jsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Json Object
					
					if (allow) {
						arrloyaltyTxn = LoyaltyDao.class.getConstructor().newInstance().getLoyaltyTransactionsForUser(relationshipNo);
						arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( relationshipNo);	
						PPWalletEnvironment.setComment(3, className, " arrloyaltyTxn "+arrloyaltyTxn.size());
						PPWalletEnvironment.setComment(3, className, " arrloyaltyTxn "+arrloyaltyTxn.size());
						if(arrloyaltyTxn!=null && arrWallet!=null){
							int count = arrloyaltyTxn.size();
							int walCount = arrWallet.size();
							String[] walletIdArray = new String [count];
							String[] payModeArray = new String [count];
							String[] txnReferenceArray = new String [count];
							String[] pointsAccruedArray = new String [count];
							String[] pointsBalanceArray = new String [count];
							String[] txnDateTimeArray = new String [count];
							String[] sequenceNumberArray = new String [count];
							String[] statusArray = new String [count];
							
							String[] walletId= new String [walCount];
							String[] walletAmountArray= new String [walCount];
							String[] walletDescArray= new String [walCount];
							
							 // put some value pairs into the JSON object .
							 for(int i=0;i<arrloyaltyTxn.size();i++){
								 walletIdArray[i]= ((Loyalty)arrloyaltyTxn.get(i)).getWalletId();
								 payModeArray[i]= ((Loyalty)arrloyaltyTxn.get(i)).getPayMode();
								 txnReferenceArray[i]= ((Loyalty)arrloyaltyTxn.get(i)).getTxnReference();
								 pointsAccruedArray[i]= ((Loyalty)arrloyaltyTxn.get(i)).getPointAccrued();
								 pointsBalanceArray[i]= ((Loyalty)arrloyaltyTxn.get(i)).getPointBalance();
								 txnDateTimeArray[i]= ((Loyalty)arrloyaltyTxn.get(i)).getTxnDate(); 
								 sequenceNumberArray[i]= ((Loyalty)arrloyaltyTxn.get(i)).getSequenceId();
								 statusArray[i]= ((Loyalty)arrloyaltyTxn.get(i)).getStatus();
							 }
							 
							 for(int i=0;i<arrWallet.size();i++){
								 walletId[i]= ((Wallet)arrWallet.get(i)).getWalletId();
								 walletAmountArray[i]= ((Wallet)arrWallet.get(i)).getCurrentBalance();
								 walletDescArray[i]= ((Wallet)arrWallet.get(i)).getWalletDesc();
							 }
							 
							 
							 	obj.add("walletid", gson.toJsonTree(walletIdArray));
							  obj.add("paymode", gson.toJsonTree(payModeArray));
							  obj.add("txnreference", gson.toJsonTree(txnReferenceArray));
							  obj.add("pointaccrued", gson.toJsonTree(pointsAccruedArray));
							  obj.add("pointbalance", gson.toJsonTree(pointsBalanceArray));
							  obj.add("txndatetime", gson.toJsonTree(txnDateTimeArray));
							  obj.add("sequenceid", gson.toJsonTree(sequenceNumberArray));
							  obj.add("status", gson.toJsonTree(statusArray));
							  obj.add("walid", gson.toJsonTree(walletId));
							  obj.add("currbal", gson.toJsonTree(walletAmountArray));
							  obj.add("walletdesc", gson.toJsonTree(walletDescArray));
							  obj.add("error", gson.toJsonTree("false"));
						}else {
							obj.add("error", gson.toJsonTree("true"));
						}
						
						
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					try {
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
						//close all objects here
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(arrLoyaltyRules!=null) arrLoyaltyRules =null; 
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;	
					}
					
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_VIEW_REWARDS_PAGE "+e.getMessage());
			}
			break;
			
			
		case Rules.JSON_CUST_CLAIM_EACH_REWARD:
			try {

				boolean success =false;  String sequenceId = null;   String userId=null; String userType="C";
		         String walletId = null; String pointAccrued = null;	String payMode = null; 
		         String walletBalance = null; String privateKey=null; String relationshipNo= null; PrintWriter jsonOutput_1 = null;	boolean allow = true;
		         PPWalletEnvironment.setComment(3, className, "claiming ");
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");
				if(jsonObj.get("pointsaccrued")!=null) pointAccrued = jsonObj.get("pointsaccrued").toString().replaceAll("\"", "");
				if(jsonObj.get("walletid")!=null) walletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
				if(jsonObj.get("paymode")!=null) payMode = jsonObj.get("paymode").toString().replaceAll("\"", "");
				if(jsonObj.get("walbalance")!=null) walletBalance = jsonObj.get("walbalance").toString().replaceAll("\"", "");
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("sequenceid")!=null) sequenceId = jsonObj.get("sequenceid").toString().replaceAll("\"", "");

				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from CUST_CLAIM_EACH_REWARD: pvt key is incorrect "+privateKey);
				}

				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				
				 if (allow) {
					 success = (boolean)LoyaltyDao.class.getConstructor().newInstance().updateLoyaltyRewardsForAPointMobile(relationshipNo, sequenceId, walletId , pointAccrued, payMode, walletBalance);
					 
					 if (success) {
						 
						 SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId , userType, "C", StringUtils.substring(" This user claimed loyalty reward "+userId, 0, 48) );
						 
						 obj.add("error", gson.toJsonTree("false"));
						 
					 } else {
						 obj.add("error", gson.toJsonTree("true"));
					 }
					 
					 
				 }else {
					 obj.add("error", gson.toJsonTree("true"));
				 }
				 
				 try {
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				 } finally {
						//close all objects here
					
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(walletBalance!=null) walletBalance =null; if(userId!=null) userId =null; if(userType!=null) userType =null; 
					if(relationshipNo!=null) relationshipNo =null; //if(txnRef!=null) txnRef =null; 
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;	
				}
				
			
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_CLAIM_EACH_REWARD "+e.getMessage());
			}
			break;
			

		case Rules.JSON_CUST_CLAIM_ALL_REWARD:
		
		try {
			  String walletId = null; String privateKey=null; String relationshipNo=null; String payMode=null;String walletBalance=null;
			  PrintWriter jsonOutput_1 = null;	boolean allow = true; String userId=null; String userType="C";boolean success= true; 
			  
			if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
			if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");
			if(jsonObj.get("walletid")!=null) walletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
			if(jsonObj.get("walletbalance")!=null) walletBalance = jsonObj.get("walletbalance").toString().replaceAll("\"", ""); 
			if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", ""); 
			
			payMode=PPWalletEnvironment.getCodeLoyaltyRedeemRate();
			
			if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
				allow = false;
				PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_CLAIM_ALL_REWARD: pvt key is incorrect "+privateKey);
			}
			PPWalletEnvironment.setComment(3, className, "RELNO "+relationshipNo);
			
			jsonOutput_1 = response.getWriter();
			Gson gson = new Gson();
			JsonObject obj = new JsonObject(); //Json Object
			
			if (allow) {
				success = (boolean)LoyaltyDao.class.getConstructor().newInstance().updateLoyaltyRewardsForAllPoint(relationshipNo, walletId , walletBalance, payMode);
				if (success) {	 
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId , userType, "C", StringUtils.substring(" This user claimed all loyalty for wallet"+walletId, 0, 48) );
					 
					obj.add("error", gson.toJsonTree("false"));
					 
				 } else {
					 obj.add("error", gson.toJsonTree("true"));
				 }
				 
				 
			 }else {
				 obj.add("error", gson.toJsonTree("true"));
			 }
			 try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
				jsonOutput_1.print(gson.toJson(obj));
			 } finally {
					//close all objects here
			
			  
				if(jsonOutput_1!=null) jsonOutput_1.close(); if(walletBalance!=null) walletBalance =null; if(userId!=null) userId =null; if(userType!=null) userType =null; 
				if(relationshipNo!=null) relationshipNo =null; if(walletId!=null) walletId =null; 
				if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;	
			}
		
		}catch(Exception e) {
			PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_CLAIM_ALL_REWARD "+e.getMessage());
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

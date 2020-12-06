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
import com.ppwallet.dao.MerchBillPaymentDao;
import com.ppwallet.dao.MerchantDao;
import com.ppwallet.dao.MerchPaymentsDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.model.MerchClientDetails;
import com.ppwallet.model.MerchCashoutTransactions;
import com.ppwallet.model.Disputes;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.MerchRetailPayTransactions;
import com.ppwallet.model.SystemMsfPlans;
import com.ppwallet.model.MerchTopUpTransactions;
import com.ppwallet.utilities.Utilities;

public class MerchMsfPlanRulesImp implements Rules {
	private static String className = MerchMsfPlanRulesImp.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		switch (rulesaction){
			case Rules.JSON_MERCHANT_MOBILE_VIEW_MSF_PLAN:
				
				try {
					
					String userId = null; String privateKey = null; 
					PrintWriter jsonOutput_1 = null; boolean result = false; String billerCode = null;  
					 ArrayList<SystemMsfPlans> arrMerchMsfPlan = null;
					
					if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						result = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_VIEW_MSF_PLAN: pvt key is incorrect "+privateKey);
					}
						
				    Gson gson = new Gson();
					JsonObject obj = new JsonObject();
					jsonOutput_1 = response.getWriter();
					arrMerchMsfPlan= MerchantDao.class.getConstructor().newInstance().getMobileMerchantMsfPlan(billerCode);	
					if(arrMerchMsfPlan!=null){
						int count = arrMerchMsfPlan.size();
						
						String[] planIdArray = new String [count];	
						String[] planNameArray = new String [count];
						String[] planFeeFixedArray = new String [count];
						String[] planFeeVarArray = new String [count];
						String[] planTypeArray = new String [count];
						String[] depositArray = new String [count];
						String[] setupFeeArray = new String [count];
						String[] monthlyFeeArray = new String [count];
						String[] annualFeeArray = new String [count];
						String[] statementFeeArray = new String [count];
						String[] latePaymentFeeArray = new String [count];
						String[] planCycleArray = new String [count];
						String[] statusArray = new String [count];
						String[] createdOnArray = new String [count];
					
						
						 // put some value pairs into the JSON object .
						  for(int i=0;i<arrMerchMsfPlan.size();i++){
//							  merchIdArray[i]= ((Customer)vecCustomer.get(i)).getMerchantId();
							  planIdArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanId();
							  planNameArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanName();
							  planFeeFixedArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanFeeFixed();
							  planFeeVarArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanFeeVar();
							  planTypeArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanType();
							  depositArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanDepositFee();
							  setupFeeArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanSetUpFee();
							  monthlyFeeArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanMonthlyFee();
							  annualFeeArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanAnnualFee();
							  statementFeeArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanStatementFee();
							  latePaymentFeeArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanLatePaymentFee();
							  planCycleArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getPlanCycle();
							  statusArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getStatus();
							  createdOnArray[i]= ((SystemMsfPlans)arrMerchMsfPlan.get(i)).getStatus();
							   
						  }

						  obj.add("planid", gson.toJsonTree(planIdArray));  
						  obj.add("planname", gson.toJsonTree(planNameArray));  
						  obj.add("planfeefixed", gson.toJsonTree(planFeeFixedArray));  
						  obj.add("planfeevar", gson.toJsonTree(planFeeVarArray));  
						  obj.add("plantype", gson.toJsonTree(planTypeArray));  
						  obj.add("deposit", gson.toJsonTree(depositArray));  
						  obj.add("setupfee", gson.toJsonTree(setupFeeArray));  
						  obj.add("monthlyfee", gson.toJsonTree(monthlyFeeArray));  
						  obj.add("annualfee", gson.toJsonTree(annualFeeArray));  
						  obj.add("statementfee", gson.toJsonTree(statementFeeArray));  
						  obj.add("latepaymentfee", gson.toJsonTree(latePaymentFeeArray));  
						  obj.add("plancycle", gson.toJsonTree(planCycleArray));  
						  obj.add("status", gson.toJsonTree(statusArray));  
						  obj.add("createdon", gson.toJsonTree(createdOnArray));  
						  obj.add("error", gson.toJsonTree("false"));
						
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					try {
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(billerCode!=null) billerCode=null;if(arrMerchMsfPlan!=null) arrMerchMsfPlan=null;
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
		
					}
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_VIEW_MSF_PLAN "+e.getMessage());
				}
			break;
			
			
         case Rules.JSON_MERCHANT_MOBILE_VIEW_TRANSACTIONS:
				
				try {
					
					String userId = null; String privateKey = null; 
					PrintWriter jsonOutput_1 = null; boolean result = false; String billerCode = null;  
					ArrayList<MerchCashoutTransactions> arrCashoutTxn = null;  ArrayList<MerchTopUpTransactions> arrTopupTxn = null;
					ArrayList<MerchRetailPayTransactions> arrRetailPayTxn = null;
					
					if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						result = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_VIEW_TRANSACTIONS: pvt key is incorrect "+privateKey);
					}
						
				    Gson gson = new Gson();
					JsonObject obj = new JsonObject();
					jsonOutput_1 = response.getWriter();
					arrCashoutTxn= MerchPaymentsDao.class.getConstructor().newInstance().getMerchCashoutTransactions(billerCode);	
					arrTopupTxn= MerchPaymentsDao.class.getConstructor().newInstance().getMerchTopUpTransactions(billerCode);	
					arrRetailPayTxn= MerchPaymentsDao.class.getConstructor().newInstance().getMerchRetailPayTransactions(billerCode);	

			
					//Topup Transaction
					if(arrTopupTxn!=null){
						int count = arrTopupTxn.size();
						
						String[] txnCodeArray = new String [count];	
						String[] txnAmountArray = new String [count];
						String[] txnDateTimeArray = new String [count];
						
						 // put some value pairs into the JSON object .
						  for(int i=0;i<arrTopupTxn.size();i++){
//							  merchIdArray[i]= ((Customer)vecCustomer.get(i)).getMerchantId();
							  txnCodeArray[i]= ((MerchTopUpTransactions)arrTopupTxn.get(i)).getTxnCode();
							  txnAmountArray[i]= ((MerchTopUpTransactions)arrTopupTxn.get(i)).getTxnAmount();
							  txnDateTimeArray[i]= Utilities.getMySQLDateTimeConvertor(((MerchTopUpTransactions)arrTopupTxn.get(i)).getTxndatetime());
							   
						  }

						  obj.add("topuptranscode", gson.toJsonTree(txnCodeArray));  
						  obj.add("topuptransamount", gson.toJsonTree(txnAmountArray));  
						  obj.add("topuptransdate", gson.toJsonTree(txnDateTimeArray));  
						  obj.add("error", gson.toJsonTree("false"));
						
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					//cashout Transactions
					if(arrCashoutTxn!=null){
						int count = arrCashoutTxn.size();
						
						String[] txnCodeArray = new String [count];	
						String[] txnAmountArray = new String [count];
						String[] txnDateTimeArray = new String [count];
						
						 // put some value pairs into the JSON object .
						  for(int i=0;i<arrCashoutTxn.size();i++){
//							  merchIdArray[i]= ((Customer)vecCustomer.get(i)).getMerchantId();
							  txnCodeArray[i]= ((MerchCashoutTransactions)arrCashoutTxn.get(i)).getTxnCode();
							  txnAmountArray[i]= ((MerchCashoutTransactions)arrCashoutTxn.get(i)).getTxnAmount();
							  txnDateTimeArray[i]= Utilities.getMySQLDateTimeConvertor(((MerchCashoutTransactions)arrCashoutTxn.get(i)).getTxndatetime());
							   
						  }

						  obj.add("cashouttranscode", gson.toJsonTree(txnCodeArray));  
						  obj.add("cashouttransamount", gson.toJsonTree(txnAmountArray));  
						  obj.add("cashouttransdate", gson.toJsonTree(txnDateTimeArray));  
						  obj.add("error", gson.toJsonTree("false"));
						
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					//RetailPay Transaction
					if(arrRetailPayTxn!=null){
						int count = arrRetailPayTxn.size();
						
						String[] txnCodeArray = new String [count];	
						String[] txnAmountArray = new String [count];
						String[] txnDateTimeArray = new String [count];
						
						 // put some value pairs into the JSON object .
						  for(int i=0;i<arrRetailPayTxn.size();i++){
//							  merchIdArray[i]= ((Customer)vecCustomer.get(i)).getMerchantId();
							  txnCodeArray[i]= ((MerchRetailPayTransactions)arrRetailPayTxn.get(i)).getTxnCode();
							  txnAmountArray[i]= ((MerchRetailPayTransactions)arrRetailPayTxn.get(i)).getTxnAmount();
							  txnDateTimeArray[i]= ((MerchRetailPayTransactions)arrRetailPayTxn.get(i)).getTxnDateTime();
							   
						  }
						  obj.add("retailpaytranscode", gson.toJsonTree(txnCodeArray));  
						  obj.add("retailpaytopuptransamount", gson.toJsonTree(txnAmountArray));  
						  obj.add("retailpaytopuptransdate", gson.toJsonTree(txnDateTimeArray));  
						  obj.add("error", gson.toJsonTree("false"));
						
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					try {
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(billerCode!=null) billerCode=null;if(arrCashoutTxn!=null) arrCashoutTxn=null;
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(arrTopupTxn!=null) arrTopupTxn=null; if(arrRetailPayTxn!=null) arrRetailPayTxn=null;
		
					}
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_VIEW_TRANSACTIONS "+e.getMessage());
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

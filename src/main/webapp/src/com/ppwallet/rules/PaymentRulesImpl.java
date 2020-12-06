package com.ppwallet.rules;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.BillPayDao;
import com.ppwallet.dao.LoyaltyDao;
import com.ppwallet.dao.PaymentDao;
import com.ppwallet.dao.RetailPayDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.dao.WalletDao;
import com.ppwallet.model.BillPayTransaction;
import com.ppwallet.model.BillerDetail;
import com.ppwallet.model.CardDetails;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.RetailPayTransaction;
import com.ppwallet.model.User;
import com.ppwallet.model.Wallet;
import com.ppwallet.utilities.Utilities;

public class PaymentRulesImpl implements Rules {
	private static String className = PaymentRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {

		HttpSession session	= request.getSession(false);
		switch (rules){
		case Rules.PAYMENT_BILL_PAYPAGE:
			try {	
				
				// get the biller code and CRN of the customer. If null, allow the data
				request.setAttribute("registeredbillers", (ArrayList<BillerDetail>)BillPayDao.class.getConstructor().newInstance().getRegisteredBillerDetailsActive( ((User)session.getAttribute("SESS_USER")).getRelationshipNo()  ));
				
				//request.setAttribute("regbillerdetails", (ArrayList<Merchant>)BillPayDao.class.getConstructor().newInstance().getBillerDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipno()  ));
		
				request.setAttribute("langPref", "en");	
				request.setAttribute("lastaction", "pmt");	
				request.setAttribute("lastrule", PAYMENT_BILL_PAYPAGE);
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerPayBillsPage()).forward(request, response);
				} finally {
					
				}
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
		break;	
	
		case Rules.PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE:
			try {
				PPWalletEnvironment.setComment(3,className," in PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE: "   );

				boolean success = false;  ArrayList<Merchant> arrallMerchants = null;		
				ArrayList<BillerDetail> arr_registeredBillers = null;  HashSet<Merchant> hash_remainingMerchants = null;	
				HashSet<String> hash_BillerCode = null;
				
				
				arrallMerchants = (ArrayList<Merchant>)BillPayDao.class.getConstructor().newInstance().getRegisteredActiveMerchants();

				arr_registeredBillers = (ArrayList<BillerDetail>)BillPayDao.class.getConstructor().newInstance().getRegisteredBillerDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
				PPWalletEnvironment.setComment(3,className," arrallMerchants: "+arrallMerchants.size()    );

				// Check if there is no registered billers
					if(arr_registeredBillers!=null){
						hash_BillerCode = new HashSet<String>();
						for (int i=0; i< arr_registeredBillers.size();i++){
							hash_BillerCode.add( ((BillerDetail)arr_registeredBillers.get(i)).getBillerCode());
						}
					}else{
						hash_remainingMerchants = new HashSet<Merchant>();
						for (int i=0; i< arrallMerchants.size();i++){
							hash_remainingMerchants.add( (Merchant)arrallMerchants.get(i));
						}
					}
					// do the following if the registered biller code exists
					if(hash_BillerCode!=null){
						hash_remainingMerchants = new HashSet<Merchant>();
						for (int i=0; i< arrallMerchants.size();i++){
							if( hash_BillerCode.contains( (String)((Merchant)arrallMerchants.get(i)).getBillerCode() ) ==false  ) {
								hash_remainingMerchants.add( (Merchant)arrallMerchants.get(i));
							}		
						}
					}
//				if(hash_remainingMerchants!=null)if(hash_remainingMerchants.isEmpty())	hash_remainingMerchants=null;
				
				request.setAttribute("registeredmerchants", hash_remainingMerchants);
				request.setAttribute("langPref", "en");	
				request.setAttribute("lastaction", "pmt");	
				request.setAttribute("lastrule", Rules.PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE);
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerRegisterBillerPage()).forward(request, response);

				}finally {
					if(arrallMerchants!=null) arrallMerchants = null; if(arr_registeredBillers!=null) arr_registeredBillers = null; 
					if(hash_remainingMerchants!=null) hash_remainingMerchants = null; if(hash_BillerCode!=null) hash_BillerCode = null;
				}
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
		break;
		
		case Rules.PAYMENT_BILLPAY_BILLER_NEWREGISTER_PAGE:
			try {
				String langType = null;		String currencyId = "404";	String billerCode = null;
				boolean success = false;	String referenceNo = null;
				if(request.getParameter("hdnlang")!=null) 					langType = StringUtils.trim(request.getParameter("hdnlang"));
				if(request.getParameter("billercode")!=null) 				billerCode = StringUtils.trim(request.getParameter("billercode"));
				
				// Generate the reference no
				referenceNo = ( RandomStringUtils.random(16, false, true)).toString();
				// Now register the Biller details
				success = (boolean)BillPayDao.class.getConstructor().newInstance().registerCustomerBiller(((User)session.getAttribute("SESS_USER")).getRelationshipNo(), currencyId, billerCode, referenceNo );
				if(success == false)	throw new Exception ("Insert failed for the registration of the Biller for customer ");
				// Now display the main biller pages
				request.setAttribute("registeredbillers", (ArrayList<BillerDetail>)BillPayDao.class.getConstructor().newInstance().getRegisteredBillerDetails(((User)session.getAttribute("SESS_USER")).getRelationshipNo() ));
				request.setAttribute("langPref", langType.toLowerCase());	
				request.setAttribute("lastaction", "pmt");	
				request.setAttribute("lastrule", PAYMENT_BILLPAY_BILLER_NEWREGISTER_PAGE);
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerPayBillsPage()).forward(request, response);
				} finally {
					if(currencyId!=null) currencyId=null; if(billerCode!=null) billerCode=null; if(referenceNo!=null) referenceNo=null; 
				}
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
		break;	
		
		case Rules.PAYMENT_BILLPAY_BILLPAYRREQ:
			try {
				String langType = null;		String currencyId = "404";	String billerCode = null; String billAmount = null;  String billDescription = null;
				boolean success = false;	String referenceNo = null;	String billpaymode = null;	String merchCompanyName = null; 
				String relationshipNo = null;
				
				if(request.getParameter("hdnlang")!=null) 				langType = StringUtils.trim(request.getParameter("hdnlang"));
				if(request.getParameter("billercode")!=null) 		billerCode = StringUtils.trim(request.getParameter("billercode"));
			//	if(request.getParameter("hdnpaymode")!=null) 			billpaymode = StringUtils.trim(request.getParameter("hdnpaymode"));
				if(request.getParameter("billamount")!=null) 			billAmount = StringUtils.trim(request.getParameter("billamount"));
				if(request.getParameter("billdescription")!=null) 		billDescription = StringUtils.trim(request.getParameter("billdescription"));
				if(request.getParameter("hdnmerchcompany")!=null) 			merchCompanyName = StringUtils.trim(request.getParameter("hdnmerchcompany"));
				if(request.getParameter("custrefno")!=null) 			referenceNo = StringUtils.trim(request.getParameter("custrefno"));	
				if(request.getParameter("hdnmode")!=null) 			billpaymode = StringUtils.trim(request.getParameter("hdnmode"));	
				relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
				PPWalletEnvironment.setComment(3,className," amount is " + billAmount +"||"+billpaymode +"||"+merchCompanyName+"||"+billerCode +"||"+referenceNo +"||"+billDescription+"||"+billpaymode);

				if(billpaymode.equals("W")) {
					// get wallet details
					request.setAttribute("walletdetails", (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails(((User)session.getAttribute("SESS_USER")).getRelationshipNo() ));
			   
				}else if(billpaymode.equals("C")) {
					request.setAttribute("carddetails",  (ArrayList<CardDetails>)WalletDao.class.getConstructor().newInstance().getCardDetails(((User)session.getAttribute("SESS_USER")).getRelationshipNo() ));
				}
				request.setAttribute("billercode", billerCode);				request.setAttribute("paymentmode", billpaymode);
				request.setAttribute("paymode", billpaymode);				request.setAttribute("paymentmode", billpaymode);
				request.setAttribute("currencyid", currencyId);				request.setAttribute("billercode", billerCode);
				request.setAttribute("billamount", billAmount);				request.setAttribute("billdesc", billDescription);
				request.setAttribute("companyname", merchCompanyName);		request.setAttribute("referenceno", referenceNo);
				// Now display the main biller pages
				request.setAttribute("langPref", langType.toLowerCase());	
				request.setAttribute("lastaction", "pmt");	
				request.setAttribute("lastrule", PAYMENT_BILLPAY_BILLPAYRREQ); 
				response.setContentType("text/html");
				try {
					if(billpaymode.equals("W")) {
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerBillerPayConfirmPage()).forward(request, response);
					}else if(billpaymode.equals("C")) {
						PPWalletEnvironment.setComment(3,className, "billpaymode "+billpaymode);
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerBillerPayCardConfirmPage()).forward(request, response);
					}
					
				} finally {
					if(currencyId!=null) currencyId=null; if(billAmount!=null) billAmount=null; if(billDescription!=null) billDescription=null;
					if(referenceNo!=null) referenceNo=null; if(billpaymode!=null) billpaymode=null; if(merchCompanyName!=null) merchCompanyName=null;
					if(billerCode!=null) billerCode=null;  
				}
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
		break;
	
		case Rules.PAYMENT_BILLPAY_REQCONFIRM:
			try {
				
				String langType = null;		String currencyId = null;	String billerCode = null; String billAmount = null;  String billDescription = null;
				boolean result = false;	String referenceNo = null;	String billpaymode = null;	String merchCompanyName = null; String walletId = null;
				String relationshipNo = null; String usertype = "C";
				if(request.getParameter("hdnlang")!=null) 					langType = StringUtils.trim(request.getParameter("hdnlang"));
				if(request.getParameter("currencyid")!=null) 				currencyId = StringUtils.trim(request.getParameter("currencyid"));
				if(request.getParameter("billercode")!=null) 				billerCode = StringUtils.trim(request.getParameter("billercode"));
				if(request.getParameter("hdnpaymode")!=null) 				billpaymode = StringUtils.trim(request.getParameter("hdnpaymode"));
				if(request.getParameter("billamount")!=null) 				billAmount = StringUtils.trim(request.getParameter("billamount"));
				if(request.getParameter("billdesc")!=null) 					billDescription = StringUtils.trim(request.getParameter("billdesc"));
				if(request.getParameter("hdnmerchcompany")!=null) 			merchCompanyName = StringUtils.trim(request.getParameter("hdnmerchcompany"));
				if(request.getParameter("custrefno")!=null) 				referenceNo = StringUtils.trim(request.getParameter("custrefno"));
				
				if(request.getParameter("hdnwalletid")!=null) 				walletId = StringUtils.trim(request.getParameter("hdnwalletid"));
				
				relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
				if(billpaymode.equals("W")) {  //wallet
					// pay bill using wallet
					//request.setAttribute("walletdetails", (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails(((User)session.getAttribute("SESS_USER")).getUserId()));
					// TODO Here the system reference of the wallet should be the transaction code of the billpay system, i.e. generate txncode in billpay table and refer it to the wallet_table sysreference
						if(Float.parseFloat(billAmount) > 0) {
							result = (boolean)BillPayDao.class.getConstructor().newInstance().payBillWithWallet(relationshipNo, billpaymode, walletId, billerCode, referenceNo, currencyId, billAmount, billDescription );
							if(!result) throw new Exception(" Payment failed for wallet");	
						}else {
							PPWalletEnvironment.setComment(3,className," Bill pay amount Must be more than  0" );
						}	
					}else  if(billpaymode.equals("A"))  {   //account
					//Login here we dont have the option for now	
				}	
				//get the all bill pay  transactions
				request.setAttribute("billpaytxns", (ArrayList<BillPayTransaction>)BillPayDao.class.getConstructor().newInstance().getBillPayTransactionsForUser(relationshipNo));
				// Now display the main biller pages
				request.setAttribute("langPref", langType.toLowerCase());	
				request.setAttribute("lastaction", "pmt");	//request.setAttribute("lastrule", "Pay Bills"); // check what is the best rule value
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerBillPayTransactionsPage()).forward(request, response);
				} finally {
					if(currencyId!=null) currencyId=null; if(billAmount!=null) billAmount=null; if(billDescription!=null) billDescription=null;
					if(referenceNo!=null) referenceNo=null; if(billpaymode!=null) billpaymode=null; if(merchCompanyName!=null) merchCompanyName=null;
					if(walletId!=null) walletId=null;  if(relationshipNo!=null) relationshipNo=null;
				}
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
		break;	
		
		case Rules.PAYBILL_WITHCARD:
			
			try {
				
				String langType = null;		String currencyId = null;	String billerCode = null; String billAmount = null;  String billDescription = null;
				boolean result = false;	String referenceNo = null;	String billpaymode = null;	String merchCompanyName = null; String tokenId = null;
				String relationshipNo = null; String usertype = "C"; String cardAlias = null; String cardDateOfExpiry = null; String cvv2 = null;
				if(request.getParameter("hdnlang")!=null) 					langType = StringUtils.trim(request.getParameter("hdnlang"));
				if(request.getParameter("currencyid")!=null) 				currencyId = StringUtils.trim(request.getParameter("currencyid"));
				if(request.getParameter("billercode")!=null) 				billerCode = StringUtils.trim(request.getParameter("billercode"));
				if(request.getParameter("hdnpaymode")!=null) 				billpaymode = StringUtils.trim(request.getParameter("hdnpaymode"));
				if(request.getParameter("billamount")!=null) 				billAmount = StringUtils.trim(request.getParameter("billamount"));
				if(request.getParameter("billdesc")!=null) 					billDescription = StringUtils.trim(request.getParameter("billdesc"));
				if(request.getParameter("hdnmerchcompany")!=null) 			merchCompanyName = StringUtils.trim(request.getParameter("hdnmerchcompany"));
				if(request.getParameter("custrefno")!=null) 				referenceNo = StringUtils.trim(request.getParameter("custrefno"));
				if(request.getParameter("hdncardid")!=null) 				tokenId = StringUtils.trim(request.getParameter("hdncardid"));
				if(request.getParameter("cvv2")!=null) 				cvv2 = StringUtils.trim(request.getParameter("cvv2"));
				
				relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
				  
					
				if(Float.parseFloat(billAmount) > 0) {
					result = (boolean)BillPayDao.class.getConstructor().newInstance().payBillWithToken(relationshipNo, billpaymode, billerCode, referenceNo, currencyId, billAmount, billDescription, tokenId, cardAlias, cardDateOfExpiry, cvv2);
					if(!result) throw new Exception(" Payment failed for wallet");	
				}else {
					PPWalletEnvironment.setComment(3,className," Bill pay amount Must be more than  0" );
				}	
					
				//get the all bill pay  transactions
				request.setAttribute("billpaytxns", (ArrayList<BillPayTransaction>)BillPayDao.class.getConstructor().newInstance().getBillPayTransactionsForUser(relationshipNo));
				// Now display the main biller pages
				request.setAttribute("langPref", langType.toLowerCase());	
				request.setAttribute("lastaction", "pmt");	///cvv2 request.setAttribute("lastrule", "Pay Bills"); // check what is the best rule value
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerBillPayTransactionsPage()).forward(request, response);
				} finally {
					if(currencyId!=null) currencyId=null; if(billAmount!=null) billAmount=null; if(billDescription!=null) billDescription=null;
					if(referenceNo!=null) referenceNo=null; if(billpaymode!=null) billpaymode=null; if(merchCompanyName!=null) merchCompanyName=null;
					if(tokenId!=null) tokenId=null;  if(relationshipNo!=null) relationshipNo=null; if(cvv2!=null) cvv2=null;
				}
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
		break;	
		
		
		case Rules.CARD_REGISTRATION_PAGE:
			
			try {
								
				request.setAttribute("lastaction", "pmt");	request.setAttribute("lastrule", "Register Cards");
				response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerRegisterCardPage()).forward(request, response);
			
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
		break;
//		PAYMENT_REGISTER_NEW_CARD_REQ
		case Rules.PAYMENT_REGISTER_NEW_CARD_REQ:
			try {
				try {
					boolean success = false;
					String cardNumber = null;  String cardName = null;  String cardCVV2 = null;		String dateOfExpiry = null;
					String cardAlias = null;	String tokenId = null;	String tokenType = null; String userType = "C"; String relationshipNo = null;
					
					
					if(request.getParameter("number")!=null) 		cardNumber = StringUtils.trim(request.getParameter("number"));
					if(request.getParameter("name")!=null) 			cardName = StringUtils.trim(request.getParameter("name"));
					if(request.getParameter("cvc")!=null) 			cardCVV2 = StringUtils.trim(request.getParameter("cvc"));
					if(request.getParameter("expiry")!=null) 		dateOfExpiry = StringUtils.trim(request.getParameter("expiry"));
					if(request.getParameter("cardalias")!=null) 	cardAlias = StringUtils.trim(request.getParameter("cardalias"));
					relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					String referenceNoFromIPG = Utilities.genAlphaNumRandom(9); //  random number generated just to update the necessary tables
					String TRANSACTION_AMOUNT_FOR_REGISTRATION = "20";
					if(cardNumber==null && cardName==null) throw new Exception ("No card number and/or Card Name from the input");
						
						tokenId = Utilities.generateToken(cardNumber);	tokenType = "C";
						success = (boolean)PaymentDao.class.getConstructor().newInstance().recordAuthorizedPaymentViaCard(userType, cardName, cardNumber, dateOfExpiry, relationshipNo, referenceNoFromIPG, TRANSACTION_AMOUNT_FOR_REGISTRATION, tokenId, cardAlias);
					if(success == false) 	throw new Exception ("Problem with the card tokenization and  registration...");

					request.setAttribute("lastaction", "pmt");	//request.setAttribute("lastrule", "Register Cards"); the lastrule is not valid as this is an orphaned page
					request.setAttribute("cardlist",  (ArrayList<CardDetails>)WalletDao.class.getConstructor().newInstance().getCardDetails(((User)session.getAttribute("SESS_USER")).getRelationshipNo() ));
					try {
						response.setContentType("text/html");
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerPaymentAllTokensPage()).forward(request, response);
					}finally {
						if(cardNumber!=null) cardNumber=null; if(cardName!=null) cardName=null; if(cardCVV2!=null) cardCVV2=null;
						if(dateOfExpiry!=null) dateOfExpiry=null; if(cardAlias!=null) cardAlias=null; if(tokenId!=null) tokenId=null;
						if(relationshipNo!=null) relationshipNo=null; if(tokenType!=null) tokenType=null; if(userType!=null) userType=null;
					}
						
				}catch (Exception e) {
					callException(request, response, ctx, session,e, e.getMessage());
				}
				
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
			
			break;
			
			case Rules.PAYMENT_CARDS_SHOWALLCARDS_USER:
				
				try {
					
					ArrayList<CardDetails> arr_cards = (ArrayList<CardDetails>)WalletDao.class.getConstructor().newInstance().getCardDetails(((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
					request.setAttribute("cardlist", arr_cards);
					request.setAttribute("lastaction", "pmt");	request.setAttribute("lastrule", "Register Cards");// the lastrule is not valid as this is an orphaned page
					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerPaymentAllTokensPage()).forward(request, response);
					
				}catch (Exception e) {
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
		case JSON_PAYMENT_BILL_PAYPAGE:
			try {
				
				String relationshipNo = null; String privateKey = null; boolean allow = true; ArrayList<BillerDetail> arrBiller = null;
				PrintWriter jsonOutput_1 = null;
				if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				
				PPWalletEnvironment.setComment(2, className, " Relationship is "+ relationshipNo);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_VIEW_PROFILE: pvt key is incorrect "+privateKey);
				}
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				arrBiller=BillPayDao.class.getConstructor().newInstance().getRegisteredBillerDetailsActive(relationshipNo);
				
				if(arrBiller!=null&& allow) {
					int count = arrBiller.size();
					String [] companyNameArray = new String [count];
					String[] billerCodeArray = new String [count];	
					String[] custreferenceNoArray = new String [count];	
					
					 // put some value pairs into the JSON object .
					  for(int i=0;i<arrBiller.size();i++){
						  
						  companyNameArray [i] = ((BillerDetail)arrBiller.get(i)).getCompanyName();
						  billerCodeArray [i] = ((BillerDetail)arrBiller.get(i)).getBillerCode();
						  custreferenceNoArray [i] = ((BillerDetail)arrBiller.get(i)).getCustRefNo();
					  }
					  
					  obj.add("companyname", gson.toJsonTree(companyNameArray));
					  obj.add("billercode", gson.toJsonTree(billerCodeArray));
					  obj.add("custrefno", gson.toJsonTree(custreferenceNoArray));
					  obj.add("error", gson.toJsonTree("false"));
						
				}else {
					obj.add("error", gson.toJsonTree("true"));
					
					
					PPWalletEnvironment.setComment(2, className, " Array is null");
				}try {
					PPWalletEnvironment.setComment(3, className, " JSON_PAYMENT_BILL_PAYPAGE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
				//close all objects here
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(relationshipNo!=null) relationshipNo=null;if(arrBiller!=null) arrBiller=null;
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
					
				}

			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_BILL_PAYPAGE "+e.getMessage());
			}
		
			break;
			
			
		case JSON_PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE:
			
			try {
				String relationshipNo = null; String privateKey = null; boolean allow = true;
				ArrayList<Merchant> arrallMerchants = null;		ArrayList<BillerDetail> arr_registeredBillers = null;
				  HashSet<Merchant> hash_remainingMerchants = null;	HashSet<String> hash_BillerCode = null; PrintWriter jsonOutput_1 = null;
				  Iterator<Merchant> itMerchants = null;
				

				if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE: pvt key is incorrect "+privateKey);
				}
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				arrallMerchants = (ArrayList<Merchant>)BillPayDao.class.getConstructor().newInstance().getRegisteredActiveMerchants();
				arr_registeredBillers = (ArrayList<BillerDetail>)BillPayDao.class.getConstructor().newInstance().getRegisteredBillerDetails(relationshipNo);
				
				// Check if there is no registered billers
				if(arr_registeredBillers!=null&& allow){
					hash_BillerCode = new HashSet<String>();
					for (int i=0; i< arr_registeredBillers.size();i++){
						hash_BillerCode.add( ((BillerDetail)arr_registeredBillers.get(i)).getBillerCode());
					}
				}else{
					hash_remainingMerchants = new HashSet<Merchant>();
					for (int i=0; i< arrallMerchants.size();i++){
						hash_remainingMerchants.add( (Merchant)arrallMerchants.get(i));
					}
				}
				// do the following if the registered biller code exists
				if(hash_BillerCode!=null && allow){
					hash_remainingMerchants = new HashSet<Merchant>();
					for (int i=0; i< arrallMerchants.size();i++){
						if( hash_BillerCode.contains( (String)((Merchant)arrallMerchants.get(i)).getBillerCode() ) ==false  ) {
							hash_remainingMerchants.add( (Merchant)arrallMerchants.get(i));
						}
							
					}	
				}
	
			
			if(hash_remainingMerchants!=null && allow == true) {
				itMerchants = (Iterator<Merchant>)hash_remainingMerchants.iterator();
				PPWalletEnvironment.setComment(3, className, " HashSet size "+ hash_remainingMerchants.size() );

				int count = hash_remainingMerchants.size();
				String[] companyNameArray = new String [count];
				String[] billerCodeArray = new String [count];
				String[] companyRegNoArray = new String [count];
				String[] merchantCategoryArray = new String [count];
				
				int i = 0;
				while(itMerchants.hasNext()){
					
					Merchant tempMerchant = itMerchants.next();
					companyNameArray[i] = tempMerchant.getCompanyName();
					billerCodeArray[i] = tempMerchant.getBillerCode();
					companyRegNoArray[i] = tempMerchant.getCompanyRegistration();
					merchantCategoryArray[i] = tempMerchant.getMccCategoryName();
					
					PPWalletEnvironment.setComment(3, className, " Array Companyname+billercode+company "+companyNameArray[i]+billerCodeArray[i]);
					
					   i++;
					   
				}	
				

				  obj.add("companyname", gson.toJsonTree(companyNameArray)); 
				  obj.add("billercode", gson.toJsonTree(billerCodeArray)); 
				  obj.add("compregistration", gson.toJsonTree(companyRegNoArray)); 
				  obj.add("mcccategoryname", gson.toJsonTree(merchantCategoryArray)); 
				  obj.add("error", gson.toJsonTree("false"));

						
			}else {
				obj.add("error", gson.toJsonTree("true"));
			}
			
			try {
				PPWalletEnvironment.setComment(3, className, " JSON_PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE String is "+gson.toJson(obj));
				jsonOutput_1.print(gson.toJson(obj));
				
			} finally {
			//close all objects here
				if(jsonOutput_1!=null) jsonOutput_1.close(); if(relationshipNo!=null) relationshipNo=null;if(arrallMerchants!=null) arrallMerchants=null;
				if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(arr_registeredBillers!=null) arr_registeredBillers=null;
				if(hash_remainingMerchants!=null) hash_remainingMerchants=null;
			}
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE "+e.getMessage());
			}
			
			
			break;
			
			
			
			
			
			
		case JSON_PAYMENT_BILLPAY_BILLER_NEWREGISTER_PAGE:
			
			
			try {
					String privateKey = null; boolean allow = true; PrintWriter jsonOutput_1 = null; String billerCode=null; String relationshipNo=null;
					String currencyId = "404";	boolean success = false;	String referenceNo = null; 
			
					if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_BILLPAY_BILLER_NEWREGISTER_PAGE: pvt key is incorrect "+privateKey);
					}
					jsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Json Object
					
					// Generate the reference no
					referenceNo = ( RandomStringUtils.random(16, false, true)).toString();
					// Now register the Biller details
					success = (boolean)BillPayDao.class.getConstructor().newInstance().registerCustomerBiller(relationshipNo, currencyId, billerCode, referenceNo );
					
					if (success & allow) {
						
						obj.add("error", gson.toJsonTree("false"));
						
					}else {
						obj.add("error", gson.toJsonTree("true"));
						
						PPWalletEnvironment.setComment(3, currencyId, "Error in registering biller");
						
					}
					try {
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj)); 
					} finally {
					
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(billerCode!=null) billerCode=null;if(referenceNo!=null) referenceNo=null;
						if(currencyId!=null) currencyId=null;if(privateKey!=null) privateKey=null; if(gson!=null) gson=null;
					 
					}
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_BILLPAY_BILLER_NEWREGISTER_PAGE "+e.getMessage());
			}
		
			break;
			
			
			
			
			
		case JSON_PAYMENT_BILLPAY_BILLPAYRREQ:
			
			try {
				
				
				String currencyId = "404";	String billerCode = null; String billAmount = null;  String billDescription = null;	String relationshipNo= null; boolean allow= true;
				boolean success = false;	String referenceNo = null;	String billPayMode = null;	String merchCompanyName = null; String privateKey = null; PrintWriter jsonOutput_1 = null;
				ArrayList<Wallet> arywalletDetails = null;
				
				if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");
				if(jsonObj.get("billdesc")!=null) billDescription = jsonObj.get("billdesc").toString().replaceAll("\"", "");
				if(jsonObj.get("custrefno")!=null) referenceNo = jsonObj.get("custrefno").toString().replaceAll("\"", "");
				if(jsonObj.get("paymode")!=null) billPayMode = jsonObj.get("paymode").toString().replaceAll("\"", "");
				if(jsonObj.get("companyname")!=null) merchCompanyName = jsonObj.get("companyname").toString().replaceAll("\"", "");
				if(jsonObj.get("billamount")!=null) billAmount = jsonObj.get("billamount").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE: pvt key is incorrect "+privateKey);
				}
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				if(billPayMode.equals("W")) {
					//get wallet details
					arywalletDetails =  (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails(relationshipNo);
					//other payment mode introduces in future shall be added here
				}/*else if(billpaymode.equals("A")) {
					//get account details }*/
				
				
				
				if(arywalletDetails != null && allow) {
					
					int count = arywalletDetails.size();
					
					String[] walletIdArray= new String [count];
					String[] walletAmountArray= new String [count];
					String[] walletDescArray= new String [count];
					
					
					 // put some value pairs into the JSON object .
					 for(int i=0;i<arywalletDetails.size();i++){
						 walletIdArray[i]= ((Wallet)arywalletDetails.get(i)).getWalletId();
						 walletAmountArray[i]= ((Wallet)arywalletDetails.get(i)).getCurrentBalance();
						 walletDescArray[i]= ((Wallet)arywalletDetails.get(i)).getWalletDesc();
						
					 }
					 
					 obj.add("walletid", gson.toJsonTree(walletIdArray));
					  obj.add("currbal", gson.toJsonTree(walletAmountArray));
					  obj.add("walletdesc", gson.toJsonTree(walletDescArray));
					  obj.add("error", gson.toJsonTree("false"));
					  
					  
				
					} else {
				
				obj.add("error", gson.toJsonTree("true"));
			}
			try {
				PPWalletEnvironment.setComment(3, className, " JSON JSON_PAYMENT_BILLPAY_BILLPAYRREQ String is "+gson.toJson(obj));
				jsonOutput_1.print(gson.toJson(obj));
			} finally {
				//close all objects here
				if(jsonOutput_1!=null) jsonOutput_1.close(); if(arywalletDetails!=null) arywalletDetails =null; 	if(gson!=null) gson=null;
				
				if(billerCode!=null) billerCode=null;	if(relationshipNo!=null) relationshipNo=null; if(privateKey!=null) privateKey=null;
				if(billAmount!=null) billAmount=null; 	if(billDescription!=null) billDescription=null; 	if(merchCompanyName!=null) merchCompanyName=null;	if (billPayMode!=null) billPayMode=null;
				
			
			}
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_BILLPAY_BILLPAYRREQ "+e.getMessage());
			}
			
			
			break;
			
		case JSON_PAYMENT_BILLPAY_PAYNOW:
			
			try {
				
				
				String currencyId = "404";	String billerCode = null; String billAmount = null;  String billDescription = null;
				boolean result = false;	String referenceNo = null;	String billPayMode = null;	String merchCompanyName = null; String custwalletId = null;
				String relationshipNo = null;  String privateKey = null; boolean allow= true; PrintWriter jsonOutput_1 = null;  String userType="C"; String userId=null;
				
				
				if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");
				if(jsonObj.get("billdesc")!=null) billDescription = jsonObj.get("billdesc").toString().replaceAll("\"", "");
				if(jsonObj.get("custrefno")!=null) referenceNo = jsonObj.get("custrefno").toString().replaceAll("\"", "");
				if (jsonObj.get("paymode")!=null) billPayMode = jsonObj.get("paymode").toString().replaceAll("\"", "");
				if(jsonObj.get("companyname")!=null) merchCompanyName = jsonObj.get("companyname").toString().replaceAll("\"", "");
				if(jsonObj.get("billamount")!=null) billAmount = jsonObj.get("billamount").toString().replaceAll("\"", "");
				if(jsonObj.get("walletid")!=null) custwalletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_BILLPAY_PAYNOW: pvt key is incorrect "+privateKey);
				}
				
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				
				
				
				if (allow) {
					if(billPayMode.equals("W")) {  //wallet
						// pay bill using wallet
					//request.setAttribute("walletdetails", (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails(((User)session.getAttribute("SESS_USER")).getUserId()));
						// TODO Here the system reference of the wallet should be the transaction code of the billpay system, i.e. generate txncode in billpay table and refer it to the wallet_table sysreference
							if(Float.parseFloat(billAmount) > 0) {
								result = (boolean)BillPayDao.class.getConstructor().newInstance().payBillWithWallet(relationshipNo, billPayMode, custwalletId, billerCode, referenceNo, currencyId, billAmount, billDescription );
								if(result) {
									
									SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId , userType, "C", StringUtils.substring(" Bill paid to "+billerCode, 0, 48) );
									obj.add("error", gson.toJsonTree("false"));
								}else {
									obj.add("error", gson.toJsonTree("true"));
								}
							
							}							
						}else  if(billPayMode.equals("A"))  {   //account
						//Login here we don't have the option for now	
					}	
						
						
				}else {
					
					
					obj.add("error", gson.toJsonTree("true"));
				}
				
				try {
					PPWalletEnvironment.setComment(3, className, " JSON JSON_PAYMENT_BILLPAY_PAYNOW String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(currencyId!=null) currencyId=null; if(billAmount!=null) billAmount=null; if(billDescription!=null) billDescription=null;
					if(referenceNo!=null) referenceNo=null; if(billPayMode!=null) billPayMode=null; if(merchCompanyName!=null) merchCompanyName=null;
					if(custwalletId!=null) custwalletId=null;  if(relationshipNo!=null) relationshipNo=null;
					if(privateKey!=null) privateKey=null; if(gson!=null) gson=null;	
				}
				
			}catch(Exception e) {
			PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_BILLPAY_PAYNOW "+e.getMessage());
			}
			
			break;
			
		case JSON_PAYMENT_VIEW_PAYBILL_TXN:
		
						try {
				
				String relationshipNo = null; String privateKey = null; boolean allow = true;  PrintWriter jsonOutput_1 = null; ArrayList<BillPayTransaction> arrTransaction = null; 
				
				
				if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				
				PPWalletEnvironment.setComment(2, className, " Relationship is "+ relationshipNo);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_VIEW_PROFILE: pvt key is incorrect "+privateKey);
				}
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				//get the all bill pay  transactions
				

				arrTransaction = (ArrayList<BillPayTransaction>)BillPayDao.class.getConstructor().newInstance().getBillPayTransactionsForUser(relationshipNo);
				
				if(arrTransaction!=null && allow == true){
					int count = arrTransaction.size();
					
					String[] companyNameArray= new String [count];
					String[] txnAmountArray= new String [count];
					String[] txnDateTimeArray= new String [count];
					
					// put some value pairs into the JSON object .
					 for(int i=0;i<arrTransaction.size();i++){
						 companyNameArray[i]= ((BillPayTransaction)arrTransaction.get(i)).getMerchantCompany();
						 txnAmountArray[i]= ((BillPayTransaction)arrTransaction.get(i)).getBillAmount();
						 txnDateTimeArray[i]= ((BillPayTransaction)arrTransaction.get(i)).getTxnDateTime();
					 }
					 
					obj.add("companyname", gson.toJsonTree(companyNameArray));
					obj.add("tnmamount", gson.toJsonTree(txnAmountArray));
					obj.add("txndatetime", gson.toJsonTree(txnDateTimeArray));
					obj.add("error", gson.toJsonTree("false"));
					
				
					
			}else {
				
				obj.add("error", gson.toJsonTree("true"));
			}
			
			try {
				PPWalletEnvironment.setComment(3, className, " JSON JSON_PAYMENT_VIEW_PAYBILL_TXN String is "+gson.toJson(obj));
				jsonOutput_1.print(gson.toJson(obj));
			} finally {
				
				if(jsonOutput_1!=null) jsonOutput_1.close();  if(relationshipNo!=null) relationshipNo=null;
				if(privateKey!=null) privateKey=null; if(gson!=null) gson=null; if(arrTransaction!=null) arrTransaction=null;
						
			}
			
			
			}catch(Exception e) {
			PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_BILLPAY_PAYNOW "+e.getMessage());
			}
	
			break;
			
		case JSON_PAYMENT_VIEW_PAYBILL_TXN_GRAPH:
			try {
				
				String relationshipNo = null; String privateKey = null; boolean allow = true;  PrintWriter jsonOutput_1 = null; ArrayList<BillPayTransaction> arrTransaction = null; 
				float amountDay1=0; float amountDay2=0; float amountDay3=0; float amountDay4=0; float amountDay5=0; float amountDay6=0; float amountToday=0;
				String currentDay=null; 	String dayOne=null; 	String dayTwo=null; 	String dayThree=null; 	String dayFour=null; 	String dayFive=null; String daySix=null;
				
				if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				
				PPWalletEnvironment.setComment(2, className, " Relationship is "+ relationshipNo);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_VIEW_PAYBILL_TXN_GRAPH: pvt key is incorrect "+privateKey);
				}
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				//get the all bill pay  transactions
			
				arrTransaction = (ArrayList<BillPayTransaction>)BillPayDao.class.getConstructor().newInstance().getBillPayTransactionsForUser(relationshipNo);
				
				if(arrTransaction!=null && allow ){
					currentDay= (Utilities.getCurrentDate()); 
					dayOne = Utilities.getDateCalculate(currentDay, -1, "yyyy-MM-dd"); dayTwo = Utilities.getDateCalculate(currentDay, -2, "yyyy-MM-dd");
					dayThree = Utilities.getDateCalculate(currentDay, -3, "yyyy-MM-dd"); dayFour = Utilities.getDateCalculate(currentDay, -4, "yyyy-MM-dd");
					dayFive = Utilities.getDateCalculate(currentDay, -5, "yyyy-MM-dd"); daySix = Utilities.getDateCalculate(currentDay, -6, "yyyy-MM-dd");
				
					//int count = arrTransaction.size();
					String[] txnDate = new String[7]; 
					String[] txnAmount = new String[7];
					
					 for(int i=0;i<arrTransaction.size();i++){
						 if (((BillPayTransaction)arrTransaction.get(i)).getTxnDateTime().contains(currentDay)) {
								amountToday += Float.parseFloat(((BillPayTransaction)arrTransaction.get(i)).getBillAmount());
								
								PPWalletEnvironment.setComment(2, className, " Amount is"+ ((BillPayTransaction)arrTransaction.get(i)).getBillAmount());
						 }else if (((BillPayTransaction)arrTransaction.get(i)).getTxnDateTime().contains(dayOne)) {
									amountDay1 += Float.parseFloat(((BillPayTransaction)arrTransaction.get(i)).getBillAmount());
							}else if (((BillPayTransaction)arrTransaction.get(i)).getTxnDateTime().contains(dayTwo)) {
								amountDay2 += Float.parseFloat(((BillPayTransaction)arrTransaction.get(i)).getBillAmount());
							}else if (((BillPayTransaction)arrTransaction.get(i)).getTxnDateTime().contains(dayThree)) {
								amountDay3 += Float.parseFloat(((BillPayTransaction)arrTransaction.get(i)).getBillAmount());
							}else if (((BillPayTransaction)arrTransaction.get(i)).getTxnDateTime().contains(dayFour)) {
								amountDay4 += Float.parseFloat(((BillPayTransaction)arrTransaction.get(i)).getBillAmount());
							}else if (((BillPayTransaction)arrTransaction.get(i)).getTxnDateTime().contains(dayFive)) {
								amountDay5 += Float.parseFloat(((BillPayTransaction)arrTransaction.get(i)).getBillAmount());
							}else if (((BillPayTransaction)arrTransaction.get(i)).getTxnDateTime().contains(daySix)) {
								amountDay6 += Float.parseFloat(((BillPayTransaction)arrTransaction.get(i)).getBillAmount());
							}
					 }
					 
					 txnAmount[0] = String.valueOf(amountToday);
					 txnAmount[1] = String.valueOf(amountDay1); 
					 txnAmount[2] = String.valueOf(amountDay2); 
					 txnAmount[3] = String.valueOf(amountDay3);
					 txnAmount[4] = String.valueOf(amountDay4); 
					 txnAmount[5] = String.valueOf(amountDay5); 
					 txnAmount[6] = String.valueOf(amountDay6); 
					 
					 txnDate[0]=currentDay;
					 txnDate[1]=dayOne;
					 txnDate[2]=dayTwo;
					 txnDate[3]=dayThree;
					 txnDate[4]=dayFour;
					 txnDate[5]=dayFive;
					 txnDate[6]=daySix;
					
					 	obj.add("tnmamount", gson.toJsonTree(txnAmount));
						obj.add("txndatetime", gson.toJsonTree(txnDate));
						obj.add("error", gson.toJsonTree("false"));
						
				}else {
					
					obj.add("error", gson.toJsonTree("true"));
				}
				
				try {
					PPWalletEnvironment.setComment(3, className, " JSON JSON_PAYMENT_VIEW_PAYBILL_TXN_GRAPH String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(privateKey!=null) privateKey=null; if(gson!=null) gson=null; if(arrTransaction!=null) arrTransaction=null;
					if(jsonOutput_1!=null) jsonOutput_1.close();  if(relationshipNo!=null) relationshipNo=null;
					if(amountDay1!=0) amountDay1=0; if(amountDay2!=0) amountDay2=0; if(amountDay3!=0) amountDay3=0;
					if(amountDay4!=0) amountDay4=0; if(amountDay5!=0) amountDay5=0; if(amountDay6!=0) amountDay6=0;if(amountToday!=0) amountToday=0;
					if(dayOne!=null) dayOne=null; if(dayTwo!=null) dayTwo=null; if(dayThree!=null) dayThree=null;
					if(dayFour!=null) dayFour=null; if(dayFive!=null) dayFive=null; if(daySix!=null) daySix=null;  if(currentDay!=null) currentDay=null;
					
				}
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_PAYMENT_VIEW_PAYBILL_TXN_GRAPH "+e.getMessage());
				}
			break;
			
			//Eric
			
			case Rules.JSON_CUST_RETAIL_PAYMENT:
			

			
			try {
				PrintWriter jsonOutput_1 = null; String scanString = null;
				String currencyId = null;	String billerCode = null; String retailAmount = null;  String paymentComment = null; String transactionCode = null; 
				boolean allow = true;	String payMode = null; String custWalletId = null; String location = null; String privateKey = null; String userId = null;
				String relationshipNo = null; String qrType =null; String dynamicKey = null; String latitude = null; String longitude= null; String userType = "C";
				String[] qrElements = null; String payType = null;  Date qrTime = null;
				
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("retailamount")!=null) retailAmount = jsonObj.get("retailamount").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("paymentcomment")!=null) paymentComment = jsonObj.get("paymentcomment").toString().replaceAll("\"", "");
				if(jsonObj.get("latitude")!=null) latitude = jsonObj.get("latitude").toString().replaceAll("\"", "");
				if(jsonObj.get("longitude")!=null) longitude = jsonObj.get("longitude").toString().replaceAll("\"", "");
				if(jsonObj.get("walletid")!=null) custWalletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
				if(jsonObj.get("scanstring")!=null) scanString = jsonObj.get("scanstring").toString().replaceAll("\"", "");
//				if(jsonObj.get("retailamount")!=null) retailAmount = jsonObj.get("retailamount").toString().replaceAll("\"", "");
				//Location
				if(latitude != null && longitude != null) {location = latitude + "-"+ longitude;}
				
				
				
				//checking private key
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_RETAIL_PAYMENT: pvt key is incorrect "+privateKey);
				}
				
				// check scanString
				if(scanString !=null) qrElements = scanString.split("\\|");{
					qrType = qrElements[0]; payType = qrElements[1]; billerCode = qrElements[2];  currencyId = qrElements[3];
					//check for dynamic qr  S|WRP|551393312715|404  D|WRP|551393312715|404|200|2020-04-19 07:23:09
					if(qrType.equals("D")) {
						 dynamicKey = qrElements[5];
						 PPWalletEnvironment.setComment(3, className, "dynamicKey is "+dynamicKey);
						 qrTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dynamicKey);
						 
						 qrTime = DateUtils.addMinutes(qrTime, 7);
					}
				}
				//check for dynamic key
				Date current=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utilities.getMYSQLCurrentTimeStampForInsert());
				PPWalletEnvironment.setComment(3, className, "current "+current +" qrTime "+qrTime);
				if(qrType.equals("D") && current.after(qrTime)) {
					allow = false;
					PPWalletEnvironment.setComment(3, className, "it is late by two minutes ");
				}
				
				
				PPWalletEnvironment.setComment(3, className, "relationshipNo "+relationshipNo+" currencyId "+currencyId+"retailAmount "+retailAmount+" paymentComment "+paymentComment+" billerCode "+billerCode+
						"latitude "+latitude+" longitude "+" payMode "+payMode+" walletid "+custWalletId+" location "+location);
				
				//compare dates
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject();

				if(Float.parseFloat(retailAmount) > 0 && allow == true && payType.equals(PPWalletEnvironment.getCodeWalletToMerchantRetailPay())) { 
					transactionCode = (String) RetailPayDao.class.getConstructor().newInstance().retailPaymentWithWallet(relationshipNo, payType, custWalletId, billerCode, currencyId, retailAmount, paymentComment,location);
					if (transactionCode != null) {
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId , userType, "C", StringUtils.substring(" retail payment Txncode "+transactionCode, 0, 48) );
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
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(currencyId!=null) currencyId=null;if(billerCode!=null) billerCode=null;if(retailAmount!=null) retailAmount=null; //payType
						if(paymentComment!=null) paymentComment = null; if(payMode!=null) payMode=null;if(custWalletId!=null) custWalletId=null;if(location!=null) location=null;if(payType!=null) payType=null;
						if(relationshipNo!=null) relationshipNo = null; if(qrType!=null) qrType=null;if(dynamicKey!=null) dynamicKey=null; if(qrElements!=null) qrElements = null; 
						if(current!=null) current = null; if(qrTime!=null) qrTime=null; if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
					}
					
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_RETAIL_PAYMENT "+e.getMessage());
			}
			
			break;
			
		case Rules.JSON_CUST_RETAIL_PAYMENT_TABLE:
			try {
				String relationshipNo = null;	 String privateKey = null;	boolean allow = true; ArrayList<RetailPayTransaction> arrayRetailTxn = null;
				PrintWriter jsonOutput_1 = null;		
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect "+privateKey);
				}
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Google Json Object
				jsonOutput_1 = response.getWriter();
				arrayRetailTxn = (ArrayList<RetailPayTransaction>)RetailPayDao.class.getConstructor().newInstance().getRetailPayTransactionsForUser(relationshipNo);
				if(arrayRetailTxn!=null && allow ){
					int count = arrayRetailTxn.size();
					String[] txnCode = new String[count]; String[] billerCode = new String[count];  String[] txnDate = new String[count];
					String[] txnAmount = new String[count];
					 for(int i=0;i<arrayRetailTxn.size();i++){
						
						 txnCode[i]= ((RetailPayTransaction)arrayRetailTxn.get(i)).getTxnCode();
						 billerCode[i]= ((RetailPayTransaction)arrayRetailTxn.get(i)).getBillerCode();
						 txnDate[i]= ((RetailPayTransaction)arrayRetailTxn.get(i)).getTxnDateTime();
						 txnAmount[i]= ((RetailPayTransaction)arrayRetailTxn.get(i)).getRetailAmount();
					 }
					 obj.add("txncode", gson.toJsonTree(txnCode)); obj.add("txndate", gson.toJsonTree(txnDate));
					 obj.add("billercode", gson.toJsonTree(billerCode)); obj.add("txnamount", gson.toJsonTree(txnAmount));
					 obj.add("error", gson.toJsonTree("false"));
					
				}else {
					obj.add("error", gson.toJsonTree("true"));
				}
				
				try {
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(privateKey!=null) privateKey=null; if(relationshipNo!=null) relationshipNo=null; 
					 if(arrayRetailTxn!=null)
						 if(arrayRetailTxn.size()==0)
							 arrayRetailTxn=null;
				}

			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE "+e.getMessage());
			}
			break;
			
			case Rules.JSON_CUST_RETAIL_PAYMENT_GRAPH:
				try {
					String relationshipNo = null;	 String privateKey = null;	boolean allow = true; ArrayList<RetailPayTransaction> arrayRetailTxn = null;
					String dayTwo = null;	 String dayThree = null; String dayOne =null; String dayFour =null; String currDate = null;
					PrintWriter jsonOutput_1 = null; float amountDay_1 = 0; float amountDay_2 = 0;  float amountDay_3 = 0;  float amountDay_4 = 0; float amountToday = 0;
					if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_RETAIL_PAYMENT_GRAPH: pvt key is incorrect "+privateKey);
					}
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Google Json Object
					jsonOutput_1 = response.getWriter();
					arrayRetailTxn = (ArrayList<RetailPayTransaction>)RetailPayDao.class.getConstructor().newInstance().getRetailPayTransactionsForUser(relationshipNo);
					
					if(arrayRetailTxn!=null && allow ){
						currDate = Utilities.getCurrentDate();
						dayOne = Utilities.getDateCalculate(currDate, -4, "yyyy-MM-dd"); dayTwo = Utilities.getDateCalculate(currDate, -3, "yyyy-MM-dd");
						dayThree = Utilities.getDateCalculate(currDate, -2, "yyyy-MM-dd"); dayFour = Utilities.getDateCalculate(currDate, -1, "yyyy-MM-dd");
						 PPWalletEnvironment.setComment(3, className, "date is  "+ currDate + " currDate" + " dayOne "+dayOne+" dayTwo "+dayTwo+" dayThree "+dayThree);
						int count = arrayRetailTxn.size();
						PPWalletEnvironment.setComment(3, className, "count "+count);
						String[] txnDate = new String[5]; String[] txnAmount = new String[5];
						 for(int i=0;i<count;i++){
							 PPWalletEnvironment.setComment(3, className, "Amouunt is  "+((RetailPayTransaction)arrayRetailTxn.get(i)).getRetailAmount());
							 PPWalletEnvironment.setComment(3, className, "date is  "+ Utilities.formartDateforGraph(((RetailPayTransaction)arrayRetailTxn.get(i)).getTxnDateTime()));
							 if(Utilities.formartDateforGraph(((RetailPayTransaction)arrayRetailTxn.get(i)).getTxnDateTime()).contains(dayOne)) {
								 amountDay_1 += Float.parseFloat(((RetailPayTransaction)arrayRetailTxn.get(i)).getRetailAmount());
							 }else if (Utilities.formartDateforGraph(((RetailPayTransaction)arrayRetailTxn.get(i)).getTxnDateTime()).contains(dayTwo)) {
								amountDay_2 += Float.parseFloat(((RetailPayTransaction)arrayRetailTxn.get(i)).getRetailAmount());
							 }else if (Utilities.formartDateforGraph(((RetailPayTransaction)arrayRetailTxn.get(i)).getTxnDateTime()).contains(dayThree)) {
								amountDay_3 += Float.parseFloat(((RetailPayTransaction)arrayRetailTxn.get(i)).getRetailAmount());
							 }else if (Utilities.formartDateforGraph(((RetailPayTransaction)arrayRetailTxn.get(i)).getTxnDateTime()).contains(dayFour)) {
								amountDay_4 += Float.parseFloat(((RetailPayTransaction)arrayRetailTxn.get(i)).getRetailAmount());
							 }else if (Utilities.formartDateforGraph(((RetailPayTransaction)arrayRetailTxn.get(i)).getTxnDateTime()).contains(currDate)) {
								amountToday += Float.parseFloat(((RetailPayTransaction)arrayRetailTxn.get(i)).getRetailAmount());
							 }
						 }
						 txnAmount[0] = String.valueOf(amountDay_1); txnAmount[1] = String.valueOf(amountDay_2); txnAmount[2] = String.valueOf(amountDay_3); txnAmount[3] = String.valueOf(amountDay_4);
						 txnAmount[4] = String.valueOf(amountToday);
						 txnDate[0] = dayOne; txnDate[1] = dayTwo; txnDate[2] = dayThree; txnDate[3] = dayFour; txnDate[4] = currDate;
						 
						 obj.add("txnamount", gson.toJsonTree(txnAmount));  obj.add("txndate", gson.toJsonTree(txnDate));  
						 obj.add("error", gson.toJsonTree("false"));
						
					}else{
						  obj.add("error", gson.toJsonTree("true"));
					}
					try {
						
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
						if(jsonOutput_1!=null) jsonOutput_1.close(); 
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
						
						if(relationshipNo!=null) relationshipNo=null; if(arrayRetailTxn!=null) arrayRetailTxn=null; if(dayTwo!=null) dayTwo=null; 
						if(dayThree!=null) dayThree=null; if(dayOne!=null) dayOne=null; if(dayFour!=null) dayFour=null; if(currDate!=null) currDate=null; 
						if(amountDay_1!=0)amountDay_1=0; if(amountDay_2!=0) amountDay_2=0; if(amountDay_3!=0) amountDay_3=0; 
						if(amountToday!=0) amountToday=0; 
						
					}
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE "+e.getMessage());
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
		// TODO Auto-generated method stub

	}

}

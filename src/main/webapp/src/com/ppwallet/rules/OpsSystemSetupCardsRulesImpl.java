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
import com.ppwallet.dao.OpsSystemManageCardsDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.dao.UserLoginDao;
import com.ppwallet.model.CardBIN;
import com.ppwallet.model.CardProduct;
import com.ppwallet.model.User;
import com.ppwallet.utilities.Utilities;

public class OpsSystemSetupCardsRulesImpl implements Rules {
	private static String className = OpsSystemSetupCardsRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(false);
		switch (rules){

			case Rules.OPS_SYS_CARDS_ADD_NEW_BIN:			
				try {
					if(session.getAttribute("SESS_USER")==null) {
						session.invalidate(); throw new Exception ("Session Expired..");
					}
					String userId = null; String userType = null;
					String strBIN = null; String currencyId = null; String cardType = null; String issuingBankName = null;
					String issuingAccountNo = null; String issuingBankAccountName = null; String issuingBankRoutingCode = null;
					String issuingBankSwiftCode = null; String interchangeRateVariable = null; String interchangeRateFixed = null;
					String bankInterchageShare = null; String bankSettlementCustoffTime = null;	String binStatus = null;
					String moduleCode = "C"; // Cards Issuing
					ArrayList<CardBIN> arrCardsBIN = null;
					PPWalletEnvironment.setComment(2,className,"inside   "+Rules.OPS_SYS_CARDS_ADD_NEW_BIN);
					if(request.getParameter("binid_a")!=null)		strBIN = StringUtils.trim(request.getParameter("binid_a"));
					if(request.getParameter("hdncurrencyid_a")!=null)	currencyId = StringUtils.trim(request.getParameter("hdncurrencyid_a"));
					if(request.getParameter("hdncardtype_a")!=null)	cardType = StringUtils.trim(request.getParameter("hdncardtype_a"));
					if(request.getParameter("issbankname_a")!=null)	issuingBankName = StringUtils.trim(request.getParameter("issbankname_a"));
					if(request.getParameter("issbankaccno_a")!=null)	issuingAccountNo = StringUtils.trim(request.getParameter("issbankaccno_a"));
					if(request.getParameter("issbankaccname_a")!=null)	issuingBankAccountName = StringUtils.trim(request.getParameter("issbankaccname_a"));
					if(request.getParameter("issbankroutcode_a")!=null)	issuingBankRoutingCode = StringUtils.trim(request.getParameter("issbankroutcode_a"));
					if(request.getParameter("issbankswiftcode_a")!=null)	issuingBankSwiftCode = StringUtils.trim(request.getParameter("issbankswiftcode_a"));
					if(request.getParameter("binvarichrate_a")!=null)	interchangeRateVariable = StringUtils.trim(request.getParameter("binvarichrate_a"));
					if(request.getParameter("binifixchrate_a")!=null)	interchangeRateFixed = StringUtils.trim(request.getParameter("binifixchrate_a"));
					if(request.getParameter("issbankichshare_a")!=null)	bankInterchageShare = StringUtils.trim(request.getParameter("issbankichshare_a"));
					if(request.getParameter("issbanksetcutoff_a")!=null)	bankSettlementCustoffTime = StringUtils.trim(request.getParameter("issbanksetcutoff_a"));
					if(request.getParameter("hdnbinstatus_a")!=null)	binStatus = StringUtils.trim(request.getParameter("hdnbinstatus_a"));
					userId = ((User)session.getAttribute("SESS_USER")).getUserId();
					userType = ((User)session.getAttribute("SESS_USER")).getUserType();
					// (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getUserId() );
					PPWalletEnvironment.setComment(2,className,"before adding BIN  ");
					if( OpsSystemManageCardsDao.class.getConstructor().newInstance().addNewBINforCards(strBIN, currencyId, cardType, issuingBankName, 
							issuingAccountNo, issuingBankAccountName, issuingBankRoutingCode, issuingBankSwiftCode, interchangeRateVariable,
							interchangeRateFixed, bankInterchageShare, bankSettlementCustoffTime, binStatus)    ) {
						// call the audit trail here
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, "Add New BIN");	
						

						arrCardsBIN  = (ArrayList<CardBIN>)OpsSystemManageCardsDao.class.getConstructor().newInstance().getAllBINforCards();
						request.setAttribute("allcardbins", arrCardsBIN);
					}else {						throw new Exception ("Problem with the addition of BIN");					}
					request.setAttribute("lastaction", "syscard"); // going to profile
					request.setAttribute("lastrule", Rules.OPS_SYS_CARDS_VIEWALL_BIN_PAGE); //  set the last rule for left menu selection
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getIssuingBINListPage()).forward(request,response);
					} finally {
						if(userId!=null) userId=null;if(arrCardsBIN!=null) arrCardsBIN=null;if(strBIN!=null) strBIN=null; if(currencyId!=null) currencyId=null;
						if(issuingBankName!=null) issuingBankName=null;if(issuingAccountNo!=null) issuingAccountNo=null;
						if(issuingBankAccountName!=null) issuingBankAccountName=null; if(issuingBankRoutingCode!=null) issuingBankRoutingCode=null;
						if(issuingBankSwiftCode!=null) issuingBankSwiftCode=null; if(interchangeRateVariable!=null) interchangeRateVariable=null;
						if(bankInterchageShare!=null) bankInterchageShare=null; if(bankSettlementCustoffTime!=null) bankSettlementCustoffTime=null;
						if(binStatus!=null) binStatus=null;
					}
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
			break;
			case Rules.OPS_SYS_CARDS_VIEWALL_BIN_PAGE:
				try {
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "syscard"); // going to profile
					request.setAttribute("lastrule", Rules.OPS_SYS_CARDS_VIEWALL_BIN_PAGE); //  set the last rule for left menu selection
					ArrayList<CardBIN> arrCardsBIN  = (ArrayList<CardBIN>)OpsSystemManageCardsDao.class.getConstructor().newInstance().getAllBINforCards();
					request.setAttribute("allcardbins", arrCardsBIN);
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getIssuingBINListPage()).forward(request,response);
					} finally {
						if(arrCardsBIN!=null) arrCardsBIN=null;
					}
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
			break;			
			case Rules.OPS_SYS_CARDS_UPDATECARDSBIN:
				try {
					if(session.getAttribute("SESS_USER")==null) {
						session.invalidate(); throw new Exception ("Session Expired..");
					}
					String userId = null; String userType = null;	ArrayList<CardBIN> arrCardsBIN = null;
					String strBIN = null; String currencyId = null; String cardType = null; String issuingBankName = null;
					String issuingAccountNo = null; String issuingBankAccountName = null; String issuingBankRoutingCode = null;
					String issuingBankSwiftCode = null; String interchangeRateVariable = null; String interchangeRateFixed = null;
					String bankInterchageShare = null; String bankSettlementCustoffTime = null;	String binStatus = null;
					String moduleCode = "C"; // Cards Issuing
					
					if(request.getParameter("hdnbinid")!=null)		strBIN = StringUtils.trim(request.getParameter("hdnbinid"));
					if(request.getParameter("hdncurrencyid")!=null)	currencyId = StringUtils.trim(request.getParameter("hdncurrencyid"));
					if(request.getParameter("hdncardtype")!=null)	cardType = StringUtils.trim(request.getParameter("hdncardtype"));
					if(request.getParameter("issbankname")!=null)	issuingBankName = StringUtils.trim(request.getParameter("issbankname"));
					if(request.getParameter("issbankaccno")!=null)	issuingAccountNo = StringUtils.trim(request.getParameter("issbankaccno"));
					if(request.getParameter("issbankaccname")!=null)	issuingBankAccountName = StringUtils.trim(request.getParameter("issbankaccname"));
					if(request.getParameter("issbankroutcode")!=null)	issuingBankRoutingCode = StringUtils.trim(request.getParameter("issbankroutcode"));
					if(request.getParameter("issbankswiftcode")!=null)	issuingBankSwiftCode = StringUtils.trim(request.getParameter("issbankswiftcode"));
					if(request.getParameter("binvarichrate")!=null)	interchangeRateVariable = StringUtils.trim(request.getParameter("binvarichrate"));
					if(request.getParameter("binifixchrate")!=null)	interchangeRateFixed = StringUtils.trim(request.getParameter("binifixchrate"));
					if(request.getParameter("issbankichshare")!=null)	bankInterchageShare = StringUtils.trim(request.getParameter("issbankichshare"));
					if(request.getParameter("issbanksetcutoff")!=null)	bankSettlementCustoffTime = StringUtils.trim(request.getParameter("issbanksetcutoff"));
					if(request.getParameter("hdnbinstatus")!=null)	binStatus = StringUtils.trim(request.getParameter("hdnbinstatus"));

					
					userId = ((User)session.getAttribute("SESS_USER")).getUserId();
					userType = ((User)session.getAttribute("SESS_USER")).getUserType();
					// (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getUserId() );

					if( OpsSystemManageCardsDao.class.getConstructor().newInstance().updateSpecBINforCards(strBIN, currencyId, cardType, issuingBankName, 
							issuingAccountNo, issuingBankAccountName, issuingBankRoutingCode, issuingBankSwiftCode, interchangeRateVariable,
							interchangeRateFixed, bankInterchageShare, bankSettlementCustoffTime, binStatus)    ) {
						// call the audit trail here
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, "Updated BIN "+strBIN);
						
						arrCardsBIN  = (ArrayList<CardBIN>)OpsSystemManageCardsDao.class.getConstructor().newInstance().getAllBINforCards();
						request.setAttribute("allcardbins", arrCardsBIN);
					}else {
						throw new Exception ("Problem with the addition of BIN");
					}
					request.setAttribute("lastaction", "syscard"); // going to profile
					request.setAttribute("lastrule", Rules.OPS_SYS_CARDS_VIEWALL_BIN_PAGE); //  set the last rule for left menu selection
					
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getIssuingBINListPage()).forward(request,
								response);
					} finally {
						if(arrCardsBIN!=null) arrCardsBIN = null; if(strBIN!=null) strBIN = null; if(currencyId!=null) currencyId = null;
						if(cardType!=null) cardType = null; if(issuingBankName!=null) issuingBankName = null; if(issuingAccountNo!=null) issuingAccountNo = null;
						if(issuingBankAccountName!=null) issuingBankAccountName = null; if(issuingBankRoutingCode!=null) issuingBankRoutingCode = null; if(issuingBankSwiftCode!=null) issuingBankSwiftCode = null;
						if(interchangeRateVariable!=null) interchangeRateVariable = null; if(interchangeRateFixed!=null) interchangeRateFixed = null; if(bankInterchageShare!=null) bankInterchageShare = null;
						if(bankSettlementCustoffTime!=null) bankSettlementCustoffTime = null; if(binStatus!=null) binStatus = null; 						
					}
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
				
			break;

			case Rules.OPS_SYS_CARDS_VIEWALLPRODUCT_PAGE:
				try {
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "syscard"); // going to profile
					request.setAttribute("lastrule", Rules.OPS_SYS_CARDS_VIEWALLPRODUCT_PAGE); //  set the last rule for left menu selection
					ArrayList<CardProduct> arrCardsProduct  = (ArrayList<CardProduct>)OpsSystemManageCardsDao.class.getConstructor().newInstance().getAllCardProducts();
					request.setAttribute("allcardproducts", arrCardsProduct);	
					ArrayList<CardBIN> arrCardsBIN  = (ArrayList<CardBIN>)OpsSystemManageCardsDao.class.getConstructor().newInstance().getAllBINforCards();
					if(arrCardsBIN!=null)
						PPWalletEnvironment.setComment(2,className,"arrCardsBIN size  "+arrCardsBIN.size());
					request.setAttribute("allcardbins", arrCardsBIN);					

					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysAllProductPage()).forward(request,
								response);
					} finally {
						if(arrCardsProduct!=null) arrCardsProduct = null; if(arrCardsBIN!=null) arrCardsBIN = null; 
					}
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}								
			break;			
			case Rules.OPS_SYS_CARDS_CREATECARDPRODUCT:
				try {
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "syscard"); // going to profile
					request.setAttribute("lastrule", Rules.OPS_SYS_CARDS_VIEWALLPRODUCT_PAGE); //  set the last rule for left menu selection
					String userId = null; String userType = null;	
					String productName = ""; String brandName = null;	String productStartDate = null; String productEndDate = null;
					String allocatedBIN = null;	String productType = null; String billingCycle = null; String suspenseAccountNo = null; 
					String productInterchangeFixed = null; String productInterchangeVariable =null; String productStatus = null;
					String moduleCode = "C"; // Cards Issuing

					userId = ((User)session.getAttribute("SESS_USER")).getUserId();
					userType = ((User)session.getAttribute("SESS_USER")).getUserType();
					
								
					if(request.getParameter("productname_a")!=null)		productName = StringUtils.trim(request.getParameter("productname_a"));					
					if(request.getParameter("brandname_a")!=null)			brandName = StringUtils.trim(request.getParameter("brandname_a"));					
					if(request.getParameter("from_date_a")!=null)			productStartDate = StringUtils.trim(request.getParameter("from_date_a"));					
					if(request.getParameter("to_date_a")!=null)			productEndDate = StringUtils.trim(request.getParameter("to_date_a"));					
					if(request.getParameter("hdnprodbin_a")!=null)		allocatedBIN = StringUtils.trim(request.getParameter("hdnprodbin_a"));					
					if(request.getParameter("prodtype_a")!=null)			productType = StringUtils.trim(request.getParameter("prodtype_a"));					
					if(request.getParameter("billingcycle_a")!=null)		billingCycle = StringUtils.trim(request.getParameter("billingcycle_a"));					
					if(request.getParameter("suspenseaccount_a")!=null)	suspenseAccountNo = StringUtils.trim(request.getParameter("suspenseaccount_a"));					
					if(request.getParameter("intchfixed_a")!=null)		productInterchangeFixed = StringUtils.trim(request.getParameter("intchfixed_a"));					
					if(request.getParameter("intchvar_a")!=null)			productInterchangeVariable = StringUtils.trim(request.getParameter("intchvar_a"));					
					if(request.getParameter("hdnprodstatus_a")!=null)		productStatus = StringUtils.trim(request.getParameter("hdnprodstatus_a"));					

					//productStartDate = Utilities.covertAnyDateFormatToMysqlDateFormat(productStartDate, "MM/dd/yyyy", "yyyy-MM-dd", PPWalletEnvironment.getLocalDateFormat());
					//productEndDate = Utilities.covertAnyDateFormatToMysqlDateFormat(productEndDate, "MM/dd/yyyy", "yyyy-MM-dd", PPWalletEnvironment.getLocalDateFormat());
					
						productName = StringUtils.substring("Created Product "+productName,0,48);
					
					// convert mm/dd/yyyy to yyyy-mm-dd
					if(OpsSystemManageCardsDao.class.getConstructor().newInstance().addCardProduct( productName, brandName, productStartDate, productEndDate, 
						allocatedBIN, productType, billingCycle, suspenseAccountNo, productInterchangeFixed, productInterchangeVariable, productStatus)  ) {
					// call the audit trail here
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, productName);
					}
					
					ArrayList<CardProduct> arrCardsProduct  = (ArrayList<CardProduct>)OpsSystemManageCardsDao.class.getConstructor().newInstance().getAllCardProducts();
					request.setAttribute("allcardproducts", arrCardsProduct);	
					ArrayList<CardBIN> arrCardsBIN  = (ArrayList<CardBIN>)OpsSystemManageCardsDao.class.getConstructor().newInstance().getAllBINforCards();
					request.setAttribute("allcardbins", arrCardsBIN);
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysAllProductPage()).forward(request,
								response);
					} finally {
						if(arrCardsProduct!=null) arrCardsProduct = null; if(productName!=null) productName = null; if(brandName!=null) brandName = null; 
						if(productStartDate!=null) productStartDate = null; if(productEndDate!=null) productEndDate = null; if(allocatedBIN!=null) allocatedBIN = null; 
						if(productType!=null) productType = null; if(billingCycle!=null) billingCycle = null; if(suspenseAccountNo!=null) suspenseAccountNo = null; 
						if(productInterchangeFixed!=null) productInterchangeFixed = null; if(productInterchangeVariable!=null) productInterchangeVariable = null; if(productStatus!=null) productStatus = null; 
						if(arrCardsBIN!=null) arrCardsBIN = null;
					}
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}				
			break;


			case Rules.OPS_SYS_CARDS_EDITSPECPRODUCT:
				try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "syscard"); // going to profile
				request.setAttribute("lastrule", Rules.OPS_SYS_CARDS_VIEWALLPRODUCT_PAGE); //  set the last rule for left menu selection
				
				String userId = null; String userType = null;
				String productID = "";
				String productName = ""; String brandName = null;	String productStartDate = null; String productEndDate = null;
				String allocatedBIN = null;	String productType = null; String billingCycle = null; String suspenseAccountNo = null; 
				String productInterchangeFixed = null; String productInterchangeVariable =null; String productStatus = null;
				String moduleCode = "C"; // Cards Issuing

				userId = ((User)session.getAttribute("SESS_USER")).getUserId();
				userType = ((User)session.getAttribute("SESS_USER")).getUserType();

				
				if(request.getParameter("hdnproductid")!=null)			productID = StringUtils.trim(request.getParameter("hdnproductid"));			
				if(request.getParameter("productname")!=null)				productName = StringUtils.trim(request.getParameter("productname"));					
				if(request.getParameter("brandname")!=null)				brandName = StringUtils.trim(request.getParameter("brandname"));					
				if(request.getParameter("from_date")!=null)				productStartDate = StringUtils.trim(request.getParameter("from_date"));					
				if(request.getParameter("to_date")!=null)					productEndDate = StringUtils.trim(request.getParameter("to_date"));					
				//if(request.getParameter("hdnprodbin")!=null)				allocatedBIN = StringUtils.trim(request.getParameter("hdnprodbin"));					
				if(request.getParameter("hdnproducttype")!=null)			productType = StringUtils.trim(request.getParameter("hdnproducttype"));					
				if(request.getParameter("billingcycle")!=null)			billingCycle = StringUtils.trim(request.getParameter("billingcycle"));					
				if(request.getParameter("suspenseaccount")!=null)			suspenseAccountNo = StringUtils.trim(request.getParameter("suspenseaccount"));					
				if(request.getParameter("intchfixed")!=null)				productInterchangeFixed = StringUtils.trim(request.getParameter("intchfixed"));					
				if(request.getParameter("intchvar")!=null)				productInterchangeVariable = StringUtils.trim(request.getParameter("intchvar"));					
				if(request.getParameter("hdnproductstatus")!=null)		productStatus = StringUtils.trim(request.getParameter("hdnproductstatus"));					
				//productStartDate = Utilities.covertAnyDateFormatToMysqlDateFormat(productStartDate, "MM/dd/yyyy", "yyyy-MM-dd", PPWalletEnvironment.getLocalDateFormat());
				//productEndDate = Utilities.covertAnyDateFormatToMysqlDateFormat(productEndDate, "MM/dd/yyyy", "yyyy-MM-dd", PPWalletEnvironment.getLocalDateFormat());
				
				
				if(OpsSystemManageCardsDao.class.getConstructor().newInstance().updateCardProduct(productID, productName, brandName, productStartDate, productEndDate, 
						 productType, billingCycle, suspenseAccountNo, productInterchangeFixed, productInterchangeVariable, productStatus)  ) {
					// call the audit trail here
					productName = StringUtils.substring("Updated Product "+productName,0,48);
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, productName);
					}		
					ArrayList<CardProduct> arrCardsProduct  = (ArrayList<CardProduct>)OpsSystemManageCardsDao.class.getConstructor().newInstance().getAllCardProducts();
					request.setAttribute("allcardproducts", arrCardsProduct);	
					ArrayList<CardBIN> arrCardsBIN  = (ArrayList<CardBIN>)OpsSystemManageCardsDao.class.getConstructor().newInstance().getAllBINforCards();
					request.setAttribute("allcardbins", arrCardsBIN);
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysAllProductPage()).forward(request,response);
					} finally {
						if(arrCardsProduct!=null) arrCardsProduct = null; if(productName!=null) productName = null; if(brandName!=null) brandName = null; 
						if(productStartDate!=null) productStartDate = null; if(productEndDate!=null) productEndDate = null; if(allocatedBIN!=null) allocatedBIN = null; 
						if(productType!=null) productType = null; if(billingCycle!=null) billingCycle = null; if(suspenseAccountNo!=null) suspenseAccountNo = null; 
						if(productInterchangeFixed!=null) productInterchangeFixed = null; if(productInterchangeVariable!=null) productInterchangeVariable = null; 
						if(productStatus!=null) productStatus = null; if(arrCardsBIN!=null) arrCardsBIN = null;
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

	}

}

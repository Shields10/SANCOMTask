package com.ppwallet.rules;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;

import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.MerchBillPaymentDao;
import com.ppwallet.dao.MerchPaymentsDao;
import com.ppwallet.model.MerchCashoutTransactions;
import com.ppwallet.model.MerchClientDetails;
import com.ppwallet.model.MerchRetailPayTransactions;
import com.ppwallet.model.MerchTopUpTransactions;
import com.ppwallet.model.User;

public class MerchantTransactionsRulesImp implements Rules {
private static String className = MerchantRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
            HttpSession session	= request.getSession(false);
	
	switch (rules){
		
		
			case Rules.MERCHANT_TOPUP_TRANSACTIONS_PAGE:
				try {
					String billerCode = null;	
					if(session.getAttribute("SESS_USER")!=null)		
					billerCode = ((User)session.getAttribute("SESS_USER")).getBillerCode();
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "merchtnxs"); 
					request.setAttribute("lastrule", Rules.MERCHANT_TOPUP_TRANSACTIONS_PAGE ); 
					response.setContentType("text/html");
					request.setAttribute("toputransactions", (ArrayList<MerchTopUpTransactions>)MerchPaymentsDao.class.getConstructor().newInstance().getMerchTopUpTransactions(billerCode));	 
					ctx.getRequestDispatcher(PPWalletEnvironment.getTopupTransactions()).forward(request, response);
						

				}catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
				break;
				

			case Rules.MERCHANT_CASHOUT_TRANSACTIONS_PAGE:
				try {
					String billerCode = null;	
					if(session.getAttribute("SESS_USER")!=null)		
					billerCode = ((User)session.getAttribute("SESS_USER")).getBillerCode();
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "merchtnxs"); 
					request.setAttribute("lastrule", Rules.MERCHANT_CASHOUT_TRANSACTIONS_PAGE ); 
					response.setContentType("text/html");
					request.setAttribute("cashouttransactions", (ArrayList<MerchCashoutTransactions>)MerchPaymentsDao.class.getConstructor().newInstance().getMerchCashoutTransactions(billerCode));	 
					ctx.getRequestDispatcher(PPWalletEnvironment.getCashoutTransactions()).forward(request, response);
						

				}catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
				break;

			case Rules.MERCHANT_RETAIL_TRANSACTIONS_PAGE:
				try {
					String billerCode = null;	
					if(session.getAttribute("SESS_USER")!=null)		
					billerCode = ((User)session.getAttribute("SESS_USER")).getBillerCode();
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "merchtnxs"); 
					request.setAttribute("lastrule", Rules.MERCHANT_RETAIL_TRANSACTIONS_PAGE ); 
					response.setContentType("text/html");
					request.setAttribute("retailpaytransactions", (ArrayList<MerchRetailPayTransactions>)MerchPaymentsDao.class.getConstructor().newInstance().getMerchRetailPayTransactions(billerCode));	 
					ctx.getRequestDispatcher(PPWalletEnvironment.getRetailpayTransactions()).forward(request, response);
						

				}catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}
				break;
		
		
			}
	}
	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		// TODO Auto-generated method stub

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
				PPWalletEnvironment.setComment(2, className, "Problem in forwarding to Error Page, error : "+e1.getMessage());
			}
	}

	@Override
	public void performUploadOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			List<FileItem> multiparts, ServletContext ctx) throws Exception {
		// TODO Auto-generated method stub

	}

}

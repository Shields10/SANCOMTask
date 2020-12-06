package com.ppwallet.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.OpsMerchantManageDao;
import com.ppwallet.dao.OpsSystemManageMerchantDao;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.Transaction;

public class OpsTransactionsRulesImpl implements Rules {
	private static String className = OpsTransactionsRulesImpl.class.getSimpleName();
	
	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(false);
		switch (rules){
		case Rules.OPS_VIEW_TRANSACTIONS_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "viewtransactions");
				request.setAttribute("lastrule", Rules.OPS_VIEW_TRANSACTIONS_PAGE); //  set the last rule for left menu selection
				response.setContentType("text/html");
				
				request.setAttribute("alltransactions", (ArrayList<Transaction>) OpsSystemManageMerchantDao.class.getConstructor().newInstance().getAllTransactions());
				response.setContentType("text/html");
				
				
				
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsTransactionsPage()).forward(request,response);
				} finally {
					
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
		// TODO Auto-generated method stub

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

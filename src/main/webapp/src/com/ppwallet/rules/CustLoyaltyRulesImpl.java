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
import com.ppwallet.dao.LoyaltyDao;
import com.ppwallet.model.LoyaltyRules;

public class CustLoyaltyRulesImpl implements Rules {
	private static String className = CustLoyaltyRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		// TODO Auto-generated method stub
		HttpSession session	= request.getSession(false);
		// TODO Auto-generated method stub
		
		switch (rules){
		
		case Rules.CUST_VIEW_REWARDS:
				try {
					if(session.getAttribute("SESS_USER")==null)
						throw new Exception ("Session has expired, please log in again");
					
					// Get Loyalty transaction details for the user
					request.setAttribute("pointruledetails", (ArrayList<LoyaltyRules>)LoyaltyDao.class.getConstructor().newInstance().getLoyaltyRules());
					
					request.setAttribute("lastaction", "rwd");	request.setAttribute("lastrule", "Reward Rules");
					response.setContentType("text/html");
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerLoyaltyRulesViewPage()).forward(request, response);
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

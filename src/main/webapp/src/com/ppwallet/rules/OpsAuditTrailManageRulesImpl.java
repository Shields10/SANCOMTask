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
import com.ppwallet.dao.OpsSystemManageCustomerDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.model.AuditTrail;
import com.ppwallet.model.CustomerDetails;

public class OpsAuditTrailManageRulesImpl implements Rules {
	private static String className = SystemUtilsDao.class.getSimpleName();

	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(false);
		switch (rules){
		case Rules.OPS_VIEW_AUDIT_TRAIL_PAGE:
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "dash"); 
				request.setAttribute("lastrule", Rules.OPS_VIEW_AUDIT_TRAIL_PAGE);
				
				ArrayList <AuditTrail> arrAudittrail= (ArrayList<AuditTrail>)SystemUtilsDao.class.getConstructor().newInstance().getAllAuditTrails();
				//PPWalletEnvironment.setComment(2,className," ---> 1 before setting to request the arrAudittrail object   is  "+arrAudittrail);
				request.setAttribute("allaudittrails", arrAudittrail);
				//PPWalletEnvironment.setComment(2,className," ---> 2  after setting to request the arrAudittrail object    ");
				if(arrAudittrail!=null) {
					//PPWalletEnvironment.setComment(2,className,"arrAudittrail size  is  "+arrAudittrail.size());
				}
				try {
					response.setContentType("text/html");
					//PPWalletEnvironment.setComment(3,className," before submitting to jsp  "+PPWalletEnvironment.getOpsSysAuditTrailsPage());
					ctx.getRequestDispatcher(PPWalletEnvironment.getOpsSysAuditTrailsPage()).forward(request,response);
				}catch (Exception e) {
					
				}finally {
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

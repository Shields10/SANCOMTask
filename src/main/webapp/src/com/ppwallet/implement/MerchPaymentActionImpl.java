package com.ppwallet.implement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.rules.Rules;
import com.ppwallet.rules.RulesFactoryImpl;

public class MerchPaymentActionImpl implements Action {
	private String rulesaction = null;
	private Rules rules;
	private static String className = MerchPaymentActionImpl.class.getSimpleName();

	@Override
	public void perform(String action, HttpServletRequest request, HttpServletResponse response, ServletContext ctx)
			throws Exception {
	       try{
	        	RulesFactoryImpl rulesfactory= new RulesFactoryImpl();
	        	if(request.getParameter("rules")!=null)    		rulesaction = request.getParameter("rules");
	        	     
	        	if(rulesaction!=null){
	        		//request.setAttribute("lastrules", rulesaction);
	    				rules = rulesfactory.createRule(rulesaction);
	    				rules.performOperation(rulesaction,request , response , ctx);
	        	}else{
				    response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getErrorPage()).forward(request, response);
					throw new Exception ("rules is null ");
	        	}       		
	  	    } catch (Exception e){
	  	    	PPWalletEnvironment.setComment(1,className,"Exception is "+e.getMessage());
			    response.setContentType("text/html");
				ctx.getRequestDispatcher(PPWalletEnvironment.getErrorPage()).forward(request, response);
			}
	}

	@Override
	public Object respond(String action, String param, Object obj, Object obj1) throws Exception {
		return null;
	}

	@Override
	public void performJSON(JsonObject jsonObj, HttpServletRequest request, HttpServletResponse response,
			ServletContext context) throws Exception {
	       try
		    {
	        	RulesFactoryImpl rulesfactory= new RulesFactoryImpl();	        	
        		rulesaction = jsonObj.get("rules").toString().replaceAll("\"", "");
 
	        	if(rulesaction!=null){
	    				rules = rulesfactory.createRule(rulesaction);
	    				rules.performJSONOperation(rulesaction,request , response , context,jsonObj);
	        	}else{
			    	throw new Exception ("rules is null ");
	        	}       		
	  	    } catch (Exception e){
	  	    	PPWalletEnvironment.setComment(1,className,"Exception is "+e.getMessage());
			}
	}

}

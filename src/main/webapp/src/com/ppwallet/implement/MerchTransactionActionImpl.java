package com.ppwallet.implement;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.rules.Rules;
import com.ppwallet.rules.RulesFactoryImpl;

public class MerchTransactionActionImpl implements Action {
	private String rulesaction = null;
	private Rules rules;
	private static String classname = MerchTransactionActionImpl.class.getSimpleName();

	@Override
	public void perform(String action, HttpServletRequest request, HttpServletResponse response, ServletContext ctx)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Object respond(String action, String param, Object obj, Object obj1) throws Exception {
		// TODO Auto-generated method stub
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
  	    	PPWalletEnvironment.setComment(1,classname,"Exception is "+e.getMessage());
		}
	}

}

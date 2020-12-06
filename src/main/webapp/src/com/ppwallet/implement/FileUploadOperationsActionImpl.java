package com.ppwallet.implement;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.rules.Rules;
import com.ppwallet.rules.RulesFactoryImpl;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class FileUploadOperationsActionImpl implements Action {
	private String rulesaction = null;
	private Rules rules;
	private static String classname = FileUploadOperationsActionImpl.class.getSimpleName();

	@SuppressWarnings("unused")
	@Override
	public void perform(String action, HttpServletRequest request, HttpServletResponse response, ServletContext ctx)
			throws Exception {
	       try{
	    	   
	    
	    	   String actionName = null;
	    	   if(request.getHeader("Content-type")!=null && request.getContentType().toLowerCase().indexOf("multipart/form-data") > -1 )  {	
	    	   
	    		   List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
	    		  
	    		    for (FileItem item : multiparts) {
	    		        if (item.isFormField()) {
	    		        	if(item.getFieldName().equals("rules")){
//	    		        		PPWalletEnvironment.setComment(3,classname," in try N");
	    		        		rulesaction = item.getString().trim();
	    		        		RulesFactoryImpl rulesfactory= new RulesFactoryImpl();	  
	    	    				rules = rulesfactory.createRule(rulesaction);  
	   
	    	    				PPWalletEnvironment.setComment(3,classname,"rulesaction is "+ rulesaction);  
	    	    				
	    	    				rules.performUploadOperation(rulesaction,request , response ,multiparts, ctx);
				            	}
	    		        	if(item.getFieldName().equals("qs"))  {    		
	    		        		actionName = item.getString().trim();
	    	    		    	if(actionName.equals("fud")==false)    		throw new Exception ("Invalid Entry, the value of qs is not provided or authorized");
	    		        	}
	    		        } 
	    		}
	    	   }else {
	    		   throw new Exception("Invalid call");
	    	   }       		
	  	    } catch (Exception e){
	  	    	PPWalletEnvironment.setComment(1,classname,"Exception is "+e.getMessage());
			    response.setContentType("text/html");
				ctx.getRequestDispatcher(PPWalletEnvironment.getErrorPage()).forward(request, response);
			}
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
	        		rulesaction = jsonObj.get("rules").toString();

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

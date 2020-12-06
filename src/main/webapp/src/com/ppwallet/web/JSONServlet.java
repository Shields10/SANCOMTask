package com.ppwallet.web;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.implement.Action;
import com.ppwallet.implement.ActionFactoryImpl;

/**
 * Servlet implementation class JSONServlet
 */
@WebServlet(description = "JSON Controller of request", urlPatterns = { "/json" })
public class JSONServlet extends HttpServletJXGB implements Servlet {
	private static final long serialVersionUID = 1L;
    private ServletContext context = null;
  private String className = JSONServlet.class.getSimpleName();         
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JSONServlet() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		//System.out.println("============== starting :" +className + " at "+java.time.LocalTime.now());

		try {
			if(PPWalletEnvironment.getInstance()==null)
				PPWalletEnvironment.init();
		} catch (Exception e) {
			System.out.println(" ************** CRITICAL ERROR : Failed to initialize core Environment parameters *************");	
			
		}
		super.init(config);	
		PPWalletEnvironment.setComment(3,className,"============== starting :" +className + " at "+java.time.LocalTime.now());

	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = null; String jsonString = null; JsonObject jsonObj = null;
		try{
			this.context = (ServletContext)getServletContext();
		//	PPWalletEnvironment.setComment(3,className," inside service before getting parameter "+request.getParameterNames().toString() );
			if(request.getParameter("objarray")!=null){
					jsonString = request.getParameter("objarray").trim();
					//PPWalletEnvironment.setComment(3,className," method service : jsonString = "+jsonString );
			}else	throw new Exception ("Missing JSON String.. cannot progress further");
			
					jsonObj = new Gson().fromJson(jsonString, JsonObject.class);
					// Check the API String
				//	PPWalletEnvironment.setComment(3,className," apikey is "+jsonObj.get("apikey").toString().replaceAll("\"", "") + " and system API is "+PPWalletEnvironment.getAPIKeyPublic());

					if(jsonObj.get("apikey")!=null && ( jsonObj.get("apikey").toString().replaceAll("\"", "").equals(PPWalletEnvironment.getAPIKeyPublic()) )) {
						//response.setStatus(HttpServletResponse.SC_ACCEPTED); // Status code (202) indicating that a request was accepted for processing, but was not completed.
				//		PPWalletEnvironment.setComment(3,className,"APIKEY accepted "+jsonObj.get("apikey").toString());
					}else {
						response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
						throw new Exception ("API String is not valid ");
					}
					action = jsonObj.get("qs").toString().replaceAll("\"", "");
					//PPWalletEnvironment.setComment(3,className," method service : action = "+action+" and rules is "+ jsonObj.get("rules").toString());
					if((delegateJSONAction(action, request, response, context))==false){
						throw new Exception (className+" CRITICAL ERROR: delegateAction failed...");
					}					
		}catch(Exception e){
			PPWalletEnvironment.setComment(1,className," method service : Problem with delegate action.. action = "+action+" Error : "+e.getMessage() );
		}
	}

	private boolean delegateJSONAction(String action, HttpServletRequest request, HttpServletResponse response, ServletContext context) throws Exception
    {
	       Action actionRef = null;
	       boolean result = false;
	       if(action != null) {
	       try {
	    	  // PPWalletEnvironment.setComment(3,className," Inside delegateJSONAction action = "+action );
	    	   actionRef = new ActionFactoryImpl().createAction(action); 
	           request.setAttribute("lastaction", action);                 
	           actionRef.performJSON(new Gson().fromJson(request.getParameter("objarray").trim(), JsonObject.class),request, response, context);
	           result=true;
	       
	       	}catch(Exception ee){
				result = false;
				PPWalletEnvironment.setComment(1,className," Exception is  "+ee.getMessage()  );
				}
	       }else{
	    	   throw new Exception (className+" Exception in delegateAction, action is "+action);       
	       }   	   
		
	       return result;

    }	
	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		super.destroy();
	}

}

package com.ppwallet.web;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.implement.Action;
import com.ppwallet.implement.ActionFactoryImpl;

/**
 * Servlet implementation class MultipartServlet
 */
@WebServlet("/MultipartServlet")
public class MultipartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private ServletContext context = null;
	private Action actionRef = null;
	private final static String MULTIPART_REQUEST= "fud";
    private String classname = MultipartServlet.class.getSimpleName();  
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MultipartServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			this.context = (ServletContext)getServletContext();
			//	session	= request.getSession(false);
				PPWalletEnvironment.setComment(3,classname," Inside multi ");
				//if(session.getAttribute("SESS_USER")==null)
				//	throw new Exception ("Session is expired");
							
			if((delegateAction(MULTIPART_REQUEST, request, response, context))==false){
				throw new Exception (classname+" CRITICAL ERROR: delegateAction failed...");
				//CDSCustodyAgentEnvironment.setComment(3,classname," Inside "  );
			}

		} catch (Exception e) {
			PPWalletEnvironment.setComment(3,classname," Exception is  "+e.getMessage()  );
    	 	response.setContentType("text/html");
 			try {
				context.getRequestDispatcher("/"+PPWalletEnvironment.getServletPath()+"?qs=err").forward(request, response);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 				
	}
	private boolean delegateAction(String action, HttpServletRequest request, HttpServletResponse response, ServletContext context) throws Exception
    {
	      //Action actionRef = null;
	       boolean result = false;
	       if(action != null) {
	       try {
	    	   actionRef = new ActionFactoryImpl().createAction(action); 
	                 request.setAttribute("lastaction", action);
	                   actionRef.perform(action,request, response, context);
	                   result=true;
	       
	       	}catch(Exception ee){
				result = false;
				PPWalletEnvironment.setComment(3,classname," Exception is  "+ee.getMessage()  );
				}
	       }else{
	    	   throw new Exception (classname+" Exception in delegateAction, action is "+action);       
	    	   }   	   
			return result;
    }

}

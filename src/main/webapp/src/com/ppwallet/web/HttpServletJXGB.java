package com.ppwallet.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.MySqlPoolableObjectFactory;

/**
 * Servlet implementation class HttpServletJXGB
 */
@WebServlet("/HttpServletJXGB")
public class HttpServletJXGB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private  static String className = HttpServletJXGB.class.getSimpleName();
	public static MySqlPoolableObjectFactory dbConnPool = null; // The Tomcat DB Connection POOL 
	 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HttpServletJXGB() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
//		System.out.println("============== starting :" +className + " at "+java.time.LocalTime.now());
		super.init(config);
		// ******* Initialize the Connection Pool	
		PPWalletEnvironment.setComment(3,className,"============== starting :" +className + " at "+java.time.LocalTime.now());

		if(dbConnPool == null){ // Only created by first servlet to call
			try {
				//Initializes the connection broker which handles connection in the connection pool.
				dbConnPool = new MySqlPoolableObjectFactory();
				MySqlPoolableObjectFactory.init();	
				PPWalletEnvironment.setComment(3,className,"Inside init method of HttpServletJXGB...dbConnPool formed "+MySqlPoolableObjectFactory.checkConnPoolformed());										
		}catch (Exception e) {
			PPWalletEnvironment.setComment(1,className,"Internal error formed during the ConnectionPool formation "+e.getMessage());
				throw new ServletException("An internal error has occurred,please retry");
			}			
		}
		}
		

		// ****** Initialize the Server socket
/*		try {
			
			if(ServerManager.INSTANCE.listServersRunning().isEmpty()==true) {
			String responseFromServer = null;
			//TODO change the Stencil Name
			responseFromServer = ServerManager.INSTANCE.startServer("Stencil_Name", PPWalletEnvironment.getServerSocketIP(), Integer.parseInt(PPWalletEnvironment.getServerSocketPort()));
		//Thread.sleep(1000);
			PPWalletEnvironment.setComment(3,className, "Server response "+responseFromServer);
			if(ServerManager.INSTANCE.listServersRunning().isEmpty()==false) {
				List<String> servers = ServerManager.INSTANCE.listServersRunning();
				StringBuffer sb = new StringBuffer();
				sb.append("server running on " + InetAddress.getLocalHost());
				for (String server: servers) {
					sb.append("\n" + "Server:" + server + "," + "address=" + ServerManager.INSTANCE.getServerPort(server)); 
				}
				PPWalletEnvironment.setComment(3,className, sb.toString());	
			}
			
		}
		} catch (Exception e) {
			PPWalletEnvironment.setComment(1,className,"Internal error formed during the ServerManager formation "+e.getMessage());
		}*/
	
	public void destroy() {
		 try {
			 if(MySqlPoolableObjectFactory.checkConnPoolformed()) {
			 MySqlPoolableObjectFactory.shutdownDriver();
			 PPWalletEnvironment.setComment(3,className,"Connection Pool shutting down..........."+MySqlPoolableObjectFactory.checkConnPoolformed());
			 }
		 	} catch (Exception e) {
			PPWalletEnvironment.setComment(1,className, "Problem with shutting of Connection Pool  "+e.getMessage());			
			}

		 
		 
		 
		 
	}
		
	}



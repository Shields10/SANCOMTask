package com.ppwallet.implement;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonObject;

public interface Action {
	 
	 public void perform(String action, HttpServletRequest request, HttpServletResponse response,ServletContext ctx) throws Exception;
	
	 
	 public Object respond(String action, String param, Object obj, Object obj1) throws Exception;
	 
	 
	 public void performJSON(JsonObject jsonObj, HttpServletRequest request, HttpServletResponse response,
				ServletContext context) throws Exception;

}

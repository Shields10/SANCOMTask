package com.example.demo.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {
	private static String API_KEY="W3ZJKULZZSNP3YMUX24A";
	
 	public static  String getAPIKEY() throws Exception{ 			return API_KEY; }	
 	
 	  public static String getCurrentTimeandDate() throws Exception{
       	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");     	        
       	 java.util.Date date = new Date();      	             	        
       	return formatter1.format(date);
       	}
}

package com.ppwallet;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ppwallet.model.Token;
import com.ppwallet.security.AESEncrypter;
import com.ppwallet.utilities.Utilities;
/*
How to Read Json parameter file. THis is a standalone Java file and should not be used at all.
*/	
@SuppressWarnings("unused")
public class JsonRead {

	public static void main(String[] args) throws Exception{
		JSONParser parser = new JSONParser();
				 //System.out.println("Following are the JVM information of your OS :");     System.out.println("");
	       Properties jvm = System.getProperties();
	       // jvm.list(System.out);
		//System.out.print(System.getProperties().propertyNames().toString()+"\n");
		JSONObject jsonObjectRead = null;
		//SimpleDateFormat formatter2 = new SimpleDateFormat ("ddMMyy");
		//java.util.Date date = new Date();
		
		try {
		//	String dateString =  "PPWallet"+formatter2.format(date);
		//	String encryptString = Utilities.asciiToHex(AESEncrypter.encryptJson(dateString));
     //    System.out.println("\n *** Now Reading the file**** ");
     //    Object objRead = parser.parse(new FileReader(StringUtils.replace(System.getProperty("java.home"), "\\", "/")+"/PPWalletApplicationParameters.json"));
         Object objRead = parser.parse(new FileReader("D:/apache-tomcat-9.0.31/PPWalletApplicationParameters.json"));
         jsonObjectRead = (JSONObject) objRead;   
       // System.out.println("\n MULTIURLPORT : " + AESEncrypter.decryptJson((String)jsonObjectRead.get("MULTIURLPORT")) );
       // System.out.println("\n MULTIPORT : " + AESEncrypter.decryptJson((String)jsonObjectRead.get("MULTIPORT")) );
        //System.out.println("hex string is "+ encryptString);
       // System.out.println("today date is "+ AESEncrypter.decryptJson(Utilities.hexToASCII(encryptString)));

        //System.out.println("Key is "+PPWalletEnvironment.tempKey());
      //  System.out.println("Encryption is  is "+ AESEncrypter.encryptSecure("test", PPWalletEnvironment.tempKey()));
       //  if(objRead!=null)	objRead=null;
			/*
			 * String temp1 = "abc"; String temp2 = "def"; String temp3 = "efg"; String
			 * temp4 = "pqr"; String temp5 = "xyz"; String jsonString =
			 * "{\"tokendetails\": " + "{ \"tokenid\": \""+ temp1 +"\", " +
			 * "\"cardnumber\":\""+ temp2 +"\", " + "\"cardname\":\""+ temp3 +"\", " +
			 * "\"dateofexpiry\": \""+ temp4 +"\", " + "\"urelno\" : \""+ temp5 +"\", " +
			 * "\"utype\":\""+ temp3 +"\" " + "}}";
			 */			
			//System.out.println("jsonString is  "+jsonString);
			/*
			 * for (int i=0;i<100;i++) { SimpleDateFormat formatter2 = new SimpleDateFormat
			 * ("yyMMdd"); String tempRelationshipNo = formatter2.format(new
			 * java.util.Date()) + RandomStringUtils.random(10, false, true);
			 * System.out.println((i+1)+" ---> is  "+tempRelationshipNo); }
			 */
        } catch (Exception e) {
        	System.out.println("Exception is "+e.getMessage());
        	
        }finally{
        	
        	if(jsonObjectRead!=null) jsonObjectRead = null;
        }
	}
	


}

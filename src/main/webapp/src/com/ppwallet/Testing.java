package com.ppwallet;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ppwallet.model.CustomerTemp;
import com.ppwallet.model.Token;
import com.ppwallet.security.AESEncrypter;
import com.ppwallet.utilities.Utilities;
/*
How to Read Json parameter file. THis is a standalone Java file and should not be used at all.
*/	

public class Testing {

	public static void main(String[] args) throws Exception{
//		NumberFormat nf = NumberFormat.getNumberInstance();
//		nf.setMaximumFractionDigits(0);
//		String rounded = nf.format(Double.parseDouble("34.99329"));
//		     
//	      System.out.println(  " rounded is "+ rounded );
		
		String  test = "abc,xyz";	String custEmail = "test";	String custName = "%%%\rtestname%";	String custPhoneNumber = "%testemail%\\n";
		//walletId = claimedWalletId.substring(0, claimedWalletId.indexOf(",", 0));
		
		 String unescapedSQL = "select a.relationshipno relationshipno, a.customername customername, a.custemail custemail, a.custcontact custcontact,  "
					+ " b.walletid walletid from customer_details a, wallet_details b where a.relationshipno=b.relationshipno and a.status='A' and b.status='A' "
					+ "  and a.custemail like '%"+escapeString( custPhoneNumber, true)+"%' and ";


	     System.out.println("Sql String is  :" +unescapedSQL);
	     
	     //System.out.println("Return Value :" +test.substring( (test.indexOf(",", 0))+1, test.length()));
	     // System.out.println(original.regionMatches(11, match2, 0, 9));
		
		try {
		 		//System.out.println("Response after Blockchain addition is ");	
        } catch (Exception e) {
        	System.out.println("Exception is "+e.getMessage());
        	
        }finally{
			try {
			}catch (Exception ee) {
				System.out.println("The exception in method insertIntoCardVault, finally block is  "+ee.getMessage());
			}       	
        }
	}

	

	    public static String escapeString(String x, boolean escapeDoubleQuotes) throws Exception {
	    	String returnString = null;StringBuilder sBuilder = null;
	    	try {
	        sBuilder = new StringBuilder(x.length() * 11/10);

	        int stringLength = x.length();

	        for (int i = 0; i < stringLength; ++i) {
	            char c = x.charAt(i);

	            switch (c) {
	            case 0: /* Must be escaped for 'mysql' */
	                sBuilder.append('\\');
	                sBuilder.append('0');

	                break;

	            case '\n': /* Must be escaped for logs */
	                sBuilder.append('\\');
	                sBuilder.append('n');

	                break;

	            case '\r':
	                sBuilder.append('\\');
	                sBuilder.append('r');

	                break;

	            case '\\':
	                sBuilder.append('\\');
	                sBuilder.append('\\');

	                break;

	            case '\'':
	                sBuilder.append('\\');
	                sBuilder.append('\'');

	                break;

	            case '"': /* Better safe than sorry */
	                if (escapeDoubleQuotes) {
	                    sBuilder.append('\\');
	                }

	                sBuilder.append('"');

	                break;

	            case '\032': /* This gives problems on Win32 */
	                sBuilder.append('\\');
	                sBuilder.append('Z');

	                break;

	            case '\u00a5':
	            case '\u20a9':
	                // escape characters interpreted as backslash by mysql
	                // fall through

	            default:
	                sBuilder.append(c);
	            }
	        }
	        returnString = sBuilder.toString();
	    	}catch(Exception e) {
	    		throw new Exception (e.getMessage());
	    	}finally {
	        
	        sBuilder = null;
	    	}
	        return returnString;
	    }
		
	
}

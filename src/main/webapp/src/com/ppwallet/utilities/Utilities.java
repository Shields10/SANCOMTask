package com.ppwallet.utilities;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;

import com.ppwallet.security.AESEncrypter;
public class Utilities {
	private static String classname = Utilities.class.getSimpleName();
	private  final static byte[] ASCII2EBCDIC = new byte[] { (byte) 0x00, (byte) 0x01,
	(byte) 0x02, (byte) 0x03, (byte) 0x37, (byte) 0x2D, (byte) 0x2E,(byte) 0x2F, (byte) 0x16, (byte) 0x05, (byte) 0x25, (byte) 0x0B,(byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F, (byte) 0x10,(byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x3C, (byte) 0x3D,
	(byte) 0x32, (byte) 0x26, (byte) 0x18, (byte) 0x19, (byte) 0x3F,(byte) 0x27, (byte) 0x1C, (byte) 0x1D, (byte) 0x1E, (byte) 0x1F,(byte) 0x40, (byte) 0x5A, (byte) 0x7F, (byte) 0x7B, (byte) 0x5B,(byte) 0x6C, (byte) 0x50, (byte) 0x7D, (byte) 0x4D, (byte) 0x5D,
	(byte) 0x5C, (byte) 0x4E, (byte) 0x6B, (byte) 0x60, (byte) 0x4B,(byte) 0x61, (byte) 0xF0, (byte) 0xF1, (byte) 0xF2, (byte) 0xF3,(byte) 0xF4, (byte) 0xF5, (byte) 0xF6, (byte) 0xF7, (byte) 0xF8,(byte) 0xF9, (byte) 0x7A, (byte) 0x5E, (byte) 0x4C, (byte) 0x7E,
	(byte) 0x6E, (byte) 0x6F, (byte) 0x7C, (byte) 0xC1, (byte) 0xC2,(byte) 0xC3, (byte) 0xC4, (byte) 0xC5, (byte) 0xC6, (byte) 0xC7,(byte) 0xC8, (byte) 0xC9, (byte) 0xD1, (byte) 0xD2, (byte) 0xD3,(byte) 0xD4, (byte) 0xD5, (byte) 0xD6, (byte) 0xD7, (byte) 0xD8,
	(byte) 0xD9, (byte) 0xE2, (byte) 0xE3, (byte) 0xE4, (byte) 0xE5,(byte) 0xE6, (byte) 0xE7, (byte) 0xE8, (byte) 0xE9, (byte) 0xBA,(byte) 0xE0, (byte) 0xBB, (byte) 0xB0, (byte) 0x6D, (byte) 0x79,(byte) 0x81, (byte) 0x82, (byte) 0x83, (byte) 0x84, (byte) 0x85,
	(byte) 0x86, (byte) 0x87, (byte) 0x88, (byte) 0x89, (byte) 0x91,(byte) 0x92, (byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96,(byte) 0x97, (byte) 0x98, (byte) 0x99, (byte) 0xA2, (byte) 0xA3,(byte) 0xA4, (byte) 0xA5, (byte) 0xA6, (byte) 0xA7, (byte) 0xA8,
	(byte) 0xA9, (byte) 0xC0, (byte) 0x4F, (byte) 0xD0, (byte) 0xA1,(byte) 0x07, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F,(byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F,(byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F,
	(byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F,(byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F,(byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x3F,(byte) 0x3F, (byte) 0x3F, (byte) 0x3F, (byte) 0x41, (byte) 0xAA,
	(byte) 0x4A, (byte) 0xB1, (byte) 0x9F, (byte) 0xB2, (byte) 0x6A,(byte) 0xB5, (byte) 0xBD, (byte) 0xB4, (byte) 0x9A, (byte) 0x8A,(byte) 0x5F, (byte) 0xCA, (byte) 0xAF, (byte) 0xBC, (byte) 0x90,(byte) 0x8F, (byte) 0xEA, (byte) 0xFA, (byte) 0xBE, (byte) 0xA0,
	(byte) 0xB6, (byte) 0xB3, (byte) 0x9D, (byte) 0xDA, (byte) 0x9B,(byte) 0x8B, (byte) 0xB7, (byte) 0xB8, (byte) 0xB9, (byte) 0xAB,(byte) 0x64, (byte) 0x65, (byte) 0x62, (byte) 0x66, (byte) 0x63,(byte) 0x67, (byte) 0x9E, (byte) 0x68, (byte) 0x74, (byte) 0x71,
	(byte) 0x72, (byte) 0x73, (byte) 0x78, (byte) 0x75, (byte) 0x76,(byte) 0x77, (byte) 0xAC, (byte) 0x69, (byte) 0xED, (byte) 0xEE,(byte) 0xEB, (byte) 0xEF, (byte) 0xEC, (byte) 0xBF, (byte) 0x80,(byte) 0xFD, (byte) 0xFE, (byte) 0xFB, (byte) 0xFC, (byte) 0xAD,
	(byte) 0xAE, (byte) 0x59, (byte) 0x44, (byte) 0x45, (byte) 0x42,(byte) 0x46, (byte) 0x43, (byte) 0x47, (byte) 0x9C, (byte) 0x48,(byte) 0x54, (byte) 0x51, (byte) 0x52, (byte) 0x53, (byte) 0x58,(byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x8C, (byte) 0x49,
	(byte) 0xCD, (byte) 0xCE, (byte) 0xCB, (byte) 0xCF, (byte) 0xCC,(byte) 0xE1, (byte) 0x70, (byte) 0xDD, (byte) 0xDE, (byte) 0xDB,(byte) 0xDC, (byte) 0x8D, (byte) 0x8E, (byte) 0xDF };

	
	static final String RNDSTRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static SecureRandom rnd = new SecureRandom();
	private static final String NUMBERS = "0123456789";
	private static final String UPPER_ALPHABETS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWER_ALPHABETS = "abcdefghijklmnopqrstuvwxyz";
	private static final String SPECIALCHARACTERS = "@#$%&*";
	private static final int MINLENGTHOFPASSWORD = 8;

 public static String replaceString(String text, String find, String replace) {
     int findLength = find.length();
     StringBuffer buffer = new StringBuffer();
     int i;
     for (i = 0; i < text.length() - find.length() + 1; i++) {
         String substring = text.substring(i, i + findLength);
         if (substring.equals(find)) {
             buffer.append(replace);
             i += find.length() - 1;
         } else {
             buffer.append(text.charAt(i));
         }
     }
     buffer.append(text.substring(text.length() - (text.length() - i)));
     return buffer.toString();
 }

 /**
  * Returns any trailing . , ; : characters on the given string
  * @param text
  * @return empty string if none are found
  */
 public static String extractTrailingPunctuation(String text) {
     StringBuffer buffer = new StringBuffer();
     for (int i = text.length() - 1; i >= 0; i--) {
         char c = text.charAt(i);
         if (c == '.' || c == ';' || c == ',' || c == ':') {
             buffer.append(c);
         } else {
             break;
         }
     }
     if (buffer.length() == 0) return "";
     buffer = buffer.reverse();
     return buffer.toString();
 }

 	public static String  encryptJSString(String string){
 	//string=replaceIgnoreCase(string," ","%20");
 	string=replaceString(string,"&","%26");
 	string=replaceString(string,"+","%2B");
 	string=replaceString(string,"?","%3F");
 	string=replaceString(string,"\"","%34");
 	string=replaceString(string,"'","%39");

 	return string;
 	}
 	public static String  decryptJSString(String string){
 	//string=replaceIgnoreCase(string,"%20"," ");
 	string=replaceString(string,"%26","&");
 	string=replaceString(string,"%2B","+");
 	string=replaceString(string,"%3F","?");
 	string=replaceString(string,"%34","\"");
 	string=replaceString(string,"%39","\\'");
 	return string;
 	}

    	public static String  sqlEncode(String string){
     	string=replaceString(string,"'","/'");
     	string=replaceString(string,"//","///");
     	return string;
     	}
    	
       public static String getMYSQLCurrentTimeStampForInsert() throws Exception{
          	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");     	        
          	 java.util.Date date = new Date();      	             	        
          	return formatter1.format(date);
          	}
    	public static String getMySQLDateTimeConvertor(String datetimestring) throws Exception{
    	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");     	        
    	 java.util.Date date = formatter1.parse(datetimestring);      	        
    	 SimpleDateFormat formatter2 = new SimpleDateFormat ("dd-MMM-yyyy HH:mm:ss");      	        
    	return formatter2.format(date);

    	}
    	
 	public static String getUTCMySQLDateTime(String datetimestring) throws Exception{
       	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");  formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));  	        
       	 java.util.Date date = formatter1.parse(datetimestring);      	        
       	return formatter1.format(date);
       	}
 	
 	public static String getUTCMySQLDateTime(Date date) throws Exception{
      	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");  formatter1.setTimeZone(TimeZone.getTimeZone("UTC"));  	        
      	return formatter1.format(date);
      	}


    	public static String getMySQLDateConvertor(String datestring) throws Exception{
    	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd");    
    	 java.util.Date date = formatter1.parse(datestring);
    	 SimpleDateFormat formatter2 = new SimpleDateFormat ("dd-MMM-yyyy");
    	 return formatter2.format(date);

    	}

  	public static String convertDatetoMySQLDateFormat(java.util.Date date) throws Exception{
       	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd");    
       	 return formatter1.format(date);

       	}
  	public static String getMoneyinDecimalFormat(String toformat) throws ParseException{
  		DecimalFormat moneyFormat = new DecimalFormat("#,###,##0.00");
  		return moneyFormat.format(Double.parseDouble(toformat)).toString();
        	}
  	public static String getMoneyinSimpleDecimalFormat(String toformat) throws ParseException{
  		DecimalFormat moneyFormat = new DecimalFormat("######0.00");
  		return moneyFormat.format(Double.parseDouble(toformat)).toString();
        	}

  	public static String getMoneyinNoDecimalFormat(String toformat) throws ParseException{
  		DecimalFormat moneyFormat = new DecimalFormat("#,###,##0");
  		return moneyFormat.format(Double.parseDouble(toformat)).toString();
        	}	
  	public static String getUTCtoISTDateTimeConvertor(String dateTimeInUTCFormat) throws Exception{

  		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

  		Date date = utcFormat.parse(dateTimeInUTCFormat);

  		DateFormat pstFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
  		pstFormat.setTimeZone(TimeZone.getTimeZone("IST"));

  		return (pstFormat.format(date));

       	} 	
  	public static String getUTCtoYourTimeZoneConvertor(String dateTimeInUTCFormat, String yourTimeZone) throws Exception{

  		DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

  		Date date = utcFormat.parse(dateTimeInUTCFormat);

  		DateFormat pstFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		pstFormat.setTimeZone(TimeZone.getTimeZone(yourTimeZone));

  		return (pstFormat.format(date));

       	}  	
  	
  	public static String getUTCDateTimeConvertor(String datetimestring, String formatting) throws Exception{
			if(formatting.equals("")){
				formatting = "MMddHHmmss";
			}
			
      	 SimpleDateFormat formatter1 = new SimpleDateFormat (formatting);  // Assuming this is in UTC
      	 formatter1.setTimeZone(TimeZone.getTimeZone("IST"));
      	 java.util.Date date = formatter1.parse(datetimestring);  
      	 SimpleDateFormat formatter2 = new SimpleDateFormat (formatting); 
      	 formatter2.setTimeZone(TimeZone.getTimeZone("UTC"));
      	
      	return formatter2.format(date);

       	}
  	
  	public static String covertAnyDateFormatToMysqlDateFormat(String datestring, String userFormat, String mysqldateFormat, String systemTimeZone) throws Exception{
  		DateFormat inputFormat = null; 		// e.g. dd/MM/yyyy
  		Date date1 = null;	
  		DateFormat mysqlFormat = null;	// e.g. yyyy-MM-dd
  		String coversionFormat = null;
  		try {
		  		inputFormat = new SimpleDateFormat(userFormat);
		  		inputFormat.setTimeZone(TimeZone.getTimeZone(systemTimeZone));
		  		date1 = inputFormat.parse(datestring);
		  		mysqlFormat = new SimpleDateFormat(mysqldateFormat);
		  		mysqlFormat.setTimeZone(TimeZone.getTimeZone(systemTimeZone));
		  		coversionFormat =  mysqlFormat.format(date1);
		  }catch (Exception e) {
  			throw new Exception ("Exception is "+e.getMessage());
  		}
  		return coversionFormat;
       	} 	
  	
	public static String getPass(String trim) {	return  StringUtils.reverse(StringUtils.replace(AESEncrypter.unRavel(trim.trim()),"&",""));}
	public static  String getKey_02(String input_1) {return input_1+Integer.toString(10-1)+(AESEncrypter.getKey_03((StringUtils.reverse(StringUtils.substring(classname, 0,4)))));}

	public static synchronized int getRandomNumber(int min, int max) throws Exception{
		return (ThreadLocalRandom.current().nextInt(min, max + 1));	
	}

	public static synchronized boolean isValidCreditCardNumber(String creditCardNumber) {
		boolean isValid = false;
		try {
			//String reversedNumber = new StringBuffer(creditCardNumber).reverse().toString();
			String reversedNumber = StringUtils.reverse(creditCardNumber);
			int mod10Count = 0;
			for (int i = 0; i < reversedNumber.length(); i++) {
				int augend = Integer.parseInt(String.valueOf(reversedNumber.charAt(i)));
				if (((i + 1) % 2) == 0) {
					String productString = String.valueOf(augend * 2);
					augend = 0;
					for (int j = 0; j < productString.length(); j++) {
						augend += Integer.parseInt(String.valueOf(productString.charAt(j)));
					}
				}
				mod10Count += augend;
			}
			if ((mod10Count % 10) == 0) {
				isValid = true;
			}
		} catch (NumberFormatException e) {
		}
		return isValid;
	}

   	/**
 	 * Applies the specified mask to the card number.
 	 *
 	 * @param cardNumber The card number in plain format
 	 * @param mask The number mask pattern. Use # to include a digit from the
 	 * card number at that position, use x to skip the digit at that position
 	 *
 	 * @return The masked card number
 	 *  for card number "1234123412341234" > 1234-xxxx-xxxx-xx34
 	 */
 	public static String maskCardNumber(String cardNumber) {
 	    // format the number	SYTXkq38KSVQS6rN6wzRFrLyeXSCs7XsHq2Aht9E1Po=
 		String mask="##xx-xx##-xx##-xx##";
 	    int index = 0;
 	    StringBuilder maskedNumber = new StringBuilder();
 	    for (int i = 0; i < mask.length(); i++) {
 	        char c = mask.charAt(i);
 	        if (c == '#') {
 	            maskedNumber.append(cardNumber.charAt(index));
 	            index++;
 	        } else if (c == 'x') {
 	            maskedNumber.append(c);
 	            index++;
 	        } else {
 	            maskedNumber.append(c);
 	        }
 	    }
 	    // return the masked number
 	    return maskedNumber.toString();
 	}

 	/*
 	 * It is done in following steps:
    Convert String to char array
    Cast it to Integer
    Use Integer.toHexString() to convert it to Hex
 	 */   			
 	public static String asciiToHex(String asciiValue)
 	{
 	    char[] chars = asciiValue.toCharArray();
 	    StringBuffer hex = new StringBuffer();
 	    for (int i = 0; i < chars.length; i++)
 	    {
 	        hex.append(Integer.toHexString((int) chars[i]));
 	    }
 	    return hex.toString();
 	}
 	/*
 	 * It is done in following steps:
    Cut the Hex value in 2 chars groups
    Convert it to base 16 Integer using Integer.parseInt(hex, 16) and cast to char
    Append all chars in StringBuilder     	 */   			
 	public static String hexToASCII(String hexValue)
 	{
 	    StringBuilder output = new StringBuilder("");
 	    for (int i = 0; i < hexValue.length(); i += 2)
 	    {
 	        String str = hexValue.substring(i, i + 2);
 	        output.append((char) Integer.parseInt(str, 16));
 	    }
 	    return output.toString();
 	}
 	
 	public static String ebcdicToASCII(String edata) throws Exception{
 	    String ebcdic_encoding = "IBM-1047"; //Setting the encoding in which the source was encoded
 	    byte[] result = edata.getBytes(ebcdic_encoding); //Getting the raw bytes of the EBCDIC string by mentioning its encoding
 	    String output = asHex(result); //Converting the raw bytes into hexadecimal format
 	    byte[] b = new BigInteger(output, 16).toByteArray(); //Now its easy to convert it into another byte array (mentioning that this is of base16 since it is hexadecimal)
 	    String ascii = new String(b, "ISO-8859-1"); //Now convert the modified byte array to normal ASCII string using its encoding "ISO-8859-1"
 		return ascii;
 	}
 	public static String asciiToEBCDIC(String adata) throws Exception{
 	    String ascii_encoding = "ISO-8859-1";
 	    byte[] res = adata.getBytes(ascii_encoding);
 	    String out = asHex(res);
 	    byte[] bytebuff = new BigInteger(out, 16).toByteArray();
 	    String ebcdic = new String(bytebuff, "IBM-1047");
 	    return ebcdic;     		
 	}
	/** 
	 * This is used to convert ASCII to EBCDIC format 	
	 */
	public static String asciiToEBCDIC(byte[] a) {
		byte[] e = new byte[a.length];
		for (int i = 0; i < a.length; i++)
			e[i] = ASCII2EBCDIC[a[i] & 0xFF];
		return new String(e);
	}     	
 	
 	//This asHex method converts the given byte array to a String of Hexadecimal equivalent
 	public static String asHex(byte[] buf) {
 	    char[] HEX_CHARS = "0123456789abcdef".toCharArray();
 	    char[] chars = new char[2 * buf.length];
 	    for (int i = 0; i < buf.length; ++i) {
 	        chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
 	        chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
 	    }
 	    return new String(chars);
 	}
 	public static char[] hexStringToByte(String s) throws Exception {

		int i = s.length();
		int j = i / 2;
		char abyte0[] = new char[j];
		int k = 0;	
		int l = 0;
			for (k = 0; k < i; k += 2) {
				int byte0 = Integer.parseInt(s.substring(k, k + 2), 16);
				abyte0[l] = (char) byte0;
				l++;
			}
		
		return abyte0;

	}
 	public static String createSingleCard (String strbin) throws Exception{
		StringBuilder builder = null;
		int  totalcardlength = 16;
		Random random = new Random(System.currentTimeMillis());
	        try {
						builder = null;
							 int randomNumberLength = totalcardlength - (strbin.length() + 1);
							 builder = new StringBuilder(strbin);
								for (int i = 0; i < randomNumberLength; i++) {
									int digit = random.nextInt(10);
									builder.append(digit);
								}
							int checkDigit = getCheckDigit(builder.toString());
				            builder.append(checkDigit);
							 
	        } catch (Exception e) {
	            throw new Exception(" Method createSingleCard: Error in creating card number",e);
	        }

 		return builder.toString();
 	}
 	public static ArrayList<String> createMultipleCards (String strbin,String totalcards) throws Exception{
 		ArrayList<String> arrCards = null;
		StringBuilder builder = null;
		int  totalcardlength = 16;
		int totalcardsgenerated  = Integer.parseInt(totalcards);
		Random random = new Random(System.currentTimeMillis());
	        try {
	        	arrCards = new ArrayList<String>();
					for (int j=0;j<totalcardsgenerated;j++){
						builder = null;
							 int randomNumberLength = totalcardlength - (strbin.length() + 1);
							 builder = new StringBuilder(strbin);
								for (int i = 0; i < randomNumberLength; i++) {
									int digit = random.nextInt(10);
									builder.append(digit);
								}
							int checkDigit = getCheckDigit(builder.toString());
				            builder.append(checkDigit);
				            arrCards.add(builder.toString());
					}		 
	        } catch (Exception e) {
	            throw new Exception(" Method createMultipleCards:  Error in creating card number",e);
	        }

 		return arrCards;
 	}
	  	 private static int getCheckDigit(String number) {
      int sum = 0;
        for (int i = 0; i < number.length(); i++) {

            // Get the digit at the current position.
            int digit = Integer.parseInt(number.substring(i, (i + 1)));

            if ((i % 2) == 0) {
                digit = digit * 2;
                if (digit > 9) {
                    digit = (digit / 10) + (digit % 10);
                }
            }
            sum += digit;
        }
        int mod = sum % 10;
        return ((mod == 0) ? 0 : 10 - mod);
 }

	  	public static void wipeString(String stringToWipe) throws Exception {
	  		try {
	  		Field stringValue = String.class.getDeclaredField("value");
	  		stringValue.setAccessible(true);
	  		Arrays.fill((char[]) stringValue.get(stringToWipe), '*');
	  		} catch (IllegalAccessException e) {
	  		throw new Exception("Can't wipe string data");
	  		}
	  	}
	  	public static String encryptString(String stringToEncrypt) throws Exception {
	  		return AESEncrypter.encrypt(stringToEncrypt);
	  	}
	  	public static String decryptString(String stringToDecrypt) throws Exception {
	  		return AESEncrypter.decrypt(stringToDecrypt);

	  	}
	  	public static synchronized String generateCVV2(int length) throws Exception {
	  		int randomNumberLength = length ;	Random random = null;	StringBuilder builder = null;
	  		random = new Random(System.currentTimeMillis());
			 builder = new StringBuilder("");
				try {
					for (int i = 0; i < randomNumberLength; i++) {
						int digit = random.nextInt(10);
						if(digit==0)
						builder.append(digit+1);
						else
							builder.append(digit);
					}
				} catch (Exception e) {
					throw new Exception("Can't generate CVV");
				}finally{
					random=null;
				}
	  		return builder.toString();  		
	  	}

	  	public static synchronized Vector<String> createCVV2(int totalcards, int length) throws Exception{
			Vector<String> vectoralCVV2 =new Vector<String>();	Random random = null;StringBuilder builder = null;
			random = new Random(System.currentTimeMillis());
			try{
			for(int count=0;count<totalcards;count++){
	   	  		int randomNumberLength = length ;
    	  		
				 builder = new StringBuilder("");						
						for (int i = 0; i < randomNumberLength; i++) {
							int digit = random.nextInt(10);
							if(digit==0)
							builder.append(digit+1);
							else
								builder.append(digit);
						}
						vectoralCVV2.add(builder.toString());
				}
			} catch (Exception e) {
				throw new Exception("Can't generate CVV");
			}
			return vectoralCVV2;
		}
	  	
	  	public static synchronized String genAlphaNumRandom(int len) throws Exception {
	  		StringBuilder sb = null;
				try {
					sb = new StringBuilder( len );
					for( int i = 0; i < len; i++ ) {
					      sb.append( RNDSTRING.charAt( rnd.nextInt(RNDSTRING.length()) ) );
					}

				} catch (Exception e) {
					throw new Exception("Can't generate Random Number");
				}
	  		return sb.toString();  		
	  	}	  	
	  	
	    public static int getBusDaysBetweenDuration(String startDateString, String endDateString) throws Exception{
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        //String dateInString = "01-07-2016";
	        Date startDate = sdf.parse(startDateString);
	        //String dateInString2 = "31-07-2016";
	        Date endDate = sdf.parse(endDateString);
	       // calculateDuration(startDate,endDate);
	    	
	    	
	      Calendar startCal = Calendar.getInstance();
	      startCal.setTime(startDate);

	      Calendar endCal = Calendar.getInstance();
	      endCal.setTime(endDate);

	      int workDays = 0;

	      if (startCal.getTimeInMillis() > endCal.getTimeInMillis()){
	        startCal.setTime(endDate);
	        endCal.setTime(startDate);
	      }

	      while (startCal.getTimeInMillis() <= endCal.getTimeInMillis()) {
	    	    if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
	    	        startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
	    	        workDays++;
	    	    }
	    	    startCal.add(Calendar.DAY_OF_MONTH, 1);
	    	}

	      return workDays;
	    }

		public static String generateToken(String acode) throws Exception {
			return asciiToHex( StringUtils.reverse(encryptString(acode))).substring(0, 24);
			
			//return asciiToHex(AESEncrypter.encrypt(acode)).substring(0, 24);
		}

/*		To resize the image call this method from the class as follows:
 * 		File input = new File("/tmp/duke.png");
        BufferedImage image = ImageIO.read(input);
        BufferedImage resized = resize(image, 500, 500);
        File output = new File("/tmp/duke-resized-500x500.png");
        ImageIO.write(resized, "png", output);	
*/	    
		public static BufferedImage resize(BufferedImage img, int height, int width) throws Exception{
			BufferedImage resized = null;
			try{
				Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			
	         resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        Graphics2D g2d = resized.createGraphics();
	        g2d.drawImage(tmp, 0, 0, null);
	        g2d.dispose();
		}catch (Exception e) {
			throw new Exception (e.getMessage());
		}
	        return resized;
	    }
		
		public static synchronized String getRandomPassword() {
		    StringBuilder password = new StringBuilder();
		    int j = 0;
		    for (int i = 0; i < MINLENGTHOFPASSWORD; i++) {
		    	//System.out.println("J is "+j);
		        password.append(getRandomPasswordCharacters(j));
		        j++;
		        
		        if (j == 4) {
		            j = 0;
		        }
		    }
		    return password.toString();
		}

		private static synchronized String getRandomPasswordCharacters(int pos) {
		    Random randomNum = new Random();
		    StringBuilder randomChar = new StringBuilder();
		    switch (pos) {
		        case 0:
		            randomChar.append(NUMBERS.charAt(randomNum.nextInt(NUMBERS.length() - 1)));
		            break;
		        case 1:
		            randomChar.append(UPPER_ALPHABETS.charAt(randomNum.nextInt(UPPER_ALPHABETS.length() - 1)));
		            break;
		        case 2:
		            randomChar.append(SPECIALCHARACTERS.charAt(randomNum.nextInt(SPECIALCHARACTERS.length() - 1)));
		            break;
		        case 3:
		            randomChar.append(LOWER_ALPHABETS.charAt(randomNum.nextInt(LOWER_ALPHABETS.length() - 1)));
		            break;
		    }
		    return randomChar.toString();

		}

		//New addition Ben
		//Convert dates from MMDDYYY to YYYYMMDD
		
		public static String formartDate(String mDate) {
			// TODO Auto-generated method stub
			
			SimpleDateFormat inSDF = new SimpleDateFormat("mm/dd/yyyy");
			  SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-mm-dd");

			 
			  String outDate = "";
			  
			    if (mDate != null) {
			        try {
			            Date date = inSDF.parse(mDate);
			            outDate = outSDF.format(date);
			            
			            
			        } catch (Exception  ex){ 
			        	ex.printStackTrace();
			        }
			    }
			    return outDate;
		}
	  	
		
		//New Addition
		public static String mobileformartDate(String mDate) {
			// TODO Auto-generated method stub
			SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat outSDF = new SimpleDateFormat("dd-MM-yyyy");

			 String outDate = "";
			 if (mDate != null) {
			    try {
			        Date date = inSDF.parse(mDate);
			        outDate = outSDF.format(date);
			        } catch (Exception  ex){ 
			        	ex.printStackTrace();
			        }
			    }
			    return outDate;
		}
		
		//Date Calculation
	
		public static String getDateCalculate(String dateString, int days, String dateFormat) {
		    Calendar cal = Calendar.getInstance();
		    SimpleDateFormat s = new SimpleDateFormat(dateFormat);
		    try {
		        cal.setTime(s.parse(dateString));
		    } catch (ParseException e) {
		        e.printStackTrace();
		    }
		    cal.add(Calendar.DATE, days);
		    return s.format(cal.getTime());
		}
		
		public static String formartDateMpesa(String mDate) {
			// TODO Auto-generated method stub
			SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat outSDF = new SimpleDateFormat("dd-MM-yyyy");

			 String outDate = "";
			 if (mDate != null) {
			    try {
			        Date date = inSDF.parse(mDate);
			        outDate = outSDF.format(date);
			        } catch (Exception  ex){ 
			        	ex.printStackTrace();
			        }
			    }
			    return outDate;
		}

		
		//New addition Ben
		//Convert dates from MMDDYYY to YYYYMMDD
		
		public static String formartDateforGraph(String mDate) {
			// TODO Auto-generated method stub
			
			SimpleDateFormat inSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-MM-dd");

			 
			  String outDate = "";
			  
			    if (mDate != null) {
			        try {
			            Date date = inSDF.parse(mDate);
			            outDate = outSDF.format(date);
			            
			            
			        } catch (Exception  ex){ 
			        	ex.printStackTrace();
			        }
			    }
			    return outDate;
		}
		
		public static String getCurrentDate() throws Exception{
	     	 SimpleDateFormat formatter1 = new SimpleDateFormat ("yyyy-MM-dd");     	        
	     	 java.util.Date date = new Date();      	             	        
	     	return formatter1.format(date);
	     	}
		
		
		
}

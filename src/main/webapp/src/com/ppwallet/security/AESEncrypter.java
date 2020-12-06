package com.ppwallet.security;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import com.ppwallet.PPWalletEnvironment;
public class AESEncrypter {
	private static final String ALGO = "AES";
	private static final int AES_KEYLENGTH = 256;
	private static byte[] iv = new byte[AES_KEYLENGTH / 16];
	private static final String appendix = "657326756376";
	private static final String classname = AESEncrypter.class.getSimpleName();
	private static final String jsonString = "GFaS64w3VyEx7OlQ";
	public static String encrypt(String Data) throws Exception {
        String encryptedValue=null;
		try {
			Key key  = new SecretKeySpec(PPWalletEnvironment.getKeyValue().getBytes("UTF-8"), ALGO);
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encVal = c.doFinal(Data.getBytes());
			encryptedValue = Base64.getEncoder().encodeToString(encVal);
		} catch (Exception e) {
			PPWalletEnvironment.setComment(1,classname, " Exception in encrytion "+e.getMessage());		
		}
        return encryptedValue;
    }
	
	public static String encryptSecure(String Data, String k) throws Exception {
        String encryptedValue=null;
		try {
			Key key  = new SecretKeySpec(k.getBytes("UTF-8"), ALGO);
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encVal = c.doFinal(Data.getBytes());
			encryptedValue = Base64.getEncoder().encodeToString(encVal);
		} catch (Exception e) {
			PPWalletEnvironment.setComment(1,classname, " Exception in encrytion "+e.getMessage());		
		}
        return encryptedValue;
    }
	public static String decrypt(String encryptedData) throws Exception {
        String decryptedValue=null;
		try {
			Key key  = new SecretKeySpec(PPWalletEnvironment.getKeyValue().getBytes("UTF-8"), ALGO);
			//NBPRSLEnvironment.setComment(3,classname, "****** Key to decrypt  "+new String( key.getEncoded(), "UTF-8"));
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
			byte[] decValue = c.doFinal(decordedValue);
			decryptedValue = new String(decValue);
		} catch (Exception e) {
			PPWalletEnvironment.setComment(1,classname, " Exception in decryption "+e.getMessage());		
		}
        return decryptedValue;
    }
	public static String getKey_03(String input_2) {return input_2+Integer.toString(10-2)+(StringUtils.reverse(StringUtils.substring(classname, 0,4)))+Integer.toString(10-3)+Integer.toString(10-4);}
	public static String unRavel(String arg) {
	    String str = "";
	    arg = arg+appendix;
	    for(int i=0;i<arg.length();i+=2)
	    {
	        String s = arg.substring(i, (i + 2));
	        int decimal = Integer.parseInt(s, 16);
	        str = str + (char) decimal;
	    }       
	    return str;
	}

	
	public static String encryptJson(String Data) throws Exception {
        String encryptedValue=null;
		try {
			Key key  = new SecretKeySpec(jsonString.getBytes("UTF-8"), ALGO);
			//NBPRSLEnvironment.setComment(3,classname, "Key to encode  "+new String( key.getEncoded(), "UTF-8"));
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encVal = c.doFinal(Data.getBytes());
			encryptedValue = Base64.getEncoder().encodeToString(encVal);
		} catch (Exception e) {
			//CPBooksEnvironment.setComment(3,classname, " Exception in encrytion "+e.getMessage());
			throw new Exception ("Problem in encryption "+e.getMessage());
		}
        return encryptedValue;
    }
	public static String decryptJson(String encryptedData) throws Exception {
        String decryptedValue=null;
		try {
			Key key  = new SecretKeySpec(jsonString.getBytes("UTF-8"), ALGO);
			//NBPRSLEnvironment.setComment(3,classname, "****** Key to decrypt  "+new String( key.getEncoded(), "UTF-8"));
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] decordedValue = Base64.getDecoder().decode(encryptedData);
			byte[] decValue = c.doFinal(decordedValue);
			decryptedValue = new String(decValue);
		} catch (Exception e) {
			//CPBooksEnvironment.setComment(3,classname, " Exception in decryption "+e.getMessage());	
			throw new Exception ("Problem in decryptJson "+e.getMessage());
		}
        return decryptedValue;
    }
}

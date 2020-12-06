package com.example.demo.security;

import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESEncrypter {
	private static final String ALGO = "AES";
	private static final int AES_KEYLENGTH = 256;
	private static byte[] iv = new byte[AES_KEYLENGTH / 16];
	public static final String apiCall = "746170";
	private static  String KEYVALUE = null;
	
	public static String encrypt(String Data) throws Exception {
        String encryptedValue=null;
		try {
			Key key  = new SecretKeySpec(KEYVALUE.getBytes("UTF-8"), ALGO);
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			c.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
			byte[] encVal = c.doFinal(Data.getBytes());
			encryptedValue = Base64.getEncoder().encodeToString(encVal);
		} catch (Exception e) {
			System.out.println( " Exception in encrytion "+e.getMessage());		
		}
        return encryptedValue;
    }
	

}

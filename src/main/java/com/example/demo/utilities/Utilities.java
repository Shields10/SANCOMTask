package com.example.demo.utilities;

import com.example.demo.security.AESEncrypter;

public class Utilities {
 	public static String encryptString(String stringToEncrypt) throws Exception {
  		return AESEncrypter.encrypt(stringToEncrypt);
  	}

}

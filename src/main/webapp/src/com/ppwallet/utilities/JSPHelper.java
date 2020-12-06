package com.ppwallet.utilities;

public class JSPHelper {
	
	 public static synchronized String geti18Translations_login_jsp(String language) {// login.jsp
		 language=language.toUpperCase();
		 StringBuffer buffer = new StringBuffer();
		 switch (language) {
		 case "EN":
			 buffer.append(  "\"login_page_header\" : \"Pesa Print Wallet - Sign In\","	);
			 buffer.append(  "\"login_page_userid\" : \"User Id\", \"login_page_password\" : \"Password\","	);
			 buffer.append(  "\"login_page_image_desc\" : \"Type Image as above\", \"login_page_radio_customer\" : \"Customer\","	);
			 buffer.append(  "\"login_page_radio_merchant\" : \"Merchant\", \"login_page_radio_operations\" : \"Operations\","	);
			 buffer.append(  "\"login_page_login_button\" : \"Log In\", \"login_page_account_text1\" : \"Don't have an account?\","	);
			 buffer.append(  "\"login_page_signup_link\" : \"Sign Up\", \"login_page_forgot_pwd\" : \"Forgot password?\","	);
			 buffer.append(  "\"login_page_recover_password\" : \"Recover Password\", \"login_page_recover_pwd_instruction\" : \"Enter your Email and instructions will be sent to you\","	);
			 buffer.append(  "\"login_page_pwd_reset_link\" : \"Reset\","	);
		 break;
		 case "SW":
			 buffer.append(  "\"login_page_header\" : \"Pesa Print Wallet - Weka sahihi\","	);
			 buffer.append(  "\"login_page_userid\" : \"Mtumiaji\", \"login_page_password\" : \"Nywila\","	);
			 buffer.append(  "\"login_page_image_desc\" : \"Andika Picha kama ilivyo hapo juu\", \"login_page_radio_customer\" : \"Mteja\","	);
			 buffer.append(  "\"login_page_radio_merchant\" : \"Mfanyabiashara\", \"login_page_radio_operations\" : \"Operesheni\","	);
			 buffer.append(  "\"login_page_login_button\" : \"Ingia\", \"login_page_account_text1\" : \"Huna akaunti?\","	);
			 buffer.append(  "\"login_page_signup_link\" : \"Jisajili\", \"login_page_forgot_pwd\" : \"Umesahau nywila?\","	);
			 buffer.append(  "\"login_page_recover_password\" : \"Rejesha Nenosiri\", \"login_page_recover_pwd_instruction\" : \"Ingiza barua pepe yako na maelekezo yatatumwa kwako\","	);
			 buffer.append(  "\"login_page_pwd_reset_link\" : \"Rudisha\","	);
		 break;		 
		 }
		 return buffer.toString();	 
	 }

	 public static synchronized String geti1ElementConversion_login_jsp() {
	 StringBuffer buffer = new StringBuffer();
	 buffer.append("  $('#login_page_header').text(i18next.t('login_page_header')); "); 
	 buffer.append("  $('#login_page_userid').text(i18next.t('login_page_userid')); "); buffer.append("  $('#login_page_password').text(i18next.t('login_page_password')); "); 
	 buffer.append("  $('#login_page_image_desc').text(i18next.t('login_page_image_desc')); "); buffer.append("  $('#login_page_radio_customer').text(i18next.t('login_page_radio_customer')); "); 
	 buffer.append("  $('#login_page_radio_merchant').text(i18next.t('login_page_radio_merchant')); "); buffer.append("  $('#login_page_radio_operations').text(i18next.t('login_page_radio_operations')); "); 
	 buffer.append("  $('#login_page_login_button').text(i18next.t('login_page_login_button')); "); buffer.append("  $('#login_page_account_text1').text(i18next.t('login_page_account_text1')); "); 
	 buffer.append("  $('#login_page_signup_link').text(i18next.t('login_page_signup_link')); "); buffer.append("  $('#login_page_forgot_pwd').text(i18next.t('login_page_forgot_pwd')); "); 
	 buffer.append("  $('#login_page_recover_password').text(i18next.t('login_page_recover_password')); "); buffer.append("  $('#login_page_recover_pwd_instruction').text(i18next.t('login_page_recover_pwd_instruction')); "); 
	 buffer.append("  $('#login_page_pwd_reset_link').text(i18next.t('login_page_pwd_reset_link')); ");  
	 return buffer.toString();	 
}
}
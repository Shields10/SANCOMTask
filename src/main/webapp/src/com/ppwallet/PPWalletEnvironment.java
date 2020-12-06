package com.ppwallet;

import java.io.FileReader;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;

import com.ppwallet.security.AESEncrypter;
import com.ppwallet.utilities.Utilities;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import org.json.simple.parser.JSONParser;


public class PPWalletEnvironment {
	private static String className = PPWalletEnvironment.class.getSimpleName();
	//private static ResourceBundle RB_LOCALE = null;
	private static JSONObject JSON_LOCALE = null;
    private static  String KEYVALUE = null;
    private static  String FILE_UPLOAD_PATH = null; 
  	private static  String FILE_DOWNLOAD_PATH = null; 
  	private static  String LOGBACK_CONFIG_FILE_PATH = null; 
  	//private static  String ISO8583_VM = null;
  	
    private static String OS = System.getProperty("os.name").toLowerCase();    
    private static boolean debugOn = false;									    
    private static  Logger logger = null;

	public static final String getFileUploadPath() throws Exception{ 			return FILE_UPLOAD_PATH; }													
	public static final String getFileDownloadPath() throws Exception{ 			return FILE_DOWNLOAD_PATH; }
	public static final String getDBUser() throws Exception{ 					return getParameters("DBUSER"); }														
	public static final String getDBPwd() throws Exception{ 					return getParameters("DBPWD"); }
	public static final String getMYSQLDriver() throws Exception{ 				return getParameters("MYSQL_DRIVER"); }						
	public static final String getPostGreSQLDriver() throws Exception{ 			return getParameters("POSTGRESQL_DRIVER"); }
	public static  String getKeyValue() throws Exception{					 	return KEYVALUE; }															
	public static final String getDBURL() throws Exception{ 					return getParameters("DB_URL"); }

	public static final String getLocalDateFormat() throws Exception{ 			return getParameters("LOCAL_DATEFORMAT"); }	

	public static final String getMultiChainUser() throws Exception{ 			return getParameters("MULTICHAINUSER"); }
	public static final String getRPCAuthKey() throws Exception{ 				return getParameters("MUTIRPCKEY"); }
	public static final String getMultiChainRPCURLPORT() throws Exception{ 		return getParameters("MULTIURLPORT"); }	
	public static final String getMultiChainRPCIP() throws Exception{ 			return getParameters("MULTIIP"); }	
	public static final String getMultiChainRPCPort() throws Exception{ 		return getParameters("MULTIPORT"); }
	public static String getBlockChainName() throws Exception{ 					return getParameters("CHAIN_NAME"); }
	public static String getBlockChainStreamName() throws Exception{			return getParameters("STREAM_NAME"); }

    public  static final String getServletPath() throws Exception{     			return getParameters("SERVLET_PATH"); }	
    public  static final String getMutipartServletPath() throws Exception{     	return getParameters("MULTIPARTSERVLET_PATH"); }
    public  static final String getJSONServletPath() throws Exception{     		return getParameters("JSON_SERVLET_PATH"); }	
	// Email Settings
	public static final String getSMTPUserId() throws Exception{ 				return getParameters("stmpuserid"); }						
	public static final String getSMTPUserPwd() throws Exception{ 				return getParameters("smtppwd"); }						
	public static final String getSMTPHost() throws Exception{ 					return getParameters("smtphost"); }			
	public static final String getSMTPTLSPort() throws Exception{ 				return getParameters("smtptlsport"); }
	public static final String getSMTPSSLPort() throws Exception{ 				return getParameters("smtpsslport"); }				
	public static final String getSendFromEmailId() throws Exception{ 			return getParameters("smtpsendfromemail"); }						
	public static final String getEmailThreadCount() throws Exception{ 			return getParameters("EMAIL_THREADS_COUNT"); }

	//public static final String getVMISO8583Template(){ 			return ISO8583_VM; }
	public static final String getAPIKeyPublic() throws Exception{ 				return getParameters("APIKEYPUB"); }	
	public static final String getAPIKeyPrivate() throws Exception{ 			return getParameters("APIKEYPVT"); }	

	public static final String getCodeWalletToMerchantBillPay() throws Exception{ 			return getParameters("CODE_WALLET_TO_MERCH_BILLPAY"); }	
	public static final String getCodeWalletToWalletP2P() throws Exception{ 			return getParameters("CODE_WALLET_TO_WALLET_P2P"); }	
	public static final String getCodeWalletTopUpFromBank() throws Exception{ 			return getParameters("CODE_WALLET_TOPUP_FROM_BANK"); }	
	public static final String getCodeLoyaltyRedeemRate() throws Exception{ 			return getParameters("CODE_LOYALTY_REDEMPTION_PAY_DEFAULT"); }	
	public static final String getCodeWalletTopUpFromMerchant() throws Exception{ 			return getParameters("CODE_WALLET_TOPUP_FROM_MERCH"); }	
	public static final String getCodeWalletTopUpFromMPESA() throws Exception{ 			return getParameters("CODE_WALLET_TOPUP_FROM_MPESA"); }	
	public static final String getCodeWalletToMerchantRetailPay() throws Exception{ 	return getParameters("CODE_WALLET_TO_MERCH_RETAIL_PAY"); }	
	public static final String getCodeWalletCashOutFromMerhant() throws Exception{ 			return getParameters("CODE_WALLET_CASHOUT_FROM_MERCH"); }	


    public static final String getErrorPage() throws Exception{     				return getParameters("ERROR_PAGE"); }
	public static final String getLoginPage() throws Exception{     				return getParameters("LOGIN_PAGE"); }
	public static final String getUserExpiryDatePage() throws Exception{			return getParameters("EXPIRY_PAGE"); }
	public static final String getCustomerDashboardPage() throws Exception{			return getParameters("CUSTOMER_DASHBOARD_PAGE"); }
	public static String getBatchJobDashboardPage()  throws Exception{              return getParameters("BATCH_JOB_DASHBOARD_PAGE");}

	
	//added
	public static final String getCustomerProfileViewPage() throws Exception{			return getParameters("CUSTOMER_VIEW_PROFILE"); }
	public static String getCustomerRegisterBankPage()       throws Exception {          return getParameters("CUSTOMER_REGISTER_BANK");}
	public static String getCustomerRegisterMpesaPage()       throws Exception {          return getParameters("CUSTOMER_REGISTER_MPESA");}

	

	
	public static String getCustomerWalletMainPage()       throws Exception {          return getParameters("CUSTOMER_WALLET_MAINPAGE");}
	public static String getCustomerWalletTopUpPage()       throws Exception {        return getParameters("CUSTOMER_TOPUP_WALLET");}
	public static String getWalletTransactionsForUserPage()       throws Exception {        return getParameters("CUSTOMER_WALLET_TRANSACTIONPAGE");}
	public static String getCustomerP2PTransferfundMainPage()       throws Exception {        return getParameters("CUSTOMER_P2PTRANSFER_FUNDSPAGE");}
	public static String getCustomerRegisterReceiverWalletPage()       throws Exception {        return getParameters("CUSTOMER_P2PTRANSFER_REGISTERWALLETPAGE");}
	
   public static String getCustomerLoyaltyRulesViewPage()       throws Exception {        return getParameters("CUSTOMER_VIEW_REWARDS_RULESPAGE");}
   public static String getCustomerLoyaltyViewPage()       throws Exception {        return getParameters("CUSTOMER_LOYALTY_VIEWPAGE");}
   public static String getCustomerLoyaltyClaimPage()       throws Exception {        return getParameters("CUSTOMER_LOYALTY_CLAIMPAGE");}
   
   public static String getCustomerPayBillsPage()       throws Exception {        return getParameters("CUSTOMER_PAY_BILLSPAGE");}
   public static String getCustomerRegisterBillerPage()       throws Exception {        return getParameters("CUSTOMER_REGISTER_BILLERPAGE");}
   public static String getCustomerBillPayViewPage()       throws Exception {        return getParameters("CUSTOMER_BILLPAY_CHOOSE_WALLETPAGE");}
   public static String getCustomerBillerPayConfirmPage()       throws Exception {        return getParameters("CUSTOMER_BILLPAY_CONFIRMPAGE");}
   public static String getCustomerBillPayTransactionsPage()       throws Exception {        return getParameters("CUSTOMER_BILLPAY_TRANSACTIONPAGE");}
   
   public static String getCustomerRegistrationPage()       throws Exception {        return getParameters("CUSTOMER_SELF_REGISTRATION_PAGE");}
   public static String getPersoCustomerRegistrationPage()       throws Exception {        return getParameters("PERSO_CUSTOMER_REGISTRATION_PAGE");}
   public static String getCustomerRegisterCardPage()       throws Exception {        return getParameters("CARD_REGISTRATION_PAGE");}
   public static String getCustomerPaymentAllTokensPage()       throws Exception {        return getParameters("SHOW_ALL_CARDSPAGE");}
   
   
   
   
   public static String getCustomerBillerPayCardConfirmPage()       throws Exception {        return getParameters("CUSTOMER_BILLPAY_CHOOSE_CARDPAGE");}
   public static final String getCodeTokenWalletTopup() throws Exception{ 			return getParameters("CODE_TOKEN_WALLET_TOPUP"); }	
   public static final String getCodeTokenToMerchantBillPay() throws Exception{ 			return getParameters("CODE_TOKEN_TO_MERCH_BILLPAY"); }
   public static final String getCodeTokenToMerchantRetailPay() throws Exception{ 			return getParameters("CODE_TOKEN_TO_MERCH_RETAIL_PAY"); }
   
   
   
   
   

	public static final String getMerchantDashboardPage() throws Exception{			return getParameters("MERCHANT_DASHBOARD_PAGE"); }
	
	
	/*
	 * public static final String getOperationsDashboardPage() throws Exception{
	 * return getParameters("OPERATIONS_DASHBOARD_PAGE"); } public static final
	 * String getOpsSysCreateBINPage() throws Exception { return
	 * getParameters("OPS_SYS_CARDS_CREATEBIN_PAGE"); } public static final String
	 * getIssuingBINListPage() throws Exception{ return
	 * getParameters("OPS_SYS_CARDS_SHOWBINLIST_PAGE"); } public static final String
	 * getOpsSysEditSpecificBINPage() throws Exception { return
	 * getParameters("OPS_SYS_CARDS_EDISPECTBIN_PAGE"); } public static final String
	 * getOpsSysCreateCardsProductPage() throws Exception { return
	 * getParameters("OPS_SYS_CARDS_CREATECARDPRODUCT_PAGE"); } public static final
	 * String getOpsSysAllProductPage() throws Exception{ return
	 * getParameters("OPS_SYS_CARDS_ALLCARDPRODUCT_PAGE"); } public static final
	 * String getOpsSysEditCardProductsPage() throws Exception{ return
	 * getParameters("OPS_SYS_CARDS_EDITCARDPRODUCT_PAGE"); } public static final
	 * String getOpsSysMerchMCCManagePage() throws Exception { return
	 * getParameters("OPS_SYS_MERCH_MCCMANAGE_PAGE"); } public static final String
	 * getOpsSysMerchNewInstitutionPage() throws Exception{ return
	 * getParameters("OPS_SYS_MERCH_INST_CREATE_NEW_PAGE"); } public static final
	 * String getOpsSysMerchEditInstitutionPage() throws Exception{ return
	 * getParameters("OPS_SYS_MERCH_INST_EDIT_PAGE"); } public static String
	 * getOpsSysMerchSetMSFPlanPage() throws Exception { return
	 * getParameters("OPS_SYS_MERCH_ALLMSFPLAN_PAGE"); } public static String
	 * getOpsMerchManagePage() throws Exception { return
	 * getParameters("OPS_SYS_MERCH_ALLMSFPLAN_PAGE"); }
	 */
	//new stuff 
	//customer stuff
	//Customer stuff
		public static String getCustRaiseDisputePage() throws Exception  { return getParameters("CUSTOMER_RAISE_DISPUTE"); }
		public static String getCustViewDisputePage() throws Exception  { return getParameters("CUSTOMER_VIEW_DISPUTE"); }
		public static String getCustSpecificDisputePage() throws Exception  { return getParameters("CUSTOMER_SHOW_SPECIFIC_DISPUTE_PAGE"); }
		//public static String getCustomerLoyaltyRulesViewPage() throws Exception  { return getParameters("CUSTOMER_VIEW_REWARD_RULES_PAGE"); }
		
		/*====================================Merch Stuf ===============================================*/
		//Merchant
		public static String getMerchantStaticQrPage() throws Exception {  			return getParameters("MERCH_PAYMENTS_MODULE");	}
		public static String getMerchantDynamicQrPage() throws Exception {  			return getParameters("MERCH_PAYMENTS_DYNMCQR_PAGE");	}
		public static String getMercAccPymtPage() throws Exception {  			return getParameters("MERCH_ACCEPT_PAYMENTS_PAGE");	}
		public static String getMerchCashout() throws Exception {  			return getParameters("MERCHANT_PAYMENT_MODULE_CASHOUT_PAGE");	}
		public static String getMerchTopUp() throws Exception {  			return getParameters("MERCHANT_PAYMENT_MODULE_TOPUP_PAGE");	}
		
		
		/*********Transactions************/
		
		public static String getTopupTransactions() throws Exception {  			return getParameters("MERCHANT_TOPUP_TRANSACTIONS_PAGE");	}
		public static String getCashoutTransactions() throws Exception {  			return getParameters("MERCHANT_CASHOUT_TRANSACTIONS_PAGE");	}
		public static String getRetailpayTransactions() throws Exception {  			return getParameters("MERCHANT_RETAIL_TRANSACTIONS_PAGE");	}
		
		
	//NEW ADDITION retro\  getCustomerRegisterCardPage
		//merchant stuff
		public static String getMerchantRegistrationPage() throws Exception {					return getParameters("MERCHANT_REGISTRATION_PAGE");		}
		public static String getMerchantProfilePage() throws Exception  { return getParameters("MERCHANT_PROFILE_VIEW_PAGE"); }
		public static String getMerchantMsfPlanPage() throws Exception  { return getParameters("MERCHANT_MSF_VIEW_PAGE"); }
		
		public static String getViewDisputePage() throws Exception  { return getParameters("MERCHANT_VIEW_DISPUTE"); }
		public static String getMerchRaiseDisputePage() throws Exception  { return getParameters("MERCHANT_RAISE_DISPUTE"); }
		public static String getSpecificDisputePage() throws Exception  { return getParameters("MERCHANT_SHOW_SPECIFIC_DISPUTE_PAGE"); }
	
		
		
		

		//Bill Module
		public static String getBillerdetails() throws Exception  {
			// TODO Auto-generated method stub
			return getParameters("MERCHANT_BILLPAYMENT_MODULE_BILLER_DETAILS_PAGE");
		}
//		public static String getBillerdetails() throws Exception {  			return getParameters("MERCHANT_BILLPAYMENT_MODULE_BILLER_DETAILS_PAGE");	}
		public static String getBillCustRegPage() throws Exception {  			return getParameters("MERCHANT_BILLPAYMENT_REGISTER_CUST");	}
		public static String getBillPayTransactionsPage() throws Exception {  			return getParameters("MERCHANT_BILLPAYMENT_TRANSACTIONS");	}
		
		
		
		
		/*=============================Operations================*/
		
		  
		public static final String getOperationsDashboardPage() throws Exception{		return getParameters("OPERATIONS_DASHBOARD_PAGE"); }
		public static final String getOpsSysCreateBINPage() throws Exception {			return getParameters("OPS_SYS_CARDS_CREATEBIN_PAGE"); }
		public static final String getIssuingBINListPage() throws Exception{			return getParameters("OPS_SYS_CARDS_SHOWBINLIST_PAGE"); }
		public static final String getOpsSysEditSpecificBINPage() throws Exception {	return getParameters("OPS_SYS_CARDS_EDISPECTBIN_PAGE"); }
		public static final String getOpsSysCreateCardsProductPage() throws Exception {	return getParameters("OPS_SYS_CARDS_CREATECARDPRODUCT_PAGE"); }
		public static final String getOpsSysAllProductPage() throws Exception{			return getParameters("OPS_SYS_CARDS_ALLCARDPRODUCT_PAGE"); }
		public static final String getOpsSysEditCardProductsPage() throws Exception{	return getParameters("OPS_SYS_CARDS_EDITCARDPRODUCT_PAGE"); }
		public static final String getOpsMerchantDetailsPage() throws Exception{     return getParameters("OPS_MERCH_VIEW_MERCHANTS_DETAILS_PAGE");}
		public static final String getOpsSysMerchantDetailsPage() throws Exception{     return getParameters("OPS_MERCH_VIEW_PENDING_MERCHANTS_PAGE");}
		public static final String getOpsSysMerchMCCManagePage() throws Exception {		return getParameters("OPS_SYS_MERCH_MCCMANAGE_PAGE"); }
		public static final String getOpsSysMerchNewInstitutionPage() throws Exception{	return getParameters("OPS_SYS_MERCH_INST_CREATE_NEW_PAGE");	}
		//public static final String getOpsSysMerchEditInstitutionPage() throws Exception{	return getParameters("OPS_SYS_MERCH_INST_EDIT_PAGE");	}
		public static String getOpsSysMerchSetMSFPlanPage() throws Exception {  			return getParameters("OPS_SYS_MERCH_SETMSFPLAN_PAGE");	}
			
		public static String getOpsMerchManagePage() throws Exception {					return getParameters("OPS_MERCH_SET_MERCHANTS_PAGE");		}
		public static String getOpsSysCustomerDisputesPage() throws Exception {		   return getParameters("OPS_CUST_VIEW_CARD_DISPUTES_PAGE");       }
		public static String getOpsSysRaiseCustomerDisputesPage() throws Exception {		   return getParameters("OPS_CUST_RAISE_CUST_DISPUTE_PAGE");       }
		public static String getOpsSysViewCustomerDisputesPage() throws Exception {		   return getParameters("OPS_VIEW_RAISE_CUST_DISPUTE_PAGE");       }
		
		public static String getOpsShowWalletForACustomerPage() throws Exception {		return getParameters("OPS_CUST_VIEWWALLETS_FORSPEC_CUSTOMER");   	}

		public static String getOpsSysMerchDisputeReasons() throws Exception {return getParameters("OPS_MERCH_DISPUTE_REASONS_PAGE");}
		public static String getOpsViewCardsPage() throws Exception {return getParameters("OPS_CUST_VIEW_CARDS_PAGE");}
		public static String getOpsSysPendingCustomersPage() throws Exception {return getParameters("OPS_CUST_VIEW_PENDING_CUSTOMERS");}
		public static String getOpsViewCustomersPage() throws Exception {return getParameters ("OPS_CUST_VIEW_CUSTOMERS_PAGE");}
		public static String getOpsViewCustomersWalletPage() throws Exception {return getParameters ("OPS_CUST_WALLETS_PAGE");}
		public static String getOpsSysDisputeandTrackerPage() throws Exception{		return getParameters ("OPS_CUST_VIEW_EACH_DISPUTES_PAGE");	}
		//public static String getOpsSysDisputeandTrackerPage() throws Exception{		return getParameters ("OPS_CUST_SHOW_EACH_DISPUTE_PAGE");	}
		public static String getOpsUpdateCustDisputePage() throws Exception{ return getParameters("OPS_CUST_UPDATE_DISPUTE_STATUS"); }
		public static String getOpsSysProfilePage() throws Exception {		return getParameters("OPS_VIEW_PROFILE_PAGE"); 	}
		public static String getOpsManageAllOpsUsersPage() throws Exception{	return getParameters("OPS_MANAGE_OPS_USERS_PAGE");	}
		//public static String getOpsSysRaiseCardDisputePage() throws Exception { return getParameters("OPS_CUST_RAISE_CUST_DISPUTE_PAGE");	}
		public static String getLoyaltyRulesPage() throws Exception { return getParameters("OPS_VIEW_LOYALTY_RULES_PAGE"); }
		public static String getCustomerLoyaltyPage() throws Exception { return getParameters("OPS_VIEW_CUSTOMER_LOYALTY_PAGE"); }
		public static String getCustomerClaimLoyaltyPage() throws Exception { return getParameters("OPS_CUSTOMER_CLAIM_LOYALTY_PAGE"); }
		//public static String getOpsSysCreateLoyaltyRulesPage() throws Exception { return getParameters("OPS_CREATE_LOYALTY_RULES"); }
		public static final String getViewDisputePageOps() throws Exception { return getParameters("OPS_MERCH_VIEW_MERCH_DISPUTE_PAGE");	}
		

		public static final String getMerchRaiseDisputePageOps() throws Exception { return getParameters("OPS_MERCH_RAISE_MERCHANT_DISPUTE_PAGE");}
		public static final String getOpsTransactionsPage() throws Exception { return getParameters("OPS_VIEW_TRANSACTIONS_PAGE");}
		public static String getOpsSysAuditTrailsPage() throws Exception { return getParameters("OPS_VIEW_AUDIT_TRAIL_PAGE");}
		public static String getOpsSysMerchCallLog() throws Exception { return getParameters("OPS_MERCH_VIEW_CALL_LOGS");}
		public static String getOpsSysCustCallLog()  throws Exception { return getParameters("OPS_CUST_VIEW_CALL_LOGS_PAGE");}

		public static String getOpsViewMSF()  throws Exception { return getParameters("OPS_MERCH_VIEW_MSF");}

		public static String getOpsViewMSFPlansForBiller() throws Exception{		return getParameters("OPS_MERCH_VIEW_ALL_MSF_PLANS_FOR_BILLER");	}
		
		public static String getOpsEditSpecificMerchanPage() throws Exception{ 		return getParameters("OPS_MERCH_EDIT_SPECIFIC_MERCHANT_PAGE");	} 
		public static String getOpsSpecificDisputePage() throws Exception{ 		return getParameters("OPS_VIEW_SPECIFIC_DISPUTE");	} 
		
		
		
		
		
		
		
		
		
		
		
		

	
    public static synchronized JSONObject getInstance(){ 
    	// calling from multiple EventListerers and Servlets to check whether the environment is formed.
    	return JSON_LOCALE; 
    	}
    

   public static synchronized void init() throws Exception {
    	try {
    		 JSONParser parser = new JSONParser();
    		if(JSON_LOCALE==null) {
    		       //Properties jvm = System.getProperties();
    		       // jvm.list(System.out);
	 					System.out.print("\n *** Now catalina.home is**** "+System.getProperty("catalina.home")+"\n");
	 					try {
	 			         System.out.println("\n *** Now Reading Properties file**** ");	 			         	 			         
	 			         Object objRead = parser.parse(new FileReader(StringUtils.replace(System.getProperty("catalina.home"), "\\", "/")+"/PPWalletApplicationParameters.json"));
	 			        //Object objRead = parser.parse(new FileReader("D:/apache-tomcat-9.0.7-2/CPBooksApplicationParameters.json"));
	 			         JSON_LOCALE = (JSONObject) objRead;
	 			        //System.out.println("\n "+JSON_LOCALE.toString());
		 				} catch (Exception e) {
		 					System.out.println(className+ "  Exception in reading Resourcebundle "+e.getMessage());
		 				}
			     			debugOn = Boolean.parseBoolean(StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("debugOn").toString()))); 
		     			if(isWindows()){
		     				//FILE_UPLOAD_PATH = StringUtils.replace(System.getProperty("catalina.home"), "\\", "/") + StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("UPLOAD_PATH_WIN").toString()));
		     				FILE_UPLOAD_PATH = StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("UPLOAD_PATH_WIN").toString()));
		     				
		     				//FILE_DOWNLOAD_PATH = StringUtils.replace(System.getProperty("catalina.home"), "\\", "/") + StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("DOWNLOAD_PATH_WIN").toString()));
		     				FILE_DOWNLOAD_PATH = StringUtils.replace(System.getProperty("catalina.home"), "\\", "/") + StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("DOWNLOAD_PATH_WIN").toString()));
		     				FILE_DOWNLOAD_PATH = StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("DOWNLOAD_PATH_WIN").toString()));

		     				LOGBACK_CONFIG_FILE_PATH = StringUtils.replace(System.getProperty("catalina.home"), "\\", "/") +	StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("ERROR_LOG_WIN").toString()));
		     			}else if(isUnix()){
		     				FILE_UPLOAD_PATH = System.getProperty("catalina.home") +	StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("UPLOAD_PATH_LIN").toString()));
		     				FILE_DOWNLOAD_PATH = System.getProperty("catalina.home") + StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("DOWNLOAD_PATH_LIN").toString()));
		     	 			LOGBACK_CONFIG_FILE_PATH = StringUtils.replace(System.getProperty("catalina.home"), "\\", "/") + StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("ERROR_LOG_LIN").toString()));
		     			}else if(isSolaris()){
		     				FILE_UPLOAD_PATH = System.getProperty("catalina.home") +	StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("UPLOAD_PATH_LIN").toString()));
		     				FILE_DOWNLOAD_PATH = System.getProperty("catalina.home") + StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("DOWNLOAD_PATH_LIN").toString()));
		     	 			LOGBACK_CONFIG_FILE_PATH = StringUtils.replace(System.getProperty("catalina.home"), "\\", "/") + StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get("ERROR_LOG_LIN").toString()));
		     			}
		     				//**** Now forming the logger file
		     	 			LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		     	 			PatternLayoutEncoder ple = new PatternLayoutEncoder();
		     	 			//ple.setPattern("%date %level [%thread] %logger{10} [%file:%line] %msg%n");
		     	 			ple.setPattern("%-12date{dd-MM-YYYY HH:mm:ss.SSS} - %msg%n");
		     	 			ple.setContext(lc);
		     	 			ple.start();
		     	 			FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
		     	 			fileAppender.setFile(LOGBACK_CONFIG_FILE_PATH);
		     	 			fileAppender.setEncoder(ple);
		     	 			fileAppender.setContext(lc);
		     	 			fileAppender.start();
		
		     	 			logger = (Logger) LoggerFactory.getLogger("");
		     	 			logger.addAppender(fileAppender);
		     	 			logger.setLevel(Level.ALL);
		     	 			logger.setAdditive(false); 
					
		      			//if(DBPASS==null){ DBPASS= getDBpass1().trim(); }
		     	 			if(KEYVALUE==null){ KEYVALUE= getKey(); }
		     	 			//VMfactory = new Iso8583MessageFactory(new FileInputStream(getVMISO8583Template()));
		     	 			setComment(3, className, "KEYVALUE is "+KEYVALUE);
		 			
    		}else {
    			setComment(3, className, "Environment already formed...");
    		}
    		
    	}catch(Exception e) {
    		System.out.println(className+"  ==> Exception in the init() method -- > "+e.getMessage());
    	}
    	
    }

    public static void setComment(int level,String className, String msg) { 
		  try{
			  if(debugOn) {
				  switch(level) {
				  case 1:   logger.error("SEVERE: "+className+" --- "+msg);				  break;
				  case 2:   logger.debug("DEBUG: "+className+" --- "+msg);				  break;
				  case 3:   logger.info("INFO: "+className+" --- "+msg);				  break;
				  }
				  }else {
					  if(level==1)
						  logger.error("SEVERE: "+className+" --- "+msg);	
				  }
				  
	      	} catch (Exception e){
	      		e.printStackTrace();
	      	}
	}
   
	//private static String getDBpass1()  throws Exception{return Utilities.getPass(DBPASS1.trim());	}
 	public static boolean isWindows() { return (OS.indexOf("win") >= 0); }
 	public static boolean isMac() { return (OS.indexOf("mac") >= 0); 	}
 	public static boolean isUnix() { return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 ); 	}
 	public static boolean isSolaris() { return (OS.indexOf("sunos") >= 0); 	}
	private static String getKey() throws Exception{return getKey_01().trim();}
	private static String getKey_01()  throws Exception{return   Utilities.getKey_02(StringUtils.reverse(StringUtils.substring(className, 0,4)));	}
	public static String tempKey() throws Exception {return getKey_01().trim();}
	private static synchronized String getParameters(String paramName) throws Exception{		return StringUtils.trim(AESEncrypter.decryptJson(JSON_LOCALE.get(paramName).toString()));	}
}

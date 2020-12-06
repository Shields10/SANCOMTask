package com.ppwallet;

import java.io.File;
import java.io.FileWriter;
import org.json.simple.JSONObject;
import com.ppwallet.security.AESEncrypter;
/*
This is a standalone Java file to write the application properties in encrypted manner
This will generate a CPBooksApplicationParameters.json file
To write data in the file, enter the fieldName and fieldValue as shown below
Then run the java file as standalone from eclipse Run or press Ctrl + F11
Once the java file is run a file called CPBooksApplicationParameters.json will be created in the tomcat.home path
Note: The development and production parameters will change for some settings such as Blockchain, Database etc
Hence be careful of the parameters

IMPORTANT: BEST TO REMOVE THIS FILE (JsonWrite.java) WHILE COMPILING PRODUCTION .war file. the .class file can be decompiled and all params can be read
*/	
public class JsonWrite {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception{
		File file = null; 
		FileWriter filewriter = null;
		JSONObject obj = null;
		System.out.print(System.getProperty("java.home")+"\n"); // put it as catalina.home or the tomcat path
		
		try {
			//file = new File(StringUtils.replace(System.getProperty("java.home"), "\\", "/")+"/PPWalletApplicationParameters.json");
	           file = new File("D:/apache-tomcat-9.0.31/PPWalletApplicationParameters.json");
	            file.setWritable(true);
	            file.setReadable(true);
	            filewriter = new FileWriter(file);
			
			//filewriter = new FileWriter("D:/apache-tomcat-9.0.21/PPWalletApplicationParameters.json");
			obj = new JSONObject();
			 System.out.println("\n *** Now Writing the file at "+file.getAbsolutePath());
			obj.put("debugOn",  setParam("true"));
			obj.put("LOCAL_DATEFORMAT",  setParam("IST"));

			obj.put("ERROR_LOG_WIN",  setParam("/apps/ppwallet/logs/ppwalleterror.log"));
			obj.put("ERROR_LOG_LIN",  setParam("/apps/ppwallet/logs/ppwalleterror.log"));
			
			//obj.put("UPLOAD_PATH_WIN",  setParam("/apps/ppwallet/upload")); // THIS WILL BE CHANGED
			//obj.put("UPLOAD_PATH_LIN",  setParam("/apps/ppwallet/upload")); // THIS WILL BE CHANGED
			//obj.put("DOWNLOAD_PATH_WIN",  setParam("/apps/ppwallet/download"));
			//obj.put("DOWNLOAD_PATH_LIN",  setParam("/apps/ppwallet/download"));
			obj.put("UPLOAD_PATH_WIN",  setParam("D:/ppwallet/fileupload"));   //^^^^^This has been changed to this
			obj.put("UPLOAD_PATH_LIN",  setParam("/usr/pi/tobedecided/ppwallet/fileupload"));
			obj.put("DOWNLOAD_PATH_WIN",  setParam("D:/ppwallet/fileupload"));
			obj.put("DOWNLOAD_PATH_LIN",  setParam("/apps/ppwallet/download"));
			
			
			//obj.put("serversocketport",  setParam("8888"));
			//obj.put("clientsocketport",  setParam("8999"));
			//obj.put("serversocketIP",  setParam("localhost"));
			//obj.put("clientsocketIP",  setParam("localhost"));
			obj.put("DBUSER",  setParam("vcuser"));
			obj.put("DBPWD",  setParam("vcuser123"));
			
			obj.put("stmpuserid",  setParam("pesaprintwallet001@gmail.com"));
			obj.put("smtppwd",  setParam("abcd!@#$1234"));
			obj.put("smtphost",  setParam("smtp.gmail.com"));
			obj.put("smtptlsport",  setParam("587"));
			obj.put("smtpsslport",  setParam("465"));
			obj.put("smtpsendfromemail",  setParam("pesaprintwallet001@gmail.com"));
			//obj.put("clientsocketsimulation",  setParam("true"));
			obj.put("APIKEYPUB",  setParam("39EB1CRWUMQFN91Z9BE0U42Y"));
			obj.put("APIKEYPVT",  setParam("6y8dU9av6"));
			obj.put("MULTICHAINUSER",  setParam("multichainrpc"));
			obj.put("MUTIRPCKEY",  setParam("3xEH8yvStaR5ZDa1i2haQbSsNizjS3bWhmJznPkbkGfV"));   
			obj.put("MULTIURLPORT",  setParam("http://127.0.0.1:9732/"));
			obj.put("MULTIIP",  setParam("127.0.0.1"));
			obj.put("MULTIPORT",  setParam("9732"));
			obj.put("CHAIN_NAME",  setParam("ppwalletchain1"));
			obj.put("STREAM_NAME",  setParam("cardvault")); // don't use this parameter
			obj.put("WALLET_STREAM_NAME",  setParam("walletledger")); // don't use this parameter
			obj.put("debugOn",  setParam("true"));
			obj.put("clientIP",  setParam("localhost"));
			obj.put("DB_URL",  setParam("jdbc:mysql://localhost:3306/ppwallet?characterEncoding=UTF-8&verifyServerCertificate=false&useSSL=true&requireSSL=false&rewriteBatchedStatements=true&zeroDateTimeBehavior=convertToNull"));
			obj.put("MYSQL_DRIVER",  setParam("com.mysql.cj.jdbc.Driver"));
			obj.put("ORACLE_DRIVER",  setParam("oracle.jdbc.OracleDriver"));
			obj.put("POSTGRESQL_DRIVER",  setParam("org.postgresql.Driver"));
			obj.put("SERVLET_PATH",  setParam("ws"));
			obj.put("JSON_SERVLET_PATH",  setParam("json"));
			obj.put("MULTIPARTSERVLET_PATH",  setParam("ms"));
			obj.put("EMAIL_THREADS_COUNT",  setParam("5"));
			
			
			obj.put("CODE_WALLET_TO_MERCH_BILLPAY",  setParam("WBP"));
			obj.put("CODE_WALLET_TO_WALLET_P2P",  	setParam("WWP"));
			obj.put("CODE_WALLET_TOPUP_FROM_BANK",  setParam("WTB"));
			obj.put("CODE_LOYALTY_REDEMPTION_PAY_DEFAULT",  setParam("WRD"));
			obj.put("CODE_WALLET_TOPUP_FROM_MERCH",  setParam("WTM"));
			obj.put("CODE_WALLET_TOPUP_FROM_MPESA",  setParam("WTP"));
			obj.put("CODE_WALLET_TO_MERCH_RETAIL_PAY",  setParam("WRP"));
			obj.put("CODE_WALLET_CASHOUT_FROM_MERCH",  setParam("WCO"));
			
			
			
			obj.put("LOGIN_PAGE",  setParam("/login.jsp"));
			obj.put("ERROR_PAGE",  setParam("/error.jsp"));
			obj.put("EXPIRY_PAGE",  setParam("/expiry_403.jsp"));
			obj.put("CUSTOMER_DASHBOARD_PAGE",  setParam("/cust_dashboard.jsp"));
			obj.put("BATCH_JOB_DASHBOARD_PAGE",  setParam("/batch_job_dashboard.jsp"));

			
			
			
			
			//added cust_dashboard
			obj.put("CUSTOMER_VIEW_PROFILE",  setParam("/cust_viewprofile.jsp"));
			obj.put("CUSTOMER_REGISTER_BANK",  setParam("/cust_register_bank.jsp"));
			obj.put("CUSTOMER_REGISTER_MPESA",  setParam("/cust_register_mpesa.jsp"));

			
			
			
			obj.put("CUSTOMER_WALLET_MAINPAGE",  setParam("/cust_walletmainpage.jsp"));
			obj.put("CUSTOMER_TOPUP_WALLET",  setParam("/cust_wallet_topup.jsp"));
			obj.put("CUSTOMER_WALLET_TRANSACTIONPAGE",  setParam("/cust_wallet_transactionpage.jsp"));
			obj.put("CUSTOMER_P2PTRANSFER_FUNDSPAGE",  setParam("/cust_wallet_p2psendmoneypage.jsp"));
			obj.put("CUSTOMER_P2PTRANSFER_REGISTERWALLETPAGE",  setParam("/cust_wallet_p2pwalletregpage.jsp"));
			
			obj.put("CUSTOMER_VIEW_REWARDS_RULESPAGE",  setParam("/cust_viewrewardrule.jsp"));
			obj.put("CUSTOMER_LOYALTY_VIEWPAGE",  setParam("/cust_viewloyaltypoints.jsp"));
			obj.put("CUSTOMER_LOYALTY_CLAIMPAGE",  setParam("/cust_loyaltyclaimpage.jsp"));
			
			obj.put("CUSTOMER_PAY_BILLSPAGE",  setParam("/cust_billpaypage.jsp"));
			obj.put("CUSTOMER_REGISTER_BILLERPAGE",  setParam("/cust_registerbillerpage.jsp"));
			obj.put("CUSTOMER_BILLPAY_CHOOSE_WALLETPAGE",  setParam("/cust_billpay_choose_walletpage.jsp"));
			obj.put("CUSTOMER_BILLPAY_CONFIRMPAGE",  setParam("/cust_billpay_confirmpage.jsp"));
			obj.put("CUSTOMER_BILLPAY_TRANSACTIONPAGE",  setParam("/cust_billpay_transactionpage.jsp"));
			
			// second phase additions
			
			obj.put("CUSTOMER_SELF_REGISTRATION_PAGE",  setParam("/cust_self_registration.jsp"));
			obj.put("PERSO_CUSTOMER_REGISTRATION_PAGE",  setParam("/cust_reg.jsp"));
			obj.put("CUSTOMER_BILLPAY_CHOOSE_CARDPAGE",  setParam("/cust_billpay_choose_cardpage.jsp"));
			obj.put("CARD_REGISTRATION_PAGE",  setParam("/cust_register_card.jsp"));
			obj.put("SHOW_ALL_CARDSPAGE",  setParam("/cust_view_card.jsp"));
			
			
			
			obj.put("MERCHANT_DASHBOARD_PAGE",  setParam("/merch_dashboard.jsp"));
			obj.put("CODE_TOKEN_WALLET_TOPUP",  setParam("TWT"));
			obj.put("CODE_TOKEN_TO_MERCH_BILLPAY",  setParam("TBP"));
			obj.put("CODE_TOKEN_TO_MERCH_RETAIL_PAY",  setParam("TRP"));
			
			
			
			/*
			 * obj.put("OPERATIONS_DASHBOARD_PAGE", setParam("/ops_dashboard.jsp"));
			 * obj.put("OPS_SYS_CARDS_CREATEBIN_PAGE",
			 * setParam("/ops_sys_cardscreatebin.jsp"));
			 * obj.put("OPS_SYS_CARDS_SHOWBINLIST_PAGE",
			 * setParam("/ops_sys_cardsshowallbin.jsp"));
			 * obj.put("OPS_SYS_CARDS_EDISPECTBIN_PAGE",
			 * setParam("/ops_sys_cardseditbin.jsp"));
			 * obj.put("OPS_SYS_CARDS_CREATECARDPRODUCT_PAGE",
			 * setParam("/ops_sys_newcardproduct.jsp"));
			 * obj.put("OPS_SYS_CARDS_ALLCARDPRODUCT_PAGE",
			 * setParam("/ops_sys_allcardproducts.jsp"));
			 * obj.put("OPS_SYS_CARDS_EDITCARDPRODUCT_PAGE",
			 * setParam("/ops_sys_editcardproduct.jsp"));
			 * obj.put("OPS_SYS_MERCH_MCCMANAGE_PAGE",
			 * setParam("/ops_sys_merchmccmanage.jsp"));
			 * obj.put("OPS_SYS_MERCH_INST_CREATE_NEW_PAGE",
			 * setParam("/ops_sys_merchinstnew.jsp"));
			 * obj.put("OPS_SYS_MERCH_INST_EDIT_PAGE",
			 * setParam("/ops_sys_merchinstedit.jsp"));
			 * obj.put("OPS_SYS_MERCH_ALLMSFPLAN_PAGE",
			 * setParam("/ops_sys_merchallsysmsfplan.jsp"));
			 * obj.put("OPS_SYS_MERCH_ALLMSFPLAN_PAGE", setParam("/ops_merchlistall.jsp"));
			 */
			
			
			
			
			// new stuff 
			//customer stuff
			obj.put("CUSTOMER_RAISE_DISPUTE",  setParam("/cust_raisedispute.jsp"));
			obj.put("CUSTOMER_VIEW_DISPUTE",  setParam("/cust_viewdispute.jsp"));
			obj.put("CUSTOMER_SHOW_SPECIFIC_DISPUTE_PAGE",  setParam("/cust_showspecdispute.jsp"));
			obj.put("CUSTOMER_VIEW_REWARD_RULES_PAGE",  setParam("/cust_viewrewardrule.jsp"));
			
			
			
			
		//Merchant stuff 
			
			obj.put("MERCH_PAYMENTS_MODULE",  setParam("/merch_static_qr.jsp"));
			obj.put("MERCH_PAYMENTS_DYNMCQR_PAGE",  setParam("/merch_dynamic_qr.jsp"));
			obj.put("MERCH_ACCEPT_PAYMENTS_PAGE",  setParam("/merch_retail_payment.jsp"));
			obj.put("MERCHANT_PAYMENT_MODULE_CASHOUT_PAGE",  setParam("/merch_cashout.jsp"));
			obj.put("MERCHANT_PAYMENT_MODULE_TOPUP_PAGE",  setParam("/merch_topup.jsp"));
			
			/***********Merchant Transactions **********/
			obj.put("MERCHANT_TOPUP_TRANSACTIONS_PAGE",  setParam("/merch_topup_transactions.jsp"));
			obj.put("MERCHANT_CASHOUT_TRANSACTIONS_PAGE",  setParam("/merch_cashout_transactions.jsp"));
			obj.put("MERCHANT_RETAIL_TRANSACTIONS_PAGE",  setParam("/merch_retailpay_transactions.jsp"));
			
			// Bill Payment Module
			obj.put("MERCHANT_BILLPAYMENT_MODULE_BILLER_DETAILS_PAGE",  setParam("/merch_clientsdetails.jsp"));
			obj.put("MERCHANT_BILLPAYMENT_TRANSACTIONS",  setParam("/merch_billpaytransactions.jsp"));
			
			
			//merch retro
			obj.put("MERCHANT_REGISTRATION_PAGE",  setParam("/merch_register.jsp"));
			obj.put("MERCHANT_PROFILE_VIEW_PAGE",  setParam("/merch_profileview.jsp"));
			obj.put("MERCHANT_MSF_VIEW_PAGE",  setParam("/merch_msfview.jsp"));

			obj.put("MERCHANT_VIEW_DISPUTE",  setParam("/merch_viewdispute.jsp"));
			obj.put("MERCHANT_RAISE_DISPUTE",  setParam("/merch_raisedispute.jsp"));
			obj.put("MERCHANT_SHOW_SPECIFIC_DISPUTE_PAGE",  setParam("/merch_showspecdispute.jsp"));
			
			
			/*=================Operations========================*/
			
			obj.put("OPERATIONS_DASHBOARD_PAGE",  setParam("/ops_dashboard.jsp"));
			obj.put("OPS_SYS_CARDS_CREATEBIN_PAGE",  setParam("/ops_sys_cardscreatebin.jsp"));
			obj.put("OPS_SYS_CARDS_SHOWBINLIST_PAGE",  setParam("/ops_sys_cardsshowallbin.jsp"));
			obj.put("OPS_SYS_CARDS_EDISPECTBIN_PAGE",  setParam("/ops_sys_cardseditbin.jsp"));
			obj.put("OPS_SYS_CARDS_CREATECARDPRODUCT_PAGE",  setParam("/ops_sys_newcardproduct.jsp"));
			obj.put("OPS_SYS_CARDS_ALLCARDPRODUCT_PAGE",  setParam("/ops_sys_allcardproducts.jsp"));
			obj.put("OPS_SYS_CARDS_EDITCARDPRODUCT_PAGE",  setParam("/ops_sys_editcardproduct.jsp"));
			obj.put("OPS_MERCH_VIEW_MERCHANTS_DETAILS_PAGE", setParam("/ops_sys_viewmerchantdetails.jsp"));
			obj.put("OPS_SYS_MERCH_MCCMANAGE_PAGE",  setParam("/ops_sys_merchmccmanage.jsp"));
			obj.put("OPS_SYS_MERCH_INST_CREATE_NEW_PAGE",  setParam("/ops_sys_merchinstnew.jsp"));
			//obj.put("OPS_SYS_MERCH_INST_EDIT_PAGE",  setParam("/ops_sys_merchinstedit.jsp"));
			obj.put("OPS_SYS_MERCH_SETMSFPLAN_PAGE",  setParam("/ops_sys_merchallsysmsfplan.jsp"));
			obj.put("OPS_CUST_VIEW_CARD_DISPUTES_PAGE",  setParam("/ops_sys_viewcustdisputes.jsp"));
			obj.put("OPS_MERCH_DISPUTE_REASONS_PAGE", setParam("/ops_sys_merch_disputereasons.jsp"));
			obj.put("OPS_MERCH_VIEW_PENDING_MERCHANTS_PAGE", setParam("/ops_sys_viewpendingmerchdetails.jsp"));
			obj.put("OPS_CUST_VIEW_CARDS_PAGE", setParam("/ops_viewcarddetails.jsp"));
			obj.put("OPS_CUST_VIEW_PENDING_CUSTOMERS", setParam("/ops_sys_viewpendingcustomers.jsp"));
			obj.put("OPS_CUST_VIEW_CUSTOMERS_PAGE", setParam("/ops_sys_viewallcustomers.jsp"));
			obj.put("OPS_CUST_VIEW_EACH_DISPUTES_PAGE", setParam("/ops_sys_vieweachdispute.jsp"));
			obj.put("OPS_VIEW_PROFILE_PAGE", setParam("/ops_viewprofile.jsp"));
			obj.put("OPS_MANAGE_OPS_USERS_PAGE", setParam("/ops_sys_manageopsusers.jsp"));
			obj.put("OPS_CUST_RAISE_CUST_DISPUTE_PAGE", setParam("/ops_sys_viewcustdisputes.jsp"));
			obj.put("OPS_VIEW_LOYALTY_RULES_PAGE", setParam("/ops_sys_view_loyaltyrules.jsp"));
			obj.put("OPS_VIEW_CUSTOMER_LOYALTY_PAGE", setParam("/Ops_sys_viewcustomerloyalty.jsp"));
			obj.put("OPS_CUSTOMER_CLAIM_LOYALTY_PAGE", setParam("/ops_sys_customerclaim_rewards.jsp"));
			obj.put("OPS_MERCH_RAISE_MERCHANT_DISPUTE_PAGE", setParam("/ops_merch_raisedispute.jsp"));
			obj.put("OPS_VIEW_TRANSACTIONS_PAGE", setParam("/ops_viewtransactions.jsp"));
			
			//obj.put("OPS_CREATE_LOYALTY_RULES", setParam("/ops_sys_createloyaltyrules.jsp"));
			obj.put("OPS_MERCH_SET_MERCHANTS_PAGE", setParam("/ops_sys_setmerchant.jsp"));
			obj.put("OPS_MERCH_VIEW_MERCH_DISPUTE_PAGE", setParam("/ops_viewmerchantdisputes.jsp"));
		
			obj.put("OPS_MERCH_RAISE_MERCHANT_DISPUTE_PAGE", setParam("/ops_merch_raisedispute.jsp"));
			obj.put("OPS_VIEW_TRANSACTIONS_PAGE", setParam("/ops_viewtransactions.jsp"));
			obj.put("OPS_VIEW_RAISE_CUST_DISPUTE_PAGE", setParam("/ops_cust_raisedispute.jsp"));
			obj.put("OPS_VIEW_AUDIT_TRAIL_PAGE", setParam("/ops_sys_viewaudittrails.jsp"));
			obj.put("OPS_MERCH_VIEW_CALL_LOGS", setParam("/ops_sys_merchviewcalllogs.jsp"));
			obj.put("OPS_CUST_VIEW_CALL_LOGS_PAGE", setParam("/ops_sys_custviewcalllogs.jsp"));
			obj.put("OPS_CUST_WALLETS_PAGE", setParam("/ops_cust_walletmainpage.jsp"));	
			
			obj.put("OPS_MERCH_VIEW_MSF", setParam("/ops_merch_view_msf.jsp"));	
			obj.put("OPS_MERCH_VIEW_ALL_MSF_PLANS_FOR_BILLER", setParam("/ops_merch_view_allmsfplans.jsp"));	
			
			obj.put("OPS_MERCH_EDIT_SPECIFIC_MERCHANT_PAGE", setParam("/ops_sys_editspecific_merchant.jsp"));	
			
			// **************************
			obj.put("OPS_CUST_VIEWWALLETS_FORSPEC_CUSTOMER", setParam("/ops_cust_allwallets_viewpage.jsp"));
			obj.put("OPS_VIEW_SPECIFIC_DISPUTE", setParam("/ops_view_specific_dispute_page.jsp"));
			





			
			
									
			
										
			filewriter.write(obj.toJSONString());
			filewriter.flush();
        } catch (Exception e) {
            System.out.println("Exception is "+e.getMessage());
        }finally{
        	
        	if(obj!=null) obj = null; if(file!=null) file=null; if(filewriter!=null) filewriter=null;
        }
	}
	
	private static String setParam(String param) throws Exception{
		return AESEncrypter.encryptJson (param) ;
	}

}

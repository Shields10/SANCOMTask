package com.ppwallet.rules;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import com.google.gson.JsonObject;

public interface Rules {
	public static final String DEFAULT_LOGIN_MODULE="defaultloginpage";	public static final String LOGIN_VALIDATE="loginvalidate";
	public static final String LOGOUT_MODULE="lgtdefault";
	
	/*** Mobile App related interface */
	public static final String JSON_OPS_LOGIN_VALIDATE="jopslogin";	public static final String JSON_CUST_LOGIN_VALIDATE="jcuslogin";
	public static final String JSON_GET_KEY="jgetkey";
	
	/*** User Profile related interface */
	public static final String PROFILE_USER_DASHBOARD="Dashboard";	public static final String PROFILE_USER_VIEWPROFILE="View Profile";
	public static final String PROFILE_USER_REGISTER_BANKPAGE="Register Bank"; public static final String PROFILE_REGISTERBANK_DETAILS="Registerbankdetails";
	public static final String PROFILE_USER_EDITPROFILE_UPDATE="editcustuserupdate";
	public static final String PROFILE_USER_EDITPROFILE_SHOW="editcustusershow";public static final String PROFILE_USER_REGISTER_MPESAPAGE="Register Mpesa"; 
	public static final String PROFILE_USER_REGISTER_MPESANUMBER="RegMpesaNo";  
	
	
	
	//second phase mobile additions
	public static final String CUST_SELF_REGISTRATION="registercust";  
	public static final String  JSON_CUST_REGISTRATION="jsonregistercust";  
	public static final String  PERSO_CUST_REGISTRATION="persorgn";  
	public static final String  JSON_PERSO_CUST_REGISTRATION="jpersorgn";  
	public static final String  PAYBILL_WITHCARD="billpayreqconfirmvbycard";  
	public static final String  TOPUP_WITHCARD="topupwalletbycard";  
	public static final String  CARD_REGISTRATION_PAGE="Register Cards";  
	public static final String  PAYMENT_REGISTER_NEW_CARD_REQ="regnewcardreq";  
	public static final String  PAYMENT_CARDS_SHOWALLCARDS_USER="showuserallcards";  
//	PAYMENT_CARDS_SHOWALLCARDS_USER
	
	/*** Customer Wallet related interface */
	public static final String WALLET_VIEW_WALLET_PAGE="View Wallet";  	public static final String WALLET_TOPUP_WALLET_WELCOMEPAGE="Topup Wallet";
	public static final String WALLET_TOPUP_BY_BANK="topupwalletbybank"; 	public static final String WALLET_TOPUP_BY_MPESA="topupwalletbympesa";
	public static final String WALLET_SHOWUSER_TXN_ALLWALLETS_PAGE="Wallet Transactions"; public static final String WALLET_SHOW_INDIVIDUAL_TRANSACTIONS="showindvwaltxns"; 
	public static final String WALLET_TRANSFER_FUND_PAGE="Transfer Funds";public static final String WALLET_SEND_MONEY_PAGE="walletsendmoneyp2p";
	public static final String WALLET_SEND_MONEY_REGISTER_RECEIVERPAGE="custregisterfundreceiver"; public static final String WALLET_P2P_REG_WALLET="custp2pregisterwallet"; 
	public static final String WALLET_P2P_REG_SEARCH_WALLET = "custp2psearchwallet";  public static final String WALLET_P2P_REG_RECEIVER_WALLETID_REGISTER = "custp2pregwalletid";
	public static final String CUST_VIEW_REWARDS_PAGE="View Rewards";  
	public static final String CUST_CLAIM_REWARDS_PAGE="Claim Rewards";
	public static final String CUST_CLAIM_EACH_REWARD="custclaimeachreward";
	public static final String CUST_CLAIM_ALL_REWARD="custclaimallreward";
	public static final String CUST_DISPLAY_SELECTED_WALLETPOINTS="displaywalletpoints";
	
	public static final String PAYMENT_BILL_PAYPAGE="Pay Bills";
	public static final String PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE="custregisterbillers";
	public static final String PAYMENT_BILLPAY_BILLER_NEWREGISTER_PAGE="custbillernewregister";
	public static final String PAYMENT_BILLPAY_BILLPAYRREQ="billpayrequest";
	public static final String PAYMENT_BILLPAY_REQCONFIRM="billpayreqconfirm";
	public static final String PAYMENT_BILLPAY_SHOW_BILLERS="billpayshowbillers";
	
	
	//new stuff
	/*customer related stuff */
	public static final String CUST_VIEW_DISPUTE="View Dispute";
	public static final String CUST_RAISE_DISPUTE="Raise Dispute";
	public static final String CUST_ADD_NEW_DISPUTE="custadddpt";
	public static final String CUST_SHOW_SPECIFIC_DISPUTE_PAGE="custshowsspecdispute";
	public static final String CUST_ADD_COMMENT_FOR_A_DISPUTE="custdptaddcomments"; 
	public static final String CUST_UPDATE_DISPUTE_STATUS="custupdatedispstatus";
	
	/*==========================mobile stuff start =======================*/
	
//	customer mobile staff
	public static final String JSON_CUST_VIEW_PROFILE="jcustviewprf";
	public static final String JSON_CUST_UPDATE_PROFILE="jcustupdateprf";
	public static final String JSON_CUST_ADD_NEW_DISPUTE="jcustadddpt";
	public static final String JSON_CUST_RAISE_DISPUTE="jcustraisedispute";
	public static final String JSON_CUST_VIEW_DISPUTE="jviewdispute";
	public static final String JSON_CUST_SHOW_SPECIFIC_DISPUTE_PAGE="jshowspecdsptpage";
	public static final String JSON_CUST_ADD_COMMENT_FOR_A_DISPUTE="jcustdptaddcomments";
	public static final String JSON_CUST_UPDATE_DISPUTE_STATUS="jcustupdatedispstatus";
	


	
	public static final String JSON_CUST_GET_WALLET_DETAILS="jcustwalletdetails";
	public static final String JSON_CUST_RETAIL_PAYMENT="jcustretailpayment";
	public static final String JSON_CUST_RETAIL_PAYMENT_GRAPH="jcustretailpaymentgraph";
	public static final String JSON_CUST_RETAIL_PAYMENT_TABLE="jcustviewretailpayment";
	public static final String JSON_CUST_WALLET_TOPUP_WITH_QR="jcustopupwithqr";
	public static final String JSON_CUST_WALLET_CASHOUT_WITH_QR="jcustcashoutwithqr";
	
	// Ben PayBill Mobile module
	
	public static final String JSON_PAYMENT_BILL_PAYPAGE="jbillpaypage";
	public static final String JSON_PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE="jcustregisterbillers";
	public static final String JSON_PAYMENT_BILLPAY_BILLER_NEWREGISTER_PAGE="jcustbillernewregister";
	public static final String JSON_PAYMENT_BILLPAY_BILLPAYRREQ="jbillpayrequest";
	public static final String JSON_PAYMENT_BILLPAY_PAYNOW="jbillpaynow";
	public static final String JSON_PAYMENT_VIEW_PAYBILL_TXN="jviewbilltxn";
	public static final String JSON_PAYMENT_VIEW_PAYBILL_TXN_GRAPH="jviewbilltxngraph";
	
	
	//Ben Wallet stuff
		public static final String JSON_WALLET_VIEW_WALLET_PAGE="jviewwallet";

		public static final String JSON_WALLET_SHOWUSER_TXN_ALLWALLETS_PAGE="jviewtxn";
		public static final String JSON_WALLET_SHOWUSER_TXN_INDIVIDUALWALLETS_PAGE="jviewtxnindvwallet";
		public static final String JSON_WALLET_SHOWUSER_TXN_INDIVIDUALWALLETS="jshowallettxn";
		public static final String JSON_WALLET_SHOWUSER_TXN_INDIVIDUALWALLETS_GRAPH="jgraphwallettxn";
		public static final String JSON_WALLET_TOPUP_WALLET_WELCOMEPAGE="jtopupwallet";
		public static final String JSON_WALLET_TOPUP_BY_BANK="jtopupbybank";
		public static final String JSON_WALLET_TOPUP_BY_MPESA="jtopupbympesa";
		public static final String JSON_WALLET_TRANSFER_FUND_PAGE="jtransferfunds";
		public static final String JSON_WALLET_SEND_FUNDS="jsendfunds";
		public static final String JSON_WALLET_P2P_REG_SEARCH_WALLET="jp2psearch";
		public static final String JSON_WALLET_P2P_REG_RECEIVER_WALLETID_REGISTER="jcustp2pregwalletid";
		public static final String JSON_USER_REGISTER_BANKPAGE="jregisterbankpage";
		public static final String JSON_REGISTERBANK_DETAILS="jregisterbankdetails";
		public static final String JSON_REGISTER_MPESAPAGE="jregistermpesa";
		public static final String JSON_REGISTER_MPESANUMBER="jregistermpesano";
		
		// Loyalty Module Mobile
		public static final String JSON_CUST_VIEW_REWARDS_RULES="jrewardrules";
		public static final String JSON_CUST_VIEW_REWARDS_PAGE="jviewreward";
		public static final String JSON_CUST_CLAIM_EACH_REWARD="jclaimeachreward";
		public static final String JSON_CUST_CLAIM_ALL_REWARD="jclaimallreward";
		
		
		/*==========================mobile stuff end  =======================*/

	
	public static final String CUST_VIEW_REWARDS_RULES_PAGE="Reward Rules";
	

	
	/*** Batch System related interface */
	public static final String BATCH_PROCES_PAGE="batchprocess";

	/*** Admin Cards System related interface */
//	public static final String OPS_SYS_CARDS_VIEWALL_BIN_PAGE="View BIN";
//	public static final String OPS_SYS_CARDS_ADD_NEW_BIN="sysaddnewbin";	public static final String OPS_SYS_CARDS_UPDATECARDSBIN="sysupdatecardbin";	
//	public static final String OPS_SYS_CARDS_CREATECARDPRODUCT="sysaddcardprod";	public static final String OPS_SYS_CARDS_VIEWALLPRODUCT_PAGE="View Card Products";
//	public static final String OPS_SYS_CARDS_EDITSPECPRODUCT="sysupdatecardproduct";
//	
//	/*** Admin Merchant System related interface */
//	public static final String OPS_SYS_MERCH_MCCMANAGE_PAGE="Set MCC";	public static final String OPS_SYS_MERCH_EDITMCC="editsysmerchmcc";
//	public static final String OPS_SYS_MERCH_ADDMCC="addsysmerchmcc";
//	
//	public static final String OPS_SYS_MERCH_SET_INST_PAGE="Set Institution";	public static final String OPS_SYS_MERCH_ADD_NEW_INST="sysacqaddinst";
//	public static final String OPS_SYS_MERCH_EDIT_INST="frmeditinst";	public static final String OPS_SYS_MERCH_SETMSFPLAN_PAGE="Set MSF Plan";
//	public static final String OPS_SYS_MERCH_ADD_MSFPLAN="sysaddmsfplan";			public static final String OPS_SYS_MERCH_EDIT_MSFPLAN="syseditmsfplan";
//	
//	public static final String OPS_MERCH_SET_MERCHANTS_PAGE="Set Merchants";	public static final String OPS_MERCH_ADDNEW_MERCHANT="sysaddnewmerch";
//	public static final String JSON_OPS_MERCH_GETSPCMERCH="jsongetspcmerch";
//	
	
	//Merchant Rules//
	
	
	

	/**Merchant  menu items **/
	//public static final String MERCHANT_STATIC_QR_PAGE = "Staticc QR";
	public static final String MERCH_PAYMENTS_MODULE_QRPAGE = "Static QR";
	public static final String MERCH_PAYMENT_MODULE_DQR_PAGE = "Dynamic QR";
	public static final String MERCHANT_PAYMENT_MODULE_RETAIL_PAYMENT_PAGE = "Retail Payments";
	public static final String MERCHANT_PAYMENT_MODULE_CASHOUT_PAGE = "CashOut";
	public static final String MERCH_PAYMENT_MODULE_DYNAMICQR = "generatemerchdyncqr";
	public static final String MERCH_PAYMENT_MODULE_RETAIL_PAYMENTSQR = "merchretailpaymentqr";
	public static final String MERCH_PAYMENT_MODULE_CASHOUT = "merchcashoutqr";
	public static final String MERCHANT_PAYMENT_MODULE_TOPUP_PAGE = "Top Up";
	public static final String MERCHANT_PAYMENT_MODULE_TOPUPQR = "topupqr";
	
	/*** Merchant module retro*/
	public static final String REGISTRATION_REQ_PAGE="regrequser";
	public static final String REGISTRATION_MERCHANT_ADD="merchregister";
	public static final String MERCHANT_VIEW_PROFILE_PAGE="Profile";
	public static final String MERCHANT_DOWNLOAD_KYC_DOC="downloadkyc";
	public static final String MERCHANT_UPDATE_PROFILE="merchupdateprf";
	public static final String MERCHANT_VIEW_MSF="View Rates";
	
	/**Merchant web Dispute module**/
	public static final String MERCHANT_RAISE_DISPUTE="Raise Disputes";
	public static final String MERCHANT_VIEW_DISPUTE="View Disputes";
	public static final String MERCHANT_ADD_NEW_DISPUTE="merchadddpt";
	public static final String MERCHANT_SHOW_SPECIFIC_DISPUTE_PAGE="merchshowsspecdispute";
	public static final String MERCHANT_ADD_COMMENT_FOR_A_DISPUTE="dptaddcomments"; 
	public static final String MERCHANT_UPDATE_DISPUTE_STATUS="updatedispstatus"; 
	
	
	/***********Transactions Module**********/
	public static final String MERCHANT_TOPUP_TRANSACTIONS_PAGE="Topup Transactions";
	public static final String MERCHANT_CASHOUT_TRANSACTIONS_PAGE="Cashout Transactions";
	public static final String MERCHANT_RETAIL_TRANSACTIONS_PAGE="Retail Pay Transactions";


	
	/* merchant mobile stuff */ 
	public static final String JSON_MERCHANT_MOBILE_RETAIL_PAYMENT="jsonretailpmtsrule";
	//public static final String JSON_MERCHANT_MOBILE_CASHOUT="cshoutrule";
	public static final String JSON_MERCHANT_MOBILE_CASHOUT_PAYMENT="jsoncashoutrule";
	public static final String JSON_MERCHANT_MOBILE_TOPUP_PAYMENT="jsontopuprule";
	public static final String JSON_MERCHANT_MOBILE_BILLER_DETAILS="mbllrsrle";
	public static final String JSON_MERCHANT_MOBILE_BILLPAY_TXN_DETAILS="jsonbillpaytxndetails";
	public static final String JSON_MERCHANT_MOBILE_MCC_GROUP="jmerchmccrule";
	public static final String JSON_MERCHANT_MOBILE_SELF_REGISTRATION="jmerchregistrationrule";
	
	/*merchant mobile profile stuff*/
	public static final String JSON_MERCHANT_MOBILE_VIEW_MERCHANT_PRF="merchantprfrl";
	public static final String JSON_MERCHANT_MOBILE_EDIT_PERSONAL_PRF="merchupdpersonalprfrl";
	public static final String JSON_MERCHANT_MOBILE_EDIT_CONTACT_PRF="merchupdcontactprfrl";
	public static final String JSON_MERCHANT_MOBILE_EDIT_COMPANY_PRF="merchupdcompanyprfrl";
	public static final String JSON_MERCHANT_MOBILE_DOWNLOAD_KYC_DOCS="merchdownloaddocsrl";
	
	
	/*merchant dispute module*/
	public static final String JSON_MERCHANT_MOBILE_SHOW_DISPUTE_REASONS="merchraisedisputesrl";
	public static final String JSON_MERCHANT_MOBILE_RAISE_DISPUTES="merchadddisputerl";
	public static final String JSON_MERCHANT_MOBILE_VIEW_DISPUTE="merchviewdisputesrl";
	public static final String JSON_MERCH_MOBILE_SHOW_SPECIFIC_DISPUTE_PAGE="merchtrackdisputesrl";
	public static final String JSON_MERCH_MOBILE_ADD_COMMENT_FOR_A_DISPUTE="merchtdispaddcomments";
	
	/*merchant msf module*/
	public static final String JSON_MERCHANT_MOBILE_VIEW_MSF_PLAN="merchviewmsfplanrl";
	
	/*merchant msf module*/
	public static final String JSON_MERCHANT_MOBILE_VIEW_TRANSACTIONS="merchtransactionrl";
	

	/** Bill Payment Menu Items**/
	public static final String MERCHANT_BILLPAYMENT_MODULE_BILLER_DETAILS_PAGE = "Biller Details";
	public static final String  MERCHANT_BILLPAYMENT_TRANSACTIONS ="View Transactions";
	

	/*** Admin Merchant System related interface */
	public static final String DISP_MERCH_QR_CODE="generatemerchqr"; public static final String GET_MERCH_DETAILS = "getMerchDetails";
	
	
	
	/**Operations */

    
	/*** Admin Cards System related interface */
	public static final String OPS_SYS_CARDS_VIEWALL_BIN_PAGE="View BIN";
	public static final String OPS_SYS_CARDS_ADD_NEW_BIN="sysaddnewbin";	public static final String OPS_SYS_CARDS_UPDATECARDSBIN="sysupdatecardbin";	
	public static final String OPS_SYS_CARDS_CREATECARDPRODUCT="sysaddcardprod";	public static final String OPS_SYS_CARDS_VIEWALLPRODUCT_PAGE="View Card Products";
	public static final String OPS_SYS_CARDS_EDITSPECPRODUCT="sysupdatecardproduct";
	
	/*** Admin Merchant System related interface */
	public static final String OPS_SYS_MERCH_MCCMANAGE_PAGE="Set MCC";	public static final String OPS_SYS_MERCH_EDITMCC="editsysmerchmcc";
	public static final String OPS_SYS_MERCH_ADDMCC="addsysmerchmcc";  
	
	
	
	//public static final String OPS_SYS_MERCH_SET_INST_PAGE="Set Institution";	
	public static final String OPS_SYS_MERCH_INST_CREATE_NEW_PAGE="Set Institution";	
	public static final String OPS_SYS_MERCH_ADD_NEW_INST="sysacqaddinst"; public static final String OPS_SYS_MERCH_EDIT_INST="sysacqeditinst";	
	
	public static final String OPS_SYS_MERCH_SETMSFPLAN_PAGE="Set MSF Plan";
	public static final String OPS_SYS_MERCH_ADD_MSFPLAN="sysaddmsfplan";			public static final String OPS_SYS_MERCH_EDIT_MSFPLAN="syseditmsfplan";
	public static final String OPS_SYS_MERCH_VIEW_MSF="Allocate MSF";
	
	
	public static final String OPS_REGISTRATION_MERCHANT_ADD="opsmerchregister";			public static final String OPS_MERCHANT_EDIT="editmerchant";		
	//public static final String MERCHANT_UPDATE_PROFILE="merchupdateprf";
	public static final String OPS_MERCHANT_DOWNLOAD_KYC_DOC="opsdownloadkyc";						
	public static final String OPS_MERCH_SET_MERCHANTS_PAGE="Set Merchants"; 			 public static final String OPS_MERCH_VIEW_CALL_LOGS = "Merchant Call Logs";  
	public static final String OPS_MERCH_ADD_CALL_LOGS = "addmerchcalllogs";
	public static final String JSON_OPS_MERCH_GETSPCMERCH="jsongetspcmerch";			public static final String OPS_MERCH_ADD_MERCHANT_PAGE = "opsaddmerch";	
	public static final String OPS_MERCH_DISPUTE_REASONS_PAGE = "Dispute Reasons"; 		public static final String OPS_MERCH_VIEW_PENDING_MERCHANTS_PAGE="View Pending Merchants";
	public static final String OPS_MERCH_ADD_MERCHANTS_PAGE = "addpendingmerchant";		public static final String OPS_MERCH_VERIFY_PENDING_MERCHANTS="verifypendingmerchants";
	public static final String OPS_MERCH_ALLOCATE_MSF_PLANS = "allocatesmfplanids";		public static final String OPS_MERCH_UPDATE_PLANSTATUS_FOR_BILLER="updatedplanstatusforbiller";
	
	public static final String OPS_MERCH_SHOWSPECIFIC_MERCH_PAGE = "showspecificmerchant"; public static final String OPS_MERCH_EDITSPECIFIC_MERCH_PAGE = "editspecificmerchant";
	
	public static final String OPS_MERCH_DISPUTE_REASONS_ADD = "addsysmerchdispreason";	public static final String OPS_MERCH_DISPUTE_REASONS_EDIT = "editsysmerchdispreason";
	
	public static final String OPS_CUST_SHOW_EACH_DISPUTE_PAGE = "showeachdispute";		
	public static final String OPS_CUST_UPDATE_DISPUTE_STATUS = "updatedisputestatus";			public static final String OPS_MERCH_RAISE_MERCHANT_DISPUTE_PAGE = "opsraisedisp";
	public static final String OPS_RAISE_DISPUTE_PAGE = "Ops Raise Disputes";
	public static final String OPS_MERCH_VIEW_MERCH_DISPUTE_PAGE = "View Merchant Disputes";    
	
	public static final String OPS_CUST_RAISE_CUST_DISPUTE_PAGE ="Submit Cards Disputes";		public static final String OPS_CUST_VIEW_CARD_DISPUTES_PAGE="View Cards Disputes";
	public static final String OPS_VIEW_RAISE_CUST_DISPUTE_PAGE = "Raise Cards Disputes";		public static final String OPS_EDIT_DISPUTE_PAGE="Edit Disputes";

	
	/*** Admin customer System related interface */
	public static final String OPS_CUST_VIEW_CARDS_PAGE  ="View Cards"; 						public static final String OPS_CUST_EDIT_CUSTDETAILS_PAGE = "editcustomerdetails";
	public static final String OPS_CUST_VIEW_PENDING_CUSTOMERS ="View Pending Customers";       public static final String OPS_CUST_VERIFY_PENDING_CUSTOMERS="verifypendingcustomers";
	public static final String OPS_CUST_VIEW_CUSTOMERS_PAGE = "View Customers";						public static final String OPS_CUST_WALLETS_PAGE = "Manage Wallets";
	public static final String OPS_CUST_VIEW_CALL_LOGS_PAGE = "Customer Call Logs";                public static final String OPS_CUST_ADD_CALL_LOGS = "addcustcalllogs";
	public static final String OPS_CUST_SHOW_WALLETS_FOR_A_CUSTOMER = "showwalletforspeccustomer"; public static final String JSON_OPS_CUST_GETLASTHUNDRED_TXNS_FORWALLET = "opsgetlasthundredtxnsforwallet";
	
	
	
	public static final String OPS_VIEW_PROFILE_PAGE="Ops Profile";	public static final String OPS_ADMIN_UPDATE_PROFILE="opseditadminprofile";
	public static final String OPS_MANAGE_OPS_USER_PAGE="Manage Ops Users";		public static final String OPS_ADD_OPS_USER="opssysaddopsuser";	
	public static final String OPS_EDIT_OPS_USER="opssyseditopsuser";

	/*** loyalty System related interface */
	public static final String OPS_VIEW_LOYALTY_RULES_PAGE ="Create Loyalty Rule";	 	public static final String OPS_ADD_LOYALTY_RULE = "addloyaltyrule";
	public static final String OPS_EDIT_LOYALTY_RULE = "editloyaltyrule";
	public static final String OPS_VIEW_CUSTOMER_LOYALTY_PAGE ="View Customer Loyalty";  
	public static final String OPS_VIEW_CUSTOMER_LOYALTY_CLAIM_PAGE ="opscustclaimloyalty";  public static final String OPS_CUST_CLAIM_ALL_REWARD = "opscustclaimallpoints";
	
	/*** Charge-back and Transaction Reversals*/
	public static final String OPS_VIEW_TRANSACTIONS_PAGE = "Transactions";	
	/*** Audit Trail */
	public static final String OPS_VIEW_AUDIT_TRAIL_PAGE = "View Audit Logs";
	public static final String OPS_VIEW_SPECIFIC_PAGE = "opsshowsspecdispute";
	public static final String OPS_UPDATE_DISPUTE_STATUS = "opsupdatedispstatus";
	public static final String OPS_ADD_DISPUTE_COMMENT = "opsdptaddcomments";
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,ServletContext ctx) throws Exception;
	
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception;;
	
	public void callException(HttpServletRequest request, HttpServletResponse response,ServletContext ctx,
			HttpSession session, Exception e, String msg) throws Exception;;
	
	public void performUploadOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			List<FileItem> multiparts, ServletContext ctx) throws Exception;;
}

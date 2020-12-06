
package com.ppwallet.implement;

public class ActionFactoryImpl implements ActionFactory{
	private Action action;
	private final static String LOGOUT_MODULE= "lgt";
	private final static String LOGIN_MODULE = "lgn";
	//added
	private final static String CUSTOMER_PROFILE  = "custprf";
	private final static String WALLET_MODULE = "wal";
	private final static String REWARDS_MODULE  = "rwd";
	private final static String PAYMENT_MODULE  = "pmt";
	
	private final static String FORGOT_PASSWORD = "fgt";

	private final static String BATCH_MODULE = "batch";
	
	
	//second phase customer addition
	private final static String CUSTOMER_SELF_REGISTRATION = "custrgn";
	
	
	
	
	
	
	
	
	
	
	

	
	
	/**Merchant Web*/
	private final static String DISP_MERCH_QR_CODE = "merch";
		//private final static String MERCH_PAYMENT_MODULE_DQR ="merchdqr";
		/** QR payments*/
	private final static String MERCHANT_PAYMENT_MODULE_RETAIL_PAYMENTQR ="merchretailpmtqs";
	private final static String MERCHANT_PAYMENT_MODULE_CASHOUTQR ="merchcashoutqs";
	private final static String MERCHANT_PAYMENT_MODULE_TOPUPQR ="merchtopupqr";
		 
		// merchant module 
	private final static String  REGISTRATION_MODULE= "rgn";
	private final static String MERCH_VIEW_PROFILE_PAGE = "merchprf";//change to merch and change this to 
	private final static String FILE_UPLOAD_OPERATIONS_MODULE = "fud";	
	private final static String MERCHANT_DISPUTE_MANAGEMENT = "merchdspt";
	public final static String MERCH_PAYMENTS_MODULE_PAGE = "pymts";
	private final static String MERCHANT_BILLPAYMENT_MODULE ="bllpy";
	private final static String MERCHANT_TRANSACTION_MODULE ="merchtnxs";
	
	
	/*merchant mobile actions */
	private final static String JSON_MERCHANT_MOBILE_ACCEPT_PAYMENT = "jsonretailptpmtsqs";
	private final static String JSON_MERCHANT_MOBILE_CASHOUT_PAYMENT = "jmerchcshtqs";
	private final static String JSON_MERCHANT_MOBILE_TOPUP_PAYMENT = "jsontopupqs";
	private final static String JSON_MERCHANT_MOBILE_BILLER_DETAILS = "mbllrsqs";
	private final static String JSON_MERCHANT_MOBILE_TRANSACTION_DETAILS = "txnqs";
	private final static String JSON_MERCHANT_MOBILE_MCC_GROUP = "jmccgroup";
	private final static String JSON_MERCHANT_MOBILE_SELF_REG= "jmerchantreg";
	
	//Mobile action implementation
	private final static String JSON_MERCHANT_MOBILE_VIEW_PRF= "profileqs";
	
	//Mobile disputes action implementation
	private final static String JSON_MERCHANT_MOBILE_DISPUTES_MODULE= "disputesqs";
	
	//Mobile msf action implementation
	private final static String JSON_MERCHANT_MOBILE_MSF_MODULE= "merchmsfqs";
	
	private final static String JSON_MERCHANT_MOBILE_TRANSACTION_MODULE= "merchtransactionsqs";
	
	
	
	
	
	
	
	
	

	//NEW ADDITION 
		//CUSTOMER REALTED STUFF
		private final static String CUSTOMER_DISPUTE_MANAGEMENT = "custdspt";	


	//private final static String FILE_UPLOAD_OPERATIONS_MODULE = "fud";
		
		/*===================================OPERATIONS ACTIONS START ===================================*/
		
		private final static String OPS_SYSTEM_CARD_MODULE = "syscard";
		private final static String OPS_SYSTEM_MERCHANT_MODULE = "sysmerch";
		private final static String OPS_CUSTOMER_MODULE = "opscust";
		private final static String OPS_MERCHANT_MODULE = "opsmerch";
		private final static String OPS_LOYALTY_MODULE = "opslyt";
		
		private final static String OPS_MERCHANT_DETAILS_MODULE = "dash";
		private final static String OPS_VIEW_CUSTOMERS_MODULE ="View Customers";
		private final static String OPS_MERCH_DISPUTE_REASONS_PAGE =" Dispute Reasons";
		

		private final static String OPS_DISPUTE_MODULE ="opsdisp";	
		private final static String OPS_PROFILE_MODULE ="prf";

		
		
		
		
		


	@Override
	public Action createAction(String actionName) {
		switch (actionName){
		 	case LOGIN_MODULE: case LOGOUT_MODULE: case FORGOT_PASSWORD: 
		 		action=new UserLoginActionImpl();
			break;
		 	 
		 	case CUSTOMER_PROFILE: case CUSTOMER_SELF_REGISTRATION:
		 		action=new CustomerActionImpl();
		 	break;
		 	case WALLET_MODULE:
		 		action=new WalletActionImpl();
		 		break;
		 	case REWARDS_MODULE:
		 		action=new RewardsActionImpl();
		 	break;
		 	case PAYMENT_MODULE:
		 		action=new PaymentActionImpl();
		 	break;

		 	case BATCH_MODULE:
		 		action=new BatchActionImpl();
		 	break;
		 	
		 	//NEW ADDITION 
			//CUSTOMER REALTED STUFF
		 	case CUSTOMER_DISPUTE_MANAGEMENT:
		 		action= new CustDisputeActionImpl();
		 		break;
		 	

		 	//Merchant Actions
				/*** Merchant Retrofitting Stuff***/
		 	case REGISTRATION_MODULE:
		 		action=new RegistrationActionImpl();
		 	break;	
		 
		 	
		 	case FILE_UPLOAD_OPERATIONS_MODULE:
		 		action=new FileUploadOperationsActionImpl();
		 	break;
		 	
		 	case MERCH_VIEW_PROFILE_PAGE:   case MERCHANT_TRANSACTION_MODULE: case DISP_MERCH_QR_CODE:
		 		action=new MerchantActionImpl();
			break;
			
		 	case  MERCHANT_BILLPAYMENT_MODULE: case JSON_MERCHANT_MOBILE_ACCEPT_PAYMENT: case JSON_MERCHANT_MOBILE_CASHOUT_PAYMENT: case JSON_MERCHANT_MOBILE_TOPUP_PAYMENT: case JSON_MERCHANT_MOBILE_BILLER_DETAILS:
		 	case JSON_MERCHANT_MOBILE_TRANSACTION_DETAILS:  case JSON_MERCHANT_MOBILE_MCC_GROUP: case JSON_MERCHANT_MOBILE_SELF_REG:
		 		action = new MerchMobileActionImpl();
		 		break;
		 		
				
			 	/* Merchant Mobile Profile Stuff*/
			 		
			 	case JSON_MERCHANT_MOBILE_VIEW_PRF:
			 		action = new MerchantMobilePrfActionImpl();
			 		
			 	break;
			 	
			 	//merchant Mobile Disputes
			 	case JSON_MERCHANT_MOBILE_DISPUTES_MODULE: case MERCHANT_DISPUTE_MANAGEMENT:
			 		action = new MerchDisputeActionImpl();
			 		
			 		break;
			 		//merchant Mobile Disputes
			 	case JSON_MERCHANT_MOBILE_MSF_MODULE:
			 		action = new MerchMsfActionImpl();
			 		
			 		break;
			 		
			 		//merchant Mobile Disputes
			 	case JSON_MERCHANT_MOBILE_TRANSACTION_MODULE:
			 		action = new MerchTransactionActionImpl();
			 		
			 		break;
			 		
			 	case MERCHANT_PAYMENT_MODULE_RETAIL_PAYMENTQR: case MERCHANT_PAYMENT_MODULE_CASHOUTQR: 
			    case MERCHANT_PAYMENT_MODULE_TOPUPQR: 
			 	case MERCH_PAYMENTS_MODULE_PAGE:
			 	action=new MerchPaymentActionImpl();
				break;
		 	
				
				/*========================================OPERATIONS ACTION CASE START================================*/
			
			 	case OPS_SYSTEM_CARD_MODULE: case OPS_SYSTEM_MERCHANT_MODULE: case OPS_CUSTOMER_MODULE: case OPS_MERCHANT_MODULE:
		 		case OPS_MERCHANT_DETAILS_MODULE: case OPS_VIEW_CUSTOMERS_MODULE: 
		 		case OPS_DISPUTE_MODULE:	case OPS_MERCH_DISPUTE_REASONS_PAGE:
		 		action=new OperationsActionImpl();
			break;

		 	case OPS_PROFILE_MODULE:
		 			action = new ProfileActionImpl();
		 	break;
			
			case OPS_LOYALTY_MODULE:
				action=new OpsLoyaltyActionImpl();
			break;
			
				
				
				
			
		/*
		 * case FILE_UPLOAD_OPERATIONS_MODULE: action=new
		 * FileUploadOperationsActionImpl(); break;
		 */
				
				
				
					
			/*		 	case REGISTRATION_MODULE:
		 		action=new RegistrationActionImpl();
		 	break;
		 	case PROFILE_MODULE:
		 		action=new ProfileActionImpl();
		 	break;
		 	case WALLET_MODULE:
		 		action=new WalletActionImpl();
		 	break;		 		
		 	case PAYMENT_MODULE:
		 		action=new PaymentActionImpl();
		 	break;
		 	case REWARDS_MODULE:
		 		action=new RewardsActionImpl();
		 	break;
		 	case MARKETPLACE_MODULE:
		 		action=new MarketplaceActionImpl();
		 	break;
		 	case FILE_UPLOAD_OPERATIONS_MODULE:
		 		action=new FileUploadOperationsActionImpl();
		 	break;
		 	case OPERATIONS_MODULE:
		 		action=new OperationsActionImpl();
		 	break;*/
		}
		return this.action;
	}

}

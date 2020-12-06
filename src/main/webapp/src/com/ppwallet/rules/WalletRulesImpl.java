package com.ppwallet.rules;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.ppwallet.model.BankDetails;
import com.ppwallet.model.CardDetails;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.MpesaDetails;
import com.ppwallet.model.Transaction;
import com.ppwallet.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.CashOutDao;
import com.ppwallet.dao.PaymentDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.dao.WalletDao;
import com.ppwallet.model.Wallet;
import com.ppwallet.utilities.Utilities;

public class WalletRulesImpl implements Rules {
	
private static String className = WalletRulesImpl.class.getSimpleName();
private static String classname = WalletDao.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(true);
		switch (rules){
			
		
		
		/* This returns page with all customer wallets*/
		
		case Rules.WALLET_VIEW_WALLET_PAGE:
			 try {
				 	request.setAttribute("langPref", "en");				
					request.setAttribute("lastaction", "wal");	
					request.setAttribute("lastrule", "View Wallet");
					ArrayList<Wallet>  arrWallet = null;
					arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
					request.setAttribute("walletlist", arrWallet);
					response.setContentType("text/html");
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerWalletMainPage()).forward(request, response);
						} finally {
							if(arrWallet!=null) arrWallet=null; 
						}
				}catch (Exception e) {
					callException(request, response, ctx, session,e, e.getMessage());
				}
			 break;
			 
			 
			 case Rules.WALLET_TOPUP_WALLET_WELCOMEPAGE:
					try {
						ArrayList<Wallet>  arrWallet = null; ArrayList<BankDetails> arrBankdetails = null; String walletId = "";
						ArrayList<MpesaDetails> arrMpesaDetails = null; ArrayList<CardDetails> cardDtls = null;
						//if(request.getParameter("hdntokenid")!=null) 		tokenId = StringUtils.trim(request.getParameter("hdntokenid"));
						if(request.getParameter("hdnwalletid")!=null) 		walletId = StringUtils.trim(request.getParameter("hdnwalletid"));
						String relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
						arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( relationshipNo );
						arrBankdetails = (ArrayList<BankDetails>)PaymentDao.class.getConstructor().newInstance().getBankDetails( relationshipNo );
						arrMpesaDetails = (ArrayList<MpesaDetails>)PaymentDao.class.getConstructor().newInstance().getMpesaDetails( relationshipNo );
						cardDtls= (ArrayList<CardDetails>)WalletDao.class.getConstructor().newInstance().getCardDetails(((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
						
						
						
						request.setAttribute("walletlist", arrWallet);		//request.setAttribute("tokenlist", arrCardToken);
						request.setAttribute("bankdetails", arrBankdetails);
						request.setAttribute("mpesadetails", arrMpesaDetails);
						request.setAttribute("carddetails", cardDtls);
						request.setAttribute("langPref", "en");				
						request.setAttribute("lastaction", "wal");	request.setAttribute("lastrule", WALLET_TOPUP_WALLET_WELCOMEPAGE);
						response.setContentType("text/html");
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerWalletTopUpPage()).forward(request, response);

						}finally {
							if(arrWallet!=null) arrWallet = null; if(arrBankdetails!=null) arrBankdetails = null; if(walletId!=null) walletId = null;
						}
					}catch (Exception e) {
						callException(request, response, ctx, session,e, e.getMessage());
					}
					
			break;
			
				  case Rules.WALLET_TOPUP_BY_BANK:
				 try {
					ArrayList<Wallet> arrWallet = null;  ArrayList<BankDetails> arrBankdetails = null;
					
					String walletId = null;	  String topUpAmount = null; 	String bankId = null;	String bankCode = null;
					String bankName = null;	  String bankAccountNo = null; 	String bankAccountName = null; 
					String relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					boolean success = false;	String transactionCode = null;	
					
					if(request.getParameter("walletid")!=null) 		        walletId = StringUtils.trim(request.getParameter("walletid") );
					if(request.getParameter("bankid")!=null) 	            bankId = StringUtils.trim(request.getParameter("bankid") );
					if(request.getParameter("bankcode")!=null) 	            bankCode = StringUtils.trim(request.getParameter("bankcode") );
					if(request.getParameter("hdnbankname")!=null) 	        bankName = StringUtils.trim(request.getParameter("hdnbankname") );
					if(request.getParameter("bankaccountnumber")!=null) 	bankAccountNo = StringUtils.trim(request.getParameter("bankaccountnumber") );
					if(request.getParameter("bankaccountname")!=null) 	    bankAccountName = StringUtils.trim(request.getParameter("bankaccountname") );
					if(request.getParameter("banktopupamount")!=null) 		topUpAmount = StringUtils.trim(request.getParameter("banktopupamount") );
			
					//step1 validate bank details
			
					//step2 initiate transaction code and save in the wallet details
					transactionCode = (String)PaymentDao.class.getConstructor().newInstance().initiatePaymentViaBank(walletId, bankId, bankCode, bankName, bankAccountNo, bankAccountName ,topUpAmount, relationshipNo );
				 	if(transactionCode==null) 	
				 		throw new Exception ("Transaction failed ");
				 	else 	
				 	SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo , ((User)session.getAttribute("SESS_USER")).getUserType(), "C", StringUtils.substring(" Bank Topup Txncode "+transactionCode, 0, 48) );					
		
			    	arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
				    arrBankdetails = (ArrayList<BankDetails>)PaymentDao.class.getConstructor().newInstance().getBankDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
					request.setAttribute("walletlist", arrWallet);	
					request.setAttribute("bankdetails", arrBankdetails);
					request.setAttribute("langPref", "en");				
					request.setAttribute("lastaction", "wal");	request.setAttribute("lastrule", "Topup Wallet");
					response.setContentType("text/html");
					
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerWalletMainPage()).forward(request, response);		
						}finally {
							if(arrWallet!=null) arrWallet=null; if(arrBankdetails!=null) arrBankdetails=null; if(walletId!=null) walletId=null;
							if(topUpAmount!=null) topUpAmount=null; if(bankId!=null) bankId=null; if(bankCode!=null) bankCode=null;
							if(bankName!=null) bankName=null;  if(bankAccountName!=null) bankAccountName=null; if(bankAccountNo!=null) bankAccountNo=null;
								}
						
				  }catch(Exception e){
					callException(request, response, ctx, session,e, e.getMessage());
					}
					break;
					
//				TOPUP_WITHCARD
				  case Rules.TOPUP_WITHCARD:
					  try { 	
						  
								ArrayList<Wallet> arrWallet = null; ArrayList<CardDetails> cardDtls = null;
								String walletId = null; String tokenId = null; String topUpAmount = null; String currencyId = null; String topUpComment = null;
								String transactionCode = null; 	String relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
								if(request.getParameter("hdnwalletid")!=null) 		walletId = StringUtils.trim(request.getParameter("hdnwalletid"));
								if(request.getParameter("hdntokenid")!=null) 		tokenId = StringUtils.trim(request.getParameter("hdntokenid"));
								if(request.getParameter("hdtopupamount")!=null) 	topUpAmount = StringUtils.trim(request.getParameter("hdtopupamount"));
								
								
								//step2 initiate transaction code and save in the wallet details
								transactionCode = (String)PaymentDao.class.getConstructor().newInstance().initiatePaymentViaToken(walletId, tokenId,  topUpAmount, relationshipNo);
							 	if(transactionCode==null) 	
							 		throw new Exception ("Transaction failed ");
							 	else 	
							 	SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(relationshipNo , ((User)session.getAttribute("SESS_USER")).getUserType(), "C", StringUtils.substring(" Bank Topup Txncode "+transactionCode, 0, 48) );					
					
						    	arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
						    	cardDtls= (ArrayList<CardDetails>)WalletDao.class.getConstructor().newInstance().getCardDetails(((User)session.getAttribute("SESS_USER")).getRelationshipNo() );								request.setAttribute("walletlist", arrWallet);	
								request.setAttribute("cardsdtls", cardDtls);
								request.setAttribute("langPref", "en");				
								request.setAttribute("lastaction", "wal");	request.setAttribute("lastrule", "Topup Wallet");
								response.setContentType("text/html");
								
									try {
										ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerWalletMainPage()).forward(request, response);		
									}finally {
										if(arrWallet!=null) arrWallet=null; if(currencyId!=null) currencyId=null; if(walletId!=null) walletId=null;
										if(topUpAmount!=null) topUpAmount=null; if(tokenId!=null) tokenId=null; if(cardDtls!=null) cardDtls=null;
										if(topUpComment!=null) topUpComment=null;  
											}
								
								
								//call next page
									
							}catch (Exception e) {
								callException(request, response, ctx, session,e, e.getMessage());
							}
						break;	
						  
						  
						  
					  	
						
			case Rules.WALLET_TOPUP_BY_MPESA:
				 try { 	 
					ArrayList<Wallet> arrWallet = null; String walletId = null;  String mpesaTopupAmount = null; 
					String mpesaNumber = null; String mpesaTxnCode = null; String relationshipNo = null; boolean success = false;
					if(request.getParameter("mpesanumber")!=null) 		    mpesaNumber = StringUtils.trim(request.getParameter("mpesanumber"));
					if(request.getParameter("mpesatopupamount")!=null) 	    mpesaTopupAmount = StringUtils.trim(request.getParameter("mpesatopupamount"));
					if(request.getParameter("walletid")!=null) 	            walletId = StringUtils.trim(request.getParameter("walletid"));
					
					PPWalletEnvironment.setComment(3, className, "mpesaNumber is : " + mpesaNumber + "mpesaTopupAmount is " + mpesaTopupAmount+ " walletId is "+ walletId);

				//Generate Mpesa Transaction Code***Later shall be integrated to Mpesa with API
					mpesaTxnCode = Utilities.genAlphaNumRandom(10);
					relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					PPWalletEnvironment.setComment(3, className, " : mpesaTxnCode  is " + mpesaTxnCode );

					success = (boolean)PaymentDao.class.getConstructor().newInstance().initiatePaymentViaMpesa(walletId, mpesaNumber, mpesaTopupAmount ,relationshipNo, mpesaTxnCode );
					if(!success) throw new Exception ("Transaction failed ");
					else 	
					 	SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail( relationshipNo , ((User)session.getAttribute("SESS_USER")).getUserType(), "C", StringUtils.substring(" Top up From Mpesa "+mpesaTxnCode, 0, 48) );					
			    	arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
					request.setAttribute("walletlist", arrWallet);	
					request.setAttribute("langPref", "en");				
					request.setAttribute("lastaction", "wal");	request.setAttribute("lastrule", "Topup Wallet");
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerWalletMainPage()).forward(request, response);
					}finally { 
						if(arrWallet!=null) arrWallet=null; if(walletId!=null) walletId=null;	if(mpesaTopupAmount!=null) mpesaTopupAmount=null; 
						if(mpesaNumber!=null) mpesaNumber=null; if(mpesaTxnCode!=null) mpesaTxnCode=null;
				   }
				 }catch(Exception e) {
						callException(request, response, ctx, session,e, e.getMessage());
				 }
				 break;
			
			
				 /*This method returns all Transactions for a specific customer */
						
			case Rules.WALLET_SHOWUSER_TXN_ALLWALLETS_PAGE:		
				 try {
				
					   // ArrayList<Transaction> arrWalletTxns = (ArrayList<Transaction> )WalletDao.class.getConstructor().newInstance().getAllWalletTransactionForUser( ((User)session.getAttribute("SESS_USER")).getRelationshipNo()  );
					//	PPWalletEnvironment.setComment(3,className," Wallet Transaction size is " + arrWalletTxns.size() );
						request.setAttribute("wallettxns", (ArrayList<Transaction> )WalletDao.class.getConstructor().newInstance().getAllWalletTransactionForUser( ((User)session.getAttribute("SESS_USER")).getRelationshipNo()) );
						request.setAttribute("langPref", "en");				
						request.setAttribute("lastaction", "wal");
						request.setAttribute("lastrule", Rules.WALLET_SHOWUSER_TXN_ALLWALLETS_PAGE);				
						response.setContentType("text/html");
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getWalletTransactionsForUserPage()).forward(request, response);
						}finally { 
					   }
		
				 }catch(Exception e){
						callException(request, response, ctx, session,e, e.getMessage());
				 }
				 break;
			 
			
			 
			 /* This method returns transactions for an individual wallet for a customer */
			 
			 case Rules.WALLET_SHOW_INDIVIDUAL_TRANSACTIONS:
				 
				 try {
					   					    

						// ArrayList<Transaction> wallettxns = null;
						String walletId = null;	
					
					if(request.getParameter("hdnwalletid")!=null) 		walletId = StringUtils.trim(request.getParameter("hdnwalletid")); 	
					
					//walletindvtxns = (ArrayList<Transaction> )WalletDao.class.getConstructor().newInstance().getAllTransactionsForWallet(walletId);
				//	PPWalletEnvironment.setComment(3,className," Wallet Transaction size is " + walletindvtxns.size() );
				
					request.setAttribute("walletindvtxns", (ArrayList<Transaction> )WalletDao.class.getConstructor().newInstance().getAllTransactionsForWallet(walletId));
					request.setAttribute("langPref", "en");	
					request.setAttribute("lastaction", "wal");
					request.setAttribute("lastrule", Rules.WALLET_VIEW_WALLET_PAGE);				
					response.setContentType("text/html");
					
//					wallettxns = (ArrayList<Transaction> )WalletDao.class.getConstructor().newInstance().getAllWalletTransactionForUser(((User)session.getAttribute("SESS_USER")).getRelationshipno());
//					request.setAttribute("wallettxns", wallettxns);

				//	arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
					request.setAttribute("walletlist", (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() ));
					
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerWalletMainPage()).forward(request, response);
						}finally { 
							if(walletId!= null) walletId = null;
							
						}
		
				}catch(Exception e){
							callException(request, response, ctx, session,e, e.getMessage());
					 }
			break;
					 
					 
					 
					 
				 /* this method returns customer registered recepients */
				
			 case Rules.WALLET_TRANSFER_FUND_PAGE:
				 
						 try {
							 
						ArrayList<Wallet> arrWallet = null; ArrayList<CustomerDetails> arrCustomerListReg = null;	
			        // ****get wallets of the users in oder to send money
						arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
						
						arrCustomerListReg = (ArrayList<CustomerDetails>)WalletDao.class.getConstructor().newInstance().getAllRegisteredWalletsForASender(((User)session.getAttribute("SESS_USER")).getRelationshipNo() );
					    
						request.setAttribute("registeredusers", arrCustomerListReg);
		
						request.setAttribute("walletlist", arrWallet);				
				    // *****Now display the main biller pages
						request.setAttribute("langPref", "en");				
						request.setAttribute("lastaction", "wal");	
						request.setAttribute("lastrule", WALLET_TRANSFER_FUND_PAGE); 
						response.setContentType("text/html");
							try {
								ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerP2PTransferfundMainPage()).forward(request, response);
							}finally { 
								if(arrWallet!=null) arrWallet=null; 
						   }
					 }catch(Exception e) {
							callException(request, response, ctx, session,e, e.getMessage());
					 } 
				 break;
				 
				 
				 
				 
				 
				 
				 
			 case Rules.WALLET_SEND_MONEY_REGISTER_RECEIVERPAGE:
					try {
						boolean success = false;  ArrayList<CustomerDetails> arrallUsersWallet = null;		
						 String relationshipNo = null;
						
						
						relationshipNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					   // arrallUsersWallet = (ArrayList<CustomerDetails>)WalletDao.class.getConstructor().newInstance().getRegisteredActiveUserWallets(  );
					  //  request.setAttribute("arralluserswallet", arrallUsersWallet);	
						request.setAttribute("langPref", "en");				
						request.setAttribute("lastaction", "pmt");	
						request.setAttribute("lastrule", PAYMENT_BILLPAY_REG_NEW_BILLER_PAGE);
						response.setContentType("text/html");
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerRegisterReceiverWalletPage()).forward(request, response);

						}finally {
						//	if(arrallMerchants!=null) arrallMerchants = null; if(arr_registeredBillers!=null) arr_registeredBillers = null; 
							//if(hash_remainingMerchants!=null) hash_remainingMerchants = null; if(hash_BillerCode!=null) hash_BillerCode = null;
						}
					}catch (Exception e) {
						callException(request, response, ctx, session,e, e.getMessage());
					}
				break;
				
				
				
				
				
				
				case Rules.WALLET_P2P_REG_SEARCH_WALLET:
					try {
						String langType = null;		String walletId = null; String custName = null;String custPhoneNumber = null;String custEmail = null;
						ArrayList<CustomerDetails> arrCustomerListReg = null;		
						boolean success = false;	
						if(request.getParameter("searchwalletid")!=null) 				walletId = StringUtils.trim(request.getParameter("searchwalletid"));
						if(request.getParameter("searchemail")!=null) 				custEmail = StringUtils.trim(request.getParameter("searchemail"));
						if(request.getParameter("searchname")!=null) 				custName = StringUtils.trim(request.getParameter("searchname"));
						if(request.getParameter("searchphone")!=null) 				custPhoneNumber = StringUtils.trim(request.getParameter("searchphone"));
						
						arrCustomerListReg = (ArrayList<CustomerDetails>)WalletDao.class.getConstructor().newInstance().getAllCustDetailsForRegistration ( walletId, custEmail,  custName,  custPhoneNumber);
						if(arrCustomerListReg.size()< 0 ) {              
							PPWalletEnvironment.setComment(1,classname," No search records Found" );
						}
						request.setAttribute("customerlistreg", arrCustomerListReg);
						request.setAttribute("langPref", "en");
						request.setAttribute("lastaction", "wal");	
						request.setAttribute("lastrule", WALLET_P2P_REG_SEARCH_WALLET);
						response.setContentType("text/html");
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerRegisterReceiverWalletPage()).forward(request, response);
						} finally {
							 if(walletId!=null) walletId=null; if(custName!=null) custName=null; if(custPhoneNumber!=null) custPhoneNumber=null; if(custEmail!=null) custEmail=null; 
						}
					}catch (Exception e) {
						callException(request, response, ctx, session,e, e.getMessage());
					}
				break;
				
				case Rules.WALLET_P2P_REG_RECEIVER_WALLETID_REGISTER:
					try {
						PPWalletEnvironment.setComment(3,className," inside  WALLET_P2P_REG_RECEIVER_WALLETID_REGISTER "  );

						String receiverWalletId = null;  String senderRegNo = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
						String receiverRelationNo = null;  ArrayList<CustomerDetails> arrCustomerListReg = null; ArrayList<Wallet> arrWallet = null;
						if(request.getParameter("hdnwalletid")!=null) 				receiverWalletId = StringUtils.trim(request.getParameter("hdnwalletid"));
						if(request.getParameter("hdnreceiverrelno")!=null) 				receiverRelationNo = StringUtils.trim(request.getParameter("hdnreceiverrelno"));
						
						boolean result = (boolean)WalletDao.class.getConstructor().newInstance().insertReceiverWalletForRegistration( receiverWalletId, senderRegNo, receiverRelationNo);
						if(result==false)
							throw new Exception ("New Wallet registration failed.");
						else {

							arrCustomerListReg = (ArrayList<CustomerDetails>)WalletDao.class.getConstructor().newInstance().getAllRegisteredWalletsForASender(senderRegNo);
						}	PPWalletEnvironment.setComment(3,classname," arrCustomerListReg size is " + arrCustomerListReg.size() );
						arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipNo() );											

						PPWalletEnvironment.setComment(3,classname," arrCustomerListReg size is " + arrCustomerListReg.size() );
					    request.setAttribute("registeredusers", arrCustomerListReg);
						request.setAttribute("walletlist", arrWallet);				
						request.setAttribute("langPref", "en");				
						request.setAttribute("lastaction", "wal");	
						request.setAttribute("lastrule", WALLET_P2P_REG_RECEIVER_WALLETID_REGISTER);
						response.setContentType("text/html");
						try {
							ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerP2PTransferfundMainPage()).forward(request, response);
						} finally {
							 if(senderRegNo!=null) senderRegNo=null; if(receiverWalletId!=null) receiverWalletId=null; if(receiverRelationNo!=null) receiverRelationNo=null;
						}									
						
					}catch (Exception e) {
						callException(request, response, ctx, session,e, e.getMessage());
					}
				
				break;
				
				/* this method handles sending from wallet to wallet */
			 
				case Rules.WALLET_SEND_MONEY_PAGE:
				 try {

					request.setAttribute("langPref", "en");				
					request.setAttribute("lastaction", "wal");	
					request.setAttribute("lastrule", Rules.WALLET_TRANSFER_FUND_PAGE); // check what is the best rule value
					String receiverWalletId = null; ArrayList<Transaction> arrTransactions = null; 	String senderWalletId = null; // i.e. user wallet id
					String payAmount = null; String payComments = null; String senderRelationship = null;	boolean success = false; String moduleCode ="W";
					
					String userType =  ((User)session.getAttribute("SESS_USER")).getUserType();			
					if(request.getParameter("hdnrecieverwalletid")!=null) 					receiverWalletId = StringUtils.trim(request.getParameter("hdnrecieverwalletid"));
					if(request.getParameter("hdnsenderwalletid")!=null) 					senderWalletId = StringUtils.trim(request.getParameter("hdnsenderwalletid"));
					if(request.getParameter("sendamount")!=null) 				    	payAmount = StringUtils.trim(request.getParameter("sendamount"));
					if(request.getParameter("paywalletcomment")!=null) 					payComments = StringUtils.trim(request.getParameter("paywalletcomment"));	
					
					PPWalletEnvironment.setComment(1, className, "coming from send money jsp : "+ receiverWalletId+":"+senderWalletId+":"+payAmount+":"+payComments);
					// sender id 
					senderRelationship = ((User)session.getAttribute("SESS_USER")).getRelationshipNo();
					success = (boolean)WalletDao.class.getConstructor().newInstance().updateWalletLedgers(senderRelationship,senderWalletId, receiverWalletId, payAmount, payComments  );
					if(success == false) throw new Exception ("Failed to process the sending money..");
					else
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(senderRelationship, userType, moduleCode, StringUtils.substring("Send Money "+senderRelationship, 0, 48) );
					// *******Step 1: Update sender wallet ledger
					// *******Step 2: Update receiver wallet ledger
					//********Step 3: Record wallet transaction
					//********Step 4: Calculate Loyalty
					arrTransactions = (ArrayList<Transaction>)WalletDao.class.getConstructor().newInstance().getAllTransactionsForWallet(senderWalletId);
					request.setAttribute("wallettxns", arrTransactions);
					
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getWalletTransactionsForUserPage()).forward(request, response);
					}finally { 
						if(receiverWalletId!=null) receiverWalletId=null; if(arrTransactions!=null) arrTransactions=null; if(senderWalletId!=null) senderWalletId=null;  
						if(payAmount!=null) payAmount=null; if(payComments!=null) payComments=null; if(senderRelationship!=null) senderRelationship=null;
						if(moduleCode!=null) moduleCode=null; 
				   }
				
			}catch (Exception e) {
					callException(request, response, ctx, session,e, e.getMessage());
				}
			 break;
				 
		}
		
	}

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		// TODO Auto-generated method stub
		
	switch (rulesaction) {
		
		case Rules.JSON_WALLET_VIEW_WALLET_PAGE:
			
			try {
			String userId = null; String privateKey = null; boolean allow = true; String userType = null;
			PrintWriter jsonOutput_1 = null;  ArrayList<Wallet> arrWallet=null; String relationshipno= null;
			
	
			if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
			if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
			if(jsonObj.get("relationshipno")!=null) relationshipno = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
			
			if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
				allow = false;
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_VIEW_WALLET_PAGE: pvt key is incorrect "+privateKey);
			}
			jsonOutput_1 = response.getWriter();
			Gson gson = new Gson();
			JsonObject obj = new JsonObject(); //Json Object
			arrWallet= WalletDao.class.getConstructor().newInstance().getWalletDetails(relationshipno);
			
			
			if(arrWallet!=null && allow){
					int count = arrWallet.size();
				
				String[] walletIdArray= new String [count];
				String[] walletAmountArray= new String [count];
				String[] walletDescArray= new String [count];
				String[] lastUpdatedArray= new String [count];
				
				
				 // put some value pairs into the JSON object .
				 for(int i=0;i<arrWallet.size();i++){
					 walletIdArray[i]= ((Wallet)arrWallet.get(i)).getWalletId();
					 walletAmountArray[i]= ((Wallet)arrWallet.get(i)).getCurrentBalance();
					 walletDescArray[i]= ((Wallet)arrWallet.get(i)).getWalletDesc();
					 lastUpdatedArray[i]= ((Wallet)arrWallet.get(i)).getLastUpdated();
		
				 }
				 
				 obj.add("walletid", gson.toJsonTree(walletIdArray));
				  obj.add("currbal", gson.toJsonTree(walletAmountArray));
				  obj.add("walletdesc", gson.toJsonTree(walletDescArray));
				  obj.add("lastupdated", gson.toJsonTree(lastUpdatedArray));
				  obj.add("error", gson.toJsonTree("false"));
				  
				  
				  
			} else {
				
				obj.add("error", gson.toJsonTree("true"));
			}
			try {
				//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
				jsonOutput_1.print(gson.toJson(obj));
			} finally {
				//close all objects here
				if(jsonOutput_1!=null) jsonOutput_1.close(); if(arrWallet!=null) arrWallet =null; 
				if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
				if(userId!=null) userId=null;	if(relationshipno!=null) relationshipno=null;
				if(userType!=null) userType=null;	
				
			}
			
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_VIEW_WALLET_PAGE "+e.getMessage());
			}
			break;
			
			
		case JSON_WALLET_SHOWUSER_TXN_INDIVIDUALWALLETS_PAGE:
			
			try {
				String privateKey = null; boolean allow = true; 
				PrintWriter jsonOutput_1 = null;  ArrayList<Wallet>  arrWallet = null; String relationshipNo= null; 
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");	
				
				
					PPWalletEnvironment.setComment(2, className, "relationship no is "+relationshipNo + privateKey );
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_SHOWUSER_TXN_INDIVIDUALWALLETS_PAGE: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
			
				arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( relationshipNo);			
				if(arrWallet!=null && allow){
					int count = arrWallet.size();
				
				String[] walletIdArray= new String [count];
				String[] walletAmountArray= new String [count];
				String[] walletDescArray= new String [count];
				 // put some value pairs into the JSON object .
				 for(int i=0;i<arrWallet.size();i++){
					 walletIdArray[i]= ((Wallet)arrWallet.get(i)).getWalletId();
					 walletAmountArray[i]= ((Wallet)arrWallet.get(i)).getCurrentBalance();
					 walletDescArray[i]= ((Wallet)arrWallet.get(i)).getWalletDesc();
				 }
				 obj.add("walletid", gson.toJsonTree(walletIdArray));
				  obj.add("currbal", gson.toJsonTree(walletAmountArray));
				  obj.add("walletdesc", gson.toJsonTree(walletDescArray));
				  obj.add("error", gson.toJsonTree("false"));
				  
			} else {
				
				obj.add("error", gson.toJsonTree("true"));
			}
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					//close all objects here
					if(jsonOutput_1!=null) jsonOutput_1.close();  if(arrWallet!=null) arrWallet =null; 
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(relationshipNo!=null) relationshipNo=null;
				}
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_SHOWUSER_TXN_INDIVIDUALWALLETS_PAGE "+e.getMessage());
			}
	
			break;
			
			
			
		case Rules.JSON_WALLET_SHOWUSER_TXN_INDIVIDUALWALLETS:
           try {
				
				String userId = null; String privateKey = null; boolean allow = true; String dateFrom = null; String dateTo = null; String walletId=null;
				PrintWriter jsonOutput_1 = null;  ArrayList<Transaction> aryTransaction=null; String relationshipNo= null;
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("startdate")!=null) dateFrom = jsonObj.get("startdate").toString().replaceAll("\"", "");
				if(jsonObj.get("enddate")!=null) dateTo = jsonObj.get("enddate").toString().replaceAll("\"", "");
				if(jsonObj.get("walletid")!=null) walletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
				
				
				PPWalletEnvironment.setComment(3, className, "changed date "+dateFrom +":" +dateTo );
				
				PPWalletEnvironment.setComment(2, className, "relationship no is "+relationshipNo + privateKey +userId );
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_SHOWUSER_TXN_ALLWALLETS_PAGE: pvt key is incorrect "+privateKey);
				}
				

				// date conversion 
				 String startDate = Utilities.formartDate(dateFrom);
				 String endDate = Utilities.formartDate(dateTo);
				
				 
				 PPWalletEnvironment.setComment(3, className, "Date Convertsion "+startDate +":" +endDate );
					
				 jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				
				
				aryTransaction = (ArrayList<Transaction>)WalletDao.class.getConstructor().newInstance().getAllWalletTransactionForUserJSON(walletId,startDate,endDate);
				//PPWalletEnvironment.setComment(3, className, "aryTransaction is "+ aryTransaction.size());

				
				
				if(aryTransaction!=null && allow){
					int count = aryTransaction.size();
				
				String[] txnCodeArray= new String [count];
				String[] amountArray= new String [count];
				String[] dateArray= new String [count];
				
				
				 for(int i=0;i<aryTransaction.size();i++){
					 txnCodeArray[i]= ((Transaction)aryTransaction.get(i)).getTxnCode();
					amountArray[i]= ((Transaction)aryTransaction.get(i)).getTxnAmount();
					 dateArray[i]= ((Transaction)aryTransaction.get(i)).getTxnDateTime();
	
				 }
				
				 obj.add("txncode", gson.toJsonTree(txnCodeArray));
				 obj.add("txnamount", gson.toJsonTree(amountArray));
				 obj.add("txndatetime", gson.toJsonTree(dateArray));
				 obj.add("error", gson.toJsonTree("false"));
				
				}
				else {
					
					obj.add("error", gson.toJsonTree("true"));
					PPWalletEnvironment.setComment(2, className, "Array is null");
				}
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					//close all objects here
					if(jsonOutput_1!=null) jsonOutput_1.close();  if(aryTransaction!=null) aryTransaction =null; 
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(relationshipNo!=null) relationshipNo=null;
					if(userId!=null) userId=null;		if(dateTo!=null) dateTo=null;
					if(dateFrom!=null) dateFrom=null;	
				}
			
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_VIEW_WALLET_PAGE "+e.getMessage());
			}
			
			
			
			break;
			
		case Rules.JSON_WALLET_SHOWUSER_TXN_INDIVIDUALWALLETS_GRAPH:
			try {
				String relationshipNo = null; String privateKey = null; boolean allow = true;  PrintWriter jsonOutput_1 = null; ArrayList<Transaction> arrTransaction = null; 
				float amountDay1=0; float amountDay2=0; float amountDay3=0; float amountDay4=0; float amountDay5=0; float amountDay6=0; float amountToday=0;
				String currentDay=null; 	String dayOne=null; 	String dayTwo=null; 	String dayThree=null; 	String dayFour=null; 	String dayFive=null; String daySix=null;
				String walletId=null;
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("walletid")!=null) walletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_SHOWUSER_TXN_ALLWALLETS_PAGE: pvt key is incorrect "+privateKey);
				}
			
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				arrTransaction = (ArrayList<Transaction>)WalletDao.class.getConstructor().newInstance().getAllTransactionsForWalletGraph(walletId);
				
				if(arrTransaction!=null && allow ){
					currentDay= (Utilities.getCurrentDate()); 
					dayOne = Utilities.getDateCalculate(currentDay, -1, "yyyy-MM-dd"); dayTwo = Utilities.getDateCalculate(currentDay, -2, "yyyy-MM-dd");
					dayThree = Utilities.getDateCalculate(currentDay, -3, "yyyy-MM-dd"); dayFour = Utilities.getDateCalculate(currentDay, -4, "yyyy-MM-dd");
					dayFive = Utilities.getDateCalculate(currentDay, -5, "yyyy-MM-dd"); daySix = Utilities.getDateCalculate(currentDay, -6, "yyyy-MM-dd");
				
					//int count = arrTransaction.size();
					String[] txnDate = new String[7]; 
					String[] txnAmount = new String[7];
					
					 for(int i=0;i<arrTransaction.size();i++){
						 if (((Transaction)arrTransaction.get(i)).getTxnDateTime().contains(currentDay)) {
								amountToday += Float.parseFloat(((Transaction)arrTransaction.get(i)).getTxnAmount());
								
								PPWalletEnvironment.setComment(2, className, " Amount is"+ ((Transaction)arrTransaction.get(i)).getTxnAmount());
						 }else if (((Transaction)arrTransaction.get(i)).getTxnDateTime().contains(dayOne)) {
									amountDay1 += Float.parseFloat(((Transaction)arrTransaction.get(i)).getTxnAmount());
							}else if (((Transaction)arrTransaction.get(i)).getTxnDateTime().contains(dayTwo)) {
								amountDay2 += Float.parseFloat(((Transaction)arrTransaction.get(i)).getTxnAmount());
							}else if (((Transaction)arrTransaction.get(i)).getTxnDateTime().contains(dayThree)) {
								amountDay3 += Float.parseFloat(((Transaction)arrTransaction.get(i)).getTxnAmount());
							}else if (((Transaction)arrTransaction.get(i)).getTxnDateTime().contains(dayFour)) {
								amountDay4 += Float.parseFloat(((Transaction)arrTransaction.get(i)).getTxnAmount());
							}else if (((Transaction)arrTransaction.get(i)).getTxnDateTime().contains(dayFive)) {
								amountDay5 += Float.parseFloat(((Transaction)arrTransaction.get(i)).getTxnAmount());
							}else if (((Transaction)arrTransaction.get(i)).getTxnDateTime().contains(daySix)) {
								amountDay6 += Float.parseFloat(((Transaction)arrTransaction.get(i)).getTxnAmount());
							}
					 }
					 
					 txnAmount[0] = String.valueOf(amountToday);
					 txnAmount[1] = String.valueOf(amountDay1); 
					 txnAmount[2] = String.valueOf(amountDay2); 
					 txnAmount[3] = String.valueOf(amountDay3);
					 txnAmount[4] = String.valueOf(amountDay4); 
					 txnAmount[5] = String.valueOf(amountDay5); 
					 txnAmount[6] = String.valueOf(amountDay6); 
					 
					 txnDate[0]=currentDay;
					 txnDate[1]=dayOne;
					 txnDate[2]=dayTwo;
					 txnDate[3]=dayThree;
					 txnDate[4]=dayFour;
					 txnDate[5]=dayFive;
					 txnDate[6]=daySix;
					
					 	obj.add("txnamount", gson.toJsonTree(txnAmount));
						obj.add("txndatetime", gson.toJsonTree(txnDate));
						obj.add("error", gson.toJsonTree("false"));
						
				}else {
					
					obj.add("error", gson.toJsonTree("true"));
				}
				
				try {
					PPWalletEnvironment.setComment(3, className, " JSON JSON_PAYMENT_VIEW_PAYBILL_TXN_GRAPH String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(privateKey!=null) privateKey=null; if(gson!=null) gson=null; if(arrTransaction!=null) arrTransaction=null;
					if(jsonOutput_1!=null) jsonOutput_1.close();  if(relationshipNo!=null) relationshipNo=null;
					if(amountDay1!=0) amountDay1=0; if(amountDay2!=0) amountDay2=0; if(amountDay3!=0) amountDay3=0;
					if(amountDay4!=0) amountDay4=0; if(amountDay5!=0) amountDay5=0; if(amountDay6!=0) amountDay6=0;if(amountToday!=0) amountToday=0;
					if(dayOne!=null) dayOne=null; if(dayTwo!=null) dayTwo=null; if(dayThree!=null) dayThree=null;
					if(dayFour!=null) dayFour=null; if(dayFive!=null) dayFive=null; if(daySix!=null) daySix=null;  if(currentDay!=null) currentDay=null;
					
				}

			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_VIEW_WALLET_PAGE "+e.getMessage());
			}
			break;
			
		
		case Rules.JSON_WALLET_SHOWUSER_TXN_ALLWALLETS_PAGE:
			try {
				
				String userId = null; String privateKey = null; boolean allow = true; String dateFrom = null; String dateTo = null;
				PrintWriter jsonOutput_1 = null;  ArrayList<Transaction> aryTransaction=null; String relationshipNo= null;
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("startdate")!=null) dateFrom = jsonObj.get("startdate").toString().replaceAll("\"", "");
				if(jsonObj.get("enddate")!=null) dateTo = jsonObj.get("enddate").toString().replaceAll("\"", "");
				
				
				PPWalletEnvironment.setComment(3, className, "changed date "+dateFrom +":" +dateTo );
				
				PPWalletEnvironment.setComment(2, className, "relationship no is "+relationshipNo + privateKey +userId );
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_SHOWUSER_TXN_ALLWALLETS_PAGE: pvt key is incorrect "+privateKey);
				}
				

				// date conversion 
				 String startDate = Utilities.formartDate(dateFrom);
				 String endDate = Utilities.formartDate(dateTo);
				
				 
				 PPWalletEnvironment.setComment(3, className, "Date Convertsion "+startDate +":" +endDate );
					
				 
				 
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				
				
				aryTransaction = (ArrayList<Transaction>)WalletDao.class.getConstructor().newInstance().getAllWalletTransactionForUserJSON(relationshipNo,startDate,endDate);
				//PPWalletEnvironment.setComment(3, className, "aryTransaction is "+ aryTransaction.size());

				
				
				if(aryTransaction!=null){
					int count = aryTransaction.size();
				
				String[] txnCodeArray= new String [count];
				String[] amountArray= new String [count];
				String[] dateArray= new String [count];
				
				
				 for(int i=0;i<aryTransaction.size();i++){
					 txnCodeArray[i]= ((Transaction)aryTransaction.get(i)).getTxnCode();
					amountArray[i]= ((Transaction)aryTransaction.get(i)).getTxnAmount();
					 dateArray[i]= ((Transaction)aryTransaction.get(i)).getTxnDateTime();
	
				 }
				
				 obj.add("txncode", gson.toJsonTree(txnCodeArray));
				 obj.add("txnamount", gson.toJsonTree(amountArray));
				 obj.add("txndatetime", gson.toJsonTree(dateArray));
				 obj.add("error", gson.toJsonTree("false"));
				
				}
				else {
					
					obj.add("error", gson.toJsonTree("true"));
					PPWalletEnvironment.setComment(2, className, "Array is null");
				}
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					//close all objects here
					if(jsonOutput_1!=null) jsonOutput_1.close();  if(aryTransaction!=null) aryTransaction =null; 
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(relationshipNo!=null) relationshipNo=null;
					if(userId!=null) userId=null;		if(dateTo!=null) dateTo=null;
					if(dateFrom!=null) dateFrom=null;	
				}
			
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_VIEW_WALLET_PAGE "+e.getMessage());
			}
			
				break;
				
   
				
		case JSON_WALLET_TOPUP_WALLET_WELCOMEPAGE:
			try {
		
		String privateKey = null; boolean allow = true; 
		PrintWriter jsonOutput_1 = null;  ArrayList<Wallet>  arrWallet = null; String relationshipNo= null; ArrayList<BankDetails> arrBankdetails = null;
		
		if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
		if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");	
		
		
			PPWalletEnvironment.setComment(2, className, "relationship no is "+relationshipNo + privateKey );
		
		if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
			allow = false;
			PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_TOPUP_WALLET_WELCOMEPAGE: pvt key is incorrect "+privateKey);
		}
		
		jsonOutput_1 = response.getWriter();
		Gson gson = new Gson();
		JsonObject obj = new JsonObject(); //Json Object
		
	
		arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( relationshipNo);	
		arrBankdetails = (ArrayList<BankDetails>)PaymentDao.class.getConstructor().newInstance().getBankDetails(relationshipNo);
		
		if(arrWallet!=null  && allow){
			int count = arrWallet.size();
		
			String[] walletIdArray= new String [count];
			String[] walletAmountArray= new String [count];
			String[] walletDescArray= new String [count];
			String[] lastUpdatedArray= new String [count];
		
			 // put some value pairs into the JSON object .
			 for(int i=0;i<arrWallet.size();i++){
				 walletIdArray[i]= ((Wallet)arrWallet.get(i)).getWalletId();
				 walletAmountArray[i]= ((Wallet)arrWallet.get(i)).getCurrentBalance();
				 walletDescArray[i]= ((Wallet)arrWallet.get(i)).getWalletDesc();
				 lastUpdatedArray[i]= ((Wallet)arrWallet.get(i)).getLastUpdated();
	
			 }
			 if(arrBankdetails!=null){
					int bankCount = arrBankdetails.size();
					
					String[] bankCodeArray= new String [bankCount];
					String[] branchCodeArray= new String [bankCount];
					String[] bankNameArray= new String [bankCount];
					String[] bankAccNoArray= new String [bankCount];
					String[] bankAccNameArray= new String [bankCount];
				
					 // put some value pairs into the JSON object .
					 for(int i=0;i<arrBankdetails.size();i++){
						 bankCodeArray[i]= ((BankDetails)arrBankdetails.get(i)).getBankCode();
						 branchCodeArray[i]= ((BankDetails)arrBankdetails.get(i)).getBranchCode();
						 bankNameArray[i]= ((BankDetails)arrBankdetails.get(i)).getBankName();
						 bankAccNoArray[i]= ((BankDetails)arrBankdetails.get(i)).getBankAccountNo();
						 bankAccNameArray[i]= ((BankDetails)arrBankdetails.get(i)).getBankAccountName();
			
					 }
					 
					 obj.add("bankcode", gson.toJsonTree(bankCodeArray));
					  obj.add("branchcode", gson.toJsonTree(branchCodeArray));
					  obj.add("bankname", gson.toJsonTree(bankNameArray));
					  obj.add("bankaccountno", gson.toJsonTree(bankAccNoArray));
					  obj.add("bankaccountname", gson.toJsonTree(bankAccNameArray));
					  obj.add("error", gson.toJsonTree("false"));
					  
				}
		 
		 obj.add("walletid", gson.toJsonTree(walletIdArray));
		  obj.add("currbal", gson.toJsonTree(walletAmountArray));
		  obj.add("walletdesc", gson.toJsonTree(walletDescArray));
		  obj.add("lastupdated", gson.toJsonTree(lastUpdatedArray));
		  obj.add("error", gson.toJsonTree("false"));
		  
	} else {
		
		obj.add("error", gson.toJsonTree("true"));
		
		
	}
		
		try {
			//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
			jsonOutput_1.print(gson.toJson(obj));
			
			
			} finally {
			//close all objects here
				if(jsonOutput_1!=null) jsonOutput_1.close(); if(arrBankdetails!=null) arrBankdetails =null; 
				if(arrWallet!=null) arrWallet =null; 
				if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
				if(relationshipNo!=null) relationshipNo=null;
		}
	
		
			}catch(Exception e) {
		PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_TOPUP_WALLET_WELCOMEPAGE "+e.getMessage());
			}
		
	break;
				
			
		case JSON_WALLET_TOPUP_BY_BANK:
			
					try {
				
				String privateKey = null; boolean allow = true; String transactionCode =null;
				String walletId = null;  String topUpAmount = null; String bankCode = null; String bankId = null;
				String bankName = null;  String bankAccountNo = null; String bankAccountName = null; 
				PrintWriter jsonOutput_1 = null;  String relationshipNo= null; String userId = null; String userType= "C";
				
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("walletid")!=null) walletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
				if(jsonObj.get("bankname")!=null) bankName = jsonObj.get("bankname").toString().replaceAll("\"", "");
				if(jsonObj.get("accname")!=null) bankAccountName = jsonObj.get("accname").toString().replaceAll("\"", "");
				if(jsonObj.get("bankcode")!=null) bankId = jsonObj.get("bankcode").toString().replaceAll("\"", "");
				if(jsonObj.get("accno")!=null) bankAccountNo = jsonObj.get("accno").toString().replaceAll("\"", "");
				if(jsonObj.get("branchcode")!=null) bankCode = jsonObj.get("branchcode").toString().replaceAll("\"", "");
				if(jsonObj.get("accamount")!=null) topUpAmount = jsonObj.get("accamount").toString().replaceAll("\"", "");
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				
				
					PPWalletEnvironment.setComment(2, className, "wallet Id "+walletId);
					PPWalletEnvironment.setComment(2, className, "bank name "+bankName);
					PPWalletEnvironment.setComment(2, className, "Account Name "+bankAccountName);
					PPWalletEnvironment.setComment(2, className, "Bank Code "+bankId);
					PPWalletEnvironment.setComment(2, className, "Branch Code "+ bankCode);
					PPWalletEnvironment.setComment(2, className, "Account number is "+bankAccountNo);
					PPWalletEnvironment.setComment(2, className, "Top Amount is "+topUpAmount);
					
					
					
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_TOPUP_BY_BANK: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				transactionCode = (String)PaymentDao.class.getConstructor().newInstance().initiatePaymentViaBank(walletId, bankId, bankCode, bankName, bankAccountNo, bankAccountName ,topUpAmount,relationshipNo );				
				
				
				if(transactionCode !=null && allow == true) {
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId , userType, "C", StringUtils.substring(" Bank Topup Txncode "+transactionCode, 0, 48) );
					obj.add("error", gson.toJsonTree("false"));
					
					
				}else {
					
					obj.add("error", gson.toJsonTree("true"));
				}
				
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					//close all objects here
				
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(transactionCode!=null) transactionCode =null; 
					if(bankName!=null) bankName=null;	if(bankAccountName!=null) bankAccountName=null;
					if(bankId!=null) bankId=null;	if(bankAccountNo!=null) bankAccountNo=null;
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
					if(bankCode!=null) bankCode=null;	if(topUpAmount!=null) topUpAmount=null;
					if(relationshipNo!=null) relationshipNo=null;
				}
			
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_TOPUP_BY_BANK "+e.getMessage());
			}
			
			
			
			
			break;
			
			
		
			
		case Rules.JSON_WALLET_TOPUP_BY_MPESA:
			
			try {
				String privateKey = null; boolean allow = true;  String walletId = null; 
				PrintWriter jsonOutput_1 = null;  String relationshipNo= null; String userId = null; String userType= "C";
				 String mpesaTopupAmount = null; String mpesaNumber = null; String mpesaTxnCode = null;   boolean success = false;
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("walletid")!=null) walletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
				if(jsonObj.get("mpesanumber")!=null) mpesaNumber = jsonObj.get("mpesanumber").toString().replaceAll("\"", "");
				if(jsonObj.get("mpesatopupamount")!=null) mpesaTopupAmount = jsonObj.get("mpesatopupamount").toString().replaceAll("\"", "");
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				
				
				PPWalletEnvironment.setComment(2, className, "Top Up Amount"+mpesaTopupAmount);
				

				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_TOPUP_BY_MPESA: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
						//Generate Mpesa Transaction Code***Later shall be integrated to Mpesa with API
				
					mpesaTxnCode = Utilities.genAlphaNumRandom(10);
					PPWalletEnvironment.setComment(3, className, " : mpesaTxnCode  is " + mpesaTxnCode );

				success = (boolean)PaymentDao.class.getConstructor().newInstance().initiatePaymentViaMpesa(walletId, mpesaNumber, mpesaTopupAmount ,relationshipNo, mpesaTxnCode );
				
				if (success && allow == true) {
					
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId , userType, "C", StringUtils.substring(" Mpesa Topup Txncode "+mpesaTxnCode, 0, 48) );
					obj.add("error", gson.toJsonTree("false"));
					
					
				}else {
 
					
					obj.add("error", gson.toJsonTree("true"));
				}
				
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					//close all objects here
				
					if(jsonOutput_1!=null) jsonOutput_1.close();  
					if(mpesaTopupAmount!=null) mpesaTopupAmount=null;	if(mpesaNumber!=null) mpesaNumber=null;
					if(mpesaTxnCode!=null) mpesaTxnCode=null;	
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
					if(relationshipNo!=null) relationshipNo=null;
				}
			
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_TOPUP_BY_MPESA "+e.getMessage());
			}
			
			break;
			
			
			
			
case JSON_WALLET_TRANSFER_FUND_PAGE:
			
			try {
				String privateKey = null; boolean allow = true; 
				PrintWriter jsonOutput_1 = null;  ArrayList<Wallet>  arrWallet = null; String relationshipNo= null; ArrayList<CustomerDetails> arrCustomerListReg = null;
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");	
				
				
					PPWalletEnvironment.setComment(2, className, "relationship no is "+relationshipNo + privateKey );
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_TOPUP_WALLET_WELCOMEPAGE: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
			
				arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( relationshipNo);	
				arrCustomerListReg = (ArrayList<CustomerDetails>)WalletDao.class.getConstructor().newInstance().getAllRegisteredWalletsForASender(relationshipNo);
				if(arrWallet!=null && allow){
					int count = arrWallet.size();
					
				
				String[] walletIdArray= new String [count];
				String[] walletAmountArray= new String [count];
				String[] walletDescArray= new String [count];
				
				
				
				
				
				 // put some value pairs into the JSON object .
				 for(int i=0;i<arrWallet.size();i++){
					 walletIdArray[i]= ((Wallet)arrWallet.get(i)).getWalletId();
					 walletAmountArray[i]= ((Wallet)arrWallet.get(i)).getCurrentBalance();
					 walletDescArray[i]= ((Wallet)arrWallet.get(i)).getWalletDesc();
		
				 }
					
					if(arrCustomerListReg!=null){
						int countCust = arrCustomerListReg.size();
						String[] customerNameArray= new String [countCust];
						String[] custWalletIdArray= new String [countCust];
						String[] contactArray= new String [countCust];
						String[] emailArray= new String [countCust];
				
					 // put some value pairs into the JSON object .
					 for(int i=0;i<arrCustomerListReg.size();i++){
						 customerNameArray[i]= ((CustomerDetails)arrCustomerListReg.get(i)).getCustomerName();
						 custWalletIdArray[i]= ((CustomerDetails)arrCustomerListReg.get(i)).getWalletId();
						 contactArray[i]= ((CustomerDetails)arrCustomerListReg.get(i)).getContact();
						 emailArray[i]= ((CustomerDetails)arrCustomerListReg.get(i)).getEmail();
	
					 }
					 
					  obj.add("receivercustomername", gson.toJsonTree(customerNameArray));
					  obj.add("receiverwalletid", gson.toJsonTree(walletIdArray));
					  obj.add("receivercontact", gson.toJsonTree(contactArray));
					  obj.add("receiveremail", gson.toJsonTree(emailArray));
						 
				}
				 
				 obj.add("walletid", gson.toJsonTree(walletIdArray));
				 obj.add("currbal", gson.toJsonTree(walletAmountArray));
				 obj.add("walletdesc", gson.toJsonTree(walletDescArray));
				 obj.add("error", gson.toJsonTree("false"));
				  
			} else {
				
				obj.add("error", gson.toJsonTree("true"));
				
				
			}
				
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
					
					
					} finally {
					//close all objects here
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(arrCustomerListReg!=null) arrCustomerListReg =null; 
						if(arrWallet!=null) arrWallet =null; 
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
						if(relationshipNo!=null) relationshipNo=null;
					}
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_TRANSFER_FUND_PAGE "+e.getMessage());
			}
			break;
			
			
		case JSON_WALLET_SEND_FUNDS:
			
			try {
				String receiverWalletId = null;  String senderWalletId = null; // i.e. user wallet id
				String transferAmount = null; String senderComments = null; String senderRelationshipNo = null;	boolean success = false; String moduleCode ="W";
				String privateKey = null; boolean allow = true; PrintWriter jsonOutput_1 = null; String userId = null; String userType = "C";
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) senderRelationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");	
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");	
				if(jsonObj.get("comments")!=null) senderComments = jsonObj.get("comments").toString().replaceAll("\"", "");	
				if(jsonObj.get("transferamount")!=null) transferAmount = jsonObj.get("transferamount").toString().replaceAll("\"", "");	
				if(jsonObj.get("senderwalletid")!=null) senderWalletId = jsonObj.get("senderwalletid").toString().replaceAll("\"", "");	
				if(jsonObj.get("receiverwalletid")!=null) receiverWalletId = jsonObj.get("receiverwalletid").toString().replaceAll("\"", "");	
				
				
					PPWalletEnvironment.setComment(2, className, "relationship no is "+senderRelationshipNo +"||"+ privateKey+"||"+transferAmount+"||"+ senderWalletId+"||"+receiverWalletId+"||"+senderWalletId);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_SEND_FUNDS: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
	
				
				
				success = (boolean)WalletDao.class.getConstructor().newInstance().updateWalletLedgers(senderRelationshipNo,senderWalletId, receiverWalletId, transferAmount, senderComments  );
				
				
					if (success && allow ) {
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Send Money to walletid "+receiverWalletId, 0, 48) );
						
						obj.add("error", gson.toJsonTree("false"));
						
					
							}else {
 
					
					obj.add("error", gson.toJsonTree("true"));
				}
				
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					//close all objects here
				
					if(jsonOutput_1!=null) jsonOutput_1.close();  
					if(senderRelationshipNo!=null) senderRelationshipNo=null; if(userId!=null) userId=null;
					if(transferAmount!=null) transferAmount=null;	if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
					if(senderWalletId!=null) senderWalletId=null; if(receiverWalletId!=null) receiverWalletId=null;if(senderComments!=null) senderComments=null;
				}
		
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_SEND_FUNDS "+e.getMessage());
			}
			
			
			break;
			
		case JSON_WALLET_P2P_REG_SEARCH_WALLET:
			
			try {
				String privateKey = null; boolean allow = true; PrintWriter jsonOutput_1 = null;String relationshipNo= null;
				String walletId = null; String custName = null;String custPhoneNumber = null;String custEmail = null;
				ArrayList<CustomerDetails> arrCustomerListReg = null;
				
				
				
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
			
				if(jsonObj.get("searchwalletid")!=null) walletId = jsonObj.get("searchwalletid").toString().replaceAll("\"", "");	
				if(jsonObj.get("searchemail")!=null) custEmail = jsonObj.get("searchemail").toString().replaceAll("\"", "");	
				if(jsonObj.get("searchname")!=null) custName = jsonObj.get("searchname").toString().replaceAll("\"", "");	
				if(jsonObj.get("searchphone")!=null) custPhoneNumber = jsonObj.get("searchphone").toString().replaceAll("\"", "");
				
				PPWalletEnvironment.setComment(2, className,  "Private key is"+privateKey);
				PPWalletEnvironment.setComment(2, className,  "Values are"+walletId +"||"+ privateKey+"||"+custEmail+"||"+ custName+"||"+custPhoneNumber);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_P2P_REG_SEARCH_WALLET: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				arrCustomerListReg = (ArrayList<CustomerDetails>)WalletDao.class.getConstructor().newInstance().getAllCustDetailsForRegistration ( walletId, custEmail,  custName,  custPhoneNumber);
				
				if(arrCustomerListReg!=null && allow ==true) {
						int count = arrCustomerListReg.size();
						
					String[] customerNameArray= new String [count];
					String[] walletIdArray= new String [count];
					String[] contactArray= new String [count];
					String[] emailArray= new String [count];
					String[] relationshipNoArray= new String [count];
				
					 // put some value pairs into the JSON object .
					 for(int i=0;i<arrCustomerListReg.size();i++){
						 customerNameArray[i]= ((CustomerDetails)arrCustomerListReg.get(i)).getCustomerName();
						 walletIdArray[i]= ((CustomerDetails)arrCustomerListReg.get(i)).getWalletId();
						 contactArray[i]= ((CustomerDetails)arrCustomerListReg.get(i)).getContact();
						 emailArray[i]= ((CustomerDetails)arrCustomerListReg.get(i)).getEmail();
						 relationshipNoArray[i]= ((CustomerDetails)arrCustomerListReg.get(i)).getRelationshipNo();
	
					 }
					 
					 obj.add("customername", gson.toJsonTree(customerNameArray));
					  obj.add("walletid", gson.toJsonTree(walletIdArray));
					  obj.add("custcontact", gson.toJsonTree(contactArray));
					  obj.add("custemail", gson.toJsonTree(emailArray));
					  obj.add("relationshipno", gson.toJsonTree(relationshipNoArray));
					  obj.add("error", gson.toJsonTree("false"));	  
				} else {
					
					obj.add("error", gson.toJsonTree("true"));
					
					
				}
				try {
					PPWalletEnvironment.setComment(3, className, " JSON JSON_WALLET_P2P_REG_SEARCH_WALLET String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
					
					
					} finally {
					//close all objects here
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(arrCustomerListReg!=null) arrCustomerListReg =null;  
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
						if(relationshipNo!=null) relationshipNo=null;
					}
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_P2P_REG_SEARCH_WALLET "+e.getMessage());
			}
			break;
			
			
			
		case JSON_WALLET_P2P_REG_RECEIVER_WALLETID_REGISTER:
			try {
				String receiverWalletId = null;  String senderRelationshipNo = null; String privateKey = null; boolean allow = true; PrintWriter jsonOutput_1 = null;
				String receiverRelationshipNo = null;   
				

				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("receiverwalletid")!=null) receiverWalletId = jsonObj.get("receiverwalletid").toString().replaceAll("\"", "");	
				if(jsonObj.get("relnosender")!=null) senderRelationshipNo = jsonObj.get("relnosender").toString().replaceAll("\"", "");
				if(jsonObj.get("relnoreceiver")!=null) receiverRelationshipNo = jsonObj.get("relnoreceiver").toString().replaceAll("\"", "");

				PPWalletEnvironment.setComment(2, className,  "Values are"+receiverWalletId +"||"+ privateKey+"||"+senderRelationshipNo+"||"+receiverRelationshipNo);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_P2P_REG_RECEIVER_WALLETID_REGISTER: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				boolean result = (boolean)WalletDao.class.getConstructor().newInstance().insertReceiverWalletForRegistration( receiverWalletId, senderRelationshipNo, receiverRelationshipNo);
				
				if(result && allow) {
					
					
					obj.add("error", gson.toJsonTree("false"));
					
					
				}else {

					obj.add("error", gson.toJsonTree("true"));
					}
			
			try {
				//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
				jsonOutput_1.print(gson.toJson(obj));
			} finally {
		//close all objects here
	
		if(jsonOutput_1!=null) jsonOutput_1.close();  if(receiverRelationshipNo!=null) receiverRelationshipNo=null;
		if(senderRelationshipNo!=null) senderRelationshipNo=null;	
		if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(receiverWalletId!=null) receiverWalletId=null;

				}
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_P2P_REG_RECEIVER_WALLETID_REGISTER "+e.getMessage());
			}
			break;
			
		case JSON_USER_REGISTER_BANKPAGE:
			try {
				String privateKey = null; boolean allow = true; 
				PrintWriter jsonOutput_1 = null; String relationshipNo= null; ArrayList<BankDetails> arrBankDetails = null;

				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");	
				

				PPWalletEnvironment.setComment(2, className,  "Values are"+relationshipNo +"||"+ privateKey);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_USER_REGISTER_BANKPAGE: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				
				arrBankDetails= (ArrayList<BankDetails>)PaymentDao.class.getConstructor().newInstance().getCustomerRegisteredBanks(relationshipNo); 
				
				if(arrBankDetails!=null && allow){
					int count = arrBankDetails.size();
					
					String[] bankCodeArray= new String [count];
					String[] branchCodeArray= new String [count];
					String[] bankNameArray= new String [count];
					String[] bankAccNoArray= new String [count];
					String[] bankAccNameArray= new String [count];
				
					 // put some value pairs into the JSON object .
					 for(int i=0;i<arrBankDetails.size();i++){
						 bankCodeArray[i]= ((BankDetails)arrBankDetails.get(i)).getBankCode();
						 branchCodeArray[i]= ((BankDetails)arrBankDetails.get(i)).getBranchCode();
						 bankNameArray[i]= ((BankDetails)arrBankDetails.get(i)).getBankName();
						 bankAccNoArray[i]= ((BankDetails)arrBankDetails.get(i)).getBankAccountNo();
						 bankAccNameArray[i]= ((BankDetails)arrBankDetails.get(i)).getBankAccountName();
			
					 }
					 
					 obj.add("bankcode", gson.toJsonTree(bankCodeArray));
					  obj.add("branchcode", gson.toJsonTree(branchCodeArray));
					  obj.add("bankname", gson.toJsonTree(bankNameArray));
					  obj.add("bankaccountno", gson.toJsonTree(bankAccNoArray));
					  obj.add("bankaccountname", gson.toJsonTree(bankAccNameArray));
					  obj.add("error", gson.toJsonTree("false"));
					  
				} else {
					
					obj.add("error", gson.toJsonTree("true"));
					
				}
				
				
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON_USER_REGISTER_BANKPAGE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
					
					
					} finally {
					//close all objects here
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(arrBankDetails!=null) arrBankDetails =null; 
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
						if(relationshipNo!=null) relationshipNo=null;
				}
					
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_USER_REGISTER_BANKPAGE "+e.getMessage());
			}
			break;
			
			
			
		case JSON_REGISTERBANK_DETAILS:
			try {
				String privateKey = null; boolean allow = true; String transactionCode =null; boolean success = false;
				 String topUpAmount = null; String bankCode = null; String branchCode = null;
				String bankName = null;  String bankAccountNo = null; String bankAccountName = null; 
				PrintWriter jsonOutput_1 = null;  String relationshipNo= null; String userId = null; String userType= "C"; String moduleCode= "C";
				
				
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("bankname")!=null) bankName = jsonObj.get("bankname").toString().replaceAll("\"", "");
				if(jsonObj.get("accname")!=null) bankAccountName = jsonObj.get("accname").toString().replaceAll("\"", "");
				if(jsonObj.get("bankcode")!=null) bankCode = jsonObj.get("bankcode").toString().replaceAll("\"", "");
				if(jsonObj.get("accno")!=null) bankAccountNo = jsonObj.get("accno").toString().replaceAll("\"", "");
				if(jsonObj.get("branchcode")!=null) branchCode = jsonObj.get("branchcode").toString().replaceAll("\"", "");
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				
				
			
					PPWalletEnvironment.setComment(2, className, "bank name "+bankName);
					PPWalletEnvironment.setComment(2, className, "Account Name "+bankAccountName);
					PPWalletEnvironment.setComment(2, className, "Bank Code "+bankCode);
					PPWalletEnvironment.setComment(2, className, "Branch Code "+ bankCode);
					PPWalletEnvironment.setComment(2, className, "Account number is "+bankAccountNo);
					PPWalletEnvironment.setComment(2, className, "Top Amount is "+topUpAmount);
					
					
					
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_WALLET_TOPUP_BY_BANK: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				
				success = (boolean)PaymentDao.class.getConstructor().newInstance().addBankDetails(bankCode, branchCode, relationshipNo, bankName, bankAccountNo, bankAccountName);
				if (success & allow) {
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Bank Details updated "+bankCode, 0, 48) );
					obj.add("error", gson.toJsonTree("false"));
					
					
				}else {
					obj.add("error", gson.toJsonTree("true"));
				}
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON_USER_REGISTER_BANKPAGE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
					
					
					} finally {
					//close all objects here
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(bankName!=null) bankName =null; if(relationshipNo!=null) relationshipNo=null;
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if (userId!=null) userId=null;
						if(bankAccountName!=null) bankAccountName=null;	if(bankCode!=null) bankCode=null;
						if(bankAccountNo!=null) bankAccountNo=null;	if(branchCode!=null) branchCode=null;
						
				}
				
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_REGISTERBANK_DETAILS "+e.getMessage());
			}
			break;
			
			
		case JSON_REGISTER_MPESAPAGE:
			
			try {
				String privateKey = null; boolean allow = true; ArrayList<MpesaDetails> arrMpesaDetails = null;
				PrintWriter jsonOutput_1 = null; String relationshipNo= null;

				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relno")!=null) relationshipNo = jsonObj.get("relno").toString().replaceAll("\"", "");	
				

				PPWalletEnvironment.setComment(2, className,  "Values are"+relationshipNo +"||"+ privateKey);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_REGISTER_MPESAPAGE: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				arrMpesaDetails= (ArrayList<MpesaDetails>)PaymentDao.class.getConstructor().newInstance().getCustomerRegMpesaNos(relationshipNo);
				
				if(arrMpesaDetails!=null && allow){
					int count = arrMpesaDetails.size();
					
					String[] mpesaNumberArray= new String [count];
					String[] createdOnArray= new String [count];
					
				
					 // put some value pairs into the JSON object .
					 for(int i=0;i<arrMpesaDetails.size();i++){
						 mpesaNumberArray[i]= ((MpesaDetails)arrMpesaDetails.get(i)).getMpesaNumber();
						 createdOnArray[i]= ((MpesaDetails)arrMpesaDetails.get(i)).getDateCreated();
			
					 }
					 
					 obj.add("mpesanumber", gson.toJsonTree(mpesaNumberArray));
					  obj.add("createdon", gson.toJsonTree(createdOnArray));
					  obj.add("error", gson.toJsonTree("false"));
					  
				} else {
					obj.add("error", gson.toJsonTree("true"));					
				}
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON_USER_REGISTER_BANKPAGE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
					
					
					} finally {
					//close all objects here
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(arrMpesaDetails!=null) arrMpesaDetails =null; 
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
						if(relationshipNo!=null) relationshipNo=null;
				}
					
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_REGISTER_MPESAPAGE "+e.getMessage());
			}
			break;
			
		case JSON_REGISTER_MPESANUMBER:
			try {
				String mpesaNumber = null; String nationalIdNumber = null; String surname = null; PrintWriter jsonOutput_1 = null; boolean allow = true;
				boolean success = false; String firstName = null; String relationshipNo=null; String userId= null; String privateKey= null; String userType="C"; String moduleCode="C";
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("mpesanumber")!=null) mpesaNumber = jsonObj.get("mpesanumber").toString().replaceAll("\"", "");
				if(jsonObj.get("surname")!=null) surname = jsonObj.get("surname").toString().replaceAll("\"", "");
				if(jsonObj.get("firstname")!=null) firstName = jsonObj.get("firstname").toString().replaceAll("\"", "");
				if(jsonObj.get("idnumber")!=null) nationalIdNumber = jsonObj.get("idnumber").toString().replaceAll("\"", "");
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_REGISTER_MPESANUMBER: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				success = (boolean)PaymentDao.class.getConstructor().newInstance().addMpesaDetails(relationshipNo, mpesaNumber, surname,firstName, nationalIdNumber);
				if (success & allow) {
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring(" Added Mpesa details "+mpesaNumber, 0, 48));
					obj.add("error", gson.toJsonTree("false"));
					}else {
					obj.add("error", gson.toJsonTree("true"));
				}
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON_USER_REGISTER_BANKPAGE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
					} finally {
					//close all objects here
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(nationalIdNumber!=null) nationalIdNumber =null; if(relationshipNo!=null) relationshipNo=null;
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if (userId!=null) userId=null;
						if(mpesaNumber!=null) mpesaNumber=null;	 if(firstName!=null) firstName=null;	
				}	
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_REGISTER_MPESANUMBER "+e.getMessage());
			}
			break;
			
		//Eric
			
				case Rules.JSON_CUST_GET_WALLET_DETAILS:
			
			/*String langType = null;	ArrayList<Wallet>  arrWallet = null;
			if(request.getParameter("hdnlang")!=null) 			langType = StringUtils.trim(request.getParameter("hdnlang"));
			arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( ((User)session.getAttribute("SESS_USER")).getRelationshipno() );
			request.setAttribute("walletlist", arrWallet);
			request.setAttribute("langPref", langType.toLowerCase());	
			request.setAttribute("lastaction", "wal");	drequest.setAttribute("lastrule", "View Wallet");
			response.setContentType("text/html");*/
			
			try {
				
				String relationshipNo = null; String privateKey = null;	boolean allow = true;
				PrintWriter jsonOutput_1 = null; ArrayList<Wallet>  arrWallet = null;
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				
				
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect "+privateKey);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject();
				arrWallet = (ArrayList<Wallet>)WalletDao.class.getConstructor().newInstance().getWalletDetails( relationshipNo);
				if(arrWallet!=null && allow){
					int count = arrWallet.size();
					  String[] walletid = new String [count];
					  String[] walBalance = new String [count];
					  String[] walName = new String [count];
					  for(int i=0;i<arrWallet.size();i++){
						  walletid[i]= ((Wallet)arrWallet.get(i)).getWalletId();
						  walBalance[i] = ((Wallet)arrWallet.get(i)).getCurrentBalance();
						  walName[i] = ((Wallet)arrWallet.get(i)).getWalletDesc();
					  }
					  obj.add("walletid", gson.toJsonTree(walletid));  
					  obj.add("walletname", gson.toJsonTree(walName));  
					  obj.add("walletbalance", gson.toJsonTree(walBalance));  
					  obj.add("error", gson.toJsonTree("false"));
				}else {
					obj.add("error", gson.toJsonTree("true"));
				}
				
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(relationshipNo!=null) relationshipNo=null;
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
				}
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE "+e.getMessage());
			}
			break;
			
			//JSON_CUST_WALLET_TOPUP_WITH_QR
		case Rules.JSON_CUST_WALLET_TOPUP_WITH_QR:
			
			try {
				
				String userId = null;	String relationshipNo = null;	String userType = "C"; String privateKey = null;	boolean allow = true;
				String scanString = null;	String latitude = null;	String longitude = null; String walletId = null;	String payType = null; 	
				String billerCode = null;	String currencyId = null;	String dynamicKey = null; String location = null;	String topUpAmount = null; 	
				PrintWriter jsonOutput_1 = null; String transactionId = null;	String qrType = null; String[] qrElements = null; 
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("scanstring")!=null) scanString = jsonObj.get("scanstring").toString().replaceAll("\"", "");
				if(jsonObj.get("latitude")!=null) latitude = jsonObj.get("latitude").toString().replaceAll("\"", "");
				if(jsonObj.get("longitude")!=null) longitude = jsonObj.get("longitude").toString().replaceAll("\"", "");
				if(jsonObj.get("walletid")!=null) walletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
				if(latitude != null && longitude != null) location = latitude+"-"+longitude;
//				D|WTM|551393312715|404|200|2020-04-19 07:23:09				
			//  1  2      3         4   5    6
				
				//check private key
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_WALLET_TOPUP_WITH_QR: pvt key is incorrect "+privateKey);
				}
				
				// check scanString
				if(scanString !=null) { qrElements = scanString.split("\\|");
					qrType = qrElements[0]; payType = qrElements[1]; billerCode = qrElements[2]; topUpAmount = qrElements[4]; currencyId = qrElements[3]; dynamicKey = qrElements[5];
				}
				
				Date current=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utilities.getMYSQLCurrentTimeStampForInsert());Date qrTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dynamicKey);
				qrTime = DateUtils.addMinutes(qrTime, 7);
				PPWalletEnvironment.setComment(3, className, "current "+current +" qrTime "+qrTime);
				if(current.after(qrTime)) {
					allow = false;
					PPWalletEnvironment.setComment(3, className, "it is late by two minutes ");
				}
				
				
				
				PPWalletEnvironment.setComment(3, className, "walletId "+walletId +" billerCode "+billerCode+" currencyId "+currencyId+" location "+location+" topUpAmount "+topUpAmount+" relationshipNo "+relationshipNo);
				//check allow is  true to avoid inserting data for false private key
				if(allow && payType.equals(PPWalletEnvironment.getCodeWalletTopUpFromMerchant())) {
					transactionId = (String)WalletDao.class.getConstructor().newInstance().initiateTopupViaMerch(walletId, billerCode, currencyId, location, topUpAmount, relationshipNo);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject();
				
				//check both allow and txn id
				if(transactionId!=null && allow) {
					//audit trail
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId , userType, "C", StringUtils.substring(" Wallet Top up using Merchant Store  Txncode "+transactionId, 0, 48) );
					obj.add("error", gson.toJsonTree("false"));
				}else {
					obj.add("error", gson.toJsonTree("true"));
				}
				
				//return response
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userType!=null) userType=null;
					if(qrType!=null) qrType=null;if(qrElements!=null) qrElements=null;if(billerCode!=null) billerCode=null;if(topUpAmount!=null) topUpAmount=null;
					if(currencyId!=null) currencyId=null;if(dynamicKey!=null) dynamicKey=null;if(location!=null) location=null;if(transactionId!=null) transactionId=null;
					if(walletId!=null) walletId=null;if(longitude!=null) longitude=null;if(latitude!=null) latitude=null;if(scanString!=null) scanString=null;
					if(relationshipNo!=null) relationshipNo=null;if(payType!=null) payType=null; if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
					if(current!=null) current=null;if(qrTime!=null) qrTime=null;
				}
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_WALLET_TOPUP_WITH_QR "+e.getMessage());
			}
			
			
			break;
			
			
//			JSON_CUST_WALLET_CASHOUT_WITH_QR
		case Rules.JSON_CUST_WALLET_CASHOUT_WITH_QR:
			try {
				
				String userId = null;	String relationshipNo = null;	String userType = "C"; String privateKey = null;	boolean allow = true;
				String latitude = null;	String longitude = null;	String custWalletId = null; String location = null;	String currencyId = null;	String dynamicKey = null; 
				String[] qrElements = null;	String qrType = null;	String payType = null; String billerCode = null;String cashoutAmount = null;	
				PrintWriter jsonOutput_1 = null; String scanString = null;  String transactionId = null;
				
				
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("relationshipno")!=null) relationshipNo = jsonObj.get("relationshipno").toString().replaceAll("\"", "");
				if(jsonObj.get("scanstring")!=null) scanString = jsonObj.get("scanstring").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("latitude")!=null) latitude = jsonObj.get("latitude").toString().replaceAll("\"", "");
				if(jsonObj.get("longitude")!=null) longitude = jsonObj.get("longitude").toString().replaceAll("\"", "");
				if(jsonObj.get("walletid")!=null) custWalletId = jsonObj.get("walletid").toString().replaceAll("\"", "");
				if(latitude != null && longitude != null) location = latitude+"-"+longitude;
				
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect "+privateKey);
				}
				
				if(scanString !=null) { qrElements = scanString.split("\\|");
				qrType = qrElements[0]; payType = qrElements[1]; billerCode = qrElements[2]; cashoutAmount = qrElements[4]; currencyId = qrElements[3]; dynamicKey = qrElements[5];
				}
				//TODO confirm with Arnab the code
				Date current=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utilities.getMYSQLCurrentTimeStampForInsert());Date qrTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dynamicKey);
				qrTime = DateUtils.addMinutes(qrTime, 7);
				PPWalletEnvironment.setComment(3, className, "current "+current +" qrTime "+qrTime);
				if(current.after(qrTime)) {
					allow = false;
					PPWalletEnvironment.setComment(3, className, "it is late by two minutes ");
				}
				
				
				
				PPWalletEnvironment.setComment(3, className, "walletId "+custWalletId +" billerCode "+billerCode+" currencyId "+currencyId+" location "+location+" cashoutAmount "+cashoutAmount+" relationshipNo "+relationshipNo);
				//check allow is  true to avoid inserting data for false private key
				if(allow && payType.equals(PPWalletEnvironment.getCodeWalletCashOutFromMerhant())) {
					transactionId = (String)CashOutDao.class.getConstructor().newInstance().cashOutTransaction(relationshipNo,  custWalletId, billerCode, currencyId, cashoutAmount,  location);
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject();
				
				//check both allow and txn id
				if(transactionId!=null && allow) {
					//audit trail
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId , userType, "C", StringUtils.substring(" Wallet Top up using Merchant Store  Txncode "+transactionId, 0, 48) );
					obj.add("error", gson.toJsonTree("false"));
				}else {
					obj.add("error", gson.toJsonTree("true"));
				}
				
				//return response
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_OPS_LOGIN_VALIDATE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userType!=null) userType=null;
					if(qrType!=null) qrType=null;if(qrElements!=null) qrElements=null;if(billerCode!=null) billerCode=null;if(cashoutAmount!=null) cashoutAmount=null;
					if(currencyId!=null) currencyId=null;if(dynamicKey!=null) dynamicKey=null;if(location!=null) location=null;if(transactionId!=null) transactionId=null;
					if(custWalletId!=null) custWalletId=null;if(longitude!=null) longitude=null;if(latitude!=null) latitude=null;if(scanString!=null) scanString=null;
					if(relationshipNo!=null) relationshipNo=null;if(payType!=null) payType=null; if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
					if(current!=null) current=null;if(qrTime!=null) qrTime=null;
				}
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_WALLET_CASHOUT_WITH_QR "+e.getMessage());
			}
			break;
		
		}

	}
	@Override
	public void callException(HttpServletRequest request, HttpServletResponse response, ServletContext ctx,
			HttpSession session, Exception e, String msg) throws Exception {
			try {
				if(session!=null) 	session.invalidate();
					PPWalletEnvironment.setComment(1, className, "Error is "+msg);
					request.setAttribute("errormsg", msg);
					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getErrorPage()).forward(request, response);
				} catch (Exception e1) {
					PPWalletEnvironment.setComment(1, className, "Problem in forwarding to Error Page, error : "+e1.getMessage());
				}
	}

	@Override
	public void performUploadOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			List<FileItem> multiparts, ServletContext ctx) throws Exception {
		// TODO Auto-generated method stub
	}

}

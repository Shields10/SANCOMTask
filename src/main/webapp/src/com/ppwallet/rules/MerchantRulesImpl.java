package com.ppwallet.rules;

import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.MerchBillPaymentDao;
import com.ppwallet.dao.MerchantDao;
import com.ppwallet.dao.MerchPaymentsDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.model.MerchBillPaymentTransactions;
import com.ppwallet.model.MerchClientDetails;
import com.ppwallet.model.ChartOfAccounts;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.SystemMsfPlans;
import com.ppwallet.model.User;
import com.ppwallet.utilities.Utilities;

public class MerchantRulesImpl implements Rules{

	private static String className = MerchantRulesImpl.class.getSimpleName();
	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(false);
	
		
		switch (rules){
    case Rules.REGISTRATION_REQ_PAGE:
			try {
				String usertype = null;	
				
				if(request.getParameter("hdnusertype")!=null)   usertype =  request.getParameter("hdnusertype").trim();
				/*
				 * if(request.getParameter("hdnlang")!=null) langType =
				 * StringUtils.trim(request.getParameter("hdnlang"));
				 */
				
				if(usertype==null)	throw new Exception ("User Type not selected during registration");
					
//				request.setAttribute("langPref", langType);				
				response.setContentType("text/html");
				if(usertype.equals("C")) {
					ctx.getRequestDispatcher(PPWalletEnvironment.getCustomerRegistrationPage()).forward(request, response);
				}else if(usertype.equals("M")) {
					request.setAttribute("mccvalues", (ConcurrentHashMap<String,String>)MerchantDao.class.getConstructor().newInstance().getMerchantCategories());
					ctx.getRequestDispatcher(PPWalletEnvironment.getMerchantRegistrationPage()).forward(request, response);
				}else if(usertype.equals("P")) {  // customers from persodb
					ctx.getRequestDispatcher(PPWalletEnvironment.getPersoCustomerRegistrationPage()).forward(request, response);
				}
						
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
		break;
		
         case Rules.MERCHANT_VIEW_PROFILE_PAGE:
		 try {
			
			User userM = null;
			userM = (User)session.getAttribute("SESS_USER");
			request.setAttribute("lastaction", "merchprf"); // going to profile
			request.setAttribute("lastrule", "View Profile"); //  set the last rule for left menu selection
			response.setContentType("text/html");
			
			request.setAttribute("merchfullprofile", (Merchant)MerchantDao.class.getConstructor().newInstance().getMerchantProfile(userM.getBillerCode()));
			request.setAttribute("mccvalues", (ConcurrentHashMap<String,String>)MerchantDao.class.getConstructor().newInstance().getMerchantCategories());
			
			try{
				ctx.getRequestDispatcher(PPWalletEnvironment.getMerchantProfilePage()).forward(request, response);
			}finally {
				if(userM!=null) userM=null;
			}
			
			} catch
			  (Exception e) { callException(request, response, ctx, session, e,  e.getMessage());
			  }
		break;
		
         case Rules.MERCHANT_DOWNLOAD_KYC_DOC: 
			  String fileName = null; 
			  PrintWriter out_download1= null;
		  
			try { 		  
			 if(request.getParameter("hdnfilename")!=null) 
				  fileName = request.getParameter("hdnfilename").trim();
				  PPWalletEnvironment.setComment(3, className, "Before Download the fileName "+fileName);
				  out_download1 = response.getWriter();
				  String filePath = PPWalletEnvironment.getFileDownloadPath();
				  response.setContentType("APPLICATION/OCTET-STREAM");
				  response.setHeader("Content-Disposition", "attachment; filename=\"" +fileName + "\"");
			  
				  FileInputStream fileInputStream = new FileInputStream(filePath +"/"+ fileName);
				  PPWalletEnvironment.setComment(3, className, "Total path is : "+filePath +"/"+ fileName);
				  int i; 
				  while ((i = fileInputStream.read()) != -1)
				  { 
					  out_download1.write(i);
				  }
				  fileInputStream.close();
			  
			  
			  	}catch
			  		(Exception e) { 
			  			callException(request, response, ctx, session, e, e.getMessage()); 
			  		}finally {
			  			if (out_download1!=null)	out_download1.close();
			  		}
			  break;
			  
         case Rules.MERCHANT_VIEW_MSF:
			  try { 
				// Now get and display msf details
					request.setAttribute("merchantmsfplan", (ArrayList<SystemMsfPlans>)MerchantDao.class.getConstructor().newInstance().getMerchantMsfPlan( ((User)session.getAttribute("SESS_USER")).getBillerCode()));
					request.setAttribute("lastaction", "merchprf");	
					request.setAttribute("lastrule", "View Rates"); 
					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getMerchantMsfPlanPage()).forward(request, response);
				  
			  }catch
		  		(Exception e) { 
		  			callException(request, response, ctx, session, e, e.getMessage()); 
		  		}
				break;
			 
			
		

		
		
		//  old
	case Rules.MERCH_PAYMENTS_MODULE_QRPAGE:
				try {
					PPWalletEnvironment.setComment(3,className,"In MERCH_PAYMENTS_MODULE_QRPAGE     ");

					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "pymts");
					request.setAttribute("lastrule", Rules.MERCH_PAYMENTS_MODULE_QRPAGE); 
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getMerchantStaticQrPage()).forward(request,response);
					} finally {
					}
				} catch (Exception e) {
					callException(request, response, ctx, session, e, e.getMessage());
				}								
			break;	
			
		
		case Rules.DISP_MERCH_QR_CODE:
			try {
			FileInputStream in = null;
			OutputStream out = null;
			File myFile = null;
			String filePath = PPWalletEnvironment.getFileUploadPath()+File.separator+"QR.png";
			String city = null; String merchId = null; String companyName = null;String billerCode = null;
			String tnxType = "WRP"; String txnCurrencyId = "404";

			Merchant mMerch = null;
				
				if(session.getAttribute("SESS_USER")!=null)		
				billerCode = ((User)session.getAttribute("SESS_USER")).getBillerCode();
								
				mMerch=(Merchant)MerchantDao.class.getConstructor().newInstance().getMerchantDetails(billerCode);
				
				if(mMerch != null) {
					city = mMerch.getCity();
					merchId = mMerch.getMerchantId();
					companyName = mMerch.getCompanyName();
				
				int size = 500;
				String fileType = "png";
				String myCodeText = null;
				myFile = new File(filePath);
				 
		 	// at this moment we are not encoding the string with dynamic ID generation
				//myCodeText = billerCode+"|"+city+"|"+merchId+"|"+companyName;
				myCodeText = "S"+"|"+tnxType+"|"+billerCode+"|"+txnCurrencyId;

				
		 		Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
				hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
				hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
				hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
				
				QRCodeWriter qrCodeWriter = new QRCodeWriter();
				BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size,	size, hintMap);
				int CrunchifyWidth = byteMatrix.getWidth();
				BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,	BufferedImage.TYPE_INT_RGB);
				image.createGraphics();
				
				Graphics2D graphics = (Graphics2D) image.getGraphics();
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
				graphics.setColor(Color.BLACK);
				
				for (int i = 0; i < CrunchifyWidth; i++) {
					for (int j = 0; j < CrunchifyWidth; j++) {
						if (byteMatrix.get(i, j)) {
							graphics.fillRect(i, j, 1, 1);
						}
					}
				}
				
				ImageIO.write(image, fileType, myFile);

				 in = new FileInputStream(myFile);
			     out = response.getOutputStream();
			     response.setContentType("image/png");
			     response.setContentLength((int)myFile.length());
			     response.setHeader("Content-Disposition", "inline; filename=\"" + myFile.getName() + "\"");
			      // Copy the contents of the file to the output stream
			       byte[] buf = new byte[1024];
			       int count = 0;
			       while ((count = in.read(buf)) >= 0) {
			         out.write(buf, 0, count);
			      }
				}
				if(out!=null)	 out.close();
			    if(in!=null)	in.close();
			    if(myFile.exists())	myFile.delete();	
			    if(myFile.exists())	myFile.delete();
		

			} catch(Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			} finally {
				

			}
		
			break;
			

		
		case Rules.MERCH_PAYMENT_MODULE_DQR_PAGE:
			
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "pymts"); 
				request.setAttribute("lastrule",Rules.MERCH_PAYMENT_MODULE_DQR_PAGE );

				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getMerchantDynamicQrPage()).forward(request,
							response);
				} finally {
				}
				
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}		
			
			break;
			

			/**merchant dynamic qr code logic **/
			
		/**
		 * **/case Rules.MERCH_PAYMENT_MODULE_DYNAMICQR:
			try {
			FileInputStream merchDqrin = null;
			OutputStream merchDqrout = null;
			File mFile = null;

				SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmm");			
				Date dateobj = new Date();
				
				PPWalletEnvironment.setComment(3,className,"Date val is    "+ df.format(dateobj) );
				String filePath = PPWalletEnvironment.getFileUploadPath()+"/"+"QR.png";				
				String city = null; String merchId = null; String companyName = null;String billerCode = null;   
				String userId = null;	Merchant mMerch = null;
				
				if(session.getAttribute("SESS_USER")!=null)		
					billerCode = ((User)session.getAttribute("SESS_USER")).getBillerCode();
				mMerch=(Merchant)MerchantDao.class.getConstructor().newInstance().getMerchantDetails(billerCode);
				if(mMerch != null) {
					//billerCode = mMerch.getBillerCode();
					city = mMerch.getCity();
					merchId = mMerch.getMerchantId();
					companyName = mMerch.getCompanyName();
				int size = 500;
				String fileType = "png";
				String myCodeText = null;
				mFile = new File(filePath);
				
				String currentDate = (df.format(dateobj));
				 //'D|WCO|551393312715|404|1000000000000|2020-04-24 11:41:18';
		 	// at this moment we are not encoding the string with dynamic ID generation
		 		myCodeText = "D"+billerCode+"|"+city+"|"+merchId+"|"+companyName+"|"+Utilities.encryptString(currentDate);
		 	
		 		PPWalletEnvironment.setComment(3,className,"QR String is     "+ myCodeText );
		 		Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
				hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
				hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
				hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
				
				QRCodeWriter qrCodeWriter = new QRCodeWriter();
				BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size,	size, hintMap);
				int CrunchifyWidth = byteMatrix.getWidth();
				BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,	BufferedImage.TYPE_INT_RGB);
				image.createGraphics();
				
				Graphics2D graphics = (Graphics2D) image.getGraphics();
				graphics.setColor(Color.WHITE);
				graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
				graphics.setColor(Color.BLACK);
				
				for (int i = 0; i < CrunchifyWidth; i++) {
					for (int j = 0; j < CrunchifyWidth; j++) {
						if (byteMatrix.get(i, j)) {
							graphics.fillRect(i, j, 1, 1);
						}
					}
				}
				PPWalletEnvironment.setComment(3,className,"before writing to      "+ myCodeText );
				ImageIO.write(image, fileType, mFile);

				merchDqrin = new FileInputStream(mFile);
				merchDqrout = response.getOutputStream();
			     response.setContentType("image/png");
			     response.setContentLength((int)mFile.length());
			     response.setHeader("Content-Disposition", "inline; filename=\"" + mFile.getName() + "\"");
			      // Copy the contents of the file to the output stream
			       byte[] buf = new byte[1024];
			       int count = 0;
			       while ((count = merchDqrin.read(buf)) >= 0) {
			    	   merchDqrout.write(buf, 0, count);
			      }
				} else {
					PPWalletEnvironment.setComment(3,className,"merch data is empty    "+ mMerch);

				}
				if(merchDqrout!=null)		    merchDqrout.close();
			    if(merchDqrin!=null)	merchDqrin.close();
			    if(mFile.exists())	mFile.delete();
				
			} catch(Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			} finally {

		}
			break;
			
	
			/* case statement to display merchant accept payment page */
		case Rules.MERCHANT_PAYMENT_MODULE_RETAIL_PAYMENT_PAGE:
			
			try {
				
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "pymts"); 
				request.setAttribute("lastrule",Rules.MERCHANT_PAYMENT_MODULE_RETAIL_PAYMENT_PAGE );
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getMercAccPymtPage()).forward(request,
							response);
				} finally {
				}
				

				
			}catch(Exception e ) {
				
				callException(request, response, ctx, session, e, e.getMessage());

			}
			
			break;
			
			
			
			
			/* merchant accept payment logic
			 * handles getting merchant and chart of accounts details then generate a qr to accept payments
			 *  
			 *  */
			
		case Rules.MERCH_PAYMENT_MODULE_RETAIL_PAYMENTSQR:
			
			try {
				
				FileInputStream merchDqrin = null;
				OutputStream merchDqrout = null;
				File mFile = null;

					SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmm");			
					Date dateobj = new Date();
					
					PPWalletEnvironment.setComment(3,className,"inside merch retail payments    "+ df.format(dateobj) );
					String filePath = PPWalletEnvironment.getFileUploadPath()+"/"+"QR.png";				
					String billerCode = null;   
					 
					String tnxType = "WRP"; String txnCurrencyId = "404"; String retailPayAmount = null;
					if(request.getParameter("amount")!=null) 			retailPayAmount = StringUtils.trim(request.getParameter("amount"));
					
			 		PPWalletEnvironment.setComment(3,className,"retailPayAmount is     "+ retailPayAmount );

					if(session.getAttribute("SESS_USER")!=null)		
						billerCode = ((User)session.getAttribute("SESS_USER")).getBillerCode();
					
					int size = 500;
					String fileType = "png";
					String myCodeText = null;
					mFile = new File(filePath);
					
					String currentDate = (df.format(dateobj));

			 	// at this moment we are not encoding the string with dynamic ID generation
			 		myCodeText = "D"+"|"+tnxType+"|"+billerCode+"|"+txnCurrencyId+"|"+retailPayAmount+"|"+Utilities.getMYSQLCurrentTimeStampForInsert() ;
			 		
			 		PPWalletEnvironment.setComment(3,className,"QR String is     "+ myCodeText );
			 		Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
					hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
					hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
					hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
					
					QRCodeWriter qrCodeWriter = new QRCodeWriter();
					BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size,	size, hintMap);
					int CrunchifyWidth = byteMatrix.getWidth();
					BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,	BufferedImage.TYPE_INT_RGB);
					image.createGraphics();
					
					Graphics2D graphics = (Graphics2D) image.getGraphics();
					graphics.setColor(Color.WHITE);
					graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
					graphics.setColor(Color.BLACK);
					
					for (int i = 0; i < CrunchifyWidth; i++) {
						for (int j = 0; j < CrunchifyWidth; j++) {
							if (byteMatrix.get(i, j)) {
								graphics.fillRect(i, j, 1, 1);
							}
						}
					}
					PPWalletEnvironment.setComment(3,className,"before writing to      "+ myCodeText );
					ImageIO.write(image, fileType, mFile);

					merchDqrin = new FileInputStream(mFile);
					merchDqrout = response.getOutputStream();
				     response.setContentType("image/png");
				     response.setContentLength((int)mFile.length());
				     response.setHeader("Content-Disposition", "inline; filename=\"" + mFile.getName() + "\"");
				      // Copy the contents of the file to the output stream
				       byte[] buf = new byte[1024];
				       int count = 0;
				       while ((count = merchDqrin.read(buf)) >= 0) {
				    	   merchDqrout.write(buf, 0, count);
				      }
					
					if(merchDqrout!=null)		    merchDqrout.close();
				    if(merchDqrin!=null)	merchDqrin.close();
				    if(mFile.exists())	mFile.delete();
					
				
				
			} catch(Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());

			}
			break;
			
			
			
			
		case Rules.MERCHANT_PAYMENT_MODULE_CASHOUT_PAGE:
				try {
					
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "pymts"); 
					request.setAttribute("lastrule",Rules.MERCHANT_PAYMENT_MODULE_CASHOUT_PAGE );
					response.setContentType("text/html");
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getMerchCashout()).forward(request,
								response);
						} finally {
						}
					
					
					}catch(Exception e ) {
					
					callException(request, response, ctx, session, e, e.getMessage());
	
					}
		
			break;
			
			
		
		case Rules.MERCH_PAYMENT_MODULE_CASHOUT:
			try {
				
				FileInputStream merchDqrin = null;  OutputStream merchDqrout = null;  File mFile = null; 
				
					SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmm");			
					Date dateobj = new Date();
					
					PPWalletEnvironment.setComment(3,className,"inside cashout   "+ df.format(dateobj) );
					String filePath = PPWalletEnvironment.getFileUploadPath()+"/"+"QR.png";				
				    String billerCode = null;   
					Merchant mMerch = null; ChartOfAccounts coa = null; String coaVal = "1001"; 
					String tnxType = "WCO"; String txnCurrencyId = "404"; String cashoutAmount = null;
					
					if(request.getParameter("amount")!=null)   cashoutAmount =  request.getParameter("amount").trim();

					if(session.getAttribute("SESS_USER")!=null)		
						billerCode = ((User)session.getAttribute("SESS_USER")).getBillerCode();
					mMerch=(Merchant)MerchantDao.class.getConstructor().newInstance().getMerchantDetails(billerCode);
					
							
					if(mMerch != null ) {
						
												
					int size = 500;
					String fileType = "png";
					String myCodeText = null;
					mFile = new File(filePath);
					String currentDate = (df.format(dateobj));
					 //'D|WCO|551393312715|404|1000000000000|2020-04-24 11:41:18';

			 	// at this moment we are not encoding the string with dynamic ID generation
			 		myCodeText = "D"+"|"+tnxType+"|"+billerCode+"|"+txnCurrencyId+"|"+cashoutAmount+"|"+Utilities.getMYSQLCurrentTimeStampForInsert() ;
			 		
			 		Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
					hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
					hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
					hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
					
					QRCodeWriter qrCodeWriter = new QRCodeWriter();
					BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size,	size, hintMap);
					int CrunchifyWidth = byteMatrix.getWidth();
					BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,	BufferedImage.TYPE_INT_RGB);
					image.createGraphics();
					
					Graphics2D graphics = (Graphics2D) image.getGraphics();
					graphics.setColor(Color.WHITE);
					graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
					graphics.setColor(Color.BLACK);
					
					for (int i = 0; i < CrunchifyWidth; i++) {
						for (int j = 0; j < CrunchifyWidth; j++) {
							if (byteMatrix.get(i, j)) {
								graphics.fillRect(i, j, 1, 1);
							}
						}
					}
					PPWalletEnvironment.setComment(3,className,"before writing to      "+ myCodeText );
					ImageIO.write(image, fileType, mFile);

					merchDqrin = new FileInputStream(mFile);
					merchDqrout = response.getOutputStream();
				     response.setContentType("image/png");
				     response.setContentLength((int)mFile.length());
				     response.setHeader("Content-Disposition", "inline; filename=\"" + mFile.getName() + "\"");
				      // Copy the contents of the file to the output stream
				       byte[] buf = new byte[1024];
				       int count = 0;
				       while ((count = merchDqrin.read(buf)) >= 0) {
				    	   merchDqrout.write(buf, 0, count);
				      }
					} else {
						PPWalletEnvironment.setComment(3,className,"merch data is empty    "+ mMerch);

					}
					if(merchDqrout!=null)		    merchDqrout.close();
				    if(merchDqrin!=null)	merchDqrin.close();
				    if(mFile.exists())	mFile.delete();
					
				
			} catch(Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			
			break;
				
			
		case Rules.MERCHANT_PAYMENT_MODULE_TOPUP_PAGE:
			
			try {
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "pymts");
				request.setAttribute("lastrule",Rules.MERCHANT_PAYMENT_MODULE_TOPUP_PAGE );
				response.setContentType("text/html");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getMerchTopUp()).forward(request,
							response);
				} finally {
				}
				
			}catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			break;
			
		
		case Rules.MERCHANT_PAYMENT_MODULE_TOPUPQR:
			try {
				
				FileInputStream merchDqrin = null; OutputStream merchDqrout = null; File mFile = null; 

					SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmm");			
					Date dateobj = new Date(); 

					
					PPWalletEnvironment.setComment(3,className,"inside Top up   "+ df.format(dateobj) );
					String filePath = PPWalletEnvironment.getFileUploadPath()+"/"+"QR.png";				
				    String billerCode = null;   
					Merchant mMerch = null; ChartOfAccounts coa = null; String coaVal = "1002"; String coaCode = null;
					String tnxType = "WTM"; String txnCurrencyId = "404"; String topupAmount = null;
							 
					if(request.getParameter("amount")!=null)   topupAmount =  request.getParameter("amount").trim();
					PPWalletEnvironment.setComment(3,className,"topupAmount is   "+ topupAmount );


					if(session.getAttribute("SESS_USER")!=null)		
						billerCode = ((User)session.getAttribute("SESS_USER")).getBillerCode();
					
												
					int size = 500;
					String fileType = "png";
					String myCodeText = null;
					mFile = new File(filePath);
					
					String currentDate = (df.format(dateobj));
					 //'D|WCO|551393312715|404|1000000000000|2020-04-24 11:41:18';

			 	// at this moment we are not encoding the string with dynamic ID generation
			 		myCodeText = "D"+"|"+tnxType+"|"+billerCode+"|"+txnCurrencyId+"|"+topupAmount+"|"+Utilities.getMYSQLCurrentTimeStampForInsert() ;
			 		
			 		PPWalletEnvironment.setComment(3,className,"QR String is     "+ myCodeText );
			 		Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
					hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
					hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
					hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
					
					QRCodeWriter qrCodeWriter = new QRCodeWriter();
					BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size,	size, hintMap);
					int CrunchifyWidth = byteMatrix.getWidth();
					BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth,	BufferedImage.TYPE_INT_RGB);
					image.createGraphics();
					
					Graphics2D graphics = (Graphics2D) image.getGraphics();
					graphics.setColor(Color.WHITE);
					graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
					graphics.setColor(Color.BLACK);
					
					for (int i = 0; i < CrunchifyWidth; i++) {
						for (int j = 0; j < CrunchifyWidth; j++) {
							if (byteMatrix.get(i, j)) {
								graphics.fillRect(i, j, 1, 1);
							}
						}
					}
					PPWalletEnvironment.setComment(3,className,"before writing to "+ myCodeText );
					ImageIO.write(image, fileType, mFile);

					merchDqrin = new FileInputStream(mFile);
					merchDqrout = response.getOutputStream();
				     response.setContentType("image/png");
				     response.setContentLength((int)mFile.length());
				     response.setHeader("Content-Disposition", "inline; filename=\"" + mFile.getName() + "\"");
				      // Copy the contents of the file to the output stream
				       byte[] buf = new byte[1024];
				       int count = 0;
				       while ((count = merchDqrin.read(buf)) >= 0) {
				    	   merchDqrout.write(buf, 0, count);
				      }
					
					
					if(merchDqrout!=null)		    merchDqrout.close();
				    if(merchDqrin!=null)	merchDqrin.close();
				    if(mFile.exists())	mFile.delete();
				
			}catch(Exception e) {
				
				callException(request, response, ctx, session, e, e.getMessage());
				
			}
			
			break;
			
				
		case Rules.MERCHANT_BILLPAYMENT_MODULE_BILLER_DETAILS_PAGE:
			try {
				String billerCode = null;	
				if(session.getAttribute("SESS_USER")!=null)		
				billerCode = ((User)session.getAttribute("SESS_USER")).getBillerCode();
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "bllpy"); 
				request.setAttribute("lastrule", Rules.MERCHANT_BILLPAYMENT_MODULE_BILLER_DETAILS_PAGE ); 
				response.setContentType("text/html");
				request.setAttribute("billerdetails", (ArrayList<MerchClientDetails>)MerchBillPaymentDao.class.getConstructor().newInstance().getMerchBillerDetails(billerCode)); 
				ctx.getRequestDispatcher(PPWalletEnvironment.getBillerdetails()).forward(request, response);
					

			}catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
			}
			
			break;
		
		case Rules.MERCHANT_BILLPAYMENT_TRANSACTIONS:
			
			try {
				
				String billerCode = null; 
				
				if(session.getAttribute("SESS_USER")!=null)		
					billerCode = ((User)session.getAttribute("SESS_USER")).getBillerCode();
			 		PPWalletEnvironment.setComment(3,className,"biller code is     "+ billerCode );
					request.setAttribute("langPref", "en");
					request.setAttribute("lastaction", "bllpy"); // going to profile
					request.setAttribute("lastrule", Rules.MERCHANT_BILLPAYMENT_MODULE_BILLER_DETAILS_PAGE ); 	
					request.setAttribute("transdetails", (ArrayList<MerchBillPaymentTransactions>)MerchBillPaymentDao.class.getConstructor().newInstance().getMerchTransactions(billerCode)); 
					response.setContentType("text/html");
					ctx.getRequestDispatcher(PPWalletEnvironment.getBillPayTransactionsPage()).forward(request, response);					
				
			} catch(Exception e) {
				
			callException(request, response, ctx, session, e, e.getMessage());
				
			} finally {
				
				
			}
			
		break;
			
		
		}
		
	}

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callException(HttpServletRequest request, HttpServletResponse response, ServletContext ctx,
			HttpSession session, Exception e, String msg) throws Exception {
		// TODO Auto-generated method stub
		
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
		HttpSession session	= request.getSession(false);
		switch (rulesaction){
			case Rules.REGISTRATION_MERCHANT_ADD:
				try {
					
					String userId = null; String userPwd = null; String userName = null; String userEmail = null; String userContact = null;
					String address1 = null; String address2 = null; String postCode = null;	String companyName = null; String registrationNo = null;
					String mccCode = null;	String billerCode = null; String nationalId= null; String city= null; ArrayList<String> arrMerchFile = new ArrayList<String>();
					int i = 1; FileItem item =null;

				            try {
				                
				                Iterator<FileItem> iterator = multiparts.iterator();
				                if(iterator!=null) {
				                	//System.out.println("inside doPost iterator is "+iterator);
				                }
				                while (iterator.hasNext()) {
				                	PPWalletEnvironment.setComment(2, className,"inside loop i= "+i);
				                    item = (FileItem) iterator.next();

				                    if (!item.isFormField()) {
				                        String fileName = item.getName();
				                        if(fileName != null && !"".equals(fileName)){
					                        PPWalletEnvironment.setComment(2, className,"fileName is "+fileName);
					                        File path = new File(PPWalletEnvironment.getFileUploadPath());
					                        File uploadedFile = new File(path + File.separator + fileName);
					                        PPWalletEnvironment.setComment(2, className,"filepath is "+uploadedFile.getAbsolutePath());
				                        
				                        item.write(uploadedFile);
				                        //TODO should clod the File objects here after writing
				                        arrMerchFile.add(uploadedFile.getAbsolutePath());
				                        }
				                    }else {
				                    	
				                    	if(item.getFieldName().equals("regmerchid"))              	userId = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchpwd")) 		        userPwd = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchname")) 	            userName = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchemail")) 	        userEmail =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchcontact")) 	        userContact =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchaddr1")) 	        address1 = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchaddr2")) 	        address2 = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchpcode")) 	        postCode =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchcompname")) 	        companyName =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchregno")) 	        registrationNo = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("hdnmccid")) 			        mccCode =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchnationalid")) 		nationalId = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("regmerchcity")) 			    city = StringUtils.trim( item.getString() );
										billerCode = Utilities.generateCVV2(12);				                    	
				                    }
				                    i++;
				                }
				             } catch (Exception e) {
				            	 throw new Exception("Error Message is "+e.getMessage());
				            	 
				            }
				            finally{
			            		 if (item != null) {
			            			 item.delete();
								}
			            	 }
				       
					
					
					if(arrMerchFile!=null)
						if(arrMerchFile.size()==0)
							arrMerchFile = null;
					

					// Now register merchant
	    		    boolean success = false;
	    		    success = (boolean)MerchantDao.class.getConstructor().newInstance().registerMerchant(nationalId, userId, userPwd, userName, userEmail, userContact, 
	    		    		address1, address2, postCode, companyName, registrationNo, mccCode, billerCode, city, arrMerchFile);
	    		    
	    		    //consult audit trail on merchant self registration
					String moduleCode = "M"; //M = Merchants Acquiring
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, "M", moduleCode, StringUtils.substring("Merchant self registered "+userId, 0, 48) );
	    		    
					if(success==false)
						throw new Exception ("merchant registration failed");
	    		    response.setContentType("text/html");
	    		    try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getLoginPage()).forward(request, response);
	    		    }finally {
	    		    	if(userId!=null) userId = null; if(userPwd!=null) userPwd = null; if(userName!=null) userName = null; if(userEmail!=null) userEmail = null; 
	    		    	if(userContact!=null) userContact = null; if(address1!=null) address1 = null; if(address2!=null) address2 = null; if(postCode!=null) postCode = null; 
	    		    	if(companyName!=null) companyName = null; if(registrationNo!=null) registrationNo = null; if(mccCode!=null) mccCode = null; if(billerCode!=null) billerCode = null; 
	    		    	if(nationalId!=null) nationalId = null; if(city!=null) city = null; 
					}
					
					
				}catch (Exception e) {
					callException(request, response, ctx, session,e, e.getMessage());
				}
				break;	
				
			case Rules.MERCHANT_UPDATE_PROFILE:
				try {
					String userId = null; String userPwd = null; String userName = null; String userEmail = null; String userContact = null;
					String address1 = null; String address2 = null; String postCode = null;	String companyName = null; String registrationNo = null;
					String mccCode = null;	String billerCode = null; String nationalId= null; String city= null; ArrayList<String> arrMerchFile = new ArrayList<String>();
					int i = 1; FileItem item = null;

				            try {
				                
				                Iterator<FileItem> iterator = multiparts.iterator();
				                if(iterator!=null) {
				                	//System.out.println("inside doPost iterator is "+iterator);
				                }
				                while (iterator.hasNext()) {
				                	PPWalletEnvironment.setComment(2, className,"inside loop i= "+i);
				                    item = (FileItem) iterator.next();

				                    if (!item.isFormField()) {
				                    	String fileName = item.getName();
				                    	 if(fileName != null && !"".equals(fileName)){
					                        PPWalletEnvironment.setComment(2, className,"fileName is "+fileName);
					                        File path = new File(PPWalletEnvironment.getFileUploadPath());
					                        File uploadedFile = new File(path + File.separator + fileName);
					                        PPWalletEnvironment.setComment(2, className,"filepath is "+uploadedFile.getAbsolutePath());
				                        
					                        item.write(uploadedFile);
					                        //TODO should clod the File objects here after writing
					                        arrMerchFile.add(uploadedFile.getAbsolutePath());
				                    	 }else {
				                    		 arrMerchFile = null;
				                    	 }
				                    }else {
				                    	
										if(item.getFieldName().equals("updmerchuserid")) 		    userId = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("hdnbillercode")) 		    billerCode = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchpwd")) 		        userPwd = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchname")) 	            userName = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchemail")) 	        userEmail =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchcontact")) 	        userContact =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchaddr1")) 	        address1 = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchaddr2")) 	        address2 = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchpcode")) 	        postCode =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchcompname")) 	        companyName =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchregno")) 	        registrationNo = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmccid")) 			        mccCode =StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchnationalid")) 		nationalId = StringUtils.trim( item.getString() );
										if(item.getFieldName().equals("updmerchcity")) 			    city = StringUtils.trim( item.getString() );
										
										
										
				                    }
				                    i++;
				                }
				             } catch (Exception e) {
				            	 throw new Exception("Error Message is "+e.getMessage());
				            }
				            finally {
				            	if (item != null) {
			            			 item.delete();
								}
				            }
				       
					
					
					if(arrMerchFile!=null)
						if(arrMerchFile.size()==0)
							arrMerchFile = null;
					

					// update merchant profile
	    		    boolean success = false;
	    		    PPWalletEnvironment.setComment(2, className,"mccCode is "+mccCode);
	    		    success = (boolean)MerchantDao.class.getConstructor().newInstance().updateMerchant(billerCode, nationalId, userId, userPwd, userName, userEmail, userContact,
	    		    		address1, address2, postCode, companyName, registrationNo, mccCode, city, arrMerchFile); 
	    		    if(success == true) {
	    		    	
						String userType = ((User)session.getAttribute("SESS_USER")).getUserType();
						String moduleCode = "M"; //M = Merchants Acquiring
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, moduleCode, StringUtils.substring("Merchant updated his profile "+userId, 0, 48) );
	    		    }
	    		   
					if(success==false)
						throw new Exception ("profile update failed");
	    		    response.setContentType("text/html");
	    		    request.setAttribute("merchfullprofile", (Merchant)MerchantDao.class.getConstructor().newInstance().getMerchantProfile(billerCode));
					request.setAttribute("mccvalues", (ConcurrentHashMap<String,String>)MerchantDao.class.getConstructor().newInstance().getMerchantCategories());
					
					try {
						ctx.getRequestDispatcher(PPWalletEnvironment.getMerchantProfilePage()).forward(request, response);
					}finally {
						if(arrMerchFile!=null) arrMerchFile = null; if(userId!=null) userId = null; if(userPwd!=null) userPwd = null; if(userName!=null) userName = null; if(userEmail!=null) userEmail = null; 
	    		    	if(userContact!=null) userContact = null; if(address1!=null) address1 = null; if(address2!=null) address2 = null; if(postCode!=null) postCode = null; 
	    		    	if(companyName!=null) companyName = null; if(registrationNo!=null) registrationNo = null; if(mccCode!=null) mccCode = null; if(billerCode!=null) billerCode = null; 
	    		    	if(nationalId!=null) nationalId = null; if(city!=null) city = null; 
					}
					
				}catch (Exception e) {
					callException(request, response, ctx, session,e, e.getMessage());
				}
				
				break;
		}
	}
	
	

}

package com.ppwallet.rules;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.MerchBillPaymentDao;
import com.ppwallet.dao.MerchMccGroupDao;
import com.ppwallet.dao.MerchMobileDao;
import com.ppwallet.dao.MerchantDao;
import com.ppwallet.dao.MerchPaymentsDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.model.MerchBillPaymentTransactions;
import com.ppwallet.model.MerchClientDetails;
import com.ppwallet.model.MerchCashoutTransactions;
import com.ppwallet.model.ChartOfAccounts;
import com.ppwallet.model.MccGroup;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.User;
import com.ppwallet.utilities.Utilities;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;




public class MerchMobileRuleImpl implements Rules  {
	
	private static String className = MerchMobileRuleImpl.class.getSimpleName();


	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		// TODO Auto-generated method stub
		
		switch (rulesaction){
		
		case Rules.JSON_MERCHANT_MOBILE_RETAIL_PAYMENT:
			try {
				
				String userId = null;	String privateKey = null;	boolean allow = true; 
				PrintWriter mJsonOutput_1 = null; String billerCode = null;
				String tnxType = "WRP"; String txnCurrencyId = "404"; String retailPayAmount = null;
				
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
				if(jsonObj.get("amount")!=null) retailPayAmount = jsonObj.get("amount").toString().replaceAll("\"", "");
				
				PPWalletEnvironment.setComment(3, className, "userid from mobile is  "+userId + privateKey + retailPayAmount);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect "+privateKey);
				}
				
				FileInputStream merchDqrin = null;
				OutputStream merchDqrout = null;
				File mFile = null;
				
				SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmm");			
				Date dateobj = new Date();
				
				String filePath = PPWalletEnvironment.getFileUploadPath()+"/"+"QR.png";				
				String city = null; String merchId = null; String companyName = null; 
				Merchant mMerch = null; String coaCode = null;
				
				//mMerch=(Merchant)MerchantDao.class.getConstructor().newInstance().getMerchantDetails(billerCode);
				//coa=(ChartOfAccounts)MerchPaymentsDao.class.getConstructor().newInstance().getChartOfAccounts(coaVal);
				
		//if(mMerch != null & coa != null  & allow) {
					//billerCode = mMerch.getBillerCode();
					//city = mMerch.getCity();
					//merchId = mMerch.getMerchantId();
					//companyName = mMerch.getCompanyName();
					//coaCode = coa.getCode();
					
					int size = 250;
					String fileType = "png";
					String myCodeText = null;
					mFile = new File(filePath);
					String currentDate = (df.format(dateobj));
					
			 		myCodeText = "D"+"|"+tnxType+"|"+billerCode+"|"+txnCurrencyId+"|"+retailPayAmount+"|"+Utilities.getMYSQLCurrentTimeStampForInsert() ;

				
					//myCodeText = billerCode+"|"+city+"|"+merchId+"|"+companyName+"|"+Utilities.encryptString(currentDate) +"|" +coaCode +"|" + amount ;
					
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
					ImageIO.write(image, fileType, mFile);
					
					/*convert image to base64 and send it to mobile endpoint */ 
					String bSixty4 = null; 
					File mbSixty4File = new File(filePath);
					FileInputStream imageInFile = new FileInputStream(mbSixty4File);
					Gson gson = new Gson();


					
					try {
//						String bSixty4 = null; 
//						FileInputStream imageInFile = new FileInputStream(mbSixty4File);
						byte imageData[] = new byte[(int) mFile.length()];
					    imageInFile.read(imageData);
					    bSixty4 = Base64.getEncoder().encodeToString(imageData);
						PPWalletEnvironment.setComment(3,className,"the qr "+ bSixty4);
						
						mJsonOutput_1 = response.getWriter();
//						Gson gson = new Gson();
						JsonObject obj = new JsonObject(); //Json Object
						obj.add("error", gson.toJsonTree("false"));
						obj.add("qr", gson.toJsonTree(bSixty4));
						
						PPWalletEnvironment.setComment(3, className, " JSON JSON_mobile qr String is "+gson.toJson(obj));
						mJsonOutput_1.print(gson.toJson(obj));
					}catch(Exception e) {
						PPWalletEnvironment.setComment(1,className,"exception when sending to mobile "+ e.getMessage());
					}finally {
						if(merchDqrout!=null)		    merchDqrout.close();   if(mMerch!= null) mMerch = null;
					    if(merchDqrin!=null)	merchDqrin.close(); 	if(df != null) df = null;
					    if(mFile.exists())	mFile.delete(); 	 if(dateobj != null) dateobj = null;
					    if(mJsonOutput_1!=null) mJsonOutput_1.close(); if(city != null) city = null;
					    if(retailPayAmount!= null) retailPayAmount = null; if(merchId!= null) merchId = null; if(companyName != null) companyName = null;
					    if(privateKey!= null) privateKey = null; if(coaCode != null) coaCode = null; if(fileType != null) fileType = null; if(myCodeText!= null) myCodeText = null;
					    if(userId!= null) userId = null; if(size!= 0) size = 0; if(currentDate != null) currentDate = null; if(qrCodeWriter != null) qrCodeWriter = null; if(byteMatrix != null) byteMatrix = null;
					    if(CrunchifyWidth != 0) CrunchifyWidth = 0; if(bSixty4!= null) bSixty4 = null; if(mbSixty4File!= null) mbSixty4File = null; if(imageInFile != null) imageInFile.close(); if(gson != null) gson = null;
					    
					    
					    
					}
					
//					merchDqrin = new FileInputStream(mFile);
//					merchDqrout = response.getOutputStream();
//				    response.setContentType("image/png");
//				    response.setContentLength((int)mFile.length());
//				    response.setHeader("Content-Disposition", "inline; filename=\"" + mFile.getName() + "\"");
//				    // Copy the contents of the file to the output stream
//				    byte[] buf = new byte[1024];
//				    int count = 0;
//				    while ((count = merchDqrin.read(buf)) >= 0) {
//				    	   merchDqrout.write(buf, 0, count);
//				    }
					
			//	}//end of if merch&coa&allow
				
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_ACCEPT_PAYMENT "+e.getMessage());
			}
	
		break;
		
		
		
		case Rules.JSON_MERCHANT_MOBILE_CASHOUT_PAYMENT:
			try {
				String userId = null;	String privateKey = null;	boolean allow = true; 
				PrintWriter mJsonOutput_1 = null; String billerCode = null;
				String tnxType = "WCO"; String txnCurrencyId = "404"; String cashoutAmount = null;

				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
				if(jsonObj.get("amount")!=null) cashoutAmount = jsonObj.get("amount").toString().replaceAll("\"", "");
				
				PPWalletEnvironment.setComment(3, className, "userid from mobile is  "+userId + privateKey + cashoutAmount);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_CASHOUT_PAYMENT: pvt key is incorrect "+privateKey);
				}
				
				
				
				FileInputStream merchDqrin = null;
				OutputStream merchDqrout = null;
				File mFile = null;
				
				SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmm");			
				Date dateobj = new Date();
				
				PPWalletEnvironment.setComment(3,className,"inside cashout   "+ df.format(dateobj) );
				String filePath = PPWalletEnvironment.getFileUploadPath()+"/"+"QR.png";				
				String city = null; String merchId = null; String companyName = null;   
				Merchant mMerch = null; ChartOfAccounts coa = null; String coaVal = "1001"; String coaCode = null;
				
				//mMerch=(Merchant)MerchantDao.class.getConstructor().newInstance().getMerchantDetails(billerCode);
				//coa=(ChartOfAccounts)MerchPaymentsDao.class.getConstructor().newInstance().getChartOfAccounts(coaVal);
		
				//if(mMerch != null & coa != null  & allow) {
					
					//billerCode = mMerch.getBillerCode();
					//city = mMerch.getCity();
					//merchId = mMerch.getMerchantId();
					//companyName = mMerch.getCompanyName();
					//coaCode = coa.getCode();
					
					int size = 250;
					String fileType = "png";
					String myCodeText = null;
					mFile = new File(filePath);
					String currentDate = (df.format(dateobj));
					
			 		myCodeText = "D"+"|"+tnxType+"|"+billerCode+"|"+txnCurrencyId+"|"+cashoutAmount+"|"+Utilities.getMYSQLCurrentTimeStampForInsert() ;

					//myCodeText = billerCode+"|"+city+"|"+merchId+"|"+companyName+"|"+Utilities.encryptString(currentDate) +"|" +coaCode +"|" + amount ;
					
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
					ImageIO.write(image, fileType, mFile);
					
					/*convert image to base64 and send it to mobile endpoint */ 
					String bSixty4 = null; 
					File mbSixty4File = new File(filePath);
					FileInputStream imageInFile = new FileInputStream(mbSixty4File);
					byte imageData[] = new byte[(int) mFile.length()];
					try {
						
					    imageInFile.read(imageData);
					    bSixty4 = Base64.getEncoder().encodeToString(imageData);
						PPWalletEnvironment.setComment(3,className,"the qr "+ bSixty4);
						
						mJsonOutput_1 = response.getWriter();
						Gson gson = new Gson();
						JsonObject obj = new JsonObject(); //Json Object
						obj.add("error", gson.toJsonTree("false"));
						obj.add("qr", gson.toJsonTree(bSixty4));
						
						PPWalletEnvironment.setComment(3, className, " JSON JSON_mobile cashoutqr String is "+gson.toJson(obj));
						mJsonOutput_1.print(gson.toJson(obj));
					}catch(Exception e) {
						PPWalletEnvironment.setComment(1,className,"exception on cashoutqr"
								+ " when sending to mobile "+ e.getMessage());
					}finally {
						if(merchDqrout!=null)merchDqrout.close(); if(mMerch!=null)mMerch=null; if(coa!=null) coa = null;
					    if(merchDqrin!=null)	merchDqrin.close(); if(filePath!=null) filePath = null; if(billerCode!= null) billerCode=null;
					    if(mFile.exists())	mFile.delete();	if(city!= null)city=null; if(merchId!= null)merchId=null;if(companyName != null) companyName = null;
					    if(coaVal!= null) coaVal = null; if(coaCode != null) coaCode = null; if(hintMap!=null) hintMap=null;
					    if(mJsonOutput_1!=null) mJsonOutput_1.close();
					    if(imageInFile!= null) imageInFile.close();
					    if(bSixty4 != null) bSixty4=null;
					    if(mbSixty4File!= null) mbSixty4File.delete();
					    if(userId!= null) userId=null;
					    if(privateKey!= null) privateKey=null;
					    if(cashoutAmount!= null) cashoutAmount=null;
					    
					}
					
//					merchDqrin = new FileInputStream(mFile);
//					merchDqrout = response.getOutputStream();
//				    response.setContentType("image/png");
//				    response.setContentLength((int)mFile.length());
//				    response.setHeader("Content-Disposition", "inline; filename=\"" + mFile.getName() + "\"");
//				    // Copy the contents of the file to the output stream
//				    byte[] buf = new byte[1024];
//				    int count = 0;
//				    while ((count = merchDqrin.read(buf)) >= 0) {
//				    	   merchDqrout.write(buf, 0, count);
//				    }
					
				//}//end of if merch&coa&allow
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_CASHOUT_PAYMENT "+e.getMessage());			
			}
			
			
			break;
			
			/*================Start Top Up Mobile Module================*/
			case Rules.JSON_MERCHANT_MOBILE_TOPUP_PAYMENT:
			
			try {
				String userId = null;	String privateKey = null;	boolean allow = true; 
				PrintWriter mJsonOutput_1 = null; String billerCode = null;
				String tnxType = "WTM"; String txnCurrencyId = "404"; String topupAmount = null;

				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
				if(jsonObj.get("amount")!=null) topupAmount = jsonObj.get("amount").toString().replaceAll("\"", "");
				
				PPWalletEnvironment.setComment(3, className, "userid from mobile is  "+userId + privateKey + topupAmount);
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect "+privateKey);
				}
				
				FileInputStream merchDqrin = null;
				OutputStream merchDqrout = null;
				File mFile = null;
				
				SimpleDateFormat df = new SimpleDateFormat("ddMMyyHHmm");			
				Date dateobj = new Date();
				
				PPWalletEnvironment.setComment(3,className,"inside Top up   "+ df.format(dateobj) );
				String filePath = PPWalletEnvironment.getFileUploadPath()+"/"+"QR.png";				
				String city = null; String merchId = null; String companyName = null;  
				Merchant mMerch = null;  String coaCode = null;
				
				//mMerch=(Merchant)MerchantDao.class.getConstructor().newInstance().getMerchantDetails(billerCode);
				//coa=(ChartOfAccounts)MerchPaymentsDao.class.getConstructor().newInstance().getChartOfAccounts(coaVal);
			
				//if(mMerch != null ) {
					
					//billerCode = mMerch.getBillerCode();
					//city = mMerch.getCity();
					//merchId = mMerch.getMerchantId();
					//companyName = mMerch.getCompanyName();
					//coaCode = coa.getCode();
					
					int size = 250;
					String fileType = "png";
					String myCodeText = null;
					mFile = new File(filePath);
					String currentDate = (df.format(dateobj));
					
			 		myCodeText = "D"+"|"+tnxType+"|"+billerCode+"|"+txnCurrencyId+"|"+topupAmount+"|"+Utilities.getMYSQLCurrentTimeStampForInsert() ;

					//myCodeText = billerCode+"|"+city+"|"+merchId+"|"+companyName+"|"+Utilities.encryptString(currentDate) +"|" +coaCode + "|" + amount ;
					
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
					ImageIO.write(image, fileType, mFile);
					
					/*convert image to base64 and send it to mobile endpoint */ 
					String bSixty4 = null; 
					File mbSixty4File = new File(filePath);
					FileInputStream imageInFile = new FileInputStream(mbSixty4File);
					try {
						
						byte imageData[] = new byte[(int) mFile.length()];
					    imageInFile.read(imageData);
					    bSixty4 = Base64.getEncoder().encodeToString(imageData);
						PPWalletEnvironment.setComment(3,className,"the qr "+ bSixty4);
						
						mJsonOutput_1 = response.getWriter();
						Gson gson = new Gson();
						JsonObject obj = new JsonObject(); //Json Object
						obj.add("error", gson.toJsonTree("false"));
						obj.add("qr", gson.toJsonTree(bSixty4));
						
						PPWalletEnvironment.setComment(3, className, " JSON JSON_mobile top upqr  String is "+gson.toJson(obj));
						mJsonOutput_1.print(gson.toJson(obj));
					}catch(Exception e) {
						PPWalletEnvironment.setComment(1,className,"exception on top up when sending to mobile "+ e.getMessage());
					}finally {
						if(merchDqrout!=null) merchDqrout.close(); if(mMerch!=null)mMerch=null; 
					    if(merchDqrin!=null)	merchDqrin.close();  if(filePath!=null) filePath = null; if(billerCode!= null) billerCode=null;
					    if(mFile.exists())	mFile.delete(); if(city!= null)city=null; if(merchId!= null)merchId=null;if(companyName != null) companyName = null;
					    if(mJsonOutput_1!=null) mJsonOutput_1.close();  if(coaCode != null) coaCode = null; if(hintMap!=null) hintMap=null;
					    if(imageInFile!= null) imageInFile.close();
					    if(bSixty4 != null) bSixty4=null;
					    if(mbSixty4File!= null) mbSixty4File.delete();
					    if(userId!= null) userId=null;
					    if(privateKey!= null) privateKey=null;
					    if(topupAmount!= null) topupAmount=null;
					}
					
//					merchDqrin = new FileInputStream(mFile);
//					merchDqrout = response.getOutputStream();
//				    response.setContentType("image/png");
//				    response.setContentLength((int)mFile.length());
//				    response.setHeader("Content-Disposition", "inline; filename=\"" + mFile.getName() + "\"");
//				    // Copy the contents of the file to the output stream
//				    byte[] buf = new byte[1024];
//				    int count = 0;
//				    while ((count = merchDqrin.read(buf)) >= 0) {
//				    	   merchDqrout.write(buf, 0, count);
//				    }
					
				//}//end of if merch&coa&allow
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from .JSON_MERCHANT_MOBILE_TOPUP_PAYMENT "+e.getMessage());
					
		}
						
			break;
			
			case Rules.JSON_MERCHANT_MOBILE_BILLER_DETAILS:
				PrintWriter mJsonOutput_1 = null;
				try {
					//PPWalletEnvironment.setComment(2, className, "triggered from mobile biller details ");
					String userId = null;	String privateKey = null;	boolean allow = true; Merchant mMerch = null; String billerCode = null;
					
					ArrayList<MerchClientDetails> mobileBillerDets = null;
					if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");

					
					PPWalletEnvironment.setComment(3, className, "userid from mobile is  "+userId +"|"+privateKey );
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_OPS_LOGIN_VALIDATE: pvt key is incorrect "+privateKey);
					}
					
					//mMerch=(Merchant)MerchantDao.class.getConstructor().newInstance().getMerchantDetails(userId);
					
					Gson gson = new Gson();
					JsonObject jObject = new JsonObject();
					mJsonOutput_1 = response.getWriter();
					//if(mMerch != null) {
					//bCode = mMerch.getBillerCode();
				 	//PPWalletEnvironment.setComment(3,className,"biller code is:"+ billerCode );
				
							
				 	mobileBillerDets = (ArrayList<MerchClientDetails>)MerchBillPaymentDao.class.getConstructor().newInstance().getMerchBillerDetails(billerCode); 
				 		
				 		if (mobileBillerDets != null) {
							int count = mobileBillerDets.size();
							String[] nameArray = new String [count];
							String[] addressArray = new String[count];
							String[] contactArray = new String[count];
							String[] emailArray = new String [count];
							
							
							
							for(int i  = 0; i < mobileBillerDets.size();i++ ) {
								nameArray[i] =((MerchClientDetails)mobileBillerDets.get(i)).getCustName();
								addressArray[i] =((MerchClientDetails)mobileBillerDets.get(i)).getaddress();
								contactArray[i] =((MerchClientDetails)mobileBillerDets.get(i)).getContact();
								emailArray[i] =((MerchClientDetails)mobileBillerDets.get(i)).getEmail();
								
								
							}
							
							jObject.add("merchname", gson.toJsonTree(nameArray));
							jObject.add("merchaddress", gson.toJsonTree(addressArray));
							jObject.add("merchcontact", gson.toJsonTree(contactArray));
							jObject.add("merchemail", gson.toJsonTree(emailArray));
							jObject.add("error", gson.toJsonTree("false"));

							
						}else {
							jObject.add("error", gson.toJsonTree("true"));
						}
				 		
				 		PPWalletEnvironment.setComment(3, className, " Json String "+gson.toJsonTree(jObject));
				 		mJsonOutput_1.print(gson.toJson(jObject));
					//}
					
					
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_ BILLER_DETAILS "+e.getMessage());
				}
				finally {
					if( mJsonOutput_1!=null)		mJsonOutput_1.close();
//					if(nameArray!=null )
				}
				
				
			break;
			
			
			
			case Rules.JSON_MERCHANT_MOBILE_BILLPAY_TXN_DETAILS:
				
				try {
					PrintWriter myJsonOutput_1 = null;
					String userId = null;	String privateKey = null;	
					boolean allow = true; String dateFrom = null; String dateTo = null;
					Merchant mMerch = null; String billerCode = null;
					
					ArrayList<MerchBillPaymentTransactions> arrTransactions = null;
					
					if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					if(jsonObj.get("startdate")!=null) dateFrom = jsonObj.get("startdate").toString().replaceAll("\"", "");
					if(jsonObj.get("enddate")!=null) dateTo = jsonObj.get("enddate").toString().replaceAll("\"", "");
					if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
					
					PPWalletEnvironment.setComment(3, className, "changed datest dateFrom"+dateFrom +" and dateTo:" +dateTo );
					PPWalletEnvironment.setComment(3, className, "privateKey "+privateKey  );

					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_TRANSACTION_DETAILS private key is "+privateKey);
					}
					mJsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject jObj = new JsonObject(); //Json Object
					
					// date conversion 
					 String startDate = formartDate(dateFrom);
					 String endDate = formartDate(dateTo);
					
					mMerch=(Merchant)MerchantDao.class.getConstructor().newInstance().getMerchantDetails(billerCode);
				
					if(mMerch != null  & allow==true) {
						//billerCode = mMerch.getBillerCode();
				 		PPWalletEnvironment.setComment(3,className,"*** inside allow "+allow+" biller code is     "+ billerCode );
						
							jObj.add("merchaddress", gson.toJsonTree( mMerch.getAddress1()  ));
							jObj.add("merchcontact", gson.toJsonTree( mMerch.getContact()  ));
							jObj.add("merchname", gson.toJsonTree(mMerch.getMerchantName()    ));
							jObj.add("error", gson.toJsonTree("false"));
							jObj.add("message", gson.toJsonTree("Succesfully Fetched Biller details"));

					}else {
						jObj.add("message", gson.toJsonTree("No Biller details"));
				 		PPWalletEnvironment.setComment(3,className," MerchDetails is empty " );
						
					}
					arrTransactions= (ArrayList<MerchBillPaymentTransactions>)MerchBillPaymentDao.class.getConstructor().newInstance().getTransactions(billerCode,startDate,endDate); 				
					if(arrTransactions != null) {
						int count = arrTransactions.size();
						String[] txnCodeArray = new String [count];	
						String[] txnAmountArray = new String [count];
						String[] txnDateTimeArray = new String [count];
						
		
						for(int i = 0;i<arrTransactions.size();i++) {
							txnCodeArray[i]= ((MerchBillPaymentTransactions)arrTransactions.get(i)).getTransactionCode();
							txnAmountArray[i]= ((MerchBillPaymentTransactions)arrTransactions.get(i)).getTransAmount();
							txnDateTimeArray[i]= Utilities.getMySQLDateTimeConvertor(((MerchBillPaymentTransactions)arrTransactions.get(i)).getDateTime());
								   
						}
						
						jObj.add("transcode", gson.toJsonTree(txnCodeArray));
						jObj.add("transamount", gson.toJsonTree(txnAmountArray));
						jObj.add("transdate", gson.toJsonTree(txnDateTimeArray));
						jObj.add("error", gson.toJsonTree("false"));
				 		PPWalletEnvironment.setComment(3,className," No error " );
				 		

						PPWalletEnvironment.setComment(3, className, " JSON JSON_mobile top upqr  String is "+gson.toJson(jObj));
					} else {
						jObj.add("error", gson.toJsonTree("true"));
						jObj.add("message", gson.toJsonTree("No Transactions"));
				 		PPWalletEnvironment.setComment(3,className," mTransactions is empty " );
					}
					
					try {
						mJsonOutput_1.print(gson.toJson(jObj));
					} finally {
						if(mJsonOutput_1!=null) mJsonOutput_1.close(); if(userId!=null) userId=null;
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;if( myJsonOutput_1!=null) myJsonOutput_1.close();
					}		

				}catch(Exception e){
					PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_TRANSACTION_DETAILS "+e.getMessage());
				}finally {
					
				}
				
			break;
				
			
			case JSON_MERCHANT_MOBILE_MCC_GROUP:
				PrintWriter myMccJsonOutput = null;

				try {
					String privateKey = null;
					boolean allow = true;
//					MccGroup myMccGroup;
					ArrayList<MccGroup> mccGroupArraylist = null;

					
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");

					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_TRANSACTION_DETAILS private key is "+privateKey);
					}
					
//					myMccGroup =  (MccGroup)MerchMccGroupDao.class.getConstructor().newInstance().getMccGroups();
					
					mccGroupArraylist =  (ArrayList<MccGroup>)MerchMccGroupDao.class.getConstructor().newInstance().getMccGroups();
					
					if(mccGroupArraylist != null && allow ) {
						int count = mccGroupArraylist.size();
						String[]mccIdArr = new String [count];
						String[]mccCatNameArr = new String [count];
						
						for(int i = 0;i<mccGroupArraylist.size();i++) {
							mccIdArr[i]=((MccGroup)mccGroupArraylist.get(i)).getMccCategoryId();
							mccCatNameArr[i]=((MccGroup)mccGroupArraylist.get(i)).getMccCategoryName();
						}
						
						myMccJsonOutput = response.getWriter();
						Gson gson = new Gson();
						JsonObject jObj = new JsonObject(); //Json Object
						jObj.add("mccId", gson.toJsonTree(mccIdArr));
						jObj.add("mccCatName", gson.toJsonTree(mccCatNameArr));
						jObj.add("error", gson.toJsonTree("false"));
						
						PPWalletEnvironment.setComment(3, className, " JSON JSON_mobile top upqr  String is "+gson.toJson(jObj));
						myMccJsonOutput.print(gson.toJson(jObj));
					} else {
				 		PPWalletEnvironment.setComment(3,className," mccGroupArraylist is empty " );
					}
					
				} catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error in JSON_MERCHANT_MOBILE_MCC_GROUP "+e.getMessage());
				}finally {
					if( myMccJsonOutput!=null) myMccJsonOutput.close();
				}
				
			 break; 
			 
			 
			case Rules.JSON_MERCHANT_MOBILE_SELF_REGISTRATION:
				try {
					PPWalletEnvironment.setComment(3, className, "got to self registration");

					String userId = null; String userPwd = null; String userName = null; String userEmail = null; String userContact = null;
					String address1 = null; String address2 = null; String postCode = null;	String companyName = null; String registrationNo = null;
					String mccCode = null;	String billerCode = null; String nationalId= null; String city= null; boolean success = false;
					String fileOne = null; String fileTwo = null; String fileThree = null;String privateKey = null; boolean allow = true; String mccGrp = null; 
					ArrayList<String> arrMerchFile = new ArrayList<String>(); 				PrintWriter jsonOutput_1 = null;

//					int i = 1; FileItem item =null;*/
					
					
					if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
					if(jsonObj.get("name")!=null) userName = jsonObj.get("name").toString().replaceAll("\"", "");
					if(jsonObj.get("natid")!=null) nationalId = jsonObj.get("natid").toString().replaceAll("\"", "");
					if(jsonObj.get("pswrd")!=null) userPwd = jsonObj.get("pswrd").toString().replaceAll("\"", "");
					if(jsonObj.get("mobile")!=null) userContact = jsonObj.get("mobile").toString().replaceAll("\"", "");
					if(jsonObj.get("email")!=null) userEmail = jsonObj.get("email").toString().replaceAll("\"", "");
					if(jsonObj.get("city")!=null) city = jsonObj.get("city").toString().replaceAll("\"", "");
					if(jsonObj.get("address1")!=null) address1 = jsonObj.get("address1").toString().replaceAll("\"", "");
					if(jsonObj.get("address2")!=null) address2 = jsonObj.get("address2").toString().replaceAll("\"", "");
					if(jsonObj.get("box")!=null) postCode = jsonObj.get("box").toString().replaceAll("\"", "");
					if(jsonObj.get("compName")!=null) companyName = jsonObj.get("compName").toString().replaceAll("\"", "");
					if(jsonObj.get("regno")!=null) registrationNo = jsonObj.get("regno").toString().replaceAll("\"", "");
					if(jsonObj.get("firstDoc")!=null) fileOne = jsonObj.get("firstDoc").toString().replaceAll("\"", "");
					if(jsonObj.get("secondDoc")!=null) fileTwo = jsonObj.get("secondDoc").toString().replaceAll("\"", "");
					if(jsonObj.get("thirdDoc")!=null) fileThree = jsonObj.get("thirdDoc").toString().replaceAll("\"", "");
					if(jsonObj.get("mccgrp")!=null) mccGrp = jsonObj.get("mccgrp").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					billerCode = Utilities.generateCVV2(12);				                    	

					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						allow = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_SELF_REG private key is "+privateKey);
					}
					jsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Json Object
					
					String imageArray[] = new String [3]; // 3 images

					for(int i=0;i<imageArray.length ;i++) {
						imageArray[i]=  fileOne;
						imageArray[i]=  fileTwo;
						imageArray[i]=  fileThree;
						PPWalletEnvironment.setComment(3, className, "ImageArray size is  "+ imageArray.length);

					}

					ArrayList <String> arrayImages = new ArrayList<String>();
					for(int i=0;i<imageArray.length;i++) {
						byte[] imageBytes = null;	BufferedImage	imgRaw = null; File uploadFile = null;
						try {
						//imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageArray[i].substring(imageArray[i].indexOf(",")+1));
						imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageArray[i]);
						imgRaw = ImageIO.read(new ByteArrayInputStream(imageBytes));
						
						//tempFile = new File((PPWalletEnvironment.getBioImagePath()+"/"+userId+"-rawimage"+i+".jpg"))	;
						uploadFile = new File((PPWalletEnvironment.getFileUploadPath()+"/"+userId+"-rawimage"+i+".jpg"));
						
						
						
						PPWalletEnvironment.setComment(3, className, "uploadFile for user "+ userId + "is" + uploadFile);
						arrMerchFile.add(uploadFile.getAbsolutePath());
						ImageIO.write(imgRaw, "png", uploadFile);
						
						//arrayImages.add(PPWalletEnvironment.getBioImagePath()+"/"+userId+"-rawimage"+i+".jpg");
						arrayImages.add((PPWalletEnvironment.getFileUploadPath()+"/"+userId+"-rawimage"+i+".jpg"))	;

						}catch(Exception e) {
							throw new Exception ("Problem in writing images :"+e.getMessage());
						}finally {
							if(imgRaw!=null) imgRaw = null;
							if(imageBytes!=null)	imageBytes = null;
						}
					}
									
						if(arrayImages!=null)
							if(arrayImages.size()==0)
								arrayImages=null;
					
		    		    success = (boolean)MerchMobileDao.class.getConstructor().newInstance().registerMerchant(nationalId, userId, userPwd, userName, userEmail, userContact, 
		    		    		address1, address2, postCode, companyName, registrationNo, mccCode, billerCode, city, arrMerchFile);
						if(success) {
							 //consult audit trail on merchant self registration
							String moduleCode = "M"; //M = Merchants Acquiring
							SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, "M", moduleCode, StringUtils.substring("Merchant self registered "+userId, 0, 48) );  
							obj.add("error", gson.toJsonTree("false"));
							obj.add("message", gson.toJsonTree("Congratulation you have successfully registered"));


						}else {
							if(success==false)
								throw new Exception ("merchant registration failed");
							obj.add("error", gson.toJsonTree("true"));
							obj.add("message", gson.toJsonTree("Registration Failed"));

						}
						try {
							PPWalletEnvironment.setComment(3, className, " JSON_MERCHANT_MOBILE_EDIT_PERSONAL_PRF String is "+gson.toJson(obj));
							jsonOutput_1.print(gson.toJson(obj));
						} finally {
							
							if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userPwd!=null) userPwd=null; if(city!=null) city =null;
							if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(billerCode!=null) billerCode =null; if(nationalId!=null) nationalId =null;
							if(userEmail!=null) userEmail =null; if(userContact!=null) userContact =null; if(address1!=null) address1 =null; if(address2!=null) address2 =null;
							if(postCode!=null) postCode =null; if(companyName!=null) companyName =null; if(registrationNo!=null) registrationNo =null; if(mccCode!=null) mccCode =null;
							if(fileOne!=null) fileOne =null; if(fileTwo!=null) fileTwo =null; if(fileThree!=null) fileThree =null; if(mccGrp!=null) mccGrp =null; if(arrMerchFile!=null) arrMerchFile =null;
							if(userName!=null) userName =null;
						}
							
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error in JSON_MERCHANT_MOBILE_SELF_REG "+e.getMessage());
		
		          }
				break;

	
	   }
		
	}
	
	private String formartDate(String mDate) {
		// TODO Auto-generated method stub
		SimpleDateFormat inSDF = new SimpleDateFormat("mm/dd/yyyy");
		  SimpleDateFormat outSDF = new SimpleDateFormat("yyyy-mm-dd");

		 
		  String outDate = "";
		  
		    if (mDate != null) {
		        try {
		            Date date = inSDF.parse(mDate);
		            outDate = outSDF.format(date);
		            
		            
		        } catch (Exception  ex){ 
		        	ex.printStackTrace();
		        }
		    }
		    return outDate;

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
		// TODO Auto-generated method stub
		
	}

}

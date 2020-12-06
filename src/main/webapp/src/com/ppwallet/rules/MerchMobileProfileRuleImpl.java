package com.ppwallet.rules;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.MerchBillPaymentDao;
import com.ppwallet.dao.MerchantDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.model.MerchClientDetails;
import com.ppwallet.model.Merchant;
import com.ppwallet.model.User;

public class MerchMobileProfileRuleImpl implements Rules {
	private static String className = MerchMobileRuleImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void performJSONOperation(String rulesaction, HttpServletRequest request, HttpServletResponse response,
			ServletContext context, JsonObject jsonObj) throws Exception {
		switch (rulesaction){
		case Rules.JSON_MERCHANT_MOBILE_VIEW_MERCHANT_PRF:
		
			try {
				
				Merchant  merchDtls = null; String userId = null; String privateKey = null; boolean allow = true; String userType = null;
				PrintWriter jsonOutput_1 = null;
				
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_VIEW_MERCHANT_PRF: pvt key is incorrect "+privateKey);
				}
				
				merchDtls=(Merchant)MerchantDao.class.getConstructor().newInstance().getMobileMerchantProfile(userId);
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				if(merchDtls!=null && allow) {
					obj.add("billercode", gson.toJsonTree( merchDtls.getBillerCode()  ));
					obj.add("userid", gson.toJsonTree( merchDtls.getMerchantId()   ));
					obj.add("username", gson.toJsonTree(merchDtls.getMerchantName()    ));
					obj.add("usercontact", gson.toJsonTree(merchDtls.getContact()  ));
					obj.add("usernationalid", gson.toJsonTree(merchDtls.getNationalId() ));
					obj.add("userstatus", gson.toJsonTree(merchDtls.getStatus()));
					
					obj.add("uemail", gson.toJsonTree(merchDtls.getEmail()));
					obj.add("ucity", gson.toJsonTree(merchDtls.getCity()));
					obj.add("address1", gson.toJsonTree(merchDtls.getAddress1()));
					obj.add("address2", gson.toJsonTree(merchDtls.getAdress2()));
					obj.add("postalcode", gson.toJsonTree(merchDtls.getPinCode()));
					
					obj.add("companyname", gson.toJsonTree(merchDtls.getCompanyName()));
					obj.add("merchcategory", gson.toJsonTree(merchDtls.getMccCategoryId() ));
					obj.add("companyregno", gson.toJsonTree(merchDtls.getCompanyRegistration()));
					
								if (!merchDtls.getDocumentArray().isEmpty()) {
									int count = merchDtls.getDocumentArray().size();
									String[] kycLocation = new String [count];
									PPWalletEnvironment.setComment(3, className, " arrkycDocs " + merchDtls.getDocumentArray().size()   );
			
									for(int i  = 0; i < merchDtls.getDocumentArray().size();i++ ) {
										kycLocation[i] = merchDtls.getDocumentArray().get(i);
										//kycLocation[i] =((MerchClientDetails)mobileBillerDets.get(i)).getCustName();
										
									}
									obj.add("kyclocation", gson.toJsonTree(kycLocation));	
			
								}
				    PPWalletEnvironment.setComment(3, className, " there is no error in ");			
					 obj.add("error", gson.toJsonTree("false"));
					
			
				}else{
					obj.add("error", gson.toJsonTree("true"));
				  }
				
				try {
					//PPWalletEnvironment.setComment(3, className, " JSON JSON_CUST_VIEW_PROFILE String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userType!=null) userType=null;
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null;
				}
				
				
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_VIEW_MERCHANT_PRF "+e.getMessage());
			}
		
		break;
		
		case Rules.JSON_MERCHANT_MOBILE_EDIT_PERSONAL_PRF:
			
			try {
				
				String userId = null; String privateKey = null; String userType = null;
				PrintWriter jsonOutput_1 = null; boolean result = false; String billerCode = null;  String merchantName =null;
				String merchContact = null; String merchPwd = null;
				
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
				
				if(jsonObj.get("username")!=null) merchantName = jsonObj.get("username").toString().replaceAll("\"", "");
				if(jsonObj.get("usercontact")!=null) merchContact = jsonObj.get("usercontact").toString().replaceAll("\"", "");
				if(jsonObj.get("password")!=null) merchPwd = jsonObj.get("password").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					result = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_EDIT_PERSONAL_PRF: pvt key is incorrect "+privateKey);
				}else {
					
					result= (boolean)MerchantDao.class.getConstructor().newInstance().MerchantUpdatePersonalProfile(userId, userType,billerCode, merchantName,merchContact, merchPwd );	
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				if(result) {
					PPWalletEnvironment.setComment(3, className, " result ");

					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, "M", StringUtils.substring("Merchant updated their profile "+userId, 0, 48) );

					obj.add("error", gson.toJsonTree("false"));
					PPWalletEnvironment.setComment(3, className, " No error ");

				}else {

					obj.add("error", gson.toJsonTree("true"));
					PPWalletEnvironment.setComment(3, className, " there is error ");

				}
				try {
					PPWalletEnvironment.setComment(3, className, " JSON_MERCHANT_MOBILE_EDIT_PERSONAL_PRF String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userType!=null) userType=null;
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(billerCode!=null) billerCode =null;
					if(merchantName!=null) merchantName=null;	if(merchContact!=null) merchContact=null; 	
				}
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_EDIT_PERSONAL_PRF "+e.getMessage());
			}
		break;
		
		
		case Rules.JSON_MERCHANT_MOBILE_EDIT_CONTACT_PRF:
			try {			
				
				String userId = null; String privateKey = null; String userType = null;
				PrintWriter jsonOutput_1 = null; boolean result = false; String billerCode = null;  String merchEmail =null; String merchCity=null;
				String address1=null; String address2 = null; String postalCode=null;
				
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
				
				if(jsonObj.get("useremail")!=null) merchEmail = jsonObj.get("useremail").toString().replaceAll("\"", "");
				if(jsonObj.get("usercity")!=null) merchCity = jsonObj.get("usercity").toString().replaceAll("\"", "");
				if(jsonObj.get("useraddress1")!=null) address1 = jsonObj.get("useraddress1").toString().replaceAll("\"", "");
				if(jsonObj.get("useraddress2")!=null) address2 = jsonObj.get("useraddress2").toString().replaceAll("\"", "");
				if(jsonObj.get("userpostalcode")!=null) postalCode = jsonObj.get("userpostalcode").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					result = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_EDIT_CONTACT_PRF: pvt key is incorrect "+privateKey);
				}else {
					
					result= (boolean)MerchantDao.class.getConstructor().newInstance().MerchantUpdateContactProfile(userId, userType,billerCode, merchEmail,merchCity,address1,address2,postalCode);	
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				if(result) {
					PPWalletEnvironment.setComment(3, className, " result ");

					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, "M", StringUtils.substring("Merchant updated their contact profile "+userId, 0, 48) );

					obj.add("error", gson.toJsonTree("false"));
					PPWalletEnvironment.setComment(3, className, " No error ");

				}else {

					obj.add("error", gson.toJsonTree("true"));
					PPWalletEnvironment.setComment(3, className, " there is error ");

				}
				try {
					PPWalletEnvironment.setComment(3, className, " JSON_MERCHANT_MOBILE_EDIT_CONTACT_PRF String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userType!=null) userType=null;
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(billerCode!=null) billerCode =null;
					if(merchEmail!=null) merchEmail=null;	if(merchCity!=null) merchCity=null; 	
					if(address1!=null) address1=null;   if(address2!=null) address2=null; 
					if(postalCode!=null) postalCode=null; 
				}
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_EDIT_CONTACT_PRF "+e.getMessage());
			}
			break;
			
			
		case Rules.JSON_MERCHANT_MOBILE_EDIT_COMPANY_PRF:
			try {	
					
				String userId = null; String privateKey = null; String userType = null;
				PrintWriter jsonOutput_1 = null; boolean result = false; String billerCode = null;  String companyName =null; String mccCategory=null;
				String compRegNo=null;
				
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
				
				if(jsonObj.get("companyname")!=null) companyName = jsonObj.get("companyname").toString().replaceAll("\"", "");
				if(jsonObj.get("mcccategory")!=null) mccCategory = jsonObj.get("mcccategory").toString().replaceAll("\"", "");
				if(jsonObj.get("compregnumber")!=null) compRegNo = jsonObj.get("compregnumber").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					result = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_EDIT_COMPANY_PRF: pvt key is incorrect "+privateKey);
				}else {
					
					result= (boolean)MerchantDao.class.getConstructor().newInstance().MerchantUpdateCompanyProfile(userId, userType,billerCode, companyName,mccCategory,compRegNo);	
				}
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				if(result) {
					PPWalletEnvironment.setComment(3, className, " result ");

					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, "M", StringUtils.substring("Merchant updated their contact profile "+userId, 0, 48) );

					obj.add("error", gson.toJsonTree("false"));
					PPWalletEnvironment.setComment(3, className, " No error ");

				}else {

					obj.add("error", gson.toJsonTree("true"));
					PPWalletEnvironment.setComment(3, className, " there is error ");

				}
				try {
					PPWalletEnvironment.setComment(3, className, " JSON_MERCHANT_MOBILE_EDIT_COMPANY_PRF String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userType!=null) userType=null;
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(billerCode!=null) billerCode =null;
					if(companyName!=null) companyName=null;	if(mccCategory!=null) mccCategory=null; 	
					if(compRegNo!=null) compRegNo=null;  
				}
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_EDIT_COMPANY_PRF "+e.getMessage());
			}
			break;
		case Rules.JSON_MERCHANT_MOBILE_DOWNLOAD_KYC_DOCS:
		  
		 try {	
			 
			 String userId = null; String privateKey = null; String userType = null;
				PrintWriter jsonOutput_1 = null; boolean result = false; String billerCode = null;  String companyName =null; String mccCategory=null;
				String compRegNo=null; 	String fileName = null;  
				
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				if(jsonObj.get("billercode")!=null) billerCode = jsonObj.get("billercode").toString().replaceAll("\"", "");
				if(jsonObj.get("fullPath")!=null) fileName = jsonObj.get("fullPath").toString().replaceAll("\"", "");
				
				PPWalletEnvironment.setComment(1, className, "Filepath is:  "+fileName);
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					result = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_EDIT_COMPANY_PRF: pvt key is incorrect "+privateKey);
				}
					
				jsonOutput_1 = response.getWriter();
				  String filePath = PPWalletEnvironment.getFileDownloadPath();
				  
				
				
				
				
				
				
				
			
				
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				if(result) {
					PPWalletEnvironment.setComment(3, className, " result ");

					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, "M", StringUtils.substring("Merchant updated their contact profile "+userId, 0, 48) );

					obj.add("error", gson.toJsonTree("false"));
					PPWalletEnvironment.setComment(3, className, " No error ");

				}else {

					obj.add("error", gson.toJsonTree("true"));
					PPWalletEnvironment.setComment(3, className, " there is error ");

				}
				try {
					PPWalletEnvironment.setComment(3, className, " JSON_MERCHANT_MOBILE_EDIT_COMPANY_PRF String is "+gson.toJson(obj));
					jsonOutput_1.print(gson.toJson(obj));
				} finally {
					if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userType!=null) userType=null;
					if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(billerCode!=null) billerCode =null;
					if(companyName!=null) companyName=null;	if(mccCategory!=null) mccCategory=null; 	
					if(compRegNo!=null) compRegNo=null;  
				}
			 
			 
			 
				
			}catch(Exception e) {
				PPWalletEnvironment.setComment(1, className, "Error from JSON_MERCHANT_MOBILE_DOWNLOAD_KYC_DOCS "+e.getMessage());
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

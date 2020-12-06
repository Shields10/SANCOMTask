package com.ppwallet.rules;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.CustomerDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.User;
import com.ppwallet.utilities.Utilities;

public class CustomerRulesImpl implements Rules {
	private static String className = CustomerRulesImpl.class.getSimpleName();
	

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(false);
				
		switch (rules){
		case Rules.PERSO_CUST_REGISTRATION:
			try{				
				String userId = null;  String userPwd = null; boolean success = false;
				String idNo = null; String passportNo = null; String dateOfBirth = null; String identityType = null;
				if(request.getParameter("userid")!=null)	userId = StringUtils.trim( request.getParameter("userid") );
				if(request.getParameter("password")!=null)	userPwd = StringUtils.trim( request.getParameter("password") );
				if(request.getParameter("passport")!=null)	passportNo = StringUtils.trim( request.getParameter("passport") );
				if(request.getParameter("idnumber")!=null)	idNo = StringUtils.trim( request.getParameter("idnumber") );
				if(request.getParameter("dob")!=null)	dateOfBirth = StringUtils.trim( request.getParameter("dob") );
				if(request.getParameter("hdnidpst")!=null)	identityType = StringUtils.trim( request.getParameter("hdnidpst") );
				
				success = (boolean)CustomerDao.class.getConstructor().newInstance().registerPersoCustomer(userId, userPwd, passportNo, userPwd, idNo, dateOfBirth);
	    		    
    		    //consult audit trail on merchant self registration
				String moduleCode = "C"; //M = Merchants Acquiring
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, "C", moduleCode, StringUtils.substring("Customer self registered "+userId, 0, 48) );
    		    
				if(success==false)
					throw new Exception ("Customer registration failed");
    		    response.setContentType("text/html");
    		    try {
				ctx.getRequestDispatcher(PPWalletEnvironment.getLoginPage()).forward(request, response);
    		    }finally {
    		    	if(userId!=null) userId = null; if(userPwd!=null) userPwd = null; if(passportNo!=null) passportNo = null; 
    		    	if(idNo!=null) idNo = null; if(dateOfBirth!=null) dateOfBirth = null; if(identityType!=null) identityType = null; 
    		    	
				}
				
			}catch(Exception e){
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
		case Rules.JSON_CUST_VIEW_PROFILE: 
			try {
				
				CustomerDetails  custDtls = null; String userId = null; String privateKey = null; boolean allow = true; String userType = null;
				PrintWriter jsonOutput_1 = null;
				if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
				if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
				if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
				
				if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
					allow = false;
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_VIEW_PROFILE: pvt key is incorrect "+privateKey);
				}
				
				
				custDtls=(CustomerDetails)CustomerDao.class.getConstructor().newInstance().getFullCustomerProfile(userId, userType);
				jsonOutput_1 = response.getWriter();
				Gson gson = new Gson();
				JsonObject obj = new JsonObject(); //Json Object
				
				if(custDtls!=null && allow) {
					obj.add("userid", gson.toJsonTree(custDtls.getCustomerId()));
					obj.add("username", gson.toJsonTree(custDtls.getCustomerName()));
					obj.add("useremail", gson.toJsonTree(custDtls.getEmail()));
					obj.add("usercontact", gson.toJsonTree(custDtls.getContact()));
					obj.add("useradress", gson.toJsonTree(custDtls.getAddress()));
					obj.add("userkrapin", gson.toJsonTree(custDtls.getKraPIN()));
					obj.add("userstatus", gson.toJsonTree(custDtls.getStatus()));
					obj.add("usertype", gson.toJsonTree(custDtls.getUserType()));
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
				PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_VIEW_PROFILE "+e.getMessage());
			}
			break;
			
			case Rules.JSON_CUST_UPDATE_PROFILE:
				try {
					String userId = null; String privateKey = null; String userType = null;
					PrintWriter jsonOutput_1 = null; 
					
					String custPwd = null; String custName = null; String custEmail = null; String custContact = null; String address = null;
					String postCode = null; boolean success = false;
					
					
					if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
					if(jsonObj.get("pvtkey")!=null) privateKey = jsonObj.get("pvtkey").toString().replaceAll("\"", "");
					if(jsonObj.get("usertype")!=null) userType = jsonObj.get("usertype").toString().replaceAll("\"", "");
					if(jsonObj.get("username")!=null) custName = jsonObj.get("username").toString().replaceAll("\"", ""); 
					if(jsonObj.get("useremail")!=null) custEmail = jsonObj.get("useremail").toString().replaceAll("\"", ""); 
					if(jsonObj.get("usercontact")!=null) custContact = jsonObj.get("usercontact").toString().replaceAll("\"", ""); 
					if(jsonObj.get("useraddress")!=null) address = jsonObj.get("useraddress").toString().replaceAll("\"", ""); 
					if(jsonObj.get("userpost")!=null) postCode = jsonObj.get("userpost").toString().replaceAll("\"", ""); 
					if(jsonObj.get("userpwd")!=null) custPwd = jsonObj.get("userpwd").toString().replaceAll("\"", ""); 
					
					if(!privateKey.equals(PPWalletEnvironment.getAPIKeyPrivate())) {
						success = false;
						PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_UPDATE_PROFILE: pvt key is incorrect "+privateKey);
					}else {
						success = (boolean)CustomerDao.class.getConstructor().newInstance().updateCustomerProfile(userId, custPwd, custName, custEmail, custContact, address, postCode );
					}
					jsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Json Object
					if(success) {
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, userType, "C", StringUtils.substring("Customer updated his profile "+userId, 0, 48) );

						obj.add("error", gson.toJsonTree("false"));
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					try {
						//PPWalletEnvironment.setComment(3, className, " JSON JSON_CUST_VIEW_PROFILE String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userType!=null) userType=null;
						if(privateKey!=null) privateKey=null;	if(gson!=null) gson=null; if(custPwd!=null) custPwd =null;
						if(custName!=null) custName=null;	if(custEmail!=null) custEmail=null; if(custContact!=null) custContact =null;
						if(address!=null) address=null;	if(postCode!=null) postCode=null; 
					}
					
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_UPDATE_PROFILE "+e.getMessage());
				}
				
				
				break;
				
//				JSON_CUST_REGISTRATION
			case Rules.JSON_CUST_REGISTRATION:
				
				try {
					
				
					
					String userId = null;	String userPwd = null;	String userType = null; 	String fullName = null; String gender = null; 
					String phoneNo = null;	String email = null;	String kraPin = null; 	String address = null; String dateOfBirth = null; 
					PrintWriter jsonOutput_1 = null; String nationalId = null; String fileOne = null; String fileTwo = null; String relationshipNo = null;
					ArrayList<String> arrFilePath = new ArrayList<String>(); ArrayList<String> imageArray = new ArrayList<String>();  boolean success = false; String passportNo = null;
					SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMdd"); int count = 0; 
					if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
					if(jsonObj.get("password")!=null) userPwd = jsonObj.get("password").toString().replaceAll("\"", "");
					if(jsonObj.get("fullname")!=null) fullName = jsonObj.get("fullname").toString().replaceAll("\"", "");
					if(jsonObj.get("gender")!=null) gender = jsonObj.get("gender").toString().replaceAll("\"", "");
					if(jsonObj.get("phoneno")!=null) phoneNo = jsonObj.get("phoneno").toString().replaceAll("\"", "");
					if(jsonObj.get("email")!=null) email = jsonObj.get("email").toString().replaceAll("\"", "");
					if(jsonObj.get("krapin")!=null) kraPin = jsonObj.get("krapin").toString().replaceAll("\"", "");
					if(jsonObj.get("address")!=null) address = jsonObj.get("address").toString().replaceAll("\"", "");
					if(jsonObj.get("dob")!=null) dateOfBirth = jsonObj.get("dob").toString().replaceAll("\"", "");
					if(jsonObj.get("nationaid")!=null) nationalId = jsonObj.get("nationaid").toString().replaceAll("\"", ""); //national id
					if(jsonObj.get("image_1")!=null) fileOne = jsonObj.get("image_1").toString().replaceAll("\"", ""); 
					if(jsonObj.get("image_2")!=null) fileTwo = jsonObj.get("image_2").toString().replaceAll("\"", ""); 
					if(jsonObj.get("passportno")!=null) passportNo = jsonObj.get("passportno").toString().replaceAll("\"", ""); 
					relationshipNo = formatter1.format(new java.util.Date())	+ RandomStringUtils.random(10, false, true);
					
					
					PPWalletEnvironment.setComment(3, className, "userId " + userId +" userPwd "+userPwd +" fullName "+fullName + " gender "+gender +" phoneNo "+phoneNo+" email "+email+
							" kraPin "+kraPin+" adress "+address+" dateOfBirth "+dateOfBirth +" nationalId "+nationalId +" passportNo "+passportNo);
					if(fileOne != null) {imageArray.add(fileOne);} if(fileTwo != null) {imageArray.add(fileTwo);}
					
					
					jsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject(); //Json Object
					
					ArrayList <String> arrayImages = new ArrayList<String>();
					if(arrayImages!=null){
						count = imageArray.size();
					
						for(int i=0;i<count;i++) {
							byte[] imageBytes = null;	BufferedImage	imgRaw = null; File uploadFile = null;
							try {
							//imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageArray[i].substring(imageArray[i].indexOf(",")+1));
							imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageArray.get(i));
							imgRaw = ImageIO.read(new ByteArrayInputStream(imageBytes));
							
							//tempFile = new File((PPWalletEnvironment.getBioImagePath()+"/"+userId+"-rawimage"+i+".jpg"))	;
							uploadFile = new File((PPWalletEnvironment.getFileUploadPath()+"/"+userId+"-rawimage"+i+".jpg"));
							
							
							
							PPWalletEnvironment.setComment(3, className, "uploadFile for user "+ userId + "is" + uploadFile);
							arrFilePath.add(uploadFile.getAbsolutePath());
							ImageIO.write(imgRaw, "png", uploadFile);
							
							//arrayImages.add(PPWalletEnvironment.getBioImagePath()+"/"+userId+"-rawimage"+i+".jpg");
							arrayImages.add((PPWalletEnvironment.getFileUploadPath()+"/"+userId+"-rawimage"+i+".jpg"))	;
	
							}catch(Exception e) {
								throw new Exception ("Problem in writing images :"+e.getMessage());
							}finally {
								if(imgRaw!=null) imgRaw = null;
								if(imageBytes!=null)	imageBytes = null;
								if(uploadFile!=null)	uploadFile = null;
							}
						}
					}
					
					if(arrayImages!=null)
						if(arrayImages.size()==0)
							arrayImages=null;
					// Now register customer
	    		    success = (boolean)CustomerDao.class.getConstructor().newInstance().registerCustomer(nationalId, userId, userPwd, fullName, email, phoneNo, 
	    		    		address, relationshipNo,  arrFilePath, dateOfBirth, passportNo, gender, kraPin);
	    		    
	    		    if(success) {
	    		    	String moduleCode = "C"; //M = Merchants Acquiring
						SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, "C", moduleCode, StringUtils.substring("Customer self registered "+userId, 0, 48) );
						obj.add("error", gson.toJsonTree("false"));
	    		    }else {
	    		    	obj.add("error", gson.toJsonTree("true"));
					}
					
	    		    try {
	    		    	PPWalletEnvironment.setComment(3, className, " JSON_CUST_REGISTRATION String is "+gson.toJson(obj));
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
					
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userPwd!=null) userPwd=null;if(userType!=null) userType=null;
						if(gson!=null) gson=null; if(fullName!=null) fullName=null;if(gender!=null) gender=null;if(phoneNo!=null) phoneNo=null; if(email!=null) email=null;
						if(formatter1!=null) formatter1=null; if(address!=null) address=null; if(dateOfBirth!=null) dateOfBirth=null;
						if(nationalId!=null) nationalId=null; if(fileOne!=null) fileOne=null;if(fileTwo!=null) fileTwo=null;if(relationshipNo!=null) relationshipNo=null; 
						if(arrFilePath!=null) arrFilePath=null; if(imageArray!=null) imageArray=null;if(passportNo!=null) passportNo=null;
					}
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_CUST_REGISTRATION "+e.getMessage());
				}
		
				
				break;
				
//				JSON_PERSO_CUST_REGISTRATION
		/*
		 * try{ String userId = null; String userPwd = null; boolean success = false;
		 * String idNo = null; String passportNo = null; String dateOfBirth = null;
		 * String identityType = null; if(request.getParameter("userid")!=null) userId =
		 * StringUtils.trim( request.getParameter("userid") );
		 * if(request.getParameter("password")!=null) userPwd = StringUtils.trim(
		 * request.getParameter("password") );
		 * if(request.getParameter("passport")!=null) passportNo = StringUtils.trim(
		 * request.getParameter("passport") );
		 * if(request.getParameter("idnumber")!=null) idNo = StringUtils.trim(
		 * request.getParameter("idnumber") ); if(request.getParameter("dob")!=null)
		 * dateOfBirth = StringUtils.trim( request.getParameter("dob") );
		 * if(request.getParameter("hdnidpst")!=null) identityType = StringUtils.trim(
		 * request.getParameter("hdnidpst") );
		 * 
		 * success = (boolean)CustomerDao.class.getConstructor().newInstance().
		 * registerPersoCustomer(userId, userPwd, passportNo, userPwd, idNo,
		 * dateOfBirth, identityType);
		 * 
		 * //consult audit trail on merchant self registration String moduleCode = "C";
		 * //M = Merchants Acquiring
		 * SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId,
		 * "C", moduleCode, StringUtils.substring("Customer self registered "+userId, 0,
		 * 48) );
		 * 
		 * if(success==false) throw new Exception ("Customer registration failed");
		 * response.setContentType("text/html"); try {
		 * ctx.getRequestDispatcher(PPWalletEnvironment.getLoginPage()).forward(request,
		 * response); }finally { if(userId!=null) userId = null; if(userPwd!=null)
		 * userPwd = null; if(passportNo!=null) passportNo = null; if(idNo!=null) idNo =
		 * null; if(dateOfBirth!=null) dateOfBirth = null; if(identityType!=null)
		 * identityType = null;
		 * 
		 * }
		 */
				
			case Rules.JSON_PERSO_CUST_REGISTRATION:
				try {
					String userId = null;	String userPwd = null;	String nationalId = null; String passportNo = null;	boolean allow = true;
					PrintWriter jsonOutput_1 = null; String dateOfBirth = null;  boolean success = false;
					if(jsonObj.get("userid")!=null) userId = jsonObj.get("userid").toString().replaceAll("\"", "");
					if(jsonObj.get("password")!=null) userPwd = jsonObj.get("password").toString().replaceAll("\"", "");
					if(jsonObj.get("nationaid")!=null) nationalId = jsonObj.get("nationaid").toString().replaceAll("\"", "");
					if(jsonObj.get("passportno")!=null) passportNo = jsonObj.get("passportno").toString().replaceAll("\"", "");
					if(jsonObj.get("dob")!=null) dateOfBirth = jsonObj.get("dob").toString().replaceAll("\"", "");
					
				
					
					jsonOutput_1 = response.getWriter();
					Gson gson = new Gson();
					JsonObject obj = new JsonObject();
					
					if(allow) {
						success = (boolean)CustomerDao.class.getConstructor().newInstance().registerPersoCustomer(userId, userPwd, passportNo, userPwd, nationalId, dateOfBirth);
						if(success) {
							SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId,"C", "C", StringUtils.substring("Customer self registered "+userId, 0,48) );
							obj.add("error", gson.toJsonTree("false"));
						}else {
							obj.add("error", gson.toJsonTree("true"));
						}
					}else {
						obj.add("error", gson.toJsonTree("true"));
					}
					
					try {
						jsonOutput_1.print(gson.toJson(obj));
					} finally {
						if(jsonOutput_1!=null) jsonOutput_1.close(); if(userId!=null) userId=null;if(userPwd!=null) userPwd=null;if(dateOfBirth!=null) dateOfBirth=null;
						if(passportNo!=null) passportNo=null;	if(gson!=null) gson=null; if(nationalId!=null) nationalId=null;
					}
					
					
					
				}catch(Exception e) {
					PPWalletEnvironment.setComment(1, className, "Error from JSON_PERSO_CUST_REGISTRATION "+e.getMessage());
				}
				
				break;
				
		
		}
		
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
//		CUST_SELF_REGISTRATION
		// TODO Auto-generated method stub
		HttpSession session	= request.getSession(false);
		
		
		switch (rulesaction){
		case Rules.CUST_SELF_REGISTRATION:
			try {
				
				String userId = null; String userPwd = null; String userName = null; String userEmail = null; String userContact = null;
				String address1 = null; String kraPin = null; 
				String gender = null;	String relationshipNo = null; String nationalId= null; String city= null; ArrayList<String> arrCustFile = new ArrayList<String>();
				int i = 1; FileItem item =null; SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMdd"); 
				String passportNo = null; String dateOfBith = null;

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
			                        arrCustFile.add(uploadedFile.getAbsolutePath());
			                        }
			                    }else {
			                    	
			                    	if(item.getFieldName().equals("regcustid"))              	userId = StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("regcustpwd")) 		        userPwd = StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("regcustname")) 	            userName = StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("regcustemail")) 	        userEmail =StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("regcustphoneno")) 	        userContact =StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("regcustaddress")) 	        address1 = StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("regcustkrapin")) 	        kraPin =StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("hdngender")) 			        gender =StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("regcustnationalid")) 		nationalId = StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("regmerchcity")) 			    city = StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("regcustpasportno")) 			    passportNo = StringUtils.trim( item.getString() );
									if(item.getFieldName().equals("custedob")) 			    dateOfBith = StringUtils.trim( item.getString() );
									
									relationshipNo = formatter1.format(new java.util.Date())	+ RandomStringUtils.random(10, false, true);				                    	
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
			       
				
				
				if(arrCustFile!=null)
					if(arrCustFile.size()==0)
						arrCustFile = null;
				

				// Now register merchant
    		    boolean success = false;
    		    success = (boolean)CustomerDao.class.getConstructor().newInstance().registerCustomer(nationalId, userId, userPwd, userName, userEmail, userContact, 
    		    		address1, relationshipNo,  arrCustFile, dateOfBith, passportNo, gender, kraPin);
    		    
    		    //consult audit trail on merchant self registration
				String moduleCode = "C"; //M = Merchants Acquiring
				SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail(userId, "C", moduleCode, StringUtils.substring("Customer self registered "+userId, 0, 48) );
    		    
				if(success==false)
					throw new Exception ("merchant registration failed");
    		    response.setContentType("text/html");
    		    try {
				ctx.getRequestDispatcher(PPWalletEnvironment.getLoginPage()).forward(request, response);
    		    }finally {
    		    	if(userId!=null) userId = null; if(userPwd!=null) userPwd = null; if(userName!=null) userName = null; if(userEmail!=null) userEmail = null; 
    		    	if(userContact!=null) userContact = null; if(address1!=null) address1 = null;  if(kraPin!=null) kraPin = null; 
    		    	if(gender!=null) gender = null; if(relationshipNo!=null) relationshipNo = null; if(passportNo!=null) passportNo = null; if(dateOfBith!=null) dateOfBith = null; 
    		    	if(nationalId!=null) nationalId = null; if(city!=null) city = null; 
				}
				
				
			}catch (Exception e) {
				callException(request, response, ctx, session,e, e.getMessage());
			}
			break;	
		}
		
		
	}
}
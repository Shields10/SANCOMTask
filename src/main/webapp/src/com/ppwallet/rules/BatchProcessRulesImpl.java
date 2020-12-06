package com.ppwallet.rules;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.ppwallet.PPWalletEnvironment;
import com.ppwallet.dao.BatchDao;
import com.ppwallet.dao.SystemUtilsDao;
import com.ppwallet.model.AssetDetail;
import com.ppwallet.model.CustomerDetails;
import com.ppwallet.model.CustomerTemp;
import com.ppwallet.model.Token;
import com.ppwallet.utilities.Utilities;

public class BatchProcessRulesImpl implements Rules {
	private static String className = BatchProcessRulesImpl.class.getSimpleName();

	@Override
	public void performOperation(String rules, HttpServletRequest request, HttpServletResponse response,
			ServletContext ctx) throws Exception {
		HttpSession session	= request.getSession(true);
		switch (rules){
		case Rules.BATCH_PROCES_PAGE:
			try {
				long startTime = System.nanoTime();
				String langPref = null;
				String batchDate = null; 
				boolean result  =false;
		      	SimpleDateFormat formatter1 = new SimpleDateFormat ("yyMMdd");    
		       	 //return formatter1.format(date);

				ConcurrentHashMap<String,String> hashBatchMessage = new ConcurrentHashMap<String,String>();
				
				if(request.getParameter("batchdate")!=null)	batchDate = StringUtils.trim(request.getParameter("batchdate"));				
				request.setAttribute("langPref", "en");
				request.setAttribute("lastaction", "batch");
				request.setAttribute("lastrule", Rules.BATCH_PROCES_PAGE); //  set the last rule for left menu selection

				// Raw Data
				ConcurrentHashMap<String, CustomerTemp> hashTempTable  = (ConcurrentHashMap<String, CustomerTemp>)BatchDao.class.getConstructor().newInstance().getTempTableDetails(batchDate);
				ConcurrentHashMap<String,String> hashAllCustomerData = (ConcurrentHashMap<String,String>)BatchDao.class.getConstructor().newInstance().getAllCustomerData();
				
				PPWalletEnvironment.setComment(3, className, " : ***** hashTempTable size is  "+hashTempTable.size());	

				//ArrayList<String> existingCustomerRelNo = new ArrayList<String>();
				ArrayList<AssetDetail> arrAssetDetails = new ArrayList<AssetDetail>();
				ArrayList<CustomerDetails> arrCustomer = new ArrayList<CustomerDetails>();//new customer array list
				
				if(hashAllCustomerData!=null) 
					PPWalletEnvironment.setComment(3, className, " : ***** hashAllCustomerData size is  "+hashAllCustomerData.size());	
					
				Enumeration<String> enumKeysFromTempTable = hashTempTable.keys();			
					int count=0;
					while(enumKeysFromTempTable.hasMoreElements()) { count++;
						String key = (String)enumKeysFromTempTable.nextElement();
						//PPWalletEnvironment.setComment(3, className, " : ***** Inside loop  "+(count)+" key for hashTempTable is :"+key);
						CustomerDetails m_CustomerDetails = new CustomerDetails();
						AssetDetail m_AssetDetail = new AssetDetail();
						//String IDFromTempTable = (String)enumKeysFromTempTable.nextElement();
						if(hashAllCustomerData.containsKey(key)) {	
							//PPWalletEnvironment.setComment(3, className, " : ***** data match is  "+ key);	
							m_AssetDetail.setRelationshipNo( hashAllCustomerData.get(key));
							}else {
								//PPWalletEnvironment.setComment(3, className, " : ***** No data match   "+ key);	
							// create a new relationship no for the customer and put the same number for the asset
								String newRelNo = formatter1.format(new java.util.Date())	+ RandomStringUtils.random(10, false, true);
								m_AssetDetail.setRelationshipNo(newRelNo	);
								// relationshipno, customerid, customerpwd, customername, nationalid, passportno, gender, custemail, custcontact, 
								// address, krapin, dateofbirth, status, 
								m_CustomerDetails.setRelationshipNo(newRelNo);
								m_CustomerDetails.setCustomerId("");
								m_CustomerDetails.setPassword("");
								m_CustomerDetails.setCustomerName(   ((CustomerTemp)hashTempTable.get(key)).getCustomerName()       );
								m_CustomerDetails.setNationalId(  ((CustomerTemp)hashTempTable.get(key)).getNationalId()   );
								m_CustomerDetails.setPassportNo( ((CustomerTemp)hashTempTable.get(key)).getPassportNo() );
								m_CustomerDetails.setGender(  ((CustomerTemp)hashTempTable.get(key)).getGender() );
								m_CustomerDetails.setEmail(   ((CustomerTemp)hashTempTable.get(key)).getCustomerEmail()  );
								m_CustomerDetails.setContact(  ((CustomerTemp)hashTempTable.get(key)).getCustomerContact()  );
								m_CustomerDetails.setAddress(   ((CustomerTemp)hashTempTable.get(key)).getAssetAddress()  );
								m_CustomerDetails.setKraPIN(  ((CustomerTemp)hashTempTable.get(key)).getKRAPin()   );
								m_CustomerDetails.setDateOfBirth( ((CustomerTemp)hashTempTable.get(key)).getDateOfBirth()     );
								m_CustomerDetails.setExpiry("9999-12-31");
								m_CustomerDetails.setStatus(  "P"  );
								arrCustomer.add(m_CustomerDetails);
							}
						
							m_AssetDetail.setAssetType("D");
							m_AssetDetail.setAssetNumber(  ((CustomerTemp)hashTempTable.get(key)).getAssetNumber()   );
							m_AssetDetail.setDateOfIssue(  ((CustomerTemp)hashTempTable.get(key)).getAssetDateOfIssue() );
							m_AssetDetail.setDateOfExpiry(  ((CustomerTemp)hashTempTable.get(key)).getAssetDateOfExpiry() );
							m_AssetDetail.setSubClass( ((CustomerTemp)hashTempTable.get(key))  .getAssetSubClass() );
							m_AssetDetail.setSerialNoIfApplicable("");
							m_AssetDetail.setCustCOntact( ((CustomerTemp)hashTempTable.get(key)).getCustomerContact()  );
							m_AssetDetail.setAssetAddress(    ((CustomerTemp)hashTempTable.get(key)).getAssetAddress()  );
							m_AssetDetail.setCardNumber(     ((CustomerTemp)hashTempTable.get(key)).getCardNumber()   );
							m_AssetDetail.setAssetStatus("A");
							m_AssetDetail.setUserType(  "C" );
							arrAssetDetails.add(m_AssetDetail);
							//PPWalletEnvironment.setComment(3, className, " : ***** Inside loop  "+(count)+" rel no from m_AssetDetail :"+m_AssetDetail.getRelationshipNo() +" and rel no from m_CustomerDetails"+m_CustomerDetails.getRelationshipNo());	
					}
				PPWalletEnvironment.setComment(3, className,  " --> Record processed for arrCustomer Table is  "+arrCustomer.size());
				PPWalletEnvironment.setComment(3, className,  " --> Record processed for AssetDetails Table is  "+arrAssetDetails.size());
				//******** 1- insert the data into the database
					
					ArrayList<Token> arrToken = new ArrayList<Token> ();
					// Step 3 : form Token details

					for(int i=0;i<arrAssetDetails.size();i++) {
					//	PPWalletEnvironment.setComment(3, className, " --> for loop "+(i+1)+" arrAssetDetails  getRelationshipNo is   "+((AssetDetail)arrAssetDetails.get(i)).getRelationshipNo() );

			 		Token m_Token = new Token();
			 		m_Token.setRelationshipNo( ((AssetDetail)arrAssetDetails.get(i)).getRelationshipNo() );
					//dPPWalletEnvironment.setComment(3, className, " --> relationshipno is  "+((AssetDetail)arrAssetDetails.get(i)).getRelationshipNo());
			 		String tokenValue = Utilities.generateToken( ((AssetDetail)arrAssetDetails.get(i)).getCardNumber() );
			 		m_Token.setTokenNo(tokenValue);
					
			 		//String tokenValue = RandomStringUtils.random(10, false, true);
			 		//m_Token.setTokenNo(tokenValue);
			 		
			 		m_Token.setCardNo( ((AssetDetail)arrAssetDetails.get(i)).getCardNumber() );
			 		m_Token.setDateOfExpiry(((AssetDetail)arrAssetDetails.get(i)).getDateOfExpiry());
			 		m_Token.setUserName(((AssetDetail)arrAssetDetails.get(i)).getUserName());
			 		m_Token.setRelationshipNo(((AssetDetail)arrAssetDetails.get(i)).getRelationshipNo());
			 		m_Token.setUserType(((AssetDetail)arrAssetDetails.get(i)).getUserType());
			 		m_Token.setAssetType( ((AssetDetail)arrAssetDetails.get(i)).getAssetType()  ) ;
			 		m_Token.setCurrencyId("404");
			 		arrToken.add(m_Token);
					//PPWalletEnvironment.setComment(3, className, " --> finally arrToken size is  "+arrToken.size());

					}
					if(arrToken!=null)
						if(arrToken.size()>0)
							//PPWalletEnvironment.setComment(3, className, " --> arrToken size is  "+arrToken.size());
					
					result = (boolean)BatchDao.class.getConstructor().newInstance().insertCustomerDataforBatch(arrAssetDetails,  arrCustomer , arrToken);
					hashBatchMessage.put("1",formatter1.format(new java.util.Date()) + " --> Record processed for Customer Table is  "+arrAssetDetails.size() + " and Asset Table is  "+arrAssetDetails.size());
					request.setAttribute("batchmessage", "Record processed for Customer Table is  "+arrCustomer.size() + " and Asset Table is  "+arrAssetDetails.size() );
				   // call the audit trail here
					SystemUtilsDao.class.getConstructor().newInstance().addAuditTrail( "BATCH_USER", 
							"B", "C",  StringUtils.substring("Updated Cust Table on date "+batchDate, 0, 48)    );
					PPWalletEnvironment.setComment(3, className, " : auditTrail added");	

			
					//******** 2- create token
					/*  toekenid, cardno, dateofexpiry,user_reg_no, username, usertype
					 * 
					 */
					long endTime1 = System.nanoTime();
					
					if(arrToken!=null && result) {			
						hashBatchMessage.put("2",formatter1.format(new java.util.Date()) + " --> Generated Tokens of size   "+arrToken.size() );
					PPWalletEnvironment.setComment(3, className, "Generated Tokens of size "+arrToken.size());
					PPWalletEnvironment.setComment(3, className, "Database time taken is  "+(endTime1)/1000000 + "milliseconds");

					boolean result2 = (boolean)BatchDao.insertIntoCardVault(arrToken );
					PPWalletEnvironment.setComment(3, className, "Blockchain executed  "+result2);
					
					if(arrToken!=null) arrToken = null;
					}
				//request.setAttribute("alltemptabledata", arrTempTable);
				request.setAttribute("batchmessage", hashBatchMessage);
				response.setContentType("text/html");
				
				long endTime2 = System.nanoTime();
				PPWalletEnvironment.setComment(3, className, "Total time taken is  "+(endTime2)/1000000 + "milliseconds");
				try {
					ctx.getRequestDispatcher(PPWalletEnvironment.getBatchJobDashboardPage()).forward(request,response);
				} finally {
					if(hashTempTable!=null) hashTempTable=null;	if(hashAllCustomerData!=null) hashAllCustomerData=null;
					if(arrAssetDetails!=null) arrAssetDetails=null;
				if(arrCustomer!=null) arrCustomer=null;	
				if(arrToken!=null) arrToken=null;
				}
			} catch (Exception e) {
				callException(request, response, ctx, session, e, e.getMessage());
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

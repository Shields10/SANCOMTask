package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.dao.ApplicantsDao;
import com.example.demo.dao.UserDao;
import com.example.demo.model.Applicants;
import com.example.demo.model.User;
import com.example.demo.security.Security;
import com.example.demo.utilities.Utilities;



@RestController
public class RestWebController {
	@Autowired
	private UserDao  userDao;
	
	
	@Autowired
	private ApplicantsDao  applicantDao;
	
	@PostMapping("/restapi/signup")
	public HashMap<String, String> signUp(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {
		  HashMap<String, String> response = new HashMap<>();
	  
  try {
		  System.out.println(payload);
		   String password= payload.get("password").toString();
		   String userEmail= payload.get("useremail").toString();
		   String apiKey= payload.get("apikey").toString();
		   System.out.println("password "+password+"userEmail"+userEmail);

		   if (apiKey.equals(Utilities.getAPIKEY())) {
		
			List<User> userDetails = userDao.findByUserEmail(userEmail);
			
			System.out.println("userDetails "+userDetails.size());
			if (userDetails.size()>0) {
				response.put("error", "userexit");
			}else {
			// save details
				User users= new User();
				UUID userRelNo= UUID.randomUUID();
				  String salt = Security.getSalt(30);
			        
			        // Protect user's password. The generated value can be stored in DB.
			        String mySecurePassword = Security.generateSecurePassword(password, salt);
				
				
				users.setUserRelationshipNo(userRelNo.toString());
				users.setPassword(mySecurePassword);
				users.setSalt(salt);
				users.setUserEmail(userEmail);
				System.out.println("mySecurePassword "+mySecurePassword+"salt "+salt);
				userDao.save(users);
				response.put("statusCode", "200");
			}

			}else {
				response.put("statusCode", "401");
			}
			System.out.println("Response is"+response.toString());
			
			
  			}catch (Exception e) {
  				System.out.println("There was an error in  login "+e.getMessage());
  			}
			return response;
		  
		}
	
	
	@PostMapping("restapi/login")
	public HashMap<String, String> loginVerification(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {
		  HashMap<String, String> response = new HashMap<>();
  try {
		  System.out.println(payload);
		   String password= payload.get("password").toString();
		   String userEmail= payload.get("userEmail").toString();
		   String apiKey= payload.get("apikey").toString();
		   System.out.println("password "+password+"userEmail"+userEmail+"apiKey"+apiKey);

		   if (apiKey.equals(Utilities.getAPIKEY())) {
		
			List<User> userDetails = userDao.findByUserEmail(userEmail);
			
			
			 System.out.println("userDetails"+userDetails.toString());
			  String passwordFromDb=null; String relNo=null; String salt=null;
			 for (int i = 0; i < userDetails.size(); i++) {
				     passwordFromDb = userDetails.get(i).getPassword();
				     relNo = userDetails.get(i).getUserRelationshipNo();
				     salt = userDetails.get(i).getSalt();

				}
			 
			if (userDetails.size()>0) {
			// compare password 
				
			     boolean passwordMatch = Security.verifyUserPassword(password, passwordFromDb, salt);
				
				if (passwordMatch){
					response.put("statusCode", "200");
					response.put("relno", relNo);

						 // Check if user has registered details  
						 
						 List<Applicants> applicantDetails = applicantDao.findByuserRelationshipNo(relNo);
						 
						 if (applicantDetails.size()>0) {
							 response.put("details", "present");
 
						 }else {
							 response.put("details", "notpresent"); 
						 }
					
					
				}else {
					response.put("error", "incorrect");
				}
			}else {
				response.put("error", "userdoesnotexist");
			}

			}else {
				response.put("statusCode", "401");
			}
			System.out.println("Response is"+response.toString());
			
			
  			}catch (Exception e) {
  				System.out.println("There was an error in  login "+e.getMessage());
  			}
			return response;
		  
		}
	
	
	
	
	

	
	
	

    }
	
	


package com.example.demo.controller;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.ApplicantsDao;
import com.example.demo.dao.AppliedJobsDao;
import com.example.demo.model.Applicants;
import com.example.demo.model.ApppliedJobs;
import com.example.demo.model.User;
import com.example.demo.utilities.Utilities;
@RestController
public class JobApplicationController {
	@Autowired
	ApplicantsDao applicantsDao;
	@Autowired
	AppliedJobsDao appliedJobsDao;
	
	
	@PostMapping("/restapi/registerdetails")
	public HashMap<String, String>saveApplicantDetails(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {
		HashMap<String, String> response = new HashMap<>();
		try {
			
			
			String apiKey= payload.get("apikey").toString();
			String userRelationshipNo= payload.get("userRelationshipNo").toString();
			String  userFirstName= payload.get("userFirstName").toString();
			String userLastName= payload.get("userLastName").toString();
			String userPhoneNumber= payload.get("userPhoneNumber").toString();
			String userEducationLevel= payload.get("userEducationLevel").toString();
			String yearsOfExperience= payload.get("yearsOfExperience").toString();
			
			if (apiKey.equals(Utilities.getAPIKEY())) {
				// Save details to db
				Applicants applicants=new Applicants();
				applicants.setUserRelationshipNo(userRelationshipNo);
				applicants.setUserFirstName(userFirstName);
				applicants.setUserLastName(userLastName);
				applicants.setUserPhoneNumber(userPhoneNumber);
				applicants.setUserEducationLevel(userEducationLevel);
				applicants.setYearsOfExperience(yearsOfExperience);
				applicantsDao.save(applicants);
				response.put("statusCode", "200");

			}else {
				response.put("statusCode", "401");
			}
			
			System.out.println("Response is"+response.toString());
		}catch (Exception e) {
			System.out.println("There was an error in  saving details "+e.getMessage());
		}
		
		return response;
	}
	
	@PostMapping("/restapi/applyjob")
	public HashMap<String, String>applyJob(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {
		HashMap<String, String> response = new HashMap<>();
		try {
			
			
			String apiKey= payload.get("apikey").toString();
			String jobId= payload.get("jobid").toString();
			String userRelNo= payload.get("relno").toString();
			String createdOn=Utilities.getCurrentTimeandDate();
			String applicationId=UUID.randomUUID().toString();
			
			if (apiKey.equals(Utilities.getAPIKEY())) {
				// Save details to db
				ApppliedJobs appliedJobs=new ApppliedJobs();
				appliedJobs.setApplicationId(applicationId);
				appliedJobs.setJobId(jobId);
				appliedJobs.setUserRelationshipNo(userRelNo);
				appliedJobs.setCreatedOn(createdOn);
				appliedJobsDao.save(appliedJobs);
				response.put("statusCode", "200");

			}else {
				response.put("statusCode", "401");
			}
			
			System.out.println("Response is"+response.toString());
		}catch (Exception e) {
			System.out.println("There was an error in  applying job "+e.getMessage());
		}
		
		return response;
	}
	@PostMapping("/restapi/viewappliedjobs")
	public HashMap<String, String>getAppliedJob(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {
		HashMap<String, String> response = new HashMap<>();
		try {
			
			
			String apiKey= payload.get("apikey").toString();
			String userRelNo= payload.get("relno").toString();
			
			
			if (apiKey.equals(Utilities.getAPIKEY())) {
				List<ApppliedJobs> appliedJobs = appliedJobsDao.findByuserRelationshipNo(userRelNo);
				
				if(appliedJobs.size()>0) {
					
					int count= appliedJobs.size();
					
					 String []arrJobId =new String [count];						 
					 String []arrCreatedOn =new String [count];						 
					 String []arrApplicationId =new String [count];						 
											 

					 
					 for (int i=0;i<count;i++) {
						 arrJobId[i]=appliedJobs.get(i).getJobId();
						 arrCreatedOn[i]=appliedJobs.get(i).getCreatedOn();
						 arrApplicationId[i]=appliedJobs.get(i).getApplicationId();
						

					 }
					 response.put("statusCode", "200"); 
					 response.put("jobId", Arrays.toString(arrJobId));
					 response.put("createdon", Arrays.toString(arrCreatedOn));
					 response.put("applicationid", Arrays.toString(arrApplicationId));

				}else {
					response.put("error", "nodata");
				}
				
				
				response.put("statusCode", "200");

			}else {
				response.put("statusCode", "401");
			}
			
			System.out.println("Response is"+response.toString());
		}catch (Exception e) {
			System.out.println("There was an error in  applying job "+e.getMessage());
		}
		
		return response;
	}
	@PostMapping("/restapi/deselectjob")
	public HashMap<String, String>deleteJob(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {
		HashMap<String, String> response = new HashMap<>();
		try {
			String apiKey= payload.get("apikey").toString();
			String applicationId= payload.get("applicationid").toString();
			if (apiKey.equals(Utilities.getAPIKEY())) {
				appliedJobsDao.deleteById(applicationId);
				response.put("statusCode", "200");
			}else {
				response.put("statusCode", "401");
			}
			
		}catch(Exception e) {
			System.out.println("There was an error in  deleting  job "+e.getMessage());
		}
		return response;

	}	
}	
//	getAppdetails
	
//	@RequestMapping("/getAppdetails")
//	public String addApplicant( Applicants  applicants) {
//	System.out.println("first name "+applicants.getUserFirstName());
//		UUID jobId=UUID.randomUUID();
//		applicants.setJobId(jobId.toString());
//		applicantsDao.save(applicants);
//
//	return "viewappliedjobs.jsp";
//	}


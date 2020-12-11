package com.example.demo.controller;

import java.util.ArrayList;
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
import com.example.demo.dao.JobDao;
import com.example.demo.model.Applicants;
import com.example.demo.model.ApppliedJobs;
import com.example.demo.model.Jobs;
import com.example.demo.utilities.Utilities;

@RestController
public class JobController {

	
	@Autowired
	JobDao jobDao;
	
	@Autowired
	AppliedJobsDao appliedJobsDao;
	
	@PostMapping("/restapi/createnewjobs")
	public HashMap<String, String>saveApplicantDetails(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {
		HashMap<String, String> response = new HashMap<>();
		try {
			
			String apiKey= payload.get("apikey").toString();
			String jobId=UUID.randomUUID().toString();
			String jobDescription= payload.get("jobDescription").toString();
			String  jobName= payload.get("jobName").toString();
			String yearOfExperience= payload.get("yearOfExperience").toString();
			String educationLevel= payload.get("educationLevel").toString();
			String interviewDate= payload.get("interviewDate").toString();
			String interviewStartTime= payload.get("interviewStartTime").toString();
			String interviewEndTime= payload.get("interviewEndTime").toString();
			String jobType= payload.get("jobType").toString();
			String status= payload.get("status").toString();
			
			if (apiKey.equals(Utilities.getAPIKEY())) {
				// Save details to db
				Jobs jobs=new Jobs();
				jobs.setJobId(jobId);;
				jobs.setJobDescription(jobDescription);
				jobs.setJobName(jobName);
				jobs.setYearOfExperience(yearOfExperience);
				jobs.setEducationLevel(educationLevel);
				jobs.setInterviewDate(interviewDate);
				jobs.setInterviewStartTime(interviewStartTime);
				jobs.setInterviewEndTime(interviewEndTime);
				jobs.setJobType(jobType);
				jobs.setStatus(status);
				jobDao.save(jobs);
				response.put("statusCode", "200");
				
				
			}else {
				response.put("statusCode", "401");
			}
			
			System.out.println("Response is"+response.toString());
		}catch (Exception e) {
			System.out.println("There was an error in  saving job "+e.getMessage());
		}
		
		return response;
	}
	
	@PostMapping("/restapi/viewalljobs")
	public HashMap<String, Object>viewAllJobs(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {
		HashMap<String, Object> response = new HashMap<>();
		try {
			
			String apiKey= payload.get("apikey").toString();
			if (apiKey.equals(Utilities.getAPIKEY())) {
				//Fetch jobs 
				ArrayList<Jobs> job=(ArrayList<Jobs>) jobDao.findAll();
				 if (job.size()>0)	{
					//Get the job details in array format
					 int count= job.size();
			
					 String []arrJobId =new String [count];						 
					 String []arrJobName =new String [count];						 
					 String []arrYearOfExperience =new String [count];						 
					 String []arrEducationLevel =new String [count];						 
					 String []arrStatus =new String [count];						 
					 String []arrInteviewDate =new String [count];						 
					 String []arrInteviewStartTime =new String [count];						 
					 String []arrInteviewEndTime =new String [count];						 
					 String []arrInteviewJobType =new String [count];
					 String []arrJobDescription =new String [count];
					 
					 for (int i=0;i<count;i++) {
						 arrJobId[i]=job.get(i).getJobId();
						 arrJobName[i]=job.get(i).getJobName();
						 arrYearOfExperience[i]=job.get(i).getYearOfExperience();
						 arrEducationLevel[i]=job.get(i).getEducationLevel();
						 arrStatus[i]=job.get(i).getJobType();
						 arrInteviewDate[i]=job.get(i).getInterviewDate();
						 arrInteviewStartTime[i]=job.get(i).getInterviewStartTime();
						 arrInteviewEndTime[i]=job.get(i).getInterviewEndTime();
						 arrInteviewJobType[i]=job.get(i).getJobType();
						 arrJobDescription[i]=job.get(i).getJobDescription();

					 }
					 response.put("statusCode", "200"); 
					 response.put("jobid", arrJobId);
					 response.put("jobname", arrJobName);
					 response.put("yearsofexperience",arrYearOfExperience);
					 response.put("educationlevel", arrEducationLevel);
					 response.put("status", arrStatus);
					 response.put("interviewdate",arrInteviewDate);
					 response.put("interviewstarttime", arrInteviewStartTime);
					 response.put("interviewendtime", arrInteviewEndTime);
					 response.put("jobtype", arrInteviewEndTime);
					 response.put("jobdescription",arrJobDescription);
					 
						
				 }else {
					 response.put("error", "nojobs"); 
				 }
				
			}else {
				response.put("statusCode", "401");
			}
	
			
			System.out.println("Response is"+response.toString());
		}catch (Exception e) {
			System.out.println("There was an error in  viewing  job "+e.getMessage());
		}
	
	return response;
	
	
	}	
	
	@PostMapping("/restapi/deletejob")
	public HashMap<String, String>deleteJob(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {
		HashMap<String, String> response = new HashMap<>();
		try {
			String apiKey= payload.get("apikey").toString();
			String jobId= payload.get("jobid").toString();
			if (apiKey.equals(Utilities.getAPIKEY())) {
				jobDao.deleteById(jobId);
				response.put("statusCode", "200");
			}else {
				response.put("statusCode", "401");
			}
			
		}catch(Exception e) {
			System.out.println("There was an error in  deleting  job "+e.getMessage());
		}
		
		
		return response;

	}	
	
	@PostMapping("/restapi/viewjobapplicants")
	public HashMap<String, Object>viewJobApplicants(@RequestBody HashMap<String, Object> payload) 
		    throws Exception {
		HashMap<String, Object> response = new HashMap<>();
		try {
			
			String apiKey= payload.get("apikey").toString();
			if (apiKey.equals(Utilities.getAPIKEY())) {
				//Fetch Applicants 
				ArrayList<ApppliedJobs> appliedJobs=(ArrayList<ApppliedJobs>) appliedJobsDao.findAll();
				 if (appliedJobs.size()>0)	{
					//Get the job details in array format
					 int count= appliedJobs.size();
			
					 String []arrJobId =new String [count];						 					 
					 String []arrApplicationId =new String [count];						 
					 String []arrUserRelationshipNo =new String [count];						 
					 String []arrcreatedOn =new String [count];						 
					 for (int i=0;i<count;i++) {
						 arrJobId[i]=appliedJobs.get(i).getJobId();
						 arrApplicationId[i]=appliedJobs.get(i).getApplicationId();
						 arrUserRelationshipNo[i]=appliedJobs.get(i).getUserRelationshipNo();
						 arrcreatedOn[i]=appliedJobs.get(i).getCreatedOn();
						 

					 }
					 response.put("statusCode", "200"); 
					 response.put("jobid", arrJobId);
					 response.put("applicationid", arrApplicationId);
					 response.put("userrelno", arrUserRelationshipNo);
					 response.put("createdon", arrcreatedOn);
				
						
				 }else {
					 response.put("error", "noapplicants"); 
				 }
				
			}else {
				response.put("statusCode", "401");
			}
	
			
			System.out.println("Response is"+response.toString());
		}catch (Exception e) {
			System.out.println("There was an error in  viewing  job "+e.getMessage());
		}
	
	return response;
	
	
	}	
	
	
}	
	

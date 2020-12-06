package com.example.demo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.dao.ApplicantsDao;
import com.example.demo.model.Applicants;
@Controller
public class JobApplicationController {
	@Autowired
	ApplicantsDao applicantsDao;
	
	
	
//	getAppdetails
	
	@RequestMapping("/getAppdetails")
	public String addApplicant( Applicants  applicants) {
	System.out.println("first name "+applicants.getUserFirstName());
		UUID jobId=UUID.randomUUID();
		applicants.setJobId(jobId.toString());
		applicantsDao.save(applicants);

	return "viewappliedjobs.jsp";
	}
}

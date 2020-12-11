package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class AuthenticationController {
	
	@RequestMapping("/")
	public String home() {	
		System.out.println("we are here");
		return "index.jsp";
	}
	
	@RequestMapping("/maintainjob")
	public String manageJob() {	
		return "maintainjob.jsp";
	}
	@RequestMapping("/viewjobapplicants")
	public String viewJobApplicants() {	
		return "viewjobapplicants.jsp";
	}
	

	
	
	
	

}

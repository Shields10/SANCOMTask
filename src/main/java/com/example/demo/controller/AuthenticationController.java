package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.dao.UserDao;


@Controller
public class AuthenticationController {
	@Autowired
	UserDao usersDao;
	
	@RequestMapping("/")
	public String home() {	
		System.out.println("we are here");
		return "index.jsp";
	}
	
	@RequestMapping("/mantainjobapplicants")
	public String manageJob() {	
		return "maintainjob.jsp";
	}
	
	@RequestMapping("/viewjobapplicants")
	public String viewJobApplicants() {	
		return "viewjobapplicants.jsp";
	}
	
	
	
	

}

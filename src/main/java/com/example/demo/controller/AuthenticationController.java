package com.example.demo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;
import com.example.demo.utilities.Utilities;

@Controller
public class AuthenticationController {
	@Autowired
	UserDao usersDao;
	
	@RequestMapping("/")
	public String home() {	
		System.out.println("we are here");
		return "index.jsp";
	}
	
	@RequestMapping("/userRegistration")
	
	public String registerUser( User  users, 	@RequestParam String password) {
		try {
					System.out.println("Email"+users.getUserEmail());
					password=Utilities.encryptString(password);
					users.setPassword(password);

					usersDao.save(users);
		
		} catch (Exception e) {
			System.out.println("Exception is "+e.getMessage());
		}
	return "index.jsp";
	}
	
	

}

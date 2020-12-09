package com.example.demo.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dao.UserDao;
import com.example.demo.model.User;


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
					UUID userRelNo= UUID.randomUUID();
					users.setUserRelationshipNo(userRelNo.toString());
					System.out.println("Email"+users.getUserEmail());
					usersDao.save(users);
		
		} catch (Exception e) {
			System.out.println("Exception is "+e.getMessage());
		}
	return "index.jsp";
	}
	
	

}

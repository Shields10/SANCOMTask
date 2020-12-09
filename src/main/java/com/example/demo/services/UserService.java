package com.example.demo.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.LoginForm;
import com.example.demo.model.User;


@Service
public class UserService {

    private List<User> users;

    public List<User> login(LoginForm loginForm) {

        //do stuffs
    	//dump user data
    	User user = new User(loginForm.getUserEmail(), loginForm.getPassword());
    	
        return new ArrayList<User>(Arrays.asList(user));

    }

}

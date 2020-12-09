package com.example.demo.model;


public class LoginForm {

   String userEmail;
    String password;

    
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public void setPassword(String password) {
		this.password = password;
	}
    
    public String getPassword() {
		return password;
	}
    
}
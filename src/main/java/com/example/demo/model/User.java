package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;
@Entity
public class User {
	@Id
	public String userEmail;
	public String password;
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}

package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "user_details")
public class User {
	@Id
	public String userRelationshipNo;
	public String userEmail;
	public String password;
	public String salt;
	
	
	public User(){}
    
    public User(String userEmail, String password){
        this.userEmail = userEmail;
        this.password = password;
    }
	
    
    @Override
    public String toString() {
        return "User{" +
                "userEmail='" + userEmail + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
	
    public String getUserRelationshipNo() {
		return userRelationshipNo;
	}

	public void setUserRelationshipNo(String userRelationshipNo) {
		this.userRelationshipNo = userRelationshipNo;
	}
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

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
}

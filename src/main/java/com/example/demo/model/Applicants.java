package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Applicants {
	
	@Id
	public String jobId;
	public String userFirstName;
	public String userLastName;
	public String userPhoneNumber;
	public String userEmail;
	public String userEducationLevel;
	public String yearsOfExperience;
	public String userRelationshioNo;
	
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getUserFirstName() {
		return userFirstName;
	}
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}
	public String getUserLastName() {
		return userLastName;
	}
	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}
	public String getUserPhoneNumber() {
		return userPhoneNumber;
	}
	public void setUserPhoneNumber(String userPhoneNumber) {
		this.userPhoneNumber = userPhoneNumber;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserEducationLevel() {
		return userEducationLevel;
	}
	public void setUserEducationLevel(String userEducationLevel) {
		this.userEducationLevel = userEducationLevel;
	}
	public String getYearsOfExperience() {
		return yearsOfExperience;
	}
	public void setYearsOfExperience(String yearsOfExperience) {
		this.yearsOfExperience = yearsOfExperience;
	}
	public String getUserRelationshioNo() {
		return userRelationshioNo;
	}
	public void setUserRelationshioNo(String userRelationshioNo) {
		this.userRelationshioNo = userRelationshioNo;
	}
	
	
	
	
	
}

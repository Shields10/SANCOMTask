package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Jobs {

	@Id
	public String jobId;
	public String jobDescription;
	public String jobName;
	public String yearOfExperience;
	public String educationLevel;
	public String status;
	public String interviewDate;
	public String interviewStartTime;
	public String interviewEndTime;
	public String jobType;
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getYearOfExperience() {
		return yearOfExperience;
	}
	public void setYearOfExperience(String yearOfExperience) {
		this.yearOfExperience = yearOfExperience;
	}
	public String getEducationLevel() {
		return educationLevel;
	}
	public void setEducationLevel(String educationLevel) {
		this.educationLevel = educationLevel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getInterviewDate() {
		return interviewDate;
	}
	public void setInterviewDate(String interviewDate) {
		this.interviewDate = interviewDate;
	}
	public String getInterviewStartTime() {
		return interviewStartTime;
	}
	public void setInterviewStartTime(String interviewStartTime) {
		this.interviewStartTime = interviewStartTime;
	}
	public String getInterviewEndTime() {
		return interviewEndTime;
	}
	public void setInterviewEndTime(String interviewEndTime) {
		this.interviewEndTime = interviewEndTime;
	}
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	
	
	
}

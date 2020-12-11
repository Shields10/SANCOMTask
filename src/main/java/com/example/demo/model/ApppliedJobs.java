package com.example.demo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ApppliedJobs {
	@Id
	String applicationId;
	String jobId;
	String userRelationshipNo;
	String createdOn;
	
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getUserRelationshipNo() {
		return userRelationshipNo;
	}
	public void setUserRelationshipNo(String userRelationshipNo) {
		this.userRelationshipNo = userRelationshipNo;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

}

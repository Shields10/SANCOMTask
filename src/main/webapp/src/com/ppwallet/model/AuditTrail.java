package com.ppwallet.model;

public class AuditTrail {
	
	String traiId;
	String userId;
	String userType;
	String moduleCode;
	String comment;
	String trailTime;
	
	public String getTraiId() {
		return traiId;
	}
	public void setTraiId(String traiId) {
		this.traiId = traiId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getModuleCode() {
		return moduleCode;
	}
	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getTrailTime() {
		return trailTime;
	}
	public void setTrailTime(String trailTime) {
		this.trailTime = trailTime;
	}
	
	

}

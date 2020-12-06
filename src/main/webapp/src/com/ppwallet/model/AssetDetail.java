package com.ppwallet.model;

public class AssetDetail {
	/*	`relationshipno` VARCHAR(16) NOT NULL,
`asset_type` CHAR(1) NOT NULL DEFAULT '0' COMMENT 'D = Drivers license, V = Vehicle Registration, I = National ID',
`asset_number` VARCHAR(50) NOT NULL DEFAULT '0' COMMENT 'Number of the asset which can be alphanumeric',
`asset_date_of_issue` VARCHAR(12) NOT NULL DEFAULT '0',
`asset_date_of_expiry` VARCHAR(12) NOT NULL DEFAULT '0',
`asset_subclass` VARCHAR(100) NOT NULL DEFAULT '0' COMMENT 'Need to understand how the class sis structured for eDL and eVL',
`asset_serial_no` VARCHAR(100) NOT NULL DEFAULT '0' COMMENT 'Chasis number for vehile, else blank for eDL',
`custcontact` VARCHAR(50) NOT NULL DEFAULT '0' COMMENT 'Can be different from the main table',
`address` VARCHAR(300) NOT NULL DEFAULT '0' COMMENT 'Can be different from the main table',
`status` CHAR(1) NULL DEFAULT 'A' COMMENT 'A-Active, I-Inactive',
`createdon` DATETIME NULL DEFAULT NULL,
`expiry` DATE NOT NULL DEFAULT '9999-12-31',
	 * 
	 * 
	 */
	String relationshipNo;
	String assetType;
	String assetNumber;
	String dateOfIssue;
	String dateOfExpiry;
	String subClass;
	String serialNoIfApplicable;
	String custCOntact;
	String assetAddress;
	String assetStatus;
	String createdOn;
	String userName;
	String cardNumber;
	String userType;
	
	public String getRelationshipNo() {
		return relationshipNo;
	}
	public void setRelationshipNo(String relationshipNo) {
		this.relationshipNo = relationshipNo;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public String getAssetNumber() {
		return assetNumber;
	}
	public void setAssetNumber(String assetNumber) {
		this.assetNumber = assetNumber;
	}
	public String getDateOfIssue() {
		return dateOfIssue;
	}
	public void setDateOfIssue(String dateOfIssue) {
		this.dateOfIssue = dateOfIssue;
	}
	public String getDateOfExpiry() {
		return dateOfExpiry;
	}
	public void setDateOfExpiry(String dateOfExpiry) {
		this.dateOfExpiry = dateOfExpiry;
	}
	public String getSubClass() {
		return subClass;
	}
	public void setSubClass(String subClass) {
		this.subClass = subClass;
	}
	public String getSerialNoIfApplicable() {
		return serialNoIfApplicable;
	}
	public void setSerialNoIfApplicable(String serialNoIfApplicable) {
		this.serialNoIfApplicable = serialNoIfApplicable;
	}
	public String getCustCOntact() {
		return custCOntact;
	}
	public void setCustCOntact(String custCOntact) {
		this.custCOntact = custCOntact;
	}
	public String getAssetAddress() {
		return assetAddress;
	}
	public void setAssetAddress(String assetAddress) {
		this.assetAddress = assetAddress;
	}
	public String getAssetStatus() {
		return assetStatus;
	}
	public void setAssetStatus(String assetStatus) {
		this.assetStatus = assetStatus;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	} 

}

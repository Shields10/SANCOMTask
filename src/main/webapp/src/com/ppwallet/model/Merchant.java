package com.ppwallet.model;

import java.util.ArrayList;

public class Merchant {
String merchantId;
String merchantName;
String password;
String nationalId;
String email;
String contact;
String address1;
String adress2;
String pinCode;
String city;
String billerCode;
String companyName;
String companyRegistration;
String msfPlanId;
String mccCategoryId;
String mccCategoryName;
String status;
String createdOn;
String expiryDate;
String walletId;
ArrayList<String> documentArray;

public String getMerchantId() {
	return merchantId;
}
public void setMerchantId(String merchantId) {
	this.merchantId = merchantId;
}
public String getMerchantName() {
	return merchantName;
}
public void setMerchantName(String merchantName) {
	this.merchantName = merchantName;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
//New Addition
public String getNationalId() {
	return nationalId;
}
public void setNationalId(String nationalId) {
	this.nationalId = nationalId;
}
public String getEmail() {
	return email;
}
public void setEmail(String email) {
	this.email = email;
}

public String getContact() {
	return contact;
}
public void setContact(String contact) {
	this.contact = contact;
}
public String getAddress1() {
	return address1;
}
public void setAddress1(String address1) {
	this.address1 = address1;
}
public String getAdress2() {
	return adress2;
}
public void setAdress2(String adress2) {
	this.adress2 = adress2;
}
public String getPinCode() {
	return pinCode;
}
public void setPinCode(String pinCode) {
	this.pinCode = pinCode;
}
public String getCity() {
	return city;
}
public void setCity(String city) {
	this.city = city;
}
public String getBillerCode() {
	return billerCode;
}
public String getCompanyName() {
	return companyName;
}
public void setCompanyName(String companyName) {
	this.companyName = companyName;
}
public void setBillerCode(String billerCode) {
	this.billerCode = billerCode;
}

public String getMccCategoryName() {
	return mccCategoryName;
}
public void setMccCategoryName(String mccCategoryName) {
	this.mccCategoryName = mccCategoryName;
}
public String getMccCategoryId() {
	return mccCategoryId;
}
public void setMccCategoryId(String mccCategoryId) {
	this.mccCategoryId = mccCategoryId;
}
public String getCompanyRegistration() {
	return companyRegistration;
}
public void setCompanyRegistration(String companyRegistration) {
	this.companyRegistration = companyRegistration;
}
public String getMsfPlanId() {
	return msfPlanId;
}
public void setMsfPlanId(String msfPlanId) {
	this.msfPlanId = msfPlanId;
}

public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getCreatedOn() {
	return createdOn;
}
public void setCreatedOn(String createdOn) {
	this.createdOn = createdOn;
}
public String getExpiryDate() {
	return expiryDate;
}
public void setExpiryDate(String expiryDate) {
	this.expiryDate = expiryDate;
}
public String getWalletId() {
	return walletId;
}
public void setWalletId(String walletId) {
	this.walletId = walletId;
}
public ArrayList<String> getDocumentArray() {
	return documentArray;
}
public void setDocumentArray(ArrayList<String> documentArray) {
	this.documentArray = documentArray;
}



}

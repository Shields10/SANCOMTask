package com.ppwallet.model;

public class MerchTopUpTransactions {
	
String txnCode;
String custWallelId;
String referenceCode;   //'Billercode- Biller, BankrefNo - Bank, Mpesa refNo -  Mpesa'
String relationshipNo;
String sysReference;
String txnAmount;
String topupMode;  //'B - Bank, M- Merchant, P- Mpesa'
String location;
String txnCurrencyId;
String txndatetime; 


public String getTxnCode() {
	return txnCode;
}
public void setTxnCode(String txnCode) {
	this.txnCode = txnCode;
}
public String getCustWallelId() {
	return custWallelId;
}
public void setCustWallelId(String custWallelId) {
	this.custWallelId = custWallelId;
}
public String getReferenceCode() {
	return referenceCode;
}
public void setReferenceCode(String referenceCode) {
	this.referenceCode = referenceCode;
}
public String getRelationshipNo() {
	return relationshipNo;
}
public void setRelationshipNo(String relationshipNo) {
	this.relationshipNo = relationshipNo;
}
public String getSysReference() {
	return sysReference;
}
public void setSysReference(String sysReference) {
	this.sysReference = sysReference;
}
public String getTxnAmount() {
	return txnAmount;
}
public void setTxnAmount(String txnAmount) {
	this.txnAmount = txnAmount;
}
public String getTopupMode() {
	return topupMode;
}
public void setTopupMode(String topupMode) {
	this.topupMode = topupMode;
}
public String getLocation() {
	return location;
}
public void setLocation(String location) {
	this.location = location;
}
public String getTxnCurrencyId() {
	return txnCurrencyId;
}
public void setTxnCurrencyId(String txnCurrencyId) {
	this.txnCurrencyId = txnCurrencyId;
}
public String getTxndatetime() {
	return txndatetime;
}
public void setTxndatetime(String txndatetime) {
	this.txndatetime = txndatetime;
}



	
}

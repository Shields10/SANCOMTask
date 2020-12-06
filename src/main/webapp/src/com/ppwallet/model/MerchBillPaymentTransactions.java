package com.ppwallet.model;

public class MerchBillPaymentTransactions {

	String transactionCode;
	String payType;
	String relationshipId;
	String assetId;  //'Wallet id for W, else TokenId for A and C of initiator of payment'
	String billerCode;
	String custReference;
	String transAmount;
	String transCurrencyId;
	String dateTime;

	
	
	public String getTransactionCode() {
		return transactionCode;
	}
	public void setTransactionCode(String transactionCode) {
		this.transactionCode = transactionCode;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public String getRelationshipId() {
		return relationshipId;
	}
	public void setRelationshipId(String relationshipId) {
		this.relationshipId = relationshipId;
	}
	
	public String getBillerCode() {
		return billerCode;
	}
	public void setBillerCode(String billerCode) {
		this.billerCode = billerCode;
	}
	public String getCustReference() {
		return custReference;
	}
	public void setCustReference(String custReference) {
		this.custReference = custReference;
	}
	public String getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(String transAmount) {
		this.transAmount = transAmount;
	}
	public String getTransCurrencyId() {
		return transCurrencyId;
	}
	public void setTransCurrencyId(String transCurrencyId) {
		this.transCurrencyId = transCurrencyId;
	}

	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	
}

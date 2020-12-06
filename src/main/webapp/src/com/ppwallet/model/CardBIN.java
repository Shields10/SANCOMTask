package com.ppwallet.model;

public class CardBIN {
	String BIN;
	String currencyId; 
	String cardType; 
	String issuingBankName;
	String issuingAccountNo; 
	String issuingBankAccountName; 
	String issuingBankRoutingCode;
	String issuingBankSwiftCode; 
	String interchangeRateVariable; 
	String interchangeRateFixed;
	String bankInterchageShare; 
	String bankSettlementCustoffTime;	
	String binStatus;

	public String getBIN() {
		return BIN;
	}
	public void setBIN(String bIN) {
		BIN = bIN;
	}
	public String getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getIssuingBankName() {
		return issuingBankName;
	}
	public void setIssuingBankName(String issuingBankName) {
		this.issuingBankName = issuingBankName;
	}
	public String getIssuingAccountNo() {
		return issuingAccountNo;
	}
	public void setIssuingAccountNo(String issuingAccountNo) {
		this.issuingAccountNo = issuingAccountNo;
	}
	public String getIssuingBankAccountName() {
		return issuingBankAccountName;
	}
	public void setIssuingBankAccountName(String issuingBankAccountName) {
		this.issuingBankAccountName = issuingBankAccountName;
	}
	public String getIssuingBankRoutingCode() {
		return issuingBankRoutingCode;
	}
	public void setIssuingBankRoutingCode(String issuingBankRoutingCode) {
		this.issuingBankRoutingCode = issuingBankRoutingCode;
	}
	public String getIssuingBankSwiftCode() {
		return issuingBankSwiftCode;
	}
	public void setIssuingBankSwiftCode(String issuingBankSwiftCode) {
		this.issuingBankSwiftCode = issuingBankSwiftCode;
	}
	public String getInterchangeRateVariable() {
		return interchangeRateVariable;
	}
	public void setInterchangeRateVariable(String interchangeRateVariable) {
		this.interchangeRateVariable = interchangeRateVariable;
	}
	public String getInterchangeRateFixed() {
		return interchangeRateFixed;
	}
	public void setInterchangeRateFixed(String interchangeRateFixed) {
		this.interchangeRateFixed = interchangeRateFixed;
	}
	public String getBankInterchageShare() {
		return bankInterchageShare;
	}
	public void setBankInterchageShare(String bankInterchageShare) {
		this.bankInterchageShare = bankInterchageShare;
	}
	public String getBankSettlementCustoffTime() {
		return bankSettlementCustoffTime;
	}
	public void setBankSettlementCustoffTime(String bankSettlementCustoffTime) {
		this.bankSettlementCustoffTime = bankSettlementCustoffTime;
	}
	public String getBinStatus() {
		return binStatus;
	}
	public void setBinStatus(String binStatus) {
		this.binStatus = binStatus;
	}
	
	
}

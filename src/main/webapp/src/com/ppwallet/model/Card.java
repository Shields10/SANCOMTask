package com.ppwallet.model;

public class Card {
	
	String cardNumber;
	String templateId;
	String productId;
	String bin;
	String cycleDay;
	String doe;
	String cvv2;
	String blockCodeId;
	String createdOn;
	
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getBin() {
		return bin;
	}
	public void setBin(String bin) {
		this.bin = bin;
	}
	public String getCycleDay() {
		return cycleDay;
	}
	public void setCycleDay(String cycleDay) {
		this.cycleDay = cycleDay;
	}
	public String getDoe() {
		return doe;
	}
	public void setDoe(String doe) {
		this.doe = doe;
	}
	public String getCvv2() {
		return cvv2;
	}
	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}
	public String getBlockCodeId() {
		return blockCodeId;
	}
	public void setBlockCodeId(String blockCodeId) {
		this.blockCodeId = blockCodeId;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

}

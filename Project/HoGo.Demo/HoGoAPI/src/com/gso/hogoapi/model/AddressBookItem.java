package com.gso.hogoapi.model;

public class AddressBookItem {
	
	private String id;
	private String firstName;
	private boolean isSelected;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLastmodifyDate() {
		return lastmodifyDate;
	}
	public void setLastmodifyDate(String lastmodifyDate) {
		this.lastmodifyDate = lastmodifyDate;
	}
	public String getBeUserId() {
		return beUserId;
	}
	public void setBeUserId(String beUserId) {
		this.beUserId = beUserId;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	private String middleName;
	private String lastName;
	private String lastmodifyDate;
	private String beUserId;
	private String company;
	private String email;

}

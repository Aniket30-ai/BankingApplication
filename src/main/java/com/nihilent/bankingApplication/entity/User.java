package com.nihilent.bankingApplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class User {

	
	@Id
	private Long mobileNumber;
	
	private String userId;
	
	private String userName;
	
	private String gender;
	
	private String address;
	
	@Column(name = "PAN")
	private String panCard;
	
	private String password;
	
	private String emailID;
	
}

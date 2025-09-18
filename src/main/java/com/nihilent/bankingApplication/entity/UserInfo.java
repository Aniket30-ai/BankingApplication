package com.nihilent.bankingApplication.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

public class UserInfo {

	
	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long mobileNumber;

//	@Column(name = "id")
	
	
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	

	private String name;

	private String emailId;

	private String gender;

	private String password;

	private String address;

	@Enumerated(value = EnumType.STRING)
	private Roles role;
	
	
	
	
	
	
}

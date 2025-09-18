package com.nihilent.bankingApplication.dto;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.nihilent.bankingApplication.entity.Roles;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;



//@Table(name = "usercredentials")
public class AuthRequest {

//	@NotNull(message = "{customer.emailId.notPresent}")
	
	
	private String username;

//	@NotNull(message = "{customer.password.notPresent}")
	private String password;
	
	
	private Roles roles;
	
	
	@UpdateTimestamp
	private LocalDateTime loginTime;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "AuthRequest [username=" + username + ", password=" + password + "]";
	}

}

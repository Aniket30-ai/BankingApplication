package com.nihilent.bank.dto;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.validation.constraints.NotNull;

public class AuthRequest {

	@NotNull(message = "{customer.username.notPresent}")
	private String username;

	@NotNull(message = "{customer.password.notPresent}")
	private String password;

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

}

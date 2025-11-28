package com.nihilent.bank.dto;

import com.nihilent.bank.entity.Roles;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CustomerDto {

	@NotNull(message = "{customer.mobileNumber.notPresent}")
	@Min(value = 1000000000L, message = "{customer.mobileNumber.invalid}")
	@Max(value = 9999999999L, message = "{customer.mobileNumber.invalid}")
	private Long mobileNumber;

	private String customerId;

	@NotNull(message = "{customer.name.notPresent}")
	@Pattern(regexp = "^[A-Z][a-z]+$", message = "{customer.name.invalid}")
	private String name;

	@NotNull(message = "{customer.emailId.notPresent}")
	@Pattern(regexp = "^[a-z]+\\d*@[a-z]+\\.[a-z]{2,}$", message = "{customer.emailId.invalid}")
	private String emailId;

	@NotNull(message = "{customer.password.notPresent}")
	private String password;

	@NotNull(message = "{customer.address.notPresent}")
	@NotBlank(message = "{customer.address.notBlank}")
	private String address;

	@NotNull(message = "{customer.gender.notPresent}")
	@Pattern(regexp = "Male|Female|Other", message = "{customer.gender.invalid}")
	private String gender;

	private Roles roles;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Roles getRoles() {
		return roles;
	}

	public Long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(Long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setRoles(Roles roles) {
		this.roles = roles;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}

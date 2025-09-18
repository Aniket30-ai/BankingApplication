package com.nihilent.bankingApplication.dto;

import com.nihilent.bankingApplication.entity.Roles;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CustomerDto {

	@NotNull(message = "{customer.mobileNumber.notPresent}")
//	@Pattern(regexp = "[0-9]{10}",message = "{customer.mobileNumber.invalid}")
	
	private Long mobileNumber;
	
	@NotNull(message = "{customer.customerId.notPresent}")
//	@Pattern(regexp = "[0-9]{10}",message = "{customer.customerId.invalid}")
	private String customerId;

	
	@NotNull(message = "{customer.name.notPresent}")
//	@Pattern(regexp = "",message = "{customer.name.invalid}")
	private String name;

	@NotNull(message = "{customer.emailId.notPresent}")
//	@Email()
	private String emailId;

	@NotNull(message = "{customer.password.notPresent}")
	private String password;
	
	@NotNull(message = "{customer.address.notPresent}")
	private String address;

	@NotNull(message = "{customer.gender.notPresent}")
	@Pattern(regexp = "Male|Female|Other",message = "{customer.gender.invalid}")
	private String gender;
	
	@NotNull(message = "{customer.role.notPresent}")
//	@Pattern(regexp = "Admin|User",message ="{customer.role.invalid}")
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

	@Override
	public String toString() {
		return "CustomerDto [mobileNumber=" + mobileNumber + ", customerId=" + customerId + ", name=" + name
				+ ", emailId=" + emailId + ", password=" + password + ", address=" + address + ", gender=" + gender
				+ ", roles=" + roles + "]";
	}

}

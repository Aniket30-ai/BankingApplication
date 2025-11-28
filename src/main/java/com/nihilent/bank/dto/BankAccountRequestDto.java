package com.nihilent.bank.dto;

import java.time.LocalDate;

import com.nihilent.bank.entity.AccountRequestStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BankAccountRequestDto {


	
	private Long accountId;

	@NotBlank(message = "{account.applicantName.notPresent}")
	@Size(min = 2, max = 50, message = "{account.applicantName.size}")
	private String applicantName;

	@NotNull(message = "{account.mobileNumber.notPresent}")
	@Min(value = 1000000000L, message = "{account.mobileNumber.invalid}")
	@Max(value = 9999999999L, message = "{account.mobileNumber.invalid}")
	private Long mobileNumber;

	@NotBlank(message = "{account.accountType.notPresent}")
	private String accountType;

	
	private LocalDate applicationDate;


	private AccountRequestStatus status;

	// getters and setters

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public Long getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(Long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public LocalDate getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(LocalDate applicationDate) {
		this.applicationDate = applicationDate;
	}

	public AccountRequestStatus getStatus() {
		return status;
	}

	public void setStatus(AccountRequestStatus status) {
		this.status = status;
	}

}

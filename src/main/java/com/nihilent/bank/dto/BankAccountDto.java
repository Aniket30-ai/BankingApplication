package com.nihilent.bank.dto;

import java.time.LocalDate;

import com.nihilent.bank.entity.AccountStatus;
import com.nihilent.bank.entity.AccountType;

import jakarta.validation.constraints.NotNull;

public class BankAccountDto {

	
	private Long accountNumber;

	
	private String bankName;

	
	private String ifscCode;

	private AccountType accountType;

	@NotNull(message = "{bankAccount.balance.notPresent}")
	private Double balance;

	@NotNull(message = "{bankAccount.panCard.notPresent}")
	private String panCard;

	@NotNull(message = "{bankAccount.accountStatus.notPresent}")
	private AccountStatus accountStatus;

	private LocalDate openingDate;

	@NotNull(message = "{bankAccount.customer.notPresent}")

	private CustomerDto customerDto;

	private String adharCard;

	private LocalDate dateOfBirth;

	public String getAdharCard() {
		return adharCard;
	}

	public void setAdharCard(String adharCard) {
		this.adharCard = adharCard;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public String getPanCard() {
		return panCard;
	}

	public void setPanCard(String panCard) {
		this.panCard = panCard;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public LocalDate getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(LocalDate openingDate) {
		this.openingDate = openingDate;
	}

	public CustomerDto getCustomerDto() {
		return customerDto;
	}

	public void setCustomerDto(CustomerDto customerDto) {
		this.customerDto = customerDto;
	}

	@Override
	public String toString() {
		return "BankAccountDto [accountNumber=" + accountNumber + ", bankName=" + bankName + ", ifscCode=" + ifscCode
				+ ", accountType=" + accountType + ", balance=" + balance + ", panCard=" + panCard + ", accountStatus="
				+ accountStatus + ", openingDate=" + openingDate + ", customerDto=" + customerDto + "]";
	}

}

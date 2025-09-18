package com.nihilent.bankingApplication.dto;

import com.nihilent.bankingApplication.entity.AccountType;

public class DigitalBankAccountDto {

	
	
	private String digitalBankId;

	private CustomerDto customerDto;

	private BankAccountDto bankAccountDto;

	private AccountType accountType;
	
	 private byte[] qrCodeImage;

	public CustomerDto getCustomerDto() {
		return customerDto;
	}

	public void setCustomerDto(CustomerDto customerDto) {
		this.customerDto = customerDto;
	}

	public BankAccountDto getBankAccountDto() {
		return bankAccountDto;
	}

	public void setBankAccountDto(BankAccountDto bankAccountDto) {
		this.bankAccountDto = bankAccountDto;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getDigitalBankId() {
		return digitalBankId;
	}

	public void setDigitalBankId(String digitalBankId) {
		this.digitalBankId = digitalBankId;
	}
	
	

	public byte[] getQrCodeImage() {
		return qrCodeImage;
	}

	public void setQrCodeImage(byte[] qrCodeImage) {
		this.qrCodeImage = qrCodeImage;
	}

	@Override
	public String toString() {
		return "DigitalBankAccountDto [digitalBankId=" + digitalBankId + ", customerDto=" + customerDto
				+ ", bankAccountDto=" + bankAccountDto + ", accountType=" + accountType + "]";
	}



}

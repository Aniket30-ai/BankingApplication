package com.nihilent.bankingApplication.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class DigitalBankAccount {

	@Id
	private String digitalBankId;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "mobile_number", unique = true)
	private Customer customer;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "account_number", unique = true)
	private BankAccount bankAccount;

	private AccountType accountType;

	@Lob
	@Column(columnDefinition = "LONGBLOB") // for MySQL, or use appropriate blob type
	private byte[] qrCodeImage;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
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

}

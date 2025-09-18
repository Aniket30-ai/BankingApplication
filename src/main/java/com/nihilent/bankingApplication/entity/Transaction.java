package com.nihilent.bankingApplication.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Transaction {

	@Id
	private String transactionId;

	private String modeOfTransaction;
//	private Long receivingMobileNumber;
	private Long receivingAccountNumber;
	private Double amount;
//	private Long senderMobileNumber;
	private Long senderAccountNumber;
	private String remark;
	private String transactionType;
	
	private String status;
	private String errorMesssage;

	
	private Double closingBalance;
	
	
	private Double credit;
	
	private Double debit;
	
	
	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	public Double getDebit() {
		return debit;
	}

	public void setDebit(Double debit) {
		this.debit = debit;
	}

	@UpdateTimestamp
	private LocalDateTime transactionTime;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getModeOfTransaction() {
		return modeOfTransaction;
	}

	public void setModeOfTransaction(String modeOfTransaction) {
		this.modeOfTransaction = modeOfTransaction;
	}

	public Long getReceivingAccountNumber() {
		return receivingAccountNumber;
	}

	public void setReceivingAccountNumber(Long receivingAccountNumber) {
		this.receivingAccountNumber = receivingAccountNumber;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getSenderAccountNumber() {
		return senderAccountNumber;
	}

	public void setSenderAccountNumber(Long senderAccountNumber) {
		this.senderAccountNumber = senderAccountNumber;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public LocalDateTime getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(LocalDateTime transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMesssage() {
		return errorMesssage;
	}

	public void setErrorMesssage(String errorMesssage) {
		this.errorMesssage = errorMesssage;
	}

	public Transaction(String transactionId, String modeOfTransaction, Long receivingAccountNumber, Double amount,
			Long senderAccountNumber, String remark) {
		super();
		this.transactionId = transactionId;
		this.modeOfTransaction = modeOfTransaction;
		this.receivingAccountNumber = receivingAccountNumber;
		this.amount = amount;
		this.senderAccountNumber = senderAccountNumber;
		this.remark = remark;
	}

	public Transaction() {
//		super();
		// TODO Auto-generated constructor stub
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public Double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(Double closingBalance) {
		this.closingBalance = closingBalance;
	}


	
	

}
package com.nihilent.bankingApplication.dto;

import java.time.LocalDateTime;

public class TransactionDto {

	private String transactionId;

//	@NotNull(message = "{}")
	private String modeOfTransaction;
//	private Long receivingMobileNumber;
	private Long receivingAccountNumber;
	private Double amount;
//	private Long senderMobileNumber;
	private Long senderAccountNumber;
	private String remark;
	private LocalDateTime transactionTime;
	private Double closingBalance;
	
	private String transactionType;
	
	

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

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

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

	public Double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(Double closingBalance) {
		this.closingBalance = closingBalance;
	}

	
	
	
}
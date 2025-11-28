package com.nihilent.bank.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class TransactionDto {

	private String transactionId;

	@NotNull(message = "{transaction.modeOfTransaction.notPresent}")
	private String modeOfTransaction;

	@NotNull(message = "{transaction.receivingAccountNumber.notPresent}")
	private Long receivingAccountNumber;

	@NotNull(message = "{transaction.amount.notPresent}")
	@Positive(message = "{transaction.amount.positive}")
	private Double amount;

	@NotNull(message = "{transaction.senderAccountNumber.notPresent}")
	private Long senderAccountNumber;

	@Size(max = 255, message = "{transaction.remark.size}")
	private String remark;

	private LocalDateTime transactionTime;

	private Double closingBalance;

	@NotNull(message = "{transaction.transactionType.notPresent}")
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
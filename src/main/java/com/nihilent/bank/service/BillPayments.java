package com.nihilent.bank.service;

import com.nihilent.bank.exception.NihilentBankException;

public interface BillPayments {

	public String mobileRecharge(Long mobileNumber, Double amount, String remark, Long accountNumber)
			throws NihilentBankException;

	public String dthRecharge(Long subscriberId, Double amount, String remark, Long accountNumber)
			throws NihilentBankException;

	public String electricityBill(Long consumerNumber, Double amount, String remark, Long accountNumber)
			throws NihilentBankException;

}

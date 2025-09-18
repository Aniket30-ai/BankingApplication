package com.nihilent.bankingApplication.service;

import java.util.List;

import com.nihilent.bankingApplication.dto.BankAccountDto;
import com.nihilent.bankingApplication.exception.NihilentBankException;

public interface BankAccountService {

	public Long createAccount(BankAccountDto accountDto) throws NihilentBankException;

	public BankAccountDto getAccountDetails(Long accountNumber) throws NihilentBankException;

	public List<BankAccountDto> showAllAcounts(Long mobileNumber) throws NihilentBankException;

	public Double getBalance(Long accountNumber) throws NihilentBankException;
	public List<BankAccountDto> showAllAcountsDetails() throws NihilentBankException;
	
	public BankAccountDto getAccountDetail(Long mobileNumber) throws NihilentBankException;
	public String accountDelete(Long accountNumber) throws NihilentBankException;

}

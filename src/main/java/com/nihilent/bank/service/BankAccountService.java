package com.nihilent.bank.service;

import java.util.List;

import com.nihilent.bank.dto.BankAccountDto;
import com.nihilent.bank.dto.BankAccountRequestDto;
import com.nihilent.bank.entity.AccountRequestStatus;
import com.nihilent.bank.entity.BankAccountRequest;
import com.nihilent.bank.exception.NihilentBankException;

public interface BankAccountService {

	public Long createAccount(BankAccountDto accountDto) throws NihilentBankException;

	public BankAccountDto getAccountDetails(Long accountNumber) throws NihilentBankException;

	public List<BankAccountDto> showAllAcounts(Long mobileNumber) throws NihilentBankException;

	public Double getBalance(Long accountNumber) throws NihilentBankException;

	public List<BankAccountDto> showAllAcountsDetails() throws NihilentBankException;

	public BankAccountDto getAccountDetail(Long mobileNumber) throws NihilentBankException;

	public String accountDelete(Long accountNumber) throws NihilentBankException;

	public String applyBankAccount(Long mobileNumber, String name, String accountType) throws NihilentBankException;

	public List<BankAccountRequest> getAllAccountRequest() throws NihilentBankException;

	public BankAccountRequest updateAccountStatus(Long accountId, AccountRequestStatus status)
			throws NihilentBankException;

	public BankAccountRequestDto getAccountStatus(Long mobileNumber) throws NihilentBankException;

}

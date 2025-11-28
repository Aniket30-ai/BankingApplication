package com.nihilent.bank.service;

import java.util.List;

import com.nihilent.bank.entity.Loan;
import com.nihilent.bank.entity.LoanStatus;
import com.nihilent.bank.exception.NihilentBankException;

public interface LoanService {

	public String applyLoan(Loan loan) throws NihilentBankException;

	public List<Loan> getLoansByAccount(Long accountNumber) throws NihilentBankException;

	public List<Loan> getAllLoans() throws NihilentBankException;

	public Loan updateLoanStatus(Long loanId, LoanStatus status) throws NihilentBankException;
}

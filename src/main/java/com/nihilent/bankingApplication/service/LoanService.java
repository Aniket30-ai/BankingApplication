package com.nihilent.bankingApplication.service;

import java.util.List;

import com.nihilent.bankingApplication.entity.Loan;
import com.nihilent.bankingApplication.entity.LoanStatus;
import com.nihilent.bankingApplication.exception.NihilentBankException;

public interface LoanService {

	public String applyLoan(Loan loan) throws NihilentBankException;

	public List<Loan> getLoansByAccount(Long accountNumber) throws NihilentBankException;

	public List<Loan> getAllLoans() throws NihilentBankException;

	public Loan updateLoanStatus(Long loanId, LoanStatus status) throws NihilentBankException;
}

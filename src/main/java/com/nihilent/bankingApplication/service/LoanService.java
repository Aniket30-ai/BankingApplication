package com.nihilent.bankingApplication.service;

import java.util.List;

import com.nihilent.bankingApplication.entity.Loan;
import com.nihilent.bankingApplication.entity.LoanStatus;

public interface LoanService {

	public String applyLoan(Loan loan);

	public List<Loan> getLoansByAccount(Long accountNumber);

	public List<Loan> getAllLoans();

	public Loan updateLoanStatus(Long loanId, LoanStatus status);
}

package com.nihilent.bankingApplication.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bankingApplication.entity.Loan;
import com.nihilent.bankingApplication.entity.LoanStatus;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.service.LoanService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping(value = "NihilentBank")
//@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class LoanController {

	private final LoanService loanService;

	public LoanController(LoanService loanService) {
		this.loanService = loanService;
	}

	@PostMapping("/applyLoan")
	public ResponseEntity<String> applyLoan(@RequestBody Loan loan) throws NihilentBankException {

		String applyLoan = loanService.applyLoan(loan);

		return new ResponseEntity<String>(applyLoan, HttpStatus.CREATED);
	}

	@GetMapping("/user/{accountNumber}")
	public ResponseEntity<List<Loan>> getLoansByAccount(
			@PathVariable @Min(value = 10000000L, message = "{bankAccount.accountNumber.invalid}") @Max(value = 99999999L, message = "{bankAccount.accountNumber.invalid}") Long accountNumber) throws NihilentBankException {
		List<Loan> loansByAccount = loanService.getLoansByAccount(accountNumber);

		return new ResponseEntity<List<Loan>>(loansByAccount, HttpStatus.OK);
	}

	@GetMapping("/all")
	public ResponseEntity<List<Loan>> getAllLoans() throws NihilentBankException {
		List<Loan> allLoans = loanService.getAllLoans();

		return new ResponseEntity<List<Loan>>(allLoans, HttpStatus.OK);
	}

	@PutMapping("/update/{loanId}/{status}")
	public ResponseEntity<Loan> updateLoanStatus(@PathVariable Long loanId, @PathVariable LoanStatus status) throws NihilentBankException {
		Loan updateLoanStatus = loanService.updateLoanStatus(loanId, status);

		return new ResponseEntity<Loan>(updateLoanStatus, HttpStatus.OK);
	}
}

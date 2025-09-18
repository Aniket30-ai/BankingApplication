package com.nihilent.bankingApplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.nihilent.bankingApplication.serviceImpl.LoanServiceImpl;

@RestController
@RequestMapping(value = "NihilentBank")
@CrossOrigin(origins = "http://localhost:4200") 
public class LoanApi {

	@Autowired
	private LoanServiceImpl loanService;

	@PostMapping("/applyLoan")
	public ResponseEntity<String> applyLoan(@RequestBody Loan loan) {

		String applyLoan = loanService.applyLoan(loan);

		return new ResponseEntity<String>(applyLoan, HttpStatus.OK);
	}

	@GetMapping("/user/{accountNumber}")
	public List<Loan> getLoansByAccount(@PathVariable Long accountNumber) {
		return loanService.getLoansByAccount(accountNumber);
	}

	@GetMapping("/all")
	public List<Loan> getAllLoans() {
		return loanService.getAllLoans();
	}

	@PutMapping("/update/{loanId}/{status}")
	public Loan updateLoanStatus(@PathVariable Long loanId, @PathVariable LoanStatus status) {
		return loanService.updateLoanStatus(loanId, status);
	}
}

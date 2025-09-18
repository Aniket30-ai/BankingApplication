package com.nihilent.bankingApplication.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nihilent.bankingApplication.Validation.LoanValidation;
import com.nihilent.bankingApplication.dto.TransactionDto;
import com.nihilent.bankingApplication.entity.BankAccount;
import com.nihilent.bankingApplication.entity.Loan;
import com.nihilent.bankingApplication.entity.LoanStatus;
import com.nihilent.bankingApplication.entity.LoanType;
import com.nihilent.bankingApplication.entity.Transaction;
import com.nihilent.bankingApplication.repository.BankAccountRepository;
import com.nihilent.bankingApplication.repository.LoanRepository;
import com.nihilent.bankingApplication.repository.TransactionRepository;
import com.nihilent.bankingApplication.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private LoanValidation loanValidation;

	@Autowired
	private BankAccountRepository bankAccountRepository;
	
	
	@Autowired
	private TransactionRepository transactionRepository;

	@Override
	public String applyLoan(Loan loanDto) {
		// TODO Auto-generated method stub

		loanDto.setApplicationDate(LocalDate.now());
		loanDto.setStatus(LoanStatus.PENDING); // Set status

		switch (loanDto.getLoanType()) {
		case HOME -> {

//			loanValidation.validateHomeLoan(loanDto);
			calculateLoanDetails(loanDto, "HOME");
		}
		case PERSONAL -> {
//			loanValidation.validatePersonalLoan(loanDto);
			calculateLoanDetails(loanDto, "PERSONAL");
		}
		case EDUCATION -> {
//			loanValidation.validateEducationLoan(loanDto);
			calculateLoanDetails(loanDto, "EDUCATION");
		}
		default -> throw new IllegalArgumentException("Unsupported loan type");
		}

		loanRepository.save(loanDto);

		return "Loan apply sucess";

	}

	@Override
	public Loan updateLoanStatus(Long loanId, LoanStatus status) {
		// TODO Auto-generated method stub

		Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new RuntimeException("Loan not found"));

		if (loan.getStatus() != LoanStatus.PENDING) {
			throw new IllegalStateException("Loan is already processed");
		}

		// 1. Update loan status
		loan.setStatus(LoanStatus.APPROVED);

		if (loan.getStatus() == LoanStatus.APPROVED) {
			// 2. Credit amount to linked bank account
			BankAccount account = loan.getBankAccount();
			double newBalance = account.getBalance() + loan.getLoanAmount();
			account.setBalance(newBalance);

			BankAccount save = bankAccountRepository.save(account); // optional, but good practice
			
			
		Transaction transaction = new Transaction();
		
		
		transaction.setAmount(loan.getLoanAmount());
		
		transaction.setCredit(loan.getLoanAmount());
		
		transaction.setClosingBalance(newBalance);
		Supplier<Integer> randomNumber = () -> (int) (Math.random() * 900000) + 100000;
		Integer integer1 = randomNumber.get();
	
		String transactionId1 = "TNX" + integer1.toString();
		
		transaction.setTransactionId(transactionId1);
		transaction.setModeOfTransaction("Loan Amount");
		transaction.setReceivingAccountNumber(loan.getBankAccount().getAccountNumber());
		transaction.setTransactionType("CREDIT");
		Long accountNumber = loan.getBankAccount().getAccountNumber();
		System.out.println(accountNumber);
		transactionRepository.save(transaction);
		System.out.println("Transaction Saved");
			
		}

		loanRepository.save(loan);

		loan.setStatus(status);
		return loanRepository.save(loan);

	}

	@Override
	public List<Loan> getLoansByAccount(Long accountNumber) {

		List<Loan> loans = loanRepository.findLoansByAccountNumber(accountNumber);

		List<Loan> sortedLoans = loans.stream() // optional
																										// filtering
				.sorted(Comparator.comparing(Loan::getApplicationDate).reversed()).collect(Collectors.toList());

		return sortedLoans;
	}

	@Override
	public List<Loan> getAllLoans() {

		List<Loan> list = loanRepository.findAll();

		List<Loan> loans = list.stream().filter(loan -> loan.getStatus() == LoanStatus.PENDING)
				.sorted(Comparator.comparing(Loan::getApplicationDate).reversed()) // Sort by recent date first
				.map(loan -> {
					Loan l = new Loan();
					l.setApplicantName(loan.getApplicantName());
					l.setLoanType(loan.getLoanType());
					l.setLoanId(loan.getLoanId());
					l.setLoanAmount(loan.getLoanAmount());
					l.setStatus(loan.getStatus());
					l.setApplicationDate(loan.getApplicationDate()); // ✅ include this so sorting makes sense
					return l;
				}).collect(Collectors.toList());
		loans.forEach(l -> System.out.println(l.getLoanId() + " - " + l.getApplicationDate()));

		return loans;

	}

	private void calculateLoanDetails(Loan loan, String type) {
		int years = loan.getTenureMonths() / 12;
		double rate;

		switch (type) {
		case "HOME" -> {
			if (years <= 5)
				rate = 7.75;
			else if (years <= 15)
				rate = 9.5;
			else if (years <= 25)
				rate = 10.75;
			else
				rate = 12.0;
		}
		case "PERSONAL" -> {
			if (years <= 2)
				rate = 11.0;
			else if (years <= 4)
				rate = 13.5;
			else
				rate = 16.0;
		}
		case "EDUCATION" -> {
			if (years <= 3)
				rate = 8.0;
			else if (years <= 6)
				rate = 9.5;
			else
				rate = 11.0;
		}
		default -> throw new IllegalArgumentException("Unknown loan type");
		}

		loan.setInterestRate(rate);

		// EMI Formula
		double principal = loan.getLoanAmount();
		double monthlyRate = rate / 12 / 100;
		int months = loan.getTenureMonths();

		double emi = (principal * monthlyRate * Math.pow(1 + monthlyRate, months))
				/ (Math.pow(1 + monthlyRate, months) - 1);

		loan.setEmiAmount(Math.round(emi * 100.0) / 100.0); // rounded to 2 decimal places
	}

}

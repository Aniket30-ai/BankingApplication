package com.nihilent.bank.serviceimpl;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.Loan;
import com.nihilent.bank.entity.LoanStatus;
import com.nihilent.bank.entity.Transaction;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.repository.LoanRepository;
import com.nihilent.bank.repository.TransactionRepository;
import com.nihilent.bank.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService {

	private final LoanRepository loanRepository;

	private final BankAccountRepository bankAccountRepository;

	private final TransactionRepository transactionRepository;

	public LoanServiceImpl(LoanRepository loanRepository, BankAccountRepository bankAccountRepository,
			TransactionRepository transactionRepository) {

		this.loanRepository = loanRepository;

		this.bankAccountRepository = bankAccountRepository;
		this.transactionRepository = transactionRepository;
	}

	@Value("${LoanService.Loan_Type}")
	private String loanTypeInvalid;

	@Value("${LoanService.Loan_Success}")
	private String loanSuccess;

	@Value("${LoanService.Loan_NotFound}")
	private String loanNotFound;

	@Value("${LoanService.Loan_Processed}")
	private String loanProcessed;

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String applyLoan(Loan loanDto) throws NihilentBankException {

		loanDto.setApplicationDate(LocalDate.now());
		loanDto.setStatus(LoanStatus.PENDING); // Set status

		switch (loanDto.getLoanType()) {
		case HOME ->

			calculateLoanDetails(loanDto, "HOME");

		case PERSONAL ->

			calculateLoanDetails(loanDto, "PERSONAL");

		case EDUCATION ->

			calculateLoanDetails(loanDto, "EDUCATION");

		default -> throw new NihilentBankException(loanTypeInvalid);
		}

		loanRepository.save(loanDto);

		return loanSuccess;

	}

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public Loan updateLoanStatus(Long loanId, LoanStatus status) throws NihilentBankException {

		Loan loan = loanRepository.findById(loanId).orElseThrow(() -> new NihilentBankException(loanNotFound));

		if (loan.getStatus() != LoanStatus.PENDING) {
			throw new NihilentBankException(loanProcessed);
		}

		// 1. Update loan status
		loan.setStatus(LoanStatus.APPROVED);

		if (loan.getStatus() == LoanStatus.APPROVED) {
			// 2. Credit amount to linked bank account
			BankAccount account = loan.getBankAccount();
			double newBalance = account.getBalance() + loan.getLoanAmount();
			account.setBalance(newBalance);

			bankAccountRepository.save(account); // optional, but good practice

			Transaction transaction = new Transaction();

			transaction.setAmount(loan.getLoanAmount());

			transaction.setCredit(loan.getLoanAmount());

			transaction.setClosingBalance(newBalance);

			SecureRandom secureRandom = new SecureRandom();

			// Define IntSupplier for a 6-digit number (100_000 to 999_999)
			IntSupplier randomNumber = () -> 100_000 + secureRandom.nextInt(900_000);

			// Get the random int value
			int integer1 = randomNumber.getAsInt();

			String transactionId1 = "TNX" + integer1;

			transaction.setTransactionId(transactionId1);
			transaction.setModeOfTransaction("Loan Amount");
			transaction.setRemark("Loan Amount");
			transaction.setReceivingAccountNumber(loan.getBankAccount().getAccountNumber());
			transaction.setTransactionType("CREDIT");
			loan.getBankAccount().getAccountNumber();
			transactionRepository.save(transaction);

		}

		loanRepository.save(loan);

		loan.setStatus(status);
		return loanRepository.save(loan);

	}

	@Override
	public List<Loan> getLoansByAccount(Long accountNumber) {

		List<Loan> loans = loanRepository.findLoansByAccountNumber(accountNumber);

		return loans.stream() // optional
								// filtering
				.sorted(Comparator.comparing(Loan::getApplicationDate).reversed())
				.collect(Collectors.toCollection(ArrayList::new));

	}

	@Override
	public List<Loan> getAllLoans() {

		List<Loan> list = loanRepository.findAll();

		return list.stream().filter(loan -> loan.getStatus() == LoanStatus.PENDING)
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
				}).collect(Collectors.toCollection(ArrayList::new));

	}

	private void calculateLoanDetails(Loan loan, String type) throws NihilentBankException {
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
		default -> throw new NihilentBankException(loanTypeInvalid);
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

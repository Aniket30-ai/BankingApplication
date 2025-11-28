package com.nihilent.bank.serviceimpl;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.function.IntSupplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.Transaction;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.repository.TransactionRepository;
import com.nihilent.bank.service.BillPayments;

@Service
public class BillPaymentsImpl implements BillPayments {

	private final TransactionRepository transactionRepository;

	private final BankAccountRepository accountRepository;
	
	private static final String DEBIT = "DEBIT";
	

	public BillPaymentsImpl(TransactionRepository transactionRepository, BankAccountRepository accountRepository) {

		this.transactionRepository = transactionRepository;
		this.accountRepository = accountRepository;
	}

	@Value("${BillPaymentService.Insufficient_Balance}")
	private String insufficientBalance;

	@Value("${BillPaymentService.Success}")
	private String success;
	
	@Value("${BankAccountService.Account_Empty}")
	private String accountNotFound;

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String mobileRecharge(Long mobileNumber, Double amount, String remark, Long accountNumber)
			throws NihilentBankException {

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		
	
		Double balance = byAccountNumber
		        .orElseThrow(() -> new RuntimeException(accountNotFound))
		        .getBalance();

		if (balance < amount) {
			throw new NihilentBankException(insufficientBalance);
		}

		BankAccount bankAccount = byAccountNumber
		        .orElseThrow(() -> new RuntimeException(accountNotFound));

		bankAccount.setBalance(balance - amount);
		BankAccount closingBalance = accountRepository.save(bankAccount);

		SecureRandom secureRandom = new SecureRandom();

		// Define IntSupplier for an integer between 100_000 and 999_999
		IntSupplier randomNumber = () -> 100_000 + secureRandom.nextInt(900_000);

		// Get the random int
		int integer1 = randomNumber.getAsInt();

		String transactionId1 = "TNX" + integer1;

		Transaction transaction = new Transaction();

		transaction.setTransactionId(transactionId1);
		transaction.setSenderAccountNumber(accountNumber);
		transaction.setReceivingAccountNumber(45872020l);
		transaction.setRemark(remark + mobileNumber);
		transaction.setTransactionType(DEBIT);
		transaction.setDebit(amount);
		transaction.setModeOfTransaction("Mobile Recharge");
		transaction.setAmount(amount);
		transaction.setClosingBalance(closingBalance.getBalance());

		transactionRepository.save(transaction);

		return success;
	}

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String dthRecharge(Long subscriberId, Double amount, String remark, Long accountNumber)
			throws NihilentBankException {

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		Double balance = byAccountNumber
		        .orElseThrow(() -> new RuntimeException(accountNotFound))
		        .getBalance();

		if (balance < amount) {
			throw new NihilentBankException(insufficientBalance);
		}

		BankAccount bankAccount = byAccountNumber
		        .orElseThrow(() -> new RuntimeException(accountNotFound));

		bankAccount.setBalance(balance - amount);
		BankAccount closingBalance = accountRepository.save(bankAccount);

		SecureRandom secureRandom = new SecureRandom();

		// Define IntSupplier for a 6-digit transaction number (100_000 to 999_999)
		IntSupplier randomNumber = () -> 100_000 + secureRandom.nextInt(900_000);

		// Get the transaction number
		int transactionNumber = randomNumber.getAsInt();

		String transactionId1 = "TNX" + transactionNumber;

		Transaction transaction = new Transaction();

		transaction.setTransactionId(transactionId1);
		transaction.setSenderAccountNumber(accountNumber);

		transaction.setRemark(remark + subscriberId);
		transaction.setTransactionType(DEBIT);
		transaction.setDebit(amount);
		transaction.setModeOfTransaction("DTH Recharge");
		transaction.setAmount(amount);
		transaction.setClosingBalance(closingBalance.getBalance());

		transactionRepository.save(transaction);

		return success;

	}

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String electricityBill(Long consumerNumber, Double amount, String remark, Long accountNumber)
			throws NihilentBankException {

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		Double balance = byAccountNumber
		        .orElseThrow(() -> new RuntimeException(accountNotFound))
		        .getBalance();

		if (balance < amount) {
			throw new NihilentBankException(insufficientBalance);
		}

		BankAccount bankAccount = byAccountNumber
		        .orElseThrow(() -> new RuntimeException(accountNotFound));

		bankAccount.setBalance(balance - amount);
		BankAccount closingBalance = accountRepository.save(bankAccount);

		SecureRandom secureRandom = new SecureRandom();

		// Define IntSupplier for a 6-digit transaction number (100_000 to 999_999)
		IntSupplier randomNumber = () -> 100_000 + secureRandom.nextInt(900_000);

		// Get the transaction number
		int transactionNumber = randomNumber.getAsInt();
		String transactionId1 = "TNX" + transactionNumber;

		Transaction transaction = new Transaction();

		transaction.setTransactionId(transactionId1);
		transaction.setSenderAccountNumber(accountNumber);

		transaction.setRemark(remark + consumerNumber);
		transaction.setTransactionType(DEBIT);
		transaction.setDebit(amount);
		transaction.setModeOfTransaction("Electricity Bill");
		transaction.setAmount(amount);
		transaction.setClosingBalance(closingBalance.getBalance());

		transactionRepository.save(transaction);

		return success;
	}

}

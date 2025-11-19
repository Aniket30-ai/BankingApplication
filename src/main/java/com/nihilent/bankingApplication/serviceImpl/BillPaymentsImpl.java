package com.nihilent.bankingApplication.serviceImpl;

import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bankingApplication.entity.BankAccount;
import com.nihilent.bankingApplication.entity.Transaction;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.BankAccountRepository;
import com.nihilent.bankingApplication.repository.TransactionRepository;
import com.nihilent.bankingApplication.service.BillPayments;

@Service
public class BillPaymentsImpl implements BillPayments {

	private final TransactionRepository transactionRepository;

	private final BankAccountRepository accountRepository;

	public BillPaymentsImpl(TransactionRepository transactionRepository, BankAccountRepository accountRepository) {

		this.transactionRepository = transactionRepository;
		this.accountRepository = accountRepository;
	}

	@Value("${BillPaymentService.Insufficient_Balance}")
	private String insufficientBalance;

	@Value("${BillPaymentService.Success}")
	private String success;

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String mobileRecharge(Long mobileNumber, Double amount, String remark, Long accountNumber)
			throws NihilentBankException {
		// TODO Auto-generated method stub

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		Double balance = byAccountNumber.get().getBalance();

		if (balance < amount) {
			throw new NihilentBankException(insufficientBalance);
		}

		BankAccount bankAccount = byAccountNumber.get();

		bankAccount.setBalance(balance - amount);
		BankAccount closingBalance = accountRepository.save(bankAccount);

		Supplier<Integer> randomNumber = () -> (int) (Math.random() * 900000) + 100000;
		Integer integer1 = randomNumber.get();

		String transactionId1 = "TNX" + integer1.toString();

		Transaction transaction = new Transaction();

		transaction.setTransactionId(transactionId1);
		transaction.setSenderAccountNumber(accountNumber);
        transaction.setReceivingAccountNumber(45872020l);
		transaction.setRemark(remark + mobileNumber);
		transaction.setTransactionType("DEBIT");
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
		// TODO Auto-generated method stub

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		Double balance = byAccountNumber.get().getBalance();

		if (balance < amount) {
			throw new NihilentBankException(insufficientBalance);
		}

		BankAccount bankAccount = byAccountNumber.get();

		bankAccount.setBalance(balance - amount);
		BankAccount closingBalance = accountRepository.save(bankAccount);

		Supplier<Integer> randomNumber = () -> (int) (Math.random() * 900000) + 100000;
		Integer integer1 = randomNumber.get();

		String transactionId1 = "TNX" + integer1.toString();

		Transaction transaction = new Transaction();

		transaction.setTransactionId(transactionId1);
		transaction.setSenderAccountNumber(accountNumber);

		transaction.setRemark(remark + subscriberId);
		transaction.setTransactionType("DEBIT");
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
		// TODO Auto-generated method stub

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		Double balance = byAccountNumber.get().getBalance();

		if (balance < amount) {
			throw new NihilentBankException(insufficientBalance);
		}

		BankAccount bankAccount = byAccountNumber.get();

		bankAccount.setBalance(balance - amount);
		BankAccount closingBalance = accountRepository.save(bankAccount);

		Supplier<Integer> randomNumber = () -> (int) (Math.random() * 900000) + 100000;
		Integer integer1 = randomNumber.get();

		String transactionId1 = "TNX" + integer1.toString();

		Transaction transaction = new Transaction();

		transaction.setTransactionId(transactionId1);
		transaction.setSenderAccountNumber(accountNumber);

		transaction.setRemark(remark + consumerNumber);
		transaction.setTransactionType("DEBIT");
		transaction.setDebit(amount);
		transaction.setModeOfTransaction("Electricity Bill");
		transaction.setAmount(amount);
		transaction.setClosingBalance(closingBalance.getBalance());

		transactionRepository.save(transaction);

		return success;
	}

}

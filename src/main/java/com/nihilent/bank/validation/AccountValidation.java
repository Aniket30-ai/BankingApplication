package com.nihilent.bank.validation;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.nihilent.bank.entity.AccountStatus;
import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;

@Component
public class AccountValidation {

	private final BankAccountRepository accountRepository;

	public AccountValidation(BankAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	public boolean accountNumberValidation(Long senderAccountNumber, Long receiverAccountNumber)
			throws NihilentBankException {

		Optional<BankAccount> sender = accountRepository.findByAccountNumber(senderAccountNumber);

		if (sender.isEmpty()) {
			throw new NihilentBankException("Invalid Sender Account Number");
		}

		Optional<BankAccount> receiver = accountRepository.findByAccountNumber(receiverAccountNumber);

		if (receiver.isEmpty()) {
			throw new NihilentBankException("Invalid receiver Account Number");
		}

		return true;
	}

	public boolean accountStatusValidation(Long accountNumber) throws NihilentBankException {

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		AccountStatus accountStatus = byAccountNumber.orElseThrow(() -> new RuntimeException("Account not found"))
				.getAccountStatus();

		if (accountStatus == AccountStatus.IN_ACTIVE) {
			throw new NihilentBankException("Invalid Account Number");
		}

		return true;
	}

	public boolean accountNumberLength(Long senderAccountNumber, Long receiverAccountNumber)
			throws NihilentBankException {

		if (String.valueOf(senderAccountNumber).length() > 8) {
			throw new NihilentBankException("Invalid Sender Account Number: exceed 7 digits");
		}
		if (String.valueOf(receiverAccountNumber).length() > 8) {
			throw new NihilentBankException("Invalid Receiver Account Number: exceed 7 digits");
		}

		return true;
	}

}

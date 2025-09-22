package com.nihilent.bankingApplication.Validation;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nihilent.bankingApplication.entity.AccountStatus;
import com.nihilent.bankingApplication.entity.BankAccount;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.BankAccountRepository;

@Component
public class AccountValidation {

	@Autowired
	private BankAccountRepository accountRepository;

	public boolean accountNumberValidation(Long senderAccountNumber, Long receiverAccountNumber)
			throws NihilentBankException {

		Optional<BankAccount> sender = accountRepository.findByAccountNumber(senderAccountNumber);

		BankAccount bankAccount = sender.orElseThrow(() -> new NihilentBankException("Invalid Sender Account Number"));

		Optional<BankAccount> receiver = accountRepository.findByAccountNumber(receiverAccountNumber);

		BankAccount bank = receiver.orElseThrow(() -> new NihilentBankException("Invalid receiver Account Number"));

		return true;
	}

	public boolean accountStatusValidation(Long accountNumber) throws NihilentBankException {

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		AccountStatus accountStatus = byAccountNumber.get().getAccountStatus();

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

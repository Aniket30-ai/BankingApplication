package com.nihilent.bank.serviceimpl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bank.entity.DigitalBankAccount;
import com.nihilent.bank.repository.DigitalBankRepository;
import com.nihilent.bank.service.DeactivateUpiIdService;

@Service
public class DeactivateUpiIdServiceImpl implements DeactivateUpiIdService {

	private DigitalBankRepository digitalBankRepository;

	public DeactivateUpiIdServiceImpl(DigitalBankRepository digitalBankRepository) {
		this.digitalBankRepository = digitalBankRepository;

	}

	@Override
	@Transactional
	public void deactivateUpiId(Long accountNumber) {
		Optional<DigitalBankAccount> byAccountNumber = digitalBankRepository.findByAccountNumber(accountNumber);

		if (byAccountNumber.isPresent()) {
			digitalBankRepository.deleteByAccountNumber(accountNumber);

		}
	}
}

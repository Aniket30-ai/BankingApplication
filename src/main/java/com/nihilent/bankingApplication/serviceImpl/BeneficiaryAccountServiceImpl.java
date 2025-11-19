package com.nihilent.bankingApplication.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bankingApplication.entity.BeneficiaryAccount;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.BeneficiaryAccountRepository;
import com.nihilent.bankingApplication.service.BeneficiaryAccountService;

@Service
public class BeneficiaryAccountServiceImpl implements BeneficiaryAccountService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	private final BeneficiaryAccountRepository accountRepository;

	public BeneficiaryAccountServiceImpl(BeneficiaryAccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Value("${BankAccountBeneficiary.present}")
	private String beneficiaryPresent;

	@Value("${BankAccountBeneficiary.addedSuccess}")
	private String beneficiarySuccess;

	@Value("${BankAccountBeneficiary.Not_Found}")
	private String beneficiaryNotFound;

	@Value("${BankAccountBeneficiary.Account_Delete}")
	private String beneficiaryAccountDelete;

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public BeneficiaryAccount addBeneficiary(BeneficiaryAccount beneficiary) throws NihilentBankException {
		Optional<BeneficiaryAccount> byAccountNumber = accountRepository
				.findByAccountNumber(beneficiary.getBankAccount().getAccountNumber());

		if (byAccountNumber.isPresent()) {
			logger.warn(beneficiaryPresent);
			throw new NihilentBankException(beneficiaryPresent);
		}
		BeneficiaryAccount save = accountRepository.save(beneficiary);

		logger.info(beneficiarySuccess);
		return save;
	}

	@Override
	public List<BeneficiaryAccount> getAllBeneficiaries() throws NihilentBankException {
		List<BeneficiaryAccount> allAccount = accountRepository.findAll();

		if (allAccount.isEmpty()) {
			logger.warn(beneficiaryNotFound);
			throw new NihilentBankException(beneficiaryNotFound);
		}
		return allAccount;
	}

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String deleteBeneficiayAccount(Long id) throws NihilentBankException {
		// TODO Auto-generated method stub

		Optional<BeneficiaryAccount> accountId = accountRepository.findById(id);

		if (accountId.isEmpty()) {
			logger.warn(beneficiaryNotFound);
			throw new NihilentBankException(beneficiaryNotFound);
		}

		accountRepository.deleteById(id);
		logger.info(beneficiaryAccountDelete);
		return beneficiaryAccountDelete;
	}
}

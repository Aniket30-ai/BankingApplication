package com.nihilent.bankingApplication.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nihilent.bankingApplication.entity.BeneficiaryAccount;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.BeneficiaryAccountRepository;
import com.nihilent.bankingApplication.service.BeneficiaryAccountService;

@Service
public class BeneficiaryAccountServiceImpl implements BeneficiaryAccountService {

	@Autowired
	private BeneficiaryAccountRepository accountRepository;

	public BeneficiaryAccount addBeneficiary(BeneficiaryAccount beneficiary) {
		Optional<BeneficiaryAccount> byAccountNumber = accountRepository
				.findByAccountNumber(beneficiary.getBankAccount().getAccountNumber());
		

		if (byAccountNumber.isPresent()) {
			throw new IllegalArgumentException("Beneficiary with this account already exists.");
		}
		return accountRepository.save(beneficiary);
	}

	public List<BeneficiaryAccount> getAllBeneficiaries() {
		return accountRepository.findAll();
	}

	@Override
	public String deleteBeneficiayAccount(Long id) throws NihilentBankException {
		// TODO Auto-generated method stub
		
		
		
		Optional<BeneficiaryAccount> accountId = accountRepository.findById(id);
		
		
		if(accountId.isEmpty()) {
			throw new NihilentBankException("Account id not present");
		}
		
		
		accountRepository.deleteById(id);
		return "Deleted sucess";
	}
}

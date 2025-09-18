package com.nihilent.bankingApplication.service;

import java.util.List;

import com.nihilent.bankingApplication.entity.BeneficiaryAccount;
import com.nihilent.bankingApplication.exception.NihilentBankException;



public interface BeneficiaryAccountService {

	
	 public BeneficiaryAccount addBeneficiary(BeneficiaryAccount beneficiary) ;
	 
	 
	 public List<BeneficiaryAccount> getAllBeneficiaries();
	 
	 
	 public String deleteBeneficiayAccount(Long id) throws NihilentBankException;
	 
	 
	 
	
}

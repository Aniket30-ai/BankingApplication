package com.nihilent.bankingApplication.service;

import java.util.List;
import com.nihilent.bankingApplication.entity.BeneficiaryAccount;
import com.nihilent.bankingApplication.exception.NihilentBankException;

public interface BeneficiaryAccountService {

	public BeneficiaryAccount addBeneficiary(BeneficiaryAccount beneficiary) throws NihilentBankException;

	public List<BeneficiaryAccount> getAllBeneficiaries() throws NihilentBankException;

	public String deleteBeneficiayAccount(Long id) throws NihilentBankException;

}

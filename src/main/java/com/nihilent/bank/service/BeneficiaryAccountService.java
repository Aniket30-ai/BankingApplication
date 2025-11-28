package com.nihilent.bank.service;

import java.util.List;

import com.nihilent.bank.entity.BeneficiaryAccount;
import com.nihilent.bank.exception.NihilentBankException;

public interface BeneficiaryAccountService {

	public BeneficiaryAccount addBeneficiary(BeneficiaryAccount beneficiary) throws NihilentBankException;

	public List<BeneficiaryAccount> getAllBeneficiaries() throws NihilentBankException;

	public String deleteBeneficiayAccount(Long id) throws NihilentBankException;

}

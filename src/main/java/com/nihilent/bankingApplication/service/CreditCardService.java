package com.nihilent.bankingApplication.service;

import java.util.List;
import java.util.Optional;

import com.nihilent.bankingApplication.entity.CreditCard;
import com.nihilent.bankingApplication.exception.NihilentBankException;

public interface CreditCardService {

	public String applyForCard(Long accountNumber) throws NihilentBankException;

	public Optional<List<CreditCard>> getRequestByUserId(Long accountNumber) throws NihilentBankException;

	public List<CreditCard> getPendingRequests() throws NihilentBankException;

	public String approveRequest(Long id) throws NihilentBankException;

	public CreditCard showCreditCard(Long id) throws NihilentBankException;
}

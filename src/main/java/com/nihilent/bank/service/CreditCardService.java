package com.nihilent.bank.service;

import java.util.List;
import java.util.Optional;

import com.nihilent.bank.entity.CreditCard;
import com.nihilent.bank.exception.NihilentBankException;

public interface CreditCardService {

	public String applyForCard(Long accountNumber) throws NihilentBankException;

	public Optional<List<CreditCard>> getRequestByUserId(Long accountNumber) throws NihilentBankException;

	public List<CreditCard> getPendingRequests() throws NihilentBankException;

	public String approveRequest(Long id) throws NihilentBankException;

	public CreditCard showCreditCard(Long id) throws NihilentBankException;
}

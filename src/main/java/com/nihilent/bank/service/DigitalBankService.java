package com.nihilent.bank.service;

import com.nihilent.bank.exception.NihilentBankException;

public interface DigitalBankService {

	public String linkAccount(Long mobileNumber, Long accountNumber) throws NihilentBankException;

	public byte[] getQRCode(String upiId) throws NihilentBankException;

	public String findUpiId(Long accountNumber) throws NihilentBankException;

}

package com.nihilent.bankingApplication.service;

import com.nihilent.bankingApplication.exception.NihilentBankException;

public interface DigitalBankService {

	public String linkAccount(Long mobileNumber, Long accountNumber) throws NihilentBankException;

	public byte[] getQRCode(String upiId) throws NihilentBankException;

	public String findUpiId(Long accountNumber) throws NihilentBankException;

}

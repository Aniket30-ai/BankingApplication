package com.nihilent.bankingApplication.exception;

public class NihilentBankException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String message;

	public NihilentBankException(String message) {
		super(message);
		this.message = message;
	}

}

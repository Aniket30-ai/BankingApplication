package com.nihilent.bankingApplication.service;

import com.nihilent.bankingApplication.dto.OperatorDto;
import com.nihilent.bankingApplication.exception.NihilentBankException;

public interface OperatorService {

	public void createOperator(OperatorDto operatorDto) throws NihilentBankException;

	public void updateOperator(OperatorDto operatorDto) throws NihilentBankException;

	public void deleteOperator(Long operatorId) throws NihilentBankException;

	public void getOperator(Long operatorId) throws NihilentBankException;

}

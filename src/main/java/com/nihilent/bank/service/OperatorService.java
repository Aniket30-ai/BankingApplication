package com.nihilent.bank.service;

import com.nihilent.bank.dto.OperatorDto;
import com.nihilent.bank.exception.NihilentBankException;

public interface OperatorService {

	public void createOperator(OperatorDto operatorDto) throws NihilentBankException;

	public void updateOperator(OperatorDto operatorDto) throws NihilentBankException;

	public void deleteOperator(Long operatorId) throws NihilentBankException;

	public void getOperator(Long operatorId) throws NihilentBankException;

}

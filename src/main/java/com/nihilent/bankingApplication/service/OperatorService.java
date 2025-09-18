package com.nihilent.bankingApplication.service;

import com.nihilent.bankingApplication.dto.OperatorDto;

public interface OperatorService {

	
	
	public void createOperator(OperatorDto operatorDto) ;
	
	
	public void updateOperator(OperatorDto operatorDto) ;
	
	public void deleteOperator(Long operatorId);
	
	public void getOperator(Long operatorId);
//	public void 
}

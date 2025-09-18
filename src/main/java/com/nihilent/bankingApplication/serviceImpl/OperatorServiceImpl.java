package com.nihilent.bankingApplication.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;

import com.nihilent.bankingApplication.dto.OperatorDto;
import com.nihilent.bankingApplication.entity.Operators;
import com.nihilent.bankingApplication.repository.OperatorRepository;
import com.nihilent.bankingApplication.service.OperatorService;

public class OperatorServiceImpl implements OperatorService {

	@Autowired
	private OperatorRepository operatorRepository;
	
	@Override
	public void createOperator(OperatorDto operatorDto) {
		// TODO Auto-generated method stub
		
		

		
		
		Operators operators = new Operators();
		
		operators.setName(operatorDto.getName());
		
		
		operators.setPlans(operators.getPlans());
		
		
			operatorRepository.save(operators);
		
	
		
		
		
		}

	@Override
	public void updateOperator(OperatorDto operatorDto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteOperator(Long operatorId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getOperator(Long operatorId) {
		// TODO Auto-generated method stub
		
	}

	
	
}

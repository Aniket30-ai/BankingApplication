package com.nihilent.bankingApplication.service;

import java.util.List;

import com.nihilent.bankingApplication.dto.CustomerDto;
import com.nihilent.bankingApplication.exception.NihilentBankException;

public interface CustomerService {

	public String registerCustomer(CustomerDto customerDto) throws NihilentBankException;

	public List<CustomerDto> showAllCustomer() throws NihilentBankException;
	
	
	public CustomerDto login(String emailId,String password) throws NihilentBankException;
	
	
	
	public CustomerDto getCustomerDetails(Long mobileNumber) throws NihilentBankException;
	
	public String updateEmailId(String emailId,Long mobileNumber) throws NihilentBankException;
}

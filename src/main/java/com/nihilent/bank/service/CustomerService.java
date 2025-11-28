package com.nihilent.bank.service;

import java.util.List;

import com.nihilent.bank.dto.CustomerDto;
import com.nihilent.bank.exception.NihilentBankException;

public interface CustomerService {

	public String registerCustomer(CustomerDto customerDto) throws NihilentBankException;

	public List<CustomerDto> showAllCustomer() throws NihilentBankException;

	public CustomerDto getCustomerDetails(Long mobileNumber) throws NihilentBankException;

	public String updateEmailId(String emailId, Long mobileNumber) throws NihilentBankException;
}

package com.nihilent.bankingApplication.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nihilent.bankingApplication.dto.CustomerDto;
import com.nihilent.bankingApplication.entity.Customer;
import com.nihilent.bankingApplication.entity.Roles;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.CustomerRepository;
import com.nihilent.bankingApplication.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService, UserDetailsService {

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public String registerCustomer(CustomerDto customerDto) throws NihilentBankException {
		// TODO Auto-generated method stub

		Optional<Customer> mobileNumber = customerRepository.findByMobileNumber(customerDto.getMobileNumber());

		if (mobileNumber.isPresent()) {
			throw new NihilentBankException("Customer_Present");
		}

		Customer customer = new Customer();

		customer.setAddress(customerDto.getAddress());
		customer.setEmailId(customerDto.getEmailId());
		customer.setMobileNumber(customerDto.getMobileNumber());
		customer.setName(customerDto.getName());
		customer.setRole(Roles.User);
//		customer.setPassword(customerDto.getPassword());

		customer.setPassword(new BCryptPasswordEncoder().encode(customerDto.getPassword()));

		customer.setGender(customerDto.getGender());
		
		 String newCustomerId = generateCustomerId();
//	        customer.setCustomerId(newCustomerId);
		
		customer.setCustomerId(newCustomerId);

		customerRepository.save(customer);
		return "User Added";
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub

		Optional<Customer> byEmailId = customerRepository.findByEmailId(username);

		if (byEmailId.get().getPassword() == null) {
			throw new UsernameNotFoundException("Enter password not present in db");
		}

		if (byEmailId.get() == null) {
			throw new UsernameNotFoundException("User Not found this username");
		}
		
		Roles role = byEmailId.get().getRole();
		
		System.out.println(role);
		
		return new CustomerInfo(byEmailId.get());
	}

	@Override
	public List<CustomerDto> showAllCustomer() throws NihilentBankException {
		// TODO Auto-generated method stub

		List<Customer> allCustomers = customerRepository.findAll();

		List<CustomerDto> listCustomers = new ArrayList<CustomerDto>();

		allCustomers.forEach(customer -> {
			CustomerDto customerDto = new CustomerDto();

			Roles role = customer.getRole();

			if (!role.equals(Roles.Admin)) {
				customerDto.setCustomerId(customer.getCustomerId());
				customerDto.setAddress(customer.getAddress());
				customerDto.setEmailId(customer.getEmailId());
				customerDto.setGender(customer.getGender());
				customerDto.setMobileNumber(customer.getMobileNumber());
				customerDto.setName(customer.getName());

				listCustomers.add(customerDto);
			}
		});

		return listCustomers;
	}

	@Override
	public CustomerDto login(String emailID, String password) throws NihilentBankException {
		// TODO Auto-generated method stub
		
		
		System.out.println(emailID);
		  Optional<Customer> byEmailId = customerRepository.findByEmailId(emailID);
		  
		  Customer customer = byEmailId.orElseThrow(()-> new NihilentBankException("Invalid Email id"));
		  
		  
		  String password2 = customer.getPassword();
		  
		  
		  System.out.println(password2);
		  if(!customer.getPassword().equals(password)) {
			  throw new NihilentBankException("Invalid Password");
		  }
		  
		  CustomerDto customerDto = new CustomerDto();
		  customerDto.setRoles(customer.getRole());
		  customerDto.setEmailId(customer.getEmailId());
		
		
		return customerDto;
	}

	@Override
	public CustomerDto getCustomerDetails(Long mobileNumber) throws NihilentBankException {
		// TODO Auto-generated method stub
		
		
		
		
		
		
		Optional<Customer> byMobileNumber = customerRepository.findByMobileNumber(mobileNumber);
		
		
		Customer customer = byMobileNumber.orElseThrow(()-> new NihilentBankException("Customer not found"));
		
		
		
		
		CustomerDto customerDto = new CustomerDto();
		
		
		customerDto.setAddress(customer.getAddress());
		customerDto.setEmailId(customer.getEmailId());
		customerDto.setGender(customer.getGender());
		customerDto.setMobileNumber(customer.getMobileNumber());
		customerDto.setName(customer.getName());
		
		return customerDto;
	}
	
	
	
	
	
	public String updateEmailId(String emailId ,Long mobileNumber) throws NihilentBankException{
		
		

		
		Optional<Customer> byMobileNumber = customerRepository.findByMobileNumber(mobileNumber);
		
		
		if(byMobileNumber.isEmpty()) {
			throw new NihilentBankException("Inavlid mobile number");
		}
		
		
//		Customer customer = new Customer();
		
		Customer customer2 = byMobileNumber.get();
	
		customer2.setEmailId(emailId);
		
		 customerRepository.save(customer2);
		
		return "Update success";
	}
	
	
	
	  private String generateCustomerId() {
		  Optional<Customer> lastCustomer = customerRepository.findTopByOrderByIdDesc();

		    int nextId = 1;
		    if (lastCustomer.isPresent()) {
		        String lastCustomerId = lastCustomer.get().getCustomerId(); // e.g., CH003
		        if (lastCustomerId != null && lastCustomerId.startsWith("CH")) {
		            String numericPart = lastCustomerId.substring(2);  // remove 'CH'
		            try {
		                nextId = Integer.parseInt(numericPart) + 1;
		            } catch (NumberFormatException e) {
		                // log warning and use 1 as fallback
		                nextId = 1;
		            }
		        }
		    }

		    return String.format("CH%03d", nextId);  // CH001, CH0
	  }
}

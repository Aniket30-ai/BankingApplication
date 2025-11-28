package com.nihilent.bank.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bank.dto.CustomerDto;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.entity.Roles;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.CustomerRepository;
import com.nihilent.bank.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService, UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	private final CustomerRepository customerRepository;

	public CustomerServiceImpl(CustomerRepository customerRepository) {

		this.customerRepository = customerRepository;
	}

	@Value("${CustomerService_Present}")
	private String customerPresent;

	@Value("${CustomerService_Register}")
	private String customerRegister;

	@Value("${CustomerService.INVALID_CREDENTIALS}")
	private String invalidCredentials;

	@Value("${CustomerService.Not_Found}")
	private String customerNotFound;

	@Value("${CustomerService.Invalid_MobileNumber}")
	private String invalidMobileNumber;

	@Value("${CustomerService.Update_Success}")
	private String customerUpdate;

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String registerCustomer(CustomerDto customerDto) throws NihilentBankException {

		Optional<Customer> mobileNumber = customerRepository.findByMobileNumber(customerDto.getMobileNumber());

		if (mobileNumber.isPresent()) {
			logger.error(customerPresent);
			throw new NihilentBankException(customerPresent);
		}

		Customer customer = new Customer();

		customer.setAddress(customerDto.getAddress());
		customer.setEmailId(customerDto.getEmailId());
		customer.setMobileNumber(customerDto.getMobileNumber());
		customer.setName(customerDto.getName());

		customer.setPassword(new BCryptPasswordEncoder().encode(customerDto.getPassword()));
		customer.setGender(customerDto.getGender());

		String newCustomerId = generateCustomerId();
		customer.setCustomerId(newCustomerId);
		customer.setRole(Roles.User);
		customerRepository.save(customer);
		logger.info(customerRegister);
		return customerRegister;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Optional<Customer> byEmailId = customerRepository.findByEmailId(username);

		Customer customer = byEmailId
		        .orElseThrow(() -> new RuntimeException("Account not found"));
		
	
		
		if (customer.getPassword() == null) {
			logger.warn(invalidCredentials);
			throw new UsernameNotFoundException(invalidCredentials);
		}

		if (customer.getEmailId() == null) {
			logger.warn(invalidCredentials);
			throw new UsernameNotFoundException(invalidCredentials);
		}

		return new CustomerInfo(customer);
	}

	@Override
	public List<CustomerDto> showAllCustomer() throws NihilentBankException {

		List<Customer> allCustomers = customerRepository.findAll();

		List<CustomerDto> listCustomers = new ArrayList<>();

		if (allCustomers.isEmpty()) {
			throw new NihilentBankException(customerNotFound);
		}

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
	public CustomerDto getCustomerDetails(Long mobileNumber) throws NihilentBankException {

		Optional<Customer> byMobileNumber = customerRepository.findByMobileNumber(mobileNumber);

		Customer customer = byMobileNumber.orElseThrow(() -> new NihilentBankException(customerNotFound));

		CustomerDto customerDto = new CustomerDto();

		customerDto.setAddress(customer.getAddress());
		customerDto.setEmailId(customer.getEmailId());
		customerDto.setGender(customer.getGender());
		customerDto.setMobileNumber(customer.getMobileNumber());
		customerDto.setName(customer.getName());

		return customerDto;
	}

	public String updateEmailId(String emailId, Long mobileNumber) throws NihilentBankException {

		Optional<Customer> byMobileNumber = customerRepository.findByMobileNumber(mobileNumber);

		if (byMobileNumber.isEmpty()) {
			throw new NihilentBankException(invalidMobileNumber);
		}

		Customer customer2 = byMobileNumber.get();

		customer2.setEmailId(emailId);

		customerRepository.save(customer2);

		return customerUpdate;
	}

	private String generateCustomerId() {
		Optional<Customer> lastCustomer = customerRepository.findTopByOrderByIdDesc();

		int nextId = 1;
		if (lastCustomer.isPresent()) {
			String lastCustomerId = lastCustomer.get().getCustomerId(); // e.g., CH003
			if (lastCustomerId != null && lastCustomerId.startsWith("CH")) {
				String numericPart = lastCustomerId.substring(2); // remove 'CH'
				try {
					nextId = Integer.parseInt(numericPart) + 1;
				} catch (NumberFormatException e) {
					// log warning and use 1 as fallback
					nextId = 1;
				}
			}
		}

		return String.format("CH%03d", nextId); // CH001, CH0
	}

}

package com.nihilent.bankingApplication.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nihilent.bankingApplication.dto.BankAccountDto;
import com.nihilent.bankingApplication.dto.CustomerDto;
import com.nihilent.bankingApplication.entity.BankAccount;
import com.nihilent.bankingApplication.entity.Customer;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.BankAccountRepository;
import com.nihilent.bankingApplication.repository.CustomerRepository;
import com.nihilent.bankingApplication.service.BankAccountService;
import com.nihilent.bankingApplication.utility.JwtUtil;

@Service
public class BankAccountServiceImpl implements BankAccountService {

	@Autowired
	private BankAccountRepository accountRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	public Long createAccount(BankAccountDto accountDto) throws NihilentBankException {
		// TODO Auto-generated method stub

//		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountDto.getAccountNumber());

		Optional<Customer> mobileNumber = customerRepository
				.findByMobileNumber(accountDto.getCustomerDto().getMobileNumber());

		if (mobileNumber.isEmpty()) {
			throw new NihilentBankException("Customer.invalid_Mobile_Number");
		}

//		if (byAccountNumber.isPresent()) {
//
//			throw new NihilentBankException("Account Already created");
//		}

		BankAccount bankAccount = new BankAccount();
		
		

		String num= "9460";
		Supplier<Long> randomNumber = () -> (long) (Math.random() * 90000000) + 10000000;
		Long accountNumber = randomNumber.get();
		
//		"num" integer.toString();
		
		String bankName="ICICI Bank";
		String ifscCode="ICICI0000527";

		bankAccount.setAccountNumber(accountNumber);

		bankAccount.setAccountStatus(accountDto.getAccountStatus());

		bankAccount.setAccountType(accountDto.getAccountType());
		bankAccount.setBalance(accountDto.getBalance());
		bankAccount.setBankName(bankName);
		bankAccount.setIfscCode(ifscCode);
		bankAccount.setPanCard(accountDto.getPanCard());
		
		bankAccount.setAdharCard(accountDto.getAdharCard());
		bankAccount.setDateOfBirth(accountDto.getDateOfBirth());

		bankAccount.setCustomer(mobileNumber.get());
		BankAccount save = accountRepository.save(bankAccount);
		
		Long accountNumber2 = save.getAccountNumber();

		return accountNumber2;
	}

	@Override
	public BankAccountDto getAccountDetails(Long accountNumber) throws NihilentBankException {
		// TODO Auto-generated method stub

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		BankAccount bankAccount = byAccountNumber
				.orElseThrow(() -> new NihilentBankException("Invalid Account Number...!!"));

		BankAccountDto accountDto = new BankAccountDto();

		accountDto.setAccountNumber(bankAccount.getAccountNumber());

		accountDto.setAccountStatus(bankAccount.getAccountStatus());
		accountDto.setAccountType(bankAccount.getAccountType());
		accountDto.setBalance(bankAccount.getBalance());
		accountDto.setBankName(bankAccount.getBankName());
		accountDto.setIfscCode(bankAccount.getIfscCode());
		accountDto.setOpeningDate(bankAccount.getOpeningDate());
		accountDto.setPanCard(bankAccount.getPanCard());
		accountDto.setAdharCard(bankAccount.getAdharCard());
		accountDto.setDateOfBirth(bankAccount.getDateOfBirth());
		CustomerDto customerDto = new CustomerDto();

		customerDto.setAddress(bankAccount.getCustomer().getAddress());
		customerDto.setCustomerId(bankAccount.getCustomer().getCustomerId());
		customerDto.setEmailId(bankAccount.getCustomer().getEmailId());
		customerDto.setGender(bankAccount.getCustomer().getGender());
		customerDto.setMobileNumber(bankAccount.getCustomer().getMobileNumber());
		customerDto.setName(bankAccount.getCustomer().getName());

		accountDto.setCustomerDto(customerDto);

		return accountDto;

	}

	@Override
	public List<BankAccountDto> showAllAcounts(Long mobileNumber) throws NihilentBankException {
		// TODO Auto-generated method stub

		List<BankAccount> byMobileNumber = accountRepository.findByMobileNumber(mobileNumber);

		if (byMobileNumber.isEmpty()) {
			throw new NihilentBankException("Invalid Mobile number");
		}

		List<BankAccountDto> listAccounts = new ArrayList<BankAccountDto>();

		byMobileNumber.forEach(bankAccount -> {

			BankAccountDto accountDto = new BankAccountDto();

			accountDto.setAccountNumber(bankAccount.getAccountNumber());
			accountDto.setAccountStatus(bankAccount.getAccountStatus());
			accountDto.setAccountType(bankAccount.getAccountType());
			accountDto.setBalance(bankAccount.getBalance());
			accountDto.setBankName(bankAccount.getBankName());
			accountDto.setIfscCode(bankAccount.getIfscCode());
			accountDto.setOpeningDate(bankAccount.getOpeningDate());
			accountDto.setPanCard(bankAccount.getPanCard());

			CustomerDto customerDto = new CustomerDto();

			customerDto.setCustomerId(bankAccount.getCustomer().getCustomerId());
			customerDto.setAddress(bankAccount.getCustomer().getAddress());
			customerDto.setEmailId(bankAccount.getCustomer().getAddress());
			customerDto.setGender(bankAccount.getCustomer().getGender());
			customerDto.setMobileNumber(bankAccount.getCustomer().getMobileNumber());
			customerDto.setName(bankAccount.getCustomer().getName());

			accountDto.setCustomerDto(customerDto);
			listAccounts.add(accountDto);
		});
		return listAccounts;
	}

	@Override
	public Double getBalance(Long accountNumber) throws NihilentBankException {
		// TODO Auto-generated method stub

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

//		jwtUtil.validateToken(null, null)
		
		BankAccount bankAccount = byAccountNumber
				.orElseThrow(() -> new NihilentBankException("Invalid Account Number"));

		Double balance = bankAccount.getBalance();

		return balance;
	}
	
	
	
	@Override
	public List<BankAccountDto> showAllAcountsDetails() throws NihilentBankException {
		// TODO Auto-generated method stub

		List<BankAccount> all = accountRepository.findAll();
		
//		List<BankAccount> byMobileNumber = accountRepository.findByMobileNumber(mobileNumber);

		if (all.isEmpty()) {
			throw new NihilentBankException("Invalid Mobile number");
		}

		List<BankAccountDto> listAccounts = new ArrayList<BankAccountDto>();

		all.forEach(bankAccount -> {

			BankAccountDto accountDto = new BankAccountDto();

			accountDto.setAccountNumber(bankAccount.getAccountNumber());
			accountDto.setAccountStatus(bankAccount.getAccountStatus());
			accountDto.setAccountType(bankAccount.getAccountType());
			accountDto.setBalance(bankAccount.getBalance());
			accountDto.setBankName(bankAccount.getBankName());
			accountDto.setIfscCode(bankAccount.getIfscCode());
			accountDto.setOpeningDate(bankAccount.getOpeningDate());
			accountDto.setPanCard(bankAccount.getPanCard());

			CustomerDto customerDto = new CustomerDto();

			customerDto.setCustomerId(bankAccount.getCustomer().getCustomerId());
			customerDto.setAddress(bankAccount.getCustomer().getAddress());
			customerDto.setEmailId(bankAccount.getCustomer().getEmailId());
			customerDto.setGender(bankAccount.getCustomer().getGender());
			customerDto.setMobileNumber(bankAccount.getCustomer().getMobileNumber());
			customerDto.setName(bankAccount.getCustomer().getName());

			accountDto.setCustomerDto(customerDto);
			listAccounts.add(accountDto);
		});
		return listAccounts;
	}

	
	@Override
	public BankAccountDto getAccountDetail(Long mobileNumber) throws NihilentBankException {
		// TODO Auto-generated method stub

BankAccount byMobileNumbers = accountRepository.findByMobileNumbers(mobileNumber);



		BankAccountDto accountDto = new BankAccountDto();

		accountDto.setAccountNumber(byMobileNumbers.getAccountNumber());

		accountDto.setAccountStatus(byMobileNumbers.getAccountStatus());
		accountDto.setAccountType(byMobileNumbers.getAccountType());
		accountDto.setBalance(byMobileNumbers.getBalance());
		accountDto.setBankName(byMobileNumbers.getBankName());
		accountDto.setIfscCode(byMobileNumbers.getIfscCode());
		accountDto.setOpeningDate(byMobileNumbers.getOpeningDate());
		accountDto.setPanCard(byMobileNumbers.getPanCard());
		
		accountDto.setAdharCard(byMobileNumbers.getAdharCard());
		accountDto.setDateOfBirth(byMobileNumbers.getDateOfBirth());

		CustomerDto customerDto = new CustomerDto();

		customerDto.setAddress(byMobileNumbers.getCustomer().getAddress());
		customerDto.setCustomerId(byMobileNumbers.getCustomer().getCustomerId());
		customerDto.setEmailId(byMobileNumbers.getCustomer().getEmailId());
		customerDto.setGender(byMobileNumbers.getCustomer().getGender());
		customerDto.setMobileNumber(byMobileNumbers.getCustomer().getMobileNumber());
		customerDto.setName(byMobileNumbers.getCustomer().getName());

		accountDto.setCustomerDto(customerDto);

		return accountDto;

	}
	
	
	public String accountDelete(Long accountNumber) throws NihilentBankException{
		
		
		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);
		
		
		BankAccount bankAccount = byAccountNumber.orElseThrow(()-> new NihilentBankException("Invalid account Number"));
		
		
		
		accountRepository.deleteById(accountNumber);
		
		
		
		return "Deleted Success";
	}


}

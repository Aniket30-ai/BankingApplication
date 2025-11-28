package com.nihilent.bank.serviceimpl;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bank.dto.BankAccountDto;
import com.nihilent.bank.dto.BankAccountRequestDto;
import com.nihilent.bank.dto.CustomerDto;
import com.nihilent.bank.entity.AccountRequestStatus;
import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.BankAccountRequest;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.repository.BankAccountRequestRepository;
import com.nihilent.bank.repository.CustomerRepository;
import com.nihilent.bank.service.BankAccountService;
import com.nihilent.bank.service.DeactivateUpiIdService;

@Service
public class BankAccountServiceImpl implements BankAccountService {

	private BankAccountRepository accountRepository;

	private CustomerRepository customerRepository;

	private BankAccountRequestRepository accountRequestRepository;

	private final DeactivateUpiIdService deactivateUpiIdService;

	public BankAccountServiceImpl(BankAccountRepository accountRepository, CustomerRepository customerRepository,
			BankAccountRequestRepository bankAccountRequestRepository, DeactivateUpiIdService deactivateUpiIdService) {

		this.accountRepository = accountRepository;
		this.customerRepository = customerRepository;
		this.accountRequestRepository = bankAccountRequestRepository;

		this.deactivateUpiIdService = deactivateUpiIdService;
	}

	@Value("${CustomerService.Invalid_MobileNumber}")
	private String invalidMobileNumber;

	@Value("${BankAccountService.Invalid_AccountNumber}")
	private String invalidAccountNumber;

	@Value("${BankAccountService.Not_Found}")
	private String accountProcessed;

	@Value("${BankAccountService.Account_Delete}")
	private String accountDeleted;

	@Value("${BankAccountService.Account_Empty}")
	private String accountNotFound;

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public Long createAccount(BankAccountDto accountDto) throws NihilentBankException {

		Optional<Customer> mobileNumber = customerRepository
				.findByMobileNumber(accountDto.getCustomerDto().getMobileNumber());

		if (mobileNumber.isEmpty()) {
			throw new NihilentBankException(invalidMobileNumber);
		}

		BankAccount bankAccount = new BankAccount();

		SecureRandom secureRandom = new SecureRandom();

		// Generate an 8-digit account number between 10_000_000 and 99_999_999
		long accountNumber = 10_000_000L + Math.abs(secureRandom.nextLong() % 90_000_000L);

		String bankName = "ICICI Bank";
		String ifscCode = "ICICI0000527";

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

		return save.getAccountNumber();

	}

	@Override
	public BankAccountDto getAccountDetails(Long accountNumber) throws NihilentBankException {

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		BankAccount bankAccount = byAccountNumber.orElseThrow(() -> new NihilentBankException(invalidAccountNumber));

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

		List<BankAccount> byMobileNumber = accountRepository.findByMobileNumber(mobileNumber);

		if (byMobileNumber.isEmpty()) {
			throw new NihilentBankException(invalidMobileNumber);
		}

		List<BankAccountDto> listAccounts = new ArrayList<>();

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

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		BankAccount bankAccount = byAccountNumber.orElseThrow(() -> new NihilentBankException(invalidAccountNumber));

		return bankAccount.getBalance();
	}

	@Override
	public List<BankAccountDto> showAllAcountsDetails() throws NihilentBankException {

		List<BankAccount> all = accountRepository.findAll();

		if (all.isEmpty()) {
			throw new NihilentBankException(accountNotFound);
		}

		List<BankAccountDto> listAccounts = new ArrayList<>();

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

		BankAccount byMobileNumbers = accountRepository.findByMobileNumbers(mobileNumber);

		if (byMobileNumbers == null) {
			throw new NihilentBankException(invalidMobileNumber);
		}

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

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String accountDelete(Long accountNumber) throws NihilentBankException {

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		if (byAccountNumber.isEmpty()) {
			throw new NihilentBankException(invalidAccountNumber);
		}

		deactivateUpiIdService.deactivateUpiId(accountNumber);

		accountRepository.deleteByAccountNumber(accountNumber);

		return accountDeleted;
	}

	public String applyBankAccount(Long mobileNumber, String name, String accountType) throws NihilentBankException {

		accountRequestRepository.findByMobileNumber(mobileNumber);

		BankAccountRequest accountRequest = new BankAccountRequest();

		accountRequest.setMobileNumber(mobileNumber);
		accountRequest.setApplicantName(name);
		accountRequest.setAccountType(accountType);
		accountRequest.setApplicationDate(LocalDate.now());
		accountRequest.setStatus(AccountRequestStatus.PENDING); // Set status

		accountRequestRepository.save(accountRequest);

		return "Request Send";

	}

	@Override
	public List<BankAccountRequest> getAllAccountRequest() throws NihilentBankException {

		List<BankAccountRequest> list = accountRequestRepository.findAll();

		return list.stream().filter(account -> account.getStatus() == AccountRequestStatus.PENDING)
				.sorted(Comparator.comparing(BankAccountRequest::getApplicationDate).reversed()) // Sort by recent date
																									// first
				.map(account -> {
					BankAccountRequest accountRequest = new BankAccountRequest();
					accountRequest.setApplicantName(account.getApplicantName());
					accountRequest.setAccountId(account.getAccountId());
					accountRequest.setStatus(account.getStatus());
					accountRequest.setAccountType(account.getAccountType());
					accountRequest.setMobileNumber(account.getMobileNumber());
					accountRequest.setApplicationDate(account.getApplicationDate()); // ✅ include this so sorting makes
																						// sense
					return accountRequest;
				}).collect(Collectors.toCollection(ArrayList::new));

	}

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public BankAccountRequest updateAccountStatus(Long accountId, AccountRequestStatus status)
			throws NihilentBankException {

		BankAccountRequest bankAccountRequest = accountRequestRepository.findById(accountId)
				.orElseThrow(() -> new NihilentBankException(invalidAccountNumber));

		if (bankAccountRequest.getStatus() != AccountRequestStatus.PENDING) {
			throw new NihilentBankException(accountProcessed);
		}

		// 1. Update Account status
		bankAccountRequest.setStatus(status);

		return accountRequestRepository.save(bankAccountRequest);

	}

	@Override
	public BankAccountRequestDto getAccountStatus(Long mobileNumber) throws NihilentBankException {

		Optional<BankAccountRequest> byMobileNumber = accountRequestRepository.findByMobileNumber(mobileNumber);

		if (byMobileNumber.isEmpty()) {
			throw new NihilentBankException(accountNotFound);
		}

		BankAccountRequestDto accountRequest = new BankAccountRequestDto();

		accountRequest.setAccountId(byMobileNumber.get().getAccountId());
		accountRequest.setAccountType(byMobileNumber.get().getAccountType());
		accountRequest.setApplicantName(byMobileNumber.get().getApplicantName());
		accountRequest.setApplicationDate(byMobileNumber.get().getApplicationDate());
		accountRequest.setStatus(byMobileNumber.get().getStatus());
		accountRequest.setMobileNumber(byMobileNumber.get().getMobileNumber());

		return accountRequest;
	}

}

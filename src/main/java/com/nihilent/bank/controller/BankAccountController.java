package com.nihilent.bank.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bank.dto.BankAccountDto;
import com.nihilent.bank.dto.BankAccountRequestDto;
import com.nihilent.bank.entity.AccountRequestStatus;
import com.nihilent.bank.entity.BankAccountRequest;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.service.BankAccountService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Controller responsible for managing bank account operations for both admin
 * and user. It provides APIs for account creation, balance check, account
 * details retrieval, account deletion, and managing account requests.
 */

@RestController
@RequestMapping(value = "NihilentBank")
@Validated
public class BankAccountController {

	// Service layer dependency for handling all bank account business logic
	private final BankAccountService accountService;

	public BankAccountController(BankAccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping(value = "/admin/createAccount")
	public ResponseEntity<Long> createAccount(@Valid @RequestBody BankAccountDto accountDto)
			throws NihilentBankException {

		Long account = accountService.createAccount(accountDto);

		return new ResponseEntity<>(account, HttpStatus.CREATED);
	}

	@GetMapping(value = "admin/accountDetails/{accountNumber}")
	public ResponseEntity<BankAccountDto> getAccountDetails(
			@PathVariable @Min(value = 10000000L, message = "{bankAccount.accountNumber.invalid}") @Max(value = 99999999L, message = "{bankAccount.accountNumber.invalid}") Long accountNumber)
			throws NihilentBankException {

		BankAccountDto accountDetails = accountService.getAccountDetails(accountNumber);

		return new ResponseEntity<>(accountDetails, HttpStatus.OK);
	}

	@GetMapping(value = "/user/getBalance/{accountNumber}")
	public ResponseEntity<Double> getBalance(
			@PathVariable @Min(value = 10000000L, message = "{bankAccount.accountNumber.invalid}") @Max(value = 99999999L, message = "{bankAccount.accountNumber.invalid}") Long accountNumber)
			throws NihilentBankException {

		Double balance = accountService.getBalance(accountNumber);

		return new ResponseEntity<>(balance, HttpStatus.OK);
	}

	@GetMapping(value = "/admin/allAccountDetails/{mobileNumber}")
	public ResponseEntity<List<BankAccountDto>> showAllAccounts(
			@PathVariable @Min(value = 1000000000L, message = "{customer.mobileNumber.invalid}") @Max(value = 9999999999L, message = "{customer.mobileNumber.invalid}") Long mobileNumber)
			throws NihilentBankException {

		List<BankAccountDto> showAllAcounts = accountService.showAllAcounts(mobileNumber);

		return new ResponseEntity<>(showAllAcounts, HttpStatus.OK);
	}

	@GetMapping(value = "/admin/allAccountDetails")
	public ResponseEntity<List<BankAccountDto>> showAllAccountDetails() throws NihilentBankException {

		List<BankAccountDto> showAllAcounts = accountService.showAllAcountsDetails();

		return new ResponseEntity<>(showAllAcounts, HttpStatus.OK);
	}

	@GetMapping(value = "user/accountDetails/{mobileNumber}")
	public ResponseEntity<BankAccountDto> getAccountDetail(
			@PathVariable @Min(value = 1000000000L, message = "{customer.mobileNumber.invalid}") @Max(value = 9999999999L, message = "{customer.mobileNumber.invalid}") long mobileNumber)
			throws NihilentBankException {

		BankAccountDto accountDetail = accountService.getAccountDetail(mobileNumber);

		return new ResponseEntity<>(accountDetail, HttpStatus.OK);
	}

	@DeleteMapping("/admin/accountDelete/{accountNumber}")
	public ResponseEntity<String> accountDelete(@PathVariable

	@Min(value = 10000000L, message = "{bankAccount.accountNumber.invalid}") @Max(value = 99999999L, message = "{bankAccount.accountNumber.invalid}") Long accountNumber)
			throws NihilentBankException {

		String accountDelete = accountService.accountDelete(accountNumber);

		return new ResponseEntity<>(accountDelete, HttpStatus.OK);
	}

	@PostMapping("/bank/request-account/{mobileNumber}/{name}/{accountType}")
	public ResponseEntity<String> requestBankAccount(@PathVariable Long mobileNumber, @PathVariable String name,
			@PathVariable String accountType) throws NihilentBankException {

		String applyBankAccount = accountService.applyBankAccount(mobileNumber, name, accountType);

		return new ResponseEntity<>(applyBankAccount, HttpStatus.OK);

	}

	@GetMapping("/allAccountRequest")
	public ResponseEntity<List<BankAccountRequest>> getAllAccountRequest() throws NihilentBankException {

		List<BankAccountRequest> allAccountRequest = accountService.getAllAccountRequest();

		return new ResponseEntity<>(allAccountRequest, HttpStatus.OK);
	}

	@PutMapping("/updateAccount/{loanId}/{status}")
	public ResponseEntity<BankAccountRequest> updateLoanStatus(@PathVariable Long loanId,
			@PathVariable AccountRequestStatus status) throws NihilentBankException {
		BankAccountRequest updateAccountStatus = accountService.updateAccountStatus(loanId, status);

		return new ResponseEntity<>(updateAccountStatus, HttpStatus.OK);
	}

	@GetMapping("accountStatus/{mobileNumber}")
	public ResponseEntity<BankAccountRequestDto> getAccountStatus(
			@PathVariable @Min(value = 1000000000L, message = "{customer.mobileNumber.invalid}") @Max(value = 9999999999L, message = "{customer.mobileNumber.invalid}") long mobileNumber)
			throws NihilentBankException {

		BankAccountRequestDto accountStatus = accountService.getAccountStatus(mobileNumber);

		return new ResponseEntity<>(accountStatus, HttpStatus.OK);
	}

}

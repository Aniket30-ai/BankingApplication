package com.nihilent.bankingApplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bankingApplication.dto.BankAccountDto;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.service.BankAccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "NihilentBank")
@CrossOrigin(origins = "http://localhost:4200") 
public class BankAccountApi {

	@Autowired
	private BankAccountService accountService;
	
	

	@PostMapping(value = "/admin/createAccount")
	public ResponseEntity<Long> createAccount( @RequestBody BankAccountDto accountDto)
			throws NihilentBankException {

		System.out.println(accountDto);

		Long account = accountService.createAccount(accountDto);

		return new ResponseEntity<Long>(account, HttpStatus.CREATED);
	}

	@GetMapping(value = "admin/accountDetails/{accountNumber}")
	public ResponseEntity<BankAccountDto> getAccountDetails(@PathVariable  Long accountNumber)
			throws NihilentBankException {

		System.out.println(accountNumber);
		BankAccountDto accountDetails = accountService.getAccountDetails(accountNumber);

		return new ResponseEntity<BankAccountDto>(accountDetails, HttpStatus.OK);
	}

	
	
	@GetMapping(value = "/user/getBalance/{accountNumber}")
	public ResponseEntity<Double> getBalance(@PathVariable Long accountNumber) throws NihilentBankException {

		Double balance = accountService.getBalance(accountNumber);

//		String message = "Your account Balance : ₹ " + balance;

		return new ResponseEntity<Double>(balance, HttpStatus.OK);
	}

	
	
	
	
	@GetMapping(value = "/admin/allAccountDetails/{mobileNumber}")
	public ResponseEntity<List<BankAccountDto>> showAllAccounts(@PathVariable Long mobileNumber)
			throws NihilentBankException {

		List<BankAccountDto> showAllAcounts = accountService.showAllAcounts(mobileNumber);

		return new ResponseEntity<List<BankAccountDto>>(showAllAcounts, HttpStatus.OK);
	}
	
	
	
	@GetMapping(value = "/admin/allAccountDetails")
	public ResponseEntity<List<BankAccountDto>> showAllAccountDetails()
			throws NihilentBankException {

		List<BankAccountDto> showAllAcounts = accountService.showAllAcountsDetails();

		return new ResponseEntity<List<BankAccountDto>>(showAllAcounts, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "user/accountDetails/{mobileNumber}")
	public ResponseEntity<BankAccountDto> getAccountDetail(@PathVariable Long mobileNumber)
			throws NihilentBankException {

	
		BankAccountDto accountDetail = accountService.getAccountDetail(mobileNumber);

		return new ResponseEntity<BankAccountDto>(accountDetail, HttpStatus.OK);
	}
	
	
	
	@DeleteMapping("/admin/accountDelete/{accountNumber}")
	public ResponseEntity<String> accountDelete(@PathVariable Long accountNumber) throws NihilentBankException{
		
		
		
		
		String accountDelete = accountService.accountDelete(accountNumber);
		
		return new ResponseEntity<String>(accountDelete,HttpStatus.OK);
	}
	
}

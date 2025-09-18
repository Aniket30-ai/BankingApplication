package com.nihilent.bankingApplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bankingApplication.entity.BeneficiaryAccount;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.service.BeneficiaryAccountService;
import com.nihilent.bankingApplication.serviceImpl.BeneficiaryAccountServiceImpl;

@RestController
@RequestMapping(value = "NihilentBank")
@CrossOrigin(origins = "http://localhost:4200") 
public class BeneficiaryAccountApi {

	@Autowired
	BeneficiaryAccountServiceImpl accountService;
	
	@PostMapping("/beneficiary/add")
	public ResponseEntity<BeneficiaryAccount> addBeneficiary(@RequestBody BeneficiaryAccount account) {
		
		
		BeneficiaryAccount beneficiary = accountService.addBeneficiary(account);
		
		
		return new ResponseEntity<BeneficiaryAccount>(beneficiary,HttpStatus.CREATED);
		
	}
	
	@GetMapping("/beneficiary/list")
	public ResponseEntity<List<BeneficiaryAccount>> listBeneficaryAccount() {
		
		
		List<BeneficiaryAccount> allBeneficiaries = accountService.getAllBeneficiaries();
		
		
		return new ResponseEntity<List<BeneficiaryAccount>>(allBeneficiaries,HttpStatus.OK);
		
	}
	
	@DeleteMapping("/beneficiary/delete/{id}")
	public ResponseEntity<String> deleteAccount(@PathVariable Long id) throws NihilentBankException {
		String deleteBeneficiayAccount = accountService.deleteBeneficiayAccount(id);
		
		
		return new ResponseEntity<String>(deleteBeneficiayAccount,HttpStatus.OK);
	}
	
}

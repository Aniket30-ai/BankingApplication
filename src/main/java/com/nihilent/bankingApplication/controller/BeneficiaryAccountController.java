package com.nihilent.bankingApplication.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping(value = "NihilentBank")
@Validated
/**
 * Controller responsible for managing beneficiary accounts. Provides APIs to
 * add a beneficiary, list all beneficiaries, and delete a beneficiary account.
 */
public class BeneficiaryAccountController {

	// Service dependency to handle beneficiary account operations
	private final BeneficiaryAccountService accountService;

	public BeneficiaryAccountController(BeneficiaryAccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping("/beneficiary/add")
	public ResponseEntity<BeneficiaryAccount> addBeneficiary(@RequestBody BeneficiaryAccount account)
			throws NihilentBankException {

		BeneficiaryAccount beneficiary = accountService.addBeneficiary(account);

		return new ResponseEntity<BeneficiaryAccount>(beneficiary, HttpStatus.CREATED);

	}

	@GetMapping("/beneficiary/list")
	public ResponseEntity<List<BeneficiaryAccount>> listBeneficaryAccount() throws NihilentBankException {

		List<BeneficiaryAccount> allBeneficiaries = accountService.getAllBeneficiaries();

		return new ResponseEntity<List<BeneficiaryAccount>>(allBeneficiaries, HttpStatus.OK);

	}

	@DeleteMapping("/beneficiary/delete/{id}")
	public ResponseEntity<String> deleteAccount(
			@PathVariable @Min(value = 1L, message = "{BeneficiaryAccount.notFound}") @Max(value = 99L, message = "{BeneficiaryAccount.notFound}") Long id)
			throws NihilentBankException {
		String deleteBeneficiayAccount = accountService.deleteBeneficiayAccount(id);

		return new ResponseEntity<String>(deleteBeneficiayAccount, HttpStatus.OK);
	}

}

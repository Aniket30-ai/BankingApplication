package com.nihilent.bank.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bank.entity.CreditCard;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.service.CreditCardService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;



/**
 * Controller responsible for handling all Credit Card–related operations.
 * Includes APIs for applying for a credit card, checking request status,
 * fetching pending requests, approving requests, and viewing credit card details.
 */
@RestController
@RequestMapping(value = "NihilentBank")
@Validated
public class CreditCardController {

	 // Service layer dependency to perform credit card operations
	private final CreditCardService creditCardService;

	public CreditCardController(CreditCardService creditCardService) {

		this.creditCardService = creditCardService;
	}

	@PostMapping("/apply/{accountNumber}")
	public ResponseEntity<String> apply(
			@PathVariable @Min(value = 10000000L, message = "{bankAccount.accountNumber.invalid}") @Max(value = 99999999L, message = "{bankAccount.accountNumber.invalid}") Long accountNumber)
			throws NihilentBankException {

		String applyForCard = creditCardService.applyForCard(accountNumber);

		return new ResponseEntity<>(applyForCard, HttpStatus.CREATED);
	}

	@GetMapping("/status/{accountNumber}")
	public ResponseEntity<Optional<List<CreditCard>>> getStatus(
			@PathVariable @Min(value = 10000000L, message = "{bankAccount.accountNumber.invalid}") @Max(value = 99999999L, message = "{bankAccount.accountNumber.invalid}") Long accountNumber)
			throws NihilentBankException {

		Optional<List<CreditCard>> requestByUserId = creditCardService.getRequestByUserId(accountNumber);

		return new ResponseEntity<>(requestByUserId, HttpStatus.OK);

	}

	@GetMapping("/admin/requests")
	public ResponseEntity<List<CreditCard>> getPendingRequests() throws NihilentBankException {
		List<CreditCard> pendingRequests = creditCardService.getPendingRequests();

		return new ResponseEntity<>(pendingRequests, HttpStatus.OK);
	}

	@PostMapping("/admin/approve/{id}")
	public ResponseEntity<String> approve(@PathVariable Long id) throws NihilentBankException {

		String approveRequest = creditCardService.approveRequest(id);

		return new ResponseEntity<>(approveRequest, HttpStatus.OK);
	}

	@GetMapping("/showCreditCard/{id}")
	public ResponseEntity<CreditCard> showCreditCard(@PathVariable Long id) throws NihilentBankException {

		CreditCard showCreditCard = creditCardService.showCreditCard(id);

		return new ResponseEntity<>(showCreditCard, HttpStatus.OK);
	}

}

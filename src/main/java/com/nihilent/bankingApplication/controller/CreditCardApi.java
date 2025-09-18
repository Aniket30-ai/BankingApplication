package com.nihilent.bankingApplication.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bankingApplication.entity.CreditCard;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.serviceImpl.CreditCardServiceImpl;

@RestController
@RequestMapping(value = "NihilentBank")
@CrossOrigin(origins = "http://localhost:4200")
public class CreditCardApi {

	@Autowired
	private CreditCardServiceImpl creditCardService;

	@PostMapping("/apply/{accountNumber}")
	public String apply(@PathVariable Long accountNumber) {
//	    Long accountNumber = payload.get("accountNumber");
		System.out.println(accountNumber);
		return creditCardService.applyForCard(accountNumber);
	}

	@GetMapping("/status/{accountNumber}")
	public ResponseEntity<Optional<List<CreditCard>>> getStatus(@PathVariable Long accountNumber) {

		Optional<List<CreditCard>> requestByUserId = creditCardService.getRequestByUserId(accountNumber);

		return new ResponseEntity<Optional<List<CreditCard>>>(requestByUserId, HttpStatus.OK);

	}

	@GetMapping("/admin/requests")
	public List<CreditCard> getPendingRequests() {
		return creditCardService.getPendingRequests();
	}

	@PostMapping("/admin/approve/{id}")
	public String approve(@PathVariable Long id) {
		System.out.println(id);
		return creditCardService.approveRequest(id);
	}

	@GetMapping("/showCreditCard/{id}")
	public ResponseEntity<CreditCard> showCreditCard(@PathVariable Long id) throws NihilentBankException {

		CreditCard showCreditCard = creditCardService.showCreditCard(id);

		return new ResponseEntity<CreditCard>(showCreditCard, HttpStatus.OK);
	}

}

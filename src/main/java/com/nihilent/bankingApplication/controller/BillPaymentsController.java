package com.nihilent.bankingApplication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.service.BillPayments;

@RestController
@RequestMapping(value = "NihilentBank")
@Validated
/**
 * Controller class that handles all types of bill payment operations
 */
public class BillPaymentsController {

	// Service layer dependency for processing bill payments
	private final BillPayments billPayments;

	public BillPaymentsController(BillPayments billPayments) {
		this.billPayments = billPayments;
	}

	@PostMapping("/bill/mobileRecharge")
	public ResponseEntity<String> mobileRecharge(@RequestParam Long mobileNumber, @RequestParam Long accountNumber,
			@RequestParam(name = "operator") String remark, @RequestParam Double amount) throws NihilentBankException {

		// Call service method to process mobile recharge
		String mobileRecharge = billPayments.mobileRecharge(mobileNumber, amount, remark, accountNumber);

		return new ResponseEntity<String>(mobileRecharge, HttpStatus.OK);
	}

	@PostMapping("/bill/dthRecharge")
	public ResponseEntity<String> dthRecharge(@RequestParam Long subscriberId, @RequestParam Long accountNumber,
			@RequestParam(name = "provider") String remark, @RequestParam Double amount) throws NihilentBankException {

		// Call service method to process DTH recharge
		String dthRecharge = billPayments.dthRecharge(subscriberId, amount, remark, accountNumber);

		return new ResponseEntity<String>(dthRecharge, HttpStatus.OK);
	}

	@PostMapping("/bill/electricityBill")
	public ResponseEntity<String> electricityBill(@RequestParam Long consumerNumber, @RequestParam Long accountNumber,
			@RequestParam(name = "provider") String remark, @RequestParam Double amount) throws NihilentBankException {

		// Call service method to process Electricity Bill
		String electricityBill = billPayments.electricityBill(consumerNumber, amount, remark, accountNumber);

		return new ResponseEntity<String>(electricityBill, HttpStatus.OK);
	}

}

package com.nihilent.bank.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.service.BillPayments;


/**
 * Controller class that handles all types of bill payment operations
 */
@RestController
@RequestMapping(value = "NihilentBank")
@Validated
public class BillPaymentsController {

	// Service layer dependency for processing bill payments
	private final BillPayments billPayments;

	public BillPaymentsController(BillPayments billPayments) {
		this.billPayments = billPayments;
	}

	@PostMapping("/bill/mobileRecharge")
	public ResponseEntity<String> mobileRecharge(@RequestParam @Min(value = 1000000000L, message = "{customer.mobileNumber.invalid}") @Max(value = 9999999999L, message = "{customer.mobileNumber.invalid}")  Long mobileNumber, @RequestParam Long accountNumber,
												 @RequestParam(name = "operator") String remark, @RequestParam Double amount) throws NihilentBankException {

		// Call service method to process mobile recharge
		String mobileRecharge = billPayments.mobileRecharge(mobileNumber, amount, remark, accountNumber);

		return new ResponseEntity<>(mobileRecharge, HttpStatus.OK);
	}

	@PostMapping("/bill/dthRecharge")
	public ResponseEntity<String> dthRecharge(  @RequestParam @Min(value = 1000000000L, message = "{customer.subscriberId.invalid}") @Max(value = 9999999999L, message = "{customer.subscriberId.invalid}") Long subscriberId, @RequestParam Long accountNumber,
												@RequestParam(name = "provider") String remark, @RequestParam Double amount) throws NihilentBankException {

		// Call service method to process DTH recharge
		String dthRecharge = billPayments.dthRecharge(subscriberId, amount, remark, accountNumber);

		return new ResponseEntity<>(dthRecharge, HttpStatus.OK);
	}

	@PostMapping("/bill/electricityBill")
	public ResponseEntity<String> electricityBill(@RequestParam @Min(value = 1000000000L, message = "{customer.consumerNumber.invalid}") @Max(value = 9999999999L, message = "{customer.consumerNumber.invalid}") Long consumerNumber, @RequestParam Long accountNumber,
												  @RequestParam(name = "provider") String remark, @RequestParam Double amount) throws NihilentBankException {

		// Call service method to process Electricity Bill
		String electricityBill = billPayments.electricityBill(consumerNumber, amount, remark, accountNumber);

		return new ResponseEntity<>(electricityBill, HttpStatus.OK);
	}
}

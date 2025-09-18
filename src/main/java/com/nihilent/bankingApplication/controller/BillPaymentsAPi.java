package com.nihilent.bankingApplication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.service.BillPayments;



@RestController
@RequestMapping(value = "NihilentBank")
@CrossOrigin(origins = "http://localhost:4200") 
public class BillPaymentsAPi {

	@Autowired
	private BillPayments billPayments;

	
	
	@PostMapping("/bill/mobileRecharge")
	public ResponseEntity<String> mobileRecharge(@RequestParam Long mobileNumber,@RequestParam Long accountNumber,@RequestParam(name = "operator") String remark,@RequestParam Double amount) throws NihilentBankException {

		
		
		System.out.println(mobileNumber);

		System.out.println(accountNumber);
		
		
		System.out.println(remark);
		
		System.out.println(amount);
		
		
		String mobileRecharge = billPayments.mobileRecharge(mobileNumber, amount, remark, accountNumber);
		
		System.out.println(mobileRecharge);
		return new ResponseEntity<String>(mobileRecharge,HttpStatus.OK);
	}
	
	
	
	
	

	@PostMapping("/bill/dthRecharge")
	public ResponseEntity<String> dthRecharge(@RequestParam Long subscriberId,@RequestParam Long accountNumber,@RequestParam(name = "provider") String remark,@RequestParam Double amount) throws NihilentBankException {

		
		
		System.out.println(subscriberId);

		System.out.println(accountNumber);
		
		
		System.out.println(remark);
		
		System.out.println(amount);
		
		
		String dthRecharge = billPayments.dthRecharge(subscriberId, amount, remark, accountNumber);
		
		System.out.println(dthRecharge);
		return new ResponseEntity<String>(dthRecharge,HttpStatus.OK);
	}
	
	
	@PostMapping("/bill/electricityBill")
	public ResponseEntity<String> electricityBill(@RequestParam Long consumerNumber,@RequestParam Long accountNumber,@RequestParam(name = "provider") String remark,@RequestParam Double amount) throws NihilentBankException {

		
		
		System.out.println(consumerNumber);

		System.out.println(accountNumber);
		
		
		System.out.println(remark);
		
		System.out.println(amount);
		
		
		String electricityBill = billPayments.electricityBill(consumerNumber, amount, remark, accountNumber);
		
		System.out.println(electricityBill);
		return new ResponseEntity<String>(electricityBill,HttpStatus.OK);
	}
}

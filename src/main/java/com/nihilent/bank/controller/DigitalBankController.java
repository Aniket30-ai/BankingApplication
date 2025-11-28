package com.nihilent.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.service.DigitalBankService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "NihilentBank")
@Validated
public class DigitalBankController {

	private final DigitalBankService digitalBankService;

	public DigitalBankController(DigitalBankService digitalBankService) {
		this.digitalBankService = digitalBankService;
	}

	
    //API use for link bank Account
	@GetMapping(value = "user/linkAccount")
	public ResponseEntity<String> linkAccount(
			@RequestParam @Min(value = 1000000000L, message = "{customer.mobileNumber.invalid}") @Max(value = 9999999999L, message = "{customer.mobileNumber.invalid}") Long mobileNumber,
			@RequestParam @Min(value = 10000000L, message = "{bankAccount.accountNumber.invalid}") @Max(value = 99999999L, message = "{bankAccount.accountNumber.invalid}") Long accountNumber)
			throws NihilentBankException {

		String linkAccount = digitalBankService.linkAccount(mobileNumber, accountNumber);

		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(linkAccount);
	}

   //API use for getting the QR code using UPI ID
	@GetMapping("getQRCode/{upiId}")
	public ResponseEntity<byte[]> getQRCode(@PathVariable

	@NotNull(message = "{DigitalAccount.upiId.notNull}") @NotBlank(message = "{DigitalAccount.upiId.invalid}") String upiId)
			throws NihilentBankException {

		byte[] qrCode = digitalBankService.getQRCode(upiId);

		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);
	}

	@GetMapping("getUpi/{accountNumber}")
	public ResponseEntity<String> getUpiId(
			@PathVariable @Min(value = 10000000L, message = "{bankAccount.accountNumber.invalid}") @Max(value = 99999999L, message = "{bankAccount.accountNumber.invalid}") Long accountNumber)
			throws NihilentBankException {

		String upiId = digitalBankService.findUpiId(accountNumber);

		return new ResponseEntity<>(upiId, HttpStatus.OK);

	}
}

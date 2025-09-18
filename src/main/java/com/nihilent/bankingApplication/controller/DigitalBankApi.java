package com.nihilent.bankingApplication.controller;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.service.DigitalBankService;

@RestController
@RequestMapping(value = "NihilentBank")
@CrossOrigin(origins = "http://localhost:4200")
public class DigitalBankApi {

	@Autowired
	private DigitalBankService digitalBankService;

	@GetMapping(value = "user/linkAccount")
	public ResponseEntity<String> linkAccount(@RequestParam Long mobileNumber, @RequestParam Long accountNumber)
			throws NihilentBankException {

		System.out.println(mobileNumber);
		System.out.println(accountNumber);
		String linkAccount = digitalBankService.linkAccount(mobileNumber, accountNumber);
  
		
//	String msg=	"Your UPI ID is :"+linkAccount;
//		return new ResponseEntity<byte[]>(linkAccount, HttpStatus.OK);
//		
//		
//
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		BitMatrix matrix = new MultiFormatWriter().encode(upiUrl, BarcodeFormat.QR_CODE, 300, 300);
//		MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);

//		byte[] qrImage = outputStream.toByteArray();

		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(linkAccount);
	}
	
	
	
	@GetMapping("getQRCode/{upiId}")
	public ResponseEntity<byte[]> getQRCode(@PathVariable String upiId) throws NihilentBankException {
		
		System.out.println(upiId);
		
		
		
		
		byte[] qrCode = digitalBankService.getQRCode(upiId);
		
		
		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);
	}
	
	
	
	@GetMapping("getUpi/{accountNumber}")
	public ResponseEntity<String> getUpiId(@PathVariable Long accountNumber) throws NihilentBankException {
		
		
		String upiId = digitalBankService.findUpiId(accountNumber);
		
		
		return new ResponseEntity<String>(upiId,HttpStatus.OK);
		
	}
}

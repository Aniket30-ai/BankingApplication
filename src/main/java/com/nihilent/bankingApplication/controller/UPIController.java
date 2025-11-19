package com.nihilent.bankingApplication.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

@RestController
@RequestMapping(value = "NihilentBank")
public class UPIController {

	
    @GetMapping("qrcode")
	public ResponseEntity<byte[]> generateQrCode(@RequestParam String upiId, @RequestParam String name,
			@RequestParam(required = false) String amount) throws WriterException, IOException {
		String upiUrl = String.format("upi://pay?pa=%s&pn=%s&am=%s&cu=INR", upiId, name, amount != null ? amount : ""); // 'am'
																														// can
																														// be																								// optional

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		BitMatrix matrix = new MultiFormatWriter().encode(upiUrl, BarcodeFormat.QR_CODE, 300, 300);
		MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);

		byte[] qrImage = outputStream.toByteArray();

		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrImage);
	}
}

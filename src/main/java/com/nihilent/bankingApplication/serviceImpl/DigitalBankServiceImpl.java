package com.nihilent.bankingApplication.serviceImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.nihilent.bankingApplication.entity.AccountType;
import com.nihilent.bankingApplication.entity.BankAccount;
import com.nihilent.bankingApplication.entity.Customer;
import com.nihilent.bankingApplication.entity.DigitalBankAccount;
import com.nihilent.bankingApplication.entity.Transaction;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.BankAccountRepository;
import com.nihilent.bankingApplication.repository.CustomerRepository;
import com.nihilent.bankingApplication.repository.DigitalBankRepository;
import com.nihilent.bankingApplication.service.DigitalBankService;

@Service
@Transactional
public class DigitalBankServiceImpl implements DigitalBankService {

	@Autowired
	private DigitalBankRepository digitalBankRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BankAccountRepository accountRepository;

	@Override
	public String linkAccount(Long mobileNumber, Long accountNumber) throws NihilentBankException {
		// TODO Auto-generated method stub
		Optional<DigitalBankAccount> byAccountNumber2 = digitalBankRepository.findByAccountNumber(accountNumber);

		System.out.println("Sarted upi id created");
//		String digitalBankId3 = byAccountNumber2.get().getDigitalBankId();
//		System.out.println(digitalBankId3);
//		if (byAccountNumber2.isPresent()) {
//			throw new NihilentBankException("UPI ID already link....! "+digitalBankId3);
//		}

		Optional<Customer> byMobileNumber = customerRepository.findByMobileNumber(mobileNumber);

		Customer customer = byMobileNumber.orElseThrow(() -> new NihilentBankException("Invalid Mobile Number"));

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		BankAccount bankAccount = byAccountNumber
				.orElseThrow(() -> new NihilentBankException("Invalid Account Number"));

		String digitalBankId = customer.getMobileNumber() + "@" + bankAccount.getBankName();

		DigitalBankAccount digitalBankAccount = new DigitalBankAccount();

		AccountType accountType = bankAccount.getAccountType();

//		System.out.println(accountType);

		digitalBankAccount.setAccountType(accountType);

		digitalBankAccount.setBankAccount(bankAccount);
		digitalBankAccount.setCustomer(customer);

		digitalBankAccount.setDigitalBankId(digitalBankId);

		// ✅ Step 1: Generate UPI QR URL
		String upiUrl = "upi://pay?pa=" + digitalBankId + "&pn=" + customer.getName() + "&cu=INR";

		// ✅ Step 2: Generate QR Code
		try {
			String filePath = "qrcodes/" + digitalBankId + ".png";
			byte[] qrCodeImage = this.generateQRCodeImage(upiUrl, 300, 300, filePath);

			digitalBankAccount.setQrCodeImage(qrCodeImage);
			DigitalBankAccount digitalBankAccount2 = digitalBankRepository.save(digitalBankAccount);

			String digitalBankId2 = digitalBankAccount2.getDigitalBankId();

			System.out.println(digitalBankId2);
			System.out.println("QR Code saved to: " + filePath);
			return digitalBankId2;

		} catch (Exception e) {
			throw new NihilentBankException("QR Code generation failed");
		}

	}

	public byte[] generateQRCodeImage(String text, int width, int height, String filePath)
			throws WriterException, IOException {

		BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
		Path path = Paths.get(filePath);
		Files.createDirectories(path.getParent()); // Ensure 'qrcodes/' folder exists
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, "PNG", stream);
		byte[] imageBytes = stream.toByteArray();
		return imageBytes;// Return as Base64 string

	}

	public byte[] getQRCode(String upiId) throws NihilentBankException {

		Optional<DigitalBankAccount> byDigitalBankId = digitalBankRepository.findByDigitalBankId(upiId);

		DigitalBankAccount digitalBankAccount = byDigitalBankId
				.orElseThrow(() -> new NihilentBankException("Invalid upi Id"));

		byte[] qrCodeImage = digitalBankAccount.getQrCodeImage();

		return qrCodeImage;

	}

	public String findUpiId(Long accountNumber) throws NihilentBankException {

		Optional<DigitalBankAccount> byAccountNumber = digitalBankRepository.findByAccountNumber(accountNumber);

		DigitalBankAccount digitalBankAccount = byAccountNumber
				.orElseThrow(() -> new NihilentBankException("Invalid accountNumber"));

		String digitalBankId = digitalBankAccount.getDigitalBankId();

		return digitalBankId;

	}

	
}

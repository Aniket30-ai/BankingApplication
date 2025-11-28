package com.nihilent.bank.serviceimpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.nihilent.bank.entity.AccountType;
import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.entity.DigitalBankAccount;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.repository.CustomerRepository;
import com.nihilent.bank.repository.DigitalBankRepository;
import com.nihilent.bank.service.DigitalBankService;

@Service
public class DigitalBankServiceImpl implements DigitalBankService {

	private final DigitalBankRepository digitalBankRepository;

	private final CustomerRepository customerRepository;

	private final BankAccountRepository accountRepository;

	public DigitalBankServiceImpl(DigitalBankRepository digitalBankRepository, CustomerRepository customerRepository,
			BankAccountRepository accountRepository) {

		this.digitalBankRepository = digitalBankRepository;
		this.customerRepository = customerRepository;
		this.accountRepository = accountRepository;
	}

	@Value("${DigitalBanKService.Invalid_MobileNumber}")
	private String invalidMobileNumber;

	@Value("${DigitalBanKService.Invalid_AccountNumber}")
	private String invalidAccountNumber;

	@Value("${DigitalBanKService.Failed_QRGenerate}")
	private String failedQrGenerate;

	@Value("${DigitalBanKService.Invalid_upiId}")
	private String invalidUpiId;

	@Override
	public String linkAccount(Long mobileNumber, Long accountNumber) throws NihilentBankException {

		digitalBankRepository.findByAccountNumber(accountNumber);

		Optional<Customer> byMobileNumber = customerRepository.findByMobileNumber(mobileNumber);

		Customer customer = byMobileNumber.orElseThrow(() -> new NihilentBankException(invalidMobileNumber));

		Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);

		BankAccount bankAccount = byAccountNumber
				.orElseThrow(() -> new NihilentBankException("Invalid Account Number"));

		String digitalBankId = customer.getMobileNumber() + "@" + bankAccount.getBankName();

		DigitalBankAccount digitalBankAccount = new DigitalBankAccount();

		AccountType accountType = bankAccount.getAccountType();

		digitalBankAccount.setAccountType(accountType);

		digitalBankAccount.setBankAccount(bankAccount);
		digitalBankAccount.setCustomer(customer);

		digitalBankAccount.setDigitalBankId(digitalBankId);

		// Generate UPI QR URL
		String upiUrl = "upi://pay?pa=" + digitalBankId + "&pn=" + customer.getName() + "&cu=INR";

		// Generate QR Code
		try {
			String filePath = "qrcodes/" + digitalBankId + ".png";
			byte[] qrCodeImage = this.generateQRCodeImage(upiUrl, 300, 300, filePath);

			digitalBankAccount.setQrCodeImage(qrCodeImage);
			DigitalBankAccount digitalBankAccount2 = digitalBankRepository.save(digitalBankAccount);

			return digitalBankAccount2.getDigitalBankId();

		} catch (Exception e) {
			throw new NihilentBankException(failedQrGenerate);
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
		return stream.toByteArray();

	}

	public byte[] getQRCode(String upiId) throws NihilentBankException {

		Optional<DigitalBankAccount> byDigitalBankId = digitalBankRepository.findByDigitalBankId(upiId);

		DigitalBankAccount digitalBankAccount = byDigitalBankId
				.orElseThrow(() -> new NihilentBankException(invalidUpiId));

		return digitalBankAccount.getQrCodeImage();

	}

	public String findUpiId(Long accountNumber) throws NihilentBankException {

		Optional<DigitalBankAccount> byAccountNumber = digitalBankRepository.findByAccountNumber(accountNumber);

		DigitalBankAccount digitalBankAccount = byAccountNumber
				.orElseThrow(() -> new NihilentBankException(invalidAccountNumber));

		return digitalBankAccount.getDigitalBankId();

	}

}

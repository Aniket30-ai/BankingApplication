package com.nihilent.bankingApplication.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bankingApplication.entity.BankAccount;
import com.nihilent.bankingApplication.entity.CreditCard;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.CreditCardRepository;
import com.nihilent.bankingApplication.service.CreditCardService;

@Service
public class CreditCardServiceImpl implements CreditCardService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	private final CreditCardRepository repository;

	public CreditCardServiceImpl(CreditCardRepository repository) {
		this.repository = repository;
	}

	@Value("${CreditCardService.Request_Send}")
	private String requestSend;

	@Value("${CreditCardService.Invalid_Id}")
	private String invalidId;

	@Value("${CreditCardService.Not_Found}")
	private String creditCardNotFound;

	@Value("${CreditCardService.Request_Approved}")
	private String requestApproved;

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String applyForCard(Long accountNumber) throws NihilentBankException {

		CreditCard request = new CreditCard();

		BankAccount bankAccount = new BankAccount();

		bankAccount.setAccountNumber(accountNumber);
		request.setBankAccount(bankAccount);

		request.setStatus(CreditCard.Status.PENDING);
		repository.save(request);

		logger.info(requestSend);
		return requestSend;
	}

	@Override
	public Optional<List<CreditCard>> getRequestByUserId(Long accountNumber) throws NihilentBankException {
		return Optional.ofNullable(repository.findCreditCardByAccountNumber(accountNumber));

	}

	@Override
	public CreditCard showCreditCard(Long id) throws NihilentBankException {
		Optional<CreditCard> byId = repository.findById(id);
		CreditCard creditCard = byId.orElseThrow(() -> new NihilentBankException(invalidId));

		return creditCard;

	}

	@Override
	public List<CreditCard> getPendingRequests() throws NihilentBankException {
		return repository.findByStatus(CreditCard.Status.PENDING);
	}

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String approveRequest(Long id) throws NihilentBankException {
		Optional<CreditCard> optionalRequest = repository.findById(id);
		if (optionalRequest.isEmpty()) {
			return creditCardNotFound;
		}

		CreditCard request = optionalRequest.get();
		request.setStatus(CreditCard.Status.APPROVED);

		String cardNumber = generateCardNumber();
		String cardExpiry = generateExpiryDate();

		request.setCardNumber(cardNumber);
		request.setCardExpiry(cardExpiry);

		repository.save(request);
		return requestApproved + cardNumber;
	}

	private static Random random = new Random();

	private static String generateCardNumber() {
		int[] cardNumber = new int[16];

		// First digit (e.g., '4' for Visa)
		cardNumber[0] = 4;

		// Fill digits 1-14 randomly
		for (int i = 1; i < 15; i++) {
			cardNumber[i] = random.nextInt(10);
		}

		// Calculate checksum digit (last digit)
		cardNumber[15] = calculateCheckDigit(cardNumber);

		// Convert to string
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 16; i++) {
			sb.append(cardNumber[i]);
			if ((i + 1) % 4 == 0 && i != 15)
				sb.append(" ");
		}
		return sb.toString();
	}

	// Calculate the Luhn check digit for the first 15 digits
	private static int calculateCheckDigit(int[] digits) {
		int sum = 0;
		for (int i = 0; i < 15; i++) {
			int digit = digits[14 - i];
			if (i % 2 == 0) {
				digit *= 2;
				if (digit > 9)
					digit -= 9;
			}
			sum += digit;
		}
		int checkDigit = (10 - (sum % 10)) % 10;
		return checkDigit;
	}

	private static String generateExpiryDate() {
		LocalDate now = LocalDate.now();

		// Random year between now and 5 years later
		int year = now.getYear() + ThreadLocalRandom.current().nextInt(1, 6);
		// Random month 1-12
		int month = ThreadLocalRandom.current().nextInt(1, 13);

		// Format MM/YY
		return String.format("%02d/%02d", month, year % 100);
	}
}

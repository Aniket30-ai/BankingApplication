package com.nihilent.bank.serviceimpl;

import java.security.SecureRandom;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.CreditCard;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.CreditCardRepository;
import com.nihilent.bank.service.CreditCardService;

@Service
public class CreditCardServiceImpl implements CreditCardService {

	private static final Logger logger = LoggerFactory.getLogger(CreditCardServiceImpl.class);

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
		return byId.orElseThrow(() -> new NihilentBankException(invalidId));

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

	private static String generateCardNumber() {
		int[] cardNumber = new int[16];

		// First digit (e.g., '4' for Visa)
		cardNumber[0] = 4;

		// Fill digits 1-14 randomly
		for (int i = 1; i < 15; i++) {
			SecureRandom secureRandom = new SecureRandom();
			cardNumber[i] = secureRandom.nextInt(10);
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
		return (10 - (sum % 10)) % 10;

	}

	private static String generateExpiryDate() {

		SecureRandom secureRandom = new SecureRandom();

// Random year between now and 5 years later
		int year = YearMonth.now().getYear() + secureRandom.nextInt(5) + 1; // 1–5 years ahead

// Random month between 1 and 12
		int month = secureRandom.nextInt(12) + 1;
		// Format MM/YY
		return String.format("%02d/%02d", month, year % 100);
	}
}

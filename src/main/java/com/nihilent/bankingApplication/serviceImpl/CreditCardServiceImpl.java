package com.nihilent.bankingApplication.serviceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nihilent.bankingApplication.entity.BankAccount;
import com.nihilent.bankingApplication.entity.CreditCard;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.CreditCardRepository;

@Service
public class CreditCardServiceImpl {

	@Autowired
	private CreditCardRepository repository;

	// Apply for credit card
	public String applyForCard(Long accountNumber) {
		
		System.out.println(accountNumber);
//		 List<CreditCard> creditCardByAccountNumber = repository.findCreditCardByAccountNumber(accountNumber);
//		System.out.println("888888888888");
//		if (!creditCardByAccountNumber.isEmpty()) {
//			return "User has already applied.";
//		}
		CreditCard request = new CreditCard();
		
		BankAccount bankAccount = new BankAccount();
		
		bankAccount.setAccountNumber(accountNumber);
		request.setBankAccount(bankAccount);
		
		
//		request.setUserId(userId);
		request.setStatus(CreditCard.Status.PENDING);
		repository.save(request);
		return "Application submitted successfully.";
	}

	// Get credit card request by userId
	public Optional<List<CreditCard>> getRequestByUserId(Long accountNumber) {
		return Optional.ofNullable(repository.findCreditCardByAccountNumber(accountNumber));
		
		
	}
	
	
	
	public CreditCard showCreditCard(Long id) throws NihilentBankException {
		Optional<CreditCard> byId = repository.findById(id);
	CreditCard creditCard = byId.orElseThrow(()-> new NihilentBankException("Invalid id"));
	
		return creditCard;
		
	}

	// Get all pending requests (for admin)
	public List<CreditCard> getPendingRequests() {
		return repository.findByStatus(CreditCard.Status.PENDING);
	}

	// Approve a credit card request by id
	public String approveRequest(Long id) {
	    Optional<CreditCard> optionalRequest = repository.findById(id);
	    if (optionalRequest.isEmpty()) {
	        return "Request not found.";
	    }

	    CreditCard request = optionalRequest.get();
	    request.setStatus(CreditCard.Status.APPROVED);

	    String cardNumber = generateCardNumber();
	    String cardExpiry = generateExpiryDate();

	    request.setCardNumber(cardNumber);
	    request.setCardExpiry(cardExpiry);

	    repository.save(request);
	    return "Request approved with card number: " + cardNumber;
	}
	
	
	
	private static Random random = new Random();
	
	
	public static String generateCardNumber() {
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
            if ((i + 1) % 4 == 0 && i != 15) sb.append(" ");
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
                if (digit > 9) digit -= 9;
            }
            sum += digit;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit;
    }
    
    
    
    
    
    public static String generateExpiryDate() {
        LocalDate now = LocalDate.now();

        // Random year between now and 5 years later
        int year = now.getYear() + ThreadLocalRandom.current().nextInt(1, 6);
        // Random month 1-12
        int month = ThreadLocalRandom.current().nextInt(1, 13);

        // Format MM/YY
        return String.format("%02d/%02d", month, year % 100);
    }
}

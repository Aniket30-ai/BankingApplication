package com.nihilent.bank.serviceimpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nihilent.bank.dto.TransactionDto;
import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.DigitalBankAccount;
import com.nihilent.bank.entity.Transaction;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.repository.DigitalBankRepository;
import com.nihilent.bank.repository.TransactionRepository;
import com.nihilent.bank.service.TransactionService;
import com.nihilent.bank.validation.AccountValidation;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	private final BankAccountRepository accountRepository;

	private final DigitalBankRepository digitalBankRepository;

	private final TransactionRepository transactionRepository;

	private final KafkaTemplate<String, Transaction> kafkaTemplate;

	private final AccountValidation accountValidation;

	private static final String FAILED = "FAILED";
	private static final String DEBIT = "DEBIT";

	private static final String CREDIT = "CREDIT";

	public TransactionServiceImpl(BankAccountRepository accountRepository, DigitalBankRepository digitalBankRepository,
			TransactionRepository transactionRepository

			, AccountValidation accountValidation, KafkaTemplate<String, Transaction> kafkaTemplate

	) {
		this.accountRepository = accountRepository;
		this.digitalBankRepository = digitalBankRepository;
		this.transactionRepository = transactionRepository;
		this.accountValidation = accountValidation;
		this.kafkaTemplate = kafkaTemplate;

	}

	@Value("${TransactionService.Invalid_SenderAccountNumber}")
	private String invalidSenderAccountNumber;

	@Value("${TransactionService.Invalid_ReceiverAccountNumber}")
	private String invalidReceiverAccountNumber;

	@Value("${TransactionService.InsufficientBalance}")
	private String insufficientBalance;

	@Value("${TransactionService.Transaction_Credit}")
	private String transactionCredit;

	@Value("${TransactionService.Transaction_Debit}")
	private String transactionDebit;

	@Value("${TransactionService.Transaction_Success}")
	private String transactionSuccess;

	@Value("${TransactionService.Transaction_Limit}")
	private String transactionLimit;

	@Value("${TransactionService.Invalid_SenderUpiId}")
	private String invalidSenderUpiId;

	@Value("${TransactionService.Invalid_ReceiverUpiId}")
	private String invalidReceiverUpiId;

	@Value("${KAFKA_TOPIC}")
	private String kafkaTopic;

	@Override
	@Transactional(rollbackFor = NihilentBankException.class)
	public String fundTransfer(TransactionDto transactionDto) throws NihilentBankException {

		Long receivingAccountNumber = transactionDto.getReceivingAccountNumber();

		accountValidation.accountNumberLength(transactionDto.getSenderAccountNumber(),
				transactionDto.getReceivingAccountNumber());
		accountValidation.accountNumberValidation(transactionDto.getSenderAccountNumber(),
				transactionDto.getReceivingAccountNumber());
		accountValidation.accountStatusValidation(receivingAccountNumber);

		Optional<BankAccount> receiverAccountNumber = accountRepository
				.findByAccountNumber(transactionDto.getReceivingAccountNumber());

		BankAccount bankAccount = receiverAccountNumber
				.orElseThrow(() -> new NihilentBankException(invalidReceiverAccountNumber));

		Optional<BankAccount> senderAccountNumber = accountRepository
				.findByAccountNumber(transactionDto.getSenderAccountNumber());

		BankAccount bankAccount2 = senderAccountNumber
				.orElseThrow(() -> new NihilentBankException(invalidSenderAccountNumber));

		if (bankAccount2.getBalance() < transactionDto.getAmount()) {
			logger.error(insufficientBalance);

			throw new NihilentBankException(insufficientBalance);

		}

		bankAccount.setBalance(bankAccount.getBalance() + transactionDto.getAmount());
		bankAccount2.setBalance(bankAccount2.getBalance() - transactionDto.getAmount());

		accountRepository.save(bankAccount);
		accountRepository.save(bankAccount2);
		SecureRandom secureRandom = new SecureRandom();

		// Define IntSupplier for a 6-digit number (100_000 to 999_999)
		IntSupplier randomNumber = () -> 100_000 + secureRandom.nextInt(900_000);

		// Get the random int value
		int integer1 = randomNumber.getAsInt();
		int integer2 = randomNumber.getAsInt();

		String transactionId1 = "TNX" + integer1;
		String transactionId2 = "TNX" + integer2;

		Transaction localTransactionDebit = new Transaction();

		localTransactionDebit.setTransactionId(transactionId1);

		localTransactionDebit.setModeOfTransaction(transactionDto.getModeOfTransaction());

		localTransactionDebit.setAmount(transactionDto.getAmount());
		localTransactionDebit.setReceivingAccountNumber(transactionDto.getReceivingAccountNumber());

		localTransactionDebit.setRemark(transactionDto.getRemark());
		localTransactionDebit.setSenderAccountNumber(transactionDto.getSenderAccountNumber());

		localTransactionDebit.setDebit(transactionDto.getAmount());

		localTransactionDebit.setTransactionType(DEBIT);

		localTransactionDebit.setClosingBalance(bankAccount2.getBalance());
		Transaction save = transactionRepository.save(localTransactionDebit);

		String transactionId3 = "";
		if (save.getTransactionType().equalsIgnoreCase(DEBIT)) {
			transactionId3 = save.getTransactionId();

		}

		try {
			transactionRepository.save(localTransactionDebit);

		} catch (Exception e) {

			logger.error("Failed to save debit transaction", e);
		}
		logger.info(this.transactionDebit);

		Transaction LocalTransactionCredit = new Transaction();

		LocalTransactionCredit.setTransactionId(transactionId2);

		LocalTransactionCredit.setModeOfTransaction(transactionDto.getModeOfTransaction());

		LocalTransactionCredit.setAmount(transactionDto.getAmount());
		LocalTransactionCredit.setReceivingAccountNumber(transactionDto.getReceivingAccountNumber());

		LocalTransactionCredit.setRemark(transactionDto.getRemark());
		LocalTransactionCredit.setSenderAccountNumber(transactionDto.getSenderAccountNumber());

		LocalTransactionCredit.setTransactionType(CREDIT);
		LocalTransactionCredit.setCredit(transactionDto.getAmount());
		LocalTransactionCredit.setClosingBalance(bankAccount.getBalance());
		transactionRepository.save(LocalTransactionCredit);
		logger.info(this.transactionCredit);
		return transactionId3;
	}

	@Override
	public List<TransactionDto> alltransactionDetails(Long accountNumber) throws NihilentBankException {

		List<Transaction> list = transactionRepository.findAll();

		List<TransactionDto> transactionDtos = new ArrayList<>();
		list.forEach(transaction -> {
			TransactionDto transactionDto = new TransactionDto();

			transactionDto.setAmount(transaction.getAmount());
			transactionDto.setModeOfTransaction(transaction.getModeOfTransaction());
			transactionDto.setReceivingAccountNumber(transaction.getReceivingAccountNumber());

			transactionDto.setRemark(transaction.getRemark());
			transactionDto.setSenderAccountNumber(transaction.getSenderAccountNumber());

			transactionDto.setTransactionId(transaction.getTransactionId());
			transactionDto.setTransactionTime(transaction.getTransactionTime());
			transactionDto.setTransactionType(transaction.getTransactionType());
			transactionDtos.add(transactionDto);

		});

		return transactionDtos;
	}

	@Override
	public List<TransactionDto> transactionDetails(Long accountNumber) throws NihilentBankException {

		List<Transaction> list = transactionRepository.findBySenderAccountNumberOrReceivingAccountNumber(accountNumber,
				accountNumber);

		List<Transaction> collect = list.stream()
				.filter(tx -> (accountNumber.equals(tx.getSenderAccountNumber())
						&& DEBIT.equalsIgnoreCase(tx.getTransactionType()))
						|| (accountNumber.equals(tx.getReceivingAccountNumber())
								&& CREDIT.equalsIgnoreCase(tx.getTransactionType())))
				.collect(Collectors.toCollection(ArrayList::new));

		List<TransactionDto> transactionDtos = new ArrayList<>();
		collect.forEach(transaction -> {
			TransactionDto transactionDto = new TransactionDto();

			transactionDto.setAmount(transaction.getAmount());
			transactionDto.setModeOfTransaction(transaction.getModeOfTransaction());
			transactionDto.setReceivingAccountNumber(transaction.getReceivingAccountNumber());

			transactionDto.setRemark(transaction.getRemark());
			transactionDto.setSenderAccountNumber(transaction.getSenderAccountNumber());

			transactionDto.setTransactionId(transaction.getTransactionId());
			transactionDto.setTransactionTime(transaction.getTransactionTime());
			transactionDto.setTransactionType(transaction.getTransactionType());

			transactionDto.setClosingBalance(transaction.getClosingBalance());

			transactionDtos.add(transactionDto);

		});

		transactionDtos.sort(Comparator.comparing(TransactionDto::getTransactionTime));
		return transactionDtos;
	}

	public void parseFileAndSave(MultipartFile file) throws NumberFormatException, IOException, NihilentBankException {

		List<String> failedTransactions = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			String line;

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				Transaction records = new Transaction();

				SecureRandom secureRandom = new SecureRandom();

				// Generate a 3-digit transaction ID (100–999)
				int transactionId = 100 + secureRandom.nextInt(900);

				String transactionStr = String.valueOf(transactionId);
				// 100–999
				records.setTransactionId(transactionStr);

				if (parts.length < 4) {

					failedTransactions.add(line);
					continue;
				}

				try {
					Long senderAccNum = Long.parseLong(parts[0].trim());
					Double amount = Double.parseDouble(parts[1].trim());
					String remark = parts[2].trim();
					Long receiverAccNum = Long.parseLong(parts[3].trim());

					records.setSenderAccountNumber(senderAccNum);
					records.setReceivingAccountNumber(receiverAccNum);
					records.setAmount(amount);
					records.setRemark(remark);

					Optional<BankAccount> senderOpt = accountRepository.findByAccountNumber(senderAccNum);
					Optional<BankAccount> receiverOpt = accountRepository.findByAccountNumber(receiverAccNum);

					if (!senderOpt.isPresent() || !receiverOpt.isPresent()) {
						records.setStatus(FAILED);
						records.setErrorMesssage("Invalid sender or receiver account");
						records.setTransactionTime(LocalDateTime.now());
						failedTransactions.add(line + " --> Invalid account");
					} else {
						BankAccount sender = senderOpt.get();

						if (sender.getBalance() < amount) {
							records.setStatus(FAILED);
							records.setErrorMesssage("Insufficient balance");
							records.setTransactionTime(LocalDateTime.now());
							failedTransactions.add(line + " --> Insufficient balance");
						} else {

							TransactionDto transactionDto = new TransactionDto();

							transactionDto.setSenderAccountNumber(senderAccNum);
							transactionDto.setReceivingAccountNumber(receiverAccNum);
							transactionDto.setAmount(amount);
							transactionDto.setRemark(remark);
							transactionDto.setModeOfTransaction("Bulk Upload");

							this.fundTransfer(transactionDto);

							records.setStatus("SUCCESS");
							records.setTransactionTime(LocalDateTime.now());
						}
					}
				} catch (Exception e) {
					records.setStatus(FAILED);
					records.setErrorMesssage("Parse error: " + e.getMessage());
					records.setTransactionTime(LocalDateTime.now());
					failedTransactions.add(line + " --> Parse error");
				}

				// ✅ Send every record to Kafka (success or failure)
				kafkaTemplate.send(kafkaTopic, records);

			}
		}

		saveFailedTransactionsToFile(failedTransactions);

	}

	private void saveFailedTransactionsToFile(List<String> failedTransactions) throws IOException {
		Path failedFile = Paths.get("src/main/resources/failed_transactions.csv");

		try (BufferedWriter writer = Files.newBufferedWriter(failedFile, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING)) {
			for (String line : failedTransactions) {
				writer.write(line);
				writer.newLine();
			}
		}
	}

	@Override
	public List<Transaction> getTransactionsByAccountNumberAndDateRange(Long accountNumber, LocalDateTime startDate,
			LocalDateTime endDate) {
		// Example using JPA repository
		if (startDate != null && endDate != null) {

			List<Transaction> list = transactionRepository.findByAccountNumberAndTransactionTimeBetween(accountNumber,
					startDate, endDate);
			return list.stream()
					.filter(tx -> (accountNumber.equals(tx.getSenderAccountNumber())
							&& DEBIT.equalsIgnoreCase(tx.getTransactionType()))
							|| (accountNumber.equals(tx.getReceivingAccountNumber())
									&& CREDIT.equalsIgnoreCase(tx.getTransactionType())))
					.collect(Collectors.toCollection(ArrayList::new));

		} else {
			return transactionRepository.findBySenderAccountNumber(accountNumber);
		}
	}

	public String upiFundTransafer(String senderUpiID, String recevierUpiId, Double amount, String remark)
			throws NihilentBankException {

		LocalDate today = LocalDate.now();
		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

		// Fetch total amount sender has already sent today

		Optional<DigitalBankAccount> byDigitalBankId = digitalBankRepository.findByDigitalBankId(senderUpiID);

		DigitalBankAccount digitalBankAccount = byDigitalBankId
				.orElseThrow(() -> new RuntimeException("Account not found"));

		Long accountNumber = digitalBankAccount.getBankAccount().getAccountNumber();
		Double totalSentToday = transactionRepository.getTotalDebitedAmountForSenderBetween(accountNumber, startOfDay,
				endOfDay);

		if (totalSentToday == null) {
			totalSentToday = 0.0;
		}

		Double maxDailyLimit = 50000.0;

		if ((totalSentToday + amount) > maxDailyLimit) {
			logger.info(transactionLimit);
			throw new NihilentBankException(transactionLimit);
		}

		Optional<DigitalBankAccount> senderUpiId = digitalBankRepository.findByDigitalBankId(senderUpiID);

		Optional<DigitalBankAccount> reciverUpiId = digitalBankRepository.findByDigitalBankId(recevierUpiId);

		DigitalBankAccount senderDigitalBankAccount = senderUpiId
				.orElseThrow(() -> new NihilentBankException(invalidSenderUpiId));

		DigitalBankAccount reciverDigitalBankAccount = reciverUpiId
				.orElseThrow(() -> new NihilentBankException(invalidReceiverUpiId));

		Double senderBalance = senderDigitalBankAccount.getBankAccount().getBalance();

		reciverDigitalBankAccount.getBankAccount().getBalance();

		Long senderAccountNumber = senderDigitalBankAccount.getBankAccount().getAccountNumber();

		Long reciverAccountNumber = reciverDigitalBankAccount.getBankAccount().getAccountNumber();

		if (senderBalance < amount) {
			throw new NihilentBankException(insufficientBalance);
		}

		BankAccount reciverBankAccount = reciverDigitalBankAccount.getBankAccount();

		BankAccount senderBankAccount = senderDigitalBankAccount.getBankAccount();

		reciverBankAccount.setBalance(reciverBankAccount.getBalance() + amount);
		senderBankAccount.setBalance(senderBankAccount.getBalance() - amount);

		BankAccount reciverAccout = accountRepository.save(reciverBankAccount);
		BankAccount senderAccount = accountRepository.save(senderBankAccount);

		Transaction localTransactionDebit = new Transaction();

		SecureRandom secureRandom = new SecureRandom();

		// Define IntSupplier for a 6-digit number (100_000 to 999_999)
		IntSupplier randomNumber = () -> 100_000 + secureRandom.nextInt(900_000);
		
		int integer1 = randomNumber.getAsInt();
		String transactionId = "TNX" + integer1;

		
		
		// for sender

		localTransactionDebit.setModeOfTransaction("UPI");
		localTransactionDebit.setAmount(amount);

		localTransactionDebit.setTransactionId(transactionId);

		localTransactionDebit.setSenderAccountNumber(senderAccountNumber);

		localTransactionDebit.setReceivingAccountNumber(reciverAccountNumber);
		localTransactionDebit.setTransactionType(DEBIT);
		localTransactionDebit.setDebit(amount);
		localTransactionDebit.setRemark(remark);
		localTransactionDebit.setClosingBalance(senderAccount.getBalance());

		Transaction save = transactionRepository.save(localTransactionDebit);

		String transactionId3 = "";
		if (save.getTransactionType().equalsIgnoreCase(DEBIT)) {
			transactionId3 = save.getTransactionId();

		}

		// for reciver

		Transaction transactionCredits = new Transaction();

		int integer2 = randomNumber.getAsInt();
		String transactionId2 = "TNX" + integer2;

		transactionCredits.setModeOfTransaction("UPI");
		transactionCredits.setAmount(amount);

		transactionCredits.setTransactionId(transactionId2);

		transactionCredits.setReceivingAccountNumber(reciverAccountNumber);

		transactionCredits.setSenderAccountNumber(senderAccountNumber);

		transactionCredits.setTransactionType(CREDIT);
		transactionCredits.setCredit(amount);
		transactionCredits.setClosingBalance(reciverAccout.getBalance());
		transactionCredits.setRemark(remark);

		transactionRepository.save(transactionCredits);

		return transactionId3;
	}

}

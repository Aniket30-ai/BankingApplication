package com.nihilent.bankingApplication.serviceImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bankingApplication.Validation.AccountValidation;
import com.nihilent.bankingApplication.dto.TransactionDto;
import com.nihilent.bankingApplication.entity.BankAccount;
import com.nihilent.bankingApplication.entity.DigitalBankAccount;
import com.nihilent.bankingApplication.entity.Transaction;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.BankAccountRepository;
import com.nihilent.bankingApplication.repository.CustomerRepository;
import com.nihilent.bankingApplication.repository.DigitalBankRepository;
import com.nihilent.bankingApplication.repository.TransactionRepository;
import com.nihilent.bankingApplication.service.TransactionService;

//import co.elastic.clients.elasticsearch.ElasticsearchClient;
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Autowired
	private BankAccountRepository accountRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private DigitalBankRepository digitalBankRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	private static final String INDEX = "transaction";

	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private ObjectMapper objectMapper;

//
//    @Autowired
//    public ElasticCustomerData(RestHighLevelClient client) {
//        this.client = client;
//        this.objectMapper = new ObjectMapper();
//    }

	@Autowired
	private KafkaTemplate<String, Transaction> kafkaTemplate;

	@Autowired
	private AccountValidation accountValidation;

	@Override
	public String fundTransfer(TransactionDto transactionDto) throws NihilentBankException {
		// TODO Auto-generated method stub

		Long receivingAccountNumber = transactionDto.getReceivingAccountNumber();



		accountValidation.accountNumberLength(transactionDto.getSenderAccountNumber(),
				transactionDto.getReceivingAccountNumber());
		accountValidation.accountNumberValidation(transactionDto.getSenderAccountNumber(),
				transactionDto.getReceivingAccountNumber());
		accountValidation.accountStatusValidation(receivingAccountNumber);

		Optional<BankAccount> receiverAccountNumber = accountRepository
				.findByAccountNumber(transactionDto.getReceivingAccountNumber());

		BankAccount bankAccount = receiverAccountNumber
				.orElseThrow(() -> new NihilentBankException("Invalid receiver Account Number"));

		Optional<BankAccount> senderAccountNumber = accountRepository
				.findByAccountNumber(transactionDto.getSenderAccountNumber());

		BankAccount bankAccount2 = senderAccountNumber
				.orElseThrow(() -> new NihilentBankException("Invalid sender account number"));

		if (bankAccount2.getBalance() < transactionDto.getAmount()) {
			logger.error("Transaction failed due to insufficient balance");
			;

			throw new NihilentBankException("Transaction failed due to insufficient balance");

		}

		bankAccount.setBalance(bankAccount.getBalance() + transactionDto.getAmount());
		bankAccount2.setBalance(bankAccount2.getBalance() - transactionDto.getAmount());

		accountRepository.save(bankAccount);
		accountRepository.save(bankAccount2);

		Supplier<Integer> randomNumber = () -> (int) (Math.random() * 900000) + 100000;
		Integer integer1 = randomNumber.get();
		Integer integer2 = randomNumber.get();
		String transactionId1 = "TNX" + integer1.toString();
		String transactionId2 = "TNX" + integer2.toString();

	
		Transaction transactionDebit = new Transaction();

		transactionDebit.setTransactionId(transactionId1);

		transactionDebit.setModeOfTransaction(transactionDto.getModeOfTransaction());

		transactionDebit.setAmount(transactionDto.getAmount());
		transactionDebit.setReceivingAccountNumber(transactionDto.getReceivingAccountNumber());


		transactionDebit.setRemark(transactionDto.getRemark());
		transactionDebit.setSenderAccountNumber(transactionDto.getSenderAccountNumber());



		transactionDebit.setDebit(transactionDto.getAmount());


		transactionDebit.setTransactionType("DEBIT");

		transactionDebit.setClosingBalance(bankAccount2.getBalance());
		transactionRepository.save(transactionDebit);

		try {
			transactionRepository.save(transactionDebit);
			System.out.println("transaction debit saved");
		} catch (Exception e) {

			logger.error("Failed to save debit transaction: {}", e.getMessage());
		}
		logger.info("transaction debit");

		Transaction transactionCredit = new Transaction();

		transactionCredit.setTransactionId(transactionId2);

		transactionCredit.setModeOfTransaction(transactionDto.getModeOfTransaction());

		transactionCredit.setAmount(transactionDto.getAmount());
		transactionCredit.setReceivingAccountNumber(transactionDto.getReceivingAccountNumber());


		transactionCredit.setRemark(transactionDto.getRemark());
		transactionCredit.setSenderAccountNumber(transactionDto.getSenderAccountNumber());


		transactionCredit.setTransactionType("CREDIT");
		transactionCredit.setCredit(transactionDto.getAmount());
		transactionCredit.setClosingBalance(bankAccount.getBalance());
		transactionRepository.save(transactionCredit);
		logger.info("Transaction Creadit success");
		return "Transaction success";
	}

	@Override
	public List<TransactionDto> alltransactionDetails(Long accountNumber) throws NihilentBankException {
		// TODO Auto-generated method stub

		List<Transaction> list = transactionRepository.findAll();

		List<TransactionDto> transactionDtos = new ArrayList<TransactionDto>();
		list.forEach(transaction -> {
			TransactionDto transactionDto = new TransactionDto();

			transactionDto.setAmount(transaction.getAmount());
			transactionDto.setModeOfTransaction(transaction.getModeOfTransaction());
			transactionDto.setReceivingAccountNumber(transaction.getReceivingAccountNumber());
//			transactionDto.setReceivingMobileNumber(transaction.getReceivingMobileNumber());

			transactionDto.setRemark(transaction.getRemark());
			transactionDto.setSenderAccountNumber(transaction.getSenderAccountNumber());

//			transactionDto.setSenderMobileNumber(transaction.getSenderMobileNumber());
			transactionDto.setTransactionId(transaction.getTransactionId());
			transactionDto.setTransactionTime(transaction.getTransactionTime());
			transactionDto.setTransactionType(transaction.getTransactionType());
			transactionDtos.add(transactionDto);

		});

		return transactionDtos;
	}

	@Override
	public List<TransactionDto> transactionDetails(Long accountNumber) throws NihilentBankException {
		// TODO Auto-generated method stub


		List<Transaction> list = transactionRepository.findBySenderAccountNumberOrReceivingAccountNumber(accountNumber,
				accountNumber);



		List<Transaction> collect = list.stream()
				.filter(tx -> (accountNumber.equals(tx.getSenderAccountNumber())
						&& "DEBIT".equalsIgnoreCase(tx.getTransactionType()))
						|| (accountNumber.equals(tx.getReceivingAccountNumber())
								&& "CREDIT".equalsIgnoreCase(tx.getTransactionType())))
				.collect(Collectors.toList());

		List<TransactionDto> transactionDtos = new ArrayList<TransactionDto>();
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

		transactionDtos.stream().sorted(Comparator.comparing(TransactionDto::getTransactionTime));
		return transactionDtos;
	}

	public void parseFileAndSave(MultipartFile file) throws NumberFormatException, IOException, NihilentBankException {
		List<Transaction> txns = new ArrayList<>();
		List<String> failedTransactions = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
			String line;

			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				Transaction record = new Transaction();
				String transactionId = "TNX" + ((int) (Math.random() * 900000) + 100000);
				record.setTransactionId(transactionId);

				if (parts.length < 4) {
//		                record.setStatus("FAILED");
//		                record.setErrorMessage("Invalid format: Less than 4 fields");
//		                kafkaProducerService.send(record);
//		            	kafkaTemplate.send(KafkaConstant.TOPIC,record);
					failedTransactions.add(line);
					continue;
				}

				try {
					Long senderAccNum = Long.parseLong(parts[0].trim());
					Double amount = Double.parseDouble(parts[1].trim());
					String remark = parts[2].trim();
					Long receiverAccNum = Long.parseLong(parts[3].trim());

					record.setSenderAccountNumber(senderAccNum);
					record.setReceivingAccountNumber(receiverAccNum);
					record.setAmount(amount);
					record.setRemark(remark);

					Optional<BankAccount> senderOpt = accountRepository.findByAccountNumber(senderAccNum);
					Optional<BankAccount> receiverOpt = accountRepository.findByAccountNumber(receiverAccNum);

					if (!senderOpt.isPresent() || !receiverOpt.isPresent()) {
						record.setStatus("FAILED");
						record.setErrorMesssage("Invalid sender or receiver account");
						failedTransactions.add(line + " --> Invalid account");
					} else {
						BankAccount sender = senderOpt.get();
						BankAccount receiver = receiverOpt.get();

						if (sender.getBalance() < amount) {
							record.setStatus("FAILED");
							record.setErrorMesssage("Insufficient balance");
							failedTransactions.add(line + " --> Insufficient balance");
						} else {
							// All good — update and save transaction
							sender.setBalance(sender.getBalance() - amount);
							receiver.setBalance(receiver.getBalance() + amount);

							Transaction txn = new Transaction();
							txn.setTransactionId(transactionId);
							txn.setSenderAccountNumber(senderAccNum);
							txn.setReceivingAccountNumber(receiverAccNum);
							txn.setAmount(amount);
							txn.setRemark(remark);
							txn.setTransactionTime(LocalDateTime.now());

							txns.add(txn);
							record.setStatus("SUCCESS");
						}
					}
				} catch (Exception e) {
					record.setStatus("FAILED");
					record.setErrorMesssage("Parse error: " + e.getMessage());
					failedTransactions.add(line + " --> Parse error");
				}

				// ✅ Send every record to Kafka (success or failure)
//		            kafkaTemplate.send(KafkaConstant.TOPIC,record);

//		            kafkaProducerService.send(record);
			}
		}

		BulkRequest bulkRequest = new BulkRequest();

		for (Transaction tnx : txns) {
			Map<String, Object> doc = objectMapper.convertValue(tnx, new TypeReference<>() {
			});
			ObjectMapper mapper = new ObjectMapper();
			IndexRequest indexRequest = new IndexRequest(INDEX).id(tnx.getTransactionId()) // This becomes _id
					.source(doc); // ✅ this becomes _source

			logger.info("Uploading JSON: " + objectMapper.writeValueAsString(doc));

			bulkRequest.add(indexRequest);
			
			logger.info("data upload");

			

		}

		transactionRepository.saveAll(txns);
		logger.info("Data saved");
		saveFailedTransactionsToFile(failedTransactions);


//          BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
//

		try {
			BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
//	        if (bulkResponse.hasFailures()) {
//	            throw new IOException("Bulk upload failures: " + bulkResponse.buildFailureMessage());
//	        } else {
//	            System.out.println("✅ Bulk upload successful");
//	        }

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
			List<Transaction> collect = list.stream()
					.filter(tx -> (accountNumber.equals(tx.getSenderAccountNumber())
							&& "DEBIT".equalsIgnoreCase(tx.getTransactionType()))
							|| (accountNumber.equals(tx.getReceivingAccountNumber())
									&& "CREDIT".equalsIgnoreCase(tx.getTransactionType())))
					.collect(Collectors.toList());

			
			return collect;

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

		Long accountNumber = byDigitalBankId.get().getBankAccount().getAccountNumber();
		Double totalSentToday = transactionRepository.getTotalDebitedAmountForSenderBetween(accountNumber, startOfDay,
				endOfDay);

		if (totalSentToday == null) {
			totalSentToday = 0.0;
		}

		Double maxDailyLimit = 50000.0;

		if ((totalSentToday + amount) > maxDailyLimit) {
			System.out.println("Daily transaction limit of 50,000 exceeded.");
			throw new NihilentBankException("Daily transaction limit of 50,000 exceeded.");
		}

	
		Optional<DigitalBankAccount> senderUpiId = digitalBankRepository.findByDigitalBankId(senderUpiID);

		Optional<DigitalBankAccount> reciverUpiId = digitalBankRepository.findByDigitalBankId(recevierUpiId);



		DigitalBankAccount senderDigitalBankAccount = senderUpiId
				.orElseThrow(() -> new NihilentBankException("Invalid sender UPI ID"));

		DigitalBankAccount reciverDigitalBankAccount = reciverUpiId
				.orElseThrow(() -> new NihilentBankException("Invalid reciver UPI ID"));

		Double senderBalance = senderDigitalBankAccount.getBankAccount().getBalance();

		Double reciverBalance = reciverDigitalBankAccount.getBankAccount().getBalance();

		Long senderAccountNumber = senderDigitalBankAccount.getBankAccount().getAccountNumber();

		Long reciverAccountNumber = reciverDigitalBankAccount.getBankAccount().getAccountNumber();

	

		if (senderBalance < amount) {
			throw new NihilentBankException("Invalid Balance");
		}

		BankAccount reciverBankAccount = reciverDigitalBankAccount.getBankAccount();

		BankAccount senderBankAccount = senderDigitalBankAccount.getBankAccount();

		reciverBankAccount.setBalance(reciverBankAccount.getBalance() + amount);
		senderBankAccount.setBalance(senderBankAccount.getBalance() - amount);

		BankAccount reciverAccout = accountRepository.save(reciverBankAccount);
		BankAccount senderAccount = accountRepository.save(senderBankAccount);

		Transaction transactionDebit = new Transaction();

		Supplier<Integer> randomNumber = () -> (int) (Math.random() * 900000) + 100000;
		Integer integer = randomNumber.get();

		String transactionId = "TNX" + integer.toString();

		// for sender

		transactionDebit.setModeOfTransaction("UPI");
		transactionDebit.setAmount(amount);

		transactionDebit.setTransactionId(transactionId);

		transactionDebit.setSenderAccountNumber(senderAccountNumber);

		transactionDebit.setReceivingAccountNumber(reciverAccountNumber);
		transactionDebit.setTransactionType("DEBIT");
		transactionDebit.setDebit(amount);
		transactionDebit.setRemark(remark);
		transactionDebit.setClosingBalance(senderAccount.getBalance());


		transactionRepository.save(transactionDebit);

		// for reciver

		Transaction transactionCredit = new Transaction();

		Integer integer2 = randomNumber.get();

		String transactionId2 = "TNX" + integer2.toString();

		transactionCredit.setModeOfTransaction("UPI");
		transactionCredit.setAmount(amount);

		transactionCredit.setTransactionId(transactionId2);

		transactionCredit.setReceivingAccountNumber(reciverAccountNumber);

		transactionCredit.setSenderAccountNumber(senderAccountNumber);

		transactionCredit.setTransactionType("CREDIT");
		transactionCredit.setCredit(amount);
		transactionCredit.setClosingBalance(reciverAccout.getBalance());
		transactionCredit.setRemark(remark);
	
		transactionRepository.save(transactionCredit);

		return "Transaction Sucess";
	}

}

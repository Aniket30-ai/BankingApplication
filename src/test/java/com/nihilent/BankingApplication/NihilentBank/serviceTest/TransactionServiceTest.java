package com.nihilent.BankingApplication.NihilentBank.serviceTest;


import com.nihilent.bank.dto.TransactionDto;
import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.Transaction;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.repository.DigitalBankRepository;
import com.nihilent.bank.repository.TransactionRepository;
import com.nihilent.bank.serviceimpl.TransactionServiceImpl;
import com.nihilent.bank.validation.AccountValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository accountRepository;

    @Mock
    private DigitalBankRepository digitalBankRepository;

    @Mock
    private AccountValidation accountValidation;

    @Mock
    private KafkaTemplate<String, Transaction> kafkaTemplate;

    @InjectMocks
    private TransactionServiceImpl transactionServiceImpl;




    @BeforeEach
    void setup() {

        transactionServiceImpl.invalidSenderAccountNumber = "Invalid Sender Account Number";
        transactionServiceImpl.invalidReceiverAccountNumber = "Invalid Receiver Account Number";
        transactionServiceImpl.insufficientBalance = "Insufficient Balance";
        transactionServiceImpl.transactionDebit = "DEBIT SUCCESS";
        transactionServiceImpl.transactionCredit = "CREDIT SUCCESS";
        transactionServiceImpl.kafkaTopic = "test_topic";
    }

    @Test
    void testFundTransfer_Success() throws NihilentBankException {

        TransactionDto dto = new TransactionDto();
        dto.setSenderAccountNumber(1111L);
        dto.setReceivingAccountNumber(2222L);
        dto.setAmount(500.0);
        dto.setModeOfTransaction("ONLINE");
        dto.setRemark("Test Payment");

        // Sender Account
        BankAccount sender = new BankAccount();
        sender.setAccountNumber(1111L);
        sender.setBalance(2000.0);

        // Receiver Account
        BankAccount receiver = new BankAccount();
        receiver.setAccountNumber(2222L);
        receiver.setBalance(1000.0);

        when(accountRepository.findByAccountNumber(2222L))   // Receiver
                .thenReturn(Optional.of(receiver));

        when(accountRepository.findByAccountNumber(1111L))   // Sender
                .thenReturn(Optional.of(sender));

        // Mock saving debit transaction
        Transaction debit = new Transaction();
        debit.setTransactionId("TNX123456");
        debit.setTransactionType("DEBIT");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(debit);

        String txId = transactionServiceImpl.fundTransfer(dto);

        assertNotNull(txId);
        assertEquals("TNX123456", txId);

        verify(accountRepository, times(1)).findByAccountNumber(2222L);
        verify(accountRepository, times(1)).findByAccountNumber(1111L);
        verify(accountRepository, times(2)).save(any(BankAccount.class)); // updated balances
        verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
    }




    @Test
    void testFundTransfer_InvalidReceiverAccount() {

        TransactionDto dto = new TransactionDto();
        dto.setSenderAccountNumber(1111L);
        dto.setReceivingAccountNumber(2222L);

        when(accountRepository.findByAccountNumber(2222L))
                .thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(
                NihilentBankException.class,
                () -> transactionServiceImpl.fundTransfer(dto)
        );

        assertEquals("Invalid Receiver Account Number", ex.getMessage());
    }




    @Test
    void testFundTransfer_InvalidSenderAccount() {

        TransactionDto dto = new TransactionDto();
        dto.setSenderAccountNumber(1111L);
        dto.setReceivingAccountNumber(2222L);

        // Receiver exists
        BankAccount receiver = new BankAccount();
        receiver.setAccountNumber(2222L);

        when(accountRepository.findByAccountNumber(2222L))
                .thenReturn(Optional.of(receiver));

        // Sender missing
        when(accountRepository.findByAccountNumber(1111L))
                .thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(
                NihilentBankException.class,
                () -> transactionServiceImpl.fundTransfer(dto)
        );

        assertEquals("Invalid Sender Account Number", ex.getMessage());
    }




    @Test
    void testFundTransfer_InsufficientBalance() {

        TransactionDto dto = new TransactionDto();
        dto.setSenderAccountNumber(1111L);
        dto.setReceivingAccountNumber(2222L);
        dto.setAmount(5000.0); // more than available

        BankAccount receiver = new BankAccount();
        receiver.setAccountNumber(2222L);
        receiver.setBalance(1000.0);

        BankAccount sender = new BankAccount();
        sender.setAccountNumber(1111L);
        sender.setBalance(1000.0); // insufficient

        when(accountRepository.findByAccountNumber(2222L))
                .thenReturn(Optional.of(receiver));

        when(accountRepository.findByAccountNumber(1111L))
                .thenReturn(Optional.of(sender));

        NihilentBankException ex = assertThrows(
                NihilentBankException.class,
                () -> transactionServiceImpl.fundTransfer(dto)
        );

        assertEquals("Insufficient Balance", ex.getMessage());
    }



    @Test
    void testAllTransactionDetails_Success() throws NihilentBankException {

        // Create sample Transaction objects
        Transaction t1 = new Transaction();
        t1.setTransactionId("TXN001");
        t1.setAmount(500.0);
        t1.setSenderAccountNumber(1111L);
        t1.setReceivingAccountNumber(2222L);
        t1.setRemark("Payment 1");
        t1.setTransactionType("DEBIT");
        t1.setModeOfTransaction("ONLINE");
        t1.setTransactionTime(LocalDateTime.now());

        Transaction t2 = new Transaction();
        t2.setTransactionId("TXN002");
        t2.setAmount(700.0);
        t2.setSenderAccountNumber(3333L);
        t2.setReceivingAccountNumber(4444L);
        t2.setRemark("Payment 2");
        t2.setTransactionType("CREDIT");
        t2.setModeOfTransaction("OFFLINE");
        t2.setTransactionTime(LocalDateTime.now());

        // Mock repository
        when(transactionRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        // Call Service Method
        List<TransactionDto> result = transactionServiceImpl.alltransactionDetails(1111L);

        // Validate
        assertNotNull(result);
        assertEquals(2, result.size());

        // Validate first record
        TransactionDto dto1 = result.get(0);
        assertEquals("TXN001", dto1.getTransactionId());
        assertEquals(500.0, dto1.getAmount());
        assertEquals(1111L, dto1.getSenderAccountNumber());
        assertEquals(2222L, dto1.getReceivingAccountNumber());
        assertEquals("Payment 1", dto1.getRemark());
        assertEquals("DEBIT", dto1.getTransactionType());
        assertEquals("ONLINE", dto1.getModeOfTransaction());

        // Validate second record
        TransactionDto dto2 = result.get(1);
        assertEquals("TXN002", dto2.getTransactionId());
        assertEquals(700.0, dto2.getAmount());
        assertEquals(3333L, dto2.getSenderAccountNumber());
        assertEquals(4444L, dto2.getReceivingAccountNumber());
        assertEquals("Payment 2", dto2.getRemark());
        assertEquals("CREDIT", dto2.getTransactionType());
        assertEquals("OFFLINE", dto2.getModeOfTransaction());

        // verification
        verify(transactionRepository, times(1)).findAll();
    }



    @Test
    void testTransactionDetails_Success() throws NihilentBankException {

        Long accountNumber = 1111L;

        // Transaction 1 (Valid - Sender & DEBIT)
        Transaction t1 = new Transaction();
        t1.setTransactionId("T1");
        t1.setAmount(500.0);
        t1.setSenderAccountNumber(1111L);
        t1.setReceivingAccountNumber(2222L);
        t1.setTransactionType("DEBIT");
        t1.setModeOfTransaction("ONLINE");
        t1.setClosingBalance(1500.0);
        t1.setRemark("Debit test");
        t1.setTransactionTime(LocalDateTime.now().minusMinutes(10));

        // Transaction 2 (Valid - Receiver & CREDIT)
        Transaction t2 = new Transaction();
        t2.setTransactionId("T2");
        t2.setAmount(700.0);
        t2.setSenderAccountNumber(3333L);
        t2.setReceivingAccountNumber(1111L);
        t2.setTransactionType("CREDIT");
        t2.setModeOfTransaction("UPI");
        t2.setClosingBalance(2200.0);
        t2.setRemark("Credit test");
        t2.setTransactionTime(LocalDateTime.now().minusMinutes(5));

        // Transaction 3 (Invalid - Sender but CREDIT → should NOT appear)
        Transaction t3 = new Transaction();
        t3.setTransactionId("T3");
        t3.setAmount(300.0);
        t3.setSenderAccountNumber(1111L);
        t3.setReceivingAccountNumber(4444L);
        t3.setTransactionType("CREDIT"); // ❌ invalid for sender
        t3.setTransactionTime(LocalDateTime.now().minusMinutes(2));

        // Transaction 4 (Invalid - Receiver but DEBIT → should NOT appear)
        Transaction t4 = new Transaction();
        t4.setTransactionId("T4");
        t4.setAmount(1000.0);
        t4.setSenderAccountNumber(5555L);
        t4.setReceivingAccountNumber(1111L);
        t4.setTransactionType("DEBIT"); // ❌ invalid for receiver
        t4.setTransactionTime(LocalDateTime.now().minusMinutes(1));

        List<Transaction> dbList = Arrays.asList(t1, t2, t3, t4);

        when(transactionRepository.findBySenderAccountNumberOrReceivingAccountNumber(accountNumber, accountNumber))
                .thenReturn(dbList);

        // ----------- CALL METHOD -----------
        List<TransactionDto> result = transactionServiceImpl.transactionDetails(accountNumber);

        // ----------- VALIDATION -----------
        assertNotNull(result);
        assertEquals(2, result.size(), "Only 2 valid transactions expected");

        // Ensure results are sorted by time ascending
        assertEquals("T1", result.get(0).getTransactionId());
        assertEquals("T2", result.get(1).getTransactionId());

        // Validate T1 (Sender DEBIT)
        TransactionDto dto1 = result.get(0);
        assertEquals(500.0, dto1.getAmount());
        assertEquals("DEBIT", dto1.getTransactionType());
        assertEquals(1111L, dto1.getSenderAccountNumber());
        assertEquals(2222L, dto1.getReceivingAccountNumber());

        // Validate T2 (Receiver CREDIT)
        TransactionDto dto2 = result.get(1);
        assertEquals(700.0, dto2.getAmount());
        assertEquals("CREDIT", dto2.getTransactionType());
        assertEquals(3333L, dto2.getSenderAccountNumber());
        assertEquals(1111L, dto2.getReceivingAccountNumber());

        verify(transactionRepository, times(1))
                .findBySenderAccountNumberOrReceivingAccountNumber(accountNumber, accountNumber);
    }





}


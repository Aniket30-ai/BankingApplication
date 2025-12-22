package com.nihilent.BankingApplication.NihilentBank.serviceTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nihilent.bank.entity.AuditLog;
import com.nihilent.bank.entity.Transaction;
import com.nihilent.bank.repository.AuditRepository;
import com.nihilent.bank.serviceimpl.AuditLogServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {


    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private Transaction transaction;

    @BeforeEach
    void setUp() {

        transaction = new Transaction();
        transaction.setTransactionId("TNX123456");
        transaction.setSenderAccountNumber(1111L);
        transaction.setReceivingAccountNumber(2222L);
        transaction.setAmount(500.0);
        transaction.setRemark("Test Transaction");
        transaction.setStatus("SUCCESS");
        transaction.setErrorMesssage(null);
    }

    @Test
    void testConsumeTransaction_SavesAuditLog() {

        AuditLog savedLog = new AuditLog();

        when(auditRepository.save(any(AuditLog.class))).thenReturn(savedLog);

        auditLogService.consumeTransaction(transaction);

        // Verify repository call
        verify(auditRepository, times(1)).save(any(AuditLog.class));

        // Capture saved log for field-level assertions
        var logCaptor = org.mockito.ArgumentCaptor.forClass(AuditLog.class);
        verify(auditRepository).save(logCaptor.capture());

        AuditLog log = logCaptor.getValue();


        assertEquals("TNX123456", log.getTransactionId());
        assertEquals(1111L, log.getSenderAccountNumber());
        assertEquals(2222L, log.getReceivingAccountNumber());
        assertEquals(500.0, log.getAmount());
        assertEquals("Test Transaction", log.getRemark());
        assertEquals("SUCCESS", log.getStatus());
        assertNull(log.getErrorMesssage());

        // transactionTime is dynamic → just check it's not null
        assertNotNull(log.getTransactionTime());
    }
}

package com.nihilent.BankingApplication.NihilentBank.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.Transaction;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.repository.TransactionRepository;
import com.nihilent.bank.serviceimpl.BillPaymentsImpl;

@ExtendWith(MockitoExtension.class)
class BillPaymentServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository accountRepository;

    @InjectMocks
    private BillPaymentsImpl billPayments;

    private BankAccount account;

    private final String success = "Success";
    private final String insufficientBalance = "Insufficient Balance";
    private final String accountNotFound = "Account Not Found";

    @BeforeEach
    void setUp() {

        // Setup a bank account with balance
        account = new BankAccount();
        account.setAccountNumber(123L);
        account.setBalance(1000.0);

        // Injecting values as they would be read from @Value
        billPayments.success = success;
        billPayments.insufficientBalance = insufficientBalance;
        billPayments.accountNotFound = accountNotFound;
    }

    @Test
    void testMobileRecharge_Success() throws Exception {
        when(accountRepository.findByAccountNumber(123L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(BankAccount.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        String result = billPayments.mobileRecharge(9999999999L, 500.0, "Recharge-", 123L);

        assertEquals(success, result);
        assertEquals(500.0, account.getBalance()); // balance reduced
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(accountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    void testMobileRecharge_InsufficientBalance() {
        account.setBalance(100.0);
        when(accountRepository.findByAccountNumber(123L)).thenReturn(Optional.of(account));

        NihilentBankException exception = assertThrows(NihilentBankException.class, () -> {
            billPayments.mobileRecharge(9999999999L, 500.0, "Recharge-", 123L);
        });

        assertEquals(insufficientBalance, exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testMobileRecharge_AccountNotFound() {
        when(accountRepository.findByAccountNumber(123L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            billPayments.mobileRecharge(9999999999L, 500.0, "Recharge-", 123L);
        });

        assertEquals(accountNotFound, exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    // Similarly, you can write tests for dthRecharge
    @Test
    void testDthRecharge_Success() throws Exception {
        when(accountRepository.findByAccountNumber(123L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(BankAccount.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        String result = billPayments.dthRecharge(55555L, 200.0, "DTH-", 123L);

        assertEquals(success, result);
        assertEquals(800.0, account.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // And for electricityBill
    @Test
    void testElectricityBill_Success() throws Exception {
        when(accountRepository.findByAccountNumber(123L)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(BankAccount.class))).thenReturn(account);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        String result = billPayments.electricityBill(101010L, 300.0, "Electricity-", 123L);

        assertEquals(success, result);
        assertEquals(700.0, account.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

}

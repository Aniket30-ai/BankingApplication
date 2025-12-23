package com.nihilent.BankingApplication.NihilentBank.serviceTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.nihilent.bank.entity.AccountStatus;
import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.validation.AccountValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AccountValidationTest {

    @Mock
    private BankAccountRepository accountRepository;

    @InjectMocks
    private AccountValidation accountValidation;

    private BankAccount activeAccount;
    private BankAccount inactiveAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        activeAccount = new BankAccount();
        activeAccount.setAccountNumber(1234567L);
        activeAccount.setAccountStatus(AccountStatus.ACTIVE);

        inactiveAccount = new BankAccount();
        inactiveAccount.setAccountNumber(7654321L);
        inactiveAccount.setAccountStatus(AccountStatus.IN_ACTIVE);
    }

    // ================= accountNumberValidation =================

    @Test
    void accountNumberValidation_success() throws NihilentBankException {
        when(accountRepository.findByAccountNumber(123L)).thenReturn(Optional.of(activeAccount));
        when(accountRepository.findByAccountNumber(456L)).thenReturn(Optional.of(activeAccount));

        assertTrue(accountValidation.accountNumberValidation(123L, 456L));
    }

    @Test
    void accountNumberValidation_invalidSender_throwsException() {
        when(accountRepository.findByAccountNumber(123L)).thenReturn(Optional.empty());

        NihilentBankException exception = assertThrows(NihilentBankException.class,
                () -> accountValidation.accountNumberValidation(123L, 456L));

        assertEquals("Invalid Sender Account Number", exception.getMessage());
    }

    @Test
    void accountNumberValidation_invalidReceiver_throwsException() {
        when(accountRepository.findByAccountNumber(123L)).thenReturn(Optional.of(activeAccount));
        when(accountRepository.findByAccountNumber(456L)).thenReturn(Optional.empty());

        NihilentBankException exception = assertThrows(NihilentBankException.class,
                () -> accountValidation.accountNumberValidation(123L, 456L));

        assertEquals("Invalid receiver Account Number", exception.getMessage());
    }

    // ================= accountStatusValidation =================

    @Test
    void accountStatusValidation_activeAccount_success() throws NihilentBankException {
        when(accountRepository.findByAccountNumber(123L)).thenReturn(Optional.of(activeAccount));

        assertTrue(accountValidation.accountStatusValidation(123L));
    }

    @Test
    void accountStatusValidation_inactiveAccount_throwsException() {
        when(accountRepository.findByAccountNumber(7654321L)).thenReturn(Optional.of(inactiveAccount));

        NihilentBankException exception = assertThrows(NihilentBankException.class,
                () -> accountValidation.accountStatusValidation(7654321L));

        assertEquals("Invalid Account Number", exception.getMessage());
    }

    @Test
    void accountStatusValidation_accountNotFound_throwsRuntimeException() {
        when(accountRepository.findByAccountNumber(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountValidation.accountStatusValidation(999L));

        assertEquals("Account not found", exception.getMessage());
    }

    // ================= accountNumberLength =================

    @Test
    void accountNumberLength_validLength_success() throws NihilentBankException {
        assertTrue(accountValidation.accountNumberLength(1234567L, 7654321L));
    }

    @Test
    void accountNumberLength_senderTooLong_throwsException() {
        NihilentBankException exception = assertThrows(NihilentBankException.class,
                () -> accountValidation.accountNumberLength(123456789L, 123L));

        assertEquals("Invalid Sender Account Number: exceed 7 digits", exception.getMessage());
    }

    @Test
    void accountNumberLength_receiverTooLong_throwsException() {
        NihilentBankException exception = assertThrows(NihilentBankException.class,
                () -> accountValidation.accountNumberLength(123L, 123456789L));

        assertEquals("Invalid Receiver Account Number: exceed 7 digits", exception.getMessage());
    }
}


package com.nihilent.BankingApplication.NihilentBank.serviceTest;

import static org.mockito.ArgumentMatchers.anyLong;
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

import com.nihilent.bank.entity.DigitalBankAccount;
import com.nihilent.bank.repository.DigitalBankRepository;
import com.nihilent.bank.serviceimpl.DeactivateUpiIdServiceImpl;

@ExtendWith(MockitoExtension.class)
class DeactivateUpiIServiceTest {

    @Mock
    private DigitalBankRepository digitalBankRepository;

    @InjectMocks
    private DeactivateUpiIdServiceImpl deactivateUpiIdService;

    private DigitalBankAccount digitalBankAccount;

    @BeforeEach
    void setup() {

        digitalBankAccount = new DigitalBankAccount();
        digitalBankAccount.setDigitalBankId("99999@BANK");
    }

    // ---------------------------------------------------------
    // 🔹 TEST: deactivateUpiId() — ACCOUNT EXISTS
    // ---------------------------------------------------------
    @Test
    void testDeactivateUpiId_AccountExists() {

        when(digitalBankRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(digitalBankAccount));

        deactivateUpiIdService.deactivateUpiId(12345L);

        // Should delete
        verify(digitalBankRepository, times(1)).deleteByAccountNumber(12345L);
    }

    // ---------------------------------------------------------
    // 🔹 TEST: deactivateUpiId() — ACCOUNT DOES NOT EXIST
    // ---------------------------------------------------------
    @Test
    void testDeactivateUpiId_AccountNotFound() {

        when(digitalBankRepository.findByAccountNumber(12345L)).thenReturn(Optional.empty());

        deactivateUpiIdService.deactivateUpiId(12345L);

        // Should NOT delete
        verify(digitalBankRepository, never()).deleteByAccountNumber(anyLong());
    }
}

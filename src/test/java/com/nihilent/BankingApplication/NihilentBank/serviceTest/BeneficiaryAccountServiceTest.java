package com.nihilent.BankingApplication.NihilentBank.serviceTest;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.BeneficiaryAccount;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BeneficiaryAccountRepository;
import com.nihilent.bank.serviceimpl.BeneficiaryAccountServiceImpl;

@ExtendWith(MockitoExtension.class)
class BeneficiaryAccountServiceTest {


    @Mock
    private BeneficiaryAccountRepository accountRepository;

    @InjectMocks
    private BeneficiaryAccountServiceImpl beneficiaryService;

    private BeneficiaryAccount beneficiary;

    @BeforeEach
    void setUp() {


        // Inject @Value fields using Reflection
        ReflectionTestUtils.setField(beneficiaryService, "beneficiaryPresent", "Beneficiary Already Exists");
        ReflectionTestUtils.setField(beneficiaryService, "beneficiarySuccess", "Beneficiary Added Successfully");
        ReflectionTestUtils.setField(beneficiaryService, "beneficiaryNotFound", "Beneficiary Not Found");
        ReflectionTestUtils.setField(beneficiaryService, "beneficiaryAccountDelete", "Beneficiary Deleted");

        // Mock entity
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(11111L);



        Customer customer = new  Customer();
        customer.setName("John Doe");

        beneficiary = new BeneficiaryAccount();
        beneficiary.setId(1L);

        bankAccount.setCustomer(customer);
        beneficiary.setBankAccount(bankAccount);

    }

    // ---------------------------------------------------------------------
    // 🔹 TEST addBeneficiary() — SUCCESS
    // ---------------------------------------------------------------------
    @Test
    void testAddBeneficiary_Success() throws Exception {

        when(accountRepository.findByAccountNumber(11111L)).thenReturn(Optional.empty());
        when(accountRepository.save(any(BeneficiaryAccount.class))).thenReturn(beneficiary);

        BeneficiaryAccount result = beneficiaryService.addBeneficiary(beneficiary);

        assertNotNull(result);
        assertEquals("John Doe", result.getBankAccount().getCustomer().getName());
        verify(accountRepository, times(1)).save(beneficiary);
    }

    // ---------------------------------------------------------------------
    // 🔹 TEST addBeneficiary() — BENEFICIARY ALREADY EXISTS
    // ---------------------------------------------------------------------
    @Test
    void testAddBeneficiary_AlreadyExists() {

        when(accountRepository.findByAccountNumber(11111L)).thenReturn(Optional.of(beneficiary));

        NihilentBankException ex = assertThrows(NihilentBankException.class, () ->
                beneficiaryService.addBeneficiary(beneficiary)
        );

        assertEquals("Beneficiary Already Exists", ex.getMessage());
        verify(accountRepository, never()).save(any());
    }

    // ---------------------------------------------------------------------
    // 🔹 TEST getAllBeneficiaries() — SUCCESS
    // ---------------------------------------------------------------------
    @Test
    void testGetAllBeneficiaries_Success() throws Exception {

        when(accountRepository.findAll()).thenReturn(List.of(beneficiary));

        List<BeneficiaryAccount> result = beneficiaryService.getAllBeneficiaries();

        assertEquals(1, result.size());
        verify(accountRepository, times(1)).findAll();
    }

    // ---------------------------------------------------------------------
    // 🔹 TEST getAllBeneficiaries() — NO BENEFICIARIES FOUND
    // ---------------------------------------------------------------------
    @Test
    void testGetAllBeneficiaries_NotFound() {

        when(accountRepository.findAll()).thenReturn(Collections.emptyList());

        NihilentBankException ex = assertThrows(NihilentBankException.class, () ->
                beneficiaryService.getAllBeneficiaries()
        );

        assertEquals("Beneficiary Not Found", ex.getMessage());
    }

    // ---------------------------------------------------------------------
    // 🔹 TEST deleteBeneficiaryAccount() — SUCCESS
    // ---------------------------------------------------------------------
    @Test
    void testDeleteBeneficiary_Success() throws Exception {

        when(accountRepository.findById(1L)).thenReturn(Optional.of(beneficiary));

        String result = beneficiaryService.deleteBeneficiayAccount(1L);

        assertEquals("Beneficiary Deleted", result);
        verify(accountRepository, times(1)).deleteById(1L);
    }

    // ---------------------------------------------------------------------
    // 🔹 TEST deleteBeneficiaryAccount() — NOT FOUND
    // ---------------------------------------------------------------------
    @Test
    void testDeleteBeneficiary_NotFound() {

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(NihilentBankException.class, () ->
                beneficiaryService.deleteBeneficiayAccount(1L)
        );

        assertEquals("Beneficiary Not Found", ex.getMessage());
        verify(accountRepository, never()).deleteById(any());
    }
}

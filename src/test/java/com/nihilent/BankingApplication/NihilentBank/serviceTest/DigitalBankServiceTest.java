package com.nihilent.BankingApplication.NihilentBank.serviceTest;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nihilent.bank.entity.AccountType;
import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.entity.DigitalBankAccount;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.repository.CustomerRepository;
import com.nihilent.bank.repository.DigitalBankRepository;
import com.nihilent.bank.serviceimpl.DigitalBankServiceImpl;

@ExtendWith(MockitoExtension.class)
class DigitalBankServiceTest {


    @Mock
    private DigitalBankRepository digitalBankRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BankAccountRepository accountRepository;


    @InjectMocks
    private DigitalBankServiceImpl digitalBankService;





    private Customer customer;
    private BankAccount bankAccount;
    private DigitalBankAccount digitalBankAccount;

    @BeforeEach
    void setUp() {

        // Inject @Value fields manually
        ReflectionTestUtils.setField(digitalBankService, "invalidMobileNumber", "Invalid Mobile");
        ReflectionTestUtils.setField(digitalBankService, "invalidAccountNumber", "Invalid Account Number");
        ReflectionTestUtils.setField(digitalBankService, "failedQrGenerate", "QR Generation Failed");
        ReflectionTestUtils.setField(digitalBankService, "invalidUpiId", "Invalid UPI");

        // Setup reusable mock objects
        customer = new Customer();
        customer.setName("John");
        customer.setMobileNumber(99999L);

        bankAccount = new BankAccount();
        bankAccount.setAccountNumber(12345L);
        bankAccount.setBankName("HDFC");
        bankAccount.setAccountType(AccountType.SAVING);

        digitalBankAccount = new DigitalBankAccount();
        digitalBankAccount.setDigitalBankId("99999@HDFC");
        digitalBankAccount.setQrCodeImage("qr".getBytes());
    }

    // ----------------------------------------------------------
    // 🔹 TEST linkAccount() — SUCCESS
    // ----------------------------------------------------------
    @Test
    void testLinkAccount_Success() throws Exception {

        when(customerRepository.findByMobileNumber(99999L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(bankAccount));

        // Mock QR generator (we don't generate real QR in tests)
        DigitalBankServiceImpl spyService = Mockito.spy(digitalBankService);
        doReturn("fake-qr".getBytes()).when(spyService).generateQRCodeImage(anyString(), anyInt(), anyInt(), anyString());

        when(digitalBankRepository.save(any(DigitalBankAccount.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String upiId = spyService.linkAccount(99999L, 12345L);

        assertEquals("99999@HDFC", upiId);
        verify(digitalBankRepository, times(1)).save(any());
    }

    // ----------------------------------------------------------
    // 🔹 TEST linkAccount() — CUSTOMER NOT FOUND
    // ----------------------------------------------------------
    @Test
    void testLinkAccount_InvalidMobile() {

        when(customerRepository.findByMobileNumber(99999L)).thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(NihilentBankException.class, () ->
                digitalBankService.linkAccount(99999L, 12345L)
        );

        assertEquals("Invalid Mobile", ex.getMessage());
    }

    // ----------------------------------------------------------
    // 🔹 TEST linkAccount() — ACCOUNT NOT FOUND
    // ----------------------------------------------------------
    @Test
    void testLinkAccount_InvalidAccount() {

        when(customerRepository.findByMobileNumber(99999L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(NihilentBankException.class, () ->
                digitalBankService.linkAccount(99999L, 12345L)
        );

        assertEquals("Invalid Account Number", ex.getMessage());
    }

    // ----------------------------------------------------------
    // 🔹 TEST linkAccount() — QR GENERATION FAILS
    // ----------------------------------------------------------
    @Test
    void testLinkAccount_QrFailure() throws Exception {

        when(customerRepository.findByMobileNumber(99999L)).thenReturn(Optional.of(customer));
        when(accountRepository.findByAccountNumber(12345L)).thenReturn(Optional.of(bankAccount));

        DigitalBankServiceImpl spyService = Mockito.spy(digitalBankService);

        doThrow(new RuntimeException("QR Error")).when(spyService)
                .generateQRCodeImage(anyString(), anyInt(), anyInt(), anyString());

        NihilentBankException ex = assertThrows(NihilentBankException.class, () ->
                spyService.linkAccount(99999L, 12345L)
        );

        assertEquals("QR Generation Failed", ex.getMessage());
    }

    // ----------------------------------------------------------
    // 🔹 TEST getQRCode() — SUCCESS
    // ----------------------------------------------------------
    @Test
    void testGetQRCode_Success() throws Exception {

        when(digitalBankRepository.findByDigitalBankId("99999@HDFC"))
                .thenReturn(Optional.of(digitalBankAccount));

        byte[] result = digitalBankService.getQRCode("99999@HDFC");

        assertArrayEquals("qr".getBytes(), result);
    }

    // ----------------------------------------------------------
    // 🔹 TEST getQRCode() — INVALID UPI
    // ----------------------------------------------------------
    @Test
    void testGetQRCode_InvalidUpi() {

        when(digitalBankRepository.findByDigitalBankId("invalid")).thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(NihilentBankException.class, () ->
                digitalBankService.getQRCode("invalid")
        );

        assertEquals("Invalid UPI", ex.getMessage());
    }

    // ----------------------------------------------------------
    // 🔹 TEST findUpiId() — SUCCESS
    // ----------------------------------------------------------
    @Test
    void testFindUpiId_Success() throws Exception {

        when(digitalBankRepository.findByAccountNumber(12345L))
                .thenReturn(Optional.of(digitalBankAccount));

        String upi = digitalBankService.findUpiId(12345L);

        assertEquals("99999@HDFC", upi);
    }

    // ----------------------------------------------------------
    // 🔹 TEST findUpiId() — FAIL
    // ----------------------------------------------------------
    @Test
    void testFindUpiId_InvalidAccount() {

        when(digitalBankRepository.findByAccountNumber(12345L)).thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(NihilentBankException.class, () ->
                digitalBankService.findUpiId(12345L)
        );

        assertEquals("Invalid Account Number", ex.getMessage());
    }
}


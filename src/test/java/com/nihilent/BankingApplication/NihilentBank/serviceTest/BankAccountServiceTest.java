package com.nihilent.BankingApplication.NihilentBank.serviceTest;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nihilent.bank.dto.BankAccountDto;
import com.nihilent.bank.dto.BankAccountRequestDto;
import com.nihilent.bank.dto.CustomerDto;
import com.nihilent.bank.entity.AccountRequestStatus;
import com.nihilent.bank.entity.AccountStatus;
import com.nihilent.bank.entity.AccountType;
import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.BankAccountRequest;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.repository.BankAccountRequestRepository;
import com.nihilent.bank.repository.CustomerRepository;
import com.nihilent.bank.service.DeactivateUpiIdService;
import com.nihilent.bank.serviceimpl.BankAccountServiceImpl;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BankAccountRequestRepository accountRequestRepository;

    @Mock
    private DeactivateUpiIdService deactivateUpiIdService;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    @BeforeEach
    void setUp() {

        bankAccountService.accountNotFound = "Account Not Found";
        bankAccountService.invalidMobileNumber = "Invalid Mobile Number";

        bankAccountService.accountDeleted = "Account deleted successfully";
        bankAccountService.accountProcessed="Account already processed";
        bankAccountService.invalidAccountNumber="Invalid Account Number";
    }

    @Test
    void testCreateAccount_Success() throws NihilentBankException {
        Customer customer = new Customer();
        customer.setCustomerId("CH001");
        customer.setMobileNumber(9999999999L);

        when(customerRepository.findByMobileNumber(9999999999L)).thenReturn(Optional.of(customer));

        BankAccountDto dto = new BankAccountDto();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setMobileNumber(9999999999L);
        dto.setCustomerDto(customerDto);
        dto.setAccountStatus(AccountStatus.ACTIVE);
        dto.setAccountType(AccountType.SAVING);

        dto.setBalance(1000.0);

        BankAccount savedAccount = new BankAccount();
        savedAccount.setAccountNumber(12345678L);

        when(accountRepository.save(any(BankAccount.class))).thenReturn(savedAccount);

        Long accountNumber = bankAccountService.createAccount(dto);
        assertEquals(12345678L, accountNumber);

        verify(accountRepository, times(1)).save(any(BankAccount.class));
    }

    @Test
    void testCreateAccount_InvalidMobile() {
        BankAccountDto dto = new BankAccountDto();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setMobileNumber(8888888888L);
        dto.setCustomerDto(customerDto);

        when(customerRepository.findByMobileNumber(8888888888L)).thenReturn(Optional.empty());

        NihilentBankException exception = assertThrows(NihilentBankException.class,
                () -> bankAccountService.createAccount(dto));

        assertEquals("Invalid Mobile Number", exception.getMessage());
    }

    @Test
    void testGetAccountDetails_Success() throws NihilentBankException {
        BankAccount account = new BankAccount();
        account.setAccountNumber(1111L);
        account.setAccountStatus(null);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setAccountType(AccountType.SAVING);

        account.setBalance(5000.0);

        Customer customer = new Customer();
        customer.setCustomerId("CH001");
        customer.setMobileNumber(9999999999L);
        account.setCustomer(customer);

        when(accountRepository.findByAccountNumber(1111L)).thenReturn(Optional.of(account));

        BankAccountDto dto = bankAccountService.getAccountDetails(1111L);
        assertNotNull(dto);
        assertEquals(1111L, dto.getAccountNumber());
        assertEquals(AccountStatus.ACTIVE, dto.getAccountStatus());
    }

    @Test
    void testAccountDelete_Success() throws NihilentBankException {
        BankAccount account = new BankAccount();
        account.setAccountNumber(2222L);

        when(accountRepository.findByAccountNumber(2222L)).thenReturn(Optional.of(account));

        doNothing().when(deactivateUpiIdService).deactivateUpiId(2222L);
        doNothing().when(accountRepository).deleteByAccountNumber(2222L);

        String result = bankAccountService.accountDelete(2222L);
        assertEquals("Account deleted successfully", result);

        verify(deactivateUpiIdService, times(1)).deactivateUpiId(2222L);
        verify(accountRepository, times(1)).deleteByAccountNumber(2222L);
    }

    @Test
    void testApplyBankAccount_Success() throws NihilentBankException {
        BankAccountRequest request = new BankAccountRequest();
        when(accountRequestRepository.save(any(BankAccountRequest.class))).thenReturn(request);

        String result = bankAccountService.applyBankAccount(9999999999L, "John Doe", "SAVINGS");
        assertEquals("Request Send", result);

        verify(accountRequestRepository, times(1)).save(any(BankAccountRequest.class));
    }

    @Test
    void testGetAccountStatus_Success() throws NihilentBankException {

        Long mobile = 9999999999L;

        BankAccountRequest request = new BankAccountRequest();
        request.setAccountId(101L);
        request.setAccountType("SAVINGS");
        request.setApplicantName("John Doe");
        request.setApplicationDate(LocalDate.now());
        request.setStatus(AccountRequestStatus.PENDING);
        request.setMobileNumber(mobile);

        when(accountRequestRepository.findByMobileNumber(mobile)).thenReturn(Optional.of(request));

        BankAccountRequestDto dto = bankAccountService.getAccountStatus(mobile);

        assertNotNull(dto);
        assertEquals(101L, dto.getAccountId());
        assertEquals("SAVINGS", dto.getAccountType());
        assertEquals("John Doe", dto.getApplicantName());
        assertEquals(AccountRequestStatus.PENDING, dto.getStatus());
        assertEquals(mobile, dto.getMobileNumber());

        verify(accountRequestRepository, times(1)).findByMobileNumber(mobile);
    }

    @Test
    void testGetAccountStatus_NotFound() {

        Long mobile = 8888888888L;

        when(accountRequestRepository.findByMobileNumber(mobile)).thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(NihilentBankException.class,
                () -> bankAccountService.getAccountStatus(mobile));

        assertEquals("Account Not Found", ex.getMessage());
    }



    @Test
    void testShowAllAccounts_Success() throws NihilentBankException {

        Long mobile = 9999999999L;

        Customer customer = new Customer();
        customer.setCustomerId("CH001");
        customer.setName("John");
        customer.setAddress("Pune");
        customer.setEmailId("john@test.com");
        customer.setGender("Male");
        customer.setMobileNumber(mobile);

        BankAccount account = new BankAccount();
        account.setAccountNumber(12345678L);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setAccountType(AccountType.SAVING);
        account.setBalance(5000.0);
        account.setBankName("ICICI");
        account.setIfscCode("ICICI0001");
        account.setPanCard("ABCDE1234F");
        account.setCustomer(customer);

        when(accountRepository.findByMobileNumber(mobile))
                .thenReturn(List.of(account));

        List<BankAccountDto> result = bankAccountService.showAllAcounts(mobile);

        assertEquals(1, result.size());
        assertEquals(12345678L, result.get(0).getAccountNumber());
        assertEquals("John", result.get(0).getCustomerDto().getName());

        verify(accountRepository, times(1)).findByMobileNumber(mobile);
    }


    @Test
    void testShowAllAccounts_NotFound() {

        Long mobile = 7777777777L;

        when(accountRepository.findByMobileNumber(mobile)).thenReturn(List.of());

        NihilentBankException ex = assertThrows(
                NihilentBankException.class,
                () -> bankAccountService.showAllAcounts(mobile)
        );

        assertEquals("Invalid Mobile Number", ex.getMessage());
    }




    @Test
    void testGetBalance_Success() throws NihilentBankException {

        Long accountNum = 12345L;

        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNum);
        account.setBalance(9000.0);

        when(accountRepository.findByAccountNumber(accountNum))
                .thenReturn(Optional.of(account));

        Double balance = bankAccountService.getBalance(accountNum);

        assertEquals(9000.0, balance);
    }


    @Test
    void testGetBalance_NotFound() {

        Long accountNum = 12345L;

        when(accountRepository.findByAccountNumber(accountNum))
                .thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(
                NihilentBankException.class,
                () -> bankAccountService.getBalance(accountNum)
        );

        assertEquals("Invalid Account Number", ex.getMessage());
    }


    @Test
    void testShowAllAccountsDetails_Success() throws NihilentBankException {

        Customer customer = new Customer();
        customer.setCustomerId("CH001");
        customer.setName("User1");
        customer.setEmailId("u1@test.com");
        customer.setAddress("Delhi");
        customer.setGender("Male");
        customer.setMobileNumber(99999L);

        BankAccount account = new BankAccount();
        account.setAccountNumber(111L);
        account.setAccountType(AccountType.SAVING);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBalance(1000.0);
        account.setBankName("ICICI");
        account.setIfscCode("ICICI0001");
        account.setCustomer(customer);

        when(accountRepository.findAll()).thenReturn(List.of(account));

        List<BankAccountDto> result = bankAccountService.showAllAcountsDetails();

        assertEquals(1, result.size());
        assertEquals(111L, result.get(0).getAccountNumber());
        assertEquals("User1", result.get(0).getCustomerDto().getName());
    }



    @Test
    void testShowAllAccountsDetails_EmptyList() {

        when(accountRepository.findAll()).thenReturn(List.of());

        NihilentBankException ex = assertThrows(
                NihilentBankException.class,
                () -> bankAccountService.showAllAcountsDetails()
        );

        assertEquals("Account Not Found", ex.getMessage());
    }



    @Test
    void testGetAccountDetail_Success() throws NihilentBankException {

        Long mobile = 9999999999L;

        Customer customer = new Customer();
        customer.setCustomerId("CH001");
        customer.setName("John");
        customer.setEmailId("john@test.com");
        customer.setMobileNumber(mobile);

        BankAccount account = new BankAccount();
        account.setAccountNumber(222L);
        account.setAccountType(AccountType.SAVING);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBalance(2000.0);
        account.setCustomer(customer);

        when(accountRepository.findByMobileNumbers(mobile))
                .thenReturn(account);

        BankAccountDto result = bankAccountService.getAccountDetail(mobile);

        assertEquals(222L, result.getAccountNumber());
        assertEquals("John", result.getCustomerDto().getName());
    }


    @Test
    void testGetAccountDetail_NotFound() {

        Long mobile = 5555555555L;

        when(accountRepository.findByMobileNumbers(mobile))
                .thenReturn(null);

        NihilentBankException ex = assertThrows(
                NihilentBankException.class,
                () -> bankAccountService.getAccountDetail(mobile)
        );

        assertEquals("Invalid Mobile Number", ex.getMessage());
    }



    @Test
    void testGetAllAccountRequest_ReturnsPendingSorted() throws NihilentBankException {

        BankAccountRequest req1 = new BankAccountRequest();
        req1.setAccountId(1L);
        req1.setApplicantName("User1");
        req1.setStatus(AccountRequestStatus.PENDING);
        req1.setApplicationDate(LocalDate.of(2023, 1, 10));

        BankAccountRequest req2 = new BankAccountRequest();
        req2.setAccountId(2L);
        req2.setApplicantName("User2");
        req2.setStatus(AccountRequestStatus.APPROVED);
        req2.setApplicationDate(LocalDate.of(2023, 1, 15));

        BankAccountRequest req3 = new BankAccountRequest();
        req3.setAccountId(3L);
        req3.setApplicantName("User3");
        req3.setStatus(AccountRequestStatus.PENDING);
        req3.setApplicationDate(LocalDate.of(2023, 1, 20));

        // Mixed list: PENDING + APPROVED
        when(accountRequestRepository.findAll()).thenReturn(List.of(req1, req2, req3));

        List<BankAccountRequest> result = bankAccountService.getAllAccountRequest();

        assertEquals(2, result.size());   // Only PENDING
        assertEquals(3L, result.get(0).getAccountId()); // Newest first
        assertEquals(1L, result.get(1).getAccountId()); // Older second

        verify(accountRequestRepository, times(1)).findAll();
    }




    @Test
    void testGetAllAccountRequest_EmptyList() throws NihilentBankException {

        when(accountRequestRepository.findAll()).thenReturn(List.of());

        List<BankAccountRequest> result = bankAccountService.getAllAccountRequest();

        assertTrue(result.isEmpty());
    }


    @Test
    void testGetAllAccountRequest_NoPending() throws NihilentBankException {

        BankAccountRequest req1 = new BankAccountRequest();
        req1.setStatus(AccountRequestStatus.APPROVED);

        when(accountRequestRepository.findAll()).thenReturn(List.of(req1));

        List<BankAccountRequest> result = bankAccountService.getAllAccountRequest();

        assertTrue(result.isEmpty());
    }



    @Test
    void testUpdateAccountStatus_Success() throws NihilentBankException {

        Long accountId = 10L;

        BankAccountRequest request = new BankAccountRequest();
        request.setAccountId(accountId);
        request.setStatus(AccountRequestStatus.PENDING);

        when(accountRequestRepository.findById(accountId)).thenReturn(Optional.of(request));
        when(accountRequestRepository.save(request)).thenReturn(request);

        BankAccountRequest updated =
                bankAccountService.updateAccountStatus(accountId, AccountRequestStatus.APPROVED);

        assertEquals(AccountRequestStatus.APPROVED, updated.getStatus());
        verify(accountRequestRepository, times(1)).save(request);
    }


    @Test
    void testUpdateAccountStatus_AccountNotFound() {

        Long accountId = 99L;

        when(accountRequestRepository.findById(accountId)).thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(
                NihilentBankException.class,
                () -> bankAccountService.updateAccountStatus(accountId, AccountRequestStatus.APPROVED)
        );

        assertEquals("Invalid Account Number", ex.getMessage());
    }



    @Test
    void testUpdateAccountStatus_AlreadyProcessed() {

        Long accountId = 5L;

        BankAccountRequest request = new BankAccountRequest();
        request.setAccountId(accountId);
        request.setStatus(AccountRequestStatus.APPROVED); // Already processed

        when(accountRequestRepository.findById(accountId)).thenReturn(Optional.of(request));

        NihilentBankException ex = assertThrows(
                NihilentBankException.class,
                () -> bankAccountService.updateAccountStatus(accountId, AccountRequestStatus.REJECTED)
        );

        assertEquals("Account already processed", ex.getMessage());
    }



    @Test
    void testGetBalance_AccountNotFound() {

        Long accountNumber = 12345L;

        when(accountRepository.findByAccountNumber(accountNumber))
                .thenReturn(Optional.empty());

        NihilentBankException ex = assertThrows(
                NihilentBankException.class,
                () -> bankAccountService.getBalance(accountNumber)
        );

        assertEquals("Invalid Account Number", ex.getMessage());

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

}

package com.nihilent.BankingApplication.NihilentBank.controlllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bank.NihilentBankApplication;
import com.nihilent.bank.controller.BankAccountController;
import com.nihilent.bank.dto.BankAccountDto;
import com.nihilent.bank.dto.BankAccountRequestDto;
import com.nihilent.bank.dto.CustomerDto;
import com.nihilent.bank.entity.*;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.service.BankAccountService;
import com.nihilent.bank.utility.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankAccountController.class)
@ContextConfiguration(classes = NihilentBankApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class BankAccountControllerTest {


    @Autowired
    private  MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockitoBean
    private BankAccountService accountService;

    @MockitoBean
    private BankAccountRepository accountRepository;

    @MockitoBean
    private JwtUtil jwtUtil;


    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private AuthenticationManager authenticationManager;



    @Test
    void testCreateAccount_success() throws Exception {

        BankAccountDto accountDto = new BankAccountDto();
        accountDto.setBankName("ICICI Bank");
        accountDto.setAccountType(AccountType.SAVING);
        accountDto.setIfscCode("ICICI00012");
        accountDto.setPanCard("GJSPR0008D");
        accountDto.setAdharCard("128976545654");
        accountDto.setAccountStatus(AccountStatus.ACTIVE);
        accountDto.setOpeningDate(LocalDate.now());
        accountDto.setDateOfBirth(LocalDate.of(2000,1,12));


        CustomerDto customerDto = new CustomerDto();


        customerDto.setEmailId("jhon@gmail.com");
        customerDto.setCustomerId("1");
        customerDto.setName("Jhon");
        customerDto.setMobileNumber(1276567656l);
        customerDto.setRoles(Roles.User);

        accountDto.setCustomerDto(customerDto);
        accountDto.setBalance(5000d);

        when(accountService.createAccount(any(BankAccountDto.class)))
                .thenReturn(12345678L);

        mockMvc.perform(post("/NihilentBank/admin/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("12345678"));
    }



    @Test
    void testCreateAccount_validationFailure() throws Exception {

        BankAccountDto accountDto = new BankAccountDto(); // Missing required fields

        mockMvc.perform(post("/NihilentBank/admin/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isBadRequest());
    }



    @Test
    void testCreateAccount_businessException() throws Exception {


        BankAccountDto accountDto = new BankAccountDto();
        accountDto.setBankName("ICICI Bank");
        accountDto.setAccountType(AccountType.SAVING);
        accountDto.setIfscCode("ICICI00012");
        accountDto.setPanCard("GJSPR0008D");
        accountDto.setAdharCard("128976545654");
        accountDto.setAccountStatus(AccountStatus.ACTIVE);
        accountDto.setOpeningDate(LocalDate.now());
        accountDto.setDateOfBirth(LocalDate.of(2000,1,12));


        CustomerDto customerDto = new CustomerDto();


        customerDto.setEmailId("jhon@gmail.com");
        customerDto.setCustomerId("1");
        customerDto.setName("Jhon");
        customerDto.setMobileNumber(1276567656l);
        customerDto.setRoles(Roles.User);

        accountDto.setCustomerDto(customerDto);
        accountDto.setBalance(5000d);

        when(accountService.createAccount(any(BankAccountDto.class)))
                .thenThrow(new NihilentBankException("Account already exists"));

        mockMvc.perform(post("/NihilentBank/admin/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Account already exists"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }


    @Test
    void showAllAccountDetails_success() throws Exception {

        when(accountService.showAllAcountsDetails())
                .thenReturn(List.of(new BankAccountDto()));

        mockMvc.perform(get("/NihilentBank/admin/allAccountDetails"))
                .andExpect(status().isOk());
    }


    @Test
    void getAccountDetail_success() throws Exception {

        long mobileNumber = 9876543210L;

        when(accountService.getAccountDetail(mobileNumber))
                .thenReturn(new BankAccountDto());

        mockMvc.perform(get("/NihilentBank/user/accountDetails/{mobileNumber}", mobileNumber))
                .andExpect(status().isOk());
    }


    @Test
    void getAccountDetail_invalidMobileNumber() throws Exception {

        mockMvc.perform(get("/NihilentBank/user/accountDetails/{mobileNumber}", 123))
                .andExpect(status().isBadRequest());
    }



    @Test
    void testGetAccountDetails_success() throws Exception {


        BankAccountDto accountDto = new BankAccountDto();
        accountDto.setBankName("ICICI Bank");
        accountDto.setAccountType(AccountType.SAVING);
        accountDto.setIfscCode("ICICI00012");
        accountDto.setPanCard("GJSPR0008D");
        accountDto.setAdharCard("128976545654");
        accountDto.setAccountStatus(AccountStatus.ACTIVE);
        accountDto.setOpeningDate(LocalDate.now());
        accountDto.setDateOfBirth(LocalDate.of(2000,1,12));

        accountDto.setAccountNumber(12345678L);


        CustomerDto customerDto = new CustomerDto();


        customerDto.setName("John");
        customerDto.setEmailId("jhon@gmail.com");
        customerDto.setCustomerId("1");
        customerDto.setName("Jhon");
        customerDto.setMobileNumber(1276567656l);
        customerDto.setRoles(Roles.User);

        accountDto.setCustomerDto(customerDto);
        accountDto.setBalance(5000d);


        when(accountService.getAccountDetails(12345678L))
                .thenReturn(accountDto);

        mockMvc.perform(get("/NihilentBank/admin/accountDetails/{accountNumber}", 12345678L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(12345678L))
                .andExpect(jsonPath("$.bankName").value("ICICI Bank"))
                .andExpect(jsonPath("$.ifscCode").value("ICICI00012"))
                .andExpect(jsonPath("$.accountType").value("SAVING"))
                .andExpect(jsonPath("$.balance").value(5000d));
    }



    @Test
    void testGetAccountDetails_invalidAccountNumber() throws Exception {

        mockMvc.perform(get("/NihilentBank/admin/accountDetails/{accountNumber}", 1234L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testGetAccountDetails_accountNotFound() throws Exception {

        when(accountService.getAccountDetails(87654321L))
                .thenThrow(new NihilentBankException("Account not found"));

        mockMvc.perform(get("/NihilentBank/admin/accountDetails/{accountNumber}", 87654321L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Account not found"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());


    }


    @Test
    void testGetBalance_success() throws Exception {

        when(accountService.getBalance(12345678L))
                .thenReturn(25000d);

        mockMvc.perform(get("/NihilentBank/user/getBalance/{accountNumber}", 12345678L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("25000.0"));
    }



    @Test
    void testGetBalance_invalidAccountNumber() throws Exception {

        mockMvc.perform(get("/NihilentBank/user/getBalance/{accountNumber}", 1234L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }



    @Test
    void testGetBalance_accountNotFound() throws Exception {

        when(accountService.getBalance(87654321L))
                .thenThrow(new NihilentBankException("Account not found"));

        mockMvc.perform(get("/NihilentBank/user/getBalance/{accountNumber}", 87654321L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Account not found"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }



    @Test
    void testShowAllAccounts_success() throws Exception {


        BankAccountDto account1 = new BankAccountDto();
        account1.setBankName("ICICI Bank");
        account1.setAccountType(AccountType.SAVING);
        account1.setIfscCode("ICICI00012");
        account1.setPanCard("GJSPR0008D");
        account1.setAdharCard("128976545654");
        account1.setAccountStatus(AccountStatus.ACTIVE);
        account1.setOpeningDate(LocalDate.now());
        account1.setDateOfBirth(LocalDate.of(2000,1,12));

        account1.setAccountNumber(12345678L);
        account1.setBalance(2000d);


        CustomerDto customerDto = new CustomerDto();



        customerDto.setMobileNumber(1276567656l);
        customerDto.setRoles(Roles.User);
        account1.setCustomerDto(customerDto);



        BankAccountDto account2 = new BankAccountDto();
        account2.setBankName("ICICI Bank");
        account2.setAccountType(AccountType.SAVING);
        account2.setIfscCode("ICICI00012");
        account2.setPanCard("GJSPR0008D");
        account2.setAdharCard("128976545654");
        account2.setAccountStatus(AccountStatus.ACTIVE);
        account2.setOpeningDate(LocalDate.now());
        account2.setDateOfBirth(LocalDate.of(2000,1,12));
        account1.setAccountNumber(12345679L);
        account2.setBalance(3000d);


        CustomerDto customerDto1= new CustomerDto();
        customerDto1.setRoles(Roles.User);
        customerDto1.setMobileNumber(1276567656L);

        account2.setCustomerDto(customerDto1);

        List<BankAccountDto> accounts = List.of(account1, account2);

        when(accountService.showAllAcounts(1276567656L))
                .thenReturn(accounts);

        mockMvc.perform(get("/NihilentBank/admin/allAccountDetails/{mobileNumber}", 1276567656l)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].accountNumber").value(12345679L))
                .andExpect(jsonPath("$[1].accountType").value("SAVING"));
    }



    @Test
    void testShowAllAccounts_invalidMobileNumber() throws Exception {

        mockMvc.perform(get("/NihilentBank/admin/allAccountDetails/{mobileNumber}", 12345L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testShowAllAccounts_noAccountsFound() throws Exception {

        when(accountService.showAllAcounts(9999999999L))
                .thenThrow(new NihilentBankException("No accounts found for this mobile number"));

        mockMvc.perform(get("/NihilentBank/admin/allAccountDetails/{mobileNumber}", 9999999999L)
                        .contentType(MediaType.APPLICATION_JSON))


                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("No accounts found for this mobile number"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());


    }



    @Test
    void testAccountDelete_success() throws Exception {

        when(accountService.accountDelete(12345678L))
                .thenReturn("Account deleted successfully");

        mockMvc.perform(delete("/NihilentBank/admin/accountDelete/{accountNumber}", 12345678L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Account deleted successfully"));
    }


    @Test
    void testAccountDelete_invalidAccountNumber() throws Exception {

        mockMvc.perform(delete("/NihilentBank/admin/accountDelete/{accountNumber}", 1234L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testAccountDelete_accountNotFound() throws Exception {

        when(accountService.accountDelete(87654321L))
                .thenThrow(new NihilentBankException("Account not found"));

        mockMvc.perform(delete("/NihilentBank/admin/accountDelete/{accountNumber}", 87654321L)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Account not found"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());


    }


    @Test
    void testRequestBankAccount_success() throws Exception {

        when(accountService.applyBankAccount(9876543210L, "John Doe", "SAVINGS"))
                .thenReturn("Bank account request submitted successfully");

        mockMvc.perform(post("/NihilentBank/bank/request-account/{mobileNumber}/{name}/{accountType}",
                        9876543210L, "John Doe", "SAVINGS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Bank account request submitted successfully"));
    }


    @Test
    void testRequestBankAccount_businessException() throws Exception {

        when(accountService.applyBankAccount(9876543210L, "John Doe", "SAVINGS"))
                .thenThrow(new NihilentBankException("Bank account request already exists"));

        mockMvc.perform(post("/NihilentBank/bank/request-account/{mobileNumber}/{name}/{accountType}",
                        9876543210L, "John Doe", "SAVINGS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Bank account request already exists"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());
    }


    @Test
    void testGetAllAccountRequest_success() throws Exception {

        BankAccountRequest request1 = new BankAccountRequest();
        request1.setMobileNumber(9876543210L);
        request1.setApplicantName("John Doe");
        request1.setAccountType("SAVINGS");

        BankAccountRequest request2 = new BankAccountRequest();
        request2.setMobileNumber(8765432109L);
        request2.setApplicantName("Jane Doe");
        request2.setAccountType("CURRENT");

        List<BankAccountRequest> requests = List.of(request1, request2);

        when(accountService.getAllAccountRequest()).thenReturn(requests);

        mockMvc.perform(get("/NihilentBank/allAccountRequest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].applicantName").value("John Doe"))
                .andExpect(jsonPath("$[1].accountType").value("CURRENT"));
    }



    @Test
    void testGetAllAccountRequest_noRequestsFound() throws Exception {

        when(accountService.getAllAccountRequest())
                .thenThrow(new NihilentBankException("No account requests found"));

        mockMvc.perform(get("/NihilentBank/allAccountRequest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("No account requests found"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }

    @Test
    void testUpdateLoanStatus_success() throws Exception {

        BankAccountRequest updatedRequest = new BankAccountRequest();
        updatedRequest.setAccountId(1L);
        updatedRequest.setApplicantName("John Doe");
        updatedRequest.setAccountType("SAVINGS");
        updatedRequest.setStatus(AccountRequestStatus.APPROVED);

        when(accountService.updateAccountStatus(1L, AccountRequestStatus.APPROVED))
                .thenReturn(updatedRequest);

        mockMvc.perform(put("/NihilentBank/updateAccount/{loanId}/{status}", 1L, "APPROVED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1L))
                .andExpect(jsonPath("$.applicantName").value("John Doe"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }


    @Test
    void testUpdateLoanStatus_accountNotFound() throws Exception {

        when(accountService.updateAccountStatus(1L, AccountRequestStatus.REJECTED))
                .thenThrow(new NihilentBankException("Account request not found"));

        mockMvc.perform(put("/NihilentBank/updateAccount/{loanId}/{status}", 1L, "REJECTED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Account request not found"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }

    @Test
    void testGetAccountStatus_success() throws Exception {

        BankAccountRequestDto accountStatusDto = new BankAccountRequestDto();
        accountStatusDto.setMobileNumber(9876543210L);
        accountStatusDto.setApplicantName("John Doe");
        accountStatusDto.setAccountType("SAVINGS");
        accountStatusDto.setStatus(AccountRequestStatus.APPROVED);

        when(accountService.getAccountStatus(9876543210L)).thenReturn(accountStatusDto);

        mockMvc.perform(get("/NihilentBank/accountStatus/{mobileNumber}", 9876543210L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mobileNumber").value(9876543210L))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.accountType").value("SAVINGS"));
    }

    @Test
    void testGetAccountStatus_invalidMobileNumber() throws Exception {

        mockMvc.perform(get("/NihilentBank/accountStatus/{mobileNumber}", 12345L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAccountStatus_accountNotFound() throws Exception {

        when(accountService.getAccountStatus(9999999999L))
                .thenThrow(new NihilentBankException("Account request not found"));

        mockMvc.perform(get("/NihilentBank/accountStatus/{mobileNumber}", 9999999999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Account request not found"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());
    }


}


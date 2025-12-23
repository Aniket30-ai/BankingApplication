package com.nihilent.BankingApplication.NihilentBank.controlllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bank.NihilentBankApplication;
import com.nihilent.bank.controller.BeneficiaryAccountController;
import com.nihilent.bank.entity.*;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.service.BeneficiaryAccountService;
import com.nihilent.bank.utility.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeneficiaryAccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = NihilentBankApplication.class)
class BeneficiaryAccountTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BeneficiaryAccountService beneficiaryAccountService;

    @MockitoBean
    private BankAccountRepository accountRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private AuthenticationManager authenticationManager;


    @Test
    void testAddBeneficiary_success() throws Exception {

        BeneficiaryAccount beneficiaryAccount = new BeneficiaryAccount();





        BankAccount accountDto = new BankAccount();
        accountDto.setBankName("ICICI Bank");
        accountDto.setAccountType(AccountType.SAVING);
        accountDto.setIfscCode("ICICI00012");
        accountDto.setAccountNumber(12345678L);
        accountDto.setPanCard("GJSPR0008D");
        accountDto.setAdharCard("128976545654");
        accountDto.setAccountStatus(AccountStatus.ACTIVE);
        accountDto.setOpeningDate(LocalDate.now());
        accountDto.setDateOfBirth(LocalDate.of(2000,1,12));
        Customer customer = new Customer();
        customer.setName("John");
        accountDto.setCustomer(customer);

        beneficiaryAccount.setBankAccount(accountDto);



        beneficiaryAccount.setId(1L);


//        BeneficiaryAccount response = new BeneficiaryAccount();
//        response.setId(1L);
//        response.setBankAccount();
//        response.setBeneficiaryName("John Doe");
//        response.setAccountNumber(12345678L);
//        response.setIfscCode("HDFC0001234");
//        response.setBankName("HDFC Bank");

        when(beneficiaryAccountService.addBeneficiary(any(BeneficiaryAccount.class)))
                .thenReturn(beneficiaryAccount);

        mockMvc.perform(post("/NihilentBank/beneficiary/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beneficiaryAccount)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.bankAccount.customer.name").value("John"))
                .andExpect(jsonPath("$.bankAccount.bankName").value("ICICI Bank"))
                .andExpect(jsonPath("$.bankAccount.accountNumber").value(12345678L));
    }








    @Test
    void testAddBeneficiary_businessException() throws Exception {
//
        BeneficiaryAccount request = new BeneficiaryAccount();
//        request.setBeneficiaryName("John Doe");
//        request.setAccountNumber(12345678L);
//        request.setIfscCode("HDFC0001234");
//        request.setBankName("HDFC Bank");

        when(beneficiaryAccountService.addBeneficiary(any(BeneficiaryAccount.class)))
                .thenThrow(new NihilentBankException("Beneficiary already exists"));

        mockMvc.perform(post("/NihilentBank/beneficiary/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Beneficiary already exists"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }




    @Test
    void listBeneficaryAccount_success() throws Exception {

        BeneficiaryAccount b1 = new BeneficiaryAccount();


        b1.setId(1L);

        BankAccount bankAccount = new BankAccount();

        bankAccount.setAccountNumber(1234567890L);

        Customer customer = new Customer();


        customer.setName("John");
        bankAccount.setCustomer(customer);
        b1.setBankAccount(bankAccount);



        BeneficiaryAccount b2 = new BeneficiaryAccount();


        b2.setId(2L);

        BankAccount bankAccount2 = new BankAccount();

        bankAccount.setAccountNumber(1234567891L);

        Customer customer2 = new Customer();


        customer2.setName("Alice");
        bankAccount.setCustomer(customer2);
        b1.setBankAccount(bankAccount2);


//        BeneficiaryAccount b2 = new BeneficiaryAccount(2L, "Alice", "9876543210");

        List<BeneficiaryAccount> beneficiaryList = Arrays.asList(b1, b2);

        Mockito.when(beneficiaryAccountService.getAllBeneficiaries())
                .thenReturn(beneficiaryList);

        mockMvc.perform(MockMvcRequestBuilders.get("/NihilentBank/beneficiary/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }



    @Test
    void listBeneficaryAccount_emptyList() throws Exception {

        Mockito.when(beneficiaryAccountService.getAllBeneficiaries())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/NihilentBank/beneficiary/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }



    @Test
    void deleteAccount_success() throws Exception {

        Long beneficiaryId = 1L;

        Mockito.when(beneficiaryAccountService.deleteBeneficiayAccount(beneficiaryId))
                .thenReturn("Beneficiary deleted successfully");

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/NihilentBank/beneficiary/delete/{id}", beneficiaryId))
                .andExpect(status().isOk())
                .andExpect(content().string("Beneficiary deleted successfully"));
    }


    @Test
    void deleteAccount_invalidId() throws Exception {

        Long invalidId = 0L;

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/NihilentBank/beneficiary/delete/{id}", invalidId))
                .andExpect(status().isBadRequest());
    }



    @Test
    void deleteAccount_notFound() throws Exception {

        Long beneficiaryId = 10L;

        Mockito.when(beneficiaryAccountService.deleteBeneficiayAccount(beneficiaryId))
                .thenThrow(new NihilentBankException("Beneficiary not found"));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/NihilentBank/beneficiary/delete/{id}", beneficiaryId))
//                .andExpect(status().isInternalServerError());


                       .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Beneficiary not found"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());
    }


}

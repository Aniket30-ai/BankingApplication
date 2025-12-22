package com.nihilent.BankingApplication.NihilentBank.controlllerTest;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bank.NihilentBankApplication;
import com.nihilent.bank.controller.BeneficiaryAccountController;
import com.nihilent.bank.dto.BankAccountDto;
import com.nihilent.bank.entity.*;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.service.BeneficiaryAccountService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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


}

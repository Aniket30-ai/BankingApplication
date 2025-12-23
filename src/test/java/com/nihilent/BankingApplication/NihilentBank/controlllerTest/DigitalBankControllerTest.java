package com.nihilent.BankingApplication.NihilentBank.controlllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bank.NihilentBankApplication;
import com.nihilent.bank.controller.DigitalBankController;
import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.repository.DigitalBankRepository;
import com.nihilent.bank.service.DigitalBankService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = NihilentBankApplication.class)
@WebMvcTest(DigitalBankController.class)
 class DigitalBankControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtUtil jwtUtil;


    @MockitoBean
     private AuthenticationManager authenticationManager;






    @MockitoBean
    private DigitalBankService digitalBankService;

    @MockitoBean
    private DigitalBankRepository digitalBankRepository;




    @Test
    void linkAccount_success() throws Exception {

        Long mobileNumber = 9876543210L;
        Long accountNumber = 12345678L;

        when(digitalBankService.linkAccount(mobileNumber, accountNumber))
                .thenReturn("Account Linked Successfully");

        mockMvc.perform(get("/NihilentBank/user/linkAccount")
                        .param("mobileNumber", mobileNumber.toString())
                        .param("accountNumber", accountNumber.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().string("Account Linked Successfully"));
    }






    @Test
    void linkAccount_invalidMobileNumber() throws Exception {

        mockMvc.perform(get("/NihilentBank/user/linkAccount")
                        .param("mobileNumber", "123")
                        .param("accountNumber", "12345678"))
                .andExpect(status().isBadRequest());
    }



    @Test
    void getQRCode_success() throws Exception {

        String upiId = "user@upi";
        byte[] qrCode = new byte[] { 1, 2, 3, 4 };

        when(digitalBankService.getQRCode(upiId))
                .thenReturn(qrCode);

        mockMvc.perform(get("/NihilentBank/getQRCode/{upiId}", upiId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(qrCode));
    }


    @Test
    void getQRCode_blankUpiId() throws Exception {

        mockMvc.perform(get("/NihilentBank/getQRCode/{upiId}", " "))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getUpiId_success() throws Exception {

        Long accountNumber = 12345678L;
        String upiId = "user@upi";

        when(digitalBankService.findUpiId(accountNumber))
                .thenReturn(upiId);

        mockMvc.perform(get("/NihilentBank/getUpi/{accountNumber}", accountNumber))
                .andExpect(status().isOk())
                .andExpect(content().string("user@upi"));
    }



    @Test
    void getUpiId_invalidAccountNumber() throws Exception {

        mockMvc.perform(get("/NihilentBank/getUpi/{accountNumber}", 123))
                .andExpect(status().isBadRequest());
    }




}

package com.nihilent.BankingApplication.NihilentBank.controlllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bank.NihilentBankApplication;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.service.BillPayments;
import com.nihilent.bank.utility.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import com.nihilent.bank.controller.BillPaymentsController;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BillPaymentsController.class)
@ContextConfiguration(classes = NihilentBankApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class BillPaymentsControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BillPayments billPayments;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtFilter jwtFilter;



    @MockitoBean
    private AuthenticationManager authenticationManager;



    @Test
    void testMobileRecharge_success() throws Exception {

        when(billPayments.mobileRecharge(9876543210L, 500.0, "Jio", 12345678L))
                .thenReturn("Recharge successful");

        mockMvc.perform(post("/NihilentBank/bill/mobileRecharge")
                        .param("mobileNumber", "9876543210")
                        .param("accountNumber", "12345678")
                        .param("operator", "Jio")
                        .param("amount", "500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Recharge successful"));
    }




    @Test
    void testMobileRecharge_validationFailure() throws Exception {

        mockMvc.perform(post("/NihilentBank/bill/mobileRecharge")
                        .param("mobileNumber", "123456787923")  // Invalid mobile number
                        .param("accountNumber", "12345678")
                        .param("operator", "Jio")
                        .param("amount", "500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }




    @Test
    void testMobileRecharge_businessException() throws Exception {

        when(billPayments.mobileRecharge(9876543210L, 500.0, "Jio", 12345678L))
                .thenThrow(new NihilentBankException("Insufficient balance"));

        mockMvc.perform(post("/NihilentBank/bill/mobileRecharge")
                        .param("mobileNumber", "9876543210")
                        .param("accountNumber", "12345678")
                        .param("operator", "Jio")
                        .param("amount", "500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Insufficient balance"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }



    @Test
    void testDthRecharge_success() throws Exception {

        when(billPayments.dthRecharge(1234567890L, 750.0, "TataSky", 12345678L))
                .thenReturn("DTH recharge successful");

        mockMvc.perform(post("/NihilentBank/bill/dthRecharge")
                        .param("subscriberId", "1234567890")
                        .param("accountNumber", "12345678")
                        .param("provider", "TataSky")
                        .param("amount", "750")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("DTH recharge successful"));
    }




    @Test
    void testDthRecharge_validationFailure() throws Exception {

        mockMvc.perform(post("/NihilentBank/bill/dthRecharge")
                        .param("subscriberId", "123456787923")  // Invalid mobile number
                        .param("accountNumber", "12345678")
                        .param("provider", "Tata Sky")
                        .param("amount", "500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }




    @Test
    void testDthRecharge_businessException() throws Exception {

        when(billPayments.dthRecharge(1234567890L, 750.0, "TataSky", 12345678L))
                .thenThrow(new NihilentBankException("Insufficient balance"));

        mockMvc.perform(post("/NihilentBank/bill/dthRecharge")
                        .param("subscriberId", "1234567890")
                        .param("accountNumber", "12345678")
                        .param("provider", "TataSky")
                        .param("amount", "750")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())


                .andExpect(jsonPath("$.message").value("Insufficient balance"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());


    }




    @Test
    void testElectricityBill_success() throws Exception {
        when(billPayments.electricityBill(9876543210L, 1200.0, "MSEDCL", 12345678L))
                .thenReturn("Electricity bill payment successful");

        mockMvc.perform(post("/NihilentBank/bill/electricityBill")
                        .param("consumerNumber", "9876543210")
                        .param("accountNumber", "12345678")
                        .param("provider", "MSEDCL")
                        .param("amount", "1200")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Electricity bill payment successful"));
    }




    @Test
    void testElectricityBill_validationFailure() throws Exception {

        mockMvc.perform(post("/NihilentBank/bill/electricityBill")
                        .param("consumerNumber", "123456787923")  // Invalid mobile number
                        .param("accountNumber", "12345678")
                        .param("provider", "Adani Power")
                        .param("amount", "500")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }








    @Test
    void testElectricityBill_businessException() throws Exception {

        when(billPayments.electricityBill(9876543210L, 1200.0, "MSEDCL", 12345678L))
                .thenThrow(new NihilentBankException("Insufficient balance"));

        mockMvc.perform(post("/NihilentBank/bill/electricityBill")
                        .param("consumerNumber", "9876543210")
                        .param("accountNumber", "12345678")
                        .param("provider", "MSEDCL")
                        .param("amount", "1200")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Insufficient balance"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());
    }


}

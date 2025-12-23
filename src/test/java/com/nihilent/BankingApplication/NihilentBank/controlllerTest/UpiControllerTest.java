package com.nihilent.BankingApplication.NihilentBank.controlllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bank.NihilentBankApplication;
import com.nihilent.bank.controller.UPIController;
import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;



import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;


import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = NihilentBankApplication.class)
@WebMvcTest(UPIController.class)
class UpiControllerTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @MockitoBean
    private JwtUtil jwtUtil;


    @MockitoBean
    private JwtFilter jwtFilter;


    @MockitoBean
    private AuthenticationManager authenticationManager;





    @Test
    void generateQrCode_success() throws Exception {

        mockMvc.perform(get("/NihilentBank/qrcode")
                        .param("upiId", "test@upi")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    assertTrue(bytes.length > 0);
                });
    }



    @Test
    void generateQrCode_withoutAmount_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/NihilentBank/qrcode")
                        .param("upiId", "test@upi")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(result -> {
                    byte[] bytes = result.getResponse().getContentAsByteArray();
                    assertTrue(bytes.length > 0);
                });
    }


    @Test
    void generateQrCode_withAmount_success() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/NihilentBank/qrcode")
                        .param("upiId", "test@upi")
                        .param("name", "John")
                        .param("amount", "100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(result -> {
                    assertTrue(result.getResponse().getContentAsByteArray().length > 0);
                });
    }












    @Test
    void generateQrCode_responseNotEmpty() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/NihilentBank/qrcode")
                        .param("upiId", "demo@upi")
                        .param("name", "Alice"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    byte[] responseBytes = result.getResponse().getContentAsByteArray();
                    assertTrue(responseBytes.length > 0);
                });
    }


}

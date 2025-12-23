package com.nihilent.BankingApplication.NihilentBank.controlllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bank.NihilentBankApplication;
import com.nihilent.bank.controller.CreditCardController;
import com.nihilent.bank.entity.CreditCard;
import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.repository.CreditCardRepository;
import com.nihilent.bank.service.CreditCardService;
import com.nihilent.bank.utility.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = NihilentBankApplication.class)
@WebMvcTest(CreditCardController.class)
class CreditCardControllerTest {

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
    private CreditCardService creditCardService;


    @MockitoBean
    private CreditCardRepository creditCardRepository;


    @Test
    void apply_success() throws Exception {

        Long accountNumber = 12345678L;
        String response = "Credit Card Applied Successfully";

        when(creditCardService.applyForCard(accountNumber))
                .thenReturn(response);

        mockMvc.perform(post("/NihilentBank/apply/{accountNumber}", accountNumber))
                .andExpect(status().isCreated())
                .andExpect(content().string(response));
    }


    @Test
    void apply_invalidAccountNumber() throws Exception {

        // accountNumber < 10000000 → violates @Min
        mockMvc.perform(post("/NihilentBank/apply/{accountNumber}", 123))
                .andExpect(status().isBadRequest());
    }





    @Test
    void getStatus_success() throws Exception {

        Long accountNumber = 12345678L;
        Optional<List<CreditCard>> response = Optional.of(List.of(new CreditCard()));

        when(creditCardService.getRequestByUserId(accountNumber))
                .thenReturn(response);

        mockMvc.perform(get("/NihilentBank/status/{accountNumber}", accountNumber))
                .andExpect(status().isOk());
    }


    @Test
    void getStatus_invalidAccountNumber() throws Exception {

        mockMvc.perform(get("/NihilentBank/status/{accountNumber}", 123))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getPendingRequests_success() throws Exception {

        when(creditCardService.getPendingRequests())
                .thenReturn(List.of(new CreditCard()));

        mockMvc.perform(get("/NihilentBank/admin/requests"))
                .andExpect(status().isOk());
    }


    @Test
    void approve_success() throws Exception {

        Long id = 1L;
        String response = "Request Approved";

        when(creditCardService.approveRequest(id))
                .thenReturn(response);

        mockMvc.perform(post("/NihilentBank/admin/approve/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(response));
    }


    @Test
    void showCreditCard_success() throws Exception {

        Long id = 1L;
        CreditCard card = new CreditCard();

        when(creditCardService.showCreditCard(id))
                .thenReturn(card);

        mockMvc.perform(get("/NihilentBank/showCreditCard/{id}", id))
                .andExpect(status().isOk());
    }
}

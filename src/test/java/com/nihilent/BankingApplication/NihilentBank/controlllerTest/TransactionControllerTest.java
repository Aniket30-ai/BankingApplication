package com.nihilent.BankingApplication.NihilentBank.controlllerTest;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bank.NihilentBankApplication;
import com.nihilent.bank.controller.TransactionController;
import com.nihilent.bank.dto.TransactionDto;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.service.TransactionService;
import com.nihilent.bank.utility.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TransactionController.class)
@ContextConfiguration(classes =  NihilentBankApplication.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private BankAccountRepository accountRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtFilter jwtFilter;


    @MockitoBean
    private AuthenticationManager authenticationManager;

    private TransactionDto transactionDto;

    @BeforeEach
    void setUp() {
        transactionDto = new TransactionDto();
        transactionDto.setSenderAccountNumber(1234567890L);
        transactionDto.setReceivingAccountNumber(9876543210L);
        transactionDto.setAmount(500.0);
    }

    @Test
    void testFundTransfer_success() throws Exception {
        // Mocking service response
        when(transactionService.fundTransfer(transactionDto)).thenReturn("Fund Transfer Successful");

        mockMvc.perform(post("/NihilentBank/user/fundTransfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        when(transactionService.fundTransfer(any(TransactionDto.class)))
                .thenReturn("Fund Transfer Successful");

    }
//
//    @Test
//     void testFundTransfer_InsufficientBalance() throws Exception {
//
//        transactionDto.setSenderAccountNumber(1L);
//        transactionDto.setReceivingAccountNumber(2L);
//        transactionDto.setAmount(5000d);
//
//        BankAccount sender = new BankAccount();
//        sender.setAccountNumber(1L);
//        sender.setBalance(10000d); // Insufficient balance
//
//        BankAccount receiver = new BankAccount();
//        receiver.setAccountNumber(2L);
//        receiver.setBalance(2000d);
//
//        when(accountRepository.findByAccountNumber(1L)).thenReturn(Optional.of(sender));
//        when(accountRepository.findByAccountNumber(2L)).thenReturn(Optional.of(receiver));
//
//        mockMvc.perform(post("/NihilentBank/user/fundTransfer")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(transactionDto)))
//                .andExpect(status().isInternalServerError())
//                .andExpect(content().string("Insufficient balance in sender's account"));
//    }
//
//
//



    @Test
    void testGetTransactionDetails_success() throws Exception {


        TransactionDto  transactionDto1 = new TransactionDto();
        transactionDto1.setTransactionId("TNX123");
        transactionDto1.setAmount(500.0);
        transactionDto1.setSenderAccountNumber(1234567890L);
        transactionDto1.setReceivingAccountNumber(9876543210L);

        TransactionDto transactionDto2 = new TransactionDto();
        transactionDto2.setTransactionId("TNX456");
        transactionDto2.setAmount(1000.0);
        transactionDto2.setSenderAccountNumber(1234567890L);
        transactionDto2.setReceivingAccountNumber(9876543210L);
        List<TransactionDto> transactions = List.of(transactionDto1, transactionDto2);
        Long mobileNumber = 1234567890L;

        // Mock service
        when(transactionService.alltransactionDetails(mobileNumber)).thenReturn(transactions);

        mockMvc.perform(get("/NihilentBank/admin/transactionDetails")
                        .param("mobileNumber", mobileNumber.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(transactions)));
    }

    @Test
    void testGetTransactionDetails_noTransactions() throws Exception {
        Long mobileNumber = 1111111111L;

        // Mock empty list
        when(transactionService.alltransactionDetails(mobileNumber)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/NihilentBank/admin/transactionDetails")
                        .param("mobileNumber", mobileNumber.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetTransactionDetails_exception() throws Exception {
        Long mobileNumber = 1234567890L;

        when(transactionService.alltransactionDetails(mobileNumber))
                .thenThrow(new NihilentBankException("Error fetching transactions"));

//        mockMvc.perform(get("/NihilentBank/admin/transactionDetails")
//                        .param("mobileNumber", mobileNumber.toString())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized())

        mockMvc.perform(get("/NihilentBank/admin/transactionDetails")
                        .param("mobileNumber", mobileNumber.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Error fetching transactions"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }


    @Test
    void testUpiTransfer_success() throws Exception {
        String senderUpiId = "sender@upi";
        String receiverUpiId = "receiver@upi";
        Double amount = 500.0;
        String remark = "Payment";

        // Mocking service response
        when(transactionService.upiFundTransafer(senderUpiId, receiverUpiId, amount, remark))
                .thenReturn("UPI Transfer Successful");

        mockMvc.perform(post("/NihilentBank/upiTransfer")
                        .param("senderUpiId", senderUpiId)
                        .param("reciverUpiId", receiverUpiId)
                        .param("amount", amount.toString())
                        .param("remark", remark)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("UPI Transfer Successful"));
    }

    @Test
    void testUpiTransfer_failure_insufficientBalance() throws Exception {
        String senderUpiId = "sender@upi";
        String receiverUpiId = "receiver@upi";
        Double amount = 10000.0;
        String remark = "Payment";

        // Mocking exception
        when(transactionService.upiFundTransafer(senderUpiId, receiverUpiId, amount, remark))
                .thenThrow(new NihilentBankException("Insufficient balance"));

        mockMvc.perform(post("/NihilentBank/upiTransfer")
                        .param("senderUpiId", senderUpiId)
                        .param("reciverUpiId", receiverUpiId)
                        .param("amount", amount.toString())
                        .param("remark", remark)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
//                .andExpect(content().string("Insufficient balance"))
                .andExpect(jsonPath("$.message").value("Insufficient balance"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }



}


package com.nihilent.BankingApplication.NihilentBank.controlllerTest;




import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bank.NihilentBankApplication;
import com.nihilent.bank.controller.LoanController;
import com.nihilent.bank.entity.Loan;
import com.nihilent.bank.entity.LoanStatus;
import com.nihilent.bank.entity.LoanType;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.repository.LoanRepository;
import com.nihilent.bank.service.LoanService;
import com.nihilent.bank.utility.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
@WebMvcTest(LoanController.class)
@ContextConfiguration(classes = NihilentBankApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoanService loanService;


    @MockitoBean
    private LoanRepository loanRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtFilter jwtFilter;


    @MockitoBean
    private AuthenticationManager authenticationManager;






    @Test
    void testApplyLoan_success() throws Exception {
        Loan loan = new Loan();
        loan.setLoanAmount(50000d);
        loan.setLoanType(LoanType.PERSONAL);

        when(loanService.applyLoan(any(Loan.class)))
                .thenReturn("Loan Applied Successfully");

        mockMvc.perform(post("/NihilentBank/applyLoan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loan)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Loan Applied Successfully"));
    }

    @Test
    void testApplyLoan_failure() throws Exception {
        Loan loan = new Loan();
        loan.setLoanAmount(1000000d); // Exceeds limit
        loan.setLoanType(LoanType.PERSONAL);

        when(loanService.applyLoan(any(Loan.class)))
                .thenThrow(new NihilentBankException("Loan application failed"));

        mockMvc.perform(post("/NihilentBank/applyLoan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loan)))


                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Loan application failed"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }



    @Test
    void testGetLoansByAccount_success() throws Exception {

        Loan loan1 = new Loan();
        loan1.setLoanAmount(50000d);
        loan1.setLoanType(LoanType.PERSONAL);

        Loan loan2 = new Loan();
        loan2.setLoanAmount(100000d);
        loan2.setLoanType(LoanType.HOME);

        List<Loan> loanList = List.of(loan1, loan2);

        when(loanService.getLoansByAccount(12345678L))
                .thenReturn(loanList);

        mockMvc.perform(get("/NihilentBank/user/{accountNumber}", 12345678L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].loanAmount").value(50000d))
                .andExpect(jsonPath("$[1].loanType").value("HOME"));
    }

    @Test
    void testGetLoansByAccount_invalidAccountNumber() throws Exception {

        mockMvc.perform(get("/NihilentBank/user/{accountNumber}", 1234L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testGetLoansByAccount_notFound() throws Exception {

        when(loanService.getLoansByAccount(87654321L))
                .thenThrow(new NihilentBankException("No loans found for this account"));

        mockMvc.perform(get("/NihilentBank/user/{accountNumber}", 87654321L)
                        .contentType(MediaType.APPLICATION_JSON))


                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("No loans found for this account"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }




    @Test
    void testGetAllLoans_success() throws Exception {

        Loan loan1 = new Loan();
        loan1.setLoanAmount(50000d);
        loan1.setLoanType(LoanType.PERSONAL);

        Loan loan2 = new Loan();
        loan2.setLoanAmount(200000d);
        loan2.setLoanType(LoanType.HOME);

        List<Loan> loans = List.of(loan1, loan2);

        when(loanService.getAllLoans()).thenReturn(loans);

        mockMvc.perform(get("/NihilentBank/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].loanType").value("PERSONAL"))
                .andExpect(jsonPath("$[1].loanAmount").value(200000d));
    }

    @Test
    void testGetAllLoans_noLoansFound() throws Exception {

        when(loanService.getAllLoans())
                .thenThrow(new NihilentBankException("No loans found"));

        mockMvc.perform(get("/NihilentBank/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("No loans found"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }


    @Test
    void testUpdateLoanStatus_success() throws Exception {

        Loan updatedLoan = new Loan();
        updatedLoan.setLoanAmount(75000d);
        updatedLoan.setLoanType(LoanType.PERSONAL);
        updatedLoan.setStatus(LoanStatus.APPROVED);

        when(loanService.updateLoanStatus(1L, LoanStatus.APPROVED))
                .thenReturn(updatedLoan);

        mockMvc.perform(put("/NihilentBank/update/{loanId}/{status}", 1L, "APPROVED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }


    @Test
    void testUpdateLoanStatus_invalidStatus() throws Exception {

        mockMvc.perform(put("/NihilentBank/update/{loanId}/{status}", 1L, "INVALID_STATUS")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }



    @Test
    void testUpdateLoanStatus_loanNotFound() throws Exception {

        when(loanService.updateLoanStatus(1L, LoanStatus.REJECTED))
                .thenThrow(new NihilentBankException("Loan not found"));

        mockMvc.perform(put("/NihilentBank/update/{loanId}/{status}", 1L, "REJECTED")
                        .contentType(MediaType.APPLICATION_JSON))


                .andExpect(status().isUnauthorized())  // 401 code in your handler
                .andExpect(jsonPath("$.message").value("Loan not found"))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.timeStamp").exists());

    }



}



package com.nihilent.BankingApplication.NihilentBank.serviceTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nihilent.bank.entity.BankAccount;
import com.nihilent.bank.entity.Loan;
import com.nihilent.bank.entity.LoanStatus;
import com.nihilent.bank.entity.LoanType;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.BankAccountRepository;
import com.nihilent.bank.repository.LoanRepository;
import com.nihilent.bank.repository.TransactionRepository;
import com.nihilent.bank.serviceimpl.LoanServiceImpl;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository accountRepository;

    @InjectMocks
    private LoanServiceImpl loanService;


    private final String invalidLoan = "Invalid Loan Type";
    private final String loanSucess = "Loan Applied Successfully";
    private final String loanNotFound = "Loan Not Found";
    private final String loanProcessed = "Loan Already Processed";

    @BeforeEach
    void setUp() {

        loanService.loanTypeInvalid = invalidLoan;
        loanService.loanSuccess = loanSucess;
        loanService.loanNotFound = loanNotFound;
        loanService.loanProcessed = loanProcessed;
    }

    @Test
    void testApplyLoan_Success_HomeLoan() throws Exception {
        Loan loan = new Loan();
        loan.setLoanType(LoanType.HOME);
        loan.setLoanAmount(100000.00);
        loan.setTenureMonths(60); // 5 years

        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        String result = loanService.applyLoan(loan);

        assertEquals(loanSucess, result);
        assertEquals(LoanStatus.PENDING, loan.getStatus());
        assertNotNull(loan.getApplicationDate());
        assertTrue(loan.getInterestRate() > 0);
        assertTrue(loan.getEmiAmount() > 0);

        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void testUpdateLoanStatus_Success() throws Exception {
        BankAccount account = new BankAccount();
        account.setAccountNumber(123L);
        account.setBalance(5000.0);

        Loan loan = new Loan();
        loan.setLoanId(1L);
        loan.setStatus(LoanStatus.PENDING);
        loan.setLoanAmount(2000.0);
        loan.setBankAccount(account);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(accountRepository.save(any(BankAccount.class))).thenReturn(account);
        when(transactionRepository.save(any())).thenReturn(null);
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan updatedLoan = loanService.updateLoanStatus(1L, LoanStatus.APPROVED);

        assertEquals(LoanStatus.APPROVED, updatedLoan.getStatus());
        assertEquals(7000.0, account.getBalance());

        verify(accountRepository, times(1)).save(account);
        verify(transactionRepository, times(1)).save(any());
        verify(loanRepository, atLeast(1)).save(any());
    }

    @Test
    void testUpdateLoanStatus_AlreadyProcessed() {
        Loan loan = new Loan();
        loan.setLoanId(1L);
        loan.setStatus(LoanStatus.APPROVED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        NihilentBankException exception = assertThrows(NihilentBankException.class, () -> {
            loanService.updateLoanStatus(1L, LoanStatus.APPROVED);
        });

        assertEquals(loanProcessed, exception.getMessage());
        verify(accountRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testUpdateLoanStatus_LoanNotFound() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        NihilentBankException exception = assertThrows(NihilentBankException.class, () -> {
            loanService.updateLoanStatus(1L, LoanStatus.APPROVED);
        });

        assertEquals(loanNotFound, exception.getMessage());
    }

    @Test
    void testGetLoansByAccount() {
        Loan loan1 = new Loan();
        loan1.setLoanId(1L);
        loan1.setApplicationDate(LocalDate.now().minusDays(1));

        Loan loan2 = new Loan();
        loan2.setLoanId(2L);
        loan2.setApplicationDate(LocalDate.now());

        when(loanRepository.findLoansByAccountNumber(123L)).thenReturn(Arrays.asList(loan1, loan2));

        List<Loan> loans = loanService.getLoansByAccount(123L);

        assertEquals(2, loans.size());
        assertEquals(2L, loans.get(0).getLoanId()); // sorted by application date descending
    }

    @Test
    void testGetAllLoans() {
        Loan loan1 = new Loan();
        loan1.setLoanId(1L);
        loan1.setStatus(LoanStatus.PENDING);
        loan1.setApplicationDate(LocalDate.now().minusDays(1));

        Loan loan2 = new Loan();
        loan2.setLoanId(2L);
        loan2.setStatus(LoanStatus.APPROVED);
        loan2.setApplicationDate(LocalDate.now());

        Loan loan3 = new Loan();
        loan3.setLoanId(3L);
        loan3.setStatus(LoanStatus.PENDING);
        loan3.setApplicationDate(LocalDate.now());

        when(loanRepository.findAll()).thenReturn(Arrays.asList(loan1, loan2, loan3));

        List<Loan> loans = loanService.getAllLoans();

        assertEquals(2, loans.size()); // only PENDING
        assertEquals(3L, loans.get(0).getLoanId()); // sorted by date descending
        assertEquals(1L, loans.get(1).getLoanId());
    }


}


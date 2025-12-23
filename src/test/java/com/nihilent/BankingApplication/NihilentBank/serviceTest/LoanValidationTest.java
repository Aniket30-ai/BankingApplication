package com.nihilent.BankingApplication.NihilentBank.serviceTest;

import static org.junit.jupiter.api.Assertions.*;

import com.nihilent.bank.entity.Loan;
import com.nihilent.bank.validation.LoanValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LoanValidationTest {

    private LoanValidation loanValidation;

    @BeforeEach
    void setUp() {
        loanValidation = new LoanValidation();
    }

    // ================= Personal Loan Tests =================

    @Test
    void validatePersonalLoan_validLoan_success() {
        Loan loan = new Loan();
        loan.setLoanAmount(100000D);
        loan.setTenureMonths(36); // 3 years

        assertDoesNotThrow(() -> loanValidation.validatePersonalLoan(loan));
    }

    @Test
    void validatePersonalLoan_amountTooLow_throwsException() {
        Loan loan = new Loan();
        loan.setLoanAmount(40000D);
        loan.setTenureMonths(24);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> loanValidation.validatePersonalLoan(loan));
        assertEquals("Personal loan amount must be ₹50,000 to ₹20,00,000", exception.getMessage());
    }

    @Test
    void validatePersonalLoan_amountTooHigh_throwsException() {
        Loan loan = new Loan();
        loan.setLoanAmount(550000000D);
        loan.setTenureMonths(24);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> loanValidation.validatePersonalLoan(loan));
        assertEquals("Personal loan amount must be ₹50,000 to ₹20,00,000", exception.getMessage());
    }

    @Test
    void validatePersonalLoan_tenureTooShort_throwsException() {
        Loan loan = new Loan();
        loan.setLoanAmount(100000D);
        loan.setTenureMonths(6); // 0.5 years

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> loanValidation.validatePersonalLoan(loan));
        assertEquals("Personal loan tenure must be between 1 to 5 years", exception.getMessage());
    }

    @Test
    void validatePersonalLoan_tenureTooLong_throwsException() {
        Loan loan = new Loan();
        loan.setLoanAmount(100000D);
        loan.setTenureMonths(72); // 6 years

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> loanValidation.validatePersonalLoan(loan));
        assertEquals("Personal loan tenure must be between 1 to 5 years", exception.getMessage());
    }

    // ================= Education Loan Tests =================

    @Test
    void validateEducationLoan_validLoan_success() {
        Loan loan = new Loan();
        loan.setLoanAmount(500000D);
        loan.setTenureMonths(60); // 5 years

        assertDoesNotThrow(() -> loanValidation.validateEducationLoan(loan));
    }

    @Test
    void validateEducationLoan_amountTooLow_throwsException() {
        Loan loan = new Loan();
        loan.setLoanAmount(50000D);
        loan.setTenureMonths(24);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> loanValidation.validateEducationLoan(loan));
        assertEquals("Education loan amount must be ₹1,00,000 to ₹25,00,000", exception.getMessage());
    }

    @Test
    void validateEducationLoan_amountTooHigh_throwsException() {
        Loan loan = new Loan();
        loan.setLoanAmount(30000D);
        loan.setTenureMonths(60);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> loanValidation.validateEducationLoan(loan));
        assertEquals("Education loan amount must be ₹1,00,000 to ₹25,00,000", exception.getMessage());
    }

    @Test
    void validateEducationLoan_tenureTooShort_throwsException() {
        Loan loan = new Loan();
        loan.setLoanAmount(500000D);
        loan.setTenureMonths(6); // 0.5 years

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> loanValidation.validateEducationLoan(loan));
        assertEquals("Education loan tenure must be between 1 to 10 years", exception.getMessage());
    }

    @Test
    void validateEducationLoan_tenureTooLong_throwsException() {
        Loan loan = new Loan();
        loan.setLoanAmount(500000D);
        loan.setTenureMonths(132); // 11 years

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> loanValidation.validateEducationLoan(loan));
        assertEquals("Education loan tenure must be between 1 to 10 years", exception.getMessage());
    }

    // ================= Home Loan Tests =================

    @Test
    void validateHomeLoan_validLoan_success() {
        Loan loan = new Loan();
        loan.setLoanAmount(500000D);
        loan.setTenureMonths(180); // 15 years

        assertDoesNotThrow(() -> loanValidation.validateHomeLoan(loan));
    }

    @ParameterizedTest(name = "PersonalLoan amount={0} should throw exception")
    @ValueSource(doubles = {40000D, 2500000D})
    void validatePersonalLoan_invalidAmount_throwsException(double amount) {

        Loan loan = new Loan();
        loan.setLoanAmount(amount);
        loan.setTenureMonths(36); // valid tenure

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanValidation.validatePersonalLoan(loan)
        );

        assertEquals(
                "Personal loan amount must be ₹50,000 to ₹20,00,000",
                exception.getMessage()
        );
    }




    @ParameterizedTest(name = "PersonalLoan tenureMonths={0} should throw exception")
    @ValueSource(ints = {6, 72}) // 0.5 year, 6 years
    void validatePersonalLoan_invalidTenure_throwsException(int tenureMonths) {

        Loan loan = new Loan();
        loan.setLoanAmount(100_000D); // valid amount
        loan.setTenureMonths(tenureMonths);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loanValidation.validatePersonalLoan(loan)
        );

        assertEquals(
                "Personal loan tenure must be between 1 to 5 years",
                exception.getMessage()
        );
    }



}


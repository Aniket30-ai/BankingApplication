package com.nihilent.bankingApplication.Validation;

import org.springframework.stereotype.Component;

import com.nihilent.bankingApplication.entity.Loan;

@Component
public class LoanValidation {

	public void validatePersonalLoan(Loan loan) {
		double amount = loan.getLoanAmount();
		int years = loan.getTenureMonths() / 12;

		if (amount < 50000 || amount > 2000000) {
			throw new IllegalArgumentException("Personal loan amount must be ₹50,000 to ₹20,00,000");
		}

		if (years < 1 || years > 5) {
			throw new IllegalArgumentException("Personal loan tenure must be between 1 to 5 years");
		}
	}

	public void validateEducationLoan(Loan loan) {
		double amount = loan.getLoanAmount();
		int years = loan.getTenureMonths() / 12;

		if (amount < 100000 || amount > 2500000) {
			throw new IllegalArgumentException("Education loan amount must be ₹1,00,000 to ₹25,00,000");
		}

		if (years < 1 || years > 10) {
			throw new IllegalArgumentException("Education loan tenure must be between 1 to 10 years");
		}
	}

	
	
	public void validateHomeLoan(Loan loan) {
	    double amount = loan.getLoanAmount();
	    int tenure = loan.getTenureMonths() / 12; // convert to years

	    if (amount < 100000 || amount > 50000000) {
	        throw new IllegalArgumentException("Home loan amount must be between ₹1,00,000 and ₹5 Cr.");
	    }

	    if (tenure < 1 || tenure > 30) {
	        throw new IllegalArgumentException("Home loan tenure must be between 1 and 30 years.");
	    }
	}
}

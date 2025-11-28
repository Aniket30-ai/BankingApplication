package com.nihilent.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nihilent.bank.entity.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

	@Query("select l from Loan l where l.bankAccount.accountNumber = ?1")
	List<Loan> findLoansByAccountNumber(Long accountNumber);

}

package com.nihilent.bankingApplication.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nihilent.bankingApplication.entity.BankAccount;

import jakarta.transaction.Transactional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

	Optional<BankAccount> findByAccountNumber(Long accountNumber);

	@Query("select b from BankAccount b where b.customer.mobileNumber=?1")
	List<BankAccount> findByMobileNumber(Long mobileNumber);

	@Query("select b from BankAccount b where b.customer.mobileNumber=?1")
	BankAccount findByMobileNumbers(Long mobileNumber);

	
	
	@Modifying
	@Transactional
	@Query("delete from BankAccount b where b.accountNumber = ?1")
	void deleteByAccountNumber(Long accountNumber);
}

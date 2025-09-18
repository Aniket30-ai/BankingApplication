package com.nihilent.bankingApplication.repository;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nihilent.bankingApplication.entity.DigitalBankAccount;

@Repository
public interface DigitalBankRepository extends JpaRepository<DigitalBankAccount, String> {

	@Query("select b from DigitalBankAccount b where b.bankAccount.accountNumber=?1")
	Optional<DigitalBankAccount> findByAccountNumber(Long accountNumber);

	Optional<DigitalBankAccount> findByDigitalBankId(String digitalId);
	
	
	
}

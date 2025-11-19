package com.nihilent.bankingApplication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nihilent.bankingApplication.entity.BeneficiaryAccount;

@Repository
public interface BeneficiaryAccountRepository extends JpaRepository<BeneficiaryAccount, Long> {

	@Query("select b from BeneficiaryAccount b where b.bankAccount.accountNumber=?1")
	Optional<BeneficiaryAccount> findByAccountNumber(Long accountNumber);

}

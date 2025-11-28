package com.nihilent.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nihilent.bank.entity.BankAccountRequest;

public interface BankAccountRequestRepository extends JpaRepository<BankAccountRequest, Long> {

	Optional<BankAccountRequest> findByMobileNumber(Long mobileNumber);
}

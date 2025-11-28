package com.nihilent.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nihilent.bank.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByMobileNumber(Long mobileNumber);

	Optional<Customer> findByEmailId(String emailId);

	Optional<Customer> findTopByOrderByIdDesc();
}

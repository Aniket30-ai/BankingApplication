package com.nihilent.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nihilent.bank.entity.CreditCard;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

	List<CreditCard> findByStatus(CreditCard.Status status);

	CreditCard findByUserId(String userId);

	@Query("select l from CreditCard l where l.bankAccount.accountNumber = ?1")
	List<CreditCard> findCreditCardByAccountNumber(Long accountNumber);

	@Query("select l from CreditCard l where l.bankAccount.accountNumber = ?1")
	Optional<CreditCard> findByCreditCardByAccountNumber(Long accountNumber);
}

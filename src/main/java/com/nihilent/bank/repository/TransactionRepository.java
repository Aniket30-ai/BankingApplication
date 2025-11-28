package com.nihilent.bank.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nihilent.bank.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

	List<Transaction> findBySenderAccountNumber(Long accountNumber);

	@Query("SELECT t FROM Transaction t WHERE "
			+ "(t.senderAccountNumber = :accountNumber OR t.receivingAccountNumber = :accountNumber) "
			+ "AND t.transactionTime BETWEEN :startTime AND :endTime")
	List<Transaction> findByAccountNumberAndTransactionTimeBetween(@Param("accountNumber") Long accountNumber,
			@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

	List<Transaction> findBySenderAccountNumberAndTransactionTimeBetween(Long accountNumber, LocalDateTime startDate,
			LocalDateTime endDate);

	List<Transaction> findBySenderAccountNumberOrReceivingAccountNumber(Long sender, Long receiver);

	@Query("SELECT COALESCE(SUM(t.debit), 0) FROM Transaction t "
			+ "WHERE t.senderAccountNumber = :senderAccountNumber " + "AND t.transactionType = 'DEBIT' "
			+ "AND t.transactionTime BETWEEN :startOfDay AND :endOfDay")
	Double getTotalDebitedAmountForSenderBetween(@Param("senderAccountNumber") Long senderAccountNumber,
			@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}

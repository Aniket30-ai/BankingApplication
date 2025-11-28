package com.nihilent.bank.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bank.entity.AuditLog;
import com.nihilent.bank.entity.Transaction;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.AuditRepository;

@Service
public class AuditLogServiceImpl {

	private final AuditRepository auditRepository;

	public AuditLogServiceImpl(AuditRepository auditRepository) {

		this.auditRepository = auditRepository;
	}

	@Value("${KAFKA_TOPIC}")
	private String kafkaTopic;

	@Value("${GROUP_ID}")
	private String groupID;

	@Value("${KAFKA_HOST}")
	private String host;

	@KafkaListener(topics = "kafkaTopic", groupId = "groupID", containerFactory = "kafkaListnerContainerFactory")
	@Transactional(rollbackFor = NihilentBankException.class)
	public void consumeTransaction(Transaction transactions) {

		AuditLog log = new AuditLog();
		log.setTransactionId(transactions.getTransactionId());
		log.setSenderAccountNumber(transactions.getSenderAccountNumber());
		log.setReceivingAccountNumber(transactions.getReceivingAccountNumber());
		log.setAmount(transactions.getAmount());
		log.setRemark(transactions.getRemark());
		log.setStatus(transactions.getStatus());
		log.setErrorMesssage(transactions.getErrorMesssage());
		log.setTransactionTime(LocalDateTime.now());
		auditRepository.save(log);

	}
}

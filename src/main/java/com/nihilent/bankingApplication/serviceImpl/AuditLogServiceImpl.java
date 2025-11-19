package com.nihilent.bankingApplication.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nihilent.bankingApplication.entity.AuditLog;
import com.nihilent.bankingApplication.entity.Transaction;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.AuditRepository;

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
	public void consumeTransaction(Transaction record) {

		AuditLog log = new AuditLog();
		log.setTransactionId(record.getTransactionId());
		log.setSenderAccountNumber(record.getSenderAccountNumber());
		log.setReceivingAccountNumber(record.getReceivingAccountNumber());
		log.setAmount(record.getAmount());
		log.setRemark(record.getRemark());
		log.setStatus(record.getStatus());
		log.setErrorMesssage(record.getErrorMesssage());
		log.setTransactionTime(LocalDateTime.now());
		auditRepository.save(log);

	}
}

package com.nihilent.bankingApplication.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.nihilent.bankingApplication.config.KafkaConstant;
import com.nihilent.bankingApplication.entity.AuditLog;
import com.nihilent.bankingApplication.entity.Transaction;
import com.nihilent.bankingApplication.repository.AuditRepository;

@Service
public class AuditLogServiceImpl {

	@Autowired
	private AuditRepository auditRepository;
	
	
//	 @KafkaListener(topics = KafkaConstant.TOPIC, groupId = KafkaConstant.GROUP_ID,containerFactory = "kafkaListnerContainerFactory")
	    public void consumeTransaction(Transaction record) {
	        System.out.println("📥 Received Transaction: " + record.getTransactionId());

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

	        System.out.println("📝 Audit log saved for: " + record.getTransactionId());
	    }
}

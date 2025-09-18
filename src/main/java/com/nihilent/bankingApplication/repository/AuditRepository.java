package com.nihilent.bankingApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nihilent.bankingApplication.entity.AuditLog;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {

}

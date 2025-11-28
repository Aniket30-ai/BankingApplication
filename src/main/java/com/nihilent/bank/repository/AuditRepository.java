package com.nihilent.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nihilent.bank.entity.AuditLog;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {

}

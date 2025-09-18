package com.nihilent.bankingApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nihilent.bankingApplication.entity.Operators;

@Repository
public interface OperatorRepository extends JpaRepository<Operators, Long>{

}

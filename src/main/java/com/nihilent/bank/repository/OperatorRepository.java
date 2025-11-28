package com.nihilent.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nihilent.bank.entity.Operators;

@Repository
public interface OperatorRepository extends JpaRepository<Operators, Long>{

}

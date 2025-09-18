package com.nihilent.bankingApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nihilent.bankingApplication.entity.Plans;

@Repository
public interface PlanRepository extends JpaRepository<Plans, Long> {

	
	
	@Query("select p from Plans p where p.operators.operatorId= ?1")
	List<Plans> findPlansByPlanId(Long planId);
}

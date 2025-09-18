package com.nihilent.bankingApplication.service;

import com.nihilent.bankingApplication.dto.OperatorDto;
import com.nihilent.bankingApplication.dto.PlanDto;

public interface PlanService {

	public void createPlan(PlanDto planDto);

	public void updatePlan(PlanDto planDto);

	public void deletePlan(Long planId);

	public void getplan(Long planId);
}

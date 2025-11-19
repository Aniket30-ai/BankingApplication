package com.nihilent.bankingApplication.service;

import com.nihilent.bankingApplication.dto.PlanDto;
import com.nihilent.bankingApplication.exception.NihilentBankException;

public interface PlanService {

	public void createPlan(PlanDto planDto) throws NihilentBankException;

	public void updatePlan(PlanDto planDto) throws NihilentBankException;

	public void deletePlan(Long planId) throws NihilentBankException;

	public void getplan(Long planId) throws NihilentBankException;
}

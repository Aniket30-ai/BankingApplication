package com.nihilent.bank.service;

import com.nihilent.bank.dto.PlanDto;
import com.nihilent.bank.exception.NihilentBankException;

public interface PlanService {

	public void createPlan(PlanDto planDto) throws NihilentBankException;

	public void updatePlan(PlanDto planDto) throws NihilentBankException;

	public void deletePlan(Long planId) throws NihilentBankException;

	public void getplan(Long planId) throws NihilentBankException;
}

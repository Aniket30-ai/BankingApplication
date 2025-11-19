package com.nihilent.bankingApplication.dto;

public class PlanDto {

	private Long planId;

	private String planName;

	private OperatorDto operators;

	private Double amount;

	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public OperatorDto getOperators() {
		return operators;
	}

	public void setOperators(OperatorDto operators) {
		this.operators = operators;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

}

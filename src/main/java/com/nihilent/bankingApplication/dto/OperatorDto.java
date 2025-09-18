package com.nihilent.bankingApplication.dto;

import java.util.List;

public class OperatorDto {

	
	
	
	    private Long operatorId;

	    private String name;


	    private List<PlanDto> plans;


		public Long getOperatorId() {
			return operatorId;
		}


		public void setOperatorId(Long operatorId) {
			this.operatorId = operatorId;
		}


		public String getName() {
			return name;
		}


		public void setName(String name) {
			this.name = name;
		}


		public List<PlanDto> getPlans() {
			return plans;
		}


		public void setPlans(List<PlanDto> plans) {
			this.plans = plans;
		}
	    
	    
	    
}

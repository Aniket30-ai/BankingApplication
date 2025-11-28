package com.nihilent.bank.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Operators {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long operatorId;

	private String name;

	@OneToMany(mappedBy = "operators", cascade = CascadeType.ALL)
	private List<Plans> plans;

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

	public List<Plans> getPlans() {
		return plans;
	}

	public void setPlans(List<Plans> plans) {
		this.plans = plans;
	}

}

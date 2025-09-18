package com.nihilent.bankingApplication.dto;

import java.time.LocalDate;

public class EmployeePojo {
	 public String id;
    public String firstName;
    public double price;
//    private LocalDate startDate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public EmployeePojo(String id, String firstName, double price) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.price = price;
	}

 
}

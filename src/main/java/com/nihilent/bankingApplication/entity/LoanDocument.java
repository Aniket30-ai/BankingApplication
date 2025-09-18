package com.nihilent.bankingApplication.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

public class LoanDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String documentName; // e.g., "Aadhar Card"
	private String documentType; // e.g., "application/pdf"

	@Lob
	@Column(length = 10485760) // Optional: 10MB max size
	private byte[] data; // Binary data (BLOB)

	@ManyToOne
	@JoinColumn(name = "loan_id", nullable = false)
	private Loan loan;

	private LocalDate uploadedDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	public LocalDate getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(LocalDate uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	// Getters and Setters
	
	
	
	

}

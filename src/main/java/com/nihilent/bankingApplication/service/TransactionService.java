package com.nihilent.bankingApplication.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.nihilent.bankingApplication.dto.TransactionDto;
import com.nihilent.bankingApplication.entity.Transaction;
import com.nihilent.bankingApplication.exception.NihilentBankException;

public interface TransactionService {

	public String fundTransfer(TransactionDto transactionDto) throws NihilentBankException;

	public List<TransactionDto> alltransactionDetails(Long accountNumber) throws NihilentBankException;

	public List<TransactionDto> transactionDetails(Long accountNumber) throws NihilentBankException;

	public void parseFileAndSave(MultipartFile file) throws NumberFormatException, IOException, NihilentBankException;

	public List<Transaction> getTransactionsByAccountNumberAndDateRange(Long accountNumber, LocalDateTime startDate,
			LocalDateTime endDate) throws NihilentBankException;

	public String upiFundTransafer(String senderUpiID, String recevierUpiId, Double amount, String remark)
			throws NihilentBankException;

}

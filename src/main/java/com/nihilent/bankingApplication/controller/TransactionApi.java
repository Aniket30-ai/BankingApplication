package com.nihilent.bankingApplication.controller;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfFixedPrint;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.nihilent.bankingApplication.config.BankPdf;
import com.nihilent.bankingApplication.dto.TransactionDto;
import com.nihilent.bankingApplication.entity.AccountType;
import com.nihilent.bankingApplication.entity.BankAccount;
import com.nihilent.bankingApplication.entity.Customer;
import com.nihilent.bankingApplication.entity.Transaction;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.BankAccountRepository;
import com.nihilent.bankingApplication.repository.TransactionRepository;
import com.nihilent.bankingApplication.service.TransactionService;



@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "NihilentBank")

public class TransactionApi {

	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private BankAccountRepository accountRepository;

	@PostMapping(value = "/user/fundTransfer")
	public ResponseEntity<String> fundTransfer(@RequestBody TransactionDto transactionDto)
			throws NihilentBankException {

		String fundTransfer = transactionService.fundTransfer(transactionDto);

		return new ResponseEntity<String>(fundTransfer, HttpStatus.OK);
	}

//	@GetMapping(value = "/user/transactionDetails/{mobileNumber}")
	@GetMapping(value = "/admin/transactionDetails")

	public ResponseEntity<List<TransactionDto>> getTransactionDetails(Long mobileNumber) throws NihilentBankException {

		List<TransactionDto> transactionDetails = transactionService.alltransactionDetails(mobileNumber);

		return new ResponseEntity<List<TransactionDto>>(transactionDetails, HttpStatus.OK);
	}

	@GetMapping(value = "/user/transactionDetail/{accountNumber}")

	public ResponseEntity<List<TransactionDto>> getTransaction(@PathVariable Long accountNumber) throws NihilentBankException {

		List<TransactionDto> transactionDetails = transactionService.transactionDetails(accountNumber);
		return new ResponseEntity<List<TransactionDto>>(transactionDetails, HttpStatus.OK);
	}

	
	
	 @PostMapping("txns/upload")
	    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
		 
		 
		
	        try {
	        	
	        	transactionService.parseFileAndSave(file);
	            return ResponseEntity.ok(Map.of("message", "Uploaded and saved"));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                                 .body(Map.of("error", e.getMessage()));
	        }
	    }
	 
	 
	 
	 @GetMapping("/api/failed-transactions")
	 public ResponseEntity<Resource> downloadFailedTransactions() throws IOException {
	     Path path = Paths.get("src/main/resources/failed_transactions.csv");

	     Resource resource = new UrlResource(path.toUri());

	     return ResponseEntity.ok()
	             .contentType(MediaType.APPLICATION_OCTET_STREAM)
	             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"src/main/resources/failed_transactions.csv\"")
	             .body(resource);
	 }
	 
	 
	 @GetMapping("transactionDetail/pdf/{accountNumber}")
	 public ResponseEntity<byte[]> downloadTransactionPdf(
	         @PathVariable Long accountNumber,
	         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
	         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
	 ) throws NihilentBankException, IOException {

		
	     // Handle null dates - if null, assign wide range
	     LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : LocalDateTime.of(1970, 1, 1, 0, 0);
	     LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : LocalDateTime.now();

	     
	   
	     // Fetch filtered transactions
	     List<Transaction> transactions = transactionService.getTransactionsByAccountNumberAndDateRange(accountNumber, startDateTime, endDateTime);

	     

	     
	     
	     if (transactions.isEmpty()) {
	         throw new NihilentBankException("No transactions found for given date range");
	     }

	     ByteArrayOutputStream out = new ByteArrayOutputStream();

	     PdfWriter writer = new PdfWriter(out);
	     PdfDocument pdfDoc = new PdfDocument(writer);


	     
	  // Add more top margin to leave room for header
	     Document document = new Document(pdfDoc, PageSize.A4);
	     document.setMargins(100f, 36f, 60f, 36f); // top, right, bottom, left

	     // Register header/footer event handler (pass document and logo path)
	     String logoPath = "src/main/resources/static/images/logo3.png"; // change path accordingly
	     
	     

	     
	    Optional<BankAccount> byAccountNumber = accountRepository.findByAccountNumber(accountNumber);
	    
	    BankAccount bankAccount = byAccountNumber.orElseThrow(()-> new NihilentBankException("Invalid account Number"));
	     
	     BankAccount account = new BankAccount();
	     
	     
	     Customer customer = new Customer(); 
	    
	Long accountNumb=     bankAccount.getAccountNumber();
	
	AccountType accountType = bankAccount.getAccountType();
	
	String name=bankAccount.getCustomer().getName();
	String ifscCode = bankAccount.getIfscCode();
	
   String emailId = bankAccount.getCustomer().getEmailId();
   
   
  Long mobileNumber= bankAccount.getCustomer().getMobileNumber();
   
	Double balance= bankAccount.getBalance();
	
	     
	     LocalDate dateOfBirth = bankAccount.getDateOfBirth();
	     pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE,
	    		    new BankPdf(document, logoPath,
	    		    		accountNumb,
	    		    		name,
	    		    		ifscCode,
	    		    		accountType.toString(),
	    		    		emailId,
	    		    		mobileNumber,
	    		    		balance,
	    		    		dateOfBirth

	    		        ));

	     // Add some spacing after header (optional)
//	     document.add(new Paragraph("\n\n Transaction Satement").);

	     
//	     Paragraph p = new Paragraph();
	     
	     
//	     document.add(p);
	     
	     
	     // Create the transaction table with appropriate column widths
	     Table table = new Table(UnitValue.createPercentArray(new float[]{2, 4, 4, 3, 3, 4, 4}))
	             .useAllAvailableWidth();

	     
	     String generatedAt = LocalDateTime.now()
	    		    .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

	    		document.add(new Paragraph("Transaction Statement Generated On: " + generatedAt )
	    		    .setFontSize(10)
	    		    .setItalic()
	    		    .setMarginBottom(10));
	    		
	    		document.add(new Paragraph("Statement of E-Bank Account No:-"+accountNumber+" for the period (From : "+startDate+" To :"+endDate+")")
	    			     .setTextAlignment(TextAlignment.CENTER)
	    			     .setFontSize(10)
		    		    .setFontColor(ColorConstants.BLACK).setBold()
		    		    .setMarginBottom(10));
	     
	     Cell spacerRow = new Cell(1, 7)  // 1 row, spans all 4 columns
	    	        .add(new Paragraph("")) // Empty paragraph
	    	        .setHeight(15f)          // Set desired space
	    	        .setBorder(Border.NO_BORDER);

	    	table.addHeaderCell(spacerRow); 
	     
	     Cell statementHeader = new Cell(1, 7) // 1 row, 4 columns
	    	        .add(new Paragraph("Transaction Statement").setBold().setFontSize(11))
	    	        .setTextAlignment(TextAlignment.CENTER)
	    	        .setBackgroundColor(new DeviceRgb(239, 127, 26)) // Optional: ICICI orange
	    	        .setFontColor(ColorConstants.WHITE);
	    	table.addHeaderCell(statementHeader);
//	     table.setMarginTop(10);
	     
	     // Add header cells with bold font
	     table.addHeaderCell(new Cell().add(new Paragraph("Transaction Id").setBold()).setFontSize(9));
	     table.addHeaderCell(new Cell().add(new Paragraph("Transaction Date").setBold()).setFontSize(9));
	     
	     table.addHeaderCell(new Cell().add(new Paragraph("Receiver Account").setBold()).setFontSize(9));
	     table.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()).setFontSize(9));
	     table.addHeaderCell(new Cell().add(new Paragraph("Credit").setBold()).setFontSize(9));
	     table.addHeaderCell(new Cell().add(new Paragraph("Debit").setBold()).setFontSize(9));
	     table.addHeaderCell(new Cell().add(new Paragraph("Closing Balance").setBold()).setFontSize(9));
	     // Add rows
	     
	     
	     
	     transactions.sort(Comparator.comparing(Transaction::getTransactionTime));
	     for (Transaction tx : transactions) {
	         table.addCell(String.valueOf(tx.getTransactionId())).setFontSize(8);
	         
	        
	         table.addCell(tx.getTransactionTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).setFontSize(8);
	         
	         String reciverAccount=Optional.ofNullable(tx.getReceivingAccountNumber())
	        		 .map(String::valueOf)
	        		 .orElse("");
	       
	       
	         table.addCell(reciverAccount).setFontSize(8);
	         

	         String remark=Optional.ofNullable(tx.getRemark())
	        		 .map(String::valueOf)
	        		 .orElse("");
	         table.addCell(remark).setFontSize(8);
	        
	      
	       
	         String creditStr = Optional.ofNullable(tx.getCredit())
		    		    .map(String::valueOf)
		    		    .orElse("");

	      
	      
	         
	         
	         String debitStr = Optional.ofNullable(tx.getDebit())
		    		    .map(String::valueOf)
		    		    .orElse("");
	         
	      
	         table.addCell(creditStr).setFontSize(8);
	         table.addCell(debitStr).setFontSize(8);

//	         table.addCell(tx.getDebit().toString()).setFontSize(8);
	         
	         String closingBalance = Optional.ofNullable(tx.getClosingBalance())
		    		    .map(String::valueOf)
		    		    .orElse("");

	         table.addCell(closingBalance).setFontSize(8);
	         
	     }
	     
	    

	     document.add(table);
	     
//	     System.out.println("*************************");
	     double totalCredit = transactions.stream()
	    		    .mapToDouble(tx -> tx.getCredit() != null ? tx.getCredit() : 0.0)
	    		    .sum();

	    		double totalDebit = transactions.stream()
	    		    .mapToDouble(tx -> tx.getDebit() != null ? tx.getDebit() : 0.0)
	    		    .sum();

	    		// Get final closing balance (from the latest transaction)
	    		double finalClosingBalance = transactions.get(transactions.size() - 1).getClosingBalance();

	    		
	    		// Space before summary
	    		document.add(new Paragraph("\nAccount Transaction Totals").setBold().setFontSize(11).setTextAlignment(TextAlignment.LEFT));

	    		// Create summary table with 2 columns
	    		Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{5, 5}))
	    		        .useAllAvailableWidth();

	    		summaryTable.addCell(new Cell().add(new Paragraph("Total Credit").setBold()).setFontSize(9));
	    		summaryTable.addCell(new Cell().add(new Paragraph("₹" + String.format("%.2f", totalCredit))).setFontSize(9));

	    		summaryTable.addCell(new Cell().add(new Paragraph("Total Debit").setBold()).setFontSize(9));
	    		summaryTable.addCell(new Cell().add(new Paragraph("₹" + String.format("%.2f", totalDebit))).setFontSize(9));

	    		summaryTable.addCell(new Cell().add(new Paragraph("Final Closing Balance").setBold()).setFontSize(9));
	    		summaryTable.addCell(new Cell().add(new Paragraph("₹" + String.format("%.2f", finalClosingBalance))).setFontSize(9));

	    		document.add(summaryTable);


	     // Add footer note (optional)
	     document.add(new Paragraph("\nThis is a computer-generated statement and does not require signature.")
	             .setFontSize(8)
	             .setFontColor(ColorConstants.DARK_GRAY));

	     document.add(new Paragraph("\n++++End of Statement++++")
	    	     .setTextAlignment(TextAlignment.CENTER)
	             .setFontSize(11)
	             .setFontColor(ColorConstants.BLACK)).setBold();
	     
	     document.close();

	     byte[] pdfBytes = out.toByteArray();

	     HttpHeaders headers = new HttpHeaders();
	     headers.setContentType(MediaType.APPLICATION_PDF);
	     headers.setContentDisposition(ContentDisposition.builder("inline")
	             .filename("transactions.pdf")
	             .build());

	     
	     return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
	 }
	
	 
	 
	 
	 
	 @PostMapping("upiTransfer")
	 public ResponseEntity<String> upiTransfer(@RequestParam String senderUpiId,@RequestParam String reciverUpiId,@RequestParam Double amount, @RequestParam String remark) throws NihilentBankException {
		 
		 
	
		 String upiFundTransafer = transactionService.upiFundTransafer(senderUpiId, reciverUpiId, amount,remark);
		 
		 
		 return new ResponseEntity<String>(upiFundTransafer,HttpStatus.OK);
		 
		
	}
	 
	 

}

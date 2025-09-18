package com.nihilent.bankingApplication.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.nihilent.bankingApplication.entity.Transaction;
import com.nihilent.bankingApplication.serviceImpl.ElasticCustomerData;


//import com.example.demo.service.BulkUploadService;
import com.opencsv.exceptions.CsvException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "NihilentBank")
public class ElasticSerachCustomerApi {

//	
	@Autowired
	 private  ElasticCustomerData productService;

	 
//
//	    // Endpoint to index a new product
//	    @PostMapping
//	    public ResponseEntity<String> addProduct(@RequestBody Transaction product) {
//	        try {
//	            String id = productService.indexProduct(product);
//	            return ResponseEntity.ok("Product indexed with ID: " + id);
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	            return ResponseEntity.status(500).body("Failed to index product");
//	        }
//	    }
//
//	    // Endpoint to search products by name keyword
//	    @GetMapping("/search")
//	    public ResponseEntity<List<Transaction>> searchProducts(@RequestParam String q) {
//	        try {
//	            List<Transaction> products = productService.searchProductsByName(q);
//	            return ResponseEntity.ok(products);
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	            return ResponseEntity.status(500).build();
//	        }
//	    }
	
	
	
	 @PostMapping("/upload")
	    public ResponseEntity<String> uploadCsvFile(@RequestParam("file") MultipartFile file) {
	        if (file.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a CSV file!");
	        }

	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
	            // Pass the reader to the service to parse and bulk index
	        	productService.uploadCsvAndBulkIndex(reader);
	            return ResponseEntity.ok("CSV data uploaded and indexed successfully!");
	        } catch (IOException | CsvException e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Failed to upload and index CSV: " + e.getMessage());
	        }
	    }
}

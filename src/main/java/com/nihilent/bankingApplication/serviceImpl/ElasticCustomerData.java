package com.nihilent.bankingApplication.serviceImpl;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bankingApplication.dto.EmployeePojo;
import com.nihilent.bankingApplication.entity.Transaction;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class ElasticCustomerData {

	
	
    private static final String INDEX = "sample_index";

    
    private final  RestHighLevelClient client;
    private final ObjectMapper objectMapper;
    
    

    @Autowired
    public ElasticCustomerData(RestHighLevelClient client) {
        this.client = client;
        this.objectMapper = new ObjectMapper();
    }
	
    public void uploadCsvAndBulkIndex(Reader reader) throws IOException, CsvException {
        List<EmployeePojo> products = readProductsFromCsv(reader);

        BulkRequest bulkRequest = new BulkRequest();

        for (EmployeePojo product : products) {
            Map<String, Object> doc = objectMapper.convertValue(product, new TypeReference<>() {});

            IndexRequest indexRequest = new IndexRequest(INDEX)
                    .id(product.getId())  // This becomes _id
                    .source(doc);         // ✅ this becomes _source

            System.out.println("Uploading JSON: " + objectMapper.writeValueAsString(doc));

            bulkRequest.add(indexRequest);
            
            
            System.out.println("data upload");
        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        if (bulkResponse.hasFailures()) {
            throw new IOException("Bulk upload failures: " + bulkResponse.buildFailureMessage());
        } else {
            System.out.println("✅ Bulk upload successful");
        }


	    }

	    private List<EmployeePojo> readProductsFromCsv(Reader reader) throws IOException, CsvException {
	        try (CSVReader csvReader = new CSVReader(reader)) {
	            List<String[]> rows = csvReader.readAll();

	            List<EmployeePojo> products = new ArrayList<>();
	            
	          
	            boolean firstRow = true;

	            for (String[] row : rows) {
	                if (firstRow) {  // skip header
	                    firstRow = false;
	                    continue;
	                }
	                
	                
	                
	                // Skip blank or invalid rows
	                if (row.length < 3 || row[0].isBlank() || row[1].isBlank() || row[2].isBlank()) {
	                    System.out.println("⚠️ Skipping invalid or incomplete row: " + Arrays.toString(row));
	                    continue;
	                }

	                String id = row[0].trim();
	                
	                System.out.println(id);
	                String name = row[1].trim();
	                
	                System.out.println(name);
	                double price = Double.parseDouble(row[2].trim());

	                System.out.println(price);
	                products.add(new EmployeePojo(id,name,price));
	                
	                System.out.println("Product added");
	            }
	            System.out.println("Product list"+products.toString());
	            return products;
	          
	        }
	        
	        
	       
	    }
}

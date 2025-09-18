package com.nihilent.bankingApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
@SpringBootApplication
@EnableKafka
//@ComponentScan(basePackages = "messages.properties")
public class NihilentBankApplication {

	
	
	private static final Logger logger = LogManager.getLogger(NihilentBankApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(NihilentBankApplication.class, args);
		

		logger.info("Application started");
		logger.error("Something went wrong");
		
		

		
//CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
//System.out.println("response id: " + createIndexResponse.index());


	}

}

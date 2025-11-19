package com.nihilent.bankingApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@PropertySource(value = { "classpath:messages.properties" })
public class NihilentBankApplication {
	public static void main(String[] args) {
	 SpringApplication.run(NihilentBankApplication.class, args);
		
	}
}

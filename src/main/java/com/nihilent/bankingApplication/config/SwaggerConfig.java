package com.nihilent.bankingApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	
	 @Bean
	public  OpenAPI customOpenAPI() {
	        return new OpenAPI()
	                .info(new Info()
	                        .title("Simple CRUD API")
//	                        .version("1.0")
	                        .description("Documentation of the API using Swaager OpenAPI"));
	    }
}

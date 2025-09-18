package com.nihilent.bankingApplication.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ElasticsearchConfig {

	
	
	  @Bean(destroyMethod = "close")
	    public RestHighLevelClient client() {
	        System.out.println("✅ Elasticsearch client created");
//	        CreateIndexRequest request = new CreateIndexRequest("sample_index3");
//	        request.settings(Settings.builder().put("index.number_of_shards", 1).put("index.number_of_replicas", 2));
//	        System.out.println("✅ Index  created");
	        
	        return new RestHighLevelClient(
	                RestClient.builder(new HttpHost("localhost", 9200, "http"))
	        );
	        
	        
	        
	    }
}

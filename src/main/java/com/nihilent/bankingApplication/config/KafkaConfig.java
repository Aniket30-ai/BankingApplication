package com.nihilent.bankingApplication.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.nihilent.bankingApplication.entity.Transaction;

@Configuration
public class KafkaConfig {

	
	@Bean
	public ProducerFactory<String, Transaction> producerFactory() {

		Map<String, Object> configProps = new HashMap<String, Object>();

		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.HOST);

		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

		return new DefaultKafkaProducerFactory<String, Transaction>(configProps);
	}

	
	
	@Bean(name = "kafkaTemplate")
	public KafkaTemplate<String, Transaction> kafkaTemplete() {
		
		
		return new KafkaTemplate<>(producerFactory());
	}
	
	
	
	
	
	@Bean
	public ConsumerFactory<String, Transaction> consumerFactory() {

		
		
		JsonDeserializer<Transaction> deserializer = new JsonDeserializer<>(Transaction.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        

		Map<String, Object> consumerProps = new HashMap<>();

//		consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.HOST);
//		consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.GROUP_ID);

//		consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

//		consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
		
//		consumerProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, KafkaConstant.MAX_POLL_RECORDS);
//		consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
//		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KafkaConstant.OFFSET_RESET_EARLIER);

		 return new DefaultKafkaConsumerFactory<>(
	            Map.of(
	                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.HOST,
	                    ConsumerConfig.GROUP_ID_CONFIG,  KafkaConstant.GROUP_ID,
	                    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
	                    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
	                ),
	                new StringDeserializer(),
	                deserializer
	            );

	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Transaction> kafkaListnerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Transaction> factory = new ConcurrentKafkaListenerContainerFactory<String, Transaction>();

		factory.setConsumerFactory(consumerFactory());

		return factory;
	}
}

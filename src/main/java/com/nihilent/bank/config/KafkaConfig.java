package com.nihilent.bank.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
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

import com.nihilent.bank.entity.Transaction;

@Configuration
public class KafkaConfig {

	@Value("${KAFKA_TOPIC}")
	private String kafkaTopic;

	@Value("${GROUP_ID}")
	private String groupID;

	@Value("${KAFKA_HOST}")
	private String host;

	/**
	 * ProducerFactory bean is responsible for creating Kafka Producers. It sets all
	 * required producer configurations such as: - bootstrap servers (Kafka broker
	 * address) - key/value serializer classes
	 */
	@Bean
	public ProducerFactory<String, Transaction> producerFactory() {

		Map<String, Object> configProps = new HashMap<>();

		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host);

		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	/**
	 * KafkaTemplate is used to publish messages to Kafka topics. It internally uses
	 * the ProducerFactory bean to create producer instances.
	 */
	@Bean(name = "kafkaTemplate")
	public KafkaTemplate<String, Transaction> kafkaTemplete() {

		return new KafkaTemplate<>(producerFactory());
	}

	/**
	 * ConsumerFactory is responsible for creating Kafka Consumers. - Kafka broker
	 * server address - Consumer group ID - Key and value deserializers
	 * JsonDeserializer is used to convert JSON messages into Transaction objects.
	 */
	@Bean
	public ConsumerFactory<String, Transaction> consumerFactory() {

		JsonDeserializer<Transaction> deserializer = new JsonDeserializer<>(Transaction.class);
		deserializer.setRemoveTypeHeaders(false);
		deserializer.addTrustedPackages("*");
		deserializer.setUseTypeMapperForKey(true);

		return new DefaultKafkaConsumerFactory<>(
				Map.of(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host, ConsumerConfig.GROUP_ID_CONFIG, groupID,
						ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
						ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class),
				new StringDeserializer(), deserializer);

	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, Transaction> kafkaListnerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, Transaction> factory = new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(consumerFactory());

		return factory;
	}
}

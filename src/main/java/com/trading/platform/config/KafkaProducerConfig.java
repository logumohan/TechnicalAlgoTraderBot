package com.trading.platform.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Producer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableAsync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading.platform.persistence.entity.InstrumentIndicators;

@EnableAsync
@Configuration
public class KafkaProducerConfig {

	private static final Logger LOGGER = LogManager.getLogger(KafkaProducerConfig.class);

	@Value("${spring.kafka.producer.bootstrap-servers}")
	private String servers;
	
	@Value("${spring.kafka.producer.key-serializer}")
	private Class<StringSerializer> keySerializer;
	
	@Value("${spring.kafka.producer.value-serializer}")
	private Class<JsonDeserializer<InstrumentIndicators>> valueSerializer;

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);

		LOGGER.info("Kafka producer configuration - {}", props);

		return props;
	}

	@Bean
	public ProducerFactory<String, InstrumentIndicators> producerFactory(ObjectMapper objectMapper) {
		return new DefaultKafkaProducerFactory<>(producerConfigs(), new StringSerializer(),
				new JsonSerializer<>(objectMapper));
	}

	@Bean
	public KafkaTemplate<String, InstrumentIndicators> kafkaTemplate(ObjectMapper objectMapper) {
		return new KafkaTemplate<>(producerFactory(objectMapper));
	}

	@Bean
	public Producer producer() {
		return new Producer();
	}

}

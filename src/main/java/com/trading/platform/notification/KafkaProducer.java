package com.trading.platform.notification;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.persistence.entity.InstrumentIndicators;

@Service
@Configuration
public class KafkaProducer {

	private static final Logger LOGGER = LogManager.getLogger(KafkaProducer.class);

	@Autowired
	private KafkaTemplate<String, InstrumentIndicators> kafkaTemplate;

	@Bean(name = "kafka-producer")
	public TaskExecutor kafkaTaskExecutor() {
		return new SimpleAsyncTaskExecutor(new BasicThreadFactory.Builder()
				.namingPattern("kafka-producer-%d")
				.uncaughtExceptionHandler((Thread thread, Throwable e) -> LOGGER
						.fatal("Uncaught exception in kafka producer - {}", thread.getName(), e))
				.daemon(true)
				.priority(Thread.NORM_PRIORITY)
				.build());
	}

	@LogExecutionTime
	@Async(value = "kafka-producer")
	public void sendMessage(String topic, InstrumentIndicators message) {
		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put("instrument-name", message.getName().toUpperCase())
				.put("token", String.valueOf(message.getToken()))) {
			LOGGER.trace("Attempting to send message {} to topic {}", message, topic);
			CompletableFuture<SendResult<String, InstrumentIndicators>> future = kafkaTemplate.send(
					topic,
					topic, message);

			future.whenCompleteAsync(new KafkaCallback(topic, message));
		} catch (Exception e) {
			LOGGER.trace("Error in attempting to send the message, topic = {}, message = {}", topic,
					message);
		}
	}

	class KafkaCallback implements BiConsumer<SendResult<String, InstrumentIndicators>, Throwable> {

		private String topic;

		private InstrumentIndicators message;

		public KafkaCallback(String topic, InstrumentIndicators message) {
			this.topic = topic;
			this.message = message;
		}

		@Override
		public void accept(SendResult<String, InstrumentIndicators> result, Throwable e) {
			if (e == null) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.trace("Message sent, message - {}, topic - {}", result
							.getProducerRecord().value(),
							result.getProducerRecord().topic());
				}
			} else {
				LOGGER.trace("Message could not be delivered, message - {}, topic - {}", message,
						topic, e);
			}
		}

	}

}

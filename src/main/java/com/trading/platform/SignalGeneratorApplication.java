package com.trading.platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SignalGeneratorApplication {

	private static final Logger LOGGER = LogManager.getLogger(SignalGeneratorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SignalGeneratorApplication.class, args);
		Thread.setDefaultUncaughtExceptionHandler(
				(thread, exception) -> LOGGER.fatal("Uncaught exception in thread - {}, error - {}", thread,
						exception.getMessage(), exception));
		LOGGER.info("Live Feed Handler application started");
	}

}

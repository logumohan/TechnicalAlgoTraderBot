package com.trading.platform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAdvice {

	private static final Logger LOGGER = LogManager.getLogger(ExecutionTimeAdvice.class);

	@Around("@annotation(LogExecutionTime)")
	public Object executionTime(ProceedingJoinPoint point) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object object = null;
		try {
			object = point.proceed();
		} catch (Throwable e) {
			LOGGER.error("Error in finding the exeuction time for {}.{}",
					point.getSignature().getDeclaringTypeName(),
					point.getSignature().getName(), e);
		}
		long endtime = System.currentTimeMillis();
		LOGGER.trace("Time taken for {}.{} is {} ms",
				point.getSignature().getDeclaringTypeName(),
				point.getSignature().getName(),
				(endtime - startTime));
		return object;
	}

}

package com.trading.platform.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.spi.ExtendedExceptionMapper;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UncaughtExceptionHandler implements ExtendedExceptionMapper<Throwable> {

	private static final Logger LOGGER = LogManager.getLogger(UncaughtExceptionHandler.class);

	@Override
	public Response toResponse(Throwable exception) {
		LOGGER.debug("toResponse: Exception Caught: {}", exception.getMessage(), exception);
		return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).build();
	}

	@Override
	public boolean isMappable(Throwable exception) {
		return !(exception instanceof WebApplicationException);
	}

}

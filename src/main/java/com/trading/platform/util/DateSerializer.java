package com.trading.platform.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class DateSerializer extends JsonSerializer<Date> {

	private static final Logger LOGGER = LogManager.getLogger(DateSerializer.class);

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			String s = sdf.format(value);
			gen.writeString(s);
		} catch (DateTimeParseException e) {
			LOGGER.error("Error in converting the date to readable format - {}", value, e);
			gen.writeString("");
		}
	}

}

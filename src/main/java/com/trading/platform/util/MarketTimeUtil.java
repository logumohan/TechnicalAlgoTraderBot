package com.trading.platform.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class MarketTimeUtil {

	private static Properties props = new Properties();

	static {
		try (FileInputStream fis = new FileInputStream(new File("src/main/resources/holidays.properties"))) {
			props.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private MarketTimeUtil() {
		// Do Nothing
	}

	public static boolean isMarketClosed() {
		return isHoliday() || ZonedDateTime.now().isBefore(MarketTimeUtil.getMarketStartTime()) ||
				ZonedDateTime.now().isAfter(MarketTimeUtil.getMarketEndTime());
	}

	public static ZonedDateTime getMarketStartTime() {
		return getMarketStartTime(ZonedDateTime.now());
	}

	public static ZonedDateTime getMarketStartTime(ZonedDateTime dateTime) {
		ZonedDateTime startTime = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
		if (dateTime != null) {
			startTime = dateTime.truncatedTo(ChronoUnit.DAYS);
		}

		startTime = startTime.plus(9, ChronoUnit.HOURS);
		startTime = startTime.plus(15, ChronoUnit.MINUTES);

		return startTime;
	}

	public static ZonedDateTime getMarketEndTime() {
		return getMarketEndTime(ZonedDateTime.now());
	}

	public static ZonedDateTime getMarketEndTime(ZonedDateTime dateTime) {
		ZonedDateTime endTime = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
		if (dateTime != null) {
			endTime = dateTime.truncatedTo(ChronoUnit.DAYS);
		}

		endTime = endTime.plus(15, ChronoUnit.HOURS);
		endTime = endTime.plus(30, ChronoUnit.MINUTES);

		return endTime;
	}

	public static ZonedDateTime getTradingStartTime() {
		ZonedDateTime startTime = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
		startTime = startTime.plus(9, ChronoUnit.HOURS);
		startTime = startTime.plus(30, ChronoUnit.MINUTES);

		return startTime;
	}

	public static ZonedDateTime getTradingCutOffTime() {
		ZonedDateTime cutOffTime = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
		cutOffTime = cutOffTime.plus(15, ChronoUnit.HOURS);
		cutOffTime = cutOffTime.plus(15, ChronoUnit.MINUTES);

		return cutOffTime;
	}

	public static ZonedDateTime getSquareOffTime() {
		ZonedDateTime cutOffTime = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
		cutOffTime = cutOffTime.plus(15, ChronoUnit.HOURS);
		cutOffTime = cutOffTime.plus(24, ChronoUnit.MINUTES);

		return cutOffTime;
	}

	public static boolean isMonthlyExpiryWeek(DayOfWeek expiryDayOfWeek) {
		return isMonthlyExpiryWeek(ZonedDateTime.now(), expiryDayOfWeek);
	}

	public static boolean isMonthlyExpiryWeek(ZonedDateTime dateTime, DayOfWeek expiryDayOfWeek) {
		ZonedDateTime expiryDate = dateTime != null ? dateTime : ZonedDateTime.now();
		LocalDate lastExpiryDate = expiryDate.toLocalDate().with(TemporalAdjusters
				.lastInMonth(expiryDayOfWeek));
		LocalDate prevExpiryDate = lastExpiryDate.minusDays(1).with(TemporalAdjusters
				.previousOrSame(expiryDayOfWeek));

		return (expiryDate.getDayOfMonth() >= prevExpiryDate.getDayOfMonth()) &&
				(expiryDate.getDayOfMonth() <= lastExpiryDate.getDayOfMonth());
	}

	public static ZonedDateTime getWeeklyExpiryDayDate() {
		return getWeeklyExpiryDayDate(ZonedDateTime.now(), 4);
	}

	public static ZonedDateTime getWeeklyExpiryDayDate(ZonedDateTime dateTime, int expiryDayOfWeek) {
		ZonedDateTime expiryDate = ZonedDateTime.now();
		if (dateTime != null) {
			expiryDate = dateTime;
		}

		int daysToAdd = 0;
		int dayOfWeek = expiryDate.getDayOfWeek().getValue();
		if (dayOfWeek > expiryDayOfWeek) {
			daysToAdd = (7 - dayOfWeek) + expiryDayOfWeek;
		} else {
			daysToAdd = expiryDayOfWeek - dayOfWeek;
		}
		expiryDate = expiryDate.plus(daysToAdd, ChronoUnit.DAYS);
		while (isHoliday(expiryDate)) {
			expiryDate = expiryDate.minus(1, ChronoUnit.DAYS);
		}

		return expiryDate.truncatedTo(ChronoUnit.DAYS);
	}

	public static ZonedDateTime getMonthlyExpiryDayDate() {
		return getMonthlyExpiryDayDate(ZonedDateTime.now(), 4);
	}

	public static ZonedDateTime getMonthlyExpiryDayDate(ZonedDateTime dateTime) {
		YearMonth yearMonth = YearMonth.now();
		if (dateTime != null) {
			yearMonth = YearMonth.from(dateTime);
		}

		LocalDate lastDay = yearMonth.atEndOfMonth();
		ZonedDateTime expiryDate = ZonedDateTime.of(lastDay.atStartOfDay(), ZoneId.of("Asia/Kolkata"));
		DayOfWeek dayOfWeek = expiryDate.getDayOfWeek();
		while (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
			expiryDate = expiryDate.minus(1, ChronoUnit.DAYS);
			dayOfWeek = expiryDate.getDayOfWeek();
		}
		while (isHoliday(expiryDate)) {
			expiryDate = expiryDate.minus(1, ChronoUnit.DAYS);
		}

		return expiryDate.truncatedTo(ChronoUnit.DAYS);
	}
	
	public static ZonedDateTime getMonthlyExpiryDayDate(ZonedDateTime dateTime, int expiryDayOfWeek) {
		YearMonth yearMonth = YearMonth.now();
		if (dateTime != null) {
			yearMonth = YearMonth.from(dateTime);
		}

		LocalDate lastDay = yearMonth.atEndOfMonth();
		ZonedDateTime expiryDate = ZonedDateTime.of(lastDay.atStartOfDay(), ZoneId.of("Asia/Kolkata"));
		while (expiryDate.getDayOfWeek().getValue() != expiryDayOfWeek) {
			expiryDate = expiryDate.minus(1, ChronoUnit.DAYS);
		}
		while (isHoliday(expiryDate)) {
			expiryDate = expiryDate.minus(1, ChronoUnit.DAYS);
		}

		return expiryDate.truncatedTo(ChronoUnit.DAYS);
	}

	public static ZonedDateTime getTimeOfDay(int hour, int minute, int second) {
		return getTimeOfDay(hour, minute, second, ZonedDateTime.now());
	}

	public static ZonedDateTime getTimeOfDay(int hour, int minute, int second, ZonedDateTime dateTime) {
		ZonedDateTime timeOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
		if (dateTime != null) {
			timeOfDay = dateTime.truncatedTo(ChronoUnit.DAYS);
		}
		timeOfDay = timeOfDay.plus(hour, ChronoUnit.HOURS);
		timeOfDay = timeOfDay.plus(minute, ChronoUnit.MINUTES);
		timeOfDay = timeOfDay.plus(second, ChronoUnit.SECONDS);

		return timeOfDay;
	}

	public static boolean isHoliday() {
		return isHoliday(ZonedDateTime.now());
	}

	public static boolean isHoliday(ZonedDateTime dateTime) {
		ZonedDateTime date = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
		if (dateTime != null) {
			date = dateTime.truncatedTo(ChronoUnit.DAYS);
		}

		int dayOfWeek = date.getDayOfWeek().getValue();
		if (dayOfWeek == 6 || dayOfWeek == 7) {
			return true;
		} else {
			String holidays = (String) props.get("holidays");
			if (!holidays.isEmpty()) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				return Arrays.asList(holidays.split(",")).contains(formatter.format(Date.from(date.toInstant())));
			}
		}

		return false;
	}

}

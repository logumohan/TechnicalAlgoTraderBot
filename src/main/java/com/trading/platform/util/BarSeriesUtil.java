package com.trading.platform.util;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;

import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.service.series.BarSeriesWrapper;

public class BarSeriesUtil {

	private static final Logger LOGGER = LogManager.getLogger(BarSeriesUtil.class);

	private BarSeriesUtil() {
		// Do Nothing
	}

	public static ZonedDateTime getStartTime(String startTime, SimpleDateFormat formatter) {
		try {
			return ZonedDateTime
					.ofInstant(formatter.parse(startTime).toInstant(),
							ZoneId.systemDefault())
					.truncatedTo(ChronoUnit.SECONDS);
		} catch (Exception e) {
			LOGGER.error("Error converting the start time : {} with formatter {}",
					startTime, formatter, e);
			return null;
		}
	}

	public static ZonedDateTime getStartTime(Date startTime) {
		return ZonedDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault())
				.truncatedTo(ChronoUnit.SECONDS);
	}

	public static ZonedDateTime getEndTime(ZonedDateTime barStartTime, AggregationType type) {
		return barStartTime.plus(Duration.ofSeconds(type.getDuration()));
	}

	public static ZonedDateTime getEndTime(ZonedDateTime barStartTime, Duration duration) {
		return barStartTime.plus(duration);
	}

	public static String getBarSeriesName(AggregationType aggregationType, InstrumentIndicators indicator,
			boolean isHA) {
		StringBuilder builder = new StringBuilder();
		if (isHA) {
			builder.append("ha-");
		}
		builder.append(aggregationType.getName().replace("_", "-"));
		builder.append("-bar-series-");
		builder.append(indicator.getToken());

		return builder.toString().toUpperCase();
	}

	public static void updateBarSeries(BarSeriesWrapper barSeriesWrapper, InstrumentIndicators instrumentIndicators,
			AggregationType aggregationType) {
		updateBarSeries(barSeriesWrapper.getSeries(), instrumentIndicators, aggregationType, false);
		updateBarSeries(barSeriesWrapper.getHaSeries(), instrumentIndicators, aggregationType, true);
	}

	public static void updateBarSeries(BarSeries barSeries, InstrumentIndicators instrumentIndicators,
			AggregationType aggregationType, boolean isHA) {
		ZonedDateTime startTime = BarSeriesUtil.getStartTime(instrumentIndicators.getTickTime());
		ZonedDateTime endTime = BarSeriesUtil.getEndTime(startTime, aggregationType);
		if (barSeries.getBarCount() > 0) {
			LOGGER.trace("{} : type : {}, Bar Count = {}, isHA = {}, startTime = {}, endTime = {}, series endTime = {}",
					instrumentIndicators.getName(), aggregationType.getName(), barSeries.getBarCount(), isHA, startTime,
					endTime, barSeries.getLastBar().getEndTime());
		}

		try {
			if (barSeries.getBarCount() > 0
					&& (endTime.equals(barSeries.getLastBar().getEndTime())
							|| endTime.isBefore(barSeries.getLastBar().getEndTime()))) {
				// End time of new bar should be greater than the bar series end time
				LOGGER.error(
						"End time of bar series {} is greater than the the end time of the new bar, series time = {}, end time = {}",
						barSeries.getName(), barSeries.getLastBar().getEndTime(), endTime);
			} else {
				if (isHA) {
					barSeries.addBar(Duration.ofSeconds(aggregationType.getDuration()), endTime,
							instrumentIndicators.getHaOpenPrice(), instrumentIndicators.getHaHighPrice(),
							instrumentIndicators.getHaLowPrice(), instrumentIndicators.getHaClosePrice(),
							instrumentIndicators.getVolumeTraded());
				} else {
					barSeries.addBar(Duration.ofSeconds(aggregationType.getDuration()), endTime,
							instrumentIndicators.getOpenPrice(), instrumentIndicators.getHighPrice(),
							instrumentIndicators.getLowPrice(), instrumentIndicators.getClosePrice(),
							instrumentIndicators.getVolumeTraded());
				}
			}
		} catch (Exception e) {
			LOGGER.error("{} : type : {}, Bar Count = {}, isHA = {}, startTime = {}, endTime = {}, series endTime = {}",
					instrumentIndicators.getName(), aggregationType.getName(), barSeries.getBarCount(), isHA, startTime,
					endTime, barSeries.getLastBar().getEndTime(), e);
		}
	}

	public static List<InstrumentIndicators> discardNonContinousSeriesData(
			List<InstrumentIndicators> instrumentIndicatorList, AggregationType aggregationType) {
		if (instrumentIndicatorList.size() < 2) {
			// Requires at least 2 records to check the continuity
			return instrumentIndicatorList;
		}

		int skipCount = 0;
		for (int index = 0; index < instrumentIndicatorList.size() - 1; index++) {
			if (isSampleMissing(instrumentIndicatorList.get(index),
					instrumentIndicatorList.get(index + 1),
					aggregationType)) {
				skipCount = index + 1;
			}
		}

		if (skipCount > 0) {
			instrumentIndicatorList = instrumentIndicatorList.stream().skip(skipCount).collect(Collectors.toList());
		}

		return instrumentIndicatorList;
	}

	private static boolean isSampleMissing(InstrumentIndicators firstSample, InstrumentIndicators secondSample,
			AggregationType aggregationType) {
		if (isLastSampleOfTheDay(firstSample, aggregationType)) {
			// Current sample is the last of the day
			if (isFirstSampleOfTheDay(secondSample, aggregationType)) {
				// Current sample is the last of the day and the next is the first of the day
				if (!isMarketHolidayBetweenTwoTicks(firstSample.getTickTime(), secondSample.getTickTime())) {
					// There is a working day in between and missing samples
					LOGGER.error("MISSING SAMPLES: Current sample is the last of the day and next is the first " +
							"of the day, but there is a working day in between {} :: {}", firstSample.getTickTime(),
							secondSample.getTickTime());
					return true;
				}
			} else {
				// Current sample is the last of the day and next is not the first of the day
				LOGGER.error("MISSING SAMPLES: Current sample is the last of the day, next is not the first " +
						"of the day {} :: {}", firstSample.getTickTime(), secondSample.getTickTime());
				return true;
			}
		} else {
			// Current sample is not the last of the day
			long timeDiffInSeconds = (secondSample.getTickTime().getTime() - firstSample.getTickTime().getTime())
					/ 1000;
			if (timeDiffInSeconds != aggregationType.getDuration()) {
				LOGGER.error("MISSING SAMPLES: Samples are not continuos, data till {} will be skipped, " +
						"next tick = {}", firstSample.getTickTime(), secondSample.getTickTime());
				return true;
			}
		}

		return false;
	}

	private static boolean isFirstSampleOfTheDay(InstrumentIndicators instrumentIndicator,
			AggregationType aggregationType) {
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(instrumentIndicator.getTickTime().toInstant(),
				ZoneId.systemDefault());
		switch (aggregationType.getName()) {
		case SignalGeneratorConstants.ONE_DAY:
			return dateTime.getHour() == 0 && dateTime.getMinute() == 0;
		case SignalGeneratorConstants.ONE_HOUR:
			return dateTime.getHour() == 9 && dateTime.getMinute() == 0;
		default:
			return dateTime.getHour() == 9 && dateTime.getMinute() == 15;
		}
	}

	private static boolean isLastSampleOfTheDay(InstrumentIndicators instrumentIndicator,
			AggregationType aggregationType) {
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(instrumentIndicator.getTickTime().toInstant(),
				ZoneId.systemDefault());
		switch (aggregationType.getName()) {
		case SignalGeneratorConstants.ONE_DAY:
			return dateTime.getHour() == 0 && dateTime.getMinute() == 0;
		case SignalGeneratorConstants.ONE_HOUR:
			return dateTime.getHour() == 15 && dateTime.getMinute() == 0;
		default:
			return dateTime.getHour() == 15 && dateTime.getMinute() == (30 - (aggregationType.getDuration() / 60));
		}
	}

	private static boolean isMarketHolidayBetweenTwoTicks(Date firstTickDate, Date secondTickDate) {
		ZonedDateTime firstSampleDay = ZonedDateTime.ofInstant(firstTickDate.toInstant(),
				ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);

		ZonedDateTime secondSampleDay = ZonedDateTime.ofInstant(secondTickDate.toInstant(),
				ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS);

		firstSampleDay = firstSampleDay.plusDays(1);
		return (!firstSampleDay.equals(secondSampleDay) && !MarketTimeUtil.isHoliday(firstSampleDay));
	}

}

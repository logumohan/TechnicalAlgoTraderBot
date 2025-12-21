package com.trading.platform.backtesting;

import java.time.Duration;

import com.trading.platform.SignalGeneratorConstants;

public class TestUtil {

	private TestUtil() {
		// Do Nothing
	}

	public static String getQuery(long token, String startTime, String endTime, String aggreagationType) {
		SelectQueryBuilder builder = new SelectQueryBuilder();
		builder.from(getIndicatorTableName(aggreagationType))
				.euqalTo("token", token)
				.and()
				.between("ticktime", startTime, endTime)
				.orderBy("ticktime", "asc");

		return builder.build();
	}

	public static String getIndicatorTableName(String aggreagationType) {
		switch (aggreagationType) {
		case SignalGeneratorConstants.ONE_MINUTE:
			return "instrument_onemin_indicators";
		case SignalGeneratorConstants.THREE_MINUTES:
			return "instrument_threemin_indicators";
		case SignalGeneratorConstants.FIVE_MINUTES:
			return "instrument_fivemin_indicators";
		case SignalGeneratorConstants.FIFTEEN_MINUTES:
			return "instrument_fifteenmin_indicators";
		case SignalGeneratorConstants.ONE_HOUR:
			return "instrument_onehour_indicators";
		case SignalGeneratorConstants.ONE_DAY:
			return "instrument_daily_indicators";
		default:
			return "instrument_onemin_indicators";
		}
	}

	public static Duration getDuration(String aggreagationType) {
		switch (aggreagationType) {
		case SignalGeneratorConstants.ONE_MINUTE:
			return Duration.ofSeconds(60);
		case SignalGeneratorConstants.THREE_MINUTES:
			return Duration.ofSeconds(180);
		case SignalGeneratorConstants.FIVE_MINUTES:
			return Duration.ofSeconds(300);
		case SignalGeneratorConstants.FIFTEEN_MINUTES:
			return Duration.ofSeconds(900);
		case SignalGeneratorConstants.ONE_HOUR:
			return Duration.ofSeconds(3600);
		case SignalGeneratorConstants.ONE_DAY:
			return Duration.ofSeconds(86400);
		default:
			return Duration.ofSeconds(60);
		}
	}

}

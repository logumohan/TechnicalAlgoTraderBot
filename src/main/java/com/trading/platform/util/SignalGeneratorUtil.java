package com.trading.platform.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.openqa.selenium.InvalidArgumentException;

import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.DailyInstrumentIndicators;
import com.trading.platform.persistence.entity.FifteenMinutesInstrumentIndicators;
import com.trading.platform.persistence.entity.FiveMinutesInstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.OneHourInstrumentIndicators;
import com.trading.platform.persistence.entity.OneMinuteInstrumentIndicators;
import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.persistence.entity.ThreeMinutesInstrumentIndicators;
import com.trading.platform.persistence.entity.views.FifteenMinuteInstrumentView;
import com.trading.platform.persistence.entity.views.FiveMinuteInstrumentView;
import com.trading.platform.persistence.entity.views.InstrumentView;
import com.trading.platform.persistence.entity.views.OneDayInstrumentView;
import com.trading.platform.persistence.entity.views.OneHourInstrumentView;
import com.trading.platform.persistence.entity.views.OneMinuteInstrumentView;
import com.trading.platform.persistence.entity.views.ThreeMinuteInstrumentView;
import com.trading.platform.service.signal.OptionType;

public class SignalGeneratorUtil {

	private SignalGeneratorUtil() {
	}

	public static void generateOptionSymbol(Signal signal, OptionType optionType,
			InstrumentSubscription subscription) {
		generateOptionSymbol(signal, optionType, subscription, subscription.getStrikePriceDelta());
	}

	public static void generateOptionSymbol(Signal signal, OptionType optionType,
			InstrumentSubscription subscription,
			int strikePriceDelta) {
		if (subscription != null) {
			long strikePrice = SignalGeneratorUtil.getNearestStrikePrice(optionType,
					signal.getLastTradedPrice(), strikePriceDelta);
			signal.setStrikePrice(strikePrice);
			String optionSymbol = SignalGeneratorUtil.getMonthlyOptionSymbol(
					subscription.getOptionName(), strikePrice, optionType);
			signal.setOptionSymbol(optionSymbol);
		}
	}

	public static String getMonthlyOptionSymbol(String symbol, long strikePrice,
			OptionType optionType) {
		LocalDate today = LocalDate.now();
		LocalDate expiryDay = getLastTuesdayOfTheMonth(today.getYear(), today.getMonth());
		if (today.isAfter(expiryDay)) {
			Month nextMonth = today.getMonth().plus(1);
            int year = today.getYear();
            if (nextMonth == Month.JANUARY) {
                year++;
            }
            expiryDay = getLastTuesdayOfTheMonth(year, nextMonth);
		}
		String shortMonth = expiryDay.getMonth().getDisplayName(TextStyle.SHORT, Locale.US)
				.toUpperCase();
        String shortYear = expiryDay.format(DateTimeFormatter.ofPattern("yy"));

		// BANKNIFTY26JAN58900CE
		return symbol + shortYear + shortMonth + strikePrice + optionType.getType();
	}
	
    private static LocalDate getLastTuesdayOfTheMonth(int year, Month month) {
        LocalDate lastDay = YearMonth.of(year, month).atEndOfMonth();
        while (lastDay.getDayOfWeek() != DayOfWeek.TUESDAY) {
            lastDay = lastDay.minusDays(1);
        }
        return lastDay;
    }

	public static String getWeeklyOptionSymbol(String symbol, long strikePrice,
			OptionType optionType,
			int weeklyExpiryDay, int monthlyExpiryDay) {
		ZonedDateTime expiryDateWeekly = MarketTimeUtil.getWeeklyExpiryDayDate(ZonedDateTime.now(),
				weeklyExpiryDay);
		ZonedDateTime marketStartTime = MarketTimeUtil.getMarketStartTime();
		ZonedDateTime expiryDayMarketEndTime = MarketTimeUtil.getMarketEndTime(expiryDateWeekly);

		if (marketStartTime.isAfter(expiryDayMarketEndTime)) {
			expiryDateWeekly = expiryDateWeekly.plus(6, ChronoUnit.DAYS);
			expiryDateWeekly = MarketTimeUtil.getWeeklyExpiryDayDate(expiryDateWeekly,
					weeklyExpiryDay);
		}

		ZonedDateTime expiryDateMonthly = MarketTimeUtil.getMonthlyExpiryDayDate(ZonedDateTime
				.now(), monthlyExpiryDay);
		DayOfWeek expiryDayOfWeek = DayOfWeek.of(monthlyExpiryDay);

		String optionSymbol = "";

		if (MarketTimeUtil.isMonthlyExpiryWeek(expiryDayOfWeek)) {
			int yearIn2Digits = expiryDateMonthly.getYear() % 100;
			String shortMonth = expiryDateMonthly.toLocalDate()
					.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);
			optionSymbol = symbol + String.format("%02d", yearIn2Digits) + shortMonth +
					String.valueOf(strikePrice) + optionType.getType();
		} else {
			int yearIn2Digits = expiryDateWeekly.getYear() % 100;
			int month = expiryDateWeekly.toLocalDate()
					.getMonth().getValue();
			int day = expiryDateWeekly.toLocalDate().getDayOfMonth();
			String shortMonth = String.valueOf(month);
			if (month == 10) {
				shortMonth = "O";
			}
			if (month == 11) {
				shortMonth = "N";
			}
			if (month == 12) {
				shortMonth = "D";
			}

			optionSymbol = symbol + String.format("%02d", yearIn2Digits) +
					shortMonth + String.format("%02d", day) +
					String.valueOf(strikePrice) + optionType.getType();
		}

		return optionSymbol.toUpperCase();
	}

	public static long getNearestStrikePrice(OptionType type, double lastTradedPrice,
			double strikePriceDelta) {
		switch (type) {
		case BUY_CE, SELL_CE:
			return Math.round((lastTradedPrice - strikePriceDelta) / 100) * 100;
		case BUY_PE, SELL_PE:
			return Math.round((lastTradedPrice + strikePriceDelta) / 100) * 100;
		default:
			throw new InvalidArgumentException("Unexepcted Trade Type");
		}
	}

	public static Class<? extends InstrumentIndicators> getIndicatorClazz(
			AggregationType aggregationType) {
		switch (aggregationType.getName()) {
		case SignalGeneratorConstants.ONE_MINUTE:
			return OneMinuteInstrumentIndicators.class;
		case SignalGeneratorConstants.THREE_MINUTES:
			return ThreeMinutesInstrumentIndicators.class;
		case SignalGeneratorConstants.FIVE_MINUTES:
			return FiveMinutesInstrumentIndicators.class;
		case SignalGeneratorConstants.FIFTEEN_MINUTES:
			return FifteenMinutesInstrumentIndicators.class;
		case SignalGeneratorConstants.ONE_HOUR:
			return OneHourInstrumentIndicators.class;
		case SignalGeneratorConstants.ONE_DAY:
			return DailyInstrumentIndicators.class;
		default:
			return OneMinuteInstrumentIndicators.class;
		}
	}

	public static Class<? extends InstrumentView> getInstrumentViewClazz(
			AggregationType aggregationType) {
		switch (aggregationType.getName()) {
		case SignalGeneratorConstants.ONE_MINUTE:
			return OneMinuteInstrumentView.class;
		case SignalGeneratorConstants.THREE_MINUTES:
			return ThreeMinuteInstrumentView.class;
		case SignalGeneratorConstants.FIVE_MINUTES:
			return FiveMinuteInstrumentView.class;
		case SignalGeneratorConstants.FIFTEEN_MINUTES:
			return FifteenMinuteInstrumentView.class;
		case SignalGeneratorConstants.ONE_HOUR:
			return OneHourInstrumentView.class;
		case SignalGeneratorConstants.ONE_DAY:
			return OneDayInstrumentView.class;
		default:
			return OneMinuteInstrumentView.class;
		}
	}

}

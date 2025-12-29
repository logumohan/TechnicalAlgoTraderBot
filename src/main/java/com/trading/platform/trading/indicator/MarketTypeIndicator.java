package com.trading.platform.trading.indicator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

public class MarketTypeIndicator extends CachedIndicator<MarketType> {

	private static final Logger LOGGER = LogManager.getLogger(MarketTypeIndicator.class);
	
	private static final int MIN_BARS_REQUIRED = 14;
	
	private static final int TRENDING_MIN_ADX = 25;
	
	private static final int RANGE_BOUND_MAX_ADX = 20;
	
	private static final int VOLATILE_MIN_RSI = 20;
	
	private BarSeries series;
	
	private int barCount;
		
	public MarketTypeIndicator(BarSeries series) {
		this(series, MIN_BARS_REQUIRED);
	}
	
	public MarketTypeIndicator(BarSeries series, int barCount) {
		super(series);
		this.series = series;
		this.barCount = barCount;
	}

	@Override
	protected MarketType calculate(int index) {
		LOGGER.info("MarketTypeIndicator: Bar Count {} (expected {})", series.getBarCount(), barCount);
		if (series.getBarCount() < barCount) {
			return MarketType.LOW_VOLUME;
		}

		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		ADXIndicator adx = new ADXIndicator(series, barCount);
		RSIIndicator rsi = new RSIIndicator(closePrice, barCount);

		int endIndex = series.getEndIndex();
		double adxValue = adx.getValue(endIndex).doubleValue();
		double rsiValue = rsi.getValue(endIndex).doubleValue();

		LOGGER.info("MarketTypeIndicator: ADX {}, RSI {}", adxValue, rsiValue);
		
		if (adxValue > TRENDING_MIN_ADX) {
			LOGGER.info("MarketTypeIndicator: Step #1 TRENDING");
			return MarketType.TRENDING;
		} else if (adxValue < RANGE_BOUND_MAX_ADX) {
			LOGGER.info("MarketTypeIndicator: Step #2 RANGE_BOUND");
			return MarketType.RANGE_BOUND;
		} else if (Math.abs(rsiValue - 50) > VOLATILE_MIN_RSI) {
			LOGGER.info("MarketTypeIndicator: Step #3 VOLATILE");
			return MarketType.VOLATILE;
		} else {
			LOGGER.info("MarketTypeIndicator: Step #4 LOW_VOLUME");
			return MarketType.LOW_VOLUME;
		}
	}

	@Override
	public int getCountOfUnstableBars() {
		return 0;
	}

}

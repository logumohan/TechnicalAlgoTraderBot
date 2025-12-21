package com.trading.platform.trading.strategies;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.BooleanRule;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.trading.indicator.MACDHistogramColorIndicator;

public class HAMACDOptionsBuyStrategy extends SignalStrategy {

	private static final Logger LOGGER = LogManager.getLogger("HA_MACD_STRATEGY");

	private static final int MACD_SHORT_EMA_BAR_COUNT = 12;

	private static final int MACD_LONG_EMA_BAR_COUNT = 26;

	private static final int MACD_SIGNAL_EMA_BAR_COUNT = 9;

	private static final int MINIMUM_BAR_REQUIRED = 26;

	private static final int MACD_UPPER_BOUND = 10;

	private static final int MACD_LOWER_BOUND = -10;

	private ClosePriceIndicator closePrice;

	private MACDIndicator macd;

	private EMAIndicator signal;

	private MACDHistogramColorIndicator colorIndicator;

	private DecimalFormat decimalFormat;

	public HAMACDOptionsBuyStrategy(BarSeriesWrapper barSeriesWrapper) {
		super(barSeriesWrapper.getHaSeries());
		this.closePrice = new ClosePriceIndicator(series);
		this.macd = new MACDIndicator(closePrice, MACD_SHORT_EMA_BAR_COUNT, MACD_LONG_EMA_BAR_COUNT);
		this.signal = new EMAIndicator(macd, MACD_SIGNAL_EMA_BAR_COUNT);
		this.colorIndicator = new MACDHistogramColorIndicator(macd, signal);
		this.decimalFormat = new DecimalFormat("0.00");
		this.decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);

		logEntryAndExitRuleInfo();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("HA_MACD: Bar series dump :\n{}", barSeriesWrapper.dumpHaSeries());
		}
	}

	@Override
	public String getName() {
		return "HA_MACD";
	}

	public Strategy getStrategy() {
		return new BaseStrategy(getEntryRule(), getExitRule());
	}

	private void logEntryAndExitRuleInfo() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("EntryRule: {}: barCount - {}, macd - {}, signal - {}, histogram - {}, " +
					"isGreen - {}, isRed - {}, MACD > 10 - {}, MACD Signal > 10 - {}, " +
					"MACD < -10 - {}, MACD Signal < -10 - {}",
					series.getName(), series.getBarCount(),
					decimalFormat.format(macd.getValue(series.getEndIndex()).doubleValue()),
					decimalFormat.format(signal.getValue(series.getEndIndex()).doubleValue()),
					decimalFormat.format(colorIndicator.getValue(series.getEndIndex()).doubleValue()),
					colorIndicator.isGreen(series.getEndIndex()), colorIndicator.isRed(series.getEndIndex()),
					new OverIndicatorRule(macd, MACD_UPPER_BOUND).isSatisfied(series.getEndIndex()),
					new OverIndicatorRule(signal, MACD_UPPER_BOUND).isSatisfied(series.getEndIndex()),
					new UnderIndicatorRule(macd, MACD_LOWER_BOUND).isSatisfied(series.getEndIndex()),
					new UnderIndicatorRule(signal, MACD_LOWER_BOUND).isSatisfied(series.getEndIndex()));

			LOGGER.info("ExitRule: {}: barCount - {}, macd - {}, signal - {}, histogram - {}, " +
					"isLightGreen - {}, isLightRed - {}", series.getName(), series.getBarCount(),
					decimalFormat.format(macd.getValue(series.getEndIndex()).doubleValue()),
					decimalFormat.format(signal.getValue(series.getEndIndex()).doubleValue()),
					decimalFormat.format(colorIndicator.getValue(series.getEndIndex()).doubleValue()),
					colorIndicator.isLightGreen(series.getEndIndex()),
					colorIndicator.isLightRed(series.getEndIndex()));
		}
	}

	private Rule getEntryRule() {
		Rule buyCERule = new OverIndicatorRule(macd, signal)
				.and(new CrossedUpIndicatorRule(macd, signal))
				.and(new BooleanRule(colorIndicator.isGreen(series.getEndIndex())));
		Rule buyPERule = new UnderIndicatorRule(macd, signal)
				.and(new CrossedDownIndicatorRule(macd, signal))
				.and(new BooleanRule(colorIndicator.isRed(series.getEndIndex())));

		return buyCERule.or(buyPERule);
	}

	private Rule getExitRule() {
		return new BooleanRule(colorIndicator.isLightGreen(series.getEndIndex()))
				.or(new BooleanRule(colorIndicator.isLightRed(series.getEndIndex())));
	}

	@Override
	public boolean shouldBuyCE() {
		if (series.getBarCount() < MINIMUM_BAR_REQUIRED) {
			LOGGER.error(
					"shouldBuyCE: {}: Number of samples is less than minimum samples required, count = {}, required = {}",
					series.getName(), series.getBarCount(), MINIMUM_BAR_REQUIRED);
			return false;
		}

		return getStrategy().shouldEnter(series.getEndIndex()) && colorIndicator.isGreen(series.getEndIndex());
	}

	@Override
	public boolean shouldBuyPE() {
		if (series.getBarCount() < MINIMUM_BAR_REQUIRED) {
			LOGGER.trace(
					"shouldBuyPE: {}: Number of samples is less than minimum samples required, count = {}, required = {}",
					series.getName(), series.getBarCount(), MINIMUM_BAR_REQUIRED);
			return false;
		}

		return getStrategy().shouldEnter(series.getEndIndex()) && colorIndicator.isRed(series.getEndIndex());
	}

	@Override
	public boolean shouldSellCE() {
		if (series.getBarCount() < MINIMUM_BAR_REQUIRED) {
			LOGGER.error(
					"shouldSellCE: {}: Number of samples is less than minimum samples required, count = {}, required = {}",
					series.getName(), series.getBarCount(), MINIMUM_BAR_REQUIRED);
			return false;
		}

		return getStrategy().shouldExit(series.getEndIndex()) && colorIndicator.isLightGreen(series.getEndIndex());
	}

	@Override
	public boolean shouldSellPE() {
		if (series.getBarCount() < MINIMUM_BAR_REQUIRED) {
			LOGGER.trace(
					"shouldSellPE: {}: Number of samples is less than minimum samples required, count = {}, required = {}",
					series.getName(), series.getBarCount(), MINIMUM_BAR_REQUIRED);
			return false;
		}

		return getStrategy().shouldExit(series.getEndIndex()) && colorIndicator.isLightRed(series.getEndIndex());
	}

}

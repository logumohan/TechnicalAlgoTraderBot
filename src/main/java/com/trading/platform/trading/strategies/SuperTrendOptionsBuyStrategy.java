package com.trading.platform.trading.strategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.supertrend.SuperTrendIndicator;
import org.ta4j.core.rules.BooleanRule;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.NotRule;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.trading.indicator.ImpulseMACDIndicator;

public class SuperTrendOptionsBuyStrategy extends SignalStrategy {

	private static final Logger LOGGER = LogManager.getLogger("SUPER_TREND_IMACD_STRATEGY");

	private static final int T1_BAR_COUNT = 10;

	private static final int T2_BAR_COUNT = 11;

	private static final int T3_BAR_COUNT = 12;

	private static final double T1_MULTIPLIER = 1;

	private static final double T2_MULTIPLIER = 2;

	private static final double T3_MULTIPLIER = 3;

	private static final int IMACD_SMA_BAR_COUNT = 34;

	private static final int IMACD_SIGNAL_SMA_BAR_COUNT = 9;

	private static final int MINIMUM_BAR_REQUIRED = 34;

	private SuperTrendIndicator t1Indicator;

	private SuperTrendIndicator t2Indicator;

	private SuperTrendIndicator t3Indicator;

	private ImpulseMACDIndicator imacd;

	private SMAIndicator imacdSignal;

	public SuperTrendOptionsBuyStrategy(BarSeriesWrapper barSeriesWrapper, InstrumentIndicators indicator) {
		super(barSeriesWrapper.getHaSeries());
		this.t1Indicator = new SuperTrendIndicator(series, T1_BAR_COUNT, T1_MULTIPLIER);
		this.t2Indicator = new SuperTrendIndicator(series, T2_BAR_COUNT, T2_MULTIPLIER);
		this.t3Indicator = new SuperTrendIndicator(series, T3_BAR_COUNT, T3_MULTIPLIER);
		this.imacd = new ImpulseMACDIndicator(series, IMACD_SMA_BAR_COUNT, indicator);
		this.imacdSignal = new SMAIndicator(imacd, IMACD_SIGNAL_SMA_BAR_COUNT);

		logEntryAndExitRuleInfo();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("SUPER_TREND_IMACD_STRATEGY: Bar series dump :\n{}", barSeriesWrapper.dumpSeries());
		}
	}

	@Override
	public String getName() {
		return "SUPER_TREND_IMACD";
	}

	public Strategy getStrategy() {
		return new BaseStrategy(getBuyCERule(), getBuyPERule());
	}

	private void logEntryAndExitRuleInfo() {
		if (LOGGER.isInfoEnabled()) {
			int index = series.getEndIndex();
			int prevIndex = series.getEndIndex() - 1;
			if (prevIndex < 0) {
				prevIndex = index;
			}
			LOGGER.info(
					"getBuyCERule: Bar Name - {}, T1 - {}, T2 - {}, T3 - {}, IMCAD_ZERO - {}, SIGNAL_ZERO - {}",
					series.getName(),
					new CrossedUpIndicatorRule(t1Indicator, getClosePriceIndicator()).isSatisfied(index),
					new CrossedUpIndicatorRule(t2Indicator, getClosePriceIndicator()).isSatisfied(index),
					new CrossedUpIndicatorRule(t3Indicator, getClosePriceIndicator()).isSatisfied(index),
					new BooleanRule(imacd.getValue(index).isZero()).isSatisfied(index),
					new BooleanRule(imacdSignal.getValue(index).isZero()).isSatisfied(index));

			LOGGER.info(
					"getBuyPERule: Bar Name - {}, T1 - {}, T2 - {}, T3 - {}, IMCAD_ZERO - {}, SIGNAL_ZERO - {}",
					series.getName(),
					new CrossedDownIndicatorRule(t1Indicator, getClosePriceIndicator()).isSatisfied(index),
					new CrossedDownIndicatorRule(t2Indicator, getClosePriceIndicator()).isSatisfied(index),
					new CrossedDownIndicatorRule(t3Indicator, getClosePriceIndicator()).isSatisfied(index),
					new BooleanRule(imacd.getValue(index).isZero()).isSatisfied(index),
					new BooleanRule(imacdSignal.getValue(index).isZero()).isSatisfied(index));
		}
	}

	private Rule getBuyCERule() {
		return new CrossedUpIndicatorRule(t1Indicator, getClosePriceIndicator())
				.and(new CrossedUpIndicatorRule(t2Indicator, getClosePriceIndicator()))
				.and(new CrossedUpIndicatorRule(t3Indicator, getClosePriceIndicator()))
				.and(new NotRule(new BooleanRule(imacd.getValue(series.getEndIndex()).isZero())))
				.and(new NotRule(new BooleanRule(imacdSignal.getValue(series.getEndIndex()).isZero())));
	}

	private Rule getBuyPERule() {
		return new CrossedDownIndicatorRule(t1Indicator, getClosePriceIndicator())
				.and(new CrossedDownIndicatorRule(t2Indicator, getClosePriceIndicator()))
				.and(new CrossedDownIndicatorRule(t3Indicator, getClosePriceIndicator()))
				.and(new NotRule(new BooleanRule(imacd.getValue(series.getEndIndex()).isZero())))
				.and(new NotRule(new BooleanRule(imacdSignal.getValue(series.getEndIndex()).isZero())));
	}

	@Override
	public boolean shouldBuyCE() {
		if (series.getBarCount() < MINIMUM_BAR_REQUIRED) {
			LOGGER.error("{}: Number of samples is less than minimum samples required, count = {}, required = {}",
					series.getName(), series.getBarCount(), MINIMUM_BAR_REQUIRED);
			return false;
		}

		return getStrategy().shouldEnter(series.getEndIndex());
	}

	@Override
	public boolean shouldBuyPE() {
		if (series.getBarCount() < MINIMUM_BAR_REQUIRED) {
			LOGGER.trace("{}: Number of samples is less than minimum samples required, count = {}, required = {}",
					series.getName(), series.getBarCount(), MINIMUM_BAR_REQUIRED);
			return false;
		}

		return getStrategy().shouldExit(series.getEndIndex());
	}

}

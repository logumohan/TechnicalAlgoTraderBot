package com.trading.platform.trading.strategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.BooleanIndicatorRule;
import org.ta4j.core.rules.NotRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.trading.indicator.BearishHammerIndicator;
import com.trading.platform.trading.indicator.BullishHammerIndicator;
import com.trading.platform.trading.indicator.MACDCrossedDownTrendingIndicator;
import com.trading.platform.trading.indicator.MACDCrossedUpTrendingIndicator;
import com.trading.platform.trading.indicator.MACDDifferenceIndicator;
import com.trading.platform.trading.rule.MACDDifferenceIndicatorRule;

public class MACDOptionsBuyStrategy extends SignalStrategy {

	private static final Logger LOGGER = LogManager.getLogger("MACD_STRATEGY");

	private static final int MACD_SHORT_EMA_BAR_COUNT = 12;

	private static final int MACD_LONG_EMA_BAR_COUNT = 26;

	private static final int MACD_SIGNAL_EMA_BAR_COUNT = 9;

	private static final int MACD_HISTO_TREND_BAR_COUNT = 3;

	private static final int MACD_UPPER_BOUND = 10;

	private static final int MACD_LOWER_BOUND = -10;

	private static final int ATR_BAR_COUNT = 21;

	private static final int MINIMUM_BAR_REQUIRED = 50;

	private ClosePriceIndicator closePrice;

	private MACDIndicator macd;

	private MACDDifferenceIndicator macdHisto;

	private MACDCrossedUpTrendingIndicator histoUpTrend;

	private MACDCrossedDownTrendingIndicator histoDownTrend;

	private EMAIndicator signal;

	private ATRIndicator atr;

	private BullishHammerIndicator bullishHammer;

	private BearishHammerIndicator bearishHammer;

	private InstrumentSubscription subscription;

	public MACDOptionsBuyStrategy(BarSeriesWrapper barSeriesWrapper, InstrumentSubscription subscription,
			InstrumentIndicators prevIndicator) {
		super(barSeriesWrapper.getSeries());
		this.subscription = subscription;
		this.closePrice = new ClosePriceIndicator(series);
		this.macd = new MACDIndicator(closePrice, MACD_SHORT_EMA_BAR_COUNT, MACD_LONG_EMA_BAR_COUNT);
		this.signal = new EMAIndicator(macd, MACD_SIGNAL_EMA_BAR_COUNT);
		this.macdHisto = new MACDDifferenceIndicator(macd, signal);
		this.histoUpTrend = new MACDCrossedUpTrendingIndicator(macdHisto, MACD_HISTO_TREND_BAR_COUNT);
		this.histoDownTrend = new MACDCrossedDownTrendingIndicator(macdHisto, MACD_HISTO_TREND_BAR_COUNT);
		this.atr = new ATRIndicator(series, ATR_BAR_COUNT);
		this.bullishHammer = new BullishHammerIndicator(series);
		this.bearishHammer = new BearishHammerIndicator(series);

		logEntryAndExitRuleInfo();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("MACD: Bar series dump :\n{}", barSeriesWrapper.dumpSeries());
		}
	}

	@Override
	public String getName() {
		return "MACD";
	}

	public Strategy getStrategy() {
		return new BaseStrategy(getBuyCERule(), getBuyPERule());
	}

	private void logEntryAndExitRuleInfo() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("getBuyCERule: Bar Name - {}, MACD over signal - {}, MACD histogram - {}, " +
					"ATR over 40 - {}, MACD histo trending - {}, Not a bearish candle - {}, " +
					"MACD < -10 - {}, MACD Signal < -10 - {}",
					series.getName(),
					new OverIndicatorRule(macd, signal).isSatisfied(series.getEndIndex()),
					new MACDDifferenceIndicatorRule(macd, signal,
							DecimalNum.valueOf(subscription.getMinimumMacdDifference()))
							.isSatisfied(series.getEndIndex()),
					new OverIndicatorRule(atr, subscription.getMinimumAtr()).isSatisfied(series.getEndIndex()),
					new BooleanIndicatorRule(histoUpTrend).isSatisfied(series.getEndIndex()),
					new NotRule(new BooleanIndicatorRule(bearishHammer)).isSatisfied(series.getEndIndex()),
					new UnderIndicatorRule(macd, MACD_LOWER_BOUND).isSatisfied(series.getEndIndex()),
					new UnderIndicatorRule(signal, MACD_LOWER_BOUND).isSatisfied(series.getEndIndex()));

			LOGGER.info("getBuyPERule: Bar Name - {}, MACD under signal - {}, MACD histogram - {}, " +
					"ATR over 40 - {}, MACD histo trending - {}, Not a bullish candle - {}, " +
					"MACD > 10 - {}, MACD Signal > 10 - {}",
					series.getName(),
					new UnderIndicatorRule(macd, signal).isSatisfied(series.getEndIndex()),
					new MACDDifferenceIndicatorRule(signal, macd,
							DecimalNum.valueOf(subscription.getMinimumMacdDifference()))
							.isSatisfied(series.getEndIndex()),
					new OverIndicatorRule(atr, subscription.getMinimumAtr()).isSatisfied(series.getEndIndex()),
					new BooleanIndicatorRule(histoDownTrend).isSatisfied(series.getEndIndex()),
					new NotRule(new BooleanIndicatorRule(bullishHammer)).isSatisfied(series.getEndIndex()),
					new OverIndicatorRule(macd, MACD_UPPER_BOUND).isSatisfied(series.getEndIndex()),
					new OverIndicatorRule(signal, MACD_UPPER_BOUND).isSatisfied(series.getEndIndex()));
		}
	}

	private Rule getBuyCERule() {
		return new OverIndicatorRule(macd, signal)
				.and(new MACDDifferenceIndicatorRule(macd, signal,
						DecimalNum.valueOf(subscription.getMinimumMacdDifference())))
				.and(new OverIndicatorRule(atr, subscription.getMinimumAtr()))
				.and(new BooleanIndicatorRule(histoUpTrend))
				.and(new NotRule(new BooleanIndicatorRule(bearishHammer)))
				.and(new UnderIndicatorRule(macd, MACD_LOWER_BOUND))
				.and(new UnderIndicatorRule(signal, MACD_LOWER_BOUND));
	}

	private Rule getBuyPERule() {
		return new UnderIndicatorRule(macd, signal)
				.and(new MACDDifferenceIndicatorRule(signal, macd,
						DecimalNum.valueOf(subscription.getMinimumMacdDifference())))
				.and(new OverIndicatorRule(atr, subscription.getMinimumAtr()))
				.and(new BooleanIndicatorRule(histoDownTrend))
				.and(new NotRule(new BooleanIndicatorRule(bullishHammer)))
				.and(new OverIndicatorRule(macd, MACD_UPPER_BOUND))
				.and(new OverIndicatorRule(signal, MACD_UPPER_BOUND));
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

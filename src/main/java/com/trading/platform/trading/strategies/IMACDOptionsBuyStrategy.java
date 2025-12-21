package com.trading.platform.trading.strategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.BooleanIndicatorRule;
import org.ta4j.core.rules.BooleanRule;
import org.ta4j.core.rules.NotRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.trading.indicator.BearishHammerIndicator;
import com.trading.platform.trading.indicator.BullishHammerIndicator;
import com.trading.platform.trading.indicator.ImpulseMACDIndicator;
import com.trading.platform.trading.indicator.MACDCrossedDownTrendingIndicator;
import com.trading.platform.trading.indicator.MACDCrossedUpTrendingIndicator;
import com.trading.platform.trading.indicator.MACDDifferenceIndicator;
import com.trading.platform.trading.rule.MACDDifferenceIndicatorRule;

public class IMACDOptionsBuyStrategy extends SignalStrategy {

	private static final Logger LOGGER = LogManager.getLogger("IMACD_STRATEGY");

	private static final int IMACD_SMA_BAR_COUNT = 34;

	private static final int IMACD_SIGNAL_SMA_BAR_COUNT = 9;

	private static final int IMACD_HISTO_TREND_BAR_COUNT = 3;

	private static final int ATR_BAR_COUNT = 21;

	private static final int MINIMUM_BAR_REQUIRED = 50;

	private ImpulseMACDIndicator imacd;

	private SMAIndicator imacdSignal;

	private MACDDifferenceIndicator macdHisto;

	private MACDCrossedUpTrendingIndicator histoUpTrend;

	private MACDCrossedDownTrendingIndicator histoDownTrend;

	private ATRIndicator atr;

	private BullishHammerIndicator bullishHammer;

	private BearishHammerIndicator bearishHammer;

	private InstrumentSubscription subscription;

	public IMACDOptionsBuyStrategy(BarSeriesWrapper barSeriesWrapper, InstrumentSubscription subscription,
			InstrumentIndicators indicator) {
		super(barSeriesWrapper.getSeries());
		this.subscription = subscription;
		this.imacd = new ImpulseMACDIndicator(series, IMACD_SMA_BAR_COUNT, indicator);
		this.imacdSignal = new SMAIndicator(imacd, IMACD_SIGNAL_SMA_BAR_COUNT);
		this.macdHisto = new MACDDifferenceIndicator(imacd, imacdSignal);
		this.histoUpTrend = new MACDCrossedUpTrendingIndicator(macdHisto, IMACD_HISTO_TREND_BAR_COUNT);
		this.histoDownTrend = new MACDCrossedDownTrendingIndicator(macdHisto, IMACD_HISTO_TREND_BAR_COUNT);
		this.atr = new ATRIndicator(series, ATR_BAR_COUNT);
		this.bullishHammer = new BullishHammerIndicator(series);
		this.bearishHammer = new BearishHammerIndicator(series);

		logEntryAndExitRuleInfo();
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("IMACD: Bar series dump :\n{}", barSeriesWrapper.dumpSeries());
		}
	}

	@Override
	public String getName() {
		return "IMACD";
	}

	public Strategy getStrategy() {
		return new BaseStrategy(getBuyCERule(), getBuyPERule());
	}

	private void logEntryAndExitRuleInfo() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("getBuyCERule: Bar Name - {}, IMACD over signal - {}, IMACD histogram - {}, IMACD != 0 - {}, " +
					"ATR over 30 - {}, MACD histo trending - {}, Not a bearish candle - {}", series.getName(),
					new OverIndicatorRule(imacd, imacdSignal).isSatisfied(series.getEndIndex()),
					new MACDDifferenceIndicatorRule(imacd, imacdSignal,
							DecimalNum.valueOf(subscription.getMinimumMacdDifference()))
							.isSatisfied(series.getEndIndex()),
					new NotRule(new BooleanRule(imacd.getValue(series.getEndIndex()).isZero()))
							.isSatisfied(series.getEndIndex()),
					new OverIndicatorRule(atr, subscription.getMinimumAtr()).isSatisfied(series.getEndIndex()),
					new BooleanIndicatorRule(histoUpTrend).isSatisfied(series.getEndIndex()),
					new NotRule(new BooleanIndicatorRule(bearishHammer)).isSatisfied(series.getEndIndex()));

			LOGGER.info("getBuyPERule: Bar Name - {}, IMACD over signal - {}, IMACD histogram - {}, IMACD != 0 - {}, " +
					"ATR over 40 - {}, MACD histo trending - {}, Not a bullish candle - {}", series.getName(),
					new UnderIndicatorRule(imacd, imacdSignal).isSatisfied(series.getEndIndex()),
					new MACDDifferenceIndicatorRule(imacdSignal, imacd,
							DecimalNum.valueOf(subscription.getMinimumMacdDifference()))
							.isSatisfied(series.getEndIndex()),
					new NotRule(new BooleanRule(imacd.getValue(series.getEndIndex()).isZero()))
							.isSatisfied(series.getEndIndex()),
					new OverIndicatorRule(atr, subscription.getMinimumAtr()).isSatisfied(series.getEndIndex()),
					new BooleanIndicatorRule(histoDownTrend).isSatisfied(series.getEndIndex()),
					new NotRule(new BooleanIndicatorRule(bullishHammer)).isSatisfied(series.getEndIndex()));
		}
	}

	private Rule getBuyCERule() {
		return new OverIndicatorRule(imacd, imacdSignal)
				.and(new MACDDifferenceIndicatorRule(imacd, imacdSignal,
						DecimalNum.valueOf(subscription.getMinimumMacdDifference())))
				.and(new NotRule(new BooleanRule(imacd.getValue(series.getEndIndex()).isZero())))
				.and(new OverIndicatorRule(atr, subscription.getMinimumAtr()))
				.and(new BooleanIndicatorRule(histoUpTrend))
				.and(new NotRule(new BooleanIndicatorRule(bearishHammer)));
	}

	private Rule getBuyPERule() {
		return new UnderIndicatorRule(imacd, imacdSignal)
				.and(new MACDDifferenceIndicatorRule(imacdSignal, imacd,
						DecimalNum.valueOf(subscription.getMinimumMacdDifference())))
				.and(new NotRule(new BooleanRule(imacd.getValue(series.getEndIndex()).isZero())))
				.and(new OverIndicatorRule(atr, subscription.getMinimumAtr()))
				.and(new BooleanIndicatorRule(histoDownTrend))
				.and(new NotRule(new BooleanIndicatorRule(bullishHammer)));
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

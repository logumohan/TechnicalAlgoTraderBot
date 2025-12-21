package com.trading.platform.trading.strategies;

import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import com.trading.platform.service.series.BarSeriesWrapper;

public class ExponentialMovingAverageStrategy extends SignalStrategy {

	public ExponentialMovingAverageStrategy(BarSeriesWrapper barSeriesWrapper) {
		super(barSeriesWrapper.getSeries());
	}
	
	@Override
	public String getName() {
		return "EMA";
	}

	public Strategy getStrategy() {
		return new BaseStrategy(getEntryRule(), getExitRule());
	}

	public Rule getEntryRule() {
		return new OverIndicatorRule(getClosePriceIndicator(), getShortEMA());
	}

	public Rule getExitRule() {
		return new UnderIndicatorRule(getClosePriceIndicator(), getShortEMA());
	}

}

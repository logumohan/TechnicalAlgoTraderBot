package com.trading.platform.trading.strategies;

import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import com.trading.platform.service.series.BarSeriesWrapper;

public class SimpleMovingAverageStrategy extends SignalStrategy {

	public SimpleMovingAverageStrategy(BarSeriesWrapper barSeriesWrapper) {
		super(barSeriesWrapper.getSeries());
	}
	
	@Override
	public String getName() {
		return "SMA";
	}

	public Strategy getStrategy() {
		return new BaseStrategy(getEntryRule(), getExitRule());
	}

	public Rule getEntryRule() {
		return new OverIndicatorRule(getClosePriceIndicator(), getShortSMA());
	}

	public Rule getExitRule() {
		return new UnderIndicatorRule(getClosePriceIndicator(), getShortSMA());
	}

}

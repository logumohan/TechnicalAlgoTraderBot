package com.trading.platform.trading.strategies;

import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import com.trading.platform.service.series.BarSeriesWrapper;

public class MovingAverageStrategy extends SignalStrategy {

	public MovingAverageStrategy(BarSeriesWrapper barSeriesWrapper) {
		super(barSeriesWrapper.getSeries());
	}
	
	@Override
	public String getName() {
		return "MA";
	}

	public Strategy getStrategy() {
		return new BaseStrategy(getEntryRule(), getExitRule());
	}

	public Rule getEntryRule() {
		return new OverIndicatorRule(getShortEMA(), getLongEMA())
				.and(new CrossedDownIndicatorRule(getStochasticOKIndicator(), 20))
				.and(new OverIndicatorRule(getMACDIndicator(), getEMAMACDIndicator()));
	}

	public Rule getExitRule() {
		return new UnderIndicatorRule(getShortEMA(), getLongEMA())
				.and(new CrossedUpIndicatorRule(getStochasticOKIndicator(), 80))
				.and(new UnderIndicatorRule(getMACDIndicator(), getEMAMACDIndicator()));
	}

}

package com.trading.platform.trading.rule;

import org.ta4j.core.Indicator;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.AbstractRule;

public class MACDDifferenceIndicatorRule extends AbstractRule {

	private final Indicator<Num> first;

	private final Indicator<Num> second;

	private final Num threshold;

	public MACDDifferenceIndicatorRule(Indicator<Num> first, Indicator<Num> second, Num threshold) {
		this.first = first;
		this.second = second;
		this.threshold = threshold;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		boolean satisfied = first.getValue(index).minus(second.getValue(index))
				.isGreaterThanOrEqual(threshold);
		traceIsSatisfied(index, satisfied);
		return satisfied;
	}

}

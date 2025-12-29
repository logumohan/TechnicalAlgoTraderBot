package com.trading.platform.trading.indicator;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class MACDCrossedUpTrendingIndicator extends CachedIndicator<Boolean> {

	private static final int DEFAULT_BAR_COUNT = 3;

	private Indicator<Num> indicator;

	private int barCount;

	public MACDCrossedUpTrendingIndicator(Indicator<Num> indicator) {
		this(indicator, DEFAULT_BAR_COUNT);
	}

	public MACDCrossedUpTrendingIndicator(Indicator<Num> indicator, int barCount) {
		super(indicator);
		this.indicator = indicator;
		this.barCount = barCount;
	}

	@Override
	protected Boolean calculate(int index) {
		if (index > 0) {
			for (int barIndex = Math.max(0, index - barCount) + 1; barIndex <= index; barIndex++) {
				if (indicator.getValue(barIndex).isLessThanOrEqual(indicator.getValue(barIndex
						- 1))) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public int getCountOfUnstableBars() {
		return 0;
	}

}

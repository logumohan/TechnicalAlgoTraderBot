package com.trading.platform.trading.indicator;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.num.Num;

public class HLC3Indicator extends CachedIndicator<Num> {

	private HighPriceIndicator highPrice;

	private LowPriceIndicator lowPrice;

	private ClosePriceIndicator closePrice;

	public HLC3Indicator(BarSeries series) {
		super(series);

		this.highPrice = new HighPriceIndicator(series);
		this.lowPrice = new LowPriceIndicator(series);
		this.closePrice = new ClosePriceIndicator(series);
	}

	@Override
	protected Num calculate(int index) {
		Num total = highPrice.getValue(index).plus(lowPrice.getValue(index)).plus(closePrice.getValue(index));
		return total.dividedBy(numOf(3));
	}

	@Override
	public int getUnstableBars() {
		// TODO Auto-generated method stub
		return 0;
	}

}

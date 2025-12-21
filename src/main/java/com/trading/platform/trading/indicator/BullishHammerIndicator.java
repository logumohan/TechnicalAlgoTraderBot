package com.trading.platform.trading.indicator;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class BullishHammerIndicator extends CachedIndicator<Boolean> {

	public BullishHammerIndicator(BarSeries series) {
		super(series);
	}

	@Override
	protected Boolean calculate(int index) {
		Bar bar = getBarSeries().getBar(index);
		Num body = bar.getClosePrice().minus(bar.getOpenPrice());
		Num shadowTop = bar.getHighPrice().minus(bar.getClosePrice());
		Num shadowBottom = bar.getOpenPrice().minus(bar.getLowPrice());
		Num shadowRatio = shadowBottom.dividedBy(body);
		Num hammerRatio = shadowTop.dividedBy(body);
		
		return body.isGreaterThan(numOf(0)) && shadowBottom.isGreaterThan(numOf(2).multipliedBy(body))
				&& shadowTop.isLessThanOrEqual(body) && shadowRatio.isGreaterThanOrEqual(numOf(2))
				&& hammerRatio.isLessThanOrEqual(numOf(1));
	}

	@Override
	public int getUnstableBars() {
		// TODO Auto-generated method stub
		return 0;
	}

}

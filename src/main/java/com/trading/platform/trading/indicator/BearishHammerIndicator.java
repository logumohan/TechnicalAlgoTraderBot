package com.trading.platform.trading.indicator;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class BearishHammerIndicator extends CachedIndicator<Boolean> {

	private BarSeries series;

	public BearishHammerIndicator(BarSeries series) {
		super(series);
		this.series = series;
	}

	@Override
	protected Boolean calculate(int index) {
		Bar bar = getBarSeries().getBar(index);
		Num body = bar.getClosePrice().minus(bar.getOpenPrice());
		Num shadowTop = bar.getHighPrice().minus(bar.getClosePrice());
		Num shadowBottom = bar.getOpenPrice().minus(bar.getLowPrice());
		Num shadowRatio = shadowTop.dividedBy(body);
		Num hammerRatio = shadowBottom.dividedBy(body);

		return body.isGreaterThan(series.numFactory().numOf(0))
				&& shadowTop.isGreaterThan(series.numFactory().numOf(2).multipliedBy(body))
				&& shadowBottom.isLessThanOrEqual(body) && shadowRatio.isGreaterThanOrEqual(series
						.numFactory().numOf(2))
				&& hammerRatio.isLessThanOrEqual(series.numFactory().numOf(1));
	}

	@Override
	public int getCountOfUnstableBars() {
		return 0;
	}

}

package com.trading.platform.trading.indicator;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class MACDDifferenceIndicator extends CachedIndicator<Num> {

	private Indicator<Num> macdIndicator;

	private Indicator<Num> signalIndicator;

	public MACDDifferenceIndicator(Indicator<Num> macdIndicator, Indicator<Num> signalIndicator) {
		super(macdIndicator.getBarSeries());
		this.macdIndicator = macdIndicator;
		this.signalIndicator = signalIndicator;
	}

	@Override
	protected Num calculate(int index) {
		return macdIndicator.getValue(index).minus(signalIndicator.getValue(index));
	}

	@Override
	public int getUnstableBars() {
		// TODO Auto-generated method stub
		return 0;
	}
}

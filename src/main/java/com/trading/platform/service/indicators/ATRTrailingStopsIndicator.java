package com.trading.platform.service.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.MMAIndicator;
import org.ta4j.core.indicators.helpers.TRIndicator;
import org.ta4j.core.num.Num;

public class ATRTrailingStopsIndicator extends AbstractIndicator<Num> {

	private static final int MULTIPLIER = 3;

	private final TRIndicator trIndicator;

	private final MMAIndicator mmaIndicator;

	public ATRTrailingStopsIndicator(BarSeries series, int barCount) {
		this(new TRIndicator(series), barCount);
	}

	public ATRTrailingStopsIndicator(TRIndicator trIndicator, int barCount) {
		super(trIndicator.getBarSeries());
		this.trIndicator = trIndicator;
		this.mmaIndicator = new MMAIndicator(
				new TRIndicator(trIndicator.getBarSeries()), barCount);
	}

	@Override
	public Num getValue(int index) {
		return mmaIndicator.getValue(index).multipliedBy(numOf(MULTIPLIER));
	}

	public TRIndicator getTRIndicator() {
		return trIndicator;
	}

	public int getBarCount() {
		return mmaIndicator.getBarCount();
	}

	@Override
	public int getUnstableBars() {
		// TODO Auto-generated method stub
		return 0;
	}

}

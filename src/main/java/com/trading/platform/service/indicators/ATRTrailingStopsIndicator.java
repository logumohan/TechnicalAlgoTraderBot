package com.trading.platform.service.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.averages.MMAIndicator;
import org.ta4j.core.indicators.helpers.TRIndicator;
import org.ta4j.core.num.Num;

public class ATRTrailingStopsIndicator extends AbstractIndicator<Num> {

	private static final int MULTIPLIER = 3;

	private final TRIndicator trIndicator;

	private final MMAIndicator mmaIndicator;
	
	private BarSeries series;

	public ATRTrailingStopsIndicator(BarSeries series, int barCount) {
		this(new TRIndicator(series), barCount);
		this.series = series;
	}

	public ATRTrailingStopsIndicator(TRIndicator trIndicator, int barCount) {
		super(trIndicator.getBarSeries());
		this.trIndicator = trIndicator;
		this.mmaIndicator = new MMAIndicator(
				new TRIndicator(trIndicator.getBarSeries()), barCount);
	}

	@Override
	public Num getValue(int index) {
		return mmaIndicator.getValue(index).multipliedBy(series.numFactory().numOf(MULTIPLIER));
	}

	public TRIndicator getTRIndicator() {
		return trIndicator;
	}

	public int getBarCount() {
		return mmaIndicator.getBarCount();
	}

	@Override
	public int getCountOfUnstableBars() {
		return 0;
	}

}

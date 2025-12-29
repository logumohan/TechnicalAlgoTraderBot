package com.trading.platform.trading.indicator;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.adx.MinusDIIndicator;
import org.ta4j.core.indicators.adx.PlusDIIndicator;

public class MarketDirectionIndicator extends CachedIndicator<MarketDirection> {

	private static final int MIN_BARS_REQUIRED = 14;

	private final PlusDIIndicator plusDI;

	private final MinusDIIndicator minusDI;

	public MarketDirectionIndicator(BarSeries series) {
		this(series, MIN_BARS_REQUIRED);
	}

	public MarketDirectionIndicator(BarSeries series, int barCount) {
		super(series);
		this.plusDI = new PlusDIIndicator(series, barCount);
		this.minusDI = new MinusDIIndicator(series, barCount);
	}

	@Override
	protected MarketDirection calculate(int index) {
		double plus = plusDI.getValue(index).doubleValue();
		double minus = minusDI.getValue(index).doubleValue();

		if (plus > minus) {
			return MarketDirection.BULLISH;
		} else if (minus > plus) {
			return MarketDirection.BEARISH;
		} else {
			return MarketDirection.NEUTRAL;
		}
	}

	@Override
	public int getCountOfUnstableBars() {
		return 0;
	}

}

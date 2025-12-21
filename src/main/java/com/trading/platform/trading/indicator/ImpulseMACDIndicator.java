package com.trading.platform.trading.indicator;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.ZLEMAIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.num.Num;

import com.trading.platform.persistence.entity.InstrumentIndicators;

public class ImpulseMACDIndicator extends CachedIndicator<Num> {

	private SMMAIndicator highPriceSma;

	private SMMAIndicator lowPriceSma;

	private ZLEMAIndicator zlema;

	public ImpulseMACDIndicator(BarSeries series, int barCount, InstrumentIndicators prevIndicator) {
		super(series);

		this.highPriceSma = new SMMAIndicator(new HighPriceIndicator(series), barCount,
				prevIndicator != null ? prevIndicator.getSmmaHp34() : -1);
		this.lowPriceSma = new SMMAIndicator(new LowPriceIndicator(series), barCount,
				prevIndicator != null ? prevIndicator.getSmmaLp34() : -1);
		this.zlema = new ZLEMAIndicator(new HLC3Indicator(series), barCount);
	}

	@Override
	protected Num calculate(int index) {
		Num result;
		if (zlema.getValue(index).isGreaterThan(highPriceSma.getValue(index))) {
			result = zlema.getValue(index).minus(highPriceSma.getValue(index));
		} else if (zlema.getValue(index).isLessThan(lowPriceSma.getValue(index))) {
			result = zlema.getValue(index).minus(lowPriceSma.getValue(index));
		} else {
			result = numOf(0);
		}

		return result;
	}

	@Override
	public int getUnstableBars() {
		// TODO Auto-generated method stub
		return 0;
	}

}

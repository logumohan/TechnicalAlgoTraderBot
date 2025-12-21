package com.trading.platform.trading.indicator;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class MACDHistogramColorIndicator extends CachedIndicator<Num> {

	private Indicator<Num> macdIndicator;

	private Indicator<Num> signalIndicator;

	public MACDHistogramColorIndicator(Indicator<Num> macdIndicator, Indicator<Num> signalIndicator) {
		super(macdIndicator.getBarSeries());
		this.macdIndicator = macdIndicator;
		this.signalIndicator = signalIndicator;
	}

	/*
	 * Positive value
	 * 
	 * Current value is greater than the previous value
	 * OR
	 * Difference between previous and current value is less than or equal to 1
	 */
	public boolean isGreen(int index) {
		if (index <= 0) {
			return false;
		}
		Num open = getBarSeries().getBar(index).getOpenPrice();
		Num low = getBarSeries().getBar(index).getLowPrice();
		if (!open.ceil().isEqual(low.ceil())) {
			return false;
		}
		Num prevValue = getValue(index - 1);
		Num value = getValue(index);
		return value.isGreaterThan(numOf(0)) &&
				(value.isGreaterThanOrEqual(prevValue) || prevValue.minus(value).isLessThanOrEqual(numOf(1)));
	}

	/*
	 * Positive value
	 * 
	 * Current value is lesser than the previous value
	 * OR
	 * Difference between previous and current value is greater than 1
	 */
	public boolean isLightGreen(int index) {
		if (index <= 0) {
			return false;
		}
		Num open = getBarSeries().getBar(index).getOpenPrice();
		Num low = getBarSeries().getBar(index).getLowPrice();
		if (!open.ceil().isEqual(low.ceil())) {
			return false;
		}

		Num prevValue = getValue(index - 1);
		Num value = getValue(index);
		return value.isGreaterThan(numOf(0)) && prevValue.minus(value).isGreaterThan(numOf(1));
	}

	/*
	 * Negative value
	 * 
	 * Current value is lesser than the previous value
	 * OR
	 * Difference between previous and current value is less than or equal to 1
	 */
	public boolean isRed(int index) {
		if (index <= 0) {
			return false;
		}
		Num open = getBarSeries().getBar(index).getOpenPrice();
		Num high = getBarSeries().getBar(index).getHighPrice();
		if (!open.ceil().isEqual(high.ceil())) {
			return false;
		}
		
		Num prevValue = getValue(index - 1);
		Num value = getValue(index);
		return value.isLessThan(numOf(0)) &&
				(value.isLessThanOrEqual(prevValue) || value.minus(prevValue).isLessThanOrEqual(numOf(1)));
	}

	/*
	 * Negative value
	 * 
	 * Current value is greater than the previous value
	 * OR
	 * Difference between previous and current value is greater than 1
	 */
	public boolean isLightRed(int index) {
		if (index <= 0) {
			return false;
		}
		Num open = getBarSeries().getBar(index).getOpenPrice();
		Num high = getBarSeries().getBar(index).getHighPrice();
		if (!open.ceil().isEqual(high.ceil())) {
			return false;
		}
		
		Num prevValue = getValue(index - 1);
		Num value = getValue(index);
		return value.isLessThan(numOf(0)) && value.minus(prevValue).isGreaterThan(numOf(1));
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

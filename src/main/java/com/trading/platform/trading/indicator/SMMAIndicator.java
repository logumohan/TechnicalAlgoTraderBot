package com.trading.platform.trading.indicator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

public class SMMAIndicator extends CachedIndicator<Num> {

	private static final Logger LOGGER = LogManager.getLogger(SMMAIndicator.class);

	private Indicator<Num> indicator;

	private int barCount;

	private double previousSmma;

	public SMMAIndicator(Indicator<Num> indicator, int barCount, double previousSmma) {
		super(indicator);

		this.barCount = barCount;
		this.previousSmma = previousSmma;
		this.indicator = indicator;
	}

	@Override
	protected Num calculate(int index) {
		if (previousSmma <= 0) {
			LOGGER.debug("SMMA [{}] : Previous SMMA missing, value = {}", barCount, indicator.getValue(index));
			return indicator.getValue(index);
		}

		LOGGER.debug("SMMA [{}] : Previous SMMA - {}, Sum = {}, Average - {}", barCount, previousSmma,
				numOf(previousSmma).multipliedBy(numOf(barCount).minus(numOf(1))).plus(indicator.getValue(index)),
				numOf(previousSmma).multipliedBy(numOf(barCount).minus(numOf(1))).plus(indicator.getValue(index))
						.dividedBy(numOf(barCount)));

		return numOf(previousSmma).multipliedBy(numOf(barCount).minus(numOf(1)))
				.plus(indicator.getValue(index))
				.dividedBy(numOf(barCount));
	}

	@Override
	public int getUnstableBars() {
		// TODO Auto-generated method stub
		return 0;
	}

}

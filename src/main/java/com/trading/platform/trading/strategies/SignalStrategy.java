package com.trading.platform.trading.strategies;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

public abstract class SignalStrategy {

	protected BarSeries series;

	protected SignalStrategy(BarSeries series) {
		this.series = series;
		if (series == null) {
			throw new IllegalArgumentException("Series cannot be null");
		}
	}
	
	public abstract String getName();

	protected SMAIndicator getShortSMA() {
		return getShortSMA(12);
	}

	protected ClosePriceIndicator getClosePriceIndicator() {
		return new ClosePriceIndicator(series);
	}

	protected SMAIndicator getShortSMA(int barCount) {
		return new SMAIndicator(getClosePriceIndicator(), barCount);
	}

	protected EMAIndicator getShortEMA() {
		return getShortEMA(9);
	}

	protected EMAIndicator getShortEMA(int barCount) {
		return new EMAIndicator(getClosePriceIndicator(), barCount);
	}

	protected EMAIndicator getLongEMA() {
		return getLongEMA(26);
	}

	protected EMAIndicator getLongEMA(int barCount) {
		return new EMAIndicator(getClosePriceIndicator(), barCount);
	}

	protected MACDIndicator getMACDIndicator() {
		return getMACDIndicator(9, 26);
	}

	protected EMAIndicator getEMAMACDIndicator() {
		return getEMAMACDIndicator(18);
	}

	protected EMAIndicator getEMAMACDIndicator(int barCount) {
		return getEMAMACDIndicator(getMACDIndicator(), barCount);
	}

	protected EMAIndicator getEMAMACDIndicator(MACDIndicator indicator, int barCount) {
		return new EMAIndicator(indicator, barCount);
	}

	protected MACDIndicator getMACDIndicator(int shortBarCount, int longBarCount) {
		return new MACDIndicator(getClosePriceIndicator(), shortBarCount, longBarCount);
	}

	protected StochasticOscillatorKIndicator getStochasticOKIndicator() {
		return getStochasticOKIndicator(14);
	}

	protected StochasticOscillatorKIndicator getStochasticOKIndicator(int barCount) {
		return new StochasticOscillatorKIndicator(series, barCount);
	}

	public boolean shouldBuyCE() {
		return false;
	}

	public boolean shouldBuyPE() {
		return false;
	}

	public boolean shouldSellCE() {
		return false;
	}

	public boolean shouldSellPE() {
		return false;
	}

}

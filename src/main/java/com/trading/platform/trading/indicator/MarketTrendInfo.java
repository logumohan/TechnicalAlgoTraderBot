package com.trading.platform.trading.indicator;

import org.ta4j.core.BarSeries;

public class MarketTrendInfo {

	private static final int MIN_BARS_REQUIRED = 14;

	private MarketType marketType;

	private MarketDirection marketDirection;

	public MarketTrendInfo(BarSeries series) {
		this.marketType = new MarketTypeIndicator(series, MIN_BARS_REQUIRED)
				.getValue(series.getEndIndex());
		this.marketDirection = new MarketDirectionIndicator(series, MIN_BARS_REQUIRED)
				.getValue(series.getEndIndex());
	}

	public MarketType getMarketType() {
		return marketType;
	}

	public MarketDirection getMarketDirection() {
		return marketDirection;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MarketTrendInfo [marketType=");
		builder.append(marketType);
		builder.append(", marketDirection=");
		builder.append(marketDirection);
		builder.append("]");
		return builder.toString();
	}

}

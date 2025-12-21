package com.trading.platform.controller.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * {
 * "name": "NIFTY BANK",
 * "token": 260105,
 * "strategy": "HAMACD",
 * "aggregation-type": "THREE_MINUTES",
 * "signal": "SELL",
 * "option-type": "PE",
 * "close-price": 43265.4,
 * "position": "1",
 * "indicators": {
 * "imacd_histogram": 18.65298952859868
 * }
 * }
 */
public class TradingViewAlert implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("name")
	private String name;

	@JsonProperty("token")
	private long token;

	@JsonProperty("strategy")
	private String strategy;

	@JsonProperty("aggregation-type")
	private String aggregationType;

	@JsonProperty("signal")
	private String signal;

	@JsonProperty("option-type")
	private String optionType;

	@JsonProperty("close-price")
	private double closePrice;

	@JsonProperty("position")
	private String position;

	@JsonProperty("indicators")
	private TradingViewIndicators indicators;

	public String getName() {
		return name;
	}

	public long getToken() {
		return token;
	}

	public String getStrategy() {
		return strategy;
	}

	public String getAggregationType() {
		return aggregationType;
	}

	public String getSignal() {
		return signal;
	}

	public String getOptionType() {
		return optionType;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public String getPosition() {
		return position;
	}

	public TradingViewIndicators getIndicators() {
		return indicators;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setToken(long token) {
		this.token = token;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public void setAggregationType(String aggregationType) {
		this.aggregationType = aggregationType;
	}

	public void setSignal(String signal) {
		this.signal = signal;
	}

	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setIndicators(TradingViewIndicators indicators) {
		this.indicators = indicators;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradingViewAlert [name=");
		builder.append(name);
		builder.append(", token=");
		builder.append(token);
		builder.append(", strategy=");
		builder.append(strategy);
		builder.append(", aggregationType=");
		builder.append(aggregationType);
		builder.append(", signal=");
		builder.append(signal);
		builder.append(", optionType=");
		builder.append(optionType);
		builder.append(", closePrice=");
		builder.append(closePrice);
		builder.append(", position=");
		builder.append(position);
		builder.append(", indicators=");
		builder.append(indicators);
		builder.append("]");
		return builder.toString();
	}

}

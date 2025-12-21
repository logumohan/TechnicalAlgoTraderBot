package com.trading.platform.controller.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.platform.persistence.entity.Signal;

public class SignalDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("tick-time")
	private Date tickTime;

	@JsonProperty("token")
	private long token;

	@JsonProperty("name")
	private String name;

	@JsonProperty("aggregation-type")
	private String aggregationType;
	
	@JsonProperty("strategy")
	private String strategy;

	@JsonProperty("trade-signal")
	private String tradeSignal;

	@JsonProperty("last-traded-price")
	private double lastTradedPrice;

	@JsonProperty("average-true-range")
	private double averageTrueRange;

	@JsonProperty("vix-last-traded-price")
	private double vixLastTradedPrice;

	@JsonProperty("strike-price")
	private double strikePrice;

	@JsonProperty("option-symbol")
	private String optionSymbol;

	public static SignalDto of(Signal signal) {
		SignalDto signalDto = new SignalDto();
		signalDto.setTickTime(signal.getTickTime());
		signalDto.setToken(signal.getToken());
		signalDto.setName(signal.getName());
		signalDto.setAggregationType(signal.getAggregationType());
		signalDto.setStrategy(signal.getStrategy());
		signalDto.setTradeSignal(signal.getTradeSignal());
		signalDto.setLastTradedPrice(signal.getLastTradedPrice());
		signalDto.setAverageTrueRange(signal.getAverageTrueRange());
		signalDto.setVixLastTradedPrice(signal.getVixLastTradedPrice());
		signalDto.setStrikePrice(signal.getStrikePrice());
		signalDto.setOptionSymbol(signal.getOptionSymbol());

		return signalDto;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "IST")
	public Date getTickTime() {
		return tickTime;
	}

	public long getToken() {
		return token;
	}

	public String getName() {
		return name;
	}

	public String getAggregationType() {
		return aggregationType;
	}
	
	public String getStrategy() {
		return strategy;
	}

	public String getTradeSignal() {
		return tradeSignal;
	}

	public double getLastTradedPrice() {
		return lastTradedPrice;
	}

	public double getAverageTrueRange() {
		return averageTrueRange;
	}

	public double getVixLastTradedPrice() {
		return vixLastTradedPrice;
	}

	public double getStrikePrice() {
		return strikePrice;
	}

	public String getOptionSymbol() {
		return optionSymbol;
	}

	public void setTickTime(Date tickTime) {
		this.tickTime = tickTime;
	}

	public void setToken(long token) {
		this.token = token;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAggregationType(String aggregationType) {
		this.aggregationType = aggregationType;
	}
	
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public void setTradeSignal(String tradeSignal) {
		this.tradeSignal = tradeSignal;
	}

	public void setLastTradedPrice(double lastTradedPrice) {
		this.lastTradedPrice = lastTradedPrice;
	}

	public void setAverageTrueRange(double averageTrueRange) {
		this.averageTrueRange = averageTrueRange;
	}

	public void setVixLastTradedPrice(double vixLastTradedPrice) {
		this.vixLastTradedPrice = vixLastTradedPrice;
	}

	public void setStrikePrice(double strikePrice) {
		this.strikePrice = strikePrice;
	}

	public void setOptionSymbol(String optionSymbol) {
		this.optionSymbol = optionSymbol;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SignalDto [tickTime=");
		builder.append(tickTime);
		builder.append(", token=");
		builder.append(token);
		builder.append(", name=");
		builder.append(name);
		builder.append(", duration=");
		builder.append(aggregationType);
		builder.append(", strategy=");
		builder.append(strategy);
		builder.append(", tradeSignal=");
		builder.append(tradeSignal);
		builder.append(", lastTradedPrice=");
		builder.append(lastTradedPrice);
		builder.append(", averageTrueRange=");
		builder.append(averageTrueRange);
		builder.append(", vixLastTradedPrice=");
		builder.append(vixLastTradedPrice);
		builder.append(", strikePrice=");
		builder.append(strikePrice);
		builder.append(", optionSymbol=");
		builder.append(optionSymbol);
		builder.append("]");
		return builder.toString();
	}

}

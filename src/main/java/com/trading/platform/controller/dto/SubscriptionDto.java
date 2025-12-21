package com.trading.platform.controller.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.platform.persistence.entity.InstrumentSubscription;

public class SubscriptionDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty(value = "token")
	private long token;

	@JsonProperty(value = "name")
	private String name;

	@JsonProperty(value = "option-name")
	private String optionName;

	@JsonProperty(value = "minimum-atr")
	private int minimumAtr;

	@JsonProperty(value = "expiry-day")
	private int expiryDay;
	
	@JsonProperty(value = "monthly-expiry-day")
	private int monthlyExpiryDay;

	@JsonProperty(value = "lot-size")
	private int lotSize;

	@JsonProperty(value = "strike-price-delta")
	private int strikePriceDelta;

	@JsonProperty(value = "minimum-macd-difference")
	private int minimumMacdDifference;

	@JsonProperty(value = "paper-tradable")
	private boolean paperTradable;

	@JsonProperty(value = "tradable")
	private boolean tradable;

	@JsonProperty(value = "parallel-trades")
	private int numParallelTrades;

	@JsonProperty(value = "trades-per-day")
	private int numTradesPerDay;

	@JsonProperty(value = "failed-trades-per-day")
	private int consecutiveFailedTradesPerDay;

	public static SubscriptionDto of(InstrumentSubscription subscription) {
		SubscriptionDto subscriptionDto = new SubscriptionDto();
		subscriptionDto.setToken(subscription.getToken());
		subscriptionDto.setName(subscription.getName());
		subscriptionDto.setOptionName(subscription.getOptionName());
		subscriptionDto.setMinimumAtr(subscription.getMinimumAtr());
		subscriptionDto.setExpiryDay(subscription.getExpiryDay());
		subscriptionDto.setMonthlyExpiryDay(subscription.getMonthlyExpiryDay());
		subscriptionDto.setLotSize(subscription.getLotSize());
		subscriptionDto.setStrikePriceDelta(subscription.getStrikePriceDelta());
		subscriptionDto.setMinimumMacdDifference(subscription.getMinimumMacdDifference());
		subscriptionDto.setPaperTradable(subscription.isPaperTradable());
		subscriptionDto.setTradable(subscription.isTradable());
		subscriptionDto.setNumParallelTrades(subscription.getNumParallelTrades());
		subscriptionDto.setNumTradesPerDay(subscription.getNumTradesPerDay());
		subscriptionDto.setConsecutiveFailedTradesPerDay(subscription.getConsecutiveFailedTradesPerDay());

		return subscriptionDto;
	}

	public InstrumentSubscription toSubscription() {
		InstrumentSubscription instrumentSubscription = new InstrumentSubscription();
		instrumentSubscription.setToken(getToken());
		instrumentSubscription.setName(getName());
		instrumentSubscription.setOptionName(getOptionName());
		instrumentSubscription.setMinimumAtr(getMinimumAtr());
		instrumentSubscription.setExpiryDay(getExpiryDay());
		instrumentSubscription.setMonthlyExpiryDay(getMonthlyExpiryDay());
		instrumentSubscription.setLotSize(getLotSize());
		instrumentSubscription.setStrikePriceDelta(getStrikePriceDelta());
		instrumentSubscription.setMinimumMacdDifference(getMinimumMacdDifference());
		instrumentSubscription.setPaperTradable(isPaperTradable());
		instrumentSubscription.setTradable(isTradable());
		instrumentSubscription.setNumParallelTrades(getNumParallelTrades());
		instrumentSubscription.setNumTradesPerDay(getNumTradesPerDay());
		instrumentSubscription.setConsecutiveFailedTradesPerDay(getConsecutiveFailedTradesPerDay());

		return instrumentSubscription;
	}

	public long getToken() {
		return token;
	}

	public String getName() {
		return name;
	}

	public String getOptionName() {
		return optionName;
	}

	public int getMinimumAtr() {
		return minimumAtr;
	}

	public int getExpiryDay() {
		return expiryDay;
	}

	public int getMonthlyExpiryDay() {
		return monthlyExpiryDay;
	}
	
	public int getLotSize() {
		return lotSize;
	}

	public int getStrikePriceDelta() {
		return strikePriceDelta;
	}

	public int getMinimumMacdDifference() {
		return minimumMacdDifference;
	}

	public boolean isPaperTradable() {
		return paperTradable;
	}

	public boolean isTradable() {
		return tradable;
	}

	public int getNumParallelTrades() {
		return numParallelTrades;
	}

	public int getNumTradesPerDay() {
		return numTradesPerDay;
	}

	public int getConsecutiveFailedTradesPerDay() {
		return consecutiveFailedTradesPerDay;
	}

	public void setToken(long token) {
		this.token = token;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public void setMinimumAtr(int minimumAtr) {
		this.minimumAtr = minimumAtr;
	}

	public void setExpiryDay(int expiryDay) {
		this.expiryDay = expiryDay;
	}
	
	public void setMonthlyExpiryDay(int monthlyExpiryDay) {
		this.monthlyExpiryDay = monthlyExpiryDay;
	}

	public void setLotSize(int lotSize) {
		this.lotSize = lotSize;
	}

	public void setStrikePriceDelta(int strikePriceDelta) {
		this.strikePriceDelta = strikePriceDelta;
	}

	public void setMinimumMacdDifference(int minimumMacdDifference) {
		this.minimumMacdDifference = minimumMacdDifference;
	}

	public void setPaperTradable(boolean paperTradable) {
		this.paperTradable = paperTradable;
	}

	public void setTradable(boolean tradable) {
		this.tradable = tradable;
	}

	public void setNumParallelTrades(int numParallelTrades) {
		this.numParallelTrades = numParallelTrades;
	}

	public void setNumTradesPerDay(int numTradesPerDay) {
		this.numTradesPerDay = numTradesPerDay;
	}

	public void setConsecutiveFailedTradesPerDay(int consecutiveFailedTradesPerDay) {
		this.consecutiveFailedTradesPerDay = consecutiveFailedTradesPerDay;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubscriptionDto [token=");
		builder.append(token);
		builder.append(", name=");
		builder.append(name);
		builder.append(", optionName=");
		builder.append(optionName);
		builder.append(", minimumAtr=");
		builder.append(minimumAtr);
		builder.append(", expiryDay=");
		builder.append(expiryDay);
		builder.append(", monthlyExpiryDay=");
		builder.append(monthlyExpiryDay);
		builder.append(", lotSize=");
		builder.append(lotSize);
		builder.append(", strikePriceDelta=");
		builder.append(strikePriceDelta);
		builder.append(", minimumMacdDifference=");
		builder.append(minimumMacdDifference);
		builder.append(", paperTradable=");
		builder.append(paperTradable);
		builder.append(", tradable=");
		builder.append(tradable);
		builder.append(", parallelTrades=");
		builder.append(numParallelTrades);
		builder.append(", tradesPerDay=");
		builder.append(numTradesPerDay);
		builder.append(", consecutiveFailedTradesPerDay=");
		builder.append(consecutiveFailedTradesPerDay);
		builder.append("]");
		return builder.toString();
	}

}

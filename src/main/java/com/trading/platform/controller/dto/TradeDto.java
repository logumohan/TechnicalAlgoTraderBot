package com.trading.platform.controller.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.platform.persistence.entity.Trade;

public class TradeDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("tick-time")
	private Date tickTime;

	@JsonProperty("trade-id")
	private String tradeId;

	@JsonProperty("user-name")
	private String userName;

	@JsonProperty("job-name")
	private String jobName;

	@JsonProperty("live")
	private boolean isLive;

	@JsonProperty("active")
	private boolean isActive;

	@JsonProperty("token")
	private long token;

	@JsonProperty("name")
	private String name;

	@JsonProperty("aggregationType")
	private String aggregationType;

	@JsonProperty("strategy")
	private String strategy;

	@JsonProperty("trade-signal")
	private String tradeSignal;

	@JsonProperty("last-traded-price")
	private double lastTradedPrice;

	@JsonProperty("average-true-range")
	private double averageTrueRange;

	@JsonProperty("strike-price")
	private double strikePrice;

	@JsonProperty("option-symbol")
	private String optionSymbol;

	@JsonProperty("lot-size")
	private int lotSize;

	@JsonProperty("order-id")
	private String orderId;

	@JsonProperty("sl-order-id")
	private String slOrderId;

	public static TradeDto of(Trade trade) {
		TradeDto tradeDto = new TradeDto();
		tradeDto.setTickTime(trade.getTickTime());
		tradeDto.setTradeId(trade.getTradeId());
		tradeDto.setUserName(trade.getUserName());
		tradeDto.setJobName(trade.getJobName());
		tradeDto.setLive(trade.isLive());
		tradeDto.setActive(trade.isActive());
		tradeDto.setToken(trade.getToken());
		tradeDto.setName(trade.getName());
		tradeDto.setAggregationType(trade.getAggregationType());
		tradeDto.setStrategy(trade.getStrategy());
		tradeDto.setTradeSignal(trade.getTradeSignal());
		tradeDto.setLastTradedPrice(trade.getLastTradedPrice());
		tradeDto.setAverageTrueRange(trade.getAverageTrueRange());
		tradeDto.setStrikePrice(trade.getStrikePrice());
		tradeDto.setOptionSymbol(trade.getOptionSymbol());
		tradeDto.setLotSize(trade.getLotSize());
		tradeDto.setOrderId(trade.getOrderId());
		tradeDto.setSlOrderId(trade.getSlOrderId());

		return tradeDto;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "IST")
	public Date getTickTime() {
		return tickTime;
	}

	public String getTradeId() {
		return tradeId;
	}

	public String getUserName() {
		return userName;
	}

	public String getJobName() {
		return jobName;
	}

	public boolean isLive() {
		return isLive;
	}

	public boolean isActive() {
		return isActive;
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

	public double getStrikePrice() {
		return strikePrice;
	}

	public String getOptionSymbol() {
		return optionSymbol;
	}

	public int getLotSize() {
		return lotSize;
	}

	public String getOrderId() {
		return orderId;
	}

	public String getSlOrderId() {
		return slOrderId;
	}

	public void setTickTime(Date tickTime) {
		this.tickTime = tickTime;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
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

	public void setStrikePrice(double strikePrice) {
		this.strikePrice = strikePrice;
	}

	public void setOptionSymbol(String optionSymbol) {
		this.optionSymbol = optionSymbol;
	}

	public void setLotSize(int lotSize) {
		this.lotSize = lotSize;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setSlOrderId(String slOrderId) {
		this.slOrderId = slOrderId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradeDto [tickTime=");
		builder.append(tickTime);
		builder.append(", tradeId=");
		builder.append(tradeId);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", jobName=");
		builder.append(jobName);
		builder.append(", isLive=");
		builder.append(isLive);
		builder.append(", isActive=");
		builder.append(isActive);
		builder.append(", token=");
		builder.append(token);
		builder.append(", name=");
		builder.append(name);
		builder.append(", aggregationType=");
		builder.append(aggregationType);
		builder.append(", strategy=");
		builder.append(strategy);
		builder.append(", tradeSignal=");
		builder.append(tradeSignal);
		builder.append(", lastTradedPrice=");
		builder.append(lastTradedPrice);
		builder.append(", averageTrueRange=");
		builder.append(averageTrueRange);
		builder.append(", strikePrice=");
		builder.append(strikePrice);
		builder.append(", optionSymbol=");
		builder.append(optionSymbol);
		builder.append(", lotSize=");
		builder.append(lotSize);
		builder.append(", orderId=");
		builder.append(orderId);
		builder.append(", slOrderId=");
		builder.append(slOrderId);
		builder.append("]");
		return builder.toString();
	}

}

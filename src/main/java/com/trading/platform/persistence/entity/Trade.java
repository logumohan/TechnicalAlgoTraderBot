package com.trading.platform.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import com.trading.platform.persistence.entity.key.TradeCompositeId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * CREATE TABLE IF NOT EXISTS TRADE (
 * TICKTIME TIMESTAMP,
 * USER_NAME TEXT NOT NULL,
 * TRADE_ID TEXT,
 * JOB_NAME TEXT,
 * IS_LIVE BOOLEAN,
 * IS_ACTIVE BOOLEAN,
 * TOKEN BIGINT,
 * NAME TEXT,
 * AGGREGATION_TYPE TEXT,
 * STRATEGY TEXT,
 * SIGNAL TEXT,
 * LTP DECIMAL,
 * ATR DECIMAL,
 * STRIKE_PRICE BIGINT,
 * OPTION_SYMBOL TEXT,
 * LOT_SIZE INTEGER NOT NULL CHECK (LOT_SIZE > 0),
 * ORDER_ID TEXT,
 * SL_ORDER_ID TEXT,
 * PRIMARY KEY (TICKTIME, TRADE_ID, TOKEN)
 * );
 */

@Entity
@Table(name = "TRADE")
@IdClass(TradeCompositeId.class)
public class Trade implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TICKTIME")
	private Date tickTime;

	@Id
	@Column(name = "TRADE_ID")
	private String tradeId;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "JOB_NAME")
	private String jobName;

	@Column(name = "IS_LIVE")
	private boolean isLive;

	@Column(name = "IS_ACTIVE")
	private boolean isActive;

	@Id
	@Column(name = "TOKEN")
	private long token;

	@Column(name = "NAME")
	private String name;

	@Column(name = "AGGREGATION_TYPE")
	private String aggregationType;

	@Column(name = "STRATEGY")
	private String strategy;

	@Column(name = "SIGNAL")
	private String tradeSignal;

	@Column(name = "LTP")
	private double lastTradedPrice;

	@Column(name = "ATR")
	private double averageTrueRange;

	@Column(name = "STRIKE_PRICE")
	private double strikePrice;

	@Column(name = "OPTION_SYMBOL")
	private String optionSymbol;

	@Column(name = "LOT_SIZE")
	private int lotSize;
	
	@Column(name = "ORDER_ID")
	private String orderId;

	@Column(name = "SL_ORDER_ID")
	private String slOrderId;

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
		builder.append("Trade [tickTime=");
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

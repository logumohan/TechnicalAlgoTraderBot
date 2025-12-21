package com.trading.platform.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import com.trading.platform.persistence.entity.key.SignalCompositeId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * CREATE TABLE IF NOT EXISTS SIGNAL (
 * TICKTIME TIMESTAMP,
 * TOKEN BIGINT,
 * NAME TEXT,
 * AGGREGATION_TYPE TEXT,
 * STRATEGY TEXT,
 * SIGNAL TEXT,
 * LTP DECIMAL,
 * ATR DECIMAL,
 * VIX DECIMAL,
 * STRIKE_PRICE BIGINT,
 * OPTION_SYMBOL TEXT,
 * PRIMARY KEY (TIMESTAMP, TOKEN)
 * );
 */
@Entity
@Table(name = "SIGNAL")
@IdClass(SignalCompositeId.class)
public class Signal implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TICKTIME")
	private Date tickTime;

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
	
	@Column(name = "VIX")
	private double vixLastTradedPrice;

	@Column(name = "STRIKE_PRICE")
	private double strikePrice;

	@Column(name = "OPTION_SYMBOL")
	private String optionSymbol;

	public Signal() {
		// Do Nothing
	}

	public Signal(Signal signal) {
		this.setTickTime(signal.getTickTime());
		this.setToken(signal.getToken());
		this.setName(signal.getName());
		this.setAggregationType(signal.getAggregationType());
		this.setStrategy(signal.getStrategy());
		this.setTradeSignal(signal.getTradeSignal());
		this.setLastTradedPrice(signal.getLastTradedPrice());
		this.setAverageTrueRange(signal.getAverageTrueRange());
		this.setTickTime(signal.getTickTime());
		this.setVixLastTradedPrice(signal.getVixLastTradedPrice());
		this.setStrikePrice(signal.getStrikePrice());
		this.setOptionSymbol(signal.getOptionSymbol());
	}

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
		builder.append("Signal [tickTime=");
		builder.append(tickTime);
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

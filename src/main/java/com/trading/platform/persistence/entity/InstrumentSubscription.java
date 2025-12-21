package com.trading.platform.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * CREATE TABLE <TABLE NAME> (
 * TOKEN BIGINT,
 * NAME TEXT,
 * OPTION_NAME TEXT,
 * MINIMUM_ATR INTEGER NOT NULL CHECK (MINIMUM_ATR > 0),
 * EXPIRY_DAY INTEGER NOT NULL CHECK (EXPIRY_DAY >= 0 AND EXPIRY_DAY <= 6),
 * MONTHLY_EXPIRY_DAY INTEGER NOT NULL CHECK (MONTHLY_EXPIRY_DAY >= 1 AND MONTHLY_EXPIRY_DAY <= 5),
 * LOT_SIZE INTEGER NOT NULL CHECK (LOT_SIZE > 0),
 * STRIKE_PRICE_DELTA INTEGER NOT NULL CHECK ((NAME = 'NIFTY BANK' AND
 * STRIKE_PRICE_DELTA % 100 = 0)
 * OR (STRIKE_PRICE_DELTA % 50 = 0)),
 * MIN_MACD_DIFF INTEGER NOT NULL CHECK (MIN_MACD_DIFF > 0),
 * PAPER_TRADABLE BOOLEAN DEFAULT TRUE,
 * TRADABLE BOOLEAN DEFAULT FALSE,
 * PARALLEL_TRADES INTEGER NOT NULL CHECK (PARALLEL_TRADES > 0) DEFAULT 1,
 * TRADES_PER_DAY INTEGER NOT NULL CHECK (TRADES_PER_DAY > 0) DEFAULT 3,
 * FAILED_TRADES_PER_DAY INTEGER NOT NULL CHECK (FAILED_TRADES_PER_DAY > 0)
 * DEFAULT 2,
 * PRIMARY KEY (TOKEN));
 */

@Entity
@Table(name = "INSTRUMENT_SUBSCRIPTION")
public class InstrumentSubscription implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TOKEN")
	private long token;

	@Column(name = "NAME")
	private String name;

	@Column(name = "OPTION_NAME")
	private String optionName;

	@Column(name = "MINIMUM_ATR")
	private int minimumAtr;

	@Column(name = "EXPIRY_DAY")
	private int expiryDay;
	
	@Column(name = "MONTHLY_EXPIRY_DAY")
	private int monthlyExpiryDay;

	@Column(name = "LOT_SIZE")
	private int lotSize;

	@Column(name = "STRIKE_PRICE_DELTA")
	private int strikePriceDelta;

	@Column(name = "MIN_MACD_DIFF")
	private int minimumMacdDifference;

	@Column(name = "PAPER_TRADABLE")
	private boolean paperTradable;

	@Column(name = "TRADABLE")
	private boolean tradable;

	@Column(name = "PARALLEL_TRADES")
	private int numParallelTrades;

	@Column(name = "TRADES_PER_DAY")
	private int numTradesPerDay;

	@Column(name = "FAILED_TRADES_PER_DAY")
	private int consecutiveFailedTradesPerDay;

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
		builder.append("InstrumentSubscription [token=");
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
		builder.append(", numParallelTrades=");
		builder.append(numParallelTrades);
		builder.append(", numTradesPerDay=");
		builder.append(numTradesPerDay);
		builder.append(", consecutiveFailedTradesPerDay=");
		builder.append(consecutiveFailedTradesPerDay);
		builder.append("]");
		return builder.toString();
	}

}

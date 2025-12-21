package com.trading.platform.persistence.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.trading.platform.persistence.entity.key.JobCompositeId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * CREATE TABLE IF NOT EXISTS JOBS (
 * NAME TEXT NOT NULL,
 * USER_NAME TEXT NOT NULL,
 * JOB_TYPE TEXT NOT NULL CHECK (JOB_TYPE = 'ATR_TSL' OR JOB_TYPE = 'FIXED_TSL'
 * OR JOB_TYPE = 'FIXED_PROFIT'),
 * STRATEGY TEXT,
 * TRAILING_STRATEGY TEXT NOT NULL CHECK (TRAILING_STRATEGY = 'DEFAULT' OR TRAILING_STRATEGY = 'SIMPLE' 
 * 		OR TRAILING_STRATEGY = 'PROTECTIVE' OR TRAILING_STRATEGY = 'AGGRESSIVE' OR TRAILING_STRATEGY = 'CUSTOM1'
 * 		OR TRAILING_STRATEGY = 'MULTI_TARGET'),
 * TRAIL_BY TEXT NOT NULL CHECK ((JOB_TYPE = 'ATR_TSL' AND TRAIL_BY = 'ATR') OR
 * ((JOB_TYPE = 'FIXED_TSL' OR JOB_TYPE = 'FIXED_PROFIT') AND TRAIL_BY =
 * 'POINTS')),
 * ATR INTEGER CHECK((JOB_TYPE = 'ATR_TSL' AND ATR > 0) OR ATR = 0),
 * ATR_MULTIPLIER DECIMAL CHECK((JOB_TYPE = 'ATR_TSL' AND ATR_MULTIPLIER > 0) OR
 * ATR_MULTIPLIER = 0),
 * AGGREGATION_TYPE TEXT NOT NULL CHECK (AGGREGATION_TYPE = 'ONE_MINUTE' OR
 * AGGREGATION_TYPE = 'THREE_MINUTES' OR AGGREGATION_TYPE = 'FIVE_MINUTES' OR
 * AGGREGATION_TYPE = 'FIFTEEN_MINUTES' OR AGGREGATION_TYPE = 'ONE_HOUR' OR
 * AGGREGATION_TYPE = 'ONE_DAY') DEFAULT 'THREE_MINUTES',
 * LOT_SIZE INTEGER NOT NULL CHECK (LOT_SIZE > 0),
 * TARGETS JSON CHECK(TARGETS IS NOT NULL),
 * STRIKE_PRICE_DELTA INTEGER NOT NULL CHECK (STRIKE_PRICE_DELTA % 10 = 0)
 * DEFAULT 0,
 * MAX_VIX_ALLOWED INTEGER DEFAULT 15,
 * PAPER_TRADABLE BOOLEAN DEFAULT TRUE,
 * TRADABLE BOOLEAN DEFAULT FALSE,
 * TRADABLE_DAYS TEXT[] CHECK(TRADABLE = TRUE AND TRADABLE_DAYS <@ ARRAY['MON',
 * 'TUE', 'WED', 'THU', 'FRI']),
 * 
 * PRIMARY KEY (NAME, USER_NAME)
 * );
 */

@Entity
@Table(name = "JOBS")
@IdClass(JobCompositeId.class)
public class Job implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "NAME")
	private String name;

	@Id
	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "JOB_TYPE")
	private String jobType;

	@Column(name = "STRATEGY")
	private String strategy;

	@Column(name = "TRAILING_STRATEGY")
	private String trailingStrategy;

	@Column(name = "TRAIL_BY")
	private String trailBy;

	@Column(name = "ATR")
	private int atr;

	@Column(name = "ATR_MULTIPLIER")
	private double atrMultiplier;

	@Column(name = "AGGREGATION_TYPE")
	private String aggregationType;

	@Column(name = "LOT_SIZE")
	private int lotSize;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "TARGETS")
	private List<Target> targets;

	@Column(name = "STRIKE_PRICE_DELTA")
	private int strikePriceDelta;

	@Column(name = "MAX_VIX_ALLOWED")
	private int maxVixAllowed;

	@Column(name = "PAPER_TRADABLE")
	private boolean paperTradable;

	@Column(name = "TRADABLE")
	private boolean tradable;

	@Column(name = "TRADABLE_DAYS", columnDefinition = "TEXT[]")
	@JdbcTypeCode(SqlTypes.ARRAY)
	private String[] tradableDays;

	public String getName() {
		return name;
	}

	public String getUserName() {
		return userName;
	}

	public String getJobType() {
		return jobType;
	}

	public String getStrategy() {
		return strategy;
	}

	public String getTrailingStrategy() {
		return trailingStrategy;
	}

	public String getTrailBy() {
		return trailBy;
	}

	public int getAtr() {
		return atr;
	}

	public double getAtrMultiplier() {
		return atrMultiplier;
	}

	public String getAggregationType() {
		return aggregationType;
	}

	public int getLotSize() {
		return lotSize;
	}

	public List<Target> getTargets() {
		return targets;
	}

	public int getStrikePriceDelta() {
		return strikePriceDelta;
	}

	public int getMaxVixAllowed() {
		return maxVixAllowed;
	}

	public boolean isPaperTradable() {
		return paperTradable;
	}

	public boolean isTradable() {
		return tradable;
	}

	public String[] getTradableDays() {
		return tradableDays;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public void setTrailingStrategy(String trailingStrategy) {
		this.trailingStrategy = trailingStrategy;
	}

	public void setTrailBy(String trailBy) {
		this.trailBy = trailBy;
	}

	public void setAtr(int atr) {
		this.atr = atr;
	}

	public void setAtrMultiplier(double atrMultiplier) {
		this.atrMultiplier = atrMultiplier;
	}

	public void setAggregationType(String aggregationType) {
		this.aggregationType = aggregationType;
	}

	public void setLotSize(int lotSize) {
		this.lotSize = lotSize;
	}

	public void setTargets(List<Target> targets) {
		this.targets = targets;
	}

	public void setStrikePriceDelta(int strikePriceDelta) {
		this.strikePriceDelta = strikePriceDelta;
	}

	public void setMaxVixAllowed(int maxVixAllowed) {
		this.maxVixAllowed = maxVixAllowed;
	}

	public void setPaperTradable(boolean paperTradable) {
		this.paperTradable = paperTradable;
	}

	public void setTradable(boolean tradable) {
		this.tradable = tradable;
	}

	public void setTradableDays(String[] tradableDays) {
		this.tradableDays = tradableDays;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Job [name=");
		builder.append(name);
		builder.append(", userName=");
		builder.append(userName);
		builder.append(", jobType=");
		builder.append(jobType);
		builder.append(", strategy=");
		builder.append(strategy);
		builder.append(", trailingStrategy=");
		builder.append(trailingStrategy);
		builder.append(", trailBy=");
		builder.append(trailBy);
		builder.append(", atr=");
		builder.append(atr);
		builder.append(", atrMultiplier=");
		builder.append(atrMultiplier);
		builder.append(", aggregationType=");
		builder.append(aggregationType);
		builder.append(", lotSize=");
		builder.append(lotSize);
		builder.append(", targets=");
		builder.append(targets);
		builder.append(", strikePriceDelta=");
		builder.append(strikePriceDelta);
		builder.append(", maxVixAllowed=");
		builder.append(maxVixAllowed);
		builder.append(", paperTradable=");
		builder.append(paperTradable);
		builder.append(", tradable=");
		builder.append(tradable);
		builder.append(", tradableDays=");
		builder.append(Arrays.asList(tradableDays));
		builder.append("]");
		return builder.toString();
	}

}

package com.trading.platform.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import com.trading.platform.persistence.entity.key.InstrumentCompositeId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * CREATE TABLE <TABLE NAME> (
 * TICKTIME TIMESTAMP,
 * TOKEN BIGINT,
 * NAME TEXT,
 * OPEN MONEY,
 * CLOSE MONEY,
 * HIGH MONEY,
 * LOW MONEY,
 * LTP MONEY,
 * VOLUMETRADED BIGINT,
 * TOTBUYQTY DECIMAL,
 * TOTSELLQTY DECIMAL,
 * PRIMARY KEY (TICKTIME, TOKEN));
 */

@Entity
@Table(name = "INSTRUMENT")
@IdClass(InstrumentCompositeId.class)
public class Instrument implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TICKTIME")
	protected Date tickTime;

	@Id
	@Column(name = "TOKEN")
	protected long token;

	@Column(name = "NAME")
	protected String name;

	@Column(name = "OPEN")
	protected double openPrice;

	@Column(name = "CLOSE")
	protected double closePrice;

	@Column(name = "HIGH")
	protected double highPrice;

	@Column(name = "LOW")
	protected double lowPrice;

	@Column(name = "LTP")
	protected double lastTradedPrice;

	@Column(name = "VOLUMETRADED")
	protected long volumeTraded;

	@Column(name = "TOTBUYQTY")
	protected double totalBuyQuantity;

	@Column(name = "TOTSELLQTY")
	protected double totalSellQuantity;

	public Date getTickTime() {
		return tickTime;
	}

	public long getToken() {
		return token;
	}

	public String getName() {
		return name;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public double getLowPrice() {
		return lowPrice;
	}

	public double getLastTradedPrice() {
		return lastTradedPrice;
	}

	public long getVolumeTraded() {
		return volumeTraded;
	}

	public double getTotalBuyQuantity() {
		return totalBuyQuantity;
	}

	public double getTotalSellQuantity() {
		return totalSellQuantity;
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

	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public void setLastTradedPrice(double lastTradedPrice) {
		this.lastTradedPrice = lastTradedPrice;
	}

	public void setVolumeTraded(long volumeTraded) {
		this.volumeTraded = volumeTraded;
	}

	public void setTotalBuyQuantity(double totalBuyQuantity) {
		this.totalBuyQuantity = totalBuyQuantity;
	}

	public void setTotalSellQuantity(double totalSellQuantity) {
		this.totalSellQuantity = totalSellQuantity;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Instrument [tickTime=");
		builder.append(tickTime);
		builder.append(", token=");
		builder.append(token);
		builder.append(", name=");
		builder.append(name);
		builder.append(", openPrice=");
		builder.append(openPrice);
		builder.append(", closePrice=");
		builder.append(closePrice);
		builder.append(", highPrice=");
		builder.append(highPrice);
		builder.append(", lowPrice=");
		builder.append(lowPrice);
		builder.append(", lastTradedPrice=");
		builder.append(lastTradedPrice);
		builder.append(", volumeTraded=");
		builder.append(volumeTraded);
		builder.append(", totalBuyQuantity=");
		builder.append(totalBuyQuantity);
		builder.append(", totalSellQuantity=");
		builder.append(totalSellQuantity);
		builder.append("]");
		return builder.toString();
	}

}

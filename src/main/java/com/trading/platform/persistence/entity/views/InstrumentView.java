package com.trading.platform.persistence.entity.views;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.MappedSuperclass;

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

@MappedSuperclass
@IdClass(InstrumentViewCompositeId.class)
public abstract class InstrumentView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "BUCKET_TICKTIME")
	protected Date bucketTickTime;

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

	public Date getBucketTickTime() {
		return bucketTickTime;
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

}

package com.trading.platform.persistence.entity;

import java.io.Serializable;
import java.util.Date;

import com.trading.platform.persistence.entity.key.InstrumentIndicatorsCompositeId;

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
 * HIGH MONEY,
 * LOW MONEY,
 * CLOSE MONEY,
 * HAOPEN MONEY,
 * HAHIGH MONEY,
 * HALOW MONEY,
 * HACLOSE MONEY,
 * LTP MONEY,
 * VOLUMETRADED BIGINT,
 * TOTBUYQTY DECIMAL,
 * TOTSELLQTY DECIMAL,
 * HCL3 DECIMAL,
 * ZLEMA_34 DECIMAL,
 * SMMA_HP_34 DECIMAL,
 * SMMA_LP_34 DECIMAL,
 * MACD DECIMAL,
 * MACD_SIGNAL DECIMAL,
 * IMACD DECIMAL,
 * IMACD_SIGNAL DECIMAL,
 * EMA10 DECIMAL,
 * EMA20 DECIMAL,
 * EMA30 DECIMAL,
 * EMA50 DECIMAL,
 * RSI DECIMAL,
 * STOCHASTICRSI DECIMAL,
 * ATR DECIMAL,
 * ATRTS DECIMAL,
 * PRIMARY KEY (TICKTIME, TOKEN));
 */

@MappedSuperclass
@IdClass(InstrumentIndicatorsCompositeId.class)
public class InstrumentIndicators implements Serializable {

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

	@Column(name = "HIGH")
	protected double highPrice;

	@Column(name = "LOW")
	protected double lowPrice;

	@Column(name = "CLOSE")
	protected double closePrice;

	@Column(name = "HAOPEN")
	private double haOpenPrice;

	@Column(name = "HAHIGH")
	private double haHighPrice;

	@Column(name = "HALOW")
	private double haLowPrice;

	@Column(name = "HACLOSE")
	private double haClosePrice;

	@Column(name = "LTP")
	protected double lastTradedPrice;

	@Column(name = "VOLUMETRADED")
	protected long volumeTraded;

	@Column(name = "TOTBUYQTY")
	protected double totalBuyQuantity;

	@Column(name = "TOTSELLQTY")
	protected double totalSellQuantity;

	@Column(name = "HLC3")
	private double hlc3;
	
	@Column(name = "ZLEMA_34")
	private double zlema34;

	@Column(name = "SMMA_HP_34")
	private double smmaHp34;

	@Column(name = "SMMA_LP_34")
	private double smmaLp34;

	@Column(name = "MACD")
	private double macd;

	@Column(name = "MACD_SIGNAL")
	private double macdSignal;

	@Column(name = "IMACD")
	private double imacd;

	@Column(name = "IMACD_SIGNAL")
	private double imacdSignal;

	@Column(name = "EMA10")
	private double ema10;

	@Column(name = "EMA20")
	private double ema20;

	@Column(name = "EMA30")
	private double ema30;

	@Column(name = "EMA50")
	private double ema50;

	@Column(name = "RSI")
	private double rsi;

	@Column(name = "STOCHASTICRSI")
	private double stochasticrsi;

	@Column(name = "ATR")
	private double atr;

	@Column(name = "ATRTS")
	private double atrTs;

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

	public double getHighPrice() {
		return highPrice;
	}

	public double getLowPrice() {
		return lowPrice;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public double getHaOpenPrice() {
		return haOpenPrice;
	}

	public double getHaHighPrice() {
		return haHighPrice;
	}

	public double getHaLowPrice() {
		return haLowPrice;
	}

	public double getHaClosePrice() {
		return haClosePrice;
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

	public double getHlc3() {
		return hlc3;
	}
	
	public double getZlema34() {
		return zlema34;
	}
	
	public double getSmmaHp34() {
		return smmaHp34;
	}
	
	public double getSmmaLp34() {
		return smmaLp34;
	}
	
	public double getMacd() {
		return macd;
	}

	public double getMacdSignal() {
		return macdSignal;
	}

	public double getImacd() {
		return imacd;
	}

	public double getImacdSignal() {
		return imacdSignal;
	}

	public double getEma10() {
		return ema10;
	}

	public double getEma20() {
		return ema20;
	}

	public double getEma30() {
		return ema30;
	}

	public double getEma50() {
		return ema50;
	}

	public double getRsi() {
		return rsi;
	}

	public double getStochasticrsi() {
		return stochasticrsi;
	}

	public double getAtr() {
		return atr;
	}

	public double getAtrTs() {
		return atrTs;
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

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public void setLowPrice(double lowPrice) {
		this.lowPrice = lowPrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

	public void setHaOpenPrice(double haOpenPrice) {
		this.haOpenPrice = haOpenPrice;
	}

	public void setHaHighPrice(double haHighPrice) {
		this.haHighPrice = haHighPrice;
	}

	public void setHaLowPrice(double haLowPrice) {
		this.haLowPrice = haLowPrice;
	}

	public void setHaClosePrice(double haClosePrice) {
		this.haClosePrice = haClosePrice;
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

	public void setHlc3(double hlc3) {
		this.hlc3 = hlc3;
	}
	
	public void setZlema34(double zlema34) {
		this.zlema34 = zlema34;
	}
	
	public void setSmmaHp34(double smmaHp34) {
		this.smmaHp34 = smmaHp34;
	}
	
	public void setSmmaLp34(double smmaLp34) {
		this.smmaLp34 = smmaLp34;
	}
	
	public void setMacd(double macd) {
		this.macd = macd;
	}

	public void setMacdSignal(double macdSignal) {
		this.macdSignal = macdSignal;
	}

	public void setImacd(double imacd) {
		this.imacd = imacd;
	}

	public void setImacdSignal(double imacdSignal) {
		this.imacdSignal = imacdSignal;
	}

	public void setEma10(double ema10) {
		this.ema10 = ema10;
	}

	public void setEma20(double ema20) {
		this.ema20 = ema20;
	}

	public void setEma30(double ema30) {
		this.ema30 = ema30;
	}

	public void setEma50(double ema50) {
		this.ema50 = ema50;
	}

	public void setRsi(double rsi) {
		this.rsi = rsi;
	}

	public void setStochasticrsi(double stochasticrsi) {
		this.stochasticrsi = stochasticrsi;
	}

	public void setAtr(double atr) {
		this.atr = atr;
	}

	public void setAtrTs(double atrTs) {
		this.atrTs = atrTs;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InstrumentIndicators [tickTime=");
		builder.append(tickTime);
		builder.append(", token=");
		builder.append(token);
		builder.append(", name=");
		builder.append(name);
		builder.append(", openPrice=");
		builder.append(openPrice);
		builder.append(", highPrice=");
		builder.append(highPrice);
		builder.append(", lowPrice=");
		builder.append(lowPrice);
		builder.append(", closePrice=");
		builder.append(closePrice);
		builder.append(", haOpenPrice=");
		builder.append(haOpenPrice);
		builder.append(", haHighPrice=");
		builder.append(haHighPrice);
		builder.append(", haLowPrice=");
		builder.append(haLowPrice);
		builder.append(", haClosePrice=");
		builder.append(haClosePrice);
		builder.append(", lastTradedPrice=");
		builder.append(lastTradedPrice);
		builder.append(", volumeTraded=");
		builder.append(volumeTraded);
		builder.append(", totalBuyQuantity=");
		builder.append(totalBuyQuantity);
		builder.append(", totalSellQuantity=");
		builder.append(totalSellQuantity);
		builder.append(", hlc3=");
		builder.append(hlc3);
		builder.append(", zlema34=");
		builder.append(zlema34);
		builder.append(", smmaHp34=");
		builder.append(smmaHp34);
		builder.append(", smmaLp34=");
		builder.append(smmaLp34);
		builder.append(", macd=");
		builder.append(macd);
		builder.append(", macdSignal=");
		builder.append(macdSignal);
		builder.append(", imacd=");
		builder.append(imacd);
		builder.append(", imacdSignal=");
		builder.append(imacdSignal);
		builder.append(", ema10=");
		builder.append(ema10);
		builder.append(", ema20=");
		builder.append(ema20);
		builder.append(", ema30=");
		builder.append(ema30);
		builder.append(", ema50=");
		builder.append(ema50);
		builder.append(", rsi=");
		builder.append(rsi);
		builder.append(", stochasticrsi=");
		builder.append(stochasticrsi);
		builder.append(", atr=");
		builder.append(atr);
		builder.append(", atrTs=");
		builder.append(atrTs);
		builder.append("]");
		return builder.toString();
	}

}

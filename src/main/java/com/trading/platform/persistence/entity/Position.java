package com.trading.platform.persistence.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.trading.platform.persistence.entity.key.PositionCompositeId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * CREATE TABLE IF NOT EXISTS POSITION (
 * TRADE_ID TEXT NOT NULL,
 * TID INTEGER,
 * NO_OF_LOTS INTEGER NOT NULL CHECK (NO_OF_LOTS > 0),
 * QUANTITY INTEGER NOT NULL CHECK (QUANTITY > 0),
 * SOLD_QUANTITY INTEGER NOT NULL DEFAULT 0,
 * ENTRY_TIME TIMESTAMP,
 * EXIT_TIME TIMESTAMP,
 * ENTRY_PRICE DECIMAL,
 * EXIT_PRICE DECIMAL,
 * OPTION_ENTRY_LTP DECIMAL,
 * OPTION_EXIT_LTP DECIMAL,
 * OPTION_ENTRY_PRICE DECIMAL,
 * OPTION_EXIT_PRICE DECIMAL,
 * UNREALIZED_PROFIT DECIMAL,
 * UNREALIZED_LOSS DECIMAL,
 * PROFIT DECIMAL,
 * ACTIVE BOOLEAN,
 * CLOSED BOOLEAN,
 * SQUARE_OFF BOOLEAN,
 * STOP_LOSS BOOLEAN,
 * SL_PRICE DECIMAL,
 * SL_TRAIL JSON,
 * PRIMARY KEY (TRADE_ID, TID)
 * );
 */

@Entity
@Table(name = "POSITION")
@IdClass(PositionCompositeId.class)
public class Position implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TRADE_ID")
	private String tradeId;

	@Id
	@Column(name = "TID")
	private int targetId;

	@Column(name = "NO_OF_LOTS")
	private int numOfLots;

	@Column(name = "QUANTITY")
	private int quantity;

	@Column(name = "SOLD_QUANTITY")
	private int soldQuantity;

	@Column(name = "ENTRY_TIME")
	private Date entryTime;

	@Column(name = "EXIT_TIME")
	private Date exitTime;

	@Column(name = "ENTRY_PRICE")
	private double entryPrice;

	@Column(name = "EXIT_PRICE")
	private double exitPrice;

	@Column(name = "OPTION_ENTRY_LTP")
	private double optionEntryLtp;

	@Column(name = "OPTION_EXIT_LTP")
	private double optionExitLtp;

	@Column(name = "OPTION_ENTRY_PRICE")
	private double optionEntryPrice;

	@Column(name = "OPTION_EXIT_PRICE")
	private double optionExitPrice;

	@Column(name = "UNREALIZED_PROFIT")
	private double unrealizedProfit;

	@Column(name = "UNREALIZED_LOSS")
	private double unrealizedLoss;

	@Column(name = "PROFIT")
	private double profit;

	@Column(name = "ACTIVE")
	private boolean isActive;

	@Column(name = "CLOSED")
	private boolean isClosed;

	@Column(name = "STOP_LOSS")
	private boolean isStopLoss;

	@Column(name = "SQUARE_OFF")
	private boolean isSquareOff;

	@Column(name = "SL_PRICE")
	private double slPrice;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "SL_TRAIL")
	private List<Double> slTrail;

	public String getTradeId() {
		return tradeId;
	}

	public int getTargetId() {
		return targetId;
	}

	public int getNumOfLots() {
		return numOfLots;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getSoldQuantity() {
		return soldQuantity;
	}

	public Date getEntryTime() {
		return entryTime;
	}

	public Date getExitTime() {
		return exitTime;
	}

	public double getEntryPrice() {
		return entryPrice;
	}

	public double getExitPrice() {
		return exitPrice;
	}

	public double getOptionEntryLtp() {
		return optionEntryLtp;
	}

	public double getOptionExitLtp() {
		return optionExitLtp;
	}

	public double getOptionEntryPrice() {
		return optionEntryPrice;
	}

	public double getOptionExitPrice() {
		return optionExitPrice;
	}

	public double getUnrealizedProfit() {
		return unrealizedProfit;
	}

	public double getUnrealizedLoss() {
		return unrealizedLoss;
	}

	public double getProfit() {
		return profit;
	}

	public boolean isActive() {
		return isActive;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public boolean isStopLoss() {
		return isStopLoss;
	}

	public boolean isSquareOff() {
		return isSquareOff;
	}

	public double getSlPrice() {
		return slPrice;
	}

	public List<Double> getSlTrail() {
		return slTrail;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public void setNumOfLots(int numOfLots) {
		this.numOfLots = numOfLots;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setSoldQuantity(int soldQuantity) {
		this.soldQuantity = soldQuantity;
	}

	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
	}

	public void setExitTime(Date exitTime) {
		this.exitTime = exitTime;
	}

	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
	}

	public void setOptionEntryLtp(double optionEntryLtp) {
		this.optionEntryLtp = optionEntryLtp;
	}

	public void setOptionExitLtp(double optionExitLtp) {
		this.optionExitLtp = optionExitLtp;
	}

	public void setOptionEntryPrice(double optionEntryPrice) {
		this.optionEntryPrice = optionEntryPrice;
	}

	public void setOptionExitPrice(double optionExitPrice) {
		this.optionExitPrice = optionExitPrice;
	}

	public void setUnrealizedProfit(double unrealizedProfit) {
		this.unrealizedProfit = unrealizedProfit;
	}

	public void setUnrealizedLoss(double unrealizedLoss) {
		this.unrealizedLoss = unrealizedLoss;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public void setStopLoss(boolean isStopLoss) {
		this.isStopLoss = isStopLoss;
	}

	public void setSquareOff(boolean isSquareOff) {
		this.isSquareOff = isSquareOff;
	}

	public void setSlPrice(double slPrice) {
		this.slPrice = slPrice;
	}

	public void setSlTrail(List<Double> slTrail) {
		this.slTrail = slTrail;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Position [tradeId=");
		builder.append(tradeId);
		builder.append(", targetId=");
		builder.append(targetId);
		builder.append(", numOfLots=");
		builder.append(numOfLots);
		builder.append(", quantity=");
		builder.append(quantity);
		builder.append(", soldQuantity=");
		builder.append(soldQuantity);
		builder.append(", entryTime=");
		builder.append(entryTime);
		builder.append(", exitTime=");
		builder.append(exitTime);
		builder.append(", entryPrice=");
		builder.append(entryPrice);
		builder.append(", exitPrice=");
		builder.append(exitPrice);
		builder.append(", optionEntryLtp=");
		builder.append(optionEntryLtp);
		builder.append(", optionExitLtp=");
		builder.append(optionExitLtp);
		builder.append(", optionEntryPrice=");
		builder.append(optionEntryPrice);
		builder.append(", optionExitPrice=");
		builder.append(optionExitPrice);
		builder.append(", unrealizedProfit=");
		builder.append(unrealizedProfit);
		builder.append(", unrealizedLoss=");
		builder.append(unrealizedLoss);
		builder.append(", profit=");
		builder.append(profit);
		builder.append(", isActive=");
		builder.append(isActive);
		builder.append(", isClosed=");
		builder.append(isClosed);
		builder.append(", isStopLoss=");
		builder.append(isStopLoss);
		builder.append(", isSquareOff=");
		builder.append(isSquareOff);
		builder.append(", slPrice=");
		builder.append(slPrice);
		builder.append(", slTrail=");
		builder.append(slTrail);
		builder.append("]");
		return builder.toString();
	}

}

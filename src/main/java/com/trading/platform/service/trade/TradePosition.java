package com.trading.platform.service.trade;

import java.util.Date;

public class TradePosition {

	private Date entryTime;

	private Date exitTime;

	private double entryPrice;

	private double exitPrice;

	private double optionEntryLtp;

	private double optionExitLtp;

	private double optionEntryPrice;

	private double optionExitPrice;

	private double unrealizedProfit;

	private double unrealizedLoss;

	private double profit;

	private boolean isActive;

	private boolean isClosed;

	private boolean isStopLoss;

	private boolean isSquareOff;

	public TradePosition() {
		this.isActive = false;
		this.isClosed = false;
		this.isStopLoss = false;
		this.isSquareOff = false;
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

	public void setEntryTime(Date entryTime) {
		this.entryTime = entryTime;
	}

	public void setExitTime(Date exitTime) {
		this.exitTime = exitTime;
	}

	public void setEntryPrice(double entryPrice) {
		this.entryPrice = entryPrice;
		setActive(true);
		setClosed(false);
	}

	public void setExitPrice(double exitPrice) {
		this.exitPrice = exitPrice;
		setActive(false);
		setClosed(true);
	}

	public void setOptionEntryLtp(double optionEntryLtp) {
		this.optionEntryLtp = optionEntryLtp;
	}

	public void setOptionExitLtp(double optionExitLtp) {
		this.optionExitLtp = optionExitLtp;
	}

	public void setOptionEntryPrice(double optionEntryPrice) {
		this.optionEntryPrice = optionEntryPrice;
		setActive(true);
		setClosed(false);
	}

	public void setOptionExitPrice(double optionExitPrice) {
		this.optionExitPrice = optionExitPrice;
		setActive(false);
		setClosed(true);
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradePosition [entryTime=");
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
		builder.append("]");
		return builder.toString();
	}

}

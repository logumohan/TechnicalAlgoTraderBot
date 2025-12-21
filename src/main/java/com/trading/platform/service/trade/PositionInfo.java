package com.trading.platform.service.trade;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;

public class PositionInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tradeId;

	private int targetId;

	private int numOfLots;

	private int quantity;

	private int soldQuantity;

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

	private double slPrice;

	private List<Double> slTrail;

	public PositionInfo(String tradeId) {
		this.tradeId = tradeId;
		this.slTrail = new LinkedList<>();
		this.isActive = false;
		this.isClosed = false;
		this.isStopLoss = false;
		this.isSquareOff = false;
	}
	
	public PositionInfo(String tradeId, Target target, Job job) {
		this.tradeId = tradeId;
		this.targetId = target.getTargetId();
		this.numOfLots = target.getNumOfLots();
		this.quantity = job.getLotSize() * target.getNumOfLots();
		this.slTrail = new LinkedList<>();
		this.isActive = false;
		this.isClosed = false;
		this.isStopLoss = false;
		this.isSquareOff = false;
	}

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

	public void setSlPrice(double slPrice) {
		this.slPrice = slPrice;
	}

	public void setSlTrail(List<Double> slTrail) {
		this.slTrail = slTrail;
	}

	public void setTrailingStopLossPrice(double slTrailPrice) {
		if (this.slPrice >= slTrailPrice) {
			return;
		}

		if (slTrail.isEmpty()) {
			this.slTrail.add(slTrailPrice);
			this.slPrice = slTrailPrice;
		} else {
			double lastStopLossPrice = slTrail.get(slTrail.size() - 1);
			if (lastStopLossPrice < slTrailPrice) {
				this.slTrail.add(slTrailPrice);
				this.slPrice = slTrailPrice;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TargetPosition [tradeId=");
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

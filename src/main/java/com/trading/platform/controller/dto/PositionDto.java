package com.trading.platform.controller.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.platform.persistence.entity.Position;
import com.trading.platform.service.trade.PositionInfo;

public class PositionDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("trade-id")
	private String tradeId;

	@JsonProperty("target-id")
	private int targetId;

	@JsonProperty("lots")
	private int numOfLots;

	@JsonProperty("quantity")
	private int quantity;

	@JsonProperty("sold-quantity")
	private int soldQuantity;

	@JsonProperty("entry-time")
	private Date entryTime;

	@JsonProperty("exit-time")
	private Date exitTime;

	@JsonProperty("entry-price")
	private double entryPrice;

	@JsonProperty("exit-price")
	private double exitPrice;

	@JsonProperty("option-entry-ltp")
	private double optionEntryLtp;

	@JsonProperty("option-exit-ltp")
	private double optionExitLtp;

	@JsonProperty("option-entry-price")
	private double optionEntryPrice;

	@JsonProperty("option-exit-price")
	private double optionExitPrice;

	@JsonProperty("unrealized-profit")
	private double unrealizedProfit;

	@JsonProperty("unrealized-loss")
	private double unrealizedLoss;

	@JsonProperty("profit")
	private double profit;

	@JsonProperty("active")
	private boolean isActive;

	@JsonProperty("closed")
	private boolean isClosed;

	@JsonProperty("stop-loss")
	private boolean isStopLoss;

	@JsonProperty("square-off")
	private boolean isSquareOff;

	@JsonProperty("sl-price")
	private double slPrice;

	@JsonProperty("sl-trail")
	private List<Double> slTrail;

	public static PositionDto of(Position position) {
		PositionDto positionDto = new PositionDto();
		positionDto.setTradeId(position.getTradeId());
		positionDto.setTargetId(position.getTargetId());
		positionDto.setNumOfLots(position.getNumOfLots());
		positionDto.setQuantity(position.getQuantity());
		positionDto.setSoldQuantity(position.getSoldQuantity());
		positionDto.setEntryTime(position.getEntryTime());
		positionDto.setExitTime(position.getExitTime());
		positionDto.setEntryPrice(position.getEntryPrice());
		positionDto.setExitPrice(position.getExitPrice());
		positionDto.setOptionEntryLtp(position.getOptionEntryLtp());
		positionDto.setOptionExitLtp(position.getOptionExitLtp());
		positionDto.setOptionEntryPrice(position.getOptionEntryPrice());
		positionDto.setOptionExitPrice(position.getOptionExitPrice());
		positionDto.setUnrealizedProfit(position.getUnrealizedProfit());
		positionDto.setUnrealizedLoss(position.getUnrealizedLoss());
		positionDto.setProfit(position.getProfit());
		positionDto.setActive(position.isActive());
		positionDto.setClosed(position.isClosed());
		positionDto.setStopLoss(position.isStopLoss());
		positionDto.setSquareOff(position.isSquareOff());
		positionDto.setSlPrice(position.getSlPrice());
		positionDto.setSlTrail(position.getSlTrail());

		return positionDto;
	}
	
	public static PositionDto of(PositionInfo positionInfo) {
		PositionDto positionDto = new PositionDto();
		positionDto.setTradeId(positionInfo.getTradeId());
		positionDto.setTargetId(positionInfo.getTargetId());
		positionDto.setNumOfLots(positionInfo.getNumOfLots());
		positionDto.setQuantity(positionInfo.getQuantity());
		positionDto.setSoldQuantity(positionInfo.getSoldQuantity());
		positionDto.setEntryTime(positionInfo.getEntryTime());
		positionDto.setExitTime(positionInfo.getExitTime());
		positionDto.setEntryPrice(positionInfo.getEntryPrice());
		positionDto.setExitPrice(positionInfo.getExitPrice());
		positionDto.setOptionEntryLtp(positionInfo.getOptionEntryLtp());
		positionDto.setOptionExitLtp(positionInfo.getOptionExitLtp());
		positionDto.setOptionEntryPrice(positionInfo.getOptionEntryPrice());
		positionDto.setOptionExitPrice(positionInfo.getOptionExitPrice());
		positionDto.setUnrealizedProfit(positionInfo.getUnrealizedProfit());
		positionDto.setUnrealizedLoss(positionInfo.getUnrealizedLoss());
		positionDto.setProfit(positionInfo.getProfit());
		positionDto.setActive(positionInfo.isActive());
		positionDto.setClosed(positionInfo.isClosed());
		positionDto.setStopLoss(positionInfo.isStopLoss());
		positionDto.setSquareOff(positionInfo.isSquareOff());
		positionDto.setSlPrice(positionInfo.getSlPrice());
		positionDto.setSlTrail(positionInfo.getSlTrail());

		return positionDto;
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

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss SSS", timezone = "IST")
	public Date getEntryTime() {
		return entryTime;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss SSS", timezone = "IST")
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
		builder.append("PositionDto [tradeId=");
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

package com.trading.platform.persistence.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Target implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("tid")
	private int targetId;

	@JsonProperty("lots")
	private int numOfLots;

	@JsonProperty("profit")
	private int targetProfit;

	@JsonProperty("tsl")
	private int trailingStopLoss;

	@JsonProperty("tp")
	private int takeProfit;

	@JsonProperty("tsl-after-tp")
	private int trailingStopLossAfterProfitHit;

	public int getTargetId() {
		return targetId;
	}

	public int getNumOfLots() {
		return numOfLots;
	}

	public int getTargetProfit() {
		return targetProfit;
	}

	public int getTrailingStopLoss() {
		return trailingStopLoss;
	}

	public int getTakeProfit() {
		return takeProfit;
	}

	public int getTrailingStopLossAfterProfitHit() {
		return trailingStopLossAfterProfitHit;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public void setNumOfLots(int numOfLots) {
		this.numOfLots = numOfLots;
	}

	public void setTargetProfit(int targetProfit) {
		this.targetProfit = targetProfit;
	}

	public void setTrailingStopLoss(int trailingStopLoss) {
		this.trailingStopLoss = trailingStopLoss;
	}

	public void setTakeProfit(int takeProfit) {
		this.takeProfit = takeProfit;
	}

	public void setTrailingStopLossAfterProfitHit(int trailingStopLossAfterProfitHit) {
		this.trailingStopLossAfterProfitHit = trailingStopLossAfterProfitHit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Target [targetId=");
		builder.append(targetId);
		builder.append(", numOfLots=");
		builder.append(numOfLots);
		builder.append(", targetProfit=");
		builder.append(targetProfit);
		builder.append(", trailingStopLoss=");
		builder.append(trailingStopLoss);
		builder.append(", takeProfit=");
		builder.append(takeProfit);
		builder.append(", trailingStopLossAfterProfitHit=");
		builder.append(trailingStopLossAfterProfitHit);
		builder.append("]");
		return builder.toString();
	}

}

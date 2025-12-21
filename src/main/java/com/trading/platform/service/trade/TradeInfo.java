package com.trading.platform.service.trade;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import com.trading.platform.persistence.entity.Signal;
import com.zerodhatech.models.Order;

public class TradeInfo {

	private String tradeId;

	private String userName;

	private String jobName;

	private boolean isLive;

	private boolean isActive;

	private int lotSize;

	private Order order;

	private Order slOrder;

	private Signal signal;

	private List<PositionInfo> positionInfoList;

	public TradeInfo(Signal signal, String userName, String jobName, boolean isLive) {
		this.signal = signal;
		this.userName = userName;
		this.jobName = jobName;
		this.isLive = isLive;
		this.isActive = true;
		this.positionInfoList = new LinkedList<>();

		updateTradeId();
	}

	public String getTradeId() {
		return tradeId;
	}

	public String getUserName() {
		return userName;
	}

	public String getJobName() {
		return jobName;
	}

	public boolean isLive() {
		return isLive;
	}

	public boolean isActive() {
		return isActive;
	}

	public int getLotSize() {
		return lotSize;
	}

	public Order getOrder() {
		return order;
	}

	public Signal getSignal() {
		return signal;
	}

	public Order getSLOrder() {
		return slOrder;
	}

	public List<PositionInfo> getPositionInfoList() {
		return positionInfoList;
	}

	public void updateTradeId() {
		SimpleDateFormat format = new SimpleDateFormat("ddMM-HHmm");
		StringBuilder builder = new StringBuilder();
		builder.append(isLive() ? "L" : "P").append("-");
		builder.append(format.format(getSignal().getTickTime())).append("-");
		builder.append(getSignal().getAggregationType()).append("-");
		builder.append(getUserName().replace("_", "-")).append("-");
		builder.append(getJobName().replace("_", "-")).append("-");
		builder.append(getSignal().getOptionSymbol());

		this.tradeId = builder.toString().toUpperCase();
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setLotSize(int lotSize) {
		this.lotSize = lotSize;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public void setSLOrder(Order slOrder) {
		this.slOrder = slOrder;
	}

	public void setSignal(Signal signal) {
		this.signal = signal;
	}

	public void setPositionInfoList(List<PositionInfo> positionInfoList) {
		this.positionInfoList = positionInfoList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradeInfo [tradeId=");
		builder.append(tradeId);
		builder.append(", jobName=");
		builder.append(jobName);
		builder.append(", isLive=");
		builder.append(isLive);
		builder.append(", isActive=");
		builder.append(isActive);
		builder.append(", lotSize=");
		builder.append(lotSize);
		builder.append(", order=");
		builder.append(order);
		builder.append(", slOrder=");
		builder.append(slOrder);
		builder.append(", signal=");
		builder.append(signal);
		builder.append(", targetPositionInfoList=");
		builder.append(positionInfoList);
		builder.append("]");
		return builder.toString();
	}

}

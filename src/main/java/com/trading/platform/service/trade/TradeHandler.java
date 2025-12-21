package com.trading.platform.service.trade;

import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.zerodhatech.models.Order;

public interface TradeHandler extends TrailingSLHandler, Runnable {

	public TradeManager getTradeManager();

	public Job getJob();

	public TradeInfo getTradeInfo();

	public void setTradeInfo(TradeInfo tradeInfo);

	public boolean isLive();

	public double getStopLossPrice(PositionInfo positionInfo);

	public Order placeMarketBuyOrder();

	public Order placeMarketSellOrder();

	public void waitTillOrderExecution(double optionEntryLtp);

	public void markEntry(PositionInfo positionInfo, double entryPrice, double optionEntryLtp, double optionEntryPrice);

	public void markExit(PositionInfo positionInfo, double exitPrice, double optionExitLtp, double optionExitPrice);

	public boolean isStopLossHit(PositionInfo positionInfo);

	public Target getTarget(int targetId);

	public int getQuantity();

	public int getQuantity(PositionInfo positionInfo);

	public int getSoldQuantity();

	public int getSoldQuantity(PositionInfo positionInfo);

	public void squareOff();

	public void setSquareOff();

	public boolean isSquareOff();

	public void doTrade();

	public double getUnRealizedProfitPoints(PositionInfo positionInfo);

}

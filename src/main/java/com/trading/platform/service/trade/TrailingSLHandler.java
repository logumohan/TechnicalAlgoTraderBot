package com.trading.platform.service.trade;

public interface TrailingSLHandler {

	public double getTrailingStopLossPrice(PositionInfo positionInfo);

	public void handleTrailingStopLoss(PositionInfo positionInfo);

}

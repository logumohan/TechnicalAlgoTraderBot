package com.trading.platform.trialing.strategies;

import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;

public class SimpleTrailingStrategy implements TrailingStrategy {

	@Override
	public double getTrailingStopLossPrice(PositionInfo positionInfo, Target target, TradeInfo tradeInfo, Job job,
			double lastTradedPrice) {
		return Math.round(lastTradedPrice - target.getTrailingStopLoss());
	}

}

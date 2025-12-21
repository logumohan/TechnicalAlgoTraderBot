package com.trading.platform.trialing.strategies;

import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;

public class ProtectiveTrailingStrategy implements TrailingStrategy {

	@Override
	public double getTrailingStopLossPrice(PositionInfo positionInfo, Target target, TradeInfo tradeInfo, Job job,
			double lastTradedPrice) {
		int fixedTsl = target.getTrailingStopLoss();
		int halfTsl = fixedTsl / 2;
		double slPrice = Math.max(positionInfo.getOptionEntryPrice() - halfTsl, lastTradedPrice - fixedTsl);

		return Math.round(slPrice);
	}

}

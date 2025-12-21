package com.trading.platform.trialing.strategies;

import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;

public class AggressiveTrailingStrategy implements TrailingStrategy {

	@Override
	public double getTrailingStopLossPrice(PositionInfo positionInfo, Target target, TradeInfo tradeInfo, Job job,
			double lastTradedPrice) {
		double profitPoints = lastTradedPrice - positionInfo.getOptionEntryPrice();
		int fixedTsl = target.getTrailingStopLoss();
		int halfTsl = fixedTsl / 2;
		double slPrice = 0;
		if (profitPoints >= (1.5 * fixedTsl)) {
			// Protects one TSL points
			slPrice = Math.max(positionInfo.getOptionEntryPrice() + fixedTsl,
					lastTradedPrice - fixedTsl);
		} else if (profitPoints >= fixedTsl) {
			// Protects half TSL points
			slPrice = Math.max(positionInfo.getOptionEntryPrice() + halfTsl,
					lastTradedPrice - halfTsl);
		} else if (profitPoints >= halfTsl) {
			// Protects capital
			slPrice = Math.max(positionInfo.getOptionEntryPrice(),
					lastTradedPrice - halfTsl);
		} else {
			slPrice = lastTradedPrice - fixedTsl;
		}

		return Math.round(slPrice);
	}

}

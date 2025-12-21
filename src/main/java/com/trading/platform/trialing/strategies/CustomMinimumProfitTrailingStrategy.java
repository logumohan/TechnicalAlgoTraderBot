package com.trading.platform.trialing.strategies;

import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;
import com.trading.platform.service.trade.TradeJobType;

public class CustomMinimumProfitTrailingStrategy implements TrailingStrategy {

	@Override
	public double getTrailingStopLossPrice(PositionInfo positionInfo, Target target, TradeInfo tradeInfo, Job job,
			double lastTradedPrice) {
		double profitPoints = lastTradedPrice - positionInfo.getOptionEntryPrice();
		double unrealizedProfitPerLot = profitPoints * tradeInfo.getLotSize();
		int fixedTsl = target.getTrailingStopLoss();
		int halfTsl = fixedTsl / 2;
		double slPrice = 0;

		if (job.getJobType().equals(TradeJobType.FIXED_PROFIT.getType()) &&
				unrealizedProfitPerLot >= target.getTargetProfit()) {
			// Profit per lot is achieved, trailing with 10 points below current price
			slPrice = lastTradedPrice - 10;
		} else if (job.getJobType().equals(TradeJobType.FIXED_TSL.getType()) &&
				unrealizedProfitPerLot >= target.getTrailingStopLoss()) {
			// Points per lot is achieved, trailing with 10 points below current price
			slPrice = lastTradedPrice - 10;
		} else if (profitPoints >= halfTsl) {
			// Protects capital
			slPrice = Math.max(positionInfo.getOptionEntryPrice(),
					lastTradedPrice - fixedTsl);
		} else {
			slPrice = lastTradedPrice - halfTsl;
		}

		return Math.round(slPrice);
	}

}

package com.trading.platform.trialing.strategies;

import java.util.Optional;

import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;

public class DefaultMultiTargetTrailingStrategy implements MultiTargetTrailingStrategy {

	@Override
	public double getTrailingStopLossPriceForTarget(PositionInfo positionInfo, TradeInfo tradeInfo, Job job,
			Target target, double lastTradedPrice) {
		// Trailing with initial TSL points till SL reaches buy price
		double slPrice = Math.max(positionInfo.getOptionEntryPrice() - target.getTrailingStopLoss(),
				lastTradedPrice - target.getTrailingStopLoss());
		double targetPrice = Math.round(positionInfo.getOptionEntryPrice() +
				((double) target.getTargetProfit() / tradeInfo.getLotSize()));
		if (lastTradedPrice >= targetPrice) {
			slPrice = Math.max(slPrice, lastTradedPrice - target.getTrailingStopLossAfterProfitHit());
		} else if (lastTradedPrice < positionInfo.getEntryPrice() + target.getTakeProfit()) {
			slPrice = Math.max(slPrice, lastTradedPrice - target.getTrailingStopLoss());
		} else {
			if (target.getTargetId() == 1) {
				slPrice = Math.max(slPrice, lastTradedPrice - target.getTakeProfit());
			} else {
				Optional<Target> prevTarget = job.getTargets().stream()
						.filter((Target t) -> t.getTargetId() == (target.getTargetId() - 1)).findFirst();
				if (prevTarget.isPresent()) {
					double prevTargetPrice = Math.round(positionInfo.getOptionEntryPrice() +
							((double) prevTarget.get().getTargetProfit() / tradeInfo.getLotSize()));
					if (lastTradedPrice > prevTargetPrice) {
						slPrice = Math.max(prevTargetPrice, lastTradedPrice - target.getTakeProfit());
					} else {
						slPrice = Math.max(slPrice, lastTradedPrice - target.getTakeProfit());
					}
				} else {
					slPrice = Math.max(slPrice, lastTradedPrice - target.getTakeProfit());
				}
			}
		}

		return Math.round(slPrice);
	}

}

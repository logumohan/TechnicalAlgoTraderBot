package com.trading.platform.trialing.strategies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;

public class DefaultTrailingStrategy implements TrailingStrategy {

	private static final Logger LOGGER = LogManager.getLogger(DefaultTrailingStrategy.class);

	@Override
	public double getTrailingStopLossPrice(PositionInfo positionInfo, Target target, TradeInfo tradeInfo, Job job,
			double lastTradedPrice) {
		double profitPoints = lastTradedPrice - positionInfo.getOptionEntryPrice();
		int fixedTsl = target.getTrailingStopLoss();
		int halfTsl = fixedTsl / 2;
		double slPrice = 0;
		if (profitPoints >= target.getTrailingStopLoss()) {
			LOGGER.info("Unrealized profit points is greater than the fixed TSL points, " +
					"bringing the stop loss just 10 points less than LTP, unrealized profit points - {}",
					profitPoints);
			slPrice = lastTradedPrice - 10;
		} else if (profitPoints >= fixedTsl) {
			// Protect unrealized minimum profit, half TSL points less than LTP
			slPrice = Math.max(positionInfo.getOptionEntryPrice() + halfTsl, lastTradedPrice - halfTsl);
		} else if (profitPoints >= halfTsl) {
			// Prevent loss. Max (entry price or half TSL points less than LTP)
			slPrice = Math.max(positionInfo.getOptionEntryPrice(), lastTradedPrice - halfTsl);
		} else {
			slPrice = lastTradedPrice - fixedTsl;
		}
		return Math.round(slPrice);
	}

}

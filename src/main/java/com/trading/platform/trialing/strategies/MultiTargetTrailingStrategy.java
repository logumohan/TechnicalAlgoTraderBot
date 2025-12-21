package com.trading.platform.trialing.strategies;

import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;

public interface MultiTargetTrailingStrategy {

	double getTrailingStopLossPriceForTarget(PositionInfo positionInfo, TradeInfo tradeInfo, Job job, Target target,
			double lastTradedPrice);

}

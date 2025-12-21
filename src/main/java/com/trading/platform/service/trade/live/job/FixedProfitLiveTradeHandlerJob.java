package com.trading.platform.service.trade.live.job;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;
import com.trading.platform.service.trade.TradeJobPersistence;
import com.trading.platform.service.trade.TradeManager;

public class FixedProfitLiveTradeHandlerJob extends FixedTSLLiveTradeHandlerJob {

	public FixedProfitLiveTradeHandlerJob(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
	}

	@Override
	public boolean isStopLossHit(PositionInfo positionInfo) {
		if (isTrailingStopLossHit(positionInfo)) {
			LOGGER.info("Trailing stop loss hit, closing the poistion immediately");
			return true;
		} else {
			double profitPerLot = getUnRealizedProfitPoints(positionInfo) * getTradeInfo().getLotSize();
			double targetProfit = getTarget(positionInfo.getTargetId()).getTargetProfit();
			if (profitPerLot >= targetProfit) {
				LOGGER.info("A profit of {} per lot is acheived, position will not be closed immediately, " +
						"allows further trailing with 10 points", targetProfit);
				return false;
			}
		}

		return false;
	}

}

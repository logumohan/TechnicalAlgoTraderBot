package com.trading.platform.service.trade.paper.job;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.signal.OptionType;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;
import com.trading.platform.service.trade.TradeJobPersistence;
import com.trading.platform.service.trade.TradeManager;
import com.trading.platform.service.trade.paper.AbstractATRTSLPaperTradeHandler;

public class ATRTSLPaperTradeHandlerJob extends AbstractATRTSLPaperTradeHandler {

	public ATRTSLPaperTradeHandlerJob(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
	}

	@Override
	public double getStopLossPrice(PositionInfo positionInfo) {
		if (getTradeInfo().getSignal().getTradeSignal().endsWith(OptionType.BUY_CE.name())) {
			return positionInfo.getEntryPrice() - (getAverageTrueRange() * getJob().getAtrMultiplier());
		} else {
			return positionInfo.getEntryPrice() + (getAverageTrueRange() * getJob().getAtrMultiplier());
		}
	}

}

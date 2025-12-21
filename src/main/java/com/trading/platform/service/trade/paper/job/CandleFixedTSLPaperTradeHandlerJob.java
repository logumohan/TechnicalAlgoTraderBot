package com.trading.platform.service.trade.paper.job;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.trade.TradeInfo;
import com.trading.platform.service.trade.TradeJobPersistence;
import com.trading.platform.service.trade.TradeManager;

public class CandleFixedTSLPaperTradeHandlerJob extends FixedTSLPaperTradeHandlerJob {

	public CandleFixedTSLPaperTradeHandlerJob(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
	}

	@Override
	protected double getLastTradedPrice() {
		return getIndicator() != null ? getIndicator().getLastTradedPrice() : super.getLastTradedPrice();
	}

}

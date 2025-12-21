package com.trading.platform.service.trade;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trading.platform.persistence.SubscriptionReadOnlyRepositoryIf;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.trade.live.job.ATRTSLLiveTradeHandlerJob;
import com.trading.platform.service.trade.live.job.FixedProfitLiveTradeHandlerJob;
import com.trading.platform.service.trade.live.job.FixedTSLLiveTradeHandlerJob;
import com.trading.platform.service.trade.live.job.MultiTargetFPLiveTradeHandlerJob;
import com.trading.platform.service.trade.paper.job.ATRTSLPaperTradeHandlerJob;
import com.trading.platform.service.trade.paper.job.FixedProfitPaperTradeHandlerJob;
import com.trading.platform.service.trade.paper.job.FixedTSLPaperTradeHandlerJob;
import com.trading.platform.service.trade.paper.job.MultiTargetFPPaperTraderHandlerJob;

@Component
public class TradeHandlerJobFactory {

	private static final Logger LOGGER = LogManager.getLogger(TradeHandlerJobFactory.class);

	@Autowired
	private KiteLoginModuleImpl kiteModule;

	@Autowired
	private TradeJobPersistence persistence;

	@Autowired
	private SubscriptionReadOnlyRepositoryIf subscriptionRepository;

	public TradeHandler createLiveTradeHandlerJob(Job job, TradeManager tradeManager, Signal signal,
			InstrumentSubscription subscription, InstrumentIndicators instrumentIndicators) throws TradeException {
		LOGGER.info("createLiveTradeHandlerJob: Attempting to create a trade job - {} for the user - {}",
				job.getName(), job.getUserName());
		KiteSessionService session = kiteModule.getKiteSessions().get(job.getUserName());
		if (session == null || !session.isLoggedIn()) {
			LOGGER.error("createLiveTradeHandlerJob: Kite session does not exist for user - {}, skipping job - {}",
					job.getUserName(), job.getName());
			throw new TradeException("Kite session for " + job.getUserName() + " does not exists!");
		}

		if (persistence.getProfitByUser(session.getUserAccount()) >= session.getUserAccount().getMaxProfitPerDay()) {
			LOGGER.info("createLiveTradeHandlerJob: Profit for {} exceeds {}, no more trades allowed",
					session.getUserAccount().getUserName(), session.getUserAccount().getMaxProfitPerDay());
			throw new TradeException("Profit exceeds for " + session.getUserAccount().getUserName());
		}

		TradeInfo tradeInfo = new TradeInfo(signal, session.getUserAccount().getUserName(), job.getName(), true);
		if (subscription == null) {
			subscription = subscriptionRepository.getByToken(tradeInfo.getSignal().getToken());
		}
		tradeInfo.setLotSize(subscription.getLotSize());
		for (Target target : job.getTargets()) {
			tradeInfo.getPositionInfoList().add(new PositionInfo(tradeInfo.getTradeId(), target, job));
		}
		switch (job.getJobType()) {
		case "ATR21_TSL":
			return new ATRTSLLiveTradeHandlerJob(job, tradeInfo, tradeManager, kiteModule, session, persistence,
					instrumentIndicators, subscription);
		case "FIXED_TSL":
			return new FixedTSLLiveTradeHandlerJob(job, tradeInfo, tradeManager, kiteModule, session, persistence,
					instrumentIndicators, subscription);
		case "FIXED_PROFIT":
			return new FixedProfitLiveTradeHandlerJob(job, tradeInfo, tradeManager, kiteModule, session,
					persistence, instrumentIndicators, subscription);
		case "MULTI_TARGET":
			return new MultiTargetFPLiveTradeHandlerJob(job, tradeInfo, tradeManager, kiteModule, session,
					persistence, instrumentIndicators, subscription);
		default:
			LOGGER.error("createLiveTradeHandlerJob: {} job is not supported for paper trading", job.getName());
			throw new TradeException(job.getName() + " job is not supported for paper trading");
		}
	}

	public TradeHandler createPaperTradeHandlerJob(Job job, TradeManager tradeManager, Signal signal,
			InstrumentSubscription subscription, InstrumentIndicators indicator) throws TradeException {
		KiteSessionService session = kiteModule.getKiteSessions().get(job.getUserName());
		LOGGER.info("createPaperTradeHandlerJob: Attempting to create a trade job - {} for the user - {}",
				job.getName(), job.getUserName());
		if (session == null || !session.isLoggedIn()) {
			LOGGER.error("createPaperTradeHandlerJob: Kite session does not exist for user - {}, skipping job - {}",
					job.getUserName(), job.getName());
			throw new TradeException("Kite session for " + job.getUserName() + " does not exists!");
		}
		TradeInfo tradeInfo = new TradeInfo(signal, session.getUserAccount().getUserName(), job.getName(), false);
		if (subscription == null) {
			subscription = subscriptionRepository.getByToken(tradeInfo.getSignal().getToken());
		}
		tradeInfo.setLotSize(subscription.getLotSize());
		for (Target target : job.getTargets()) {
			tradeInfo.getPositionInfoList().add(new PositionInfo(tradeInfo.getTradeId(), target, job));
		}

		switch (job.getJobType()) {
		case "ATR21_TSL":
			return new ATRTSLPaperTradeHandlerJob(job, tradeInfo, tradeManager, kiteModule, session, persistence,
					indicator, subscription);
		case "FIXED_TSL":
			return new FixedTSLPaperTradeHandlerJob(job, tradeInfo, tradeManager, kiteModule, session, persistence,
					indicator, subscription);
		case "FIXED_PROFIT":
			return new FixedProfitPaperTradeHandlerJob(job, tradeInfo, tradeManager, kiteModule, session,
					persistence, indicator, subscription);
		case "MULTI_TARGET":
			return new MultiTargetFPPaperTraderHandlerJob(job, tradeInfo, tradeManager, kiteModule,
					session, persistence, indicator, subscription);
		default:
			LOGGER.error("createPaperTradeHandlerJob: {} job is not supported for paper trading", job.getName());
			throw new TradeException(job.getName() + " job is not supported for paper trading");
		}
	}

	public TradeHandler createTradeHandlerJob(Job job, TradeInfo tradeInfo, TradeManager tradeManager)
			throws TradeException {
		TradeHandler tradeHandler = null;
		InstrumentSubscription subscription = subscriptionRepository.getByToken(tradeInfo.getSignal().getToken());
		if (tradeInfo.isLive()) {
			tradeHandler = createLiveTradeHandlerJob(job, tradeManager, tradeInfo.getSignal(), subscription, null);
		} else {
			tradeHandler = createPaperTradeHandlerJob(job, tradeManager, tradeInfo.getSignal(), subscription,
					null);
		}
		if (tradeHandler != null) {
			tradeHandler.setTradeInfo(tradeInfo);
		}

		return tradeHandler;
	}

}

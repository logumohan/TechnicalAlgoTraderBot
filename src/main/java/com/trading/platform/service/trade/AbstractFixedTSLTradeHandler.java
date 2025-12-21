package com.trading.platform.service.trade;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.trialing.strategies.TrailingStrategy;
import com.trading.platform.trialing.strategies.TrailingStrategyFactory;

public abstract class AbstractFixedTSLTradeHandler extends AbstractTradeHandler {

	protected AbstractFixedTSLTradeHandler(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule,
			KiteSessionService session, TradeJobPersistence persistence, InstrumentIndicators indicator,
			InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
	}

	@Override
	public double getTrailingStopLossPrice(PositionInfo positionInfo) {
		TrailingStrategy trialingStrategy = TrailingStrategyFactory.getTrialingStrategyByType(getJob()
				.getTrailingStrategy());
		return trialingStrategy.getTrailingStopLossPrice(positionInfo, getTarget(positionInfo.getTargetId()),
				getTradeInfo(), getJob(), getOptionLastTradedPrice());
	}

	public boolean isTrailingStopLossHit(PositionInfo positionInfo) {
		return getOptionLastTradedPrice() <= positionInfo.getSlPrice();
	}

	@Override
	public void handleTrailingStopLoss(PositionInfo positionInfo) {
		double slPrice = getTrailingStopLossPrice(positionInfo);
		if (slPrice > positionInfo.getSlPrice()) {
			positionInfo.setSlPrice(slPrice);
			LOGGER.info("Trailing stop loss is adjusted, stop loss price - {}", positionInfo.getSlPrice());
			positionInfo.setTrailingStopLossPrice(positionInfo.getSlPrice());
			getPersistence().updateTrade(getTradeInfo());
		}

		double unrealizedProfitLoss = getQuantity(positionInfo) * (getOptionLastTradedPrice() - positionInfo
				.getOptionEntryPrice());
		LOGGER.info("Unrealized profit or loss for position {} is {}", positionInfo.getTargetId(),
				unrealizedProfitLoss);
		if (unrealizedProfitLoss > 0 && unrealizedProfitLoss > positionInfo.getUnrealizedProfit()) {
			positionInfo.setUnrealizedProfit(Double.valueOf(decimalFormat.format(unrealizedProfitLoss)));
			getPersistence().updateTrade(getTradeInfo());
			LOGGER.debug("Updated unrealized profit for the position {} is {}", positionInfo.getTargetId(),
					positionInfo);
		} else if (unrealizedProfitLoss < 0 && unrealizedProfitLoss < positionInfo.getUnrealizedLoss()) {
			positionInfo.setUnrealizedLoss(Double.valueOf(decimalFormat.format(unrealizedProfitLoss)));
			getPersistence().updateTrade(getTradeInfo());
			LOGGER.debug("Updated unrealized loss for the position {} is {}", positionInfo.getTargetId(),
					positionInfo);
		}

		LOGGER.info("Trailing stop loss price - {}", positionInfo.getSlPrice());
	}

}

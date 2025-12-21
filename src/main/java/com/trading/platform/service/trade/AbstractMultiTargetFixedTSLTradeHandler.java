package com.trading.platform.service.trade;

import java.time.ZonedDateTime;
import java.util.Date;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.trialing.strategies.MultiTargetTrailingStrategy;
import com.trading.platform.trialing.strategies.TrailingStrategyFactory;

public abstract class AbstractMultiTargetFixedTSLTradeHandler extends AbstractTradeHandler {

	protected AbstractMultiTargetFixedTSLTradeHandler(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
	}

	@Override
	public double getTrailingStopLossPrice(PositionInfo positionInfo) {
		MultiTargetTrailingStrategy trialingStrategy = TrailingStrategyFactory.getMultiTargetTrialingStrategyByType(
				getJob()
						.getTrailingStrategy());
		return trialingStrategy.getTrailingStopLossPriceForTarget(positionInfo, getTradeInfo(), getJob(),
				getJob().getTargets().get(0), getOptionLastTradedPrice());
	}

	public double getTrailingStopLossPrice(PositionInfo positionInfo, Target target) {
		MultiTargetTrailingStrategy trialingStrategy = TrailingStrategyFactory.getMultiTargetTrialingStrategyByType(
				getJob().getTrailingStrategy());
		return trialingStrategy.getTrailingStopLossPriceForTarget(positionInfo, getTradeInfo(), getJob(),
				target, getOptionLastTradedPrice());
	}

	@Override
	public boolean isStopLossHit(PositionInfo positionInfo) {
		return getOptionLastTradedPrice() <= positionInfo.getSlPrice();
	}

	@Override
	public void handleTrailingStopLoss(PositionInfo positionInfo) {
		Target target = getJob().getTargets().stream()
				.filter((Target currTarget) -> positionInfo.getTargetId() == currTarget.getTargetId())
				.findAny().orElse(null);
		if (target == null) {
			LOGGER.error("Could not find the target for the given position - {}, trailing with 10 points",
					positionInfo);
			positionInfo.setSlPrice(getOptionLastTradedPrice() - 10);
			getPersistence().updateTrade(getTradeInfo());
			return;
		}
		double slPrice = getTrailingStopLossPrice(positionInfo, target);
		double prevSlPrice = positionInfo.getSlPrice();
		if (slPrice > prevSlPrice) {
			prevSlPrice = slPrice;
			LOGGER.info("Trailing stop loss is adjusted for targetId:{}, stop loss price - {}", positionInfo
					.getTargetId(),
					prevSlPrice);

			positionInfo.setTrailingStopLossPrice(prevSlPrice);
			if (target.getTargetId() == 1) {
				positionInfo.setTrailingStopLossPrice(prevSlPrice);
			}
		}

		int quantity = target.getNumOfLots() * subscription.getLotSize();
		double unrealizedProfitLoss = quantity * (getOptionLastTradedPrice() - positionInfo.getOptionEntryPrice());
		LOGGER.info("Unrealized profit or loss for position {} is {}", positionInfo.getTargetId(),
				unrealizedProfitLoss);
		if (unrealizedProfitLoss > 0 && unrealizedProfitLoss > positionInfo.getUnrealizedProfit()) {
			positionInfo.setUnrealizedProfit(Double.valueOf(decimalFormat.format(unrealizedProfitLoss)));
			positionInfo.setUnrealizedProfit(Double.valueOf(decimalFormat.format(
					unrealizedProfitLoss)));
			LOGGER.debug("Updated unrealized profit for the position {} is {}", positionInfo.getTargetId(),
					positionInfo);
		} else if (unrealizedProfitLoss < 0 && unrealizedProfitLoss < positionInfo.getUnrealizedLoss()) {
			positionInfo.setUnrealizedLoss(Double.valueOf(decimalFormat.format(unrealizedProfitLoss)));
			positionInfo.setUnrealizedLoss(Double.valueOf(decimalFormat.format(
					unrealizedProfitLoss)));
			LOGGER.debug("Updated unrealized loss for the position {} is {}", positionInfo.getTargetId(), positionInfo);
		}

		getPersistence().updateTrade(getTradeInfo());

		LOGGER.info("Trailing stop loss price for targetId:{} : {}", positionInfo.getTargetId(), prevSlPrice);
	}

	@Override
	public void markEntry(PositionInfo positionInfo, double entryPrice, double optionEntryLtp,
			double optionEntryPrice) {
		positionInfo.setEntryTime(Date.from(ZonedDateTime.now().toInstant()));
		positionInfo.setEntryPrice(entryPrice);
		positionInfo.setOptionEntryLtp(optionEntryLtp);
		positionInfo.setOptionEntryPrice(optionEntryPrice);
	}

	@Override
	public void markExit(PositionInfo positionInfo, double exitPrice, double optionExitLtp,
			double optionExitPrice) {
		positionInfo.setExitTime(Date.from(ZonedDateTime.now().toInstant()));
		positionInfo.setExitPrice(exitPrice);
		positionInfo.setOptionExitLtp(optionExitLtp);
		positionInfo.setOptionExitPrice(optionExitPrice);
	}

}

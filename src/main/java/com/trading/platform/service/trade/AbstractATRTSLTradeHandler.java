package com.trading.platform.service.trade;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.signal.OptionType;

public abstract class AbstractATRTSLTradeHandler extends AbstractTradeHandler {

	protected AbstractATRTSLTradeHandler(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule,
			KiteSessionService session, TradeJobPersistence persistence, InstrumentIndicators indicator,
			InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
	}

	@Override
	public double getTrailingStopLossPrice(PositionInfo positionInfo) {
		double stopLossPrice = 0;
		if (getTradeInfo().getSignal().getTradeSignal().endsWith(OptionType.BUY_CE.name())) {
			stopLossPrice = getLastTradedPrice() - (getAverageTrueRange() * getJob().getAtrMultiplier());
		} else {
			stopLossPrice = getLastTradedPrice() + (getAverageTrueRange() * getJob().getAtrMultiplier());
		}
		return Math.round(stopLossPrice);
	}

	public boolean isTrailingStopLossHit(PositionInfo positionInfo) {
		return (getTradeInfo().getSignal().getTradeSignal().endsWith(OptionType.BUY_CE.name())
				&& getLastTradedPrice() <= positionInfo.getSlPrice())
				||
				(getTradeInfo().getSignal().getTradeSignal().endsWith(OptionType.BUY_PE.name())
						&& getLastTradedPrice() >= positionInfo.getSlPrice());
	}

	@Override
	public void handleTrailingStopLoss(PositionInfo positionInfo) {
		double nonRealizedProfit = getUnRealizedProfitPoints(positionInfo);
		if (nonRealizedProfit >= 150 && getJob().getAtrMultiplier() > 1) {
			LOGGER.info("Multiplier for trailing stop loss is adjusted to 1 as non realized profit is over 150");
			getJob().setAtrMultiplier(1);
		} else if (nonRealizedProfit >= 100 && getJob().getAtrMultiplier() > 2) {
			LOGGER.info("Multiplier for trailing stop loss is adjusted to 2 as non realized profit is over 100");
			getJob().setAtrMultiplier(2);
		} else if (nonRealizedProfit >= 50 && getJob().getAtrMultiplier() >= 3) {
			LOGGER.info("Multiplier for trailing stop loss is adjusted to 2.5 as non realized profit is over 50");
			getJob().setAtrMultiplier(2.5);
		}
		double diffInPrice = 0;
		if (getTradeInfo().getSignal().getTradeSignal().endsWith(OptionType.BUY_CE.name())) {
			diffInPrice = getLastTradedPrice() - positionInfo.getSlPrice();
		} else {
			diffInPrice = positionInfo.getSlPrice() - getLastTradedPrice();
		}

		if (diffInPrice > (getAverageTrueRange() * getJob().getAtrMultiplier())) {
			positionInfo.setSlPrice(getTrailingStopLossPrice(positionInfo));
			LOGGER.info("Trailing stop loss price adjusted, stop loss price - {}", positionInfo.getSlPrice());
			positionInfo.setTrailingStopLossPrice(positionInfo.getSlPrice());
			positionInfo.setUnrealizedProfit(Double.valueOf(decimalFormat.format(getQuantity(positionInfo) *
					(getOptionLastTradedPrice() - positionInfo.getOptionEntryPrice()))));
			getPersistence().updateTrade(getTradeInfo());
		}
		LOGGER.info("Trailing stop loss price - {}", positionInfo.getSlPrice());
	}

}

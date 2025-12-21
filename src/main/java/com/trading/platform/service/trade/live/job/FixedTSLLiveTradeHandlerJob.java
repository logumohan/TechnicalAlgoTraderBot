package com.trading.platform.service.trade.live.job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeException;
import com.trading.platform.service.trade.TradeInfo;
import com.trading.platform.service.trade.TradeJobPersistence;
import com.trading.platform.service.trade.TradeManager;
import com.trading.platform.service.trade.live.AbstractFixedTSLLiveTradeHandler;
import com.trading.platform.util.MarketTimeUtil;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;

public class FixedTSLLiveTradeHandlerJob extends AbstractFixedTSLLiveTradeHandler {

	public FixedTSLLiveTradeHandlerJob(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
		LOGGER.trace("Fixed TSL live trade handler is intialized");
	}

	@Override
	public double getStopLossPrice(PositionInfo positionInfo) {
		return positionInfo.getOptionEntryPrice() - getTarget(positionInfo.getTargetId()).getTrailingStopLoss();
	}

	@Override
	public boolean isStopLossHit(PositionInfo positionInfo) {
		if (isTrailingStopLossHit(positionInfo)) {
			LOGGER.info("Waiting till next minute candle to check whether the trailing stop loss is hit");
			// Wait till next minute candle, to ignore a small dip in market
			Optional<Double> candleLtp = Optional.ofNullable(getOneMinIndicator())
					.map(InstrumentIndicators::getLastTradedPrice);
			if (candleLtp.isPresent()) {
				LOGGER.info("Current candle LTP (index) - {}, current option LTP - {}",
						candleLtp.get(), getOptionLastTradedPrice());
				do {
					if ((positionInfo.getSlPrice() - getOptionLastTradedPrice()) >= 10) {
						// quit if current LTP is 10 or more points less than the stop loss price
						LOGGER.info("Current LTP is 10 or more points lower than the stop loss price, " +
								"closing hte position immediately, current LTP - {}", getOptionLastTradedPrice());
						return true;
					}
					// Wait for 2 seconds to check whether the candle changes
					wait(2);
				} while (getOneMinIndicator().getLastTradedPrice() == candleLtp.get());
				// candle changed
				LOGGER.info("Next candle is received, previous ltp - {}, current ltp - {}", candleLtp.get(),
						getOneMinIndicator().getLastTradedPrice());
				return isTrailingStopLossHit(positionInfo);
			} else {
				LOGGER.error("Previous minute candle is not available, skipping...");
			}

			return true;
		}

		return false;
	}

	private void handleStopLoss() {
		// Place market sell order
		double optionExitLtp = getOptionLastTradedPrice();
		try {
			Order sellOrder = placeMarketSellOrder();
			wait(5);

			Order order = null;
			if (sellOrder != null) {
				do {
					List<Order> slmOrderList = getSession().getOrder(sellOrder.orderId);
					order = slmOrderList.get(slmOrderList.size() - 1);
					wait(1);
				} while (!Constants.ORDER_COMPLETE.equals(order.status));
				for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
					markExit(positionInfo, getLastTradedPrice(), optionExitLtp,
							Double.valueOf(decimalFormat.format(Double.valueOf(order.averagePrice))));
					getTradeManager().publishPositionInfo("Trailing Stop Loss Triggered", positionInfo);
				}
			} else {
				LOGGER.fatal("Sell market order is not placed, signal - {}, position should be closed manually",
						getTradeInfo().getSignal());
				for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
					markExit(positionInfo, getLastTradedPrice(), optionExitLtp, getOptionLastTradedPrice());
				}
			}
		} catch (TradeException e) {
			LOGGER.error("Error in fetching the sell market order status for the symbol - {}",
					getTradeInfo().getSignal().getOptionSymbol(), e);
			for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
				markExit(positionInfo, getLastTradedPrice(), optionExitLtp, getOptionLastTradedPrice());
			}
		}

		getPersistence().updateTrade(getTradeInfo());
	}

	@Override
	public void doTrade() {
		LOGGER.info("Entered inside doTrade()");
		double optionEntryLtp = getOptionLastTradedPrice();
		placeMarketBuyOrder();
		waitTillOrderExecution(optionEntryLtp);

		PositionInfo positionInfo = getTradeInfo().getPositionInfoList().stream().filter(PositionInfo::isActive)
				.findFirst().orElse(null);
		while (positionInfo != null && positionInfo.isActive()) {
			if (isSquareOff() || MarketTimeUtil.getSquareOffTime().toLocalDateTime().isBefore(LocalDateTime.now())) {
				squareOff();
				getTradeManager().publishPositionInfo("Squareoff", positionInfo);
			} else if (isStopLossHit(positionInfo)) {
				handleStopLoss();
			} else {
				handleTrailingStopLoss(positionInfo);

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					LOGGER.error("Error while waiting to get the next quote for the symbol - {}",
							getTradeInfo().getSignal().getOptionSymbol(), e);
					Thread.currentThread().interrupt();
				}
			}
		}
	}

}

package com.trading.platform.service.trade.paper.job;

import java.time.LocalDateTime;
import java.util.List;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;
import com.trading.platform.service.trade.TradeJobPersistence;
import com.trading.platform.service.trade.TradeManager;
import com.trading.platform.service.trade.paper.AbstractMultiTargetFixedTSLPaperTradeHandler;
import com.trading.platform.util.MarketTimeUtil;
import com.zerodhatech.models.Order;

public class MultiTargetFPPaperTraderHandlerJob extends AbstractMultiTargetFixedTSLPaperTradeHandler {

	public MultiTargetFPPaperTraderHandlerJob(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
	}

	@Override
	public double getStopLossPrice(PositionInfo positionInfo) {
		return positionInfo.getSlPrice();
	}
	
	@Override
	public boolean isStopLossHit(PositionInfo positionInfo) {
		if (isTrailingStopLossHit(positionInfo)) {
			LOGGER.info("Trailing stop loss hit, closing the poistion immediately");
			return true;
		}

		return false;
	}

	private void handleStopLoss(PositionInfo positionInfo) {
		// Place market sell order
		double optionExitLtp = getOptionLastTradedPrice();
		Target target = getJob().getTargets().stream()
				.filter((Target currTarget) -> positionInfo.getTargetId() == currTarget.getTargetId())
				.findAny().orElse(null);
		if (target == null) {
			LOGGER.error("Target Id - {}: Could not find the target for the given position - {}",
					positionInfo.getTargetId(), positionInfo);
			return;
		}

		Order order = placeMarketSellOrder(target);
		wait(1);

		if (order != null) {
			markExit(positionInfo, getLastTradedPrice(), optionExitLtp,
					Double.valueOf(decimalFormat.format(Double.valueOf(order.averagePrice))));
		} else {
			LOGGER.fatal(
					"Target Id - {}: Sell market order is not placed, signal - {}, position should be closed manually",
					positionInfo.getTargetId(), getTradeInfo().getSignal());
			markExit(positionInfo, getLastTradedPrice(), optionExitLtp, getOptionLastTradedPrice());
		}

		getPersistence().updateTrade(getTradeInfo());
	}

	private boolean isActive() {
		return getTradeInfo().getPositionInfoList().stream().anyMatch(PositionInfo::isActive);
	}

	private void handlePositions() {
		List<PositionInfo> positionInfoList = getTradeInfo().getPositionInfoList().stream().filter(
				PositionInfo::isActive).toList();
		for (PositionInfo positionInfo : positionInfoList) {
			if (isStopLossHit(positionInfo)) {
				if (getOptionLastTradedPrice() <= positionInfo.getOptionEntryPrice()) {
					LOGGER.info("Target Id - {}: Stop loss hit below entry price, squaring off all positions",
							positionInfo.getTargetId());
					setSquareOff();
					break;
				} else {
					handleStopLoss(positionInfo);
				}
			} else {
				handleTrailingStopLoss(positionInfo);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOGGER.error("Target Id - {}: Error while waiting to get the next quote for the symbol - {}",
							positionInfo.getTargetId(), getTradeInfo().getSignal().getOptionSymbol(), e);
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Override
	public void doTrade() {
		LOGGER.info("Entered inside doTrade()");
		double optionEntryLtp = getOptionLastTradedPrice();
		placeMarketBuyOrder();
		waitTillOrderExecution(optionEntryLtp);

		while (isActive()) {
			if (isSquareOff() || MarketTimeUtil.getSquareOffTime().toLocalDateTime().isBefore(LocalDateTime.now())) {
				squareOff();
				break;
			}

			handlePositions();
		}

		getPersistence().updateTrade(getTradeInfo());
	}

}

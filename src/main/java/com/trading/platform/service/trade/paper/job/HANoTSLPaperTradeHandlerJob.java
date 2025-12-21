package com.trading.platform.service.trade.paper.job;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.CloseableThreadContext;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;
import com.trading.platform.service.trade.TradeJobPersistence;
import com.trading.platform.service.trade.TradeManager;
import com.trading.platform.util.MarketTimeUtil;
import com.zerodhatech.models.Order;

public class HANoTSLPaperTradeHandlerJob extends AbstractHANoTSLTradeHandler {

	private AtomicInteger orderIdGenerator;

	public HANoTSLPaperTradeHandlerJob(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
		orderIdGenerator = new AtomicInteger();
	}

	@Override
	public boolean isLive() {
		return false;
	}

	@Override
	public double getStopLossPrice(PositionInfo positionInfo) {
		return positionInfo.getOptionEntryPrice() - getTarget(positionInfo.getTargetId()).getTrailingStopLoss();
	}

	protected String generateOrderId() {
		StringBuilder builder = new StringBuilder();
		builder.append("PO");
		builder.append(getTradeInfo().getSignal().getName().replace(" ", ""));
		builder.append(getTradeInfo().getSignal().getAggregationType().replace("_", ""));
		builder.append(String.valueOf(orderIdGenerator.getAndIncrement()));

		return builder.toString();
	}

	@Override
	public Order placeMarketBuyOrder() {
		Order order = new Order();
		order.orderId = generateOrderId();
		order.averagePrice = String.valueOf(getLastTradedPrice());

		getTradeInfo().setOrder(order);

		LOGGER.info("Paper Trade: placeMarketBuyOrder(), do nothing... name = {}, option symbol = {}, "
				+ "duration = {}, order id = {}, average price = {}",
				getTradeInfo().getSignal().getName(),
				getTradeInfo().getSignal().getOptionSymbol(),
				getTradeInfo().getSignal().getAggregationType(),
				order.orderId,
				order.averagePrice);

		return order;
	}

	@Override
	public Order placeMarketSellOrder() {
		Order order = new Order();
		order.orderId = generateOrderId();
		order.averagePrice = String.valueOf(getLastTradedPrice());

		LOGGER.info("Paper Trade: placeMarketSellOrder(), do nothing... name = {}, option symbol = {}, "
				+ "duration = {}, order id = {}, average price = {}",
				getTradeInfo().getSignal().getName(),
				getTradeInfo().getSignal().getOptionSymbol(),
				getTradeInfo().getSignal().getAggregationType(),
				order.orderId,
				order.averagePrice);

		return order;
	}

	@Override
	public void waitTillOrderExecution(double optionEntryLtp) {
		LOGGER.info(
				"Paper Trade: waitTillOrderExecution(), do nothing... name = {}, option symbol = {}, duration = {}",
				getTradeInfo().getSignal().getName(),
				getTradeInfo().getSignal().getOptionSymbol(),
				getTradeInfo().getSignal().getAggregationType());
	}

	@Override
	public boolean isStopLossHit(PositionInfo positionInfo) {
		return getOptionLastTradedPrice() <= positionInfo.getSlPrice();
	}

	@Override
	public void squareOff() {
		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put(TRADE_ID, getTradeInfo().getTradeId())
				.put(INSTRUMENT_NAME, getTradeInfo().getSignal().getName().toUpperCase())
				.put(TOKEN, String.valueOf(getTradeInfo().getSignal().getToken()))
				.put(DURATION, getTradeInfo().getSignal().getAggregationType())
				.put(SYMBOL, getTradeInfo().getSignal().getOptionSymbol())) {
			for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
				positionInfo.setSquareOff(true);
			}
			LOGGER.info(
					"Paper Trade: squareOff(), do nothing... name = {}, option symbol = {}, duration = {}",
					getTradeInfo().getSignal().getName(),
					getTradeInfo().getSignal().getOptionSymbol(),
					getTradeInfo().getSignal().getAggregationType());
		}
	}

	@Override
	public void doTrade() {
		double optionEntryLtp = getOptionLastTradedPrice();
		placeMarketBuyOrder();
		waitTillOrderExecution(optionEntryLtp);
		for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
			markEntry(positionInfo, getLastTradedPrice(), optionEntryLtp, getOptionLastTradedPrice());
		}
		getPersistence().persistTrade(getTradeInfo());
		getTradeManager().publishTradeInfo("Order Executed", getTradeInfo());

		PositionInfo positionInfo = getTradeInfo().getPositionInfoList().stream().filter(PositionInfo::isActive)
				.findFirst().orElse(null);
		while (positionInfo != null && positionInfo.isActive()) {
			if (isSquareOff() || MarketTimeUtil.getSquareOffTime().toLocalDateTime().isBefore(LocalDateTime.now())) {
				LOGGER.info(
						"TRADE: Square off. Closing the position, name = {}, option symbol = {}, duration = {}",
						getTradeInfo().getSignal().getName(),
						getTradeInfo().getSignal().getOptionSymbol(),
						getTradeInfo().getSignal().getAggregationType());
				double optionExitLtp = getOptionLastTradedPrice();
				squareOff();
				markExit(positionInfo, getLastTradedPrice(), optionExitLtp, getOptionLastTradedPrice());
				getPersistence().updateTrade(getTradeInfo());
				getTradeManager().publishPositionInfo("Squared off", positionInfo);
			} else if (isStopLossHit(positionInfo)) {
				LOGGER.info("TRADE: Stop loss hit, name = {}, option symbol = {}, duration = {}",
						getTradeInfo().getSignal().getName(),
						getTradeInfo().getSignal().getOptionSymbol(),
						getTradeInfo().getSignal().getAggregationType());
				double optionExitLtp = getOptionLastTradedPrice();
				placeMarketSellOrder();
				markExit(positionInfo, getLastTradedPrice(), optionExitLtp, getOptionLastTradedPrice());
				getPersistence().updateTrade(getTradeInfo());
				getTradeManager().publishPositionInfo("Stop Loss Hit", positionInfo);
			} else {
				updateUnrealizedProfitLoss(positionInfo);

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					LOGGER.error("TRADE: Error while waiting to get the next quote for the symbol - {}",
							getTradeInfo().getSignal().getOptionSymbol(), e);
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	private void updateUnrealizedProfitLoss(PositionInfo positionInfo) {
		double unrealizedProfitLoss = Double.parseDouble(decimalFormat.format(getQuantity(positionInfo) *
				(getOptionLastTradedPrice() - positionInfo.getOptionEntryPrice())));
		if (unrealizedProfitLoss > positionInfo.getUnrealizedProfit()) {
			positionInfo.setUnrealizedProfit(unrealizedProfitLoss);
			getPersistence().updateTrade(getTradeInfo());
		} else if (unrealizedProfitLoss < 0 && unrealizedProfitLoss < positionInfo.getUnrealizedLoss()) {
			positionInfo.setUnrealizedLoss(Double.valueOf(decimalFormat.format(unrealizedProfitLoss)));
			getPersistence().updateTrade(getTradeInfo());
		}
	}

	@Override
	public double getTrailingStopLossPrice(PositionInfo positionInfo) {
		return 0;
	}

	@Override
	public void handleTrailingStopLoss(PositionInfo positionInfo) {
		// Do Nothing
	}

}

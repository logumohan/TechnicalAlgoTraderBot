package com.trading.platform.service.trade.paper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.logging.log4j.CloseableThreadContext;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.trade.AbstractMultiTargetFixedTSLTradeHandler;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;
import com.trading.platform.service.trade.TradeJobPersistence;
import com.trading.platform.service.trade.TradeManager;
import com.zerodhatech.models.Order;

public abstract class AbstractMultiTargetFixedTSLPaperTradeHandler extends AbstractMultiTargetFixedTSLTradeHandler {

	private AtomicInteger orderIdGenerator;

	protected AbstractMultiTargetFixedTSLPaperTradeHandler(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
		this.orderIdGenerator = new AtomicInteger();
	}

	@Override
	public boolean isLive() {
		return false;
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
		order.averagePrice = String.valueOf(getOptionLastTradedPrice());

		getTradeInfo().setOrder(order);

		LOGGER.info("Paper Trade: placeMarketBuyOrder(), do nothing... name = {}, option symbol = {}, "
				+ "duration = {}, order id = {}, avg price = {}",
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

	public Order placeMarketSellOrder(Target target) {
		Order order = new Order();
		order.orderId = generateOrderId();
		order.averagePrice = String.valueOf(getOptionLastTradedPrice());

		LOGGER.info(
				"Paper Trade: placeMarketSellOrder(target), do nothing... target = {}, name = {}, option symbol = {}, "
						+ "duration = {}, order id = {}, average price = {}",
				target.getTargetId(),
				getTradeInfo().getSignal().getName(),
				getTradeInfo().getSignal().getOptionSymbol(),
				getTradeInfo().getSignal().getAggregationType(),
				order.orderId,
				order.averagePrice);

		return order;
	}

	public boolean isTrailingStopLossHit(PositionInfo positionInfo) {
		return getOptionLastTradedPrice() <= positionInfo.getSlPrice();
	}

	@Override
	public void waitTillOrderExecution(double optionEntryLtp) {
		LOGGER.info(
				"Paper Trade: waitTillOrderExecution(), do nothing... name = {}, option symbol = {}, duration = {}",
				getTradeInfo().getSignal().getName(),
				getTradeInfo().getSignal().getOptionSymbol(),
				getTradeInfo().getSignal().getAggregationType());
		for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
			markEntry(positionInfo, getLastTradedPrice(), optionEntryLtp,
					Double.valueOf(decimalFormat.format(Double.valueOf(getOptionLastTradedPrice()))));
			positionInfo.setSlPrice(positionInfo.getOptionEntryPrice() - getTarget(positionInfo.getTargetId())
					.getTrailingStopLoss());
		}
		getPersistence().persistTrade(getTradeInfo());
	}

	@Override
	public void squareOff() {
		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put(TRADE_ID, getTradeInfo().getTradeId())
				.put(INSTRUMENT_NAME, getTradeInfo().getSignal().getName().toUpperCase())
				.put(TOKEN, String.valueOf(getTradeInfo().getSignal().getToken()))
				.put(DURATION, getTradeInfo().getSignal().getAggregationType())
				.put(SYMBOL, getTradeInfo().getSignal().getOptionSymbol())) {
			Order order = new Order();
			order.orderId = generateOrderId();
			order.averagePrice = String.valueOf(getOptionLastTradedPrice());
			markPositionExit(order, getOptionLastTradedPrice());
			getPersistence().updateTrade(getTradeInfo());
			LOGGER.info(
					"Paper Trade: squareOff(), do nothing... name = {}, option symbol = {}, duration = {}",
					getTradeInfo().getSignal().getName(),
					getTradeInfo().getSignal().getOptionSymbol(),
					getTradeInfo().getSignal().getAggregationType());
		}
	}

	private void markPositionExit(Order order, double optionExitLtp) {
		List<PositionInfo> positionInfoList = getTradeInfo().getPositionInfoList().stream().filter(
				PositionInfo::isActive).collect(Collectors.toList());
		for (PositionInfo positionInfo : positionInfoList) {
			if (positionInfo.isActive()) {
				markExit(positionInfo, getLastTradedPrice(), optionExitLtp,
						order.averagePrice != null ? Double.valueOf(order.averagePrice)
								: getOptionLastTradedPrice());
				positionInfo.setSquareOff(true);
			}
		}
	}

}

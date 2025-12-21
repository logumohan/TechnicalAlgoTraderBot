package com.trading.platform.service.trade.live;

import java.util.List;

import org.apache.logging.log4j.CloseableThreadContext;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.trade.AbstractFixedTSLTradeHandler;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeException;
import com.trading.platform.service.trade.TradeInfo;
import com.trading.platform.service.trade.TradeJobPersistence;
import com.trading.platform.service.trade.TradeManager;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;

public abstract class AbstractFixedTSLLiveTradeHandler extends AbstractFixedTSLTradeHandler {

	protected AbstractFixedTSLLiveTradeHandler(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
	}

	@Override
	public boolean isLive() {
		return true;
	}

	@Override
	public Order placeMarketBuyOrder() {
		try {
			Order order = getSession().placeMarketBuyOrder(getTradeInfo().getSignal().getOptionSymbol(), getQuantity());
			getTradeInfo().setOrder(order);
			LOGGER.info("Buy market order placed, signal - {}, orderId - {}", getTradeInfo().getSignal(),
					order.orderId);

			return order;
		} catch (TradeException e) {
			LOGGER.error("Error in placing the market buy order for the symbol - {}",
					getTradeInfo().getSignal().getOptionSymbol(), e);
		}
		return null;
	}

	@Override
	public Order placeMarketSellOrder() {
		try {
			Order order = getSession().placeMarketSellOrder(getTradeInfo().getSignal().getOptionSymbol(),
					getQuantity());
			getTradeInfo().setOrder(order);
			LOGGER.info("Sell market order placed, signal - {}, orderId - {}", getTradeInfo().getSignal(),
					order.orderId);

			return order;
		} catch (TradeException e) {
			LOGGER.error("Error in placing the market sell order for the symbol - {}",
					getTradeInfo().getSignal().getOptionSymbol(), e);
		}
		return null;
	}

	@Override
	public void squareOff() {
		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put(TRADE_ID, getTradeInfo().getTradeId())
				.put(INSTRUMENT_NAME, getTradeInfo().getSignal().getName().toUpperCase())
				.put(TOKEN, String.valueOf(getTradeInfo().getSignal().getToken()))
				.put(DURATION, getTradeInfo().getSignal().getAggregationType())
				.put(SYMBOL, getTradeInfo().getSignal().getOptionSymbol())) {
			double optionExitLtp = getOptionLastTradedPrice();
			Order order = placeMarketSellOrder();
			wait(5);

			Order slOrder = null;
			do {
				try {
					List<Order> orders = getSession().getOrder(order.orderId);
					slOrder = orders.get(orders.size() - 1);

					if (Constants.ORDER_COMPLETE.equals(slOrder.status)) {
						for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
							positionInfo.setSquareOff(true);
							markExit(positionInfo, getLastTradedPrice(), optionExitLtp,
									order.averagePrice != null ? Double.valueOf(order.averagePrice)
											: getOptionLastTradedPrice());
						}
					} else {
						LOGGER.info("Square off order is not complete, waiting for a second, orderId - {}",
								order.orderId);
						wait(1);
					}
				} catch (TradeException e) {
					LOGGER.error("Error in checking the status of the stop loss market order, orderId - {}",
							order.orderId);
					markExitForAllPositions(optionExitLtp, order);
				}
			} while (slOrder != null && !Constants.ORDER_COMPLETE.equals(slOrder.status));

			getPersistence().updateTrade(getTradeInfo());
		}
	}

	private void markExitForAllPositions(double optionExitLtp, Order order) {
		for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
			positionInfo.setSquareOff(true);
			markExit(positionInfo, getLastTradedPrice(), optionExitLtp,
					order.averagePrice != null ? Double.valueOf(order.averagePrice)
							: getOptionLastTradedPrice());
		}
	}

	@Override
	public void waitTillOrderExecution(double optionEntryLtp) {
		Order order = null;
		do {
			try {
				LOGGER.info("Checking whether order is executed, order id - {}", getTradeInfo().getOrder().orderId);
				List<Order> orderList = getSession().getOrder(getTradeInfo().getOrder().orderId);
				order = orderList.get(orderList.size() - 1);
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				LOGGER.error("Interrupted while waiting to get order status, {}", getTradeInfo().getOrder().orderId, e);
				Thread.currentThread().interrupt();
			} catch (TradeException e) {
				LOGGER.error("Error retrieving the order status, orderId = {}", getTradeInfo().getOrder().orderId, e);
			}
		} while (order != null && isOrderOpen(order.status));

		if (order == null || !Constants.ORDER_COMPLETE.equals(order.status)) {
			getTradeInfo().getOrder().status = Constants.ORDER_REJECTED;
			for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
				markEntry(positionInfo, 0, 0, 0);
				markExit(positionInfo, 0, 0, 0);
			}
		} else {
			for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
				markEntry(positionInfo, getLastTradedPrice(), optionEntryLtp,
						Double.valueOf(decimalFormat.format(Double.valueOf(order.averagePrice))));
			}
			getTradeManager().publishTradeInfo("Order Executed", getTradeInfo());
		}

		getPersistence().persistTrade(getTradeInfo());
	}

}

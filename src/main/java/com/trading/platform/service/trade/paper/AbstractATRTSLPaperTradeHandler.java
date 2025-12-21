package com.trading.platform.service.trade.paper;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.CloseableThreadContext;

import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.trade.AbstractATRTSLTradeHandler;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;
import com.trading.platform.service.trade.TradeJobPersistence;
import com.trading.platform.service.trade.TradeManager;
import com.trading.platform.util.MarketTimeUtil;
import com.zerodhatech.models.Order;

public abstract class AbstractATRTSLPaperTradeHandler extends AbstractATRTSLTradeHandler {

	private AtomicInteger orderIdGenerator;

	protected AbstractATRTSLPaperTradeHandler(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		super(job, tradeInfo, tradeManager, kiteModule, session, persistence, indicator, subscription);
		this.orderIdGenerator = new AtomicInteger();
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
	public boolean isLive() {
		return false;
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
		if (isTrailingStopLossHit(positionInfo)) {
			LOGGER.info("Paper Trade: Waiting till next minute candle to check whether the trailing stop loss is hit");
			// Wait till next minute candle, to ignore a small dip in market
			Optional<Double> candleLtp = Optional.ofNullable(getOneMinIndicator())
					.map(InstrumentIndicators::getLastTradedPrice);
			if (candleLtp.isPresent()) {
				LOGGER.info("Paper Trade: Current candle LTP (index) - {}, current option LTP - {}",
						candleLtp.get(), getOptionLastTradedPrice());
				do {
					if (getUnRealizedProfitPoints(positionInfo) >= getTarget(positionInfo.getTargetId())
							.getTrailingStopLoss()) {
						LOGGER.info("Non realized profit points is greater than the fixed TSL points, " +
								"closing the position immediately, non realized profit points - {}",
								getUnRealizedProfitPoints(positionInfo));
						return true;
					} else if ((positionInfo.getSlPrice() - getOptionLastTradedPrice()) >= 5) {
						// quit if current LTP is 5 or more points less than the stop loss price
						LOGGER.info("Current LTP is 5 or more points lower than the stop loss price, " +
								"closing hte position immediately, current LTP - {}", getOptionLastTradedPrice());
						return true;
					}

					// Wait for 2 seconds to check whether the candle changes
					wait(2);
				} while (getOneMinIndicator().getLastTradedPrice() == candleLtp.get());
				// candle changed
				LOGGER.info("Paper Trade: Next candle is received, previous ltp - {}, current ltp - {}",
						candleLtp.get(),
						getOneMinIndicator().getLastTradedPrice());
				return isTrailingStopLossHit(positionInfo);
			} else {
				LOGGER.error("Paper Trade: Previous minute candle is not available, skipping...");
			}

			return true;
		}

		return false;
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
			} else if (isStopLossHit(positionInfo)) {
				LOGGER.info("TRADE: Stop loss hit, name = {}, option symbol = {}, duration = {}",
						getTradeInfo().getSignal().getName(),
						getTradeInfo().getSignal().getOptionSymbol(),
						getTradeInfo().getSignal().getAggregationType());
				double optionExitLtp = getOptionLastTradedPrice();
				placeMarketSellOrder();
				markExit(positionInfo, getLastTradedPrice(), optionExitLtp, getOptionLastTradedPrice());
				getPersistence().updateTrade(getTradeInfo());
			} else {
				handleTrailingStopLoss(positionInfo);

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

}

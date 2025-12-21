package com.trading.platform.util;

import java.util.concurrent.ThreadFactory;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.trading.platform.persistence.entity.Position;
import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.persistence.entity.Trade;
import com.trading.platform.service.trade.PositionInfo;
import com.trading.platform.service.trade.TradeInfo;
import com.zerodhatech.models.Order;

public class TradeUtil {

	private static final Logger LOGGER = LogManager.getLogger(TradeUtil.class);

	private TradeUtil() {
		// Do Nothing
	}

	public static ThreadFactory getLiveTradingThreadFactory() {
		return new BasicThreadFactory.Builder()
				.namingPattern("live-trading-%d")
				.uncaughtExceptionHandler((Thread thread, Throwable e) -> LOGGER
						.fatal("Uncaught exception during live trading - {}", thread.getName(), e))
				.daemon(true)
				.priority(Thread.MAX_PRIORITY)
				.build();
	}

	public static ThreadFactory getPaperTradingThreadFactory() {
		return new BasicThreadFactory.Builder()
				.namingPattern("paper-trading-%d")
				.uncaughtExceptionHandler((Thread thread, Throwable e) -> LOGGER
						.error("Uncaught exception during paper trading - {}", thread.getName(), e))
				.daemon(true)
				.priority(Thread.NORM_PRIORITY)
				.build();
	}

	public static TradeInfo convertToTradeInfo(Trade trade) {
		Signal signal = new Signal();
		signal.setTickTime(trade.getTickTime());
		signal.setToken(trade.getToken());
		signal.setName(trade.getName());
		signal.setAggregationType(trade.getAggregationType());
		signal.setStrategy(trade.getStrategy());
		signal.setTradeSignal(trade.getTradeSignal());
		signal.setLastTradedPrice(trade.getLastTradedPrice());
		signal.setStrikePrice(trade.getStrikePrice());
		signal.setOptionSymbol(trade.getOptionSymbol());
		signal.setAverageTrueRange(trade.getAverageTrueRange());

		TradeInfo tradeInfo = new TradeInfo(signal, trade.getUserName(), trade.getJobName(), trade.isLive());
		tradeInfo.setLive(trade.isLive());
		tradeInfo.setActive(trade.isActive());
		tradeInfo.setSignal(signal);
		tradeInfo.setLotSize(trade.getLotSize());
		tradeInfo.setTradeId(trade.getTradeId());

		Order order = new Order();
		order.orderId = trade.getOrderId();
		tradeInfo.setOrder(order);

		Order slOrder = new Order();
		order.orderId = trade.getSlOrderId();
		tradeInfo.setSLOrder(slOrder);

		return tradeInfo;
	}

	public static PositionInfo convertToPositionInfo(Position position) {
		PositionInfo positionInfo = new PositionInfo(position.getTradeId());
		positionInfo.setTradeId(position.getTradeId());
		positionInfo.setTargetId(position.getTargetId());
		positionInfo.setNumOfLots(position.getNumOfLots());
		positionInfo.setQuantity(position.getQuantity());
		positionInfo.setSoldQuantity(position.getSoldQuantity());
		if (position.getEntryTime() != null) {
			positionInfo.setEntryTime(position.getEntryTime());
		}
		if (position.getExitTime() != null) {
			positionInfo.setExitTime(position.getExitTime());
		}
		positionInfo.setEntryPrice(position.getEntryPrice());
		positionInfo.setExitPrice(position.getExitPrice());
		positionInfo.setOptionEntryLtp(position.getOptionEntryLtp());
		positionInfo.setOptionExitLtp(position.getOptionExitLtp());
		positionInfo.setOptionEntryPrice(position.getOptionEntryPrice());
		positionInfo.setOptionExitPrice(position.getOptionExitPrice());
		positionInfo.setUnrealizedProfit(position.getUnrealizedProfit());
		positionInfo.setUnrealizedLoss(position.getUnrealizedLoss());
		positionInfo.setProfit(position.getProfit());
		positionInfo.setActive(position.isActive());
		positionInfo.setClosed(position.isClosed());
		positionInfo.setStopLoss(position.isStopLoss());
		positionInfo.setSquareOff(position.isSquareOff());
		positionInfo.setSlPrice(position.getSlPrice());
		positionInfo.setSlTrail(position.getSlTrail());

		return positionInfo;
	}

	public static Trade convertToTrade(TradeInfo tradeInfo) {
		Trade trade = new Trade();
		trade.setTickTime(tradeInfo.getSignal().getTickTime());
		trade.setTradeId(tradeInfo.getTradeId());
		trade.setUserName(tradeInfo.getUserName());
		trade.setJobName(tradeInfo.getJobName());
		trade.setLive(tradeInfo.isLive());
		trade.setActive(tradeInfo.isActive());
		trade.setToken(tradeInfo.getSignal().getToken());
		trade.setName(tradeInfo.getSignal().getName());
		trade.setAggregationType(tradeInfo.getSignal().getAggregationType());
		trade.setStrategy(tradeInfo.getSignal().getStrategy());
		trade.setTradeSignal(tradeInfo.getSignal().getTradeSignal());
		trade.setLastTradedPrice(tradeInfo.getSignal().getLastTradedPrice());
		trade.setAverageTrueRange(tradeInfo.getSignal().getAverageTrueRange());
		trade.setStrikePrice(tradeInfo.getSignal().getStrikePrice());
		trade.setOptionSymbol(tradeInfo.getSignal().getOptionSymbol());
		trade.setLotSize(tradeInfo.getLotSize());
		if (tradeInfo.getOrder() != null) {
			trade.setOrderId(tradeInfo.getOrder().orderId);
		}

		return trade;
	}

	public static Position convertToPosition(PositionInfo positionInfo) {
		Position position = new Position();
		position.setTradeId(positionInfo.getTradeId());
		position.setTargetId(positionInfo.getTargetId());
		position.setNumOfLots(positionInfo.getNumOfLots());
		position.setQuantity(positionInfo.getQuantity());
		position.setSoldQuantity(positionInfo.getSoldQuantity());
		if (positionInfo.getEntryTime() != null) {
			position.setEntryTime(positionInfo.getEntryTime());
		}
		if (positionInfo.getExitTime() != null) {
			position.setExitTime(positionInfo.getExitTime());
		}
		if (positionInfo.getEntryPrice() != 0) {
			position.setEntryPrice(positionInfo.getEntryPrice());
		}
		if (positionInfo.getExitPrice() != 0) {
			position.setExitPrice(positionInfo.getExitPrice());
		}
		if (positionInfo.getOptionEntryLtp() != 0) {
			position.setOptionEntryLtp(positionInfo.getOptionEntryLtp());
		}
		if (positionInfo.getOptionExitLtp() != 0) {
			position.setOptionExitLtp(positionInfo.getOptionExitLtp());
		}
		if (positionInfo.getOptionEntryPrice() != 0) {
			position.setOptionEntryPrice(positionInfo.getOptionEntryPrice());
		}
		if (positionInfo.getOptionExitPrice() != 0) {
			position.setOptionExitPrice(positionInfo.getOptionExitPrice());
		}
		if (positionInfo.getUnrealizedProfit() != 0) {
			position.setUnrealizedProfit(positionInfo.getUnrealizedProfit());
		}
		if (positionInfo.getUnrealizedLoss() != 0) {
			position.setUnrealizedLoss(positionInfo.getUnrealizedLoss());
		}
		if (positionInfo.getProfit() != 0) {
			position.setProfit(positionInfo.getProfit());
		}
		position.setActive(positionInfo.isActive());
		position.setClosed(positionInfo.isClosed());
		position.setStopLoss(positionInfo.isStopLoss());
		position.setSquareOff(positionInfo.isSquareOff());
		if (positionInfo.getSlPrice() != 0) {
			position.setSlPrice(positionInfo.getSlPrice());
		}
		position.setSlTrail(positionInfo.getSlTrail());

		return position;
	}

	public static void updateTradeStatus(Trade trade, TradeInfo tradeInfo) {
		if (tradeInfo.getSLOrder() != null) {
			trade.setSlOrderId(tradeInfo.getSLOrder().orderId);
		}
		trade.setActive(tradeInfo.isActive());
		trade.setStrategy(tradeInfo.getSignal().getStrategy());
		trade.setLotSize(tradeInfo.getLotSize());
	}

	public static void updateTargetPositionStatus(Position position,
			PositionInfo positionInfo) {
		position.setNumOfLots(positionInfo.getNumOfLots());
		position.setQuantity(positionInfo.getQuantity());
		position.setSoldQuantity(positionInfo.getSoldQuantity());
		if (positionInfo.getEntryTime() != null) {
			position.setEntryTime(positionInfo.getEntryTime());
		}
		if (positionInfo.getExitTime() != null) {
			position.setExitTime(positionInfo.getExitTime());
		}
		if (positionInfo.getEntryPrice() != 0) {
			position.setEntryPrice(positionInfo.getEntryPrice());
		}
		if (positionInfo.getExitPrice() != 0) {
			position.setExitPrice(positionInfo.getExitPrice());
		}
		if (positionInfo.getOptionEntryLtp() != 0) {
			position.setOptionEntryLtp(positionInfo.getOptionEntryLtp());
		}
		if (positionInfo.getOptionExitLtp() != 0) {
			position.setOptionExitLtp(positionInfo.getOptionExitLtp());
		}
		if (positionInfo.getOptionEntryPrice() != 0) {
			position.setOptionEntryPrice(positionInfo.getOptionEntryPrice());
		}
		if (positionInfo.getOptionExitPrice() != 0) {
			position.setOptionExitPrice(positionInfo.getOptionExitPrice());
		}
		if (positionInfo.getUnrealizedProfit() != 0) {
			position.setUnrealizedProfit(positionInfo.getUnrealizedProfit());
		}
		if (positionInfo.getUnrealizedLoss() != 0) {
			position.setUnrealizedLoss(positionInfo.getUnrealizedLoss());
		}
		if (positionInfo.getProfit() != 0) {
			position.setProfit(positionInfo.getProfit());
		}
		position.setActive(positionInfo.isActive());
		position.setClosed(positionInfo.isClosed());
		position.setStopLoss(positionInfo.isStopLoss());
		position.setSquareOff(positionInfo.isSquareOff());
		if (positionInfo.getSlPrice() != 0) {
			position.setSlPrice(positionInfo.getSlPrice());
		}
		position.setSlTrail(positionInfo.getSlTrail());
	}

}

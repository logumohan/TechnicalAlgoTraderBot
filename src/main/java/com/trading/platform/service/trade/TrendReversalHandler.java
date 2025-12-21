package com.trading.platform.service.trade;

import java.util.Map;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.service.signal.OptionType;

@Component
public class TrendReversalHandler {

	private static final Logger LOGGER = LogManager.getLogger(TrendReversalHandler.class);

	public TrendReversalHandler() {
		// Do Nothing
	}

	public void handleTrendReversalForLiveTrades(Signal signal, Map<String, TradeHandler> subscriptionMap) {
		try {
			subscriptionMap.values().stream().filter(getLiveTradeFilter().and(getTrendReversalFilter(signal)))
					.forEach(handler -> {
						TradeInfo tradeInfo = handler.getTradeInfo();
						LOGGER.info("Attempting to square off live trade - {}, as the trend is reversing", tradeInfo);

						handler.setSquareOff();
					});
		} catch (Exception e) {
			LOGGER.error("Error in handling the trend reversal for the signal - {}", signal, e);
		}
	}

	public void handleTrendReversalForPaperTrades(Signal signal, Map<String, TradeHandler> subscriptionMap) {
		try {
			subscriptionMap.values().stream().filter(getPaperTradeFilter().and(getTrendReversalFilter(signal)))
					.forEach(handler -> {
						TradeInfo tradeInfo = handler.getTradeInfo();
						LOGGER.info("Attempting to square off paper trade - {}, as the trend is reversing", tradeInfo);

						handler.setSquareOff();
					});
		} catch (Exception e) {
			LOGGER.error("Error in handling the trend reversal for the signal - {}", signal, e);
		}
	}

	private Predicate<TradeHandler> getLiveTradeFilter() {
		return (TradeHandler handler) -> handler.getTradeInfo().isLive();
	}

	private Predicate<TradeHandler> getPaperTradeFilter() {
		return (TradeHandler handler) -> !handler.getTradeInfo().isLive();
	}

	private Predicate<TradeHandler> getTrendReversalFilter(Signal signal) {
		return (TradeHandler handler) -> handler.getTradeInfo().getSignal().getToken() == signal.getToken() &&
				handler.getTradeInfo().getSignal().getAggregationType().equals(signal.getAggregationType()) &&
				((signal.getStrategy().startsWith("TV_ALL")
						&& signal.getTradeSignal().startsWith(OptionType.SELL_CE.name())
						&& handler.getTradeInfo().getSignal().getTradeSignal().equals(OptionType.BUY_CE.name())
						&& handler.getTradeInfo().getSignal().getStrategy().startsWith("TV"))
						||
						(signal.getStrategy().startsWith("TV_ALL")
								&& signal.getTradeSignal().startsWith(OptionType.SELL_PE.name())
								&& handler.getTradeInfo().getSignal().getTradeSignal().equals(OptionType.BUY_PE.name())
								&& handler.getTradeInfo().getSignal().getStrategy().startsWith("TV"))
						||
						((signal.getTradeSignal().equals(OptionType.SELL_CE.name())
								|| signal.getTradeSignal().equals(OptionType.BUY_PE.name()))
								&& handler.getTradeInfo().getSignal().getTradeSignal().equals(OptionType.BUY_CE.name())
								&& signal.getStrategy().equals(handler.getTradeInfo().getSignal().getStrategy()))
						||
						((signal.getTradeSignal().equals(OptionType.SELL_PE.name())
								|| signal.getTradeSignal().equals(OptionType.BUY_CE.name()))
								&& handler.getTradeInfo().getSignal().getTradeSignal().equals(OptionType.BUY_PE.name())
								&& signal.getStrategy().equals(handler.getTradeInfo().getSignal().getStrategy())));
	}

}

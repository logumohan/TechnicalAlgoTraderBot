package com.trading.platform.test;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.service.trade.TradeManager;

public class TradeManagerTest {

	@Autowired
	private static TradeManager tradeManager;

	public static void main(String[] args) {
		Signal signal = new Signal();
		signal.setTickTime(new Date());
		signal.setToken(257801);
		signal.setName("NIFTY FIN SERVICE");
		signal.setAggregationType("ONE_MINUTE");
		signal.setTradeSignal("BUY_CE");
		signal.setLastTradedPrice(18669.9);
		signal.setAverageTrueRange(12.9133458588094);
		signal.setStrikePrice(19000);
		signal.setOptionSymbol("FINNIFTY22D2719000PE");

		tradeManager.handleSignal(signal, null, null);
	}

}

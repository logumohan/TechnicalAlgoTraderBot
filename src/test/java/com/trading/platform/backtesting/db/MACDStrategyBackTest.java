package com.trading.platform.backtesting.db;

import com.trading.platform.backtesting.AbstractBackTest;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.trading.strategies.MACDOptionsBuyStrategy;

public class MACDStrategyBackTest extends AbstractBackTest {

	public MACDStrategyBackTest(long token, String startTime, String endTime, AggregationType aggregationType) {
		super(token, startTime, endTime, aggregationType);
	}

	private void testStrategy() {
		BarSeriesWrapper barSeriesWrapper = getBarSeriesFromCSV("src/test/resources/onemin_nifty_bank.csv");

		InstrumentSubscription subscription = new InstrumentSubscription();
		subscription.setToken(260105);
		subscription.setName("NIFTY BANK");
		subscription.setOptionName("BANKNIFTY");
		subscription.setExpiryDay(4);
		subscription.setMinimumAtr(30);
		subscription.setLotSize(25);
		subscription.setStrikePriceDelta(300);
		subscription.setMinimumMacdDifference(4);
		subscription.setTradable(true);

		// Building the trading strategy
		MACDOptionsBuyStrategy strategy = new MACDOptionsBuyStrategy(barSeriesWrapper, subscription, null);

		testStrategy(barSeriesWrapper, strategy.getStrategy());
	}

	public static void main(String[] args) {
		AggregationType aggregationType = new AggregationType();
		aggregationType.setName("ONE_MINUTE");
		aggregationType.setDuration(60);
		aggregationType.setAggregable(true);

		MACDStrategyBackTest backTest = new MACDStrategyBackTest(260105, "26-12-2022 09:15:00", "27-12-2022 15:30:00",
				aggregationType);
		backTest.testStrategy();
	}

}

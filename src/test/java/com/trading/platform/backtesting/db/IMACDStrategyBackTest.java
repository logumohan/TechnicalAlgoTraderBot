package com.trading.platform.backtesting.db;

import com.trading.platform.backtesting.AbstractBackTest;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.trading.strategies.IMACDOptionsBuyStrategy;

public class IMACDStrategyBackTest extends AbstractBackTest {

	public IMACDStrategyBackTest(long token, String startTime, String endTime, AggregationType aggregationType) {
		super(token, startTime, endTime, aggregationType);
	}

	private void testStrategy() {
		BarSeriesWrapper barSeriesWrapper = getBarSeriesFromCSV("src/test/resources/niftybank_three_min_06_02_2023.csv");

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
		IMACDOptionsBuyStrategy strategy = new IMACDOptionsBuyStrategy(barSeriesWrapper, subscription, null);

		testStrategy(barSeriesWrapper, strategy.getStrategy());
	}

	public static void main(String[] args) {
		AggregationType aggregationType = new AggregationType();
		aggregationType.setName("THREE_MINUTES");
		aggregationType.setDuration(180);
		aggregationType.setAggregable(true);

		IMACDStrategyBackTest backTest = new IMACDStrategyBackTest(260105, "06-02-2023 09:15:00", "09-02-2023 15:30:00",
				aggregationType);
		backTest.testStrategy();
	}

}

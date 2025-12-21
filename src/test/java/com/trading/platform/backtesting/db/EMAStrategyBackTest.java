package com.trading.platform.backtesting.db;

import org.ta4j.core.Strategy;

import com.trading.platform.backtesting.AbstractBackTest;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.trading.strategies.ExponentialMovingAverageStrategy;

public class EMAStrategyBackTest extends AbstractBackTest {

	public EMAStrategyBackTest(long token, String startTime, String endTime, AggregationType aggregationType) {
		super(token, startTime, endTime, aggregationType);
	}

	private void testStrategy() {
		BarSeriesWrapper barSeriesWrapper = getBarSeriesFromDB();

		// Building the trading strategy
		Strategy strategy = new ExponentialMovingAverageStrategy(barSeriesWrapper).getStrategy();

		testStrategy(barSeriesWrapper, strategy);
	}

	public static void main(String[] args) {
		AggregationType aggregationType = new AggregationType();
		aggregationType.setName("FIVE_MINUTES");
		aggregationType.setDuration(300);
		aggregationType.setAggregable(true);

		EMAStrategyBackTest backTest = new EMAStrategyBackTest(3465729, "2022-12-01 09:15:00", "2022-12-16 15:30:00",
				aggregationType);
		backTest.testStrategy();
	}

}

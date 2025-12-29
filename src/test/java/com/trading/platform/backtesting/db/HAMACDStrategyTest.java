package com.trading.platform.backtesting.db;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.trading.platform.backtesting.AbstractBackTest;
import com.trading.platform.backtesting.TestUtil;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.trading.strategies.HAMACDOptionsBuyStrategy;
import com.trading.platform.util.BarSeriesUtil;

public class HAMACDStrategyTest extends AbstractBackTest {

	protected HAMACDStrategyTest(long token, String startTime, String endTime, AggregationType aggregationType) {
		super(token, startTime, endTime, aggregationType);
	}

	@Override
	public BarSeriesWrapper getBarSeriesFromCSV(String csvFile) {
		HAMACDOptionsBuyStrategy strategy = new HAMACDOptionsBuyStrategy(barSeriesWrapper);
		try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
			String[] line;
			reader.readNext(); // skip header
			while ((line = reader.readNext()) != null) {
				Instant barStartTime = BarSeriesUtil.getStartTime(line[0],
						CSV_DATE_FORMATTER);
				Instant barEndTime = BarSeriesUtil.getEndTime(barStartTime,
						TestUtil.getDuration(aggregationType.getName()));
				Bar bar = new BaseBar(
					    Duration.ofSeconds(aggregationType.getDuration()),
					    barStartTime,
					    barEndTime,
					    series.numFactory().numOf(Double.valueOf(line[1])),
					    series.numFactory().numOf(Double.valueOf(line[2])),
					    series.numFactory().numOf(Double.valueOf(line[3])),
					    series.numFactory().numOf(Double.valueOf(line[4])),
					    series.numFactory().numOf(0),
					    series.numFactory().numOf(0),
					    0L
					);
				series.addBar(bar, true);
				
				Bar haBar = new BaseBar(
					    Duration.ofSeconds(aggregationType.getDuration()),
					    barStartTime,
					    barEndTime,
					    series.numFactory().numOf(Double.valueOf(line[5])),
					    series.numFactory().numOf(Double.valueOf(line[6])),
					    series.numFactory().numOf(Double.valueOf(line[7])),
					    series.numFactory().numOf(Double.valueOf(line[8])),
					    series.numFactory().numOf(0),
					    series.numFactory().numOf(0),
					    0L
					);
				haSeries.addBar(haBar, true);
				System.out.println(line[0] + " : " + strategy.shouldBuyCE() + " : " + strategy.shouldBuyPE() + " : "
						+ strategy.shouldSellCE() + " : " + strategy.shouldSellPE());
			}
		} catch (CsvValidationException | IOException e) {
			e.printStackTrace();
		}

		return barSeriesWrapper;
	}

	private void testStrategy() {
		BarSeriesWrapper barSeriesWrapper = getBarSeriesFromCSV("src/test/resources/niftybank-3minutes-2023-03-27.csv");

		// Building the trading strategy
		HAMACDOptionsBuyStrategy strategy = new HAMACDOptionsBuyStrategy(barSeriesWrapper);

		testStrategy(barSeriesWrapper, strategy.getStrategy());
	}

	public static void main(String[] args) {
		AggregationType aggregationType = new AggregationType();
		aggregationType.setName("THREE_MINUTES");
		aggregationType.setDuration(180);
		aggregationType.setAggregable(true);

		HAMACDStrategyTest backTest = new HAMACDStrategyTest(260105, "27-03-2023 09:15:00", "27-03-2023 15:30:00",
				aggregationType);
		backTest.testStrategy();
	}
}

package com.trading.platform.backtesting;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Position;
import org.ta4j.core.Strategy;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.criteria.pnl.ReturnCriterion;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.service.series.BarSeriesWrapperBuilder;
import com.trading.platform.util.BarSeriesUtil;

public abstract class AbstractBackTest implements BackTestInterface {

	private long token;

	private String startTime;

	private String endTime;

	protected AggregationType aggregationType;

	protected BarSeriesWrapper barSeriesWrapper;
	
	protected BarSeries series;
	
	protected BarSeries haSeries;

	protected AbstractBackTest(long token, String startTime, String endTime, AggregationType aggregationType) {
		this.token = token;
		this.startTime = startTime;
		this.endTime = endTime;
		this.aggregationType = aggregationType;

		InstrumentIndicators instrumentIndicator = new InstrumentIndicators();
		instrumentIndicator.setToken(token);
		this.barSeriesWrapper = new BarSeriesWrapperBuilder(aggregationType, instrumentIndicator)
				.build();
		this.series = barSeriesWrapper.getSeries();
		this.haSeries = barSeriesWrapper.getHaSeries();
	}

	@Override
	public BarSeriesWrapper getBarSeriesFromDB() {
		try (Connection connection = DriverManager.getConnection(POSTGRES_URL);
				Statement statement = connection.createStatement()) {
			String query = TestUtil.getQuery(token, startTime, endTime, aggregationType.getName());
			ResultSet result = statement.executeQuery(query);
			while (result.next()) {
				ZonedDateTime barStartTime = BarSeriesUtil.getStartTime(result.getString(1),
						CSV_DATE_FORMATTER);
				series.addBar(BarSeriesUtil.getEndTime(barStartTime, TestUtil.getDuration(aggregationType.getName())),
						result.getBigDecimal(3), result.getBigDecimal(4), result.getBigDecimal(5),
						result.getBigDecimal(6), result.getLong(12));
				haSeries.addBar(BarSeriesUtil.getEndTime(barStartTime, TestUtil.getDuration(aggregationType.getName())),
						result.getBigDecimal(7), result.getBigDecimal(8), result.getBigDecimal(9),
						result.getBigDecimal(10), result.getLong(12));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return barSeriesWrapper;
	}

	public BarSeriesWrapper getBarSeriesFromCSV(String csvFile) {
		try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
			String[] line;
			reader.readNext(); // skip header
			while ((line = reader.readNext()) != null) {
				ZonedDateTime barStartTime = BarSeriesUtil.getStartTime(line[0],
						CSV_DATE_FORMATTER);
				ZonedDateTime barEndTime = BarSeriesUtil.getEndTime(barStartTime,
						TestUtil.getDuration(aggregationType.getName()));
				series.addBar(barEndTime, Double.valueOf(line[1]),
						Double.valueOf(line[2]), Double.valueOf(line[3]),
						Double.valueOf(line[4]));
				haSeries.addBar(barEndTime, Double.valueOf(line[5]),
						Double.valueOf(line[6]), Double.valueOf(line[7]),
						Double.valueOf(line[8]));
			}
		} catch (CsvValidationException | IOException e) {
			e.printStackTrace();
		}

		return barSeriesWrapper;
	}

	@Override
	public void testStrategy(BarSeriesWrapper barSeriesWrapper, Strategy strategy) {
		// Running the strategy
		BarSeriesManager seriesManager = new BarSeriesManager(barSeriesWrapper.getSeries());
		TradingRecord tradingRecord = seriesManager.run(strategy);
		printPositions(tradingRecord);
	}

	@Override
	@SuppressWarnings("java:S106")
	public void printPositions(TradingRecord tradingRecord) {
		List<Position> positions = tradingRecord.getPositions();
		if (!positions.isEmpty()) {
			String format = "| %-13s | %-30s | %-12s | %-30s | %-12s | %-10s | %-12s |%n";
			System.out.println(StringUtils.repeat("-", 141));
			System.out.printf(format, "STARTING TYPE", "ENTRY TIME", "BUY PRICE", "EXIT TIME",
					"SELL PRICE", "CLOSED?", "GROSS PROFIT");
			System.out.println(StringUtils.repeat("-", 141));

			for (Position position : positions) {
				Date entryTime = Date.from(series.getBar(position.getEntry().getIndex()).getEndTime()
						.toInstant().truncatedTo(ChronoUnit.MINUTES));
				Date exitTime = Date.from(series.getBar(position.getExit().getIndex()).getEndTime()
						.toInstant().truncatedTo(ChronoUnit.MINUTES));
				System.out.printf(format, position.getStartingType(), entryTime, position.getEntry().getValue(),
						exitTime, position.getExit().getValue(),
						position.isClosed(), position.getGrossProfit());
			}

			Position currPostion = tradingRecord.getCurrentPosition();
			if (currPostion != null && currPostion.getEntry() != null) {
				Date entryTime = Date.from(series.getBar(currPostion.getEntry().getIndex()).getEndTime()
						.toInstant().truncatedTo(ChronoUnit.MINUTES));
				System.out.printf(format, currPostion.getStartingType(), entryTime, currPostion.getEntry().getValue(),
						"", "", currPostion.isClosed(), "");
			}

			System.out.println(StringUtils.repeat("-", 141));
		}

		System.out.println("Gross Return : " +
				new ReturnCriterion().calculate(series, tradingRecord));
	}

}

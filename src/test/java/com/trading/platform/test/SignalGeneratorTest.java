package com.trading.platform.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import com.trading.platform.backtesting.TestUtil;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.service.series.BarSeriesWrapperBuilder;
import com.trading.platform.service.signal.FiveMinuteSignalGenerator;
import com.trading.platform.service.signal.SignalGenerator;
import com.trading.platform.util.BarSeriesUtil;

public class SignalGeneratorTest {

	private static final String POSTGRES_URL = "jdbc:postgresql:instruments?user=postgres&password=postgres";

	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static AggregationType aggregationType;

	public static final long token = 260105;

	public static void main(String[] args) {
		aggregationType = new AggregationType();
		aggregationType.setName("FIVE_MINUTES");
		aggregationType.setDuration(300);
		aggregationType.setAggregable(true);
		
		InstrumentIndicators instrumentIndicator = new InstrumentIndicators();
		instrumentIndicator.setToken(token);

		BarSeriesWrapper barSeriesWrapper = new BarSeriesWrapperBuilder(aggregationType, instrumentIndicator)
				.build();

		SignalGenerator signalGenerator = new FiveMinuteSignalGenerator();
		try (Connection connection = DriverManager.getConnection(POSTGRES_URL);
				Statement statement = connection.createStatement()) {
			String query = TestUtil.getQuery(token,
					"2022-12-23 09:15:00", "2022-12-23 15:25:00", aggregationType.getName());
			System.out.println(query);
			ResultSet result = statement.executeQuery(query);
			while (result.next()) {
				System.out.println(result.getString(1));
				InstrumentIndicators indicator = new InstrumentIndicators();
				indicator.setTickTime(DATE_FORMATTER.parse(result.getString(1)));
				indicator.setToken(Long.valueOf(result.getString(2)));
				indicator.setName(result.getString(3));
				indicator.setOpenPrice(Double.valueOf(result.getString(4)));
				indicator.setHighPrice(Double.valueOf(result.getString(5)));
				indicator.setLowPrice(Double.valueOf(result.getString(6)));
				indicator.setClosePrice(Double.valueOf(result.getString(7)));

				BarSeriesUtil.updateBarSeries(barSeriesWrapper, indicator, aggregationType);

				signalGenerator.addSeries(barSeriesWrapper, indicator, aggregationType);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}

package com.trading.platform.backtesting;

import java.text.SimpleDateFormat;

import org.ta4j.core.Strategy;
import org.ta4j.core.TradingRecord;

import com.trading.platform.service.series.BarSeriesWrapper;

public interface BackTestInterface {

	public final String POSTGRES_URL = "jdbc:postgresql:instruments?user=postgres&password=postgres";
	
	public final SimpleDateFormat CSV_DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	public abstract BarSeriesWrapper getBarSeriesFromDB();

	public abstract void testStrategy(BarSeriesWrapper barSeriesWrapper, Strategy strategy);

	public abstract void printPositions(TradingRecord tradingRecord);

}

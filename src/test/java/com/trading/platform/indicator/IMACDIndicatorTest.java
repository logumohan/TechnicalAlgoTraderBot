package com.trading.platform.indicator;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.averages.ZLEMAIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.num.DecimalNumFactory;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.backtesting.TestUtil;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.trading.indicator.HLC3Indicator;
import com.trading.platform.trading.indicator.ImpulseMACDIndicator;
import com.trading.platform.trading.indicator.SMMAIndicator;
import com.trading.platform.util.BarSeriesUtil;

public class IMACDIndicatorTest {

	private static final SimpleDateFormat CSV_DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	private static final DecimalFormat FORMATTER = new DecimalFormat("0.00");

	public static void main(String[] args) {
		BarSeries series = new BaseBarSeriesBuilder()
				.withMaxBarCount(500)
				.withName("Hammer-Test-Series")
				.withNumFactory(DecimalNumFactory.getInstance())
				.build();
		try (CSVReader reader = new CSVReader(
				new FileReader("src/test/resources/niftybank-3minutes-2023-03-24.csv"))) {
			String[] line;
			reader.readNext(); // skip header

			InstrumentIndicators prevIndicator = new InstrumentIndicators();
			prevIndicator.setToken(230165);
			prevIndicator.setName("NIFTY BANK");
			prevIndicator.setOpenPrice(39571.60);
			prevIndicator.setHighPrice(39616.90);
			prevIndicator.setLowPrice(39571.60);
			prevIndicator.setClosePrice(39616.90);
			prevIndicator.setSmmaHp34(39903.25);
			prevIndicator.setSmmaLp34(39852.05);
			prevIndicator.setZlema34(39571.7);
			
			while ((line = reader.readNext()) != null) {
				Instant barStartTime = BarSeriesUtil.getStartTime(line[0],
						CSV_DATE_FORMATTER);
				Instant barEndTime = BarSeriesUtil.getEndTime(barStartTime,
						TestUtil.getDuration(SignalGeneratorConstants.THREE_MINUTES));
				
				Bar bar = new BaseBar(
						TestUtil.getDuration(SignalGeneratorConstants.THREE_MINUTES),
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

				ImpulseMACDIndicator imacdIndicator = new ImpulseMACDIndicator(series, 34, prevIndicator);
				double imacd = Double
						.valueOf(FORMATTER.format(imacdIndicator.getValue(series.getEndIndex()).doubleValue()));

				double smmaHp34 = -1;
				double smmaLp34 = -1;
				if (prevIndicator != null) {
					smmaHp34 = prevIndicator.getSmmaHp34();
					smmaLp34 = prevIndicator.getSmmaLp34();
				}

				prevIndicator.setToken(230165);
				prevIndicator.setName("NIFTY BANK");
				prevIndicator.setTickTime(Date.from(barStartTime));
				prevIndicator.setOpenPrice(series.getLastBar().getOpenPrice().doubleValue());
				prevIndicator.setHighPrice(series.getLastBar().getHighPrice().doubleValue());
				prevIndicator.setLowPrice(series.getLastBar().getLowPrice().doubleValue());
				prevIndicator.setClosePrice(series.getLastBar().getClosePrice().doubleValue());
				prevIndicator.setSmmaHp34(
						Double.valueOf(FORMATTER.format(new SMMAIndicator(new HighPriceIndicator(series), 34, smmaHp34)
								.getValue(series.getEndIndex()).doubleValue())));
				prevIndicator.setSmmaLp34(
						Double.valueOf(FORMATTER.format(new SMMAIndicator(new LowPriceIndicator(series), 34, smmaLp34)
								.getValue(series.getEndIndex()).doubleValue())));
				prevIndicator.setZlema34(Double.valueOf(FORMATTER
						.format(new ZLEMAIndicator(new HLC3Indicator(series), 34).getValue(series.getEndIndex())
								.doubleValue())));

				System.out.println(line[0] + ", " + imacd + ", " + prevIndicator.getSmmaHp34() + ", "
						+ prevIndicator.getSmmaLp34() + ", " + prevIndicator.getZlema34());
			}
		} catch (CsvValidationException | IOException e) {
			e.printStackTrace();
		}
	}

}

package com.trading.platform.indicator;

import java.time.Duration;
import java.time.Instant;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DecimalNumFactory;

import com.trading.platform.trading.indicator.BearishHammerIndicator;
import com.trading.platform.trading.indicator.BullishHammerIndicator;

public class HammerIndicatorTest {

	public static void main(String[] args) {
		BarSeries barSeries = new BaseBarSeriesBuilder()
				.withName("Hammer-Test-Series")
				.withNumFactory(DecimalNumFactory.getInstance())
				.build();
		
		Bar bar = new BaseBar(
			    Duration.ofSeconds(180),
			    Instant.now(),
			    Instant.now().plusMillis(180),
			    barSeries.numFactory().numOf(102),
			    barSeries.numFactory().numOf(105),
			    barSeries.numFactory().numOf(96),
			    barSeries.numFactory().numOf(104),
			    barSeries.numFactory().numOf(0),
			    barSeries.numFactory().numOf(0),
			    0L
			);
		barSeries.addBar(bar, true);
		
		BullishHammerIndicator indicator = new BullishHammerIndicator(barSeries);
		System.out.println(indicator.getValue(barSeries.getEndIndex()));
		
		Bar bar2 = new BaseBar(
			    Duration.ofSeconds(180),
			    Instant.now(),
			    Instant.now().plusMillis(180),
			    barSeries.numFactory().numOf(102),
			    barSeries.numFactory().numOf(110),
			    barSeries.numFactory().numOf(100),
			    barSeries.numFactory().numOf(104),
			    barSeries.numFactory().numOf(0),
			    barSeries.numFactory().numOf(0),
			    0L
			);
		barSeries.addBar(bar2, true);
		
		BearishHammerIndicator indicator2 = new BearishHammerIndicator(barSeries);
		System.out.println(indicator2.getValue(barSeries.getEndIndex()));
	}

}

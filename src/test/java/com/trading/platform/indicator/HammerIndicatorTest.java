package com.trading.platform.indicator;

import java.time.ZonedDateTime;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DecimalNum;

import com.trading.platform.trading.indicator.BearishHammerIndicator;
import com.trading.platform.trading.indicator.BullishHammerIndicator;

public class HammerIndicatorTest {

	public static void main(String[] args) {
		BarSeries barSeries = new BaseBarSeriesBuilder()
				.withName("Hammer-Test-Series")
				.withNumTypeOf(DecimalNum.class).build();
		
		barSeries.addBar(ZonedDateTime.now(), 102, 105, 96, 104);
		BullishHammerIndicator indicator = new BullishHammerIndicator(barSeries);
		System.out.println(indicator.getValue(barSeries.getEndIndex()));
		
		barSeries.addBar(ZonedDateTime.now(), 102, 110, 100, 104);
		BearishHammerIndicator indicator2 = new BearishHammerIndicator(barSeries);
		System.out.println(indicator2.getValue(barSeries.getEndIndex()));
	}

}

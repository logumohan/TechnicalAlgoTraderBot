package com.trading.platform.service.series;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort.Direction;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.num.DecimalNum;

import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.persistence.IndicatorsRepository;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.util.BarSeriesUtil;
import com.trading.platform.util.SignalGeneratorUtil;

public class BarSeriesWrapper {

	private static final Logger LOGGER = LogManager.getLogger(BarSeriesWrapper.class);

	private AggregationType aggregationType;

	private InstrumentIndicators indicator;

	private BarSeries series;

	private BarSeries haSeries;

	public BarSeriesWrapper(AggregationType aggregationType, InstrumentIndicators indicator) {
		this.aggregationType = aggregationType;
		this.indicator = indicator;

		series = new BaseBarSeriesBuilder()
				.withName(BarSeriesUtil.getBarSeriesName(aggregationType, indicator, false))
				.withNumTypeOf(DecimalNum.class)
				.withMaxBarCount(SignalGeneratorConstants.MAX_BAR_COUNT)
				.build();

		haSeries = new BaseBarSeriesBuilder()
				.withName(BarSeriesUtil.getBarSeriesName(aggregationType, indicator, true))
				.withNumTypeOf(DecimalNum.class)
				.withMaxBarCount(SignalGeneratorConstants.MAX_BAR_COUNT)
				.build();
	}

	public void populateData(IndicatorsRepository repository) {
		try {
			List<InstrumentIndicators> indicatorsList = repository.findOrderedByTickTimeLimitedTo(
					SignalGeneratorUtil.getIndicatorClazz(aggregationType), indicator.getToken(), "tickTime",
					Direction.DESC, SignalGeneratorConstants.MAX_BAR_COUNT + 1);

			if (!indicatorsList.isEmpty()) {
				// Skip the most recent sample as this may not be fully aggregated
				indicatorsList.remove(0);
			}

			Collections.reverse(indicatorsList);
			indicatorsList = BarSeriesUtil.discardNonContinousSeriesData(indicatorsList, aggregationType);
			for (int index = 0; index < indicatorsList.size(); index++) {
				InstrumentIndicators instrumentIndicator = indicatorsList.get(index);
				LOGGER.info("Updating the bar series {}", series.getName());
				BarSeriesUtil.updateBarSeries(series, instrumentIndicator, aggregationType, false);
				LOGGER.info("Updating the HA bar series {}", series.getName());
				BarSeriesUtil.updateBarSeries(haSeries, instrumentIndicator, aggregationType, true);
			}

			LOGGER.info("Bar series for \"{}\" of aggregation type {} is initialized with {} most recent samples",
					indicator.getName(), aggregationType.getName(), indicatorsList.size());

		} catch (Exception e) {
			LOGGER.error("Error in populating the data for aggregation - {} and instrument - {}",
					aggregationType.getName(), indicator.getName(), e);
			e.printStackTrace();
		}
	}
	
	public String dumpSeries() {
		return getSeries().getBarData().stream().map((Bar bar) ->
			String.format("\"%1s\", %2$f, %3$f, %4$f, %5$f, %6$f}",
				bar.getBeginTime().withZoneSameInstant(ZoneId.systemDefault()),
				bar.getOpenPrice().doubleValue(),
				bar.getHighPrice().doubleValue(),
				bar.getLowPrice().doubleValue(),
				bar.getClosePrice().doubleValue(),
				bar.getVolume().doubleValue())).collect(Collectors.joining("\n"));
	}
	
	public String dumpHaSeries() {
		return getHaSeries().getBarData().stream().map((Bar bar) ->
			String.format("\"%1s\", %2$f, %3$f, %4$f, %5$f, %6$f}",
				bar.getBeginTime().withZoneSameInstant(ZoneId.systemDefault()),
				bar.getOpenPrice().doubleValue(),
				bar.getHighPrice().doubleValue(),
				bar.getLowPrice().doubleValue(),
				bar.getClosePrice().doubleValue(),
				bar.getVolume().doubleValue())).collect(Collectors.joining("\n"));
	}

	public BarSeries getSeries() {
		return series;
	}

	public BarSeries getHaSeries() {
		return haSeries;
	}

}

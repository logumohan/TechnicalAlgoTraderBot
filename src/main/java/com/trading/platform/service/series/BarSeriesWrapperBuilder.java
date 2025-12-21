package com.trading.platform.service.series;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.trading.platform.persistence.IndicatorsRepository;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.InstrumentIndicators;

public class BarSeriesWrapperBuilder {

	private static final Logger LOGGER = LogManager.getLogger(BarSeriesWrapperBuilder.class);

	private AggregationType aggregationType;

	private InstrumentIndicators indicator;

	private IndicatorsRepository repository;

	public BarSeriesWrapperBuilder(AggregationType aggregationType, InstrumentIndicators indicator) {
		this.aggregationType = aggregationType;
		this.indicator = indicator;
	}

	public BarSeriesWrapperBuilder withIndicatorRepository(IndicatorsRepository repository) {
		this.repository = repository;
		return this;
	}

	public BarSeriesWrapper build() {
		LOGGER.info("Building bar series wrapper for aggregation type - {} and instrument - {}",
				aggregationType.getName(), indicator.getName());
		BarSeriesWrapper wrapper = new BarSeriesWrapper(aggregationType, indicator);
		if (repository != null) {
			wrapper.populateData(repository);
		}

		return wrapper;
	}

}

package com.trading.platform.service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.persistence.AggregationTypeRepository;
import com.trading.platform.persistence.IndicatorsRepository;
import com.trading.platform.persistence.InstrumentsViewRepository;
import com.trading.platform.persistence.SubscriptionReadOnlyRepository;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.DailyInstrumentIndicators;
import com.trading.platform.persistence.entity.FifteenMinutesInstrumentIndicators;
import com.trading.platform.persistence.entity.FiveMinutesInstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.OneHourInstrumentIndicators;
import com.trading.platform.persistence.entity.OneMinuteInstrumentIndicators;
import com.trading.platform.persistence.entity.ThreeMinutesInstrumentIndicators;
import com.trading.platform.persistence.entity.views.InstrumentView;
import com.trading.platform.service.indicators.DailyIndicatorService;
import com.trading.platform.service.indicators.FifteenMinutesIndicatorService;
import com.trading.platform.service.indicators.FiveMinutesIndicatorService;
import com.trading.platform.service.indicators.OneHourIndicatorService;
import com.trading.platform.service.indicators.OneMinuteIndicatorService;
import com.trading.platform.service.indicators.ThreeMinutesIndicatorService;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.service.series.BarSeriesWrapperBuilder;
import com.trading.platform.util.BarSeriesUtil;
import com.trading.platform.util.MarketTimeUtil;
import com.trading.platform.util.SignalGeneratorUtil;

@Component
@Configuration
public class AggregatedTicksHandler implements AutoCloseable {

	private static final Logger LOGGER = LogManager.getLogger(AggregatedTicksHandler.class);

	private boolean flag = true;

	@Autowired
	private OneMinuteIndicatorService oneMinuteIndicatorService;

	@Autowired
	private ThreeMinutesIndicatorService threeMinutesIndicatorService;

	@Autowired
	private FiveMinutesIndicatorService fiveMinutesIndicatorService;

	@Autowired
	private FifteenMinutesIndicatorService fifteenMinutesIndicatorService;

	@Autowired
	private OneHourIndicatorService oneHourIndicatorService;

	@Autowired
	private DailyIndicatorService dailyIndicatorService;

	@Autowired
	private InstrumentsViewRepository repository;

	@Autowired
	private IndicatorsRepository indicatorsRepository;

	@Autowired
	private SubscriptionReadOnlyRepository subscriptionRepository;

	@Bean(name = "taskExecutor")
	public TaskExecutor taskExecutor() {
		return new SimpleAsyncTaskExecutor(new BasicThreadFactory.Builder()
				.namingPattern("tick-producer-%d")
				.uncaughtExceptionHandler((Thread thread, Throwable e) -> LOGGER
						.fatal("Uncaught exception in aggregated tick producer - {}", thread.getName(), e))
				.daemon(true)
				.priority(Thread.NORM_PRIORITY)
				.build());
	}

	@Bean(name = "tsTickProducer")
	public CommandLineRunner schedulingTickProducer(@Qualifier("taskExecutor") TaskExecutor executor,
			@Autowired AggregationTypeRepository repository) {
		return new CommandLineRunner() {
			public void run(String... args) throws Exception {
				List<AggregationType> aggregationList = repository.findAll();
				aggregationList.stream().filter(AggregationType::isAggregable).forEach(
						aggregationType -> executor.execute(new TickProducer(aggregationType)));
			}
		};
	}

	class TickProducer implements Runnable {

		private AggregationType aggregationType;

		private Map<String, Date> previousTickTime;

		private Map<String, BarSeriesWrapper> barSeriesMap;

		private DecimalFormat decimalFormat;

		public TickProducer(AggregationType aggregationType) {
			this.barSeriesMap = new LinkedHashMap<>();
			this.aggregationType = aggregationType;
			this.previousTickTime = new LinkedHashMap<>();
			this.decimalFormat = new DecimalFormat("0.00");
			this.decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
		}

		private BarSeriesWrapper getDefaultBarSeriesWrapper(InstrumentIndicators instrumentIndicators,
				AggregationType aggregationType) {
			return new BarSeriesWrapperBuilder(aggregationType, instrumentIndicators)
					.withIndicatorRepository(indicatorsRepository)
					.build();
		}

		private void publishTicksToIndicatorService(InstrumentIndicators instrumentIndicators) {
			try (CloseableThreadContext.Instance context = CloseableThreadContext
					.put("instrument-name", instrumentIndicators.getName().toUpperCase())
					.put("token", String.valueOf(instrumentIndicators.getToken()))
					.put("duration", aggregationType.getName())) {
				if (!barSeriesMap.containsKey(instrumentIndicators.getName())) {
					LOGGER.info("Creating bar series for aggregation type - {}", aggregationType.getName());
					barSeriesMap.putIfAbsent(instrumentIndicators.getName(),
							getDefaultBarSeriesWrapper(instrumentIndicators, aggregationType));
					LOGGER.info("Bar series for aggregation type - {} is created", aggregationType.getName());
				}
				BarSeriesWrapper barSeriesWrapper = barSeriesMap.get(instrumentIndicators.getName());
				updateHAOHLC(barSeriesWrapper, instrumentIndicators);
				LOGGER.info("HA OHLC is updated for aggregation type - {}", aggregationType.getName());
				BarSeriesUtil.updateBarSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
				LOGGER.info("New tick is updated to the bar for aggregation type - {}", aggregationType.getName());

				switch (aggregationType.getName()) {
				case SignalGeneratorConstants.ONE_MINUTE:
					oneMinuteIndicatorService.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
					break;
				case SignalGeneratorConstants.THREE_MINUTES:
					threeMinutesIndicatorService.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
					break;
				case SignalGeneratorConstants.FIVE_MINUTES:
					fiveMinutesIndicatorService.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
					break;
				case SignalGeneratorConstants.FIFTEEN_MINUTES:
					fifteenMinutesIndicatorService.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
					break;
				case SignalGeneratorConstants.ONE_HOUR:
					oneHourIndicatorService.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
					break;
				case SignalGeneratorConstants.ONE_DAY:
					dailyIndicatorService.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
					break;
				default:
					break;
				}
			}
		}

		private void updateHAOHLC(BarSeriesWrapper barSeriesWrapper, InstrumentIndicators instrumentIndicators) {
			BarSeries haSeries = barSeriesWrapper.getHaSeries();
			if (haSeries.getBarCount() > 0) {
				instrumentIndicators.setHaClosePrice(
						Double.valueOf(decimalFormat.format((instrumentIndicators.getOpenPrice() +
						instrumentIndicators.getHighPrice() + instrumentIndicators.getLowPrice() +
						instrumentIndicators.getClosePrice()) / 4)));

				instrumentIndicators.setHaOpenPrice(Double.valueOf(decimalFormat.format(
						(haSeries.getLastBar().getOpenPrice().doubleValue()
								+ haSeries.getLastBar().getClosePrice().doubleValue()) / 2)));

				instrumentIndicators.setHaHighPrice(Double.valueOf(decimalFormat.format(Math.max(
						instrumentIndicators.getHighPrice(),
						Math.max(instrumentIndicators.getHaOpenPrice(), instrumentIndicators.getHaClosePrice())))));

				instrumentIndicators.setHaLowPrice(Double.valueOf(decimalFormat.format(Math.min(
						instrumentIndicators.getLowPrice(),
						Math.min(instrumentIndicators.getHaOpenPrice(), instrumentIndicators.getHaClosePrice())))));

			} else {
				instrumentIndicators.setHaOpenPrice(instrumentIndicators.getOpenPrice());
				instrumentIndicators.setHaHighPrice(instrumentIndicators.getHighPrice());
				instrumentIndicators.setHaLowPrice(instrumentIndicators.getLowPrice());
				instrumentIndicators.setHaClosePrice(instrumentIndicators.getClosePrice());
			}
		}

		private InstrumentIndicators convertToInstrumentIndicators(InstrumentView instrumentView) {
			InstrumentIndicators instrumentIndicators = new InstrumentIndicators();
			switch (aggregationType.getName()) {
			case SignalGeneratorConstants.ONE_MINUTE:
				instrumentIndicators = new OneMinuteInstrumentIndicators();
				break;
			case SignalGeneratorConstants.THREE_MINUTES:
				instrumentIndicators = new ThreeMinutesInstrumentIndicators();
				break;
			case SignalGeneratorConstants.FIVE_MINUTES:
				instrumentIndicators = new FiveMinutesInstrumentIndicators();
				break;
			case SignalGeneratorConstants.FIFTEEN_MINUTES:
				instrumentIndicators = new FifteenMinutesInstrumentIndicators();
				break;
			case SignalGeneratorConstants.ONE_HOUR:
				instrumentIndicators = new OneHourInstrumentIndicators();
				break;
			case SignalGeneratorConstants.ONE_DAY:
				instrumentIndicators = new DailyInstrumentIndicators();
				break;
			default:
				break;
			}
			instrumentIndicators.setTickTime(instrumentView.getBucketTickTime());
			instrumentIndicators.setToken(instrumentView.getToken());
			instrumentIndicators.setName(instrumentView.getName());
			instrumentIndicators.setOpenPrice(instrumentView.getOpenPrice());
			instrumentIndicators.setHighPrice(instrumentView.getHighPrice());
			instrumentIndicators.setLowPrice(instrumentView.getLowPrice());
			instrumentIndicators.setClosePrice(instrumentView.getClosePrice());
			instrumentIndicators.setLastTradedPrice(instrumentView.getLastTradedPrice());
			instrumentIndicators.setVolumeTraded(instrumentView.getVolumeTraded());
			instrumentIndicators.setTotalBuyQuantity(instrumentView.getTotalBuyQuantity());
			instrumentIndicators.setTotalSellQuantity(instrumentView.getTotalSellQuantity());

			return instrumentIndicators;
		}

		@LogExecutionTime
		private void publishLatestTimeSeriesForAllInstruments() {
			List<Long> tokens = subscriptionRepository.getAllTokens();
			LOGGER.debug("Unique token retrieved from database for series {} is {}",
					aggregationType.getName(), tokens);
			for (Long token : tokens) {
				List<? extends InstrumentView> instrumentViewList = repository
						.findOrderedByTickTimeLimitedTo(SignalGeneratorUtil.getInstrumentViewClazz(aggregationType),
								token, "bucketTickTime", Direction.DESC, 2);
				LOGGER.debug("View - {}, Count - {}", aggregationType.getName(),
						instrumentViewList.size());

				if (instrumentViewList.size() > 1) {
					InstrumentView instrumentView = instrumentViewList.get(1);
					if (previousTickTime.get(String.valueOf(instrumentView.getToken())) == null
							|| instrumentView.getBucketTickTime()
									.after(previousTickTime.get(String.valueOf(instrumentView.getToken())))) {
						publishTicksToIndicatorService(convertToInstrumentIndicators(instrumentView));
						previousTickTime.put(String.valueOf(instrumentView.getToken()),
								instrumentView.getBucketTickTime());
					}
				}
			}
		}

		public void waitTillMarketOpen() {
			while (MarketTimeUtil.isMarketClosed()) {
				LOGGER.trace("TickProducer: Waiting to process the aggregated ticks as market is closed now, {}",
						ZonedDateTime.now());
				try {
					Thread.sleep(60 * 1000L);
				} catch (InterruptedException e) {
					LOGGER.error("Error while waiting till market is opened", e);
					Thread.currentThread().interrupt();
				}
			}
		}

		@Override
		public void run() {
			try (CloseableThreadContext.Instance context = CloseableThreadContext
					.put("duration", aggregationType.getName())) {
				LOGGER.info("Tick producer for the aggregation type {} is started", aggregationType.getName());
				while (flag) {
					waitTillMarketOpen();
					try {
						publishLatestTimeSeriesForAllInstruments();
					} catch (Exception e) {
						LOGGER.error("Error polling the time series data from db for view - {}",
								aggregationType.getName(), e);
					}

					try {
						Thread.sleep(5 * 1000L);
					} catch (Exception e) {
						Thread.currentThread().interrupt();
						LOGGER.error("Interrupted when waiting for the next polling of time series data for view - {}",
								aggregationType.getName(), e);
					}
				}
			}
		}

	}

	@Override
	public void close() throws Exception {
		this.flag = false;
	}

}

package com.trading.platform.service.indicators;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.StochasticRSIIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.averages.SMAIndicator;
import org.ta4j.core.indicators.averages.ZLEMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.notification.KafkaProducer;
import com.trading.platform.persistence.IndicatorsRepository;
import com.trading.platform.persistence.InstrumentIndicatorsRepository;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.service.signal.DailySignalGenerator;
import com.trading.platform.service.signal.FifteenMinuteSignalGenerator;
import com.trading.platform.service.signal.FiveMinuteSignalGenerator;
import com.trading.platform.service.signal.OneHourSignalGenerator;
import com.trading.platform.service.signal.OneMinuteSignalGenerator;
import com.trading.platform.service.signal.ThreeMinuteSignalGenerator;
import com.trading.platform.service.trade.OptionTickConsumer;
import com.trading.platform.trading.indicator.HLC3Indicator;
import com.trading.platform.trading.indicator.ImpulseMACDIndicator;
import com.trading.platform.trading.indicator.SMMAIndicator;
import com.trading.platform.util.SignalGeneratorUtil;

public abstract class IndicatorService {

	private static final Logger LOGGER = LogManager.getLogger(IndicatorService.class);

	private static final int EMA10_BAR_COUNT = 10;

	private static final int EMA20_BAR_COUNT = 20;

	private static final int EMA30_BAR_COUNT = 30;

	private static final int EMA50_BAR_COUNT = 50;

	private static final int MACD_SHORT_EMA_BAR_COUNT = 12;

	private static final int MACD_LONG_EMA_BAR_COUNT = 26;

	private static final int MACD_SIGNAL_EMA_BAR_COUNT = 9;

	private static final int IMACD_SMA_BAR_COUNT = 34;

	private static final int SMMA_BAR_COUNT = 34;

	private static final int ZLEMA_BAR_COUNT = 34;
	
	private static final int IMACD_SIGNAL_SMA_BAR_COUNT = 9;

	private static final int RSI_BAR_COUNT = 14;

	private static final int ATR_BAR_COUNT = 21;

	private DecimalFormat decimalFormat;

	@Autowired
	private InstrumentIndicatorsRepository instrumentIndicatorsRepository;

	@Autowired
	private IndicatorsRepository indicatorsRepository;

	@Autowired
	private OneMinuteSignalGenerator oneMinuteSignalGenerator;

	@Autowired
	private ThreeMinuteSignalGenerator threeMinuteSignalGenerator;

	@Autowired
	private FiveMinuteSignalGenerator fiveMinuteSignalGenerator;

	@Autowired
	private FifteenMinuteSignalGenerator fifteenMinuteSignalGenerator;

	@Autowired
	private OneHourSignalGenerator oneHourSignalGenerator;

	@Autowired
	private DailySignalGenerator dailySignalGenerator;

	@Autowired
	private KafkaProducer kafkaProducer;

	private Map<Long, List<OptionTickConsumer>> consumers;

	private Map<Long, List<OptionTickConsumer>> oneMinConsumers;

	private Set<Long> subscribedTokens;

	private Set<Long> oneMinSubscribedTokens;

	private Map<Long, InstrumentIndicators> previousIndicatorMap;

	protected IndicatorService() {
		this.consumers = new LinkedHashMap<>();
		this.oneMinConsumers = new LinkedHashMap<>();
		this.subscribedTokens = new HashSet<>();
		this.oneMinSubscribedTokens = new HashSet<>();
		this.previousIndicatorMap = new HashMap<>();
		this.decimalFormat = new DecimalFormat("0.00");
		this.decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
	}

	@Bean(name = "indicator-service")
	public TaskExecutor indicatorServiceTaskExecutor() {
		return new SimpleAsyncTaskExecutor(new BasicThreadFactory.Builder()
				.namingPattern("indicator-serivce-%d")
				.uncaughtExceptionHandler((Thread thread, Throwable e) -> LOGGER
						.fatal("Uncaught exception in indicator service - {}", thread.getName(), e))
				.daemon(true)
				.priority(Thread.NORM_PRIORITY)
				.build());
	}

	public void subscribeTicks(OptionTickConsumer consumer) {
		Long token = consumer.getToken();
		LOGGER.info("Subscribing for aggregated tick for the token - {}", token);
		if (token != null) {
			consumers.putIfAbsent(token, new ArrayList<>());
			consumers.get(token).add(consumer);
			subscribedTokens.add(token);
			LOGGER.info("Subscription of aggregated tick for the token - {} is successful", token);
		}
	}

	public void subscribeOneMinTicks(OptionTickConsumer consumer) {
		Long token = consumer.getToken();
		LOGGER.info("Subscribing for one minute aggregated tick for the token - {}", token);
		if (token != null) {
			oneMinConsumers.putIfAbsent(token, new ArrayList<>());
			oneMinConsumers.get(token).add(consumer);
			oneMinSubscribedTokens.add(token);
			LOGGER.info("Subscription of one minute aggregated tick for the token - {} is successful", token);
		}
	}

	public void unsubscribeTicks(OptionTickConsumer consumer) {
		Long token = consumer.getToken();
		LOGGER.info("Unsubscribing for aggregated tick for the token - {}", token);
		if (token != null && consumers.containsKey(token)) {
			consumers.get(token).remove(consumer);
			if (consumers.get(token).isEmpty()) {
				consumers.remove(token);
				subscribedTokens.remove(token);
				LOGGER.info("Unsubscription of aggregated tick for the token - {} is successful", token);
			}
		}
	}

	public void unsubscribeOneMinTicks(OptionTickConsumer consumer) {
		Long token = consumer.getToken();
		LOGGER.info("Unsubscribing one minute aggregated tick for the token - {}", token);
		if (token != null && oneMinConsumers.containsKey(token)) {
			oneMinConsumers.get(token).remove(consumer);
			if (oneMinConsumers.get(token).isEmpty()) {
				oneMinConsumers.remove(token);
				oneMinSubscribedTokens.remove(token);
				LOGGER.info("Unsubscription of one minute aggregated tick for the token - {} is successful", token);
			}
		}
	}

	private void publishTicksToConsumers(InstrumentIndicators instrumentIndicators, AggregationType aggregationType) {
		LOGGER.trace("{}, Attemping to publish the ticks to consumers", aggregationType);
		if (subscribedTokens.contains(instrumentIndicators.getToken())) {
			LOGGER.trace("{}: Publising ticks to consumers", aggregationType);
			consumers.get(instrumentIndicators.getToken()).stream()
					.forEach(consumer -> consumer.onAggregatedTick(instrumentIndicators));
		}
		LOGGER.trace("{}: One minute subscriptions : {}", aggregationType, oneMinSubscribedTokens);
		if (oneMinSubscribedTokens.contains(instrumentIndicators.getToken())) {
			LOGGER.info("{}: Publising one minute ticks to consumers", aggregationType);
			oneMinConsumers.get(instrumentIndicators.getToken()).stream()
					.forEach(consumer -> consumer.onAggregatedOneMinuteTick(instrumentIndicators));
		}
	}

	private void publishTicksToSingalGenerator(BarSeriesWrapper barSeriesWrapper, InstrumentIndicators instrumentIndicators,
			AggregationType aggregationType) {
		switch (aggregationType.getName()) {
		case SignalGeneratorConstants.ONE_MINUTE:
			oneMinuteSignalGenerator.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
			break;
		case SignalGeneratorConstants.THREE_MINUTES:
			threeMinuteSignalGenerator.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
			break;
		case SignalGeneratorConstants.FIVE_MINUTES:
			fiveMinuteSignalGenerator.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
			break;
		case SignalGeneratorConstants.FIFTEEN_MINUTES:
			fifteenMinuteSignalGenerator.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
			break;
		case SignalGeneratorConstants.ONE_HOUR:
			oneHourSignalGenerator.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
			break;
		case SignalGeneratorConstants.ONE_DAY:
			dailySignalGenerator.addSeries(barSeriesWrapper, instrumentIndicators, aggregationType);
			break;
		default:
			break;
		}
	}

	@LogExecutionTime
	@Async(value = "indicator-service")
	public void addSeries(BarSeriesWrapper barSeriesWrapper, InstrumentIndicators instrumentIndicators,
			AggregationType aggregationType) {
		LOGGER.info("addSeries: {}, {}", aggregationType.getName(), instrumentIndicators);
		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put("instrument-name", instrumentIndicators.getName().toUpperCase())
				.put("token", String.valueOf(instrumentIndicators.getToken()))
				.put("duration", aggregationType.getName())) {
			if (previousIndicatorMap.get(instrumentIndicators.getToken()) == null) {
				InstrumentIndicators lastIndicator = indicatorsRepository.findLast(
						SignalGeneratorUtil.getIndicatorClazz(aggregationType),
						instrumentIndicators.getToken());
				previousIndicatorMap.put(instrumentIndicators.getToken(), lastIndicator);
			}

			updateIndicators(barSeriesWrapper, instrumentIndicators);

			publishTicksToSingalGenerator(barSeriesWrapper, instrumentIndicators, aggregationType);
			publishTicksToConsumers(instrumentIndicators, aggregationType);

			LOGGER.info("Attempting to save time series data for token - {}, topic - {}",
					instrumentIndicators.getToken(), aggregationType);
			instrumentIndicatorsRepository.save(instrumentIndicators);

			LOGGER.info("Publishing time series data for token - {}, message - {}, topic - {}",
					instrumentIndicators.getToken(), instrumentIndicators, aggregationType);
			kafkaProducer.sendMessage(aggregationType.getName(), instrumentIndicators);
		}
	}

	private void updateIndicators(BarSeriesWrapper barSeriesWrapper, InstrumentIndicators instrumentIndicators) {
		BarSeries barSeries = barSeriesWrapper.getSeries();
		ClosePriceIndicator closePrice = new ClosePriceIndicator(barSeries);

		if (barSeries.getBarCount() >= EMA10_BAR_COUNT) {
			EMAIndicator ema10 = new EMAIndicator(closePrice, EMA10_BAR_COUNT);
			instrumentIndicators.setEma10(
					Double.valueOf(decimalFormat.format(ema10.getValue(barSeries.getEndIndex()).doubleValue())));
		}

		if (barSeries.getBarCount() >= RSI_BAR_COUNT) {
			RSIIndicator rsi = new RSIIndicator(closePrice, RSI_BAR_COUNT);
			instrumentIndicators.setRsi(
					Double.valueOf(decimalFormat.format(rsi.getValue(barSeries.getEndIndex()).doubleValue())));

			StochasticRSIIndicator stochastic = new StochasticRSIIndicator(rsi, RSI_BAR_COUNT);
			instrumentIndicators.setStochasticrsi(
					Double.valueOf(decimalFormat.format(stochastic.getValue(barSeries.getEndIndex()).doubleValue())));
		}

		if (barSeries.getBarCount() >= EMA20_BAR_COUNT) {
			EMAIndicator ema20 = new EMAIndicator(closePrice, EMA20_BAR_COUNT);
			instrumentIndicators.setEma20(
					Double.valueOf(decimalFormat.format(ema20.getValue(barSeries.getEndIndex()).doubleValue())));
		}

		if (barSeries.getBarCount() >= ATR_BAR_COUNT) {
			ATRIndicator atr = new ATRIndicator(barSeries, ATR_BAR_COUNT);
			instrumentIndicators.setAtr(
					Double.valueOf(decimalFormat.format(atr.getValue(barSeries.getEndIndex()).doubleValue())));

			ATRTrailingStopsIndicator atrts = new ATRTrailingStopsIndicator(barSeries, ATR_BAR_COUNT);
			instrumentIndicators.setAtrTs(
					Double.valueOf(decimalFormat.format(atrts.getValue(barSeries.getEndIndex()).doubleValue())));
		}

		HLC3Indicator hlc3 = new HLC3Indicator(barSeries);
		instrumentIndicators
				.setHlc3(Double.valueOf(decimalFormat.format(hlc3.getValue(barSeries.getEndIndex()).doubleValue())));

		InstrumentIndicators prevIndicator = previousIndicatorMap.get(instrumentIndicators.getToken());
		SMMAIndicator smmaHp = new SMMAIndicator(new HighPriceIndicator(barSeries), SMMA_BAR_COUNT,
				prevIndicator != null ? prevIndicator.getSmmaHp34() : -1);
		instrumentIndicators.setSmmaHp34(
				Double.valueOf(decimalFormat.format(smmaHp.getValue(barSeries.getEndIndex()).doubleValue())));

		SMMAIndicator smmaLp = new SMMAIndicator(new LowPriceIndicator(barSeries), SMMA_BAR_COUNT,
				prevIndicator != null ? prevIndicator.getSmmaLp34() : -1);
		instrumentIndicators.setSmmaLp34(
				Double.valueOf(decimalFormat.format(smmaLp.getValue(barSeries.getEndIndex()).doubleValue())));

		if (barSeries.getBarCount() >= MACD_LONG_EMA_BAR_COUNT) {
			MACDIndicator macd = new MACDIndicator(closePrice, MACD_SHORT_EMA_BAR_COUNT, MACD_LONG_EMA_BAR_COUNT);
			EMAIndicator macdSignal = new EMAIndicator(macd, MACD_SIGNAL_EMA_BAR_COUNT);
			instrumentIndicators.setMacd(
					Double.valueOf(decimalFormat.format(macd.getValue(barSeries.getEndIndex()).doubleValue())));
			instrumentIndicators.setMacdSignal(
					Double.valueOf(decimalFormat.format(macdSignal.getValue(barSeries.getEndIndex()).doubleValue())));
		}

		if (barSeries.getBarCount() >= IMACD_SMA_BAR_COUNT) {
			ImpulseMACDIndicator imacd = new ImpulseMACDIndicator(barSeries, IMACD_SMA_BAR_COUNT,
					previousIndicatorMap.get(instrumentIndicators.getToken()));
			SMAIndicator imacdSignal = new SMAIndicator(imacd, IMACD_SIGNAL_SMA_BAR_COUNT);
			ZLEMAIndicator zlema = new ZLEMAIndicator(hlc3, ZLEMA_BAR_COUNT);
			instrumentIndicators.setImacd(
					Double.valueOf(decimalFormat.format(imacd.getValue(barSeries.getEndIndex()).doubleValue())));
			instrumentIndicators.setImacdSignal(
					Double.valueOf(decimalFormat.format(imacdSignal.getValue(barSeries.getEndIndex()).doubleValue())));
			instrumentIndicators.setZlema34(
					Double.valueOf(decimalFormat.format(zlema.getValue(barSeries.getEndIndex()).doubleValue())));
		}

		if (barSeries.getBarCount() >= EMA30_BAR_COUNT) {
			EMAIndicator ema30 = new EMAIndicator(closePrice, EMA30_BAR_COUNT);
			instrumentIndicators.setEma30(
					Double.valueOf(decimalFormat.format(ema30.getValue(barSeries.getEndIndex()).doubleValue())));
		}

		if (barSeries.getBarCount() >= EMA50_BAR_COUNT) {
			EMAIndicator ema50 = new EMAIndicator(closePrice, EMA50_BAR_COUNT);
			instrumentIndicators.setEma50(
					Double.valueOf(decimalFormat.format(ema50.getValue(barSeries.getEndIndex()).doubleValue())));
		}

		// This is required to calculate the SMMA
		previousIndicatorMap.put(instrumentIndicators.getToken(), instrumentIndicators);
	}

}

package com.trading.platform.service.signal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.persistence.AggregationTypeRepository;
import com.trading.platform.persistence.SignalRepository;
import com.trading.platform.persistence.SubscriptionReadOnlyRepositoryIf;
import com.trading.platform.persistence.entity.AggregationType;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.service.LiveTicksConsumer;
import com.trading.platform.service.series.BarSeriesWrapper;
import com.trading.platform.service.trade.TradeManager;
import com.trading.platform.trading.indicator.MarketTrendInfo;
import com.trading.platform.trading.strategies.HAMACDOptionsBuyStrategy;
import com.trading.platform.trading.strategies.IMACDOptionsBuyStrategy;
import com.trading.platform.trading.strategies.MACDOptionsBuyStrategy;
import com.trading.platform.trading.strategies.SignalStrategy;
import com.trading.platform.trading.strategies.SuperTrendOptionsBuyStrategy;
import com.trading.platform.util.SignalGeneratorUtil;

public abstract class SignalGenerator {

	private static final Logger LOGGER = LogManager.getLogger(SignalGenerator.class);

	@Autowired
	private SignalRepository repository;

	@Autowired
	private SubscriptionReadOnlyRepositoryIf subscriptionRepository;

	@Autowired
	private AggregationTypeRepository aggregationTypeRepository;

	@Autowired
	private TradeManager tradeManager;

	@Autowired
	private LiveTicksConsumer ticksConsumer;

	private Map<Long, InstrumentIndicators> previousIndicatorMap;

	protected SignalGenerator() {
		this.previousIndicatorMap = new HashMap<>();
	}

	@Bean(name = "signal-generator")
	public TaskExecutor sigalGeneratorTaskExecutor() {
		return new SimpleAsyncTaskExecutor(new BasicThreadFactory.Builder()
				.namingPattern("signal-generator-%d")
				.uncaughtExceptionHandler((Thread thread, Throwable e) -> LOGGER
						.fatal("Uncaught exception in signal generator - {}", thread.getName(), e))
				.daemon(true)
				.priority(Thread.NORM_PRIORITY)
				.build());
	}

	@LogExecutionTime
	@Async(value = "signal-generator")
	public void addSeries(BarSeriesWrapper barSeriesWrapper,
			InstrumentIndicators instrumentIndicators,
			AggregationType aggregationType) {
		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put("instrument-name", instrumentIndicators.getName().toUpperCase())
				.put("token", String.valueOf(instrumentIndicators.getToken()))
				.put("duration", aggregationType.getName())) {
			InstrumentSubscription subscription = subscriptionRepository.getByToken(
					instrumentIndicators.getToken());

			MarketTrendInfo trendInfo = new MarketTrendInfo(barSeriesWrapper.getSeries());
			LOGGER.info("Market Trend Info - {}", trendInfo);

			List<SignalStrategy> strategyList = new LinkedList<>();
			strategyList.add(new MACDOptionsBuyStrategy(barSeriesWrapper, subscription,
					instrumentIndicators));
			strategyList.add(new IMACDOptionsBuyStrategy(barSeriesWrapper, subscription,
					instrumentIndicators));
			strategyList.add(new HAMACDOptionsBuyStrategy(barSeriesWrapper));
			strategyList.add(new SuperTrendOptionsBuyStrategy(barSeriesWrapper,
					instrumentIndicators));

			for (SignalStrategy strategy : strategyList) {
				applyStrategy(trendInfo, strategy, instrumentIndicators, subscription,
						aggregationType);
			}
		}
		previousIndicatorMap.put(instrumentIndicators.getToken(), instrumentIndicators);
	}

	@LogExecutionTime
	@Async(value = "signal-generator")
	public void applyStrategy(MarketTrendInfo trendInfo, SignalStrategy strategy,
			InstrumentIndicators instrumentIndicators,
			InstrumentSubscription subscription, AggregationType aggregationType) {
		if (strategy.shouldBuyCE()) {
			generateSignal(trendInfo, instrumentIndicators, subscription, aggregationType,
					OptionType.BUY_CE,
					strategy);
		} else if (strategy.shouldBuyPE()) {
			generateSignal(trendInfo, instrumentIndicators, subscription, aggregationType,
					OptionType.BUY_PE,
					strategy);
		} else if (strategy.shouldSellCE()) {
			generateSignal(trendInfo, instrumentIndicators, subscription, aggregationType,
					OptionType.SELL_CE,
					strategy);
		} else if (strategy.shouldSellPE()) {
			generateSignal(trendInfo, instrumentIndicators, subscription, aggregationType,
					OptionType.SELL_PE,
					strategy);
		} else {
			LOGGER.info("{}: Signal Generator: addSeries: NO_SIGNAL: {}", strategy.getName(),
					instrumentIndicators.getName());
		}
	}

	public void addSignal(Signal signal) {
		InstrumentSubscription subscription = subscriptionRepository.getByToken(signal.getToken());
		InstrumentIndicators indicator = previousIndicatorMap.get(signal.getToken());
		OptionType optionType = OptionType.getByName(signal.getTradeSignal());
		if (optionType != null) {
			SignalGeneratorUtil.generateOptionSymbol(signal, optionType, subscription);
		}
		if (signal.getOptionSymbol() != null) {
			repository.save(signal);
			LOGGER.info("{}: Signal Generator: generateSignal: {}: {}", signal.getStrategy(), signal
					.getTradeSignal(), signal);

			Optional<AggregationType> aggregationType = aggregationTypeRepository.findAll().stream()
					.filter(AggregationType::isAggregable)
					.filter(type -> type.getName().equalsIgnoreCase(signal.getAggregationType()))
					.findFirst();
			if (aggregationType.isPresent()) {
				BarSeriesWrapper wrapper = new BarSeriesWrapper(aggregationType.get(), indicator);
				MarketTrendInfo trendInfo = new MarketTrendInfo(wrapper.getSeries());
				tradeManager.handleSignal(trendInfo, signal, subscription, indicator);
			}
		} else {
			LOGGER.error("{}: Unable to save the signal - {}", signal.getStrategy(), signal);
		}
	}

	private void generateSignal(MarketTrendInfo trendInfo,
			InstrumentIndicators instrumentIndicators,
			InstrumentSubscription subscription, AggregationType aggregationType,
			OptionType optionType, SignalStrategy strategy) {
		Signal signal = getSignal(instrumentIndicators, aggregationType, optionType,
				strategy);
		SignalGeneratorUtil.generateOptionSymbol(signal, optionType, subscription);

		if (signal.getOptionSymbol() != null) {
			repository.save(signal);
			LOGGER.info("{}: Signal Generator: generateSignal: {}: {}", strategy.getName(),
					optionType, signal);

			tradeManager.handleSignal(trendInfo, signal, subscription, instrumentIndicators);
		} else {
			LOGGER.error("{}: Error in generating the option symbol for signal - {}", strategy
					.getName(), signal);
		}
	}

	private Signal getSignal(InstrumentIndicators instrumentIndicators,
			AggregationType aggregationType, OptionType optionType, SignalStrategy strategy) {
		Signal signal = new Signal();
		signal.setTickTime(instrumentIndicators.getTickTime());
		signal.setToken(instrumentIndicators.getToken());
		signal.setName(instrumentIndicators.getName());
		signal.setAggregationType(aggregationType.getName());
		signal.setStrategy(strategy.getName());
		signal.setTradeSignal(optionType.name());
		signal.setLastTradedPrice(instrumentIndicators.getLastTradedPrice());
		signal.setAverageTrueRange(instrumentIndicators.getAtr());
		signal.setVixLastTradedPrice(ticksConsumer.getVixLastTradedPrice());

		return signal;
	}

}

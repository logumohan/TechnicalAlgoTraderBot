package com.trading.platform.service.trade;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trading.platform.persistence.JobsReadOnlyRepositoryImpl;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.signal.OptionType;
import com.trading.platform.util.SignalGeneratorUtil;
import com.trading.platform.util.TradeUtil;
import com.zerodhatech.models.Quote;

@Component
public class TradeJobScheduler {

	private static final Logger LOGGER = LogManager.getLogger(TradeJobScheduler.class);

	@Autowired
	private KiteLoginModuleImpl kiteModule;

	@Autowired
	private TradeHandlerJobFactory jobFactory;

	@Autowired
	private TradeJobPersistence persistence;

	@Autowired
	private JobsReadOnlyRepositoryImpl jobsRepository;

	private ExecutorService liveTradeExecutor;

	private ExecutorService paperTradeExecutor;

	private DecimalFormat decimalFormat;

	public TradeJobScheduler() {
		this.liveTradeExecutor = Executors.newCachedThreadPool(TradeUtil.getLiveTradingThreadFactory());
		this.paperTradeExecutor = Executors.newCachedThreadPool(TradeUtil.getPaperTradingThreadFactory());
		this.decimalFormat = new DecimalFormat("0.00");
		this.decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
	}

	private Predicate<Job> getAggregationTypeFilter(Signal signal) {
		return (Job job) -> signal.getAggregationType().equals(job.getAggregationType());
	}

	private Predicate<Job> getStrategyFilter(Signal signal) {
		return (Job job) -> signal.getStrategy().equals(job.getStrategy());
	}

	private Predicate<Job> getVixFilter(Signal signal) {
		return (Job job) -> signal.getVixLastTradedPrice() < job.getMaxVixAllowed();
	}

	private Predicate<Job> getATRPaperJobFilter() {
		return (Job job) -> job.isPaperTradable()
				&& job.getTrailBy().equals(TrailBy.ATR.getValue())
				&& job.getJobType().equals(TradeJobType.ATR_TSL.getType());
	}

	private Predicate<Job> getFixedPaperJobFilter() {
		return (Job job) -> job.isPaperTradable()
				&& job.getTrailBy().equals(TrailBy.POINTS.getValue())
				&& job.getJobType().equals(TradeJobType.FIXED_TSL.getType());
	}

	private Predicate<Job> getFixedProfitPaperJobFilter() {
		return (Job job) -> job.isPaperTradable()
				&& job.getTrailBy().equals(TrailBy.POINTS.getValue())
				&& job.getJobType().equals(TradeJobType.FIXED_PROFIT.getType());
	}

	private Predicate<Job> getMultiTargetPaperJobFilter() {
		return (Job job) -> job.isPaperTradable()
				&& job.getTrailBy().equals(TrailBy.POINTS.getValue())
				&& job.getJobType().equals(TradeJobType.MULTI_TARGET.getType());
	}

	private Predicate<Job> getATRLiveJobFilter() {
		return (Job job) -> job.isTradable()
				&& job.getTrailBy().equals(TrailBy.ATR.getValue())
				&& job.getJobType().equals(TradeJobType.ATR_TSL.getType());
	}

	private Predicate<Job> getFixedLiveJobFilter() {
		return (Job job) -> job.isTradable()
				&& job.getTrailBy().equals(TrailBy.POINTS.getValue())
				&& job.getJobType().equals(TradeJobType.FIXED_TSL.getType());
	}

	private Predicate<Job> getFixedProfitLiveJobFilter() {
		return (Job job) -> job.isTradable()
				&& job.getTrailBy().equals(TrailBy.POINTS.getValue())
				&& job.getJobType().equals(TradeJobType.FIXED_PROFIT.getType());
	}

	private Predicate<Job> getMultiTargetLiveJobFilter() {
		return (Job job) -> job.isTradable()
				&& job.getTrailBy().equals(TrailBy.POINTS.getValue())
				&& job.getJobType().equals(TradeJobType.MULTI_TARGET.getType());
	}

	private void updateOptionSymbolByJob(Job job, Signal signal, InstrumentSubscription subscription) {
		OptionType optionType;
		if (signal.getTradeSignal().equals(OptionType.BUY_CE.name())) {
			optionType = OptionType.BUY_CE;
		} else if (signal.getTradeSignal().equals(OptionType.BUY_PE.name())) {
			optionType = OptionType.BUY_PE;
		} else {
			return;
		}

		KiteSessionService session = kiteModule.getKiteSessions().get(job.getUserName());
		if (session == null || !session.isLoggedIn()) {
			LOGGER.error(
					"updateOptionSymbolByJob: Kite session does not exist for user - {}, skipping symbol generation - {}",
					job.getUserName(), job.getName());
			return;
		}

		int delta = 100;
		while (delta <= job.getStrikePriceDelta()) {
			long strikePrice = SignalGeneratorUtil.getNearestStrikePrice(optionType,
					signal.getLastTradedPrice(), delta);
			String optionSymbol = SignalGeneratorUtil.getWeeklyOptionSymbol(
					subscription.getOptionName(), strikePrice, optionType,
					subscription.getExpiryDay(), subscription.getMonthlyExpiryDay());
			long optionToken = session.getTokenByTradingSymbol(optionSymbol);
			Quote optionQuote = session.getQuote(String.valueOf(optionToken));
			double currentPrice = Optional.ofNullable(optionQuote)
					.map((Quote quote) -> Double.valueOf(decimalFormat.format(quote.lastPrice)))
					.orElse(-1D);
			LOGGER.info("Symbol :: {}, Delta :: {}, LTP :: {}, Strike Price :: {}", optionSymbol, delta, currentPrice,
					strikePrice);
			if (currentPrice >= 300 || delta == job.getStrikePriceDelta()) {
				LOGGER.info("Selected Symbol :: {}, Delta :: {}, LTP :: {}, Strike Price :: {}", optionSymbol, delta,
						currentPrice, strikePrice);
				signal.setStrikePrice(strikePrice);
				signal.setOptionSymbol(optionSymbol);
				break;
			} else {
				LOGGER.info("Option LTP is less than 300 for Symbol :: {}, Delta :: {}, LTP :: {}, Strike Price :: {}",
						optionSymbol, delta, currentPrice, strikePrice);
				delta += 100;
			}
		}
		LOGGER.info("Updated signal for the job - {}, signal - {}", job, signal);
	}

	private void scheduleATRTSLPaperTrade(Signal signal, InstrumentSubscription subscription, TradeManager tradeManager,
			InstrumentIndicators instrumentIndicators) {
		Predicate<Job> jobFilter = getStrategyFilter(signal)
				.and(getAggregationTypeFilter(signal))
				.and(getVixFilter(signal))
				.and(getATRPaperJobFilter());
		List<Job> jobsList = jobsRepository.findJobs();
		jobsList = jobsList.stream().filter(jobFilter).collect(Collectors.toList());
		LOGGER.info("scheduleATRTSLPaperTrade: Job List - {}", jobsList);
		for (Job job : jobsList) {
			try {
				Signal updatedSignal = new Signal(signal);
				updateOptionSymbolByJob(job, updatedSignal, subscription);
				TradeHandler tradeHandler = jobFactory.createPaperTradeHandlerJob(job, tradeManager, updatedSignal,
						subscription, instrumentIndicators);
				if (tradeHandler != null) {
					if (!persistence.isActivePaperTradeExists(updatedSignal, job)) {
						paperTradeExecutor.execute(tradeHandler);
						LOGGER.info("ATR TSL paper trade handler is scheduled, signal - {}, job - {}", updatedSignal,
								job.getName());
					} else {
						LOGGER.info("ATR TSL paper trade for signal - {}, job - {} is already running, skipping...",
								updatedSignal, job.getName());
					}
				} else {
					LOGGER.error("Error in creating ATR TSL paper trade handler job for signal - {}, job - {}",
							updatedSignal, job.getName());
				}
			} catch (TradeException e) {
				LOGGER.error("Exception in scheduling a ATR TSL paper trade, job - {}, signal - {}", job.getName(),
						signal, e);
			}
		}
	}

	private void scheduleFixedTSLPaperTrade(Signal signal, InstrumentSubscription subscription,
			TradeManager tradeManager, InstrumentIndicators instrumentIndicators) {
		Predicate<Job> jobFilter = getStrategyFilter(signal)
				.and(getAggregationTypeFilter(signal))
				.and(getVixFilter(signal))
				.and(getFixedPaperJobFilter());
		List<Job> jobsList = jobsRepository.findJobs();
		jobsList = jobsList.stream().filter(jobFilter).collect(Collectors.toList());
		LOGGER.info("scheduleFixedTSLPaperTrade: Job List - {}", jobsList);
		for (Job job : jobsList) {
			try {
				Signal updatedSignal = new Signal(signal);
				updateOptionSymbolByJob(job, updatedSignal, subscription);
				TradeHandler tradeHandler = jobFactory.createPaperTradeHandlerJob(job, tradeManager, updatedSignal,
						subscription, instrumentIndicators);
				if (tradeHandler != null) {
					if (!persistence.isActivePaperTradeExists(updatedSignal, job)) {
						paperTradeExecutor.execute(tradeHandler);
						LOGGER.info("Fixed TSL paper trade handler is scheduled, signal - {}, job - {}", updatedSignal,
								job.getName());
					} else {
						LOGGER.info("Fixed TSL paper trade for signal - {}, job - {} is already running, skipping...",
								updatedSignal, job.getName());
					}
				} else {
					LOGGER.error("Error in creating fixed TSL paper trade handler job for signal - {}, job - {}",
							updatedSignal, job.getName());
				}
			} catch (TradeException e) {
				LOGGER.error("Exception in scheduling a fixed TSL paper trade, job - {}, signal - {}", job.getName(),
						signal, e);
			}
		}
	}

	private void scheduleFixedProfitPaperTrade(Signal signal, InstrumentSubscription subscription,
			TradeManager tradeManager, InstrumentIndicators instrumentIndicators) {
		Predicate<Job> jobFilter = getStrategyFilter(signal)
				.and(getAggregationTypeFilter(signal))
				.and(getVixFilter(signal))
				.and(getFixedProfitPaperJobFilter());
		List<Job> jobsList = jobsRepository.findJobs();
		jobsList = jobsList.stream().filter(jobFilter).collect(Collectors.toList());
		LOGGER.info("scheduleFixedProfitPaperTrade: Job List - {}", jobsList);
		for (Job job : jobsList) {
			try {
				Signal updatedSignal = new Signal(signal);
				updateOptionSymbolByJob(job, updatedSignal, subscription);
				TradeHandler tradeHandler = jobFactory.createPaperTradeHandlerJob(job, tradeManager, updatedSignal,
						subscription, instrumentIndicators);
				if (tradeHandler != null) {
					if (!persistence.isActivePaperTradeExists(updatedSignal, job)) {
						paperTradeExecutor.execute(tradeHandler);
						LOGGER.info("Fixed profit paper trade handler is scheduled, signal - {}, job - {}",
								updatedSignal,
								job.getName());
					} else {
						LOGGER.info(
								"Fixed profit paper trade for signal - {}, job - {} is already running, skipping...",
								updatedSignal, job.getName());
					}
				} else {
					LOGGER.error("Error in creating fixed profit paper trade handler job for signal - {}, job - {}",
							updatedSignal, job.getName());
				}
			} catch (TradeException e) {
				LOGGER.error("Exception in scheduling a fixed profit paper trade, job - {}, signal - {}", job.getName(),
						signal, e);
			}
		}
	}

	private void scheduleMultiTargetPaperTrade(Signal signal, InstrumentSubscription subscription,
			TradeManager tradeManager, InstrumentIndicators instrumentIndicators) {
		Predicate<Job> jobFilter = getStrategyFilter(signal)
				.and(getAggregationTypeFilter(signal))
				.and(getVixFilter(signal))
				.and(getMultiTargetPaperJobFilter());
		List<Job> jobsList = jobsRepository.findJobs();
		jobsList = jobsList.stream().filter(jobFilter).collect(Collectors.toList());
		LOGGER.info("scheduleMultiTargetPaperTrade: Job List - {}", jobsList);
		for (Job job : jobsList) {
			try {
				Signal updatedSignal = new Signal(signal);
				updateOptionSymbolByJob(job, updatedSignal, subscription);
				TradeHandler tradeHandler = jobFactory.createPaperTradeHandlerJob(job, tradeManager, updatedSignal,
						subscription, instrumentIndicators);
				if (tradeHandler != null) {
					if (!persistence.isActivePaperTradeExists(updatedSignal, job)) {
						paperTradeExecutor.execute(tradeHandler);
						LOGGER.info("Multi target paper trade handler is scheduled, signal - {}, job - {}",
								updatedSignal,
								job.getName());
					} else {
						LOGGER.info(
								"Multi target paper trade for signal - {}, job - {} is already running, skipping...",
								updatedSignal, job.getName());
					}
				} else {
					LOGGER.error("Error in creating multi target paper trade handler job for signal - {}, job - {}",
							updatedSignal, job.getName());
				}
			} catch (TradeException e) {
				LOGGER.error("Exception in scheduling a multi target paper trade, job - {}, signal - {}", job.getName(),
						signal, e);
			}
		}
	}

	private void scheduleATRTSLLiveTrade(Signal signal, InstrumentSubscription subscription, TradeManager tradeManager,
			InstrumentIndicators instrumentIndicators) {
		Predicate<Job> jobFilter = getStrategyFilter(signal)
				.and(getAggregationTypeFilter(signal))
				.and(getVixFilter(signal))
				.and(getATRLiveJobFilter());
		List<Job> jobsList = jobsRepository.findJobs();
		jobsList = jobsList.stream().filter(jobFilter).collect(Collectors.toList());

		for (Job job : jobsList) {
			try {
				Signal updatedSignal = new Signal(signal);
				updateOptionSymbolByJob(job, updatedSignal, subscription);
				TradeHandler tradeHandler = jobFactory.createLiveTradeHandlerJob(job, tradeManager, updatedSignal,
						subscription, instrumentIndicators);
				if (tradeHandler != null) {
					if (persistence.isActiveLiveTradeExists(updatedSignal, job)) {
						LOGGER.info("ATR TSL live trade for signal - {}, job - {} is already running, skipping",
								updatedSignal, job.getName());
					} else if (persistence.isConsecutiveTradesFailed(updatedSignal, subscription)) {
						LOGGER.info(
								"ATR TSL Live: Consecutive failed trades for {} crossed the threshold, skipping signal - {}",
								subscription, updatedSignal);
					} else {
						liveTradeExecutor.execute(tradeHandler);
						LOGGER.info("ATR TSL live trade handler is scheduled, signal - {}", updatedSignal);
					}
				} else {
					LOGGER.error("Error in creating ATR TSL live trade handler job for signal - {}", updatedSignal);
				}
			} catch (TradeException e) {
				LOGGER.error("Exception in scheduling a ATR TSL live trade, job - {}, signal - {}", job.getName(),
						signal, e);
			}
		}
	}

	private void scheduleFixedTSLLiveTrade(Signal signal, InstrumentSubscription subscription,
			TradeManager tradeManager,
			InstrumentIndicators instrumentIndicators) {
		Predicate<Job> jobFilter = getStrategyFilter(signal)
				.and(getAggregationTypeFilter(signal))
				.and(getVixFilter(signal))
				.and(getFixedLiveJobFilter());
		List<Job> jobsList = jobsRepository.findJobs();
		jobsList = jobsList.stream().filter(jobFilter).collect(Collectors.toList());

		for (Job job : jobsList) {
			try {
				Signal updatedSignal = new Signal(signal);
				updateOptionSymbolByJob(job, updatedSignal, subscription);
				TradeHandler tradeHandler = jobFactory.createLiveTradeHandlerJob(job, tradeManager, updatedSignal,
						subscription, instrumentIndicators);
				if (tradeHandler != null) {
					if (persistence.isActiveLiveTradeExists(updatedSignal, job)) {
						LOGGER.info("Fixed TSL live trade for signal - {}, job - {} is already running, skipping",
								updatedSignal, job.getName());
					} else if (persistence.isConsecutiveTradesFailed(updatedSignal, subscription)) {
						LOGGER.info(
								"Fixed TSL: Consecutive failed trades for {} crossed the threshold, skipping signal - {}",
								subscription, updatedSignal);
					} else {
						liveTradeExecutor.execute(tradeHandler);
						LOGGER.info("Fixed TSL live trade handler is scheduled, signal - {}", updatedSignal);
					}
				} else {
					LOGGER.error("Error in creating fixed TSL live trade handler job for signal - {}", updatedSignal);
				}
			} catch (TradeException e) {
				LOGGER.error("Exception in scheduling a fixed TSL live trade, job - {}, signal - {}", job.getName(),
						signal, e);
			}
		}
	}

	private void scheduleFixedProfitLiveTrade(Signal signal, InstrumentSubscription subscription,
			TradeManager tradeManager, InstrumentIndicators instrumentIndicators) {
		Predicate<Job> jobFilter = getStrategyFilter(signal)
				.and(getAggregationTypeFilter(signal))
				.and(getVixFilter(signal))
				.and(getFixedProfitLiveJobFilter());
		List<Job> jobsList = jobsRepository.findJobs();
		jobsList = jobsList.stream().filter(jobFilter).collect(Collectors.toList());

		for (Job job : jobsList) {
			try {
				Signal updatedSignal = new Signal(signal);
				updateOptionSymbolByJob(job, updatedSignal, subscription);
				TradeHandler tradeHandler = jobFactory.createLiveTradeHandlerJob(job, tradeManager, updatedSignal,
						subscription, instrumentIndicators);
				if (tradeHandler != null) {
					if (persistence.isActiveLiveTradeExists(updatedSignal, job)) {
						LOGGER.info("Fixed Profit live trade for signal - {}, job - {} is already running, skipping",
								updatedSignal, job.getName());
					} else if (persistence.isConsecutiveTradesFailed(updatedSignal, subscription)) {
						LOGGER.info(
								"Fixed Profit: Consecutive failed trades for {} crossed the threshold, skipping signal - {}",
								subscription, updatedSignal);
					} else {
						liveTradeExecutor.execute(tradeHandler);
						LOGGER.info("Fixed Profit live trade handler is scheduled, signal - {}", updatedSignal);
					}
				} else {
					LOGGER.error("Error in creating fixed profit live trade handler job for signal - {}",
							updatedSignal);
				}
			} catch (TradeException e) {
				LOGGER.error("Exception in scheduling a fixed profit live trade, job - {}, signal - {}", job.getName(),
						signal, e);
			}
		}
	}

	private void scheduleMultiTargetLiveTrade(Signal signal, InstrumentSubscription subscription,
			TradeManager tradeManager, InstrumentIndicators instrumentIndicators) {
		Predicate<Job> jobFilter = getStrategyFilter(signal)
				.and(getAggregationTypeFilter(signal))
				.and(getVixFilter(signal))
				.and(getMultiTargetLiveJobFilter());
		List<Job> jobsList = jobsRepository.findJobs();
		jobsList = jobsList.stream().filter(jobFilter).collect(Collectors.toList());

		for (Job job : jobsList) {
			try {
				Signal updatedSignal = new Signal(signal);
				updateOptionSymbolByJob(job, updatedSignal, subscription);
				TradeHandler tradeHandler = jobFactory.createLiveTradeHandlerJob(job, tradeManager, updatedSignal,
						subscription, instrumentIndicators);
				if (tradeHandler != null) {
					if (persistence.isActiveLiveTradeExists(updatedSignal, job)) {
						LOGGER.info("Multi target live trade for signal - {}, job - {} is already running, skipping",
								updatedSignal, job.getName());
					} else if (persistence.isConsecutiveTradesFailed(updatedSignal, subscription)) {
						LOGGER.info(
								"Multi Target: Consecutive failed trades for {} crossed the threshold, skipping signal - {}",
								subscription, updatedSignal);
					} else {
						liveTradeExecutor.execute(tradeHandler);
						LOGGER.info("Multi target live trade handler is scheduled, signal - {}", updatedSignal);
					}
				} else {
					LOGGER.error("Error in creating multi target live trade handler job for signal - {}",
							updatedSignal);
				}
			} catch (TradeException e) {
				LOGGER.error("Exception in scheduling a multi target live trade, job - {}, signal - {}", job.getName(),
						signal, e);
			}
		}
	}

	public List<String> getLiveJobStrategies() {
		List<Job> jobsList = jobsRepository.findJobs();
		return jobsList.stream().filter(Job::isTradable).map(Job::getStrategy).collect(Collectors.toList());
	}

	public void schedulePaperTrades(Signal signal, InstrumentSubscription subscription, TradeManager tradeManager,
			InstrumentIndicators instrumentIndicators) {
		scheduleATRTSLPaperTrade(signal, subscription, tradeManager, instrumentIndicators);
		scheduleFixedTSLPaperTrade(signal, subscription, tradeManager, instrumentIndicators);
		scheduleFixedProfitPaperTrade(signal, subscription, tradeManager, instrumentIndicators);
		scheduleMultiTargetPaperTrade(signal, subscription, tradeManager, instrumentIndicators);
		LOGGER.info("Scheduled paper trades, signal = {}", signal);
	}

	public void scheduleLiveTrades(Signal signal, InstrumentSubscription subscription, TradeManager tradeManager,
			InstrumentIndicators instrumentIndicators) {
		scheduleATRTSLLiveTrade(signal, subscription, tradeManager, instrumentIndicators);
		scheduleFixedTSLLiveTrade(signal, subscription, tradeManager, instrumentIndicators);
		scheduleFixedProfitLiveTrade(signal, subscription, tradeManager, instrumentIndicators);
		scheduleMultiTargetLiveTrade(signal, subscription, tradeManager, instrumentIndicators);
		LOGGER.info("Scheduled live trades, signal = {}", signal);
	}

	public void rescheduleTradeHandlerJob(TradeInfo tradeInfo, TradeManager tradeManager) {
		try {
			List<Job> jobsList = jobsRepository.findJobs();
			Optional<Job> job = jobsList.stream().filter(jobInfo -> jobInfo.getName().equals(tradeInfo.getJobName()))
					.findFirst();

			if (job.isPresent()) {
				TradeHandler tradeHandler = jobFactory.createTradeHandlerJob(job.get(), tradeInfo, tradeManager);
				if (tradeHandler != null) {
					if (tradeInfo.isLive()) {
						liveTradeExecutor.execute(tradeHandler);
					} else {
						paperTradeExecutor.execute(tradeHandler);
					}
					LOGGER.info("Trade job {} for trade {} is started again", tradeInfo.getJobName(), tradeInfo);
				} else {
					LOGGER.error("Error initializing the job by name - {}, trade - {}", tradeInfo.getJobName(),
							tradeInfo);
				}
			} else {
				LOGGER.error("Job name {} is not configured", tradeInfo.getJobName());
			}
		} catch (TradeException e) {
			LOGGER.error("Exception in rescheduling the trade after restart - {}", e.getMessage(), e);
		}
	}

}

package com.trading.platform.service.trade;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.trading.platform.persistence.entity.Instrument;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Target;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Quote;

public abstract class AbstractTradeHandler implements OptionTickConsumer, TradeHandler {

	protected static final Logger LOGGER = LogManager.getLogger("TRADE");

	protected static final String TRADE_ID = "tradeId";

	protected static final String INSTRUMENT_NAME = "instrument-name";

	protected static final String TOKEN = "token";

	protected static final String DURATION = "duration";

	protected static final String SYMBOL = "symbol";

	protected DecimalFormat decimalFormat;

	private TradeManager tradeManager;

	private Job job;

	private TradeInfo tradeInfo;

	private KiteLoginModuleImpl kiteModule;

	private KiteSessionService session;

	private TradeJobPersistence persistence;

	private boolean isLiveTrade;

	private double lastTradedPrice;

	private double optionLastTradedPrice;

	private long optionToken;

	private InstrumentIndicators indicator;

	private InstrumentIndicators oneMinIndicator;

	protected InstrumentSubscription subscription;

	private boolean isSquareOff;

	protected AbstractTradeHandler(Job job, TradeInfo tradeInfo, TradeManager tradeManager,
			KiteLoginModuleImpl kiteModule, KiteSessionService session, TradeJobPersistence persistence,
			InstrumentIndicators indicator, InstrumentSubscription subscription) {
		this.job = job;
		this.tradeInfo = tradeInfo;
		this.tradeManager = tradeManager;
		this.kiteModule = kiteModule;
		this.session = session;
		this.persistence = persistence;
		this.indicator = indicator;
		this.subscription = subscription;
		this.decimalFormat = new DecimalFormat("0.00");
		this.decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
		this.tradeInfo.setLive(isLive());
	}

	@Override
	public void run() {
		StringBuilder builder = new StringBuilder(getTradeInfo().getJobName().replace("_", "-"));
		builder.append("-").append(getTradeInfo().getSignal().getOptionSymbol());
		Thread.currentThread().setName(builder.toString().toUpperCase());

		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put(TRADE_ID, getTradeInfo().getTradeId())
				.put(INSTRUMENT_NAME, getTradeInfo().getSignal().getName().toUpperCase())
				.put(TOKEN, String.valueOf(getTradeInfo().getSignal().getToken()))
				.put(DURATION, getTradeInfo().getSignal().getAggregationType())
				.put(SYMBOL, getTradeInfo().getSignal().getOptionSymbol())) {
			try {
				optionToken = getOptionToken();
				if (optionToken > 0) {
					LOGGER.info("Option token retrieved for symbol - {} is {}",
							getTradeInfo().getSignal().getOptionSymbol(), optionToken);
					getKiteModule().subscribeTicks(this);
					getTradeManager().subscribe(this);
					doTrade();
				} else {
					LOGGER.error("Could not retrieve the option token for the symbol - {}",
							getTradeInfo().getSignal().getOptionSymbol());
				}
			} catch (Exception e) {
				LOGGER.fatal("Error while trading for the signal - {}", getTradeInfo().getSignal(), e);
				if (optionToken > 0) {
					double optionPrice = getOptionLastTradedPrice();
					for (PositionInfo positionInfo : getTradeInfo().getPositionInfoList()) {
						if (positionInfo.isActive()) {
							markExit(positionInfo, getLastTradedPrice(), optionPrice, optionPrice);
						}
					}
					getPersistence().updateTrade(tradeInfo);
				}
			} finally {
				if (optionToken > 0) {
					updateProfit();
				}
				getTradeInfo().setActive(false);
				getPersistence().updateTrade(tradeInfo);
				getTradeManager().unsubscribe(this);
				getKiteModule().unsubscribeTicks(this);
			}
		}
	}

	public void updateProfit() {
		getTradeInfo().getPositionInfoList().stream()
				.filter(position -> !position.isActive())
				.forEach(position -> {
					Optional<Target> target = getJob().getTargets().stream()
							.filter(currTarget -> currTarget.getTargetId() == position.getTargetId())
							.findAny();
					if (target.isPresent()) {
						double profit = (position.getOptionExitPrice() - position.getOptionEntryPrice())
								* (target.get().getNumOfLots() * subscription.getLotSize());
						position.setProfit(Double.valueOf(decimalFormat.format(profit)));
					}
				});
		getPersistence().updateTrade(getTradeInfo());
	}
	
	public void updatePositionProfit(PositionInfo positionInfo) {
		Optional<Target> target = getJob().getTargets().stream()
				.filter(currTarget -> currTarget.getTargetId() == positionInfo.getTargetId())
				.findAny();
		if (target.isPresent()) {
			double profit = (positionInfo.getOptionExitPrice() - positionInfo.getOptionEntryPrice())
					* (target.get().getNumOfLots() * subscription.getLotSize());
			positionInfo.setProfit(Double.valueOf(decimalFormat.format(profit)));
		}
	}

	protected KiteLoginModuleImpl getKiteModule() {
		return kiteModule;
	}

	protected KiteSessionService getSession() {
		return session;
	}

	public TradeJobPersistence getPersistence() {
		return persistence;
	}

	@Override
	public TradeManager getTradeManager() {
		return tradeManager;
	}

	public boolean isOrderOpen(String orderStatus) {
		return Constants.ORDER_TRIGGER_PENDING.equals(orderStatus)
				|| Constants.ORDER_OPEN.equals(orderStatus)
				|| "OPEN PENDING".equals(orderStatus)
				|| "PUT ORDER REQ RECEIVED".equals(orderStatus)
				|| "VALIDATION PENDING".equals(orderStatus);
	}

	@Override
	public Job getJob() {
		return job;
	}

	@Override
	public Target getTarget(int targetId) {
		List<Target> targets = getJob().getTargets();
		Optional<Target> target = targets.stream().filter((Target t) -> t.getTargetId() == targetId).findFirst();
		if (target.isPresent()) {
			return target.get();
		} else {
			return targets.get(0);
		}
	}

	@Override
	public TradeInfo getTradeInfo() {
		return tradeInfo;
	}

	@Override
	public void setTradeInfo(TradeInfo tradeInfo) {
		this.tradeInfo = tradeInfo;
	}

	protected boolean isPaperTrade() {
		return isLiveTrade;
	}

	protected double getLastTradedPrice() {
		if (lastTradedPrice == 0) {
			return getTradeInfo().getSignal().getLastTradedPrice();
		}
		return lastTradedPrice;
	}

	protected double getAverageTrueRange() {
		if (indicator != null) {
			return indicator.getAtr();
		}

		return tradeInfo.getSignal().getAverageTrueRange();
	}

	protected void setLastTradedPrice(double lastTradedPrice) {
		this.lastTradedPrice = Double.valueOf(decimalFormat.format(lastTradedPrice));
	}

	protected void setOptionLastTradedPrice(double optionLastTradedPrice) {
		this.optionLastTradedPrice = optionLastTradedPrice;
	}

	@Override
	public int getQuantity() {
		int numOfLots = getTradeInfo().getPositionInfoList().stream()
				.map(PositionInfo::getNumOfLots).collect(Collectors.summingInt(Integer::intValue));
		return numOfLots * getJob().getLotSize();
	}

	@Override
	public int getQuantity(PositionInfo positionInfo) {
		return positionInfo.getNumOfLots() * getJob().getLotSize();
	}

	@Override
	public int getSoldQuantity() {
		return getTradeInfo().getPositionInfoList().stream()
				.map(PositionInfo::getSoldQuantity).collect(Collectors.summingInt(Integer::intValue));
	}

	@Override
	public int getSoldQuantity(PositionInfo positionInfo) {
		return positionInfo.getSoldQuantity();
	}

	protected InstrumentIndicators getIndicator() {
		return indicator;
	}

	protected void setIndicator(InstrumentIndicators indicator) {
		this.indicator = indicator;
	}

	protected InstrumentIndicators getOneMinIndicator() {
		return oneMinIndicator;
	}

	protected void setOneMinIndicator(InstrumentIndicators oneMinIndicator) {
		this.oneMinIndicator = oneMinIndicator;
	}

	@Override
	public Long getToken() {
		return tradeInfo.getSignal().getToken();
	}

	@Override
	public Long getOptionToken() {
		return session.getTokenByTradingSymbol(tradeInfo.getSignal().getOptionSymbol());
	}

	@Override
	public double getOptionLastTradedPrice() {
		if (optionLastTradedPrice > 0) {
			return optionLastTradedPrice;
		} else {
			LOGGER.info("Attempting to get a last traded price for the option token - {}", optionToken);
			Quote optionQuote = getSession().getQuote(String.valueOf(optionToken));
			double currentPrice = Optional.ofNullable(optionQuote)
					.map((Quote quote) -> Double.valueOf(decimalFormat.format(optionQuote.lastPrice)))
					.orElse(-1D);
			if (currentPrice != -1) {
				optionLastTradedPrice = currentPrice;
			}
			LOGGER.info("Last traded price for the option token - {} is {}", optionToken, currentPrice);

			return currentPrice;
		}
	}

	@Override
	public double getUnRealizedProfitPoints(PositionInfo positionInfo) {
		return getOptionLastTradedPrice() - positionInfo.getOptionEntryPrice();
	}

	@Override
	public void onTick(Instrument instrument) {
		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put(TRADE_ID, getTradeInfo().getTradeId())
				.put(INSTRUMENT_NAME, getTradeInfo().getSignal().getName().toUpperCase())
				.put(TOKEN, String.valueOf(getTradeInfo().getSignal().getToken()))
				.put(DURATION, getTradeInfo().getSignal().getAggregationType())
				.put(SYMBOL, getTradeInfo().getSignal().getOptionSymbol())) {
			LOGGER.info("Tick received : {}", instrument);
			setLastTradedPrice(instrument.getLastTradedPrice());
		}
	}

	@Override
	public void onOptionTick(Instrument instrument) {
		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put(TRADE_ID, getTradeInfo().getTradeId())
				.put(INSTRUMENT_NAME, getTradeInfo().getSignal().getName().toUpperCase())
				.put(TOKEN, String.valueOf(getTradeInfo().getSignal().getToken()))
				.put(DURATION, getTradeInfo().getSignal().getAggregationType())
				.put(SYMBOL, getTradeInfo().getSignal().getOptionSymbol())) {
			LOGGER.info("Option tick received : {}", instrument);
			setOptionLastTradedPrice(instrument.getLastTradedPrice());
		}
	}

	@Override
	public void onAggregatedTick(InstrumentIndicators indicator) {
		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put(TRADE_ID, getTradeInfo().getTradeId())
				.put(INSTRUMENT_NAME, getTradeInfo().getSignal().getName().toUpperCase())
				.put(TOKEN, String.valueOf(getTradeInfo().getSignal().getToken()))
				.put(DURATION, getTradeInfo().getSignal().getAggregationType())
				.put(SYMBOL, getTradeInfo().getSignal().getOptionSymbol())) {
			LOGGER.info("Aggregated tick received : {}", indicator.getName());
			setIndicator(indicator);
		}
	}

	@Override
	public void onAggregatedOneMinuteTick(InstrumentIndicators oneMinIndicator) {
		try (CloseableThreadContext.Instance context = CloseableThreadContext
				.put(TRADE_ID, getTradeInfo().getTradeId())
				.put(INSTRUMENT_NAME, getTradeInfo().getSignal().getName().toUpperCase())
				.put(TOKEN, String.valueOf(getTradeInfo().getSignal().getToken()))
				.put(DURATION, getTradeInfo().getSignal().getAggregationType())
				.put(SYMBOL, getTradeInfo().getSignal().getOptionSymbol())) {
			LOGGER.info("One minute aggregated tick received : {}", oneMinIndicator.getName());
			setOneMinIndicator(oneMinIndicator);
		}
	}

	@Override
	public String getAggregationType() {
		return tradeInfo.getSignal().getAggregationType();
	}

	@Override
	public void setSquareOff() {
		this.isSquareOff = true;
	}

	@Override
	public boolean isSquareOff() {
		return this.isSquareOff;
	}

	@Override
	public void markEntry(PositionInfo positionInfo, double entryPrice, double optionEntryLtp,
			double optionEntryPrice) {
		positionInfo.setEntryTime(Date.from(ZonedDateTime.now().toInstant()));
		positionInfo.setEntryPrice(entryPrice);
		positionInfo.setOptionEntryLtp(optionEntryLtp);
		positionInfo.setOptionEntryPrice(optionEntryPrice);
	}

	@Override
	public void markExit(PositionInfo positionInfo, double exitPrice, double optionExitLtp, double optionExitPrice) {
		positionInfo.setExitTime(Date.from(ZonedDateTime.now().toInstant()));
		positionInfo.setExitPrice(exitPrice);
		positionInfo.setOptionExitLtp(optionExitLtp);
		positionInfo.setOptionExitPrice(optionExitPrice);
	}

	public void wait(int seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException e) {
			LOGGER.error("Error while waiting for {} seconds", seconds);
			Thread.currentThread().interrupt();
		}
	}

}

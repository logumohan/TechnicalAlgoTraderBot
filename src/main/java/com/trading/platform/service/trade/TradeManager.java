package com.trading.platform.service.trade;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.persistence.TradeInfoRepository;
import com.trading.platform.persistence.entity.InstrumentIndicators;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Position;
import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.persistence.entity.Trade;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.signal.OptionType;
import com.trading.platform.trading.indicator.MarketTrendInfo;
import com.trading.platform.util.MarketTimeUtil;
import com.trading.platform.util.TradeUtil;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.Quote;

@Component
public class TradeManager extends TradeManagerTelegramBot {

	private static final Logger LOGGER = LogManager.getLogger(TradeManager.class);

	private boolean allowLiveTrade;

	private boolean allowPaperTrade;

	private KiteLoginModuleImpl kiteModule;

	private TradeJobScheduler jobScheduler;

	private TradeJobPersistence persistence;

	private PositionPersistence positionPersistence;

	private TradeInfoRepository tradeRepository;

	private TrendReversalHandler trendReversalHandler;

	private Map<String, TradeHandler> subscriptionMap;

	private DecimalFormat decimalFormat;

	@Autowired
	public TradeManager(@Qualifier("allowLiveTrade") boolean allowLiveTrade,
			@Qualifier("allowPaperTrade") boolean allowPaperTrade,
			TradeJobPersistence persistence, PositionPersistence positionPersistence,
			TradeJobScheduler jobScheduler, KiteLoginModuleImpl kiteModule,
			TrendReversalHandler trendReversalHandler, TradeInfoRepository tradeRepository) {
		super(kiteModule);
		this.allowLiveTrade = allowLiveTrade;
		this.allowPaperTrade = allowPaperTrade;
		this.trendReversalHandler = trendReversalHandler;
		this.tradeRepository = tradeRepository;
		this.persistence = persistence;
		this.positionPersistence = positionPersistence;
		this.jobScheduler = jobScheduler;
		this.kiteModule = kiteModule;
		this.subscriptionMap = new LinkedHashMap<>();
		this.decimalFormat = new DecimalFormat("0.00");

		LOGGER.info("TradeManager initialized, allowLiveTrade = {}, allowPaperTrade = {}",
				allowLiveTrade, allowPaperTrade);

		try {
			sendMessageToChannel("Trade Manager Bot initialized, server = " + InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			LOGGER.error("Error in sending bot initialization message", e);
		}

		loadTradesFromDB();
	}

	public void setAllowLiveTrade(boolean allowLiveTrade) {
		this.allowLiveTrade = allowLiveTrade;
	}

	public void setAllowPaperTrade(boolean allowPaperTrade) {
		this.allowPaperTrade = allowPaperTrade;
	}

	public void subscribe(TradeHandler tradeHandler) {
		subscriptionMap.put(tradeHandler.getTradeInfo().getTradeId(), tradeHandler);
	}

	public void unsubscribe(TradeHandler tradeHandler) {
		subscriptionMap.remove(tradeHandler.getTradeInfo().getTradeId());
	}

	public void squareOffLiveTrades() {
		subscriptionMap.values().stream().filter(handler -> handler.getTradeInfo().isLive())
				.forEach(TradeHandler::setSquareOff);
	}

	public void squareOffPaperTrades() {
		subscriptionMap.values().stream().filter(handler -> !handler.getTradeInfo().isLive())
				.forEach(TradeHandler::setSquareOff);
	}

	public void squareOffPaperTradeById(String tradeId) {
		Optional<TradeHandler> tradeHandler = subscriptionMap.values().stream()
				.filter(handler -> !handler.getTradeInfo().isLive()
						&& tradeId.equals(handler.getTradeInfo().getTradeId()))
				.findFirst();

		if (tradeHandler.isPresent()) {
			LOGGER.info("Found a paper trade handler for the trade id - {}, tradeInfo - {}", tradeId,
					tradeHandler.get().getTradeInfo());
			tradeHandler.get().setSquareOff();
		} else {
			LOGGER.error("Paper trade handler not exists for the trade id - {}, checking database for active trade",
					tradeId);
			Trade trade = persistence.getByTradeId(tradeId);
			try {
				if (trade != null && !trade.isLive() && persistence.isTradeActive(trade)) {
					KiteSessionService session = kiteModule.getKiteSessions()
							.get(kiteModule.getMasterAccount().getUserName());
					if (session != null) {
						Quote quote = session.getQuote(trade.getOptionSymbol());
						updatePaperTradeInDB(trade, quote.averagePrice);
					} else {
						LOGGER.error("No active session for master account, couldn't square off trade id - {}",
								tradeId);
					}
				} else {
					LOGGER.error("Active paper trade for trade id - {} is not present in database", tradeId);
				}
			} catch (Exception e) {
				LOGGER.error("Error in square off paper trade, tradeId - {}, symbol - {}", tradeId, e);
			}
		}
	}

	private void updatePaperTradeInDB(Trade trade, double averagePrice) {
		TradeInfo tradeInfo = TradeUtil.convertToTradeInfo(trade);
		List<PositionInfo> positionInfoList = new ArrayList<>();
		for (Position position : positionPersistence.getByTradeId(trade.getTradeId())) {
			position.setOptionExitPrice(averagePrice);
			position.setExitTime(new Date());
			positionInfoList.add(TradeUtil.convertToPositionInfo(position));
		}
		tradeInfo.setPositionInfoList(positionInfoList);
		persistence.updateTrade(tradeInfo);

		LOGGER.info("Paper trade updated in the database, tradeInfo - {}", tradeInfo);
	}

	public void squareOffLiveTradeById(String tradeId) {
		Optional<TradeHandler> tradeHandler = subscriptionMap.values().stream()
				.filter(handler -> handler.getTradeInfo().isLive()
						&& tradeId.equals(handler.getTradeInfo().getTradeId()))
				.findFirst();

		if (tradeHandler.isPresent()) {
			LOGGER.info("Found a live trade handler for the trade id - {}, tradeInfo - {}", tradeId,
					tradeHandler.get().getTradeInfo());
			tradeHandler.get().setSquareOff();
		} else {
			LOGGER.error("Live trade handler not exists for the trade id - {}, checking database for active trade",
					tradeId);
			Trade trade = persistence.getByTradeId(tradeId);

			try {
				if (trade != null && trade.isLive() && persistence.isTradeActive(trade)) {
					LOGGER.info("Attempting to place a stop loss market order for tradeId - {}", tradeId);

					KiteSessionService session = kiteModule.getKiteSessions()
							.get(kiteModule.getMasterAccount().getUserName());
					if (session != null) {
						updateLiveTradeInDB(session, tradeId, trade);
					} else {
						LOGGER.error("No active session for master account, couldn't square off trade id - {}",
								tradeId);
					}
				} else {
					LOGGER.error("Active live trade for trade id - {} is not present in database", tradeId);
				}
			} catch (Exception e) {
				LOGGER.error("Error in square off live trade, tradeId - {}", tradeId, e);
			}
		}
	}

	private void updateLiveTradeInDB(KiteSessionService session, String tradeId, Trade trade) {
		Order order = null;
		try {
			List<Position> positionList = positionPersistence.getByTradeId(trade.getTradeId());
			int remainingQuantity = positionList.stream()
					.map((Position position) -> position.getQuantity() - position.getSoldQuantity())
					.collect(Collectors.summingInt(Integer::intValue));
			Order slmOrder = session.placeMarketSellOrder(trade.getOptionSymbol(),
					remainingQuantity);
			do {
				LOGGER.info(
						"Attempting to get the order history for the stop loss marker order, tradeId - {}",
						tradeId);
				List<Order> orderList = session.getOrder(slmOrder.orderId);
				order = orderList.get(orderList.size() - 1);
				LOGGER.info("Stop loss market order status for the tradeId - {} is {}", tradeId,
						order.status);
				wait(1);
			} while (!Constants.ORDER_COMPLETE.equals(order.status));

			TradeInfo tradeInfo = TradeUtil.convertToTradeInfo(trade);
			List<PositionInfo> positionInfoList = new ArrayList<>();
			for (Position position : positionList) {
				position.setOptionExitPrice(Double.valueOf(decimalFormat.format(Double.valueOf(
						order.averagePrice))));
				position.setExitTime(new Date());
				positionInfoList.add(TradeUtil.convertToPositionInfo(position));
			}
			tradeInfo.setPositionInfoList(positionInfoList);
			persistence.updateTrade(tradeInfo);

			LOGGER.info("Live trade updated in the database, tradeInfo - {}", tradeInfo);
		} catch (TradeException e) {
			LOGGER.error("Error in fetching the order history, tradeId - {}", tradeId);
		}
	}

	public void wait(int seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException e) {
			LOGGER.error("Error while waiting for {} seconds", seconds);
			Thread.currentThread().interrupt();
		}
	}

	public boolean isPaperTradeAllowed(Signal signal, InstrumentSubscription subscription) {
		boolean allowed = true;
		if (!allowPaperTrade) {
			LOGGER.info("Paper trade is not allowed, allowPaperTrade = {}", allowPaperTrade);
			allowed = false;
		} else if (!subscription.isPaperTradable()) {
			LOGGER.info("Paper trade is not allowed for this instrument, signal = {}, subscription - {}",
					signal, subscription);
			allowed = false;
		} else if (!SignalGeneratorConstants.THREE_MINUTES.equals(signal.getAggregationType())) {
			LOGGER.info("{} is not tradable for the aggregation type {}, signal - {}", signal.getName(),
					signal.getAggregationType(), signal);
			allowed = false;
		}

		return allowed;
	}

	public boolean isLiveTradeAllowed(Signal signal, InstrumentSubscription subscription) {
		boolean allowed = true;
		if (!subscription.isTradable()) {
			LOGGER.info("{} is not tradable, signal - {}", signal.getName(), signal);
			allowed = false;
		} else if (!SignalGeneratorConstants.THREE_MINUTES.equals(signal.getAggregationType())) {
			LOGGER.info("{} is not tradable for the aggregation type {}, signal - {}", signal.getName(),
					signal.getAggregationType(), signal);
			allowed = false;
		} else if (!allowLiveTrade) {
			LOGGER.info("Live trade is not allowed, allowLiveTrade = {}", allowLiveTrade);
			allowed = false;
		} else if (persistence.getActiveLiveTradeCount(signal.getToken()) >= subscription.getNumParallelTrades()) {
			LOGGER.info("Number of active live trades is exceeded the allowed number of trades in parallel, " +
					"subscription = {}", subscription);
			allowed = false;
		} else if (persistence.getAllLiveTradeCount(signal.getToken()) >= subscription.getNumTradesPerDay()) {
			LOGGER.info("Number of live trades for the instrument {} is exceeded number of allowed trades per day, " +
					"subscription = {}", signal.getName(), subscription);
			allowed = false;
		}

		return allowed;
	}

	public void loadTradesFromDB() {
		LOGGER.info("Loading the trades from database after restart ...");
		List<Trade> tradeList;
		if (allowLiveTrade) {
			tradeList = persistence.getActiveLiveTrades();
			restartTradeJobs(tradeList);
		}
		tradeList = persistence.getActivePaperTrades();
		restartPaperTradeJobs(tradeList);
	}

	public void restartPaperTradeJobs(List<Trade> tradeList) {
		for (Trade trade : tradeList) {
			TradeInfo tradeInfo = TradeUtil.convertToTradeInfo(trade);
			jobScheduler.rescheduleTradeHandlerJob(tradeInfo, this);
		}
	}

	public void restartTradeJobs(List<Trade> tradeList) {
		for (Trade trade : tradeList) {
			String orderId = trade.getOrderId();
			try {
				KiteSessionService session = kiteModule.getKiteSessions()
						.get(kiteModule.getMasterAccount().getUserName());
				if (session != null) {
					List<Order> orderList = session.getOrder(orderId);
					if (!orderList.isEmpty()) {
						Order order = orderList.get(orderList.size() - 1);
						TradeInfo tradeInfo = TradeUtil.convertToTradeInfo(trade);
						if (Constants.ORDER_OPEN.equals(order.status)
								|| Constants.ORDER_TRIGGER_PENDING.equals(order.status)) {
							jobScheduler.rescheduleTradeHandlerJob(tradeInfo, this);
						} else {
							updateClosedTradeInDB(tradeInfo, trade);
						}
					}
				} else {
					LOGGER.error("No active session for master account, couldn't start the trade jobs now");
				}
			} catch (TradeException e) {
				LOGGER.error("Error while loading the trades from database after restart, trade - {}", trade, e);
			}
		}
	}

	private void updateClosedTradeInDB(TradeInfo tradeInfo, Trade trade) {
		LOGGER.info("Order is not active now, updating the trade to closed - {}", trade);
		List<PositionInfo> positionInfoList = new ArrayList<>();
		for (Position position : positionPersistence.getByTradeId(trade.getTradeId())) {
			position.setClosed(true);
			positionInfoList.add(TradeUtil.convertToPositionInfo(position));
		}
		tradeInfo.setPositionInfoList(positionInfoList);
		persistence.updateTrade(tradeInfo);
	}

	public boolean isFirstCandleSignal(Signal signal) {
		ZonedDateTime candleTime = ZonedDateTime.ofInstant(signal.getTickTime().toInstant(), ZoneId.systemDefault())
				.truncatedTo(ChronoUnit.MINUTES);
		ZonedDateTime marketStartTime = MarketTimeUtil.getMarketStartTime();

		return candleTime.isBefore(marketStartTime) || candleTime.isEqual(marketStartTime);
	}

	public void handleSignal(MarketTrendInfo trendInfo, Signal signal, InstrumentSubscription subscription,
			InstrumentIndicators instrumentIndicators) {
		LOGGER.info("Attempting to place order, signal = {}", signal);

		if (MarketTimeUtil.getTradingStartTime().toLocalDateTime().isAfter(LocalDateTime.now())) {
			LOGGER.info("Signal is ignored, as it is generated before the trading start time, signal - {}", signal);
		} else if (MarketTimeUtil.getTradingCutOffTime().toLocalDateTime().isBefore(LocalDateTime.now())) {
			LOGGER.info("Signal is ingored, as current time is ahead of market cut off time {}", LocalDateTime.now());
		} else {
			if (SignalGeneratorConstants.THREE_MINUTES.equals(signal.getAggregationType())) {
				List<String> strategies = jobScheduler.getLiveJobStrategies();
				if (strategies.contains(signal.getStrategy())) {
					// Publish signals only if live jobs configured
					publishSignal("Signal", signal);
				}
			}
			scheduleTrades(trendInfo, signal, subscription, instrumentIndicators);
		}
	}

	private void scheduleTrades(MarketTrendInfo trendInfo, Signal signal, InstrumentSubscription subscription,
			InstrumentIndicators instrumentIndicators) {
		trendReversalHandler.handleTrendReversalForLiveTrades(signal, subscriptionMap);
		if (OptionType.BUY_CE.name().equals(signal.getTradeSignal())
				|| OptionType.BUY_PE.name().equals(signal.getTradeSignal())) {
			if (isLiveTradeAllowed(signal, subscription)) {
				try {
					jobScheduler.scheduleLiveTrades(trendInfo, signal, subscription, this, instrumentIndicators);
				} catch (Exception e) {
					LOGGER.error("Error in scheduling live trade, symbol - {}", signal.getOptionSymbol(), e);
				}
			} else {
				LOGGER.info("Live trades is not allowed, skipping signal - {}", signal);
			}
		}

		trendReversalHandler.handleTrendReversalForPaperTrades(signal, subscriptionMap);
		if (OptionType.BUY_CE.name().equals(signal.getTradeSignal())
				|| OptionType.BUY_PE.name().equals(signal.getTradeSignal())) {
			if (isPaperTradeAllowed(signal, subscription)) {
				jobScheduler.schedulePaperTrades(trendInfo, signal, subscription, this, instrumentIndicators);
			} else {
				LOGGER.info("Paper trades is not allowed, skipping signal - {}", signal);
			}
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (!update.hasMessage() || !update.getMessage().hasText()) {
			return;
		}

		String messageText = update.getMessage().getText();
		LOGGER.info("Telegram Message received  = {}", messageText);

		String response;
		switch (messageText) {
		case MENU_ALL_TRADES:
			response = convertTradesToJson(tradeRepository.findAllLiveTrades());
			break;
		case MENU_OPEN_TRADES:
			response = convertTradesToJson(tradeRepository.findActiveLiveTrades());
			break;
		case MENU_CLOSED_TRADES:
			response = convertTradesToJson(tradeRepository.findClosedLiveTrades());
			break;
		case MENU_SQUAREOFF_TRADE:
			response = "Not yet supported";
			break;
		case MENU_SQUUAREOFFALL_TRADES:
			squareOffLiveTrades();
			response = "Square off triggered. Please check the trade status.";
			break;
		case MENU_ENABLELIVE_TRADES:
			setAllowLiveTrade(true);
			response = "Live Trade Enabled!";
			break;
		case MENU_DISABLELIVE_TRADES:
			setAllowLiveTrade(false);
			response = "Live Trade Disabled!";
			break;
		default:
			response = "Unknown Message!. Pls try again...";
			break;
		}

		sendMessageToChannel(response, String.valueOf(update.getMessage().getChatId()));
	}

}

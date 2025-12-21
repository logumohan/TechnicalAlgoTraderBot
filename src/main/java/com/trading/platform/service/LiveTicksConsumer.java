package com.trading.platform.service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.persistence.InstrumentsRepository;
import com.trading.platform.persistence.SubscriptionReadOnlyRepositoryIf;
import com.trading.platform.persistence.entity.Instrument;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.service.trade.OptionTickConsumer;
import com.trading.platform.util.MarketTimeUtil;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.ticker.OnConnect;
import com.zerodhatech.ticker.OnDisconnect;
import com.zerodhatech.ticker.OnError;
import com.zerodhatech.ticker.OnOrderUpdate;
import com.zerodhatech.ticker.OnTicks;

@Component
public class LiveTicksConsumer implements OnConnect, OnDisconnect, OnError, OnOrderUpdate, OnTicks {

	private static final Logger LOGGER = LogManager.getLogger(LiveTicksConsumer.class);

	private KiteTicker tickerProvider;

	private Set<Long> tokens;

	private Set<Long> consumerOptionTokens;

	private Set<Long> consumerTokens;

	private Map<Long, List<OptionTickConsumer>> consumers;

	private Map<Long, String> tokenToNameMap;

	private TickConsumerListener tickConsumerListener;

	@Autowired
	private InstrumentsRepository instrumentsRepository;

	@Autowired
	private SubscriptionReadOnlyRepositoryIf subscriptionReadOnlyRepository;

	private double vixLastTradePrice;

	public LiveTicksConsumer() {
		this.tokens = new HashSet<>();
		this.consumerOptionTokens = new HashSet<>();
		this.consumerTokens = new HashSet<>();
		this.consumers = new ConcurrentHashMap<>();
		this.tokenToNameMap = new ConcurrentHashMap<>();
	}

	public void start(String accessToken, String apiKey) throws KiteException {
		Instant deleteOlder = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(7, ChronoUnit.DAYS);
		instrumentsRepository.deleteOlderThan(Date.from(deleteOlder));

		this.tickerProvider = new KiteTicker(accessToken, apiKey);
		tickerProvider.setOnConnectedListener(this);
		tickerProvider.setOnDisconnectedListener(this);
		tickerProvider.setOnOrderUpdateListener(this);
		tickerProvider.setOnErrorListener(this);
		tickerProvider.setOnTickerArrivalListener(this);
		tickerProvider.setTryReconnection(true);
		tickerProvider.setMaximumRetries(Integer.MAX_VALUE);
		tickerProvider.setMaximumRetryInterval(5);

		this.tickerProvider.connect();

		if (this.tickerProvider.isConnectionOpen()) {
			LOGGER.info("Connection established with ticks provider");
			updateSubscriptions();
		}
	}

	public void stop() {
		tickerProvider.disconnect();
		LOGGER.info("Tick provider stopped");
	}

	public void addTickConsumerListener(TickConsumerListener tickConsumerListener) {
		this.tickConsumerListener = tickConsumerListener;
	}

	public void updateSubscriptions() {
		updateTokens();
		subscribe();
	}

	public void subscribe() {
		LOGGER.info("Subscribing for the ticks, tokens - {}", tokens);
		tickerProvider.subscribe(new ArrayList<>(tokens));
		tickerProvider.setMode(new ArrayList<>(tokens), KiteTicker.modeFull);
	}

	public void unsubscribe() {
		LOGGER.info("Unsubscribing for the ticks, tokens - {}", tokens);
		tickerProvider.unsubscribe(new ArrayList<>(tokens));
	}

	public void subscribeTicks(OptionTickConsumer consumer) {
		Long token = consumer.getToken();
		if (token != null) {
			LOGGER.info("Subscribe ticks for consumers, token - {}", token);
			consumers.putIfAbsent(token, new ArrayList<>());
			consumers.get(token).add(consumer);

			if (!consumerTokens.contains(token)) {
				consumerTokens.add(token);
				LOGGER.info("Ticks subscription for consumers is successful, token - {}", token);
			}
		}

		Long optionToken = consumer.getOptionToken();
		if (optionToken != null) {
			LOGGER.info("Subscribe option ticks for consumers, token - {}", optionToken);
			consumers.putIfAbsent(optionToken, new ArrayList<>());
			consumers.get(optionToken).add(consumer);

			if (!consumerOptionTokens.contains(optionToken)) {
				consumerOptionTokens.add(optionToken);
				LOGGER.info("Option ticks subscription for consumers is successful, token - {}", optionToken);
			}
		}

		updateSubscriptions();
	}

	public void unsubscribeTicks(OptionTickConsumer consumer) {
		Long token = consumer.getToken();
		if (token != null && consumers.get(token) != null) {
			LOGGER.info("Unsubscribe ticks for consumers, token - {}", token);
			consumers.get(token).remove(consumer);
			if (consumers.get(token).isEmpty()) {
				consumerTokens.remove(token);
				consumers.remove(token);
				LOGGER.info("Ticks unsubscribe for consumers is successful, token - {}", token);
			}
		}

		Long optionToken = consumer.getOptionToken();
		if (optionToken != null && consumers.get(optionToken) != null) {
			LOGGER.info("Unsubscribe option ticks for consumers, token - {}", optionToken);
			consumers.get(optionToken).remove(consumer);
			if (consumers.get(optionToken).isEmpty()) {
				consumerOptionTokens.remove(optionToken);
				consumers.remove(optionToken);
				LOGGER.info("Option ticks unsubscribe for consumers is successful, token - {}", optionToken);
			}
		}

		updateSubscriptions();
	}

	public void updateTokens() {
		List<InstrumentSubscription> subscriptionList = subscriptionReadOnlyRepository.getAll();
		tokens.clear();

		// Add VIX
		tokens.add(SignalGeneratorConstants.VIX_TOKEN);

		// Index tokens
		for (InstrumentSubscription subscription : subscriptionList) {
			tokens.add(subscription.getToken());
			tokenToNameMap.put(subscription.getToken(), subscription.getName());
		}

		// Option tokens
		tokens.addAll(consumerTokens);
		tokens.addAll(consumerOptionTokens);

		LOGGER.info("updateTokens: List of tokens - {}, consumer tokens - {}, consumer option tokens - {}", tokens,
				consumerTokens, consumerOptionTokens);
	}

	public double getVixLastTradedPrice() {
		return this.vixLastTradePrice;
	}

	@Override
	public void onConnected() {
		LOGGER.info("onConnected: Subscribe tokens");
		updateSubscriptions();
	}

	@Override
	public void onDisconnected() {
		LOGGER.info("onDisconnected: Unsubscribe tokens");
	}

	@Override
	public void onOrderUpdate(Order order) {
		LOGGER.info("onOrderUpdate: Order update - {}", order);
	}

	@Override
	@LogExecutionTime
	public void onTicks(ArrayList<Tick> ticks) {
		if (MarketTimeUtil.isMarketClosed()) {
			LOGGER.trace("onTicks: Skipping the ticks as market is closed now, {}",
					ZonedDateTime.now());
			return;
		}

		LOGGER.trace("onTicks: Ticks received, size - {}", ticks.size());
		ArrayList<Instrument> instrumentList = new ArrayList<>();
		ArrayList<Instrument> consumersList = new ArrayList<>();
		ArrayList<Instrument> consumersOptionList = new ArrayList<>();
		convertTicks(ticks, instrumentList, consumersList, consumersOptionList);
		publishConvertedTicks(instrumentList, consumersList, consumersOptionList);
	}

	private void convertTicks(ArrayList<Tick> ticks, ArrayList<Instrument> instrumentList,
			ArrayList<Instrument> consumersList, ArrayList<Instrument> consumersOptionList) {
		for (Tick tick : ticks) {
			if (tick.getTickTimestamp() == null) {
				// Skip processing the ticks those are without tick time
				LOGGER.trace("Skipping tick as it is received without tick time, token - {}",
						tick.getInstrumentToken());
				continue;
			}
			try {
				Instrument instrument = new Instrument();
				instrument.setTickTime(tick.getTickTimestamp());
				instrument.setToken(tick.getInstrumentToken());
				instrument.setOpenPrice(tick.getOpenPrice());
				instrument.setClosePrice(tick.getClosePrice());
				instrument.setHighPrice(tick.getHighPrice());
				instrument.setLowPrice(tick.getLowPrice());
				instrument.setLastTradedPrice(tick.getLastTradedPrice());
				instrument.setVolumeTraded(tick.getVolumeTradedToday());
				instrument.setTotalBuyQuantity(tick.getTotalBuyQuantity());
				instrument.setTotalSellQuantity(tick.getTotalSellQuantity());

				if (instrument.getToken() == SignalGeneratorConstants.VIX_TOKEN) {
					vixLastTradePrice = instrument.getLastTradedPrice();
					LOGGER.debug("VIX LTP = {}", vixLastTradePrice);
				} else {
					String name = tokenToNameMap.get(instrument.getToken());
					if (name != null) {
						instrument.setName(name);
						instrumentList.add(instrument);
						consumersList.add(instrument);
					} else {
						instrument.setName(String.valueOf(instrument.getToken()));
						consumersOptionList.add(instrument);
					}
				}

				LOGGER.trace("onTicks: Ticks converted as instrument - {}", instrument.getToken());
			} catch (Exception e) {
				LOGGER.error("onTicks: Error processing the ticks - {}", tick.getInstrumentToken(), e);
			}
		}
	}

	private void publishConvertedTicks(ArrayList<Instrument> instrumentList, ArrayList<Instrument> consumersList,
			ArrayList<Instrument> consumersOptionList) {
		if (!instrumentList.isEmpty()) {
			instrumentsRepository.saveAll(instrumentList);
		}
		if (!consumersOptionList.isEmpty()) {
			for (Instrument instrument : consumersOptionList) {
				consumers.get(instrument.getToken()).stream()
						.forEach(consumer -> consumer.onOptionTick(instrument));
			}
		}
		if (!consumersList.isEmpty()) {
			for (Instrument instrument : consumersList) {
				consumers.get(instrument.getToken()).stream()
						.forEach(consumer -> consumer.onTick(instrument));
			}
		}
	}

	@Override
	public void onError(Exception e) {
		LOGGER.error("onError: Exception", e);
		if (tickConsumerListener != null) {
			stop();
			tickConsumerListener.tickProviderSessionExpired();
		}
	}

	@Override
	public void onError(KiteException e) {
		LOGGER.error("onError: Kite Exception", e);
	}

	@Override
	public void onError(String error) {
		LOGGER.error("onError: Error - {}", error);
	}

}

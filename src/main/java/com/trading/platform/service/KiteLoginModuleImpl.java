package com.trading.platform.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.persistence.UserAccountInfoRepository;
import com.trading.platform.persistence.entity.UserAccount;
import com.trading.platform.service.indicators.DailyIndicatorService;
import com.trading.platform.service.indicators.FifteenMinutesIndicatorService;
import com.trading.platform.service.indicators.FiveMinutesIndicatorService;
import com.trading.platform.service.indicators.OneHourIndicatorService;
import com.trading.platform.service.indicators.OneMinuteIndicatorService;
import com.trading.platform.service.indicators.ThreeMinutesIndicatorService;
import com.trading.platform.service.kite.KiteSessionService;
import com.trading.platform.service.trade.OptionTickConsumer;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;

@Component
@ComponentScan("com.trading.platform")
public class KiteLoginModuleImpl implements TickConsumerListener {

	private static final Logger LOGGER = LogManager.getLogger(KiteLoginModuleImpl.class);

	private UserAccount masterAccount;

	private LiveTicksConsumer liveTicksConsumer;

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

	private UserAccountInfoRepository userRepository;

	private Map<String, KiteSessionService> kiteSessions;

	@Autowired
	public KiteLoginModuleImpl(LiveTicksConsumer liveTicksConsumer,
			UserAccountInfoRepository userRepository) {
		this.liveTicksConsumer = liveTicksConsumer;
		this.userRepository = userRepository;
		this.kiteSessions = new LinkedHashMap<>();

		doLogin();
	}

	@LogExecutionTime
	public void doLogin() {
		List<UserAccount> userAccountList = userRepository.getAllTradableUserAccount();
		for (UserAccount userAccount : userAccountList) {
			KiteSessionService session = new KiteSessionService(new RequestTokenGenerator(), userAccount);
			session.doLogin();
			kiteSessions.put(userAccount.getUserName(), session);
		}

		KiteSessionService masterSession = kiteSessions.get(getMasterAccount().getUserName());
		while (!masterSession.isLoggedIn()) {
			LOGGER.info("Kite session for master account is not ready yet, user - {}",
					getMasterAccount().getUserName());
			wait(1);
		}

		liveTicksConsumer.addTickConsumerListener(this);

		try {
			liveTicksConsumer.start(masterSession.getAccessToken(), getMasterAccount().getApiKey());
			LOGGER.info("Ticks consumer is started");
		} catch (KiteException | Exception e) {
			LOGGER.error("Failed to start Live Ticks Consumer for master account, user - {}",
					getMasterAccount().getUserName(), e);
		}
	}

	@Override
	public void tickProviderSessionExpired() {
		LOGGER.error("Live tick provider session expired. Attempting to login again...");

		KiteSessionService masterSession = kiteSessions.get(getMasterAccount().getUserName());
		masterSession.doLogin();
		while (!masterSession.isLoggedIn()) {
			LOGGER.info("Kite session for master account is not ready yet, user - {}",
					getMasterAccount().getUserName());
		}

		reconnectConsumer(masterSession);
	}

	private void reconnectConsumer(KiteSessionService masterSession) {
		boolean connected = false;
		do {
			try {
				liveTicksConsumer.start(masterSession.getAccessToken(), getMasterAccount().getApiKey());
				connected = true;
			} catch (Exception | KiteException e) {
				LOGGER.error("Error while attempting to reconnecting consumer after session expiry...", e);
				connected = false;
			}
			if (!connected) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					LOGGER.error("Interrupted while waiting for reconnecting consumer after session expiry...", e);
					Thread.currentThread().interrupt();
				}
			}
		} while (!connected);
	}

	public UserAccount getMasterAccount() {
		if (masterAccount == null) {
			masterAccount = userRepository.getMasterAccount();
		}

		return masterAccount;
	}

	public Map<String, KiteSessionService> getKiteSessions() {
		return kiteSessions;
	}

	public synchronized void subscribeTicks(OptionTickConsumer consumer) {
		liveTicksConsumer.subscribeTicks(consumer);
		oneMinuteIndicatorService.subscribeOneMinTicks(consumer);
		switch (consumer.getAggregationType()) {
		case SignalGeneratorConstants.ONE_MINUTE:
			oneMinuteIndicatorService.subscribeTicks(consumer);
			break;
		case SignalGeneratorConstants.THREE_MINUTES:
			threeMinutesIndicatorService.subscribeTicks(consumer);
			break;
		case SignalGeneratorConstants.FIVE_MINUTES:
			fiveMinutesIndicatorService.subscribeTicks(consumer);
			break;
		case SignalGeneratorConstants.FIFTEEN_MINUTES:
			fifteenMinutesIndicatorService.subscribeTicks(consumer);
			break;
		case SignalGeneratorConstants.ONE_HOUR:
			oneHourIndicatorService.subscribeTicks(consumer);
			break;
		case SignalGeneratorConstants.ONE_DAY:
			dailyIndicatorService.subscribeTicks(consumer);
			break;
		default:
			break;
		}
	}

	public synchronized void unsubscribeTicks(OptionTickConsumer consumer) {
		liveTicksConsumer.unsubscribeTicks(consumer);
		oneMinuteIndicatorService.unsubscribeOneMinTicks(consumer);
		switch (consumer.getAggregationType()) {
		case SignalGeneratorConstants.ONE_MINUTE:
			oneMinuteIndicatorService.unsubscribeTicks(consumer);
			break;
		case SignalGeneratorConstants.THREE_MINUTES:
			threeMinutesIndicatorService.unsubscribeTicks(consumer);
			break;
		case SignalGeneratorConstants.FIVE_MINUTES:
			fiveMinutesIndicatorService.unsubscribeTicks(consumer);
			break;
		case SignalGeneratorConstants.FIFTEEN_MINUTES:
			fifteenMinutesIndicatorService.unsubscribeTicks(consumer);
			break;
		case SignalGeneratorConstants.ONE_HOUR:
			oneHourIndicatorService.unsubscribeTicks(consumer);
			break;
		case SignalGeneratorConstants.ONE_DAY:
			dailyIndicatorService.unsubscribeTicks(consumer);
			break;
		default:
			break;
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

}

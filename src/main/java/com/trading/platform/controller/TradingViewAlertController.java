package com.trading.platform.controller;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.controller.dto.TradingViewAlert;
import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.service.LiveTicksConsumer;
import com.trading.platform.service.signal.DailySignalGenerator;
import com.trading.platform.service.signal.FifteenMinuteSignalGenerator;
import com.trading.platform.service.signal.FiveMinuteSignalGenerator;
import com.trading.platform.service.signal.OneHourSignalGenerator;
import com.trading.platform.service.signal.OneMinuteSignalGenerator;
import com.trading.platform.service.signal.ThreeMinuteSignalGenerator;

@RestController
public class TradingViewAlertController {

	private static final Logger LOGGER = LogManager.getLogger(TradingViewAlertController.class);

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
	private LiveTicksConsumer ticksConsumer;

	@PostMapping("/trading-view-alert")
	@LogExecutionTime
	public ResponseEntity<String> handleTradingViewAlert(@RequestBody TradingViewAlert alert) {
		LOGGER.info("Alert Received - {}", alert);
		Signal signal = convertAlertToSingal(alert);

		if (signal.getAggregationType().equals(SignalGeneratorConstants.ONE_MINUTE)) {
			oneMinuteSignalGenerator.addSignal(signal);
		} else if (signal.getAggregationType().equals(SignalGeneratorConstants.THREE_MINUTES)) {
			threeMinuteSignalGenerator.addSignal(signal);
		} else if (signal.getAggregationType().equals(SignalGeneratorConstants.FIVE_MINUTES)) {
			fiveMinuteSignalGenerator.addSignal(signal);
		} else if (signal.getAggregationType().equals(SignalGeneratorConstants.FIFTEEN_MINUTES)) {
			fifteenMinuteSignalGenerator.addSignal(signal);
		} else if (signal.getAggregationType().equals(SignalGeneratorConstants.ONE_HOUR)) {
			oneHourSignalGenerator.addSignal(signal);
		} else if (signal.getAggregationType().equals(SignalGeneratorConstants.ONE_DAY)) {
			dailySignalGenerator.addSignal(signal);
		} else {
			LOGGER.error("Unknown aggregation type, skipping trading view alert - {}", alert);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private Signal convertAlertToSingal(TradingViewAlert alert) {
		Signal signal = new Signal();
		signal.setTickTime(new Date());
		signal.setToken(alert.getToken());
		signal.setStrategy(alert.getStrategy());
		signal.setName(alert.getName());
		signal.setAggregationType(alert.getAggregationType());
		signal.setLastTradedPrice(alert.getClosePrice());
		signal.setAverageTrueRange(40);
		signal.setVixLastTradedPrice(ticksConsumer.getVixLastTradedPrice());
		signal.setTradeSignal(generateTradeSignal(alert));

		return signal;
	}

	private String generateTradeSignal(TradingViewAlert alert) {
		String tradeSignal;
		if (alert.getPosition() != null) {
			if (alert.getSignal().equalsIgnoreCase("SELL") && alert.getPosition().equals("-1")) {
				tradeSignal = "BUY_PE";
			} else if (alert.getSignal().equalsIgnoreCase("BUY") && alert.getPosition().equals("0")) {
				tradeSignal = "SELL_PE";
			} else if (alert.getSignal().equalsIgnoreCase("BUY") && alert.getPosition().equals("1")) {
				tradeSignal = "BUY_CE";
			} else if (alert.getSignal().equalsIgnoreCase("SELL") && alert.getPosition().equals("0")) {
				tradeSignal = "SELL_CE";
			} else {
				tradeSignal = toTradeSignal(alert.getSignal(), alert.getOptionType());
			}
		} else {
			tradeSignal = toTradeSignal(alert.getSignal(), alert.getOptionType());
		}

		return tradeSignal;
	}

	private String toTradeSignal(String signal, String optionType) {
		return String.join("_", signal.toUpperCase(), optionType.toUpperCase());
	}
}

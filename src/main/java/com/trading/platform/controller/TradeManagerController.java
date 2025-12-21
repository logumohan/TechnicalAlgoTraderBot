package com.trading.platform.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.controller.dto.SignalDto;
import com.trading.platform.controller.dto.TradeDto;
import com.trading.platform.persistence.SubscriptionReadOnlyRepositoryIf;
import com.trading.platform.persistence.TradeInfoRepository;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.persistence.entity.Trade;
import com.trading.platform.service.trade.TradeManager;

@RestController
public class TradeManagerController {

	private static final Logger LOGGER = LogManager.getLogger(TradeManagerController.class);

	@Autowired
	private TradeManager tradeManager;

	@Autowired
	private TradeInfoRepository tradeRepository;

	@Autowired
	private SubscriptionReadOnlyRepositoryIf subscriptionRepository;

	@PostMapping("/live-trade/{allow-trade}")
	@LogExecutionTime
	public ResponseEntity<String> allowLiveTrade(@PathVariable(name = "allow-trade") boolean allowTrade) {
		tradeManager.setAllowLiveTrade(allowTrade);
		LOGGER.info("Live Trade is set to {}", allowTrade);

		return new ResponseEntity<>("TradeManager.allowLiveTrade=" + allowTrade, HttpStatus.OK);
	}

	@PostMapping("/paper-trade/{allow-trade}")
	@LogExecutionTime
	public ResponseEntity<String> allowPaperTrade(@PathVariable(name = "allow-trade") boolean allowTrade) {
		tradeManager.setAllowPaperTrade(allowTrade);
		LOGGER.info("Paper Trade is set to {}", allowTrade);

		return new ResponseEntity<>("TradeManager.allowPaperTrade=" + allowTrade, HttpStatus.OK);
	}

	@GetMapping("/historical/trades/live/{token}")
	@LogExecutionTime
	public ResponseEntity<List<TradeDto>> getHistoricalLiveTrades(
			@PathVariable(name = "token", required = false) Optional<Long> token) {
		List<Trade> tradeList = new ArrayList<>();
		if (token.isPresent()) {
			tradeList = tradeRepository.findHistoricalTrades(token.get(), true);
		} else {
			List<Long> tokens = subscriptionRepository.getAllTokens();
			LOGGER.info("List of tokens subscribed - {}", tokens);
			for (Long subscribedToken : tokens) {
				LOGGER.info("Attempting to fetch historical trades for the token - {}", subscribedToken);
				tradeList.addAll(tradeRepository.findHistoricalTrades(subscribedToken, true));
				LOGGER.info("Historical trades for token - {}, {}", subscribedToken, tradeList);
			}
		}
		return new ResponseEntity<>(tradeList.stream().map(TradeDto::of).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	@GetMapping("/historical/trades/paper/{token}")
	@LogExecutionTime
	public ResponseEntity<List<TradeDto>> getHistoricalPaperTrades(
			@PathVariable(name = "token", required = false) Optional<Long> token) {
		List<Trade> tradeList = new ArrayList<>();
		if (token.isPresent()) {
			tradeList = tradeRepository.findHistoricalTrades(token.get(), false);
		} else {
			List<Long> tokens = subscriptionRepository.getAllTokens();
			LOGGER.info("List of tokens subscribed - {}", tokens);
			for (Long subscribedToken : tokens) {
				LOGGER.info("Attempting to fetch historical trades for the token - {}", subscribedToken);
				tradeList.addAll(tradeRepository.findHistoricalTrades(subscribedToken, false));
				LOGGER.info("Historical trades for token - {}, {}", subscribedToken, tradeList);
			}
		}
		return new ResponseEntity<>(tradeList.stream().map(TradeDto::of).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	@GetMapping({ "/trades", "/trades/{trade-type}", "/trades/{trade-type}/{trade-status}" })
	@LogExecutionTime
	public ResponseEntity<List<TradeDto>> getTrades(
			@PathVariable(name = "trade-type", required = false) Optional<String> tradeType,
			@PathVariable(name = "trade-status", required = false) Optional<String> tradeStatus) {
		List<Trade> tradeList = getTradesByType(tradeType, tradeStatus);

		return new ResponseEntity<>(tradeList.stream().map(TradeDto::of).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	private List<Trade> getTradesByType(Optional<String> tradeType, Optional<String> tradeStatus) {
		if (tradeType.isPresent()) {
			if ("live".equalsIgnoreCase(tradeType.get())) {
				return getLiveTradesByStatus(tradeStatus);
			} else {
				return getPaperTradesByStatus(tradeStatus);
			}
		} else {
			return tradeRepository.findAllTrades();
		}
	}

	private List<Trade> getLiveTradesByStatus(Optional<String> tradeStatus) {
		if (tradeStatus.isPresent()) {
			if ("active".equalsIgnoreCase(tradeStatus.get())) {
				return tradeRepository.findActiveLiveTrades();
			} else {
				return tradeRepository.findClosedLiveTrades();
			}
		} else {
			return tradeRepository.findAllLiveTrades();
		}
	}

	private List<Trade> getPaperTradesByStatus(Optional<String> tradeStatus) {
		if (tradeStatus.isPresent()) {
			if ("active".equalsIgnoreCase(tradeStatus.get())) {
				return tradeRepository.findActivePaperTrades();
			} else {
				return tradeRepository.findClosedPaperTrades();
			}
		} else {
			return tradeRepository.findAllPaperTrades();
		}
	}

	@PutMapping({ "/square-off/live", "/square-off/live/{trade-id}" })
	@LogExecutionTime
	public ResponseEntity<String> squareOffLiveTrades(
			@PathVariable(name = "trade-id", required = false) Optional<String> tradeId) {
		if (tradeId.isPresent()) {
			tradeManager.squareOffLiveTradeById(tradeId.get());
			return new ResponseEntity<>("Squared off live trade with id - " + tradeId.get(), HttpStatus.OK);
		} else {
			tradeManager.squareOffLiveTrades();
			return new ResponseEntity<>("Squared off all live trades", HttpStatus.OK);
		}
	}

	@PutMapping({ "/square-off/paper", "/square-off/paper/{trade-id}" })
	@LogExecutionTime
	public ResponseEntity<String> squareOffPaperTrades(
			@PathVariable(name = "trade-id", required = false) Optional<String> tradeId) {
		if (tradeId.isPresent()) {
			tradeManager.squareOffPaperTradeById(tradeId.get());
			return new ResponseEntity<>("Squared off paper trade with id - " + tradeId.get(), HttpStatus.OK);
		} else {
			tradeManager.squareOffPaperTrades();
			return new ResponseEntity<>("Squared off all paper trades", HttpStatus.OK);
		}
	}

	@PostMapping("/place-order")
	@LogExecutionTime
	public ResponseEntity<String> placeOrder(@RequestBody SignalDto signalDto) {
		LOGGER.info("Attempting to send signal to trade manager, signal - {}", signalDto);

		InstrumentSubscription subscription = subscriptionRepository.getByToken(signalDto.getToken());

		Signal signal = new Signal();
		signal.setTickTime(new Date());
		signal.setToken(signalDto.getToken());
		signal.setName(signalDto.getName());
		signal.setTradeSignal(signalDto.getTradeSignal());
		signal.setOptionSymbol(signalDto.getOptionSymbol());
		signal.setAggregationType(signalDto.getAggregationType());
		signal.setStrikePrice(signalDto.getStrikePrice());
		signal.setLastTradedPrice(signalDto.getLastTradedPrice());
		signal.setAverageTrueRange(signalDto.getAverageTrueRange());
		signal.setStrategy(signalDto.getStrategy());

		LOGGER.info("Sending the signal to trade manager, signal- {}", signal);
		tradeManager.handleSignal(signal, subscription, null);

		return new ResponseEntity<>("Order Placed", HttpStatus.OK);
	}

}

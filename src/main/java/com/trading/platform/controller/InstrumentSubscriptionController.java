package com.trading.platform.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.controller.dto.SubscriptionDto;
import com.trading.platform.persistence.InstrumentSubscriptionRepository;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.service.LiveTicksConsumer;
import com.trading.platform.service.kite.KiteSessionService;
import com.zerodhatech.models.Instrument;

@RestController
public class InstrumentSubscriptionController {

	private static final Logger LOGGER = LogManager.getLogger(InstrumentSubscriptionController.class);

	@Autowired
	private KiteLoginModuleImpl kiteModule;

	@Autowired
	private LiveTicksConsumer ticksConsumer;

	@Autowired
	private InstrumentSubscriptionRepository repository;
	
	@GetMapping("/subscription")
	@LogExecutionTime
	public ResponseEntity<List<SubscriptionDto>> getSubscriptions() {
		List<InstrumentSubscription> subscritionList = repository.findAll();
		
		List<SubscriptionDto> susbcriptions = subscritionList.stream().map(SubscriptionDto::of).toList();
		
		return new ResponseEntity<>(susbcriptions, HttpStatus.OK);
	}

	@PostMapping("/subscribe")
	@LogExecutionTime
	public ResponseEntity<String> subscribeInstrument(@RequestBody List<SubscriptionDto> subscriptionList) {
		LOGGER.info("Subscribing list of instruments - {}", subscriptionList);
		if (subscriptionList == null || subscriptionList.isEmpty()) {
			return new ResponseEntity<>(SignalGeneratorConstants.SUBSCRIPTION_INVALID,
					HttpStatus.BAD_REQUEST);
		}

		Map<String, KiteSessionService> kiteSessions = kiteModule.getKiteSessions();
		KiteSessionService session = kiteSessions.get(kiteModule.getMasterAccount().getUserName());
		List<Instrument> instruments = session.getInstruments();
		if (!instruments.isEmpty()) {
			try {
				List<InstrumentSubscription> instrumentSubscriptions = new ArrayList<>();
				for (SubscriptionDto subscription : subscriptionList) {
					Instrument instrument = instruments.stream()
							.filter((Instrument inst) -> inst.getInstrument_token() == subscription.getToken())
							.findAny().orElse(null);

					if (instrument != null) {
						instrumentSubscriptions.add(subscription.toSubscription());
					} else {
						LOGGER.error("Invalid subscription, please check the token - {}", subscription);
						return new ResponseEntity<>(SignalGeneratorConstants.SUBSCRIPTION_INVALID,
								HttpStatus.BAD_REQUEST);
					}
				}

				repository.saveAll(instrumentSubscriptions);
				ticksConsumer.updateSubscriptions();
			} catch (Exception e) {
				LOGGER.error("Error in saving the list of subscription into database", e);
				return new ResponseEntity<>(SignalGeneratorConstants.SUBSCRIPTION_FAILURE,
						HttpStatus.SERVICE_UNAVAILABLE);
			}
		} else {
			LOGGER.error("Unable to retrieve the list of isntruments, subscription list - {}", subscriptionList);
			return new ResponseEntity<>(SignalGeneratorConstants.SUBSCRIPTION_FAILURE,
					HttpStatus.SERVICE_UNAVAILABLE);
		}

		return new ResponseEntity<>(SignalGeneratorConstants.SUBSCRIPTION_SUCCESS, HttpStatus.OK);
	}

}

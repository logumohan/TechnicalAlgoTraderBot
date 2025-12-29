package com.trading.platform.service.kite;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.persistence.entity.UserAccount;
import com.trading.platform.service.RequestTokenGenerator;
import com.trading.platform.service.trade.OrderParamsWrapper;
import com.trading.platform.service.trade.OrderWrapper;
import com.trading.platform.service.trade.TradeException;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.Instrument;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.Quote;
import com.zerodhatech.models.User;

public class KiteSessionService {

	private static final Logger LOGGER = LogManager.getLogger(KiteSessionService.class);

	private static final Logger AUTH_LOGGER = LogManager.getLogger("AUTH");

	private KiteConnect kiteConnect;

	private RequestTokenGenerator tokenGenerator;

	private UserAccount userAccount;

	private List<Instrument> instrumentsList;

	private boolean isLoggedIn = false;

	public KiteSessionService(RequestTokenGenerator tokenGenerator, UserAccount userAccount) {
		this.tokenGenerator = tokenGenerator;
		this.userAccount = userAccount;
	}

	@LogExecutionTime
	public void doLogin() {
		KiteLoginThread thread = new KiteLoginThread();
		thread.start();
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public String getAccessToken() {
		return kiteConnect.getAccessToken();
	}

	public Quote getQuote(String symbol) {
		Map<String, Quote> quoteMap = null;
		try {
			LOGGER.info("{}: Attempting to get quote for the symbol - {}", userAccount.getUserName(), symbol);
			quoteMap = kiteConnect.getQuote(new String[] { symbol });
			if (quoteMap == null || quoteMap.get(symbol) == null) {
				LOGGER.error("{}: Quote retrieval failed for symbol - {}, quote = {}",
						userAccount.getUserName(), symbol, quoteMap);
			}
		} catch (KiteException e) {
			LOGGER.error("{}: Error while fetching the quote for the symbol - {}, error = {}:{}",
					userAccount.getUserName(), symbol, e.code, e.message, e);
		} catch (JSONException | IOException e) {
			LOGGER.error("{}: Error while fetching the quote for symbol - {}", userAccount.getUserName(), symbol, e);
		}
		return Optional.ofNullable(quoteMap).map((Map<String, Quote> map) -> map.get(symbol)).orElse(null);
	}

	public Order placeMarketBuyOrder(String symbol, int quantity) throws TradeException {
		OrderParams orderParams = new OrderParams();
		orderParams.quantity = quantity;
		orderParams.price = 0.0;
		orderParams.triggerPrice = 0.0;
		orderParams.orderType = Constants.ORDER_TYPE_MARKET;
		orderParams.tradingsymbol = symbol;
		orderParams.product = Constants.PRODUCT_MIS;
		orderParams.exchange = Constants.EXCHANGE_NFO;
		orderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
		orderParams.validity = Constants.VALIDITY_DAY;

		OrderParamsWrapper wrapper = new OrderParamsWrapper(orderParams);
		try {
			LOGGER.info("{}: Placing a market buy order for the symbol - {}, order params - {}",
					userAccount.getUserName(), symbol, wrapper);
			Order order = kiteConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
			LOGGER.info("{}: Buy order (market) placed for the symbol - {}, order Id - {}, order params - {}",
					userAccount.getUserName(), symbol, order.orderId, wrapper);

			return order;
		} catch (KiteException e) {
			LOGGER.error("{}: Error received from kite for placing market buy order for the " +
					"symbol - {}, order params - {}, error = {}:{}", userAccount.getUserName(),
					symbol, wrapper, e.code, e.message, e);
			throw new TradeException(userAccount.getUserName() + " : " + e.code + " : " + e.message, e);
		} catch (JSONException | IOException e) {
			LOGGER.error("{}: Error in placing market buy order for symbol - {}, order params - {}",
					userAccount.getUserName(), symbol, wrapper, e);
			throw new TradeException(userAccount.getUserName() +
					" : Error while placing market buy order for symbol " + symbol, e);
		}
	}

	public Order placeMarketSellOrder(String symbol, int quantity) throws TradeException {
		OrderParams orderParams = new OrderParams();
		orderParams.quantity = quantity;
		orderParams.price = 0.0;
		orderParams.triggerPrice = 0.00;
		orderParams.orderType = Constants.ORDER_TYPE_MARKET;
		orderParams.tradingsymbol = symbol;
		orderParams.product = Constants.PRODUCT_MIS;
		orderParams.exchange = Constants.EXCHANGE_NFO;
		orderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;
		orderParams.validity = Constants.VALIDITY_DAY;

		OrderParamsWrapper wrapper = new OrderParamsWrapper(orderParams);
		try {
			LOGGER.info("{}: Placing a market sell order for the symbol - {}, order params - {}",
					userAccount.getUserName(), symbol, wrapper);
			Order order = kiteConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
			LOGGER.info("{}: Sell order (market) placed for the symbol - {}, order Id - {}, order params - {}",
					userAccount.getUserName(), symbol, order.orderId, wrapper);

			return order;
		} catch (KiteException e) {
			LOGGER.error("{}: Error received from kite for placing market sell order for the " +
					"symbol - {}, order params - {}, error = {}:{}", userAccount.getUserName(),
					symbol, wrapper, e.code, e.message, e);
			throw new TradeException(userAccount.getUserName() + " : " + e.code + " : " + e.message, e);
		} catch (JSONException | IOException e) {
			LOGGER.error("{}: Error in placing market sell order for symbol - {}, order params - {}",
					userAccount.getUserName(), symbol, wrapper, e);
			throw new TradeException(userAccount.getUserName() +
					" : Error while placing market sell order for symbol " + symbol, e);
		}
	}

	public Order placeSLOrder(String symbol, int quantity, double stopLossPrice) throws TradeException {
		OrderParams orderParams = new OrderParams();
		orderParams.quantity = quantity;
		orderParams.orderType = Constants.ORDER_TYPE_SL;
		orderParams.tradingsymbol = symbol;
		orderParams.price = stopLossPrice;
		orderParams.triggerPrice = stopLossPrice;
		orderParams.stoploss = stopLossPrice;
		orderParams.product = Constants.PRODUCT_MIS;
		orderParams.exchange = Constants.EXCHANGE_NFO;
		orderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;
		orderParams.validity = Constants.VALIDITY_DAY;

		OrderParamsWrapper wrapper = new OrderParamsWrapper(orderParams);
		try {
			Order order = kiteConnect.placeOrder(orderParams, Constants.VARIETY_REGULAR);
			LOGGER.info("{}: Stop loss order placed for the symbol - {}, order Id - {}, order params - {}",
					userAccount.getUserName(), symbol, order.orderId, wrapper);

			return order;
		} catch (KiteException e) {
			LOGGER.error("{}: Error placing the SL order for the symbol - {}, error = {}:{}, order params - {}",
					userAccount.getUserName(), symbol, wrapper, e.code, e.message, e);
			throw new TradeException(userAccount.getUserName() + " : " + e.code + " : " + e.message, e);
		} catch (JSONException | IOException e) {
			LOGGER.error("{}: Error in placing the stop loss order for symbol - {}, order params - {}",
					userAccount.getUserName(), symbol, wrapper, e);
			throw new TradeException(userAccount.getUserName() +
					" : Error while placing the stop loss order for symbol " + symbol, e);
		}
	}

	public Order cancelOrder(String orderId) throws TradeException {
		try {
			Order order = kiteConnect.cancelOrder(orderId, Constants.VARIETY_REGULAR);
			LOGGER.info("{}: Order cancelled for orderId - {}, status - {}",
					userAccount.getUserName(), order.orderId, order.status);

			return order;
		} catch (KiteException e) {
			LOGGER.error("{}: Error in cancelling the order for the order Id - {}, error = {}:{}",
					userAccount.getUserName(), orderId, e.code, e.message, e);
			throw new TradeException(userAccount.getUserName() + " : " + e.code + " : " + e.message, e);
		} catch (JSONException | IOException e) {
			LOGGER.error("{}: Error in cancelling order for orderId - {}",
					userAccount.getUserName(), orderId, e);
			throw new TradeException(userAccount.getUserName() +
					" : Error while cancelling order for orderId - " + orderId, e);
		}
	}

	public List<Order> getOrder(String orderId) throws TradeException {
		return getOrder(orderId, 1);
	}

	public List<Order> getOrder(String orderId, int retryCount) throws TradeException {
		try {
			LOGGER.info("{}: Attempting to get the order history for the order id - {}",
					userAccount.getUserName(), orderId);
			List<Order> orderList = kiteConnect.getOrderHistory(orderId);
			if (orderList == null || orderList.isEmpty()) {
				LOGGER.error("{}: Order history is received as empty, orderId - {}",
						userAccount.getUserName(), orderId);
				throw new TradeException(userAccount.getUserName() +
						" : Empty order list is received, orderId - " + orderId);
			} else {
				orderList.stream().map(OrderWrapper::of)
						.forEach((OrderWrapper orderWrapper) -> LOGGER.info("{} :: {} :: {}",
								userAccount.getUserName(), orderId, orderWrapper));
			}
			return orderList;
		} catch (KiteException e) {
			LOGGER.error("{}: Error in retrieving the order history for the order Id - {}, error = {}:{}",
					userAccount.getUserName(), orderId, e.code, e.message, e);
			if (retryCount > 0) {
				retryCount--;
				return getOrder(orderId, retryCount);
			}
			throw new TradeException(userAccount.getUserName() + " : " + e.code + " : " + e.message, e);
		} catch (JSONException | IOException e) {
			LOGGER.error("{}: Error in retrieving the order history for the orderId - {}",
					userAccount.getUserName(), orderId, e);
			throw new TradeException(userAccount.getUserName() +
					" : Error in retrieving the order history for the orderId - " + orderId, e);
		}
	}

	public List<Long> getAllTokens() {
		try {
			return getInstruments().stream()
					.map(Instrument::getInstrument_token)
					.toList();
		} catch (Exception e) {
			LOGGER.error("Error fetching all tokens", e);
		}

		return Collections.emptyList();
	}

	public Instrument getByToken(Long token) {
		try {
			return getInstruments().stream()
					.filter((Instrument instrument) -> instrument.getInstrument_token() == token)
					.findAny()
					.orElse(null);
		} catch (Exception e) {
			LOGGER.error("Error fetching instrument by token", e);
		}

		return null;
	}

	public long getTokenByTradingSymbol(String tradingSymbol) {
		try {
			return getInstruments().stream()
					.filter((Instrument instrument) -> instrument.getTradingsymbol().equals(tradingSymbol))
					.map(Instrument::getInstrument_token)
					.findAny()
					.orElse(-1L);
		} catch (Exception e) {
			LOGGER.error("Error in fetching the token by options trading symbol, symbol - {}",
					tradingSymbol, e);
		}

		return -1L;
	}

	public synchronized List<Instrument> getInstruments() {
		if (instrumentsList == null) {
			try {
				instrumentsList = kiteConnect.getInstruments();
			} catch (JSONException | IOException | KiteException e) {
				LOGGER.error("Error in retrieving the list of instruments from exchange", e);
			}
		}

		return instrumentsList;
	}

	public class KiteLoginThread extends Thread {

		@Override
		public void run() {
			do {
				try {
					kiteConnect = new KiteConnect(userAccount.getApiKey(), userAccount.isDebug());
					kiteConnect.setUserId(userAccount.getClientId());

					String url = kiteConnect.getLoginURL();
					AUTH_LOGGER.info("{}: Kite login URL - {}", userAccount.getUserName(), url);

					String requestToken = tokenGenerator.getRequestToken(url, userAccount);
					AUTH_LOGGER.info("{}: Requset token - {}", userAccount.getUserName(), requestToken);

					if (requestToken != null && !requestToken.isEmpty()) {
						User user = kiteConnect.generateSession(requestToken, userAccount.getApiSecret());

						kiteConnect.setAccessToken(user.accessToken);
						kiteConnect.setPublicToken(user.publicToken);

						kiteConnect.setSessionExpiryHook(new KiteSessionExpiryHook());
						AUTH_LOGGER.info("{}: Kite login successful", userAccount.getUserName());
						isLoggedIn = true;
					} else {
						AUTH_LOGGER.error("{}: Kite login failed", userAccount.getUserName());
						isLoggedIn = false;
					}
				} catch (KiteException | JSONException | IOException e) {
					AUTH_LOGGER.error("{}: Error during kite login", userAccount.getUserName(), e);
					isLoggedIn = false;
				}

				if (!isLoggedIn) {
					try {
						AUTH_LOGGER.info("{}: Waiting for 5 seconds to reattempt kite login", userAccount
								.getUserName());
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						AUTH_LOGGER.error("{}: Interrupted while waiting for to reattempt login again",
								userAccount.getUserName(), e);
						Thread.currentThread().interrupt();
					}
				}
			} while (!isLoggedIn);
		}

	}

	public class KiteSessionExpiryHook implements SessionExpiryHook {

		@Override
		public void sessionExpired() {
			AUTH_LOGGER.error("{}: Kite session expired. Attempting to login again...", userAccount.getUserName());
			isLoggedIn = false;

			doLogin();
		}

	}

}

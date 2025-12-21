package com.trading.platform.service.trade;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.trading.platform.controller.dto.PositionDto;
import com.trading.platform.controller.dto.SignalDto;
import com.trading.platform.controller.dto.TradeDto;
import com.trading.platform.persistence.entity.Position;
import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.persistence.entity.Trade;
import com.trading.platform.service.KiteLoginModuleImpl;
import com.trading.platform.util.DateSerializer;
import com.trading.platform.util.TradeUtil;

public abstract class TradeManagerTelegramBot extends TelegramLongPollingBot {

	private static final Logger LOGGER = LogManager.getLogger(TradeManagerTelegramBot.class);

	protected static final String MENU_ALL_TRADES = "/alltrades";

	protected static final String MENU_OPEN_TRADES = "/opentrades";

	protected static final String MENU_CLOSED_TRADES = "/closedtrades";

	protected static final String MENU_SQUAREOFF_TRADE = "/squareofftrade";

	protected static final String MENU_SQUUAREOFFALL_TRADES = "/squareoffall";

	protected static final String MENU_ENABLELIVE_TRADES = "/enablelive";

	protected static final String MENU_DISABLELIVE_TRADES = "/disablelive";

	protected static final String TOKEN_PARAM = "token";

	protected static final String TRADE_SIGNAL_PARAM = "trade-signal";

	protected static final String LTP_PARAM = "last-traded-price";

	protected static final String ATR_PARAM = "average-true-range";

	protected static final String VIX_LTP_PARAM = "vix-last-traded-price";

	protected static final String STRIKE_PRICE_PARAM = "strike-price";

	protected static final String LOT_SIZE_PARAM = "lot-size";

	protected static final String ORDER_ID_PARAM = "order-id";

	protected static final String QUANTITY_PARAM = "quantity";

	protected static final String SOLD_QUANTITY_PARAM = "sold-quantity";

	protected static final String ENTRY_PRICE_PARAM = "entry-price";

	protected static final String EXIT_PRICE_PARAM = "exit-price";

	protected static final String OPTION_ENTRY_LTP = "option-entry-ltp";

	protected static final String OPTION_EXIT_LTP = "option-exit-ltp";

	protected static final String UNREALIZED_PROFIT = "unrealized-profit";

	protected static final String UNREALIZED_LOSS = "unrealized-loss";

	protected static final String ACTIVE_PARAM = "active";

	protected static final String CLOSED_PARAM = "closed";

	protected static final String SQUARE_OFF_PARAM = "square-off";

	protected static final String STOP_LOSS_PARAM = "stop-loss";

	protected static final String SL_TRAIL_PARAM = "sl-trail";

	private ObjectMapper objectMapper;

	private KiteLoginModuleImpl kiteModule;

	protected TradeManagerTelegramBot(KiteLoginModuleImpl kiteModule) {
		super(kiteModule.getMasterAccount().getTelegramToken());
		this.kiteModule = kiteModule;
		this.objectMapper = new ObjectMapper();
		this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		SimpleModule module = new SimpleModule();
		module.addSerializer(Date.class, new DateSerializer());
		this.objectMapper.registerModule(module);
	}

	public void publishSignal(String message, Signal signal) {
		String jsonPayload = convertSignalToJson(signal);
		sendMessageToChannel(formatMessage(message, jsonPayload));
	}

	public void publishTradeInfo(String message, TradeInfo tradeInfo) {
		String jsonPayload = convertTradeToJson(TradeUtil.convertToTrade(tradeInfo));
		sendMessageToChannel(formatMessage(message, jsonPayload));
	}

	public void publishPositionInfo(String message, PositionInfo positionInfo) {
		String jsonPayload = convertPositionToJson(TradeUtil.convertToPosition(positionInfo));
		sendMessageToChannel(formatMessage(message, jsonPayload));
	}

	public void publishPositionInfo(String message, List<PositionInfo> positionInfoList) {
		List<Position> positionList = positionInfoList.stream().map(TradeUtil::convertToPosition)
				.collect(Collectors.toList());
		String jsonPayload = convertPositionToJson(positionList);
		sendMessageToChannel(formatMessage(message, jsonPayload));
	}

	public void sendMessageToChannel(String message) {
		sendMessageToChannel(message, kiteModule.getMasterAccount().getTelegramChannelName());
	}

	public void sendMessageToChannel(String message, String chatId) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(message);
		/**
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			LOGGER.error("Error while sending message to channel = {}", chatId, e);
		}
		*/
	}

	@Override
	public String getBotUsername() {
		return kiteModule.getMasterAccount().getTelegramBotName();
	}

	protected String formatMessage(String message, String jsonPayload) {
		StringBuilder builder = new StringBuilder();
		builder.append(message);
		builder.append(" : ").append(StringUtils.LF);
		builder.append(jsonPayload);

		return builder.toString();
	}

	protected String convertSignalToJson(Signal signal) {
		SignalDto signalDto = SignalDto.of(signal);
		try {
			return objectMapper.writerWithDefaultPrettyPrinter()
					.withoutAttribute(TOKEN_PARAM)
					.withoutAttribute(TRADE_SIGNAL_PARAM)
					.withoutAttribute(LTP_PARAM)
					.withoutAttribute(ATR_PARAM)
					.withoutAttribute(VIX_LTP_PARAM)
					.withoutAttribute(STRIKE_PRICE_PARAM)
					.writeValueAsString(signalDto);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error in converting the signal to json string - {}", signalDto, e);
			return signalDto.toString();
		}
	}

	protected String convertTradeToJson(Trade trade) {
		TradeDto tradeDto = TradeDto.of(trade);
		try {
			return objectMapper.writerWithDefaultPrettyPrinter()
					.withoutAttribute(TOKEN_PARAM)
					.withoutAttribute(TRADE_SIGNAL_PARAM)
					.withoutAttribute(LTP_PARAM)
					.withoutAttribute(ATR_PARAM)
					.withoutAttribute(STRIKE_PRICE_PARAM)
					.withoutAttribute(LOT_SIZE_PARAM)
					.withoutAttribute(ORDER_ID_PARAM)
					.writeValueAsString(tradeDto);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error in converting the trade to json string - {}", tradeDto, e);
			return tradeDto.toString();
		}
	}

	protected String convertTradesToJson(List<Trade> trades) {
		List<TradeDto> tradesList = trades.stream().map(TradeDto::of).collect(Collectors.toList());
		try {
			return objectMapper.writerWithDefaultPrettyPrinter()
					.withoutAttribute(TOKEN_PARAM)
					.withoutAttribute(TRADE_SIGNAL_PARAM)
					.withoutAttribute(LTP_PARAM)
					.withoutAttribute(ATR_PARAM)
					.withoutAttribute(STRIKE_PRICE_PARAM)
					.withoutAttribute(LOT_SIZE_PARAM)
					.withoutAttribute(ORDER_ID_PARAM)
					.writeValueAsString(tradesList);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error in converting the list of trades to json string - {}", tradesList, e);
			return trades.toString();
		}
	}

	protected String convertPositionToJson(Position position) {
		PositionDto positionDto = PositionDto.of(position);
		try {
			return objectMapper.writerWithDefaultPrettyPrinter()
					.withoutAttribute(QUANTITY_PARAM)
					.withoutAttribute(SOLD_QUANTITY_PARAM)
					.withoutAttribute(ENTRY_PRICE_PARAM)
					.withoutAttribute(EXIT_PRICE_PARAM)
					.withoutAttribute(OPTION_ENTRY_LTP)
					.withoutAttribute(OPTION_EXIT_LTP)
					.withoutAttribute(UNREALIZED_PROFIT)
					.withoutAttribute(UNREALIZED_LOSS)
					.withoutAttribute(ACTIVE_PARAM)
					.withoutAttribute(CLOSED_PARAM)
					.withoutAttribute(SQUARE_OFF_PARAM)
					.withoutAttribute(STOP_LOSS_PARAM)
					.withoutAttribute(SL_TRAIL_PARAM)
					.writeValueAsString(positionDto);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error in converting the position to json string - {}", positionDto, e);
			return positionDto.toString();
		}
	}

	protected String convertPositionToJson(List<Position> positions) {
		List<PositionDto> positionDtoList = positions.stream().map(PositionDto::of).collect(Collectors.toList());
		try {
			return objectMapper.writerWithDefaultPrettyPrinter()
					.withoutAttribute(QUANTITY_PARAM)
					.withoutAttribute(SOLD_QUANTITY_PARAM)
					.withoutAttribute(ENTRY_PRICE_PARAM)
					.withoutAttribute(EXIT_PRICE_PARAM)
					.withoutAttribute(OPTION_ENTRY_LTP)
					.withoutAttribute(OPTION_EXIT_LTP)
					.withoutAttribute(UNREALIZED_PROFIT)
					.withoutAttribute(UNREALIZED_LOSS)
					.withoutAttribute(ACTIVE_PARAM)
					.withoutAttribute(CLOSED_PARAM)
					.withoutAttribute(SQUARE_OFF_PARAM)
					.withoutAttribute(STOP_LOSS_PARAM)
					.withoutAttribute(SL_TRAIL_PARAM)
					.writeValueAsString(positionDtoList);
		} catch (JsonProcessingException e) {
			LOGGER.error("Error in converting the positions to json string - {}", positionDtoList, e);
			return positionDtoList.toString();
		}
	}

}

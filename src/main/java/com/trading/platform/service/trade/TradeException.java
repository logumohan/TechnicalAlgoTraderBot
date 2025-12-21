package com.trading.platform.service.trade;

public class TradeException extends Exception {

	private static final long serialVersionUID = 1L;

	public TradeException() {
		super();
	}

	public TradeException(String message) {
		super(message);
	}

	public TradeException(String message, Throwable e) {
		super(message, e);
	}

}

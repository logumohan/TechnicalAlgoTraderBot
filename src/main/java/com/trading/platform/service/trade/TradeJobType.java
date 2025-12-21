package com.trading.platform.service.trade;

public enum TradeJobType {

	ATR_TSL("ATR_TSL"),
	FIXED_TSL("FIXED_TSL"),
	FIXED_PROFIT("FIXED_PROFIT"),
	MULTI_TARGET("MULTI_TARGET"),

	UNKNOWN("Unknown");

	private TradeJobType(String type) {
		this.type = type;
	}

	public static TradeJobType getByName(String type) {
		for (TradeJobType job : TradeJobType.values()) {
			if (job.getType().equals(type)) {
				return job;
			}
		}

		return UNKNOWN;
	}

	public String getType() {
		return type;
	}

	private String type;

}

package com.trading.platform.trialing.strategies;

public enum TrailingType {

	DEFAULT("DEFAULT"),
	SIMPLE("SIMPLE"),
	PROTECTIVE("PROTECTIVE"),
	AGGRESSIVE("AGGRESSIVE"),
	CUSTOM_1("CUSTOM1");

	private String type;

	private TrailingType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static TrailingType getType(String trialingType) {
		for (TrailingType type : TrailingType.values()) {
			if (type.getType().equalsIgnoreCase(trialingType)) {
				return type;
			}
		}

		return DEFAULT;
	}

}

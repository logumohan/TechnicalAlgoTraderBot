package com.trading.platform.service.signal;

public enum OptionType {

	BUY_CE("CE"),
	BUY_PE("PE"),
	SELL_CE("CE"),
	SELL_PE("PE");

	private OptionType(String type) {
		this.type = type;
	}

	private String type;

	public String getType() {
		return type;
	}

	public static OptionType getByName(String name) {
		for (OptionType optionType : values()) {
			if (optionType.name().equals(name)) {
				return optionType;
			}
		}

		return null;
	}

}

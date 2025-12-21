package com.trading.platform.trialing.strategies;

public enum MultiTargetTrailingType {

	MULTI_TARGET("MULTI_TARGET");

	private String type;

	private MultiTargetTrailingType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static MultiTargetTrailingType getType(String trialingType) {
		for (MultiTargetTrailingType type : MultiTargetTrailingType.values()) {
			if (type.getType().equalsIgnoreCase(trialingType)) {
				return type;
			}
		}

		return MULTI_TARGET;
	}

}

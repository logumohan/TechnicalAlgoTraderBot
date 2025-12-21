package com.trading.platform.service.trade;

public enum TrailBy {

	ATR("ATR"),
	POINTS("POINTS"),

	UNKNOWN("Unknown");

	private TrailBy(String value) {
		this.value = value;
	}

	public static TrailBy getByValue(String value) {
		for (TrailBy job : TrailBy.values()) {
			if (job.getValue().equals(value)) {
				return job;
			}
		}

		return UNKNOWN;
	}

	public String getValue() {
		return value;
	}

	private String value;

}

package com.trading.platform.controller.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TradingViewIndicators implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("imacd-histogram")
	private double imacdHistogram;

	public double getImacdHistogram() {
		return imacdHistogram;
	}

	public void setImacdHistogram(double imacdHistogram) {
		this.imacdHistogram = imacdHistogram;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TradingViewIndicators [imacdHistogram=");
		builder.append(imacdHistogram);
		builder.append("]");
		return builder.toString();
	}

}

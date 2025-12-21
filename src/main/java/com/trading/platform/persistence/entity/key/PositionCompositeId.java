package com.trading.platform.persistence.entity.key;

import java.io.Serializable;

public class PositionCompositeId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tradeId;

	private int targetId;

	public String getTradeId() {
		return tradeId;
	}

	public int getTargetId() {
		return targetId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

}

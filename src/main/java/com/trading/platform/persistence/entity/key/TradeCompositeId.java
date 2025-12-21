package com.trading.platform.persistence.entity.key;

import java.io.Serializable;
import java.util.Date;

public class TradeCompositeId implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date tickTime;

	private String tradeId;

	private long token;

	public Date getTickTime() {
		return tickTime;
	}

	public String getTradeId() {
		return tradeId;
	}

	public long getToken() {
		return token;
	}

	public void setTickTime(Date tickTime) {
		this.tickTime = tickTime;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public void setToken(long token) {
		this.token = token;
	}

}

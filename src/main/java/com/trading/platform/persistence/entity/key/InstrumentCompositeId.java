package com.trading.platform.persistence.entity.key;

import java.io.Serializable;
import java.util.Date;

public class InstrumentCompositeId implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Date tickTime;

	protected long token;

	public Date getTickTime() {
		return tickTime;
	}

	public long getToken() {
		return token;
	}

	public void setTickTime(Date tickTime) {
		this.tickTime = tickTime;
	}

	public void setToken(long token) {
		this.token = token;
	}

}

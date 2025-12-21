package com.trading.platform.persistence.entity.views;

import java.io.Serializable;
import java.util.Date;

public class InstrumentViewCompositeId implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Date bucketTickTime;

	protected long token;

	public Date getBucketTickTime() {
		return bucketTickTime;
	}

	public long getToken() {
		return token;
	}

	public void setBucketTickTime(Date bucketTickTime) {
		this.bucketTickTime = bucketTickTime;
	}

	public void setToken(long token) {
		this.token = token;
	}

}

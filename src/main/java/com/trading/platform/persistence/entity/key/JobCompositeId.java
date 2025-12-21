package com.trading.platform.persistence.entity.key;

import java.io.Serializable;

public class JobCompositeId implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String userName;

	public String getName() {
		return name;
	}

	public String getUserName() {
		return userName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}

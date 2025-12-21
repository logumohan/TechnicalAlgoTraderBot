package com.trading.platform.config;

public class DatabaseConfiguration {

	private String jdbcUrl;

	private String userName;

	private String password;

	private int connectionTimeout;

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

}

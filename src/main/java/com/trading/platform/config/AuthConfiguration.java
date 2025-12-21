package com.trading.platform.config;

public class AuthConfiguration {

	private String clientId;

	private String password;

	private String apiKey;

	private String apiSecret;

	private String totpSecretKey;
	
	private boolean debugEnabled;

	public String getClientId() {
		return clientId;
	}

	public String getPassword() {
		return password;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getApiSecret() {
		return apiSecret;
	}

	public String getTotpSecretKey() {
		return totpSecretKey;
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
	}

	public void setTotpSecretKey(String totpSecretKey) {
		this.totpSecretKey = totpSecretKey;
	}

	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

}

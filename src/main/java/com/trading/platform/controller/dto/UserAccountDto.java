package com.trading.platform.controller.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.platform.persistence.entity.UserAccount;

public class UserAccountDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("user-name")
	private String userName;

	@JsonProperty("email-id")
	private String emailId;

	@JsonProperty("mobile-number")
	private long mobileNumber;

	@JsonProperty("client-id")
	private String clientId;

	@JsonProperty("api-key")
	private String apiKey;

	@JsonProperty("api-secret")
	private String apiSecret;

	@JsonProperty("totp-secret-key")
	private String totpSecretKey;

	@JsonProperty("password")
	private String password;
	
	@JsonProperty("max-profit-per-day")
	private int maxProfitPerDay;

	@JsonProperty("is-debug")
	private boolean isDebug;

	@JsonProperty("is-master")
	private boolean isMaster;

	@JsonProperty("is-trade-allowed")
	private boolean isTradeAllowed;

	@JsonProperty("telegram-token")
	private String telegramToken;

	@JsonProperty("telegram-channel-name")
	private String telegramChannelName;

	@JsonProperty("telegram-bot-name")
	private String telegramBotName;

	public static UserAccountDto of(UserAccount userAccount) {
		UserAccountDto userAccountDto = new UserAccountDto();
		userAccountDto.setUserName(userAccount.getUserName());
		userAccountDto.setEmailId(userAccount.getEmailId());
		userAccountDto.setMobileNumber(userAccount.getMobileNumber());
		userAccountDto.setClientId(userAccount.getClientId());
		userAccountDto.setApiKey(userAccount.getApiKey());
		userAccountDto.setApiSecret(userAccount.getApiSecret());
		userAccountDto.setTotpSecretKey(userAccount.getTotpSecretKey());
		userAccountDto.setPassword(userAccount.getPassword());
		userAccountDto.setMaxProfitPerDay(userAccount.getMaxProfitPerDay());
		userAccountDto.setDebug(userAccount.isDebug());
		userAccountDto.setMaster(userAccount.isMaster());
		userAccountDto.setTradeAllowed(userAccount.isTradeAllowed());
		userAccountDto.setTelegramToken(userAccount.getTelegramToken());
		userAccountDto.setTelegramChannelName(userAccount.getTelegramChannelName());
		userAccountDto.setTelegramBotName(userAccount.getTelegramBotName());

		return userAccountDto;
	}

	public UserAccount toUserAccount() {
		UserAccount userAccount = new UserAccount();
		userAccount.setUserName(this.getUserName());
		userAccount.setEmailId(this.getEmailId());
		userAccount.setMobileNumber(this.getMobileNumber());
		userAccount.setClientId(this.getClientId());
		userAccount.setApiKey(this.getApiKey());
		userAccount.setApiSecret(this.getApiSecret());
		userAccount.setTotpSecretKey(this.getTotpSecretKey());
		userAccount.setPassword(this.getPassword());
		userAccount.setMaxProfitPerDay(this.getMaxProfitPerDay());
		userAccount.setDebug(this.isDebug());
		userAccount.setMaster(this.isMaster());
		userAccount.setTradeAllowed(this.isTradeAllowed());
		userAccount.setTelegramToken(this.getTelegramToken());
		userAccount.setTelegramChannelName(this.getTelegramChannelName());
		userAccount.setTelegramBotName(this.getTelegramBotName());

		return userAccount;
	}

	public String getUserName() {
		return userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public long getMobileNumber() {
		return mobileNumber;
	}

	public String getClientId() {
		return clientId;
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

	public String getPassword() {
		return password;
	}
	
	public int getMaxProfitPerDay() {
		return maxProfitPerDay;
	}

	public boolean isDebug() {
		return isDebug;
	}

	public boolean isMaster() {
		return isMaster;
	}

	public boolean isTradeAllowed() {
		return isTradeAllowed;
	}

	public String getTelegramToken() {
		return telegramToken;
	}

	public String getTelegramChannelName() {
		return telegramChannelName;
	}

	public String getTelegramBotName() {
		return telegramBotName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public void setMobileNumber(long mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
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

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setMaxProfitPerDay(int maxProfitPerDay) {
		this.maxProfitPerDay = maxProfitPerDay;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public void setMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public void setTradeAllowed(boolean isTradeAllowed) {
		this.isTradeAllowed = isTradeAllowed;
	}

	public void setTelegramToken(String telegramToken) {
		this.telegramToken = telegramToken;
	}

	public void setTelegramChannelName(String telegramChannelName) {
		this.telegramChannelName = telegramChannelName;
	}

	public void setTelegramBotName(String telegramBotName) {
		this.telegramBotName = telegramBotName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserAccountDto [userName=");
		builder.append(userName);
		builder.append(", emailId=");
		builder.append(emailId);
		builder.append(", mobileNumber=");
		builder.append(mobileNumber);
		builder.append(", clientId=");
		builder.append(clientId);
		builder.append(", apiKey=");
		builder.append(apiKey);
		builder.append(", apiSecret=");
		builder.append(apiSecret);
		builder.append(", totpSecretKey=");
		builder.append(totpSecretKey);
		builder.append(", password=");
		builder.append(password);
		builder.append(", maxProfitPerDay=");
		builder.append(maxProfitPerDay);
		builder.append(", isDebug=");
		builder.append(isDebug);
		builder.append(", isMaster=");
		builder.append(isMaster);
		builder.append(", isTradeAllowed=");
		builder.append(isTradeAllowed);
		builder.append(", telegramToken=");
		builder.append(telegramToken);
		builder.append(", telegramChannelName=");
		builder.append(telegramChannelName);
		builder.append(", telegramBotName=");
		builder.append(telegramBotName);
		builder.append("]");
		return builder.toString();
	}

}

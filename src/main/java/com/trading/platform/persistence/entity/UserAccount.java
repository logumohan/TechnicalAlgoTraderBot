package com.trading.platform.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * CREATE TABLE IF NOT EXISTS USER_ACCOUNT (
 * USER_NAME TEXT NOT NULL,
 * EMAIL_ID TEXT NOT NULL,
 * MOBILE_NUMBER BIGINT NOT NULL,
 * CLIENT_ID TEXT NOT NULL,
 * API_KEY TEXT NOT NULL,
 * API_SECRET TEXT NOT NULL,
 * TOTP_SECRET_KEY TEXT NOT NULL,
 * PASSWORD TEXT NOT NULL,
 * MAX_PROFIT_PER_DAY BIGINT NOT NULL DEFAULT 3000,
 * IS_DEBUG BOOLEAN DEFAULT TRUE,
 * IS_MASTER BOOLEAN DEFAULT FALSE,
 * IS_TRADE_ALLOWED BOOLEAN DEFAULT FALSE,
 * TELEGRAM_TOKEN TEXT NOT NULL,
 * TELEGRAM_CHANNEL_NAME TEXT NOT NULL,
 * TELEGRAM_BOT_NAME TEXT NOT NULL,
 * PRIMARY KEY (CLIENT_ID)
 * );
 */
@Entity
@Table(name = "USER_ACCOUNT")
public class UserAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "EMAIL_ID")
	private String emailId;

	@Column(name = "MOBILE_NUMBER")
	private long mobileNumber;

	@Id
	@Column(name = "CLIENT_ID")
	private String clientId;

	@Column(name = "API_KEY")
	private String apiKey;

	@Column(name = "API_SECRET")
	private String apiSecret;

	@Column(name = "TOTP_SECRET_KEY")
	private String totpSecretKey;

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "MAX_PROFIT_PER_DAY")
	private int maxProfitPerDay;

	@Column(name = "IS_DEBUG")
	private boolean isDebug;

	@Column(name = "IS_MASTER")
	private boolean isMaster;

	@Column(name = "IS_TRADE_ALLOWED")
	private boolean isTradeAllowed;

	@Column(name = "TELEGRAM_TOKEN")
	private String telegramToken;

	@Column(name = "TELEGRAM_CHANNEL_NAME")
	private String telegramChannelName;

	@Column(name = "TELEGRAM_BOT_NAME")
	private String telegramBotName;

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
		builder.append("UserAccount [userName=");
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

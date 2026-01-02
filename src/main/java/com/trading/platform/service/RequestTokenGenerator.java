package com.trading.platform.service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.persistence.entity.UserAccount;
import com.warrenstrange.googleauth.GoogleAuthenticator;

public class RequestTokenGenerator {

	private static final Logger AUTH_LOGGER = LogManager.getLogger("AUTH");

	@LogExecutionTime
	public String getRequestToken(String url, UserAccount userAccount) {
		try (HeadlessChromeDriver chrome = new HeadlessChromeDriver(userAccount)) {
			WebDriver driver = chrome.getDriver();
			driver.manage().timeouts().implicitlyWait(Duration.of(5, ChronoUnit.SECONDS));
			driver.manage().window().maximize();
			driver.get(url);

			handleLogin(driver, userAccount.getClientId(), userAccount.getPassword());

			handleTimeBasedOTP(driver, userAccount);

			return parseRequestToken(driver, userAccount);
		} catch (Exception e) {
			AUTH_LOGGER.error("{}: Error in processing the request token", userAccount
					.getUserName(), e);
		}

		return StringUtils.EMPTY;
	}

	private void handleLogin(WebDriver driver, String username, String password) {
		WebElement usernameElement = driver.findElement(By.id(
				SignalGeneratorConstants.USER_ID_ELEMENT));
		WebElement passwordElement = driver.findElement(By.id(
				SignalGeneratorConstants.PASSWORD_ELEMENT));
		usernameElement.sendKeys(username);
		passwordElement.sendKeys(password);

		String label = "Login";
		WebElement submitElement = driver.findElement(By.xpath("//button[contains(.,'" + label
				+ "')]"));
		submitElement.submit();

		waitFor(3);
	}

	private void handleTimeBasedOTP(WebDriver driver, UserAccount userAccount) {
		int totp = generateTOTP(userAccount.getTotpSecretKey());
		String timeBasedOTP = String.format("%06d", totp);
		AUTH_LOGGER.info("{}: Time based OTP - {}", userAccount.getUserName(), timeBasedOTP);

		WebElement inputElement = driver.findElement(By.tagName(
				SignalGeneratorConstants.INPUT_ELEMENT));
		inputElement.click();
		inputElement.sendKeys(timeBasedOTP);

		String label = "Continue";
		try {
			inputElement = driver.findElement(By.xpath("//button[contains(.,'" + label + "')]"));
			inputElement.submit();
		} catch (Exception e) {
			AUTH_LOGGER.error("{}: Error observed while clicking the continue button", userAccount
					.getUserName());
		}

		waitFor(3);
	}

	private String parseRequestToken(WebDriver driver, UserAccount userAccount) {
		String currentUrl = driver.getCurrentUrl();
		AUTH_LOGGER.info("{}: Request Token URL - {}", userAccount.getUserName(), currentUrl);

		String queryString = currentUrl.split("\\?")[1];
		String[] params = queryString.split("&");
		Optional<String> requestToken = Arrays.asList(params).stream()
				.filter((String param) -> param.startsWith(
						SignalGeneratorConstants.REQUEST_TOKEN_QUERY_PARAM))
				.map((String param) -> param.split("=")[1]).findFirst();
		String reqToken = requestToken.isPresent() ? requestToken.get() : StringUtils.EMPTY;
		AUTH_LOGGER.info("{}: Request Token - {}", userAccount.getUserName(), reqToken);

		return reqToken;
	}

	private void waitFor(int seconds) {
		try {
			Thread.sleep(seconds * 1000L);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			AUTH_LOGGER.error("Error while waiting for {} seconds", seconds);
		}
	}

	private int generateTOTP(String secretKey) {
		GoogleAuthenticator auth = new GoogleAuthenticator();
		return auth.getTotpPassword(secretKey);
	}

}

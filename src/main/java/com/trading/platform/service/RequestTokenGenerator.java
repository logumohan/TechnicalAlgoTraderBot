package com.trading.platform.service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.persistence.entity.UserAccount;
import com.warrenstrange.googleauth.GoogleAuthenticator;

public class RequestTokenGenerator {

	private static final Logger AUTH_LOGGER = LogManager.getLogger("AUTH");

	@LogExecutionTime
	public String getRequestToken(String url, UserAccount userAccount) {
		setChromeDriverPath(userAccount);

		WebDriver driver = new ChromeDriver(getChromeOptions(userAccount));
		try {
			driver.manage().timeouts().implicitlyWait(Duration.of(5, ChronoUnit.SECONDS));
			driver.manage().window().maximize();
			driver.get(url);

			handleLogin(driver, userAccount.getClientId(), userAccount.getPassword());

			handleTimeBasedOTP(driver, userAccount);

			return parseRequestToken(driver, userAccount);
		} catch (Exception e) {
			AUTH_LOGGER.error("{}: Error in processing the request token", userAccount.getUserName(), e);
		} finally {
			driver.close();
			driver.quit();
		}

		return StringUtils.EMPTY;
	}

	private void handleLogin(WebDriver driver, String username, String password) {
		WebElement usernameElement = driver.findElement(By.id(SignalGeneratorConstants.USER_ID_ELEMENT));
		WebElement passwordElement = driver.findElement(By.id(SignalGeneratorConstants.PASSWORD_ELEMENT));
		usernameElement.sendKeys(username);
		passwordElement.sendKeys(password);

		String label = "Login";
		WebElement submitElement = driver.findElement(By.xpath("//button[contains(.,'" + label + "')]"));
		submitElement.submit();

		waitFor(3);
	}

	private void handleTimeBasedOTP(WebDriver driver, UserAccount userAccount) {
		int totp = generateTOTP(userAccount.getTotpSecretKey());
		String timeBasedOTP = String.format("%06d", totp);
		AUTH_LOGGER.info("{}: Time based OTP - {}", userAccount.getUserName(), timeBasedOTP);

		WebElement inputElement = driver.findElement(By.tagName(SignalGeneratorConstants.INPUT_ELEMENT));
		inputElement.click();
		inputElement.sendKeys(timeBasedOTP);

		String label = "Continue";
		try {
			inputElement = driver.findElement(By.xpath("//button[contains(.,'" + label + "')]"));
			inputElement.submit();
		} catch (Exception e) {
			AUTH_LOGGER.error("{}: Error observed while clicking the continue button", userAccount.getUserName());
		}

		waitFor(3);
	}

	private String parseRequestToken(WebDriver driver, UserAccount userAccount) {
		String currentUrl = driver.getCurrentUrl();
		AUTH_LOGGER.info("{}: Request Token URL - {}", userAccount.getUserName(), currentUrl);

		String queryString = currentUrl.split("\\?")[1];
		String[] params = queryString.split("&");
		Optional<String> requestToken = Arrays.asList(params).stream()
				.filter((String param) -> param.startsWith(SignalGeneratorConstants.REQUEST_TOKEN_QUERY_PARAM))
				.map((String param) -> param.split("=")[1]).findFirst();
		String reqToken = requestToken.isPresent() ? requestToken.get() : StringUtils.EMPTY;
		AUTH_LOGGER.info("{}: Request Token - {}", userAccount.getUserName(), reqToken);

		return reqToken;
	}

	private void setChromeDriverPath(UserAccount userAccount) {
		Resource resource = new ClassPathResource(SignalGeneratorConstants.WINDOWS_CHROME_DRIVER);
		if (SystemUtils.IS_OS_LINUX) {
			resource = new ClassPathResource(SignalGeneratorConstants.LINUX_CHROME_DRIVER);
		}

		String filePath = null;
		try {
			filePath = resource.getFile().getPath();
			AUTH_LOGGER.info("{}: Chrome Driver location, {}", userAccount.getUserName(), filePath);
		} catch (IOException e) {
			AUTH_LOGGER.error("{}: Error in locating the chrome driver", userAccount.getUserName(), e);
		} finally {
			if (filePath != null) {
				System.setProperty(SignalGeneratorConstants.CHROME_DRIVER_SYSTEM_PROPERTY, filePath);
			}
		}
	}

	private ChromeOptions getChromeOptions(UserAccount userAccount) {
		File file = new File("chrome-profile/" + userAccount.getClientId());
		file.mkdirs();
		
		List<String> optionsList = new ArrayList<>();
		optionsList.add("--headless=new");
		optionsList.add("--incognito");
		optionsList.add("--disable-gpu");
		optionsList.add("--window-size=1920,1200");
		optionsList.add("--ignore-certificate-errors");
		optionsList.add("--disable-extensions");
		optionsList.add("--no-sandbox");
		optionsList.add("--disable-dev-shm-usage");
		optionsList.add("--remote-allow-origins=*");
		optionsList.add("--user-data-dir=" + file.getAbsolutePath());

		ChromeOptions options = new ChromeOptions();
		options.addArguments(optionsList);

		return options;
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

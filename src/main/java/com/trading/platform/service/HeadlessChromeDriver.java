package com.trading.platform.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.trading.platform.SignalGeneratorConstants;
import com.trading.platform.persistence.entity.UserAccount;

public class HeadlessChromeDriver implements AutoCloseable {

	private static final Logger AUTH_LOGGER = LogManager.getLogger("AUTH");

	private final WebDriver driver;

	public HeadlessChromeDriver(UserAccount userAccount) {
		setChromeDriverPath(userAccount);
		this.driver = new ChromeDriver(getChromeOptions(userAccount));
	}

	private ChromeOptions getChromeOptions(UserAccount userAccount) {
		File file = new File("chrome-profile/" + userAccount.getClientId());
		file.mkdirs();

		List<String> optionsList = new ArrayList<>();
		optionsList.add("--headless");
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
			AUTH_LOGGER.error("{}: Error in locating the chrome driver", userAccount.getUserName(),
					e);
		} finally {
			if (filePath != null) {
				System.setProperty(SignalGeneratorConstants.CHROME_DRIVER_SYSTEM_PROPERTY,
						filePath);
			}
		}
	}

	public WebDriver getDriver() {
		return driver;
	}

	@Override
	public void close() {
		driver.quit();
	}
}

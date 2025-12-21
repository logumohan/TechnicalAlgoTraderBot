package com.trading.platform;

public class SignalGeneratorConstants {

	private SignalGeneratorConstants() {
		// Do Nothing
	}

	public static final String WINDOWS_CHROME_DRIVER = "chromedriver.exe";
	
	public static final String LINUX_CHROME_DRIVER = "chromedriver";

	public static final String CHROME_DRIVER_SYSTEM_PROPERTY = "webdriver.chrome.driver";

	public static final String REQUEST_TOKEN_QUERY_PARAM = "request_token";

	public static final String USER_ID_ELEMENT = "userid";

	public static final String PASSWORD_ELEMENT = "password";

	public static final String INPUT_ELEMENT = "input";

	// Subscriptions
	public static final String SUBSCRIPTION_FAILURE = "Subscription Failure";

	public static final String SUBSCRIPTION_SUCCESS = "Subscription is successful";

	public static final String SUBSCRIPTION_INVALID = "Bad subscription request";

	// Bar Series
	public static final int MAX_BAR_COUNT = 60;
	
	// Aggregation Types
	public static final String ONE_MINUTE = "ONE_MINUTE";
	
	public static final String THREE_MINUTES = "THREE_MINUTES";
	
	public static final String FIVE_MINUTES = "FIVE_MINUTES";
	
	public static final String FIFTEEN_MINUTES = "FIFTEEN_MINUTES";
	
	public static final String ONE_HOUR = "ONE_HOUR";
	
	public static final String ONE_DAY = "ONE_DAY";

	// VIX
	public static final long VIX_TOKEN = 264969;
	
	public static final String VIX_NAME = "INDIA VIX";
	
}

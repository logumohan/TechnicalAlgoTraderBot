package com.trading.platform.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

	@Bean(name = "allowLiveTrade")
	public Boolean allowLiveTrade() {
		return Boolean.TRUE;
	}

	@Bean(name = "allowPaperTrade")
	public Boolean allowPaperTrade() {
		return Boolean.TRUE;
	}

	@Bean
	public DatabaseConfiguration getDatabaseConfiguration() {
		DatabaseConfiguration config = new DatabaseConfiguration();
		config.setJdbcUrl("jdbc:postgresql://localhost:5432/instruments");
		config.setUserName("postgres");
		config.setPassword("postgres");
		config.setConnectionTimeout(10);

		return config;
	}

	@Bean
	public Symbols getSymbols() {
		Symbols symbols = new Symbols();
		symbols.setNifty50Symbols(Arrays.asList("ADANIENT", "ADANIPORTS", "APOLLOHOSP", "ASIANPAINT",
				"AXISBANK", "BAJAJ-AUTO", "BAJFINANCE", "BAJAJFINSV", "BPCL", "BHARTIARTL", "BRITANNIA", "CIPLA",
				"COALINDIA", "DIVISLAB", "DRREDDY", "EICHERMOT", "GRASIM", "HCLTECH", "HDFCBANK", "HDFCLIFE",
				"HEROMOTOCO", "HINDALCO", "HINDUNILVR", "HDFC", "ICICIBANK", "ITC", "INDUSINDBK", "INFY", "JSWSTEEL",
				"KOTAKBANK", "LT", "M&M", "MARUTI", "NTPC", "NESTLEIND", "ONGC", "POWERGRID", "RELIANCE", "SBILIFE",
				"SBIN", "SUNPHARMA", "TCS", "TATACONSUM", "TATAMOTORS", "TATASTEEL", "TECHM", "TITAN", "UPL",
				"ULTRACEMCO", "WIPRO"));

		return symbols;
	}

}

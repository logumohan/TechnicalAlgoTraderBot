package com.trading.platform.config;

import java.util.ArrayList;
import java.util.List;

public enum TradingInstrument {

	NIFTY_50("NIFTY 50", 256265, "NIFTY", 4, 50),
	NIFTY_BANK("NIFTY BANK", 260105, "BANKNIFTY", 4, 25),
	NIFTY_FIN_SERVICE("NIFTY FIN SERVICE", 257801, "FINNIFTY", 2, 40),

	ADANIENT("ADANIENT", 6401),
	ADANIPORTS("ADANIPORTS", 3861249),
	APOLLOHOSP("APOLLOHOSP", 40193),
	ASIANPAINT("ASIANPAINT", 60417),
	AXISBANK("AXISBANK", 1510401),
	BAJAJ_AUTO("BAJAJ-AUTO", 4267265),
	BAJFINANCE("BAJFINANCE", 81153),
	BAJAJFINSV("BAJAJFINSV", 4268801),
	BPCL("BPCL", 134657),
	BHARTIARTL("BHARTIARTL", 2714625),
	BRITANNIA("BRITANNIA", 140033),
	CIPLA("CIPLA", 177665),
	COALINDIA("COALINDIA", 5215745),
	DIVISLAB("DIVISLAB", 2800641),
	DRREDDY("DRREDDY", 225537),
	EICHERMOT("EICHERMOT", 232961),
	GRASIM("GRASIM", 315393),
	HCLTECH("HCLTECH", 1850625),
	HDFCBANK("HDFCBANK", 341249),
	HDFCLIFE("HDFCLIFE", 119553),
	HEROMOTOCO("HEROMOTOCO", 345089),
	HINDALCO("HINDALCO", 348929),
	HINDUNILVR("HINDUNILVR", 356865),
	HDFC("HDFC", 340481),
	ICICIBANK("ICICIBANK", 1270529),
	ITC("ITC", 424961),
	INDUSINDBK("INDUSINDBK", 1346049),
	INFY("INFY", 408065),
	JSWSTEEL("JSWSTEEL", 3001089),
	KOTAKBANK("KOTAKBANK", 492033),
	LT("LT", 2939649),
	M_AND_M("M&M", 519937),
	MARUTI("MARUTI", 2815745),
	NTPC("NTPC", 2977281),
	NESTLEIND("NESTLEIND", 4598529),
	ONGC("ONGC", 633601),
	POWERGRID("POWERGRID", 3834113),
	RELIANCE("RELIANCE", 3906817),
	SBILIFE("SBILIFE", 5582849),
	SBIN("SBIN", 779521),
	SUNPHARMA("SUNPHARMA", 857857),
	TCS("TCS", 2953217),
	TATACONSUM("TATACONSUM", 878593),
	TATAMOTORS("TATAMOTORS", 884737),
	TATASTEEL("TATASTEEL", 895745),
	TECHM("TECHM", 3465729),
	TITAN("TITAN", 897537),
	UPL("UPL", 2889473),
	ULTRACEMCO("ULTRACEMCO", 2952193),
	WIPRO("WIPRO", 969473);

	private String instrument;

	private long token;

	private String optionName;

	private int expiryDay;
	
	private int lotSize;

	private TradingInstrument(String instrument, long token) {
		this.instrument = instrument;
		this.token = token;
	}

	private TradingInstrument(String instrument, long token, String optionName, 
			int expiryDay, int lotSize) {
		this.instrument = instrument;
		this.token = token;
		this.optionName = optionName;
		this.expiryDay = expiryDay;
		this.lotSize = lotSize;
	}

	public String getInstrument() {
		return instrument;
	}

	public long getToken() {
		return token;
	}

	public String getOptionName() {
		return optionName;
	}

	public int getExpiryDay() {
		return expiryDay;
	}
	
	public int getLotSize() {
		return lotSize;
	}

	public static TradingInstrument getByToken(long token) {
		for (TradingInstrument instrument : TradingInstrument.values()) {
			if (token == instrument.getToken()) {
				return instrument;
			}
		}

		return null;
	}

	public static List<Long> getAllTokens() {
		ArrayList<Long> tokens = new ArrayList<>();
		for (TradingInstrument instrument : TradingInstrument.values()) {
			tokens.add(instrument.getToken());
		}

		return tokens;
	}

	public static List<String> getAllInstruments() {
		ArrayList<String> instruments = new ArrayList<>();
		for (TradingInstrument instrument : TradingInstrument.values()) {
			instruments.add(instrument.getInstrument());
		}

		return instruments;
	}

}

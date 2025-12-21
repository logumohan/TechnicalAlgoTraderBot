package com.trading.platform.service.trade;

import com.trading.platform.persistence.entity.Instrument;
import com.trading.platform.persistence.entity.InstrumentIndicators;

public interface OptionTickConsumer {

	public Long getToken();

	public Long getOptionToken();

	public double getOptionLastTradedPrice();
	
	public void onTick(Instrument instrument);
	
	public void onOptionTick(Instrument instrument);

	public void onAggregatedTick(InstrumentIndicators indicator);
	
	public void onAggregatedOneMinuteTick(InstrumentIndicators oneMinIndicator);

	public String getAggregationType();

}

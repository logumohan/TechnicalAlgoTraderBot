package com.trading.platform.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "INSTRUMENT_FIVEMIN_INDICATORS")
public class FiveMinutesInstrumentIndicators extends InstrumentIndicators {

	private static final long serialVersionUID = 1L;

}

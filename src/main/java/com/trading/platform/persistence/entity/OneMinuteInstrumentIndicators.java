package com.trading.platform.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "INSTRUMENT_ONEMIN_INDICATORS")
public class OneMinuteInstrumentIndicators extends InstrumentIndicators {

	private static final long serialVersionUID = 1L;

}

package com.trading.platform.persistence.entity.views;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "INSTRUMENT_FIFTEENMIN")
@Immutable
public class FifteenMinuteInstrumentView extends InstrumentView {

	private static final long serialVersionUID = 1L;

}

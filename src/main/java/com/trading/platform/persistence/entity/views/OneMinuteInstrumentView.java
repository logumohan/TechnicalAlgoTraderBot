package com.trading.platform.persistence.entity.views;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "INSTRUMENT_ONEMIN")
@Immutable
public class OneMinuteInstrumentView extends InstrumentView {

	private static final long serialVersionUID = 1L;

}

package com.trading.platform.persistence.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * CREATE TABLE IF NOT EXISTS AGGREGATION_TYPE (
 * NAME TEXT NOT NULL CHECK (NAME = 'ONE_MINUTE' OR NAME = 'THREE_MINUTE' OR
 * 		NAME = 'FIVE_MINUTE' OR NAME = 'FIFTEEN_MINUTE' OR NAME = 'ONE_HOUR' OR 
 * 		NAME = 'ONE_DAY'),
 * DURATION INTEGER,
 * AGGREGABLE BOOLEAN,
 * PRIMARY KEY (NAME)
 * );
 */
@Entity
@Table(name = "AGGREGATION_TYPE")
public class AggregationType implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "NAME")
	private String name;

	@Column(name = "DURATION")
	private int duration;

	@Column(name = "AGGREGABLE")
	private boolean aggregable;

	public String getName() {
		return name;
	}

	public int getDuration() {
		return duration;
	}

	public boolean isAggregable() {
		return aggregable;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setAggregable(boolean aggregable) {
		this.aggregable = aggregable;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AggregationType [name=");
		builder.append(name);
		builder.append(", duration=");
		builder.append(duration);
		builder.append(", aggregable=");
		builder.append(aggregable);
		builder.append("]");
		return builder.toString();
	}

}

package com.trading.platform.backtesting;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SelectQueryBuilder {

	private String table;

	private List<String> columnList;

	private StringBuilder whereClause;

	private String orderBy;

	private String direction;

	public SelectQueryBuilder() {
		whereClause = new StringBuilder();
	}

	public SelectQueryBuilder from(String table) {
		this.table = table;
		return this;
	}

	public SelectQueryBuilder columns(String... columns) {
		columnList = Arrays.asList(columns);
		return this;
	}

	public SelectQueryBuilder euqalTo(String column, String value) {
		whereClause.append(column).append(" = \'").append(value).append("\'");
		return this;
	}

	public SelectQueryBuilder euqalTo(String column, long value) {
		whereClause.append(column).append(" = ").append(value);
		return this;
	}

	public SelectQueryBuilder lessThan(String column, String value) {
		whereClause.append(column).append(" < \'").append(value).append("\'");
		return this;
	}

	public SelectQueryBuilder lessThanOrEqualTo(String column, String value) {
		whereClause.append(column).append(" <= \'").append(value).append("\'");
		return this;
	}

	public SelectQueryBuilder greaterThan(String column, String value) {
		whereClause.append(column).append(" > \'").append(value).append("\'");
		return this;
	}

	public SelectQueryBuilder greaterThanOrEqualTo(String column, String value) {
		whereClause.append(column).append(" >= \'").append(value).append("\'");
		return this;
	}

	public SelectQueryBuilder between(String column, String startValue, String endValue) {
		whereClause.append(column).append(" between \'")
				.append(startValue).append("\' and \'").append(endValue).append("\'");
		return this;
	}

	public SelectQueryBuilder and() {
		whereClause.append(" and ");
		return this;
	}

	public SelectQueryBuilder or() {
		whereClause.append(" or ");
		return this;
	}

	public SelectQueryBuilder orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public SelectQueryBuilder orderBy(String orderBy, String direction) {
		this.orderBy = orderBy;
		this.direction = direction;
		return this;
	}

	public String build() {
		StringBuilder builder = new StringBuilder();
		builder.append("select");
		if (Optional.ofNullable(columnList).isPresent() && !columnList.isEmpty()) {
			builder.append(columnList.stream().collect(Collectors.joining(",", " ", " ")));
		} else {
			builder.append(" * ");
		}
		builder.append("from ");
		builder.append(table);
		if (whereClause.length() != 0) {
			builder.append(" where ");
			builder.append(whereClause.toString());
		}
		if (orderBy != null) {
			builder.append(" ");
			builder.append("order by ").append(orderBy);
			if (direction != null) {
				builder.append(" ");
				builder.append(direction);
			}
		}

		return builder.toString();
	}

}

package com.trading.platform.service.trade;

import com.zerodhatech.models.OrderParams;

public class OrderParamsWrapper {

	private OrderParams orderParams;

	public OrderParamsWrapper(OrderParams orderParams) {
		this.orderParams = orderParams;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OrderParams [exchange=");
		builder.append(orderParams.exchange);
		builder.append(", tradingsymbol=");
		builder.append(orderParams.tradingsymbol);
		builder.append(", transactionType=");
		builder.append(orderParams.transactionType);
		builder.append(", quantity=");
		builder.append(orderParams.quantity);
		builder.append(", price=");
		builder.append(orderParams.price);
		builder.append(", product=");
		builder.append(orderParams.product);
		builder.append(", orderType=");
		builder.append(orderParams.orderType);
		builder.append(", validity=");
		builder.append(orderParams.validity);
		builder.append(", disclosedQuantity=");
		builder.append(orderParams.disclosedQuantity);
		builder.append(", triggerPrice=");
		builder.append(orderParams.triggerPrice);
		builder.append(", squareoff=");
		builder.append(orderParams.squareoff);
		builder.append(", stoploss=");
		builder.append(orderParams.stoploss);
		builder.append(", trailingStoploss=");
		builder.append(orderParams.trailingStoploss);
		builder.append(", tag=");
		builder.append(orderParams.tag);
		builder.append(", parentOrderId=");
		builder.append(orderParams.parentOrderId);
		builder.append(", validityTTL=");
		builder.append(orderParams.validityTTL);
		builder.append(", icebergQuantity=");
		builder.append(orderParams.icebergQuantity);
		builder.append(", icebergLegs=");
		builder.append(orderParams.icebergLegs);
		builder.append("]");

		return builder.toString();
	}

}

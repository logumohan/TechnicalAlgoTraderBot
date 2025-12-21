package com.trading.platform.service.trade;

import com.zerodhatech.models.Order;

public class OrderWrapper {

	private Order order;

	public OrderWrapper(Order order) {
		this.order = order;
	}

	public static OrderWrapper of(Order order) {
		return new OrderWrapper(order);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Order [");
		builder.append("exchangeOrderId=");
		builder.append(order.exchangeOrderId).append(", ");
		builder.append("disclosedQuantity=");
		builder.append(order.disclosedQuantity).append(", ");
		builder.append("validity=");
		builder.append(order.validity).append(", ");
		builder.append("tradingSymbol=");
		builder.append(order.tradingSymbol).append(", ");
		builder.append("orderVariety=");
		builder.append(order.orderVariety).append(", ");
		builder.append("orderType=");
		builder.append(order.orderType).append(", ");
		builder.append("triggerPrice=");
		builder.append(order.triggerPrice).append(", ");
		builder.append("statusMessage=");
		builder.append(order.statusMessage).append(", ");
		builder.append("price=");
		builder.append(order.price).append(", ");
		builder.append("status=");
		builder.append(order.status).append(", ");
		builder.append("product=");
		builder.append(order.product).append(", ");
		builder.append("accountId=");
		builder.append(order.accountId).append(", ");
		builder.append("exchange=");
		builder.append(order.exchange).append(", ");
		builder.append("orderId=");
		builder.append(order.orderId).append(", ");
		builder.append("pendingQuantity=");
		builder.append(order.pendingQuantity).append(", ");
		builder.append("orderTimestamp=");
		builder.append(order.orderTimestamp).append(", ");
		builder.append("exchangeTimestamp=");
		builder.append(order.exchangeTimestamp).append(", ");
		builder.append("exchangeUpdateTimestamp=");
		builder.append(order.exchangeUpdateTimestamp).append(", ");
		builder.append("averagePrice=");
		builder.append(order.averagePrice).append(", ");
		builder.append("transactionType=");
		builder.append(order.transactionType).append(", ");
		builder.append("filledQuantity=");
		builder.append(order.filledQuantity).append(", ");
		builder.append("quantity=");
		builder.append(order.quantity).append(", ");
		builder.append("parentOrderId=");
		builder.append(order.parentOrderId).append(", ");
		builder.append("tag=");
		builder.append(order.tag).append(", ");
		builder.append("guid=");
		builder.append(order.guid).append(", ");
		builder.append("validityTTL=");
		builder.append(order.validityTTL).append(", ");
		builder.append("meta=");
		builder.append(order.meta);
		builder.append("]");

		return builder.toString();
	}

}

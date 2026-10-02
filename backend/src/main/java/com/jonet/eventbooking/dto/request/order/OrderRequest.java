package com.jonet.eventbooking.dto.request.order;

import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.enums.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
	private UUID id;
	private UUID userId;
	private OrderStatus status = OrderStatus.PENDING;
	private List<OrderItemsRequest> orderItems;
}

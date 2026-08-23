package com.jonet.eventbooking.dto.request.order;

import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.enums.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
	private UUID userId;
	private String status = OrderStatus.PENDING.name();
	private List<OrderItemsRequest> orderItems;
}

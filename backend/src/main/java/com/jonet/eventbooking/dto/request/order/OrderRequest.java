package com.jonet.eventbooking.dto.request.order;

import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.enums.OrderStatus;
import com.jonet.eventbooking.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
	private UUID id;
	private UUID userId;
	private OrderStatus status = OrderStatus.PENDING;
	private List<OrderItemsRequest> orderItems;
	@NotNull(message = "Payment method is required")
	private PaymentMethod paymentMethod;
}

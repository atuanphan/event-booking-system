package com.jonet.eventbooking.dto.request.order;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemsRequest {
	private UUID id;

	@Min(value = 1, message = "the quantity must be greater than 0")
	private int quantity;
	private UUID ticketTypeId;
}

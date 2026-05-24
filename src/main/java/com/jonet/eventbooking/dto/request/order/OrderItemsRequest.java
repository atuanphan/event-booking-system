package com.jonet.eventbooking.dto.request.order;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemsRequest {
	private UUID id;
	private int quantity;
	private UUID ticketTypeId;
}

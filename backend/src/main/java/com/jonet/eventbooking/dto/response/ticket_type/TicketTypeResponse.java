package com.jonet.eventbooking.dto.response.ticket_type;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketTypeResponse {
	private UUID id;
	private String name;
	private BigDecimal price;
	private Integer totalQuantity;
	private Integer availableQuantity;
}

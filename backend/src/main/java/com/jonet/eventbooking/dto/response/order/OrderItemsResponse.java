package com.jonet.eventbooking.dto.response.order;

import java.math.BigDecimal;
import java.util.UUID;

import com.jonet.eventbooking.dto.response.event.EventSummaryResponse;
import com.jonet.eventbooking.dto.response.ticket_type.TicketTypeResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
@AllArgsConstructor 
public class OrderItemsResponse {
    private UUID id;
	private Integer quantity;
	private BigDecimal price;
	private BigDecimal subtotal;
	private UUID seatId;
	private TicketTypeResponse ticketType;
	private EventSummaryResponse event;
}

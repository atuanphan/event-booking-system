package com.jonet.eventbooking.dto.request.ticket_type;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.AbstractDTO;
import com.jonet.eventbooking.dto.request.seat.SeatRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketTypeRequest extends AbstractDTO{
	private UUID id;
	private String name;
	private BigDecimal price;
	private Integer totalQuantity;
	private Integer availableQuantity;
	private List<SeatRequest> seats;
}

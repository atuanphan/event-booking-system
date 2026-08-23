package com.jonet.eventbooking.dto.response.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.response.ticket_type.TicketTypeResponse;
import com.jonet.eventbooking.dto.response.venue.VenueResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDetailResponse {
	private UUID id;
	private String name;
	private String description;
	private String status;
	private Integer totalQuantity;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String imageUrl;
	private VenueResponse venue;
	private List<TicketTypeResponse> ticketTypes;
}

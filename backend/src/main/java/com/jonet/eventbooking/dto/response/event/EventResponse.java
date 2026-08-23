package com.jonet.eventbooking.dto.response.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.response.ticket_type.TicketTypeResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventResponse {
	private UUID id;
	private String name;
	private String description;
	private String status;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String imageUrl;
	private UUID venueId;
	private List<TicketTypeResponse> ticketTypes;
}

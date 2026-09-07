package com.jonet.eventbooking.dto.request.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.AbstractDTO;
import com.jonet.eventbooking.dto.request.ticket_type.TicketTypeRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@Builder 
public class EventSearchRequest extends AbstractDTO{
    private UUID id;
	private String name;
	private String description;
	private String status;
	private UUID venueId;
	private String imageUrl;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private List<TicketTypeRequest> ticketTypes;
}

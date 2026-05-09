package com.jonet.eventbooking.dto.request.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.AbstractDTO;
import com.jonet.eventbooking.dto.request.ticket_type.TicketTypeRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRequest extends AbstractDTO{
	private UUID id;
	
	@NotBlank(message = "name is required!")
	private String name;
	
	@NotBlank(message =  "description is required")
	private String description;
	
	@NotBlank(message = "status is required")
	private String status;
	
	@NotNull(message = "venue is required")
	private UUID venueId;
	
	@NotNull(message = "url is required")
	private String imageUrl;
	
	@NotNull(message = "start time not null")
	private LocalDateTime startTime;
	
	@NotNull(message = "end time not null")
	private LocalDateTime endTime;
	
	@NotEmpty(message = "ticketType not empty")
	private List<TicketTypeRequest> ticketTypes;
}

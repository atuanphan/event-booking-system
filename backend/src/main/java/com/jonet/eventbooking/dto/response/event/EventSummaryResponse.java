package com.jonet.eventbooking.dto.response.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.jonet.eventbooking.dto.response.venue.VenueResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventSummaryResponse {
	private UUID id;
	private String name;
	private String status;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String imageUrl;
	private VenueResponse venue;
}

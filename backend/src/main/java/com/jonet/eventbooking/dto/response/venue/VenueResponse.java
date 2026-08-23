package com.jonet.eventbooking.dto.response.venue;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueResponse {
	private UUID id;
	private String name;
	private String address;
	private Integer capacity;
}

package com.jonet.eventbooking.dto.request.venue;

import java.util.UUID;

import com.jonet.eventbooking.dto.AbstractDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@Builder 
public class VenueSearchRequest extends AbstractDTO {
    private UUID id;
	private String name;
	private String address;
	private Integer capacity;
}

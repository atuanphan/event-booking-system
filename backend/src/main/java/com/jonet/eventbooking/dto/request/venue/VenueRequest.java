package com.jonet.eventbooking.dto.request.venue;

import java.util.UUID;

import com.jonet.eventbooking.dto.AbstractDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueRequest extends AbstractDTO{
	private UUID id;
	
	@NotBlank(message = "name is required!")
	private String name;
	
	@NotBlank(message = "address is required!")
	private String address;
	
	@NotNull(message = "capacity is empty!")
	@PositiveOrZero(message = "Capacity must be a positive number!")
	private Integer capacity;
}

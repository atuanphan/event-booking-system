package com.jonet.eventbooking.dto;

import java.util.UUID;

public record TopSellingEventDTO(
    UUID eventId,
	String eventName,
	Long soldQuantity
) { }

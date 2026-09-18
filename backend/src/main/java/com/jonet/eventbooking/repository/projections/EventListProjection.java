package com.jonet.eventbooking.repository.projections;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EventListProjection {
	UUID getId();
	String getName();
	String getStatus();
	String getDescription();
	LocalDateTime getStartTime();
	LocalDateTime getEndTime();
	String getImageUrl();
	UUID getVenueId();
}

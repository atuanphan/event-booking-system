package com.jonet.eventbooking.repository.projections;

import java.util.UUID;

public interface BestSellingEventProjection {
    UUID getEventId();
    String getEventName();
    long getTicketsSold();
    long getTotalQuantity();
}

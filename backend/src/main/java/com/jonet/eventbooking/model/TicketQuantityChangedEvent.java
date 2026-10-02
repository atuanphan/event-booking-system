package com.jonet.eventbooking.model;

import java.util.UUID;

public record TicketQuantityChangedEvent(UUID ticketTypeId, int remainingQuantity) {

}

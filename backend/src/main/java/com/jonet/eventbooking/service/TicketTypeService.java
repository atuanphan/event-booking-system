package com.jonet.eventbooking.service;

import java.util.UUID;

import com.jonet.eventbooking.entity.TicketTypeEntity;

public interface TicketTypeService {
	void updateTicketRedis(TicketTypeEntity ticketTypeEntity);
	int updateAvailableQuantity(UUID ticketTypeId, int quantity);
}

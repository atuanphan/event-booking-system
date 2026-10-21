package com.jonet.eventbooking.service;

import java.util.Map;
import java.util.UUID;

import com.jonet.eventbooking.entity.TicketTypeEntity;

public interface TicketTypeService {
	void updateTicketRedis(TicketTypeEntity ticketTypeEntity);
	Map<UUID, Integer> updateAvailableQuantity(Map<UUID, Integer> data);
	Map<UUID, Integer> releaseStock(Map<UUID, Integer> data);
}

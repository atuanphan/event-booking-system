package com.jonet.eventbooking.service.impl;

import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.entity.TicketTypeEntity;
import com.jonet.eventbooking.service.TicketTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService {
	private final StringRedisTemplate redisTemplate;
	private final String redisKey = "ticket:stock:";

	@Override
	public void updateTicketRedis(TicketTypeEntity ticketTypeEntity) {
		String redisKey = "ticket:stock:" + ticketTypeEntity.getId();
		redisTemplate.opsForValue().set(redisKey, ticketTypeEntity.getTotalQuantity().toString());
	}

	@Override
	public void updateAvailableQuantity(UUID ticketTypeId, int quantity) {
		String rawData = (String) redisTemplate.opsForValue().get(redisKey + ticketTypeId);
		int inventoryQuantity = Integer.parseInt(rawData);
		if (inventoryQuantity > quantity) {
			redisTemplate.opsForValue().decrement(redisKey + ticketTypeId, quantity);
		}
	}

}

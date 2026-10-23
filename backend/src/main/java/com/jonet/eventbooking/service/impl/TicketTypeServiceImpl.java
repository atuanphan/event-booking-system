package com.jonet.eventbooking.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.customexception.InsufficientTicketException;
import com.jonet.eventbooking.customexception.RedisKeyNotFoundException;
import com.jonet.eventbooking.entity.TicketTypeEntity;
import com.jonet.eventbooking.service.TicketTypeService;
import com.jonet.eventbooking.utils.RedisUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j 
public class TicketTypeServiceImpl implements TicketTypeService {
	private final StringRedisTemplate redisTemplate;

	@Override
	public void updateTicketRedis(TicketTypeEntity ticketTypeEntity) {
		String redisKey = RedisUtil.ticketStockKey(ticketTypeEntity.getId());
		redisTemplate.opsForValue().set(redisKey, ticketTypeEntity.getTotalQuantity().toString());
	}

	@Override
	public Map<UUID, Integer> updateAvailableQuantity(Map<UUID, Integer> data) {
		if (data == null || data.isEmpty()) {
			throw new IllegalArgumentException("Reservation data must not be empty");
		}
		data.forEach((id, qty) -> {
			if (id == null || qty == null || qty <= 0) {
				throw new IllegalArgumentException("Invalid reservation: " + id + " -> " + qty);
			}
		});

		data.forEach((id, quantity) -> {
			Boolean exists = redisTemplate.hasKey(RedisUtil.ticketStockKey(id));
			if (!Boolean.TRUE.equals(exists)) {
				throw new RedisKeyNotFoundException("Redis key not found");
			}
		});

		List<UUID> ids = new ArrayList<>(data.keySet());
		Map<UUID, Integer> decremented = new LinkedHashMap<>();
		Map<UUID, Integer> remaining = new HashMap<>();
		UUID insufficientId = null;

		try {
			for (UUID id : ids) {
				int qty = data.get(id);
				Long after = redisTemplate.opsForValue().decrement(RedisUtil.ticketStockKey(id), qty);
				if (after == null) {
					throw new IllegalStateException("Redis returned null when decrementing stock of " + id);
				}
				decremented.put(id, qty);

				if (after < 0) {
					insufficientId = id;
					break;
				}
				remaining.put(id, Math.toIntExact(after));
			}
		} catch (RuntimeException e) {
			releaseStock(decremented);
			throw e;
		}

		if (insufficientId != null) {
			releaseStock(decremented); 
			throw new InsufficientTicketException(
					"Not enough tickets available for ticket type " + insufficientId);
		}
		return remaining;
	}

	@Override
	public Map<UUID, Integer> releaseStock(Map<UUID, Integer> data) {
		Map<UUID, Integer> remaining = new HashMap<>();
		data.forEach((id, quantity) -> {
			try {
				Long value = redisTemplate.opsForValue().increment(RedisUtil.ticketStockKey(id), quantity);
				if (value != null) {
					remaining.put(id, Math.toIntExact(value));
				}
			} catch (RuntimeException e) {
				log.error("Hoàn kho thất bại, cần đối soát thủ công. ticketType={}, qty={}", id, quantity, e);
			}
		});
		return remaining;
	}

}

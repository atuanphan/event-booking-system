package com.jonet.eventbooking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.jonet.eventbooking.repository.TicketTypeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CacheRunner implements CommandLineRunner{
	private final TicketTypeRepository ticketTypeRepository;
	private final StringRedisTemplate redisTemplate;

	@Override
	public void run(String... args) throws Exception {
		ticketTypeRepository.findAll().forEach(item -> {
			redisTemplate.opsForValue().set("ticket:stock:" + item.getId(), item.getAvailableQuantity().toString());
		});
	}

}

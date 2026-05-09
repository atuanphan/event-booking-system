package com.jonet.eventbooking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonet.eventbooking.entity.EventEntity;

public interface EventRepository extends JpaRepository<EventEntity, UUID>{
	public void deleteByIdIn(List<UUID> ids);
}

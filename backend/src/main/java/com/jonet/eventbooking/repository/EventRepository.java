package com.jonet.eventbooking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jonet.eventbooking.entity.EventEntity;

import jakarta.transaction.Transactional;

@Transactional
public interface EventRepository extends JpaRepository<EventEntity, UUID> {
	
	@Modifying
	@Query(value = "UPDATE events SET status = 'CANCELLED' WHERE id IN (:ids)", nativeQuery = true)
	public void softDeleteEvents(@Param("ids") List<UUID> ids);
}

package com.jonet.eventbooking.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jonet.eventbooking.entity.PromotionEntity;

public interface PromotionRepository extends JpaRepository<PromotionEntity, UUID>{
	Page<PromotionEntity> findAllByEventId(UUID eventId, Pageable pageable);
	Optional<PromotionEntity> findByIdAndEventId(UUID id, UUID eventId);
}

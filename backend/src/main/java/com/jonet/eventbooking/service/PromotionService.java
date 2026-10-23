package com.jonet.eventbooking.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jonet.eventbooking.dto.request.promotion.PromotionRequest;
import com.jonet.eventbooking.dto.response.promotion.PromotionResponse;

public interface PromotionService {
	Page<PromotionResponse> getPromotions(UUID eventId, UUID organizerId, Pageable pageable);
	PromotionResponse getPromotion(UUID eventId, UUID id, UUID organizerId);
	void create(UUID eventId, UUID organizerId, PromotionRequest request);
	void update(UUID eventId, UUID id, UUID organizerId, PromotionRequest request);
	void delete(UUID eventId, UUID id, UUID organizerId);
}

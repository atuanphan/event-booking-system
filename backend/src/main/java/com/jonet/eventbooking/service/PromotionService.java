package com.jonet.eventbooking.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jonet.eventbooking.dto.request.promotion.PromotionRequest;
import com.jonet.eventbooking.dto.response.promotion.PromotionResponse;

public interface PromotionService {
	Page<PromotionResponse> getPromotions(Pageable pageable);
	PromotionResponse getPromotion(UUID id);
	void create(PromotionRequest request);
	void update(UUID id, PromotionRequest request);
	void delete(UUID id);
}

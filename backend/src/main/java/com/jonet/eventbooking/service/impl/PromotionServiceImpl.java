package com.jonet.eventbooking.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.converter.PromotionMapper;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.dto.request.promotion.PromotionRequest;
import com.jonet.eventbooking.dto.response.promotion.PromotionResponse;
import com.jonet.eventbooking.entity.PromotionEntity;
import com.jonet.eventbooking.repository.PromotionRepository;
import com.jonet.eventbooking.service.PromotionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PromotionServiceImpl implements PromotionService {
	private final PromotionRepository promotionRepository;
	private final PromotionMapper promotionMapper;

	@Override
	public Page<PromotionResponse> getPromotions(Pageable pageable) {
		return promotionRepository.findAll(pageable).map(promotionMapper::toResponse);
	}

	@Override
	public PromotionResponse getPromotion(UUID id) {
		return promotionMapper.toResponse(findPromotion(id));
	}

	@Override
	public void create(PromotionRequest request) {
		PromotionEntity promotion = promotionMapper.toEntity(request);
		promotion.setUsedCount(0);
		promotionRepository.save(promotion);
	}

	@Override
	public void update(UUID id, PromotionRequest request) {
		PromotionEntity promotion = findPromotion(id);
		promotionMapper.updateEntity(request, promotion);
		promotionRepository.save(promotion);
	}

	@Override
	public void delete(UUID id) {
		promotionRepository.delete(findPromotion(id));
	}

	private PromotionEntity findPromotion(UUID id) {
		return promotionRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Promotion not found"));
	}
}

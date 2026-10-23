package com.jonet.eventbooking.service.impl;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.converter.PromotionMapper;
import com.jonet.eventbooking.customexception.BadRequestException;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.dto.request.promotion.PromotionRequest;
import com.jonet.eventbooking.dto.response.promotion.PromotionResponse;
import com.jonet.eventbooking.entity.EventEntity;
import com.jonet.eventbooking.entity.PromotionEntity;
import com.jonet.eventbooking.repository.EventRepository;
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
	private final EventRepository eventRepository;

	@Override
	public Page<PromotionResponse> getPromotions(UUID eventId, UUID organizerId, Pageable pageable) {
		requireOwnedEvent(eventId, organizerId);
		return promotionRepository.findAllByEventId(eventId, pageable).map(promotionMapper::toResponse);
	}

	@Override
	public PromotionResponse getPromotion(UUID eventId, UUID id, UUID organizerId) {
		requireOwnedEvent(eventId, organizerId);
		return promotionMapper.toResponse(findPromotion(eventId, id));
	}

	@Override
	public void create(UUID eventId, UUID organizerId, PromotionRequest request) {
		EventEntity event = requireOwnedEvent(eventId, organizerId);
		validateDiscount(request);
		PromotionEntity promotion = promotionMapper.toEntity(request);
		promotion.setCode(request.getCode().trim().toUpperCase(Locale.ROOT));
		promotion.setUsedCount(0);
		promotion.setEvent(event);
		promotionRepository.save(promotion);
	}

	@Override
	public void update(UUID eventId, UUID id, UUID organizerId, PromotionRequest request) {
		requireOwnedEvent(eventId, organizerId);
		validateDiscount(request);
		PromotionEntity promotion = findPromotion(eventId, id);
		promotionMapper.updateEntity(request, promotion);
		promotion.setCode(request.getCode().trim().toUpperCase(Locale.ROOT));
		promotionRepository.save(promotion);
	}

	@Override
	public void delete(UUID eventId, UUID id, UUID organizerId) {
		requireOwnedEvent(eventId, organizerId);
		promotionRepository.delete(findPromotion(eventId, id));
	}

	private EventEntity requireOwnedEvent(UUID eventId, UUID organizerId) {
		return eventRepository.findOwnedById(eventId, organizerId)
				.orElseThrow(() -> new EntityNotFoundException("Event not found"));
	}

	private PromotionEntity findPromotion(UUID eventId, UUID id) {
		return promotionRepository.findByIdAndEventId(id, eventId)
				.orElseThrow(() -> new EntityNotFoundException("Promotion not found"));
	}

	private void validateDiscount(PromotionRequest request) {
		if ("PERCENTAGE".equals(request.getDiscountType())
				&& request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
			throw new BadRequestException("Percentage discount cannot exceed 100");
		}
	}
}

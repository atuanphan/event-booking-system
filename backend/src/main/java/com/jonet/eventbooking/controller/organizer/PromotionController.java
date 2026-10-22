package com.jonet.eventbooking.controller.organizer;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.PagedResult;
import com.jonet.eventbooking.dto.request.promotion.PromotionRequest;
import com.jonet.eventbooking.dto.request.promotion.PromotionSearchRequest;
import com.jonet.eventbooking.dto.response.promotion.PromotionResponse;
import com.jonet.eventbooking.service.PromotionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/organizer/events/{eventId}/promotions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ORGANIZER')")
public class PromotionController {
	private final PromotionService promotionService;

	@GetMapping
	public ResponseEntity<PagedResult> getPromotions(@PathVariable UUID eventId, PromotionSearchRequest request) {
		Page<PromotionResponse> response = promotionService.getPromotions(eventId, organizerId(),
				PageRequest.of(request.getPage() - 1, request.getPageSize()));
		return ResponseEntity.ok(PagedResult.of(response.getContent(), response.getTotalPages()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<PromotionResponse> getPromotion(@PathVariable UUID eventId, @PathVariable UUID id) {
		return ResponseEntity.ok(promotionService.getPromotion(eventId, id, organizerId()));
	}

	@PostMapping
	public ResponseEntity<Void> create(@PathVariable UUID eventId, @Valid @RequestBody PromotionRequest request) {
		promotionService.create(eventId, organizerId(), request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> update(@PathVariable UUID eventId, @PathVariable UUID id,
			@Valid @RequestBody PromotionRequest request) {
		promotionService.update(eventId, id, organizerId(), request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID eventId, @PathVariable UUID id) {
		promotionService.delete(eventId, id, organizerId());
		return ResponseEntity.noContent().build();
	}

	private UUID organizerId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return UUID.fromString(authentication.getName());
	}
}

package com.jonet.eventbooking.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.PagedResult;
import com.jonet.eventbooking.dto.request.event.EventSearchRequest;
import com.jonet.eventbooking.dto.response.event.EventResponse;
import com.jonet.eventbooking.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminEventController {
	private final EventService eventService;

	@GetMapping("/events")
	public ResponseEntity<PagedResult> getEvents(EventSearchRequest eventRequest) {
		Page<EventResponse> response = eventService.getEvents(eventRequest, PageRequest.of(eventRequest.getPage() - 1, eventRequest.getPageSize()));
		return ResponseEntity.ok(PagedResult.of(response.getContent(), response.getTotalPages()));
	}
	
}

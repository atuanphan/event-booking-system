package com.jonet.eventbooking.controller.client;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.response.event.EventDetailResponse;
import com.jonet.eventbooking.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/events")
@RequiredArgsConstructor
public class EventController {
	private final EventService eventService;
	
	@GetMapping
	public ResponseEntity<?> getEvents(@RequestParam(name = "name", required = false) String name) {
		return ResponseEntity.ok(eventService.getEvents(name));
	}

	@GetMapping("/{id}")
	public ResponseEntity<EventDetailResponse> eventDetailById(@PathVariable UUID id) {
		return ResponseEntity.ok(eventService.getEventDetailById(id));
	}
}

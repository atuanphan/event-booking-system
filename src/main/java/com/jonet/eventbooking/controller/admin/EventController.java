package com.jonet.eventbooking.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.PagedResult;
import com.jonet.eventbooking.dto.request.event.EventRequest;
import com.jonet.eventbooking.service.admin.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class EventController {
	private final EventService eventService;

	@GetMapping("/events")
	public ResponseEntity<PagedResult> getEvents(@RequestBody EventRequest eventRequest) {
		return ResponseEntity.ok(PagedResult.of(eventService.getEvents(eventRequest,
				PageRequest.of(eventRequest.getPage() - 1, eventRequest.getPageSize())), eventService.totalPage()));
	}
	
	@PostMapping("/event")
	public ResponseEntity<String> create(@Valid @RequestBody EventRequest eventRequest) {
		eventService.create(eventRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping("/event")
	public ResponseEntity<String> update(@Valid @RequestBody EventRequest eventRequest) {
		eventService.update(eventRequest);
		return ResponseEntity.ok("update successfully!");
	}
	
	@DeleteMapping("/event/{ids}")
	public ResponseEntity<String> delete(@PathVariable List<UUID> ids) {
		eventService.delete(ids);
		return ResponseEntity.ok("delete successfully!");
	}
}

package com.jonet.eventbooking.controller.organizer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jonet.eventbooking.dto.request.event.EventRequest;
import com.jonet.eventbooking.dto.response.event.EventResponse;
import com.jonet.eventbooking.service.EventService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.jonet.eventbooking.dto.PagedResult;
import com.jonet.eventbooking.dto.response.venue.VenueResponse;
import com.jonet.eventbooking.service.VenueService;


@RestController 
@RequestMapping("/v1/organizer/events")
@RequiredArgsConstructor 
public class OrganizerEventController {
    private final EventService eventService;
	private final VenueService venueService;
    
    @GetMapping
    public ResponseEntity<List<EventResponse>> getEvents(@RequestParam  String name) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = UUID.fromString(authentication.getName());
		return ResponseEntity.ok(eventService.getEventsByUserId(userId, name));
    }

    @GetMapping("/venues")
    public ResponseEntity<PagedResult> getVenues() {
		Page<VenueResponse> response = venueService.getVenues(PageRequest.of(0, 100));
		return ResponseEntity.ok(PagedResult.of(response.getContent(), response.getTotalPages()));
	}

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> create(@RequestPart @Valid EventRequest eventRequest,
			@RequestPart(value = "file", required = false) MultipartFile file) {
		eventService.create(eventRequest, file);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> update(@RequestPart @Valid EventRequest eventRequest,
			@RequestPart(value = "file", required = false) MultipartFile file) {
		eventService.update(eventRequest, file);
		return ResponseEntity.ok("update successfully!");
	}
	
	@DeleteMapping("/{ids}")
	public ResponseEntity<String> delete(@PathVariable List<UUID> ids) {
		eventService.delete(ids);
		return ResponseEntity.ok("delete successfully!");
	}
}

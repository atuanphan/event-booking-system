package com.jonet.eventbooking.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
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
import com.jonet.eventbooking.dto.request.venue.VenueRequest;
import com.jonet.eventbooking.dto.response.venue.VenueResponse;
import com.jonet.eventbooking.service.VenueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class VenueController {
	private final VenueService venueService;

	@GetMapping("/venues")
	public ResponseEntity<PagedResult> getVenues(@RequestBody VenueRequest venueRequest) {
		Page<VenueResponse> response = venueService.getVenues(PageRequest.of(venueRequest.getPage() - 1, venueRequest.getPageSize()));
		return ResponseEntity.ok(PagedResult.of(response.getContent(), response.getTotalPages()));
	}

	@PostMapping("/venues")
	public ResponseEntity<String> create(@Valid @RequestBody VenueRequest venueRequest) {
		venueService.create(venueRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping("/venues")
	public ResponseEntity<String> update(@Valid @RequestBody VenueRequest venueRequest) {
		venueService.update(venueRequest);
		return ResponseEntity.ok("update successfully!");
	}
	
	@DeleteMapping("/venues/{ids}")
	public ResponseEntity<String> delete(@PathVariable List<UUID> ids) {
		venueService.delete(ids);
		return ResponseEntity.ok("delete successfully!");
	}
}

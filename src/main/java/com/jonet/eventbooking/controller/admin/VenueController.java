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
import com.jonet.eventbooking.dto.request.venue.VenueRequest;
import com.jonet.eventbooking.service.admin.VenueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class VenueController {
	private final VenueService venueService;

	@GetMapping("/venues")
	public ResponseEntity<PagedResult> getVenues(@RequestBody VenueRequest venueRequest) {
		return ResponseEntity.ok(PagedResult.of(
				venueService.getVenues(PageRequest.of(venueRequest.getPage() - 1, venueRequest.getPageSize())),
				venueService.totalPage()));
	}

	@PostMapping("/venue")
	public ResponseEntity<String> create(@Valid @RequestBody VenueRequest venueRequest) {
		venueService.create(venueRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping("/venue")
	public ResponseEntity<String> update(@Valid @RequestBody VenueRequest venueRequest) {
		venueService.update(venueRequest);
		return ResponseEntity.ok("update successfully!");
	}
	
	@DeleteMapping("/venue/{ids}")
	public ResponseEntity<String> delete(@PathVariable List<UUID> ids) {
		venueService.delete(ids);
		return ResponseEntity.ok("delete successfully!");
	}
}

package com.jonet.eventbooking.service.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.jonet.eventbooking.dto.request.venue.VenueRequest;
import com.jonet.eventbooking.dto.response.venue.VenueResponse;

public interface VenueService {
	public List<VenueResponse> getVenues(Pageable pageable);
	public void create(VenueRequest venueRequest);
	public void update(VenueRequest venueRequest);
	public void delete(List<UUID> ids);
	public int totalPage();
}

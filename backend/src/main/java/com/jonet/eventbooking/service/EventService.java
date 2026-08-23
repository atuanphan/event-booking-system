package com.jonet.eventbooking.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jonet.eventbooking.dto.request.event.EventRequest;
import com.jonet.eventbooking.dto.response.event.EventDetailResponse;
import com.jonet.eventbooking.dto.response.event.EventResponse;

public interface EventService {
	public Page<EventResponse> getEvents(EventRequest eventRequest, Pageable pageable);
	public void create(EventRequest eventRequest);
	public void update(EventRequest eventRequest);
	public void delete(List<UUID> ids);
	public EventDetailResponse getEventDetailById(UUID id);
}

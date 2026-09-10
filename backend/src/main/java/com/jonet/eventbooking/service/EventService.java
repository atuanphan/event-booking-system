package com.jonet.eventbooking.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.jonet.eventbooking.dto.request.event.EventRequest;
import com.jonet.eventbooking.dto.request.event.EventSearchRequest;
import com.jonet.eventbooking.dto.response.event.EventDetailResponse;
import com.jonet.eventbooking.dto.response.event.EventResponse;

public interface EventService {
	public Page<EventResponse> getEvents(EventSearchRequest eventRequest, Pageable pageable);
	public void create(EventRequest eventRequest, MultipartFile file);
	public void update(EventRequest eventRequest);
	public void delete(List<UUID> ids);
	public EventDetailResponse getEventDetailById(UUID id);
	public List<EventResponse> getEvents(String name);
}

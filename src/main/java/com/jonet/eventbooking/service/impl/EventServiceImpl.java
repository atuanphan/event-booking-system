package com.jonet.eventbooking.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.converter.EventMapper;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.dto.request.event.EventRequest;
import com.jonet.eventbooking.dto.response.event.EventDetailResponse;
import com.jonet.eventbooking.dto.response.event.EventResponse;
import com.jonet.eventbooking.entity.EventEntity;
import com.jonet.eventbooking.repository.EventRepository;
import com.jonet.eventbooking.service.EventService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
	private final EventRepository eventRepository;
	private final EventMapper eventMapper;

	@Override
	public Page<EventResponse> getEvents(EventRequest eventRequest, Pageable pageable) {
		Page<EventEntity> events = eventRepository.findAll(pageable);
		return events.map(eventMapper::toEventResponse);
	}

	@Override
	public void create(EventRequest eventRequest) {
		EventEntity event = eventMapper.toEventEntity(eventRequest);
		event.getTicketTypes().forEach(it -> it.setEvent(event));
		eventRepository.save(event);
	}

	@Override
	public void update(EventRequest eventRequest) {
		EventEntity event = eventMapper.toEventEntity(eventRequest);
		event.getTicketTypes().forEach(it -> it.setEvent(event));
		eventRepository.save(event);
	}

	@Override
	public void delete(List<UUID> ids) {
		eventRepository.softDeleteEvents(ids);
	}

	// phần bussiness phía client
	
	@Override
	public EventDetailResponse getEventDetailById(UUID id) {
		EventEntity eventEntity = eventRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Event not found"));
		return eventMapper.toEventDetailResponse(eventEntity);
	}

}

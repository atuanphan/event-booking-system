package com.jonet.eventbooking.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.converter.EventMapper;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.dto.request.event.EventRequest;
import com.jonet.eventbooking.dto.request.event.EventSearchRequest;
import com.jonet.eventbooking.dto.response.event.EventDetailResponse;
import com.jonet.eventbooking.dto.response.event.EventResponse;
import com.jonet.eventbooking.entity.EventEntity;
import com.jonet.eventbooking.repository.EventRepository;
import com.jonet.eventbooking.service.EventService;
import com.jonet.eventbooking.service.TicketTypeService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
	private final EventRepository eventRepository;
	private final EventMapper eventMapper;
	private final TicketTypeService ticketTypeService;

	@Override
	public Page<EventResponse> getEvents(EventSearchRequest eventRequest, Pageable pageable) {
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
		event.getTicketTypes().forEach(it -> {
			it.setEvent(event);
			ticketTypeService.updateTicketRedis(it);
		});
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

	@Override
	public List<EventResponse> getEvents(String name) {
		List<EventEntity> events = eventRepository.findAll();
		return events.stream().map(eventMapper::toEventResponse).toList();
	}
}

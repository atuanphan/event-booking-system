package com.jonet.eventbooking.service.admin.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.converter.EventMapper;
import com.jonet.eventbooking.dto.request.event.EventRequest;
import com.jonet.eventbooking.dto.response.event.EventResponse;
import com.jonet.eventbooking.entity.EventEntity;
import com.jonet.eventbooking.repository.EventRepository;
import com.jonet.eventbooking.service.admin.EventService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
	private final EventRepository eventRepository;
	private final EventMapper eventMapper;

	@Override
	public List<EventResponse> getEvents(EventRequest eventRequest, Pageable pageable) {
		Page<EventEntity> events = eventRepository.findAll(pageable);
		return events.stream().map(eventMapper::toEventResponse).toList();
	}

	@Override
	public void create(EventRequest eventRequest) {
		eventRepository.save(eventMapper.toEventEntity(eventRequest));
	}

	@Override
	public void update(EventRequest eventRequest) {
		eventRepository.save(eventMapper.toEventEntity(eventRequest));
	}

	@Override
	public void delete(List<UUID> ids) {
		eventRepository.deleteByIdIn(ids);
	}

	@Override
	public int totalPage() {
		double total = (double) eventRepository.count() / 10;
		return (int) Math.ceil(total);
	}

}

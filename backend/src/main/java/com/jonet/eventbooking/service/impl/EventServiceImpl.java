package com.jonet.eventbooking.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jonet.eventbooking.converter.EventMapper;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.dto.TopSellingEventDTO;
import com.jonet.eventbooking.dto.UploadResult;
import com.jonet.eventbooking.dto.request.event.EventRequest;
import com.jonet.eventbooking.dto.request.event.EventSearchRequest;
import com.jonet.eventbooking.dto.response.event.EventDetailResponse;
import com.jonet.eventbooking.dto.response.event.EventResponse;
import com.jonet.eventbooking.entity.EventEntity;
import com.jonet.eventbooking.repository.EventRepository;
import com.jonet.eventbooking.service.EventService;
import com.jonet.eventbooking.service.ImageService;
import com.jonet.eventbooking.service.TicketTypeService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j 
public class EventServiceImpl implements EventService {
	private final EventRepository eventRepository;
	private final EventMapper eventMapper;
	private final TicketTypeService ticketTypeService;
	private final ImageService imageService;

	@Override
	public Page<EventResponse> getEvents(EventSearchRequest eventRequest, Pageable pageable) {
		Page<EventEntity> events = eventRepository.findAll(pageable);
		return events.map(eventMapper::toEventResponse);
	}

	@Override
	public List<EventResponse> getEvents(String name) {
		List<EventEntity> events = name == null || name.isBlank()
				? eventRepository.findAll()
				: eventRepository.findByNameContainingIgnoreCase(name);
		return events.stream().map(eventMapper::toEventResponse).toList();
	}

	@Override
	public void create(EventRequest eventRequest, MultipartFile file) {
		EventEntity event = eventMapper.toEventEntity(eventRequest);
		event.getTicketTypes().forEach(ticketType -> {
			ticketType.setEvent(event);
			ticketType.setAvailableQuantity(ticketType.getTotalQuantity());
		});
		UploadResult uploadResult = imageService.uploadImage(file, "events/seatmaps");
		event.setImageUrl(uploadResult.getUrl());
		event.setPublicId(uploadResult.getPublicId());
		eventRepository.save(event);
	}

	@Override
	public void update(EventRequest eventRequest, MultipartFile file) {
		EventEntity event = eventRepository.findById(eventRequest.getId())
				.orElseThrow(() -> new EntityNotFoundException("Event not found"));

		eventMapper.updateEventEntity(eventRequest, event);
		event.getTicketTypes().forEach(it -> {
			it.setEvent(event);
			ticketTypeService.updateTicketRedis(it);
		});

		if (file != null && !file.isEmpty()) {
			String oldPublicId = event.getPublicId();
			UploadResult uploadResult = imageService.uploadImage(file, "events/seatmaps");
			event.setImageUrl(uploadResult.getUrl());
			event.setPublicId(uploadResult.getPublicId());
			eventRepository.save(event);

			if (oldPublicId != null && !oldPublicId.isBlank()) {
				try {
					imageService.deleteImage(oldPublicId);
				} catch (Exception e) {
					log.warn("Failed to delete old image: {}", oldPublicId, e);
				}
			}
		} else {
			eventRepository.save(event);
		}

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
	public List<EventResponse> getTopEvents() {
		List<TopSellingEventDTO> soldEvents = eventRepository.findTopSellingEvents(PageRequest.of(0, 8));
		if (soldEvents.isEmpty()) {
			return List.of();
		}

		List<UUID> eventIds = soldEvents.stream().map(TopSellingEventDTO::eventId).toList();
		Map<UUID, Integer> eventOrder = eventIds.stream()
				.collect(Collectors.toMap(Function.identity(), eventIds::indexOf));

		return eventRepository.findAllById(eventIds).stream()
				.sorted((first, second) -> Integer.compare(
						eventOrder.get(first.getId()),
						eventOrder.get(second.getId())))
				.map(eventMapper::toEventResponse)
				.toList();
	}
}

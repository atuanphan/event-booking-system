package com.jonet.eventbooking.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jonet.eventbooking.dto.request.event.EventRequest;
import com.jonet.eventbooking.dto.response.event.EventDetailResponse;
import com.jonet.eventbooking.dto.response.event.EventResponse;
import com.jonet.eventbooking.entity.EventEntity;

@Mapper(componentModel = "spring", uses = {TicketTypeMapper.class})
public interface EventMapper {
	
	@Mapping(source = "venue.id", target = "venueId")
	EventResponse toEventResponse(EventEntity eventEntity);
	
	@Mapping(source = "venueId", target = "venue.id")
	EventEntity toEventEntity(EventRequest eventRequest);
	
	EventDetailResponse toEventDetailResponse(EventEntity eventEntity);
	
}

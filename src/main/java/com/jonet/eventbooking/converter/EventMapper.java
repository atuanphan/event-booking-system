package com.jonet.eventbooking.converter;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jonet.eventbooking.dto.request.event.EventRequest;
import com.jonet.eventbooking.dto.response.event.EventResponse;
import com.jonet.eventbooking.entity.EventEntity;
import com.jonet.eventbooking.entity.TicketTypeEntity;

@Mapper(componentModel = "spring", uses = {TicketTypeMapper.class})
public interface EventMapper {
	@Mapping(source = "venue.id", target = "venueId")
	@Mapping(source = "ticketTypes", target = "ticketTypeIds")
	EventResponse toEventResponse(EventEntity eventEntity);
	
	default UUID mapTicketTypeToId(TicketTypeEntity ticketTypeEntity) {
		if(ticketTypeEntity == null) return null;
		return ticketTypeEntity.getId();
	}
	
	@Mapping(source = "venueId", target = "venue.id")
	EventEntity toEventEntity(EventRequest eventRequest);
	
}

package com.jonet.eventbooking.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jonet.eventbooking.dto.request.ticket_type.TicketTypeRequest;
import com.jonet.eventbooking.dto.response.ticket_type.TicketTypeResponse;
import com.jonet.eventbooking.entity.TicketTypeEntity;

@Mapper(componentModel = "spring")
public interface TicketTypeMapper {
	public TicketTypeResponse toTicketTypeResponse(TicketTypeEntity ticketTypeEntity);
	
	@Mapping(ignore = true, target = "event")
	public TicketTypeEntity toTicketTypeEntity(TicketTypeRequest ticketTypeRequest);
}

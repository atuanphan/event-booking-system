package com.jonet.eventbooking.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jonet.eventbooking.dto.request.ticket_type.TicketTypeRequest;
import com.jonet.eventbooking.dto.response.ticket_type.TicketTypeResponse;
import com.jonet.eventbooking.entity.TicketTypeEntity;

@Mapper(componentModel = "spring")
public interface TicketTypeMapper {
	@Mapping(source = "event.id", target = "eventId")
	public TicketTypeResponse toTicketTypeResponse(TicketTypeEntity ticketTypeEntity);
	
//	default TicketTypeEntity mapIdToTicketType(UUID id) {
//		if(id == null) return null;
//		TicketTypeEntity ticketType = new TicketTypeEntity();
//        ticketType.setId(id);
//        return ticketType;
//	}
	
	public TicketTypeEntity toTicketTypeEntity(TicketTypeRequest ticketTypeRequest);
}

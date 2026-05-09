package com.jonet.eventbooking.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jonet.eventbooking.dto.request.seat.SeatRequest;
import com.jonet.eventbooking.entity.SeatEntity;

@Mapper(componentModel = "spring")
public interface SeatMapper {
	
	@Mapping(ignore = true, target = "ticketType")
	public SeatEntity toSeatEntity(SeatRequest seatRequest);
}

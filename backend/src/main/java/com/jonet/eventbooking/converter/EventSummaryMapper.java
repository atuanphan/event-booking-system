package com.jonet.eventbooking.converter;

import org.mapstruct.Mapper;

import com.jonet.eventbooking.dto.response.event.EventSummaryResponse;
import com.jonet.eventbooking.entity.EventEntity;

@Mapper(componentModel = "spring")
public interface EventSummaryMapper {
	EventSummaryResponse toResponse(EventEntity eventEntity);
}

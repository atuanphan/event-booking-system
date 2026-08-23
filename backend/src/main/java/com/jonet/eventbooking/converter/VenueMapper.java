package com.jonet.eventbooking.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jonet.eventbooking.dto.request.venue.VenueRequest;
import com.jonet.eventbooking.dto.response.venue.VenueResponse;
import com.jonet.eventbooking.entity.VenueEntity;

@Mapper(componentModel = "spring")
//@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VenueMapper {
	VenueResponse toVenueResponse(VenueEntity venueEntity);
	
	@Mapping(ignore = true, target = "eventEntities")
	VenueEntity toVenueEntity(VenueRequest venueRequest);
}

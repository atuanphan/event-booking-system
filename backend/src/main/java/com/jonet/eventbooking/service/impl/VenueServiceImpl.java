package com.jonet.eventbooking.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.converter.VenueMapper;
import com.jonet.eventbooking.dto.request.venue.VenueRequest;
import com.jonet.eventbooking.dto.response.venue.VenueResponse;
import com.jonet.eventbooking.entity.VenueEntity;
import com.jonet.eventbooking.repository.VenueRepository;
import com.jonet.eventbooking.service.VenueService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VenueServiceImpl implements VenueService{
	private final VenueRepository venueRepository;
	private final VenueMapper venueMapper;
	
	@Override
	public Page<VenueResponse> getVenues(Pageable pageable) {
		Page<VenueEntity> venues = venueRepository.findAll(pageable);
		return venues.map(venueMapper::toVenueResponse);
	}

	@Override
	public void create(VenueRequest venueRequest) {
		venueRepository.save(venueMapper.toVenueEntity(venueRequest));
	}

	@Override
	public void update(VenueRequest venueRequest) {
		venueRepository.save(venueMapper.toVenueEntity(venueRequest));
	}

	@Override
	public void delete(List<UUID> ids) {
		venueRepository.deleteByIdIn(ids);
	}

}

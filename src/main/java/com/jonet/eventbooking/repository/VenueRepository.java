package com.jonet.eventbooking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonet.eventbooking.entity.VenueEntity;

public interface VenueRepository extends JpaRepository<VenueEntity, UUID>{
	public void deleteByIdIn(List<UUID> ids);
}

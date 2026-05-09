package com.jonet.eventbooking.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonet.eventbooking.entity.SeatEntity;

public interface SeatRepository extends JpaRepository<SeatEntity, UUID>{

}

package com.jonet.eventbooking.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonet.eventbooking.entity.RoleEntity;
import com.jonet.eventbooking.enums.RoleCode;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID>{
	Optional<RoleEntity> findByCode(RoleCode code);
}

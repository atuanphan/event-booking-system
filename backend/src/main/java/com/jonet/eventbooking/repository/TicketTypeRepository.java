package com.jonet.eventbooking.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jonet.eventbooking.entity.TicketTypeEntity;

import jakarta.transaction.Transactional;

@Transactional
public interface TicketTypeRepository extends JpaRepository<TicketTypeEntity, UUID> {

	@Modifying
	@Query(value = """
			UPDATE ticket_types SET available_quantity = available_quantity - :available_quantity WHERE id = :id 
			AND available_quantity >= :available_quantity
			""", nativeQuery = true)
	public void updateAvailableQuantity(@Param("available_quantity") int availableQuantity, @Param("id") UUID id);
}

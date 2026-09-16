package com.jonet.eventbooking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jonet.eventbooking.dto.TopSellingEventDTO;
import com.jonet.eventbooking.entity.EventEntity;

import jakarta.transaction.Transactional;

@Transactional
public interface EventRepository extends JpaRepository<EventEntity, UUID> {
	@Modifying
	@Query(value = "UPDATE events SET status = 'CANCELLED' WHERE id IN (:ids)", nativeQuery = true)
	public void softDeleteEvents(@Param("ids") List<UUID> ids);

	@Query("""
		SELECT new com.jonet.eventbooking.dto.EventSoldDTO(
			    e.id,
			    e.name,
			    SUM(oi.quantity))
		FROM EventEntity e
		JOIN e.ticketTypes tt
		JOIN tt.orderItems oi
		JOIN oi.order o
		WHERE o.status = 'COMPLETED' AND e.status = 'UPCOMING'
		GROUP BY e.id, e.name
		ORDER BY SUM(oi.quantity) DESC
	""")
	List<TopSellingEventDTO> findTopSellingEvents(Pageable pageable);

	List<EventEntity> findByNameContainingIgnoreCase(String name);
}

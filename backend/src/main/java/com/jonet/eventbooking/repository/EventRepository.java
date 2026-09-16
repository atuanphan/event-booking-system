package com.jonet.eventbooking.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jonet.eventbooking.dto.TopSellingEventDTO;
import com.jonet.eventbooking.entity.EventEntity;

import jakarta.transaction.Transactional;

@Transactional
public interface EventRepository extends JpaRepository<EventEntity, UUID>, JpaSpecificationExecutor<EventEntity> {
	@Modifying
	@Query(value = "UPDATE events SET status = 'CANCELLED' WHERE id IN (:ids)", nativeQuery = true)
	public void softDeleteEvents(@Param("ids") List<UUID> ids);

	@Query("""
				SELECT new com.jonet.eventbooking.dto.TopSellingEventDTO(
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

	Page<EventEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

	@Query(value = """
			SELECT e.*
			FROM events e
			WHERE EXISTS (
			    SELECT 1
			    FROM ticket_types tt
			    WHERE tt.event_id = e.id
			      AND tt.price > :price
			)
			""", countQuery = """
			SELECT COUNT(*)
			FROM events e
			WHERE EXISTS (
			    SELECT 1
			    FROM ticket_types tt
			    WHERE tt.event_id = e.id
			      AND tt.price > :price
			)
			""", nativeQuery = true)
	Page<EventEntity> findByPriceGreaterThan(@Param("price") BigDecimal price, Pageable pageable);

	Page<EventEntity> findByStartTimeAfter(LocalDateTime startTime, Pageable pageable);

}

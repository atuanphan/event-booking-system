package com.jonet.eventbooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jonet.eventbooking.entity.OrderItemsEntity;
import com.jonet.eventbooking.enums.OrderStatus;
import com.jonet.eventbooking.repository.projections.BestSellingEventProjection;
import com.jonet.eventbooking.repository.projections.MonthlyTicketProjection;

public interface OrderItemRepository extends JpaRepository<OrderItemsEntity, UUID>{

	List<OrderItemsEntity> findByOrderId(UUID orderId);

	@Query("""
        SELECT COUNT(DISTINCT oi.ticketType.event.id)
        FROM OrderItemsEntity oi
        WHERE oi.ticketType.event.user.id = :organizerId
          AND oi.order.status = :status
        """)
	long countEventsWithTicketSales(@Param("organizerId") UUID organizerId,
	                               @Param("status") OrderStatus status);

	@Query("""
        SELECT MONTH(oi.order.createdAt) AS month,
               COALESCE(SUM(oi.quantity), 0) AS sold
        FROM OrderItemsEntity oi
        WHERE oi.ticketType.event.user.id = :organizerId
          AND oi.order.status = :status
          AND oi.order.createdAt >= :from
          AND oi.order.createdAt <  :to
        GROUP BY MONTH(oi.order.createdAt)
        ORDER BY MONTH(oi.order.createdAt)
        """)
    List<MonthlyTicketProjection> ticketsSoldByMonth(@Param("organizerId") UUID organizerId,
                                                 @Param("status") OrderStatus status,
                                                 @Param("from") LocalDateTime from,
                                                 @Param("to") LocalDateTime to);

  @Query("""
    SELECT e.id   AS eventId,
           e.name AS eventName,
           COALESCE(SUM(oi.quantity), 0) AS ticketsSold,
           tt.totalQuantity AS totalQuantity
    FROM OrderItemsEntity oi
    JOIN oi.ticketType tt
    JOIN tt.event e
    WHERE e.user.id = :organizerId
      AND oi.order.status = :status
    GROUP BY e.id, e.name, tt.totalQuantity
    ORDER BY SUM(oi.quantity) DESC, e.name, tt.totalQuantity ASC
    """)
  List<BestSellingEventProjection> findTopSellingEvents(@Param("organizerId") UUID organizerId,
                                              @Param("status") OrderStatus status,
                                              Pageable pageable);
}

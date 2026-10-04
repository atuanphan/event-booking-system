package com.jonet.eventbooking.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jonet.eventbooking.entity.OrderItemsEntity;
import com.jonet.eventbooking.enums.OrderStatus;
import com.jonet.eventbooking.repository.projections.MonthlyTicketProjection;

public interface OrderItemRepository extends JpaRepository<OrderItemsEntity, UUID>{

	List<OrderItemsEntity> findByOrderId(UUID orderId);

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
}

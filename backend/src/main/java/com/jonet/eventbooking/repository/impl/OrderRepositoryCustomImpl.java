package com.jonet.eventbooking.repository.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.jonet.eventbooking.dto.request.order.OrderSearchRequest;
import com.jonet.eventbooking.entity.OrderEntity;
import com.jonet.eventbooking.repository.OrderRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    @Transactional 
    public Page<OrderEntity> findByOrganizer(UUID organizerId,
            OrderSearchRequest req,
            Pageable pageable) {

        Map<String, Object> params = new HashMap<>();
        params.put("organizerId", organizerId);

        // Điều kiện nằm TRONG EXISTS (organizer + event)
        StringBuilder where = new StringBuilder("""
                WHERE EXISTS (
                    SELECT 1 FROM OrderItemsEntity oi
                    WHERE oi.order = o
                      AND oi.ticketType.event.user.id = :organizerId
                """);
        if (req.getEventId() != null) {
            where.append(" AND oi.ticketType.event.id = :eventId\n");
            params.put("eventId", req.getEventId());
        }
        where.append(")\n"); // đóng EXISTS

        // Điều kiện trên order
        if (req.getStatus() != null) {
            where.append(" AND o.status = :status\n");
            params.put("status", req.getStatus());
        }
        if (req.getStartDate() != null) {
            where.append(" AND o.createdAt >= :startDate\n");
            params.put("startDate", req.getStartDate().atStartOfDay());
        }
        if (req.getEndDate() != null) {
            where.append(" AND o.createdAt < :endDate\n");
            params.put("endDate", req.getEndDate().plusDays(1).atStartOfDay());
        }

        // 1) Query dữ liệu (có phân trang)
        TypedQuery<OrderEntity> dataQuery = entityManager.createQuery(
                "SELECT o FROM OrderEntity o " + where + " ORDER BY o.createdAt DESC",
                OrderEntity.class);
        params.forEach(dataQuery::setParameter);
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());
        List<OrderEntity> content = dataQuery.getResultList();

        // 2) Query đếm tổng
        TypedQuery<Long> countQuery = entityManager.createQuery(
                "SELECT COUNT(o) FROM OrderEntity o " + where, Long.class);
        params.forEach(countQuery::setParameter);
        long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

}

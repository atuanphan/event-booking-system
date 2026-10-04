package com.jonet.eventbooking.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jonet.eventbooking.dto.request.order.OrderSearchRequest;
import com.jonet.eventbooking.entity.OrderEntity;

public interface OrderRepositoryCustom {
    Page<OrderEntity> findByOrganizer(UUID organizerId, OrderSearchRequest orderSearchRequest, Pageable pageable);
}

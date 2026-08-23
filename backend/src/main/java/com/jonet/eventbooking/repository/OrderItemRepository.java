package com.jonet.eventbooking.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jonet.eventbooking.entity.OrderItemsEntity;

public interface OrderItemRepository extends JpaRepository<OrderItemsEntity, UUID>{

	List<OrderItemsEntity> findByOrderId(UUID orderId);
}

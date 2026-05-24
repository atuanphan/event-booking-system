package com.jonet.eventbooking.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jonet.eventbooking.entity.OrderEntity;
import com.jonet.eventbooking.projections.OrderMinInfo;

import jakarta.transaction.Transactional;

@Transactional
public interface OrderRepository extends JpaRepository<OrderEntity, UUID>{
	@Query(value = """
			SELECT o.id AS id, o.totalAmount AS totalAmount FROM OrderEntity o WHERE o.user.Id = :user_id AND o.status = 'PENDING'
			""")
	OrderMinInfo getOrderByUser(@Param("user_id") UUID userId);
	
	@Modifying
	@Query(value = "UPDATE orders SET status = :status WHERE id = :id", nativeQuery = true)
	void updateOrderStatusById(@Param("id") UUID id, @Param("status") String stauts);
	
	@Query(value = """
			SELECT o.id AS id, o.totalAmount AS totalAmount FROM OrderEntity o WHERE o.id = :id AND o.status = 'PENDING'
			""")
	OrderMinInfo getOrderById(@Param("id") UUID id);

	void deleteByStatus(String status);
}

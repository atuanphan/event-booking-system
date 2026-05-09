package com.jonet.eventbooking.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@Column(name = "total_amount", precision = 19, scale = 2, nullable = false)
	private BigDecimal totalAmount;
	
	@Column(name = "status", nullable = false)
	private String status;
	
	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;
	
	@OneToMany(mappedBy = "order")
	private List<OrderItemEntity> orderItems;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	
	@ManyToOne
	@JoinColumn(name = "promotion_id")
	private PromotionEntity promotion;
}

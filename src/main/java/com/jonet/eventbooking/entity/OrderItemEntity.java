package com.jonet.eventbooking.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItemEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	private UUID seatId;
	
	@ManyToOne
	@JoinColumn(name = "order_id")
	private OrderEntity order;
}

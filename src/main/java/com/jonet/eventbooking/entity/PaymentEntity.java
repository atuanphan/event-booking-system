package com.jonet.eventbooking.entity;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class PaymentEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	private UUID orderId;
	
	@Column(name = "transaction_id", length = 100, nullable = false)
	private String transactionId;
	
	@Column(name = "payment_method", length = 50, nullable = false)
	private String paymentMethod;
	
	@Column(name = "amount", precision = 19, scale = 2, nullable = false)
	private BigDecimal amount;
	
	@Column(name = "payment_status", nullable = false)
	private String paymentStatus;
}

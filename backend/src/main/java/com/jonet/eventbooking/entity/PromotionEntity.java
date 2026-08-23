package com.jonet.eventbooking.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "promotions")
@Getter
@Setter
public class PromotionEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@Column(name = "code", length = 20, unique = true)
	private String code;
	
	@Column(name = "discount_type")
	private String discountType;
	
	@Column(name = "discount_value", precision = 19, scale = 2)
	private BigDecimal discountValue;
	
	@Column(name = "max_usage")
	private Integer maxUsage;
	
	@Column(name = "used_count")
	private Integer used_count;
	
	@OneToMany(mappedBy = "promotion")
	private List<OrderEntity> orderEntities;
}

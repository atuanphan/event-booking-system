package com.jonet.eventbooking.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
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
@Table(name = "ticket_types")
@Getter
@Setter
public class TicketTypeEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@Column(name = "name", length = 50, nullable = false)
	private String name;
	
	@Column(name = "price", precision = 19, scale = 2, nullable = false)
	private BigDecimal price;
	
	@Column(name = "total_quantity", nullable = false)
	private Integer totalQuantity;
	
	@ManyToOne
	@JoinColumn(name = "event_id")
	private EventEntity event;
	
	@OneToMany(mappedBy = "ticketType", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<SeatEntity> seats;
}

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
@Table(name = "seats")
@Getter
@Setter
public class SeatEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@Column(name = "seat_row", length = 10, nullable = false)
	private String seatRow;
	
	@Column(name = "seat_number", length = 10, nullable = false)
	private String seatNumber;
	
	@Column(name = "status", length = 50)
	private String status;
	
	@Column(name = "version", nullable = false)
	private Integer version;
	
	@ManyToOne
	@JoinColumn(name = "tickettype_id")
	private TicketTypeEntity ticketType;
}

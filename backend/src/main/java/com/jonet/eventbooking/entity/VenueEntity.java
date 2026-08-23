package com.jonet.eventbooking.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "venues")
@Getter
@Setter
public class VenueEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Lob
	@Column(name = "address", nullable = false)
	private String address;
	
	@Column(name = "capacity")
	private Integer capacity;
	
	@OneToMany(mappedBy = "venue")
	private List<EventEntity> eventEntities;
}

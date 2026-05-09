package com.jonet.eventbooking.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class RoleEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "code", nullable = false, unique = false)
	private String code;
	
	@ManyToMany(mappedBy = "roles")
	private List<UserEntity> users;
}

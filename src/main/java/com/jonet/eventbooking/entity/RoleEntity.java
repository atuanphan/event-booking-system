package com.jonet.eventbooking.entity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class RoleEntity implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "name", length = 50)
	private String name;
	
	@Column(name = "code", length = 20, nullable = false, unique = false)
	private String code;
	
	@ManyToMany(mappedBy = "roles")
	@JsonIgnore
	private List<UserEntity> users;
}

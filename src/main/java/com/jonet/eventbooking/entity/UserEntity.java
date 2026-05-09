package com.jonet.eventbooking.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;
	
	@Column(name = "email", unique = false)
	private String email;
	
	@Column(name = "fullname")
	private String fullname;
	
	@Column(name = "password")
	private String password;
	
	@ManyToMany
	@JoinTable(name = "user_role",
	           joinColumns =  @JoinColumn(name = "user_id", nullable = false),
	           inverseJoinColumns =  @JoinColumn(name =  "role_id", nullable = false))
	private List<RoleEntity> roles;
	
	@OneToMany(mappedBy = "user")
	private List<OrderEntity> orderEntities;
}

package com.jonet.eventbooking.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.jonet.eventbooking.enums.AuthProvider;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(name = "user_email", columnNames = { "email" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
	public UserEntity(UUID id) {
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "fullname", length = 150)
	private String fullname;

	@Column(name = "password")
	private String password;

	@Column(name = "status")
	private int status;
	
	@Enumerated(EnumType.STRING)
	private AuthProvider provider;
	
	private String providerId;

	@ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false))
	private List<RoleEntity> roles;

	@OneToMany(mappedBy = "user")
	private List<OrderEntity> orderEntities;
}

package com.jonet.eventbooking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jonet.eventbooking.entity.UserEntity;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<UserEntity, UUID>, UserRepositoryCustom{
	Optional<UserEntity> findByEmail(String email);
	boolean existsByEmail(String email);
	void deleteByIdIn(List<UUID> ids);
	
	@Modifying
	@Query(value = "UPDATE users SET status = 0, email = CONCAT(email, '_deleted') WHERE id = :id", nativeQuery = true)
	void deleteAccount(@Param("id") UUID id);

	@Query("""
			    SELECT u
			    FROM UserEntity u
			    LEFT JOIN FETCH u.roles
			    WHERE u.email = :email
			""")
	Optional<UserEntity> findByEmailWithRoles(String email);
}

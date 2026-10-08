package com.jonet.eventbooking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.RoleCode;

import jakarta.transaction.Transactional;
import com.jonet.eventbooking.entity.RoleEntity;
import org.springframework.data.repository.query.Param;


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

	List<UserEntity> findByRoles(List<RoleEntity> roles);

	@Query("""
			SELECT DISTINCT u
            FROM UserEntity u
            JOIN u.roles r
            WHERE r.code IN :roles
               AND (:email IS NULL OR :email = '' OR u.email = :email)
			""")
	Page<UserEntity> findStaffByRolesAndEmail(@Param("roles") List<RoleCode> roles, @Param("email") String email, Pageable pageable);
}

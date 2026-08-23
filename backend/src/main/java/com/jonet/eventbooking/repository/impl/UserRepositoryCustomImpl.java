package com.jonet.eventbooking.repository.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.jonet.eventbooking.converter.UUIDConvert;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.repository.UserRepositoryCustom;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {
	private final JdbcTemplate jdbcTemplate;
	private final UUIDConvert uuidConvert;

	public UUID roleCustomerId() {
//		return roleRepository.findByCode(RoleCode.ROLE_CUSTOMER.name()).getId();
		return null;
	}
	
	@Override
	public void create(List<UserEntity> users) {
		for(UserEntity userEntity : users) {
			if(userEntity.getId() == null) {
				userEntity.setId(UUID.randomUUID());
			}
		}
		StringBuilder sqlInsertUser = new StringBuilder("INSERT INTO users (id, fullname, email, password, status) VALUES(?, ?, ?, ?, ?)");
		jdbcTemplate.batchUpdate(sqlInsertUser.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				UserEntity userEntity = users.get(i);
				ps.setObject(1, uuidConvert.uuidToBytes(userEntity.getId()));
				ps.setString(2, userEntity.getFullname());
				ps.setString(3, userEntity.getEmail());
				ps.setString(4, userEntity.getPassword());
				ps.setInt(5, userEntity.getStatus());
			}

			@Override
			public int getBatchSize() {
				return users.size();
			}
		});
		
		StringBuilder sqlInsertUserRole = new StringBuilder("INSERT INTO user_role (user_id, role_id) VALUES(?, ?)");
		byte[] roleId = uuidConvert.uuidToBytes(roleCustomerId());
		jdbcTemplate.batchUpdate(sqlInsertUserRole.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				UserEntity userEntity = users.get(i);
				ps.setObject(1, uuidConvert.uuidToBytes(userEntity.getId()));
				ps.setObject(2, roleId);
			}
			
			@Override
			public int getBatchSize() {
				return users.size();
			}
		});
	}

}

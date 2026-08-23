package com.jonet.eventbooking.repository;

import java.util.List;

import com.jonet.eventbooking.entity.UserEntity;

public interface UserRepositoryCustom {
	public void create(List<UserEntity> users);
}

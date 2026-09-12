package com.jonet.eventbooking.service;

import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.request.user.UserRequest;
import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.AuthProvider;

public interface UserService {
	public List<UserResponse> getUsers();
	public void changePassword(UserRequest userRequest);
	public void delete(UUID id, String email);
	public UserEntity findOrCreateByEmail(String email,  String fullname, AuthProvider provider, String providerId);
}

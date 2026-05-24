package com.jonet.eventbooking.service;

import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.request.user.UserRequest;
import com.jonet.eventbooking.dto.response.user.UserResponse;

public interface UserService {
	public List<UserResponse> getUsers();
	public void create(UserRequest userRequest);
	public void changePassword(UserRequest userRequest);
	public void delete(UUID id, String email);
}

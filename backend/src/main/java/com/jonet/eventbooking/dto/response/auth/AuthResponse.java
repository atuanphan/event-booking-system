package com.jonet.eventbooking.dto.response.auth;

import com.jonet.eventbooking.dto.response.user.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
	private String accessToken;
	private Long expiresIn;
	private UserResponse user;
}

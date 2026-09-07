package com.jonet.eventbooking.service;

import org.springframework.security.core.Authentication;

import com.jonet.eventbooking.dto.AuthResult;
import com.jonet.eventbooking.dto.RefreshResult;
import com.jonet.eventbooking.dto.request.AuthRequest;
import com.jonet.eventbooking.dto.response.user.UserResponse;

public interface AuthService {
	public AuthResult login(AuthRequest request);
	public RefreshResult refreshToken(String refreshToken);
	public void logout(String refreshToken);
	public UserResponse getCurrentUser(Authentication authentication);
}

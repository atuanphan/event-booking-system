package com.jonet.eventbooking.service;

import com.jonet.eventbooking.dto.AuthResult;
import com.jonet.eventbooking.dto.RefreshResult;
import com.jonet.eventbooking.dto.request.AuthRequest;

public interface AuthService {
	public AuthResult login(AuthRequest request);
	public RefreshResult refreshToken(String refreshToken);
	public void logout(String refreshToken);
}

package com.jonet.eventbooking.dto;

import org.springframework.http.ResponseCookie;

import com.jonet.eventbooking.dto.response.auth.AuthResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResult {
	private final AuthResponse body;
	private final ResponseCookie refreshTokenCookie;
}

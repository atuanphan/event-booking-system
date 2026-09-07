package com.jonet.eventbooking.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.AuthResult;
import com.jonet.eventbooking.dto.RefreshResult;
import com.jonet.eventbooking.dto.request.AuthRequest;
import com.jonet.eventbooking.dto.response.auth.AuthResponse;
import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
		AuthResult result = authService.login(request);
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, result.getRefreshTokenCookie().toString())
				.body(result.getBody());
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(@CookieValue("refresh_token") String refreshToken) {
		RefreshResult result = authService.refreshToken(refreshToken);
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, result.getRefreshToken().toString())
				.body(result.getAccessToken());
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
		authService.logout(refreshToken);
		ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
				.httpOnly(true).secure(true).sameSite("Lax").path("/").maxAge(15).build();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).build();
	}

	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser(Authentication authentication) {
		UserResponse response = authService.getCurrentUser(authentication);
		return ResponseEntity.ok(response);
	}
}

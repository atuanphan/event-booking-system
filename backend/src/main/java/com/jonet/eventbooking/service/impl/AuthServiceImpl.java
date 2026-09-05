package com.jonet.eventbooking.service.impl;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.auth.JwtService;
import com.jonet.eventbooking.customexception.InvalidRefreshTokenException;
import com.jonet.eventbooking.dto.AuthResult;
import com.jonet.eventbooking.dto.RefreshResult;
import com.jonet.eventbooking.dto.RefreshTokenPayload;
import com.jonet.eventbooking.dto.request.AuthRequest;
import com.jonet.eventbooking.dto.response.auth.AuthResponse;
import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.model.MyUserDetails;
import com.jonet.eventbooking.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final RedisTemplate<Object, Object> redisTemplate;
	private final String REFRESH_KEY = "refresh-token";

	@Override
	public AuthResult login(AuthRequest authRequest) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
		MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();

		String accessToken = jwtService.generateAccessToken(myUserDetails);
		UserResponse userResponse = UserResponse.builder()
		        .id(myUserDetails.getId())
				.email(myUserDetails.getEmail())
				.fullname(myUserDetails.getFullname())
				.roles(myUserDetails.getRoles())
				.provider(myUserDetails.getProvider())
				.build();

		return new AuthResult(new AuthResponse(accessToken, 900L, userResponse),
				jwtService.generateRefreshToken(myUserDetails));
	}

	@Override
	public RefreshResult refreshToken(String refreshToken) {
		RefreshTokenPayload payload = (RefreshTokenPayload) redisTemplate.opsForValue().get(REFRESH_KEY.concat(":" + refreshToken));
		if(payload == null) {
			throw new InvalidRefreshTokenException("Refresh token invalid, expired, or already used");
		}
		redisTemplate.delete(REFRESH_KEY);

		MyUserDetails user = MyUserDetails.builder()
				.id(payload.id())
				.email(payload.email())
				.roles(payload.roles())
				.build();

		String newAccessToken = jwtService.generateAccessToken(user);
		ResponseCookie newRefreshCookie = jwtService.generateRefreshToken(user);

		return RefreshResult.builder()
				.accessToken(newAccessToken)
				.refreshToken(newRefreshCookie)
				.build();
	}

	@Override
	public void logout(String refreshToken) {
		if (refreshToken != null) {
            redisTemplate.delete(REFRESH_KEY.concat(":" + refreshToken));
        }
	}

}

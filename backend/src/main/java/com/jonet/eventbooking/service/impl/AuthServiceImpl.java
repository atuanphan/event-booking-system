package com.jonet.eventbooking.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.jonet.eventbooking.auth.JwtService;
import com.jonet.eventbooking.components.MailExecutor;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.customexception.InvalidRefreshTokenException;
import com.jonet.eventbooking.customexception.ResourceAlreadyExistsException;
import com.jonet.eventbooking.dto.AuthResult;
import com.jonet.eventbooking.dto.RefreshResult;
import com.jonet.eventbooking.dto.RefreshTokenPayload;
import com.jonet.eventbooking.dto.request.AuthRequest;
import com.jonet.eventbooking.dto.request.user.UserRequest;
import com.jonet.eventbooking.dto.response.auth.AuthResponse;
import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.entity.RoleEntity;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.AuthProvider;
import com.jonet.eventbooking.enums.RoleCode;
import com.jonet.eventbooking.model.MyUserDetails;
import com.jonet.eventbooking.repository.UserRepository;
import com.jonet.eventbooking.service.AuthService;
import com.jonet.eventbooking.service.EmailService;
import com.jonet.eventbooking.service.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final RedisTemplate<Object, Object> redisTemplate;
	private final String REFRESH_KEY = "refresh-token";
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleService roleService;
	private final MailExecutor mailExecutor;
	private final EmailService emailService;
	
	private static final String REDIS_SET_KEY = "emails:registed_set";

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

	@Override
	public UserResponse getCurrentUser(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		UUID userId = (UUID) authentication.getPrincipal();
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		UserResponse response = UserResponse.builder()
				.id(user.getId())
				.fullname(user.getFullname())
				.email(user.getEmail())
				.roles(user.getRoles().stream()
						.map(RoleEntity::getCode)
						.toList())
				.provider(user.getProvider().name())
				.build();
		return response;
	}

	@Override
	public String exchangeOAuthCode(Map<String, String> body) {
		String code = body.get("code");
		Object userIdObject = redisTemplate.opsForValue().get("oauth-code:" + code);
		if (userIdObject == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired code");
		}
		redisTemplate.delete("oauth-code:" + code);

		UUID userId = UUID.fromString(userIdObject.toString());
		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
		MyUserDetails userDetails = MyUserDetails.build(user);
		String accessToken = jwtService.generateAccessToken(userDetails);

		return accessToken;
	}

	@Override
	public void registerAccount(UserRequest userRequest) {
		boolean isEmail = redisTemplate.opsForSet().isMember(REDIS_SET_KEY, userRequest.getEmail());
		if(Boolean.TRUE.equals(isEmail)) {
			throw new ResourceAlreadyExistsException("Email đã được đăng kí.Vui lòng đăng nhập");
		}

		UserEntity userEntity = UserEntity.builder()
                .email(userRequest.getEmail())
                .fullname(userRequest.getFullname())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .roles(List.of(roleService.getRoleByCode(RoleCode.CUSTOMER.toString())))
                .status(userRequest.getStatus())
                .provider(AuthProvider.LOCAL)
                .build();
                
		userRepository.save(userEntity);
		redisTemplate.opsForSet().add(REDIS_SET_KEY, userEntity.getEmail());
		mailExecutor.submitTask(() -> {
			emailService.sendEmailRegisterSuccess(userRequest.getEmail());
		});
	}

}

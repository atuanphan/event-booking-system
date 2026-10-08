package com.jonet.eventbooking.service.impl;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.jonet.eventbooking.auth.JwtService;
import com.jonet.eventbooking.components.MailExecutor;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.customexception.InvalidRefreshTokenException;
import com.jonet.eventbooking.customexception.ResourceAlreadyExistsException;
import com.jonet.eventbooking.dto.AuthResult;
import com.jonet.eventbooking.dto.RefreshResult;
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
import com.jonet.eventbooking.utils.RedisUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final StringRedisTemplate stringRedisTemplate;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleService roleService;
	private final MailExecutor mailExecutor;
	private final EmailService emailService;
	private final RedisTemplate<Object, Object> redisTemplate;
	
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
				jwtService.generateRefreshToken(myUserDetails.getId()));
	}

	@Override
	@Transactional(readOnly = true)
	public RefreshResult refreshToken(String refreshToken) {
		String refreshTokenKey = RedisUtil.getRefreshTokenKey(JwtService.sha256Hex(refreshToken));
		String value = stringRedisTemplate.opsForValue().getAndDelete(refreshTokenKey);
		if (value == null) {
			throw new InvalidRefreshTokenException("Refresh token invalid, expired, or already used");
		}
		UUID userId = UUID.fromString(value);
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new InvalidRefreshTokenException("User not found"));
		if(user.getStatus() == 0) {
			throw new InvalidRefreshTokenException("Account disabled");
		}

		MyUserDetails userDetails = MyUserDetails.builder()
				.id(user.getId())
				.email(user.getEmail())
				.roles(user.getRoles().stream().map(RoleEntity::getCode).toList())
				.fullname(user.getFullname())
				.provider(user.getProviderId())
				.build();

		String newAccessToken = jwtService.generateAccessToken(userDetails);
		ResponseCookie newRefreshCookie = jwtService.generateRefreshToken(userDetails.getId());

		return RefreshResult.builder()
				.accessToken(newAccessToken)
				.refreshToken(newRefreshCookie)
				.build();
	}

	@Override
	public void logout(HttpServletRequest request, String refreshToken) {
		String header = request.getHeader("Authorization");
		String accessToken = header.startsWith("Bearer ") ? header.substring(7) : null;
		try {
			Claims claims = jwtService.validateAndParse(accessToken);
			long ttlMs = claims.getExpiration().getTime() - System.currentTimeMillis();
			if (ttlMs > 0) {
				redisTemplate.opsForValue().set("denylist:" + claims.getId(), 1, Duration.ofMillis(ttlMs));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (refreshToken != null) {
			redisTemplate.delete(RedisUtil.getRefreshTokenKey(JwtService.sha256Hex(refreshToken)));
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
                .roles(List.of(roleService.getRoleByCode(RoleCode.CUSTOMER)))
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

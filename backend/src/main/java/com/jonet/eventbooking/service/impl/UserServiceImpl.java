package com.jonet.eventbooking.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.components.MailExecutor;
import com.jonet.eventbooking.converter.UserMapper;
import com.jonet.eventbooking.customexception.ResourceAlreadyExistsException;
import com.jonet.eventbooking.dto.request.user.UserRequest;
import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.RoleCode;
import com.jonet.eventbooking.repository.UserRepository;
import com.jonet.eventbooking.service.EmailService;
import com.jonet.eventbooking.service.RoleService;
import com.jonet.eventbooking.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final RoleService roleService;
	private final MailExecutor mailExecutor;
	private final EmailService emailService;
	private final RedisTemplate<Object, Object> redisTemplate;
	private static final String REDIS_SET_KEY = "emails:registed_set";

	@Override
	public void create(UserRequest userRequest) {
		boolean isEmail = redisTemplate.opsForSet().isMember(REDIS_SET_KEY, userRequest.getEmail());
		if(Boolean.TRUE.equals(isEmail)) {
			throw new ResourceAlreadyExistsException("Email đã được đăng kí.Vui lòng đăng nhập");
		}
		UserEntity userEntity = userMapper.toUserEntity(userRequest);
		userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		userEntity.setRoles(List.of(roleService.getRoleByCode(RoleCode.ROLE_CUSTOMER.toString())));
		userRepository.save(userEntity);
		redisTemplate.opsForSet().add(REDIS_SET_KEY, userEntity.getEmail());
		mailExecutor.submitTask(() -> {
			emailService.sendEmailRegisterSuccess(userRequest.getEmail());
		});
	}

	@Override
	public void changePassword(UserRequest userRequest) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(UUID id, String email) {
		userRepository.deleteAccount(id);
		redisTemplate.opsForSet().remove(REDIS_SET_KEY, email);
	}

	@Override
	public List<UserResponse> getUsers() {
		List<UserEntity> users = userRepository.findAll();
		return users.stream().map(userMapper::toUserResponse).toList();
	}
}

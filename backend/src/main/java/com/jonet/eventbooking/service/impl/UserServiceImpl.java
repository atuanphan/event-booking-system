package com.jonet.eventbooking.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.components.MailExecutor;
import com.jonet.eventbooking.converter.UserMapper;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.dto.request.user.UserRequest;
import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.entity.RoleEntity;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.AuthProvider;
import com.jonet.eventbooking.enums.RoleCode;
import com.jonet.eventbooking.repository.RoleRepository;
import com.jonet.eventbooking.repository.UserRepository;
import com.jonet.eventbooking.service.EmailService;
import com.jonet.eventbooking.service.UserService;
import com.jonet.eventbooking.utils.PasswordSecureRandom;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final RoleRepository roleRepository;
	private final RedisTemplate<Object, Object> redisTemplate;
    private final PasswordSecureRandom passwordSecureRandom;
    private final PasswordEncoder passwordEncoder;
    private final MailExecutor mailExecutor;
    private final EmailService emailService;

	private static final String REDIS_SET_KEY = "emails:registed_set";
	private static final String DEFAULT_ROLE = "CUSTOMER"; 
    private static final int STATUS_ACTIVE = 1;

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
	public List<UserResponse> getCustomers() {
		List<UserEntity> customers = userRepository.findByRoles(List.of(getDefaultRole()));
		return customers.stream().map(userMapper::toUserResponse).toList();
	}

	@Transactional
	@Override
	public UserEntity findOrCreateByEmail(String email, String fullname,
                                           AuthProvider provider, String providerId) {

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                new OAuth2Error("email_not_found"),
                "Email không được cung cấp bởi " + provider
            );
        }

        return userRepository.findByEmailWithRoles(email)
                .map(existingUser -> updateExistingUser(existingUser, fullname, provider, providerId))
                .orElseGet(() -> createNewUser(email, fullname, provider, providerId));
    }

    private UserEntity createNewUser(String email, String fullname,
            AuthProvider provider, String providerId) {
        UserEntity user = UserEntity.builder()
                .email(email)
                .fullname(fullname != null && !fullname.isBlank() ? fullname : email)
                .password(null)
                .status(STATUS_ACTIVE)
                .provider(provider)
                .providerId(providerId)
                .roles(new ArrayList<>(List.of(getDefaultRole())))
                .build();
        return userRepository.save(user);
    }

    private UserEntity updateExistingUser(UserEntity user, String fullname,
                                           AuthProvider provider, String providerId) {
        if (fullname != null && !fullname.isBlank()) {
            user.setFullname(fullname);
        }

        // Nếu user vốn đăng ký LOCAL (có password), lần đầu login OAuth2
        // thì gắn thêm provider để nhận diện, KHÔNG xoá password cũ
        if (user.getProvider() == null) {
            user.setProvider(provider);
            user.setProviderId(providerId);
        }

        return userRepository.save(user);
    }

    private RoleEntity getDefaultRole() {
        return roleRepository.findByCode(RoleCode.CUSTOMER)
                .orElseThrow(() -> new IllegalStateException(
                    "Role mặc định '" + DEFAULT_ROLE + "' chưa tồn tại trong DB"));
    }

    @Override
    public Page<UserResponse> getStaff(String email, Pageable pageable) {
        List<RoleCode> staffRoles = new ArrayList<>(List.of(
                RoleCode.ADMIN,
                RoleCode.ORGANIZER));
        String normalizedEmail = email == null || email.isBlank() ? null : email.trim();
        if (normalizedEmail != null) {
            staffRoles.add(RoleCode.CUSTOMER);
        }
        return userRepository.findStaffByRolesAndEmail(staffRoles, normalizedEmail, pageable)
                .map(userMapper::toUserResponse);
    }

    @Override
    public void resetPassword(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        String temporaryPassword = passwordSecureRandom.generateTemporaryPassword(10);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);
        
        mailExecutor.submitTask(() -> {
            emailService.sendEmailResetPassword(user.getEmail(), temporaryPassword);
        });
    }

    @Override
    public void updateStaffRole(UUID userId, RoleCode role) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        RoleEntity roleEntity = roleRepository.findByCode(role).orElseThrow(() -> new EntityNotFoundException("Role not found"));
        user.setRoles(new ArrayList<>(List.of(roleEntity)));
        userRepository.save(user);
    }

}

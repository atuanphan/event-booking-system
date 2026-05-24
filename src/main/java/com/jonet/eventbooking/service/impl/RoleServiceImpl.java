package com.jonet.eventbooking.service.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.customexception.RequestTimeoutException;
import com.jonet.eventbooking.entity.RoleEntity;
import com.jonet.eventbooking.repository.RoleRepository;
import com.jonet.eventbooking.service.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
	private final RoleRepository roleRepository;
	private final RedisTemplate<Object, Object> redisTemplate;
	private static final String redis_role_key = "role:name:";
	private final ReentrantLock reentrantLock = new ReentrantLock();

	@Override
	public RoleEntity getRoleByCode(String code) {
		RoleEntity roleEntity = (RoleEntity) redisTemplate.opsForValue().get(redis_role_key.concat(code));
		if (roleEntity != null) {
			return roleEntity;
		}
		try {
			if (reentrantLock.tryLock(3, TimeUnit.SECONDS)) {
				try {
					roleEntity = (RoleEntity) redisTemplate.opsForValue().get(redis_role_key.concat(code));
					if (roleEntity != null) {
						return roleEntity;
					}
					roleEntity = roleRepository.findByCode(code)
							.orElseThrow(() -> new EntityNotFoundException("Role Not Found"));
					redisTemplate.opsForValue().set(redis_role_key.concat(code), roleEntity);
					return roleEntity;
				} finally {
					reentrantLock.unlock();
				}
			} else {
				roleEntity = (RoleEntity) redisTemplate.opsForValue().get(redis_role_key.concat(code));
                if (roleEntity != null) {
                    return roleEntity;
                }
                // Nếu vẫn không có, chủ động ném ra ngoại lệ để giải phóng Thread cho Tomcat, không bắt User đợi vô tận nữa.
                throw new RequestTimeoutException("Hệ thống đang bận, vui lòng thử lại sau vài giây!");
			}
		} catch (Exception e) {
			Thread.currentThread().interrupt();
            throw new RuntimeException("Thread bị gián đoạn", e);
		}

	}

}

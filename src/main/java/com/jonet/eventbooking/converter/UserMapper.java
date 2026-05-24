package com.jonet.eventbooking.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jonet.eventbooking.dto.request.user.UserRequest;
import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
	public UserEntity toUserEntity(UserRequest userRequest);
	
	
	public UserResponse toUserResponse(UserEntity userEntity);
}

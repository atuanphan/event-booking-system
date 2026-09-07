package com.jonet.eventbooking.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.jonet.eventbooking.dto.request.user.UserRequest;
import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.entity.RoleEntity;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.AuthProvider;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "password", ignore = true)
	public UserEntity toUserEntity(UserRequest userRequest);

	@Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoles")
	@Mapping(source = "provider", target = "provider", qualifiedByName = "mapProvider")
	UserResponse toUserResponse(UserEntity userEntity);

	@Named("mapRoles")
	default List<String> mapRoles(List<RoleEntity> roles) {
		if (roles == null) {
			return List.of();
		}

		return roles.stream().map(RoleEntity::getCode).toList();
	}
	
	@Named("mapProvider")
    default String mapProvider(AuthProvider provider) {
        return provider != null ? provider.name() : null;
    }
}

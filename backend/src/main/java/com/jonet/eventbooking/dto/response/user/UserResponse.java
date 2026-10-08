package com.jonet.eventbooking.dto.response.user;

import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.enums.RoleCode;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
	private UUID id;
	private String fullname;
	private String email;
	private List<RoleCode> roles;
	private String provider;
	private int status;
}

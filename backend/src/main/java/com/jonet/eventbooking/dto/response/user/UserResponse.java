package com.jonet.eventbooking.dto.response.user;

import java.util.List;
import java.util.UUID;

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
	private List<String> roles;
	private String provider;
}

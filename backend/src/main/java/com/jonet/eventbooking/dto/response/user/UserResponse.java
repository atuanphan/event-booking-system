package com.jonet.eventbooking.dto.response.user;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
	private UUID id;
	private String fullname;
	private String email;
}

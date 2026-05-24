package com.jonet.eventbooking.dto.request.user;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
	private UUID id;
	
	@NotBlank(message = "Email is required")
	@Email(message = "Email is not in the correct format")
	@Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email is not in the correct format")
	private String email;
	
	@NotBlank(message = "Fullname is required")
	private String fullname;
	
	@NotBlank(message = "Password is required")
	private String password;
	
	private int status = 1;
}

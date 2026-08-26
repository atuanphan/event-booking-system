package com.jonet.eventbooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthRequest {
	@NotBlank(message = "Email is required")
	@Email(message = "Email is not in the correct format")
	@Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email is not in the correct format")
	private String email;
	
	@NotBlank(message = "Password is required")
	private String password;
}

package com.jonet.eventbooking.controller.client;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.request.user.UserRequest;
import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
	private final UserService userService;
	
	@GetMapping("/auth")
	public ResponseEntity<List<UserResponse>> getCustomers() {
		return ResponseEntity.ok(userService.getUsers());
	}
	
	@PostMapping("/auth/register")
	public ResponseEntity<String> registerAccount(@Valid @RequestBody UserRequest userRequest) {
		userService.create(userRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@DeleteMapping("/auth/{id}/{email}")
	public ResponseEntity<String> delete(@PathVariable UUID id, @PathVariable String email) {
		userService.delete(id, email);
		return ResponseEntity.ok("delete successfully!");
	}
	
}

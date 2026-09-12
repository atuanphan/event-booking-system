package com.jonet.eventbooking.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/users")
public class UserController {
	private final UserService userService;

	@GetMapping
	public ResponseEntity<List<UserResponse>> getCustomers() {
		return ResponseEntity.ok(userService.getUsers());
	}

	@DeleteMapping("/{id}/{email}")
	public ResponseEntity<String> delete(@PathVariable UUID id, @PathVariable String email) {
		userService.delete(id, email);
		return ResponseEntity.ok("delete successfully!");
	}
}

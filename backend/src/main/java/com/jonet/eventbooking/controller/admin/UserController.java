package com.jonet.eventbooking.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.response.user.UserResponse;
import com.jonet.eventbooking.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/users")
public class UserController {
	private final UserService userService;

	@GetMapping
	public ResponseEntity<List<UserResponse>> getCustomers() {
		return ResponseEntity.ok(userService.getCustomers());
	}

	@DeleteMapping("/{id}/{email}")
	public ResponseEntity<String> delete(@PathVariable UUID id, @PathVariable String email) {
		userService.delete(id, email);
		return ResponseEntity.ok("delete successfully!");
	}

	@GetMapping("/staff")
	public ResponseEntity<Page<UserResponse>> getStaff(
			@RequestParam(required = false) String email,
			@RequestParam(defaultValue = "0") int page) {
		return ResponseEntity.ok(userService.getStaff(email, PageRequest.of(page, 8)));
	}
}

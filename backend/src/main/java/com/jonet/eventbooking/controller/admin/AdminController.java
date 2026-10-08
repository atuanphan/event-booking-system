package com.jonet.eventbooking.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.enums.RoleCode;
import com.jonet.eventbooking.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	private final UserService userService;
	
	@PutMapping("/users/{userId}/password/reset")
	public ResponseEntity<String> resetPassword(@PathVariable UUID userId) {
		userService.resetPassword(userId);
		return ResponseEntity.ok("Password update successfully!");
	} 

	@PutMapping("staff/{userId}/role")
	public ResponseEntity<?> updateStaffRole(@PathVariable UUID userId, @RequestParam String role) {
		userService.updateStaffRole(userId, RoleCode.valueOf(role));
		return ResponseEntity.ok("Staff role updated successfully!");
	}
	
	@DeleteMapping("/staff/{ids}")
	public ResponseEntity<String> delete(@PathVariable List<UUID> ids) {
//		userService.delete(ids);
		return ResponseEntity.ok("delete successfully!");
	}
}

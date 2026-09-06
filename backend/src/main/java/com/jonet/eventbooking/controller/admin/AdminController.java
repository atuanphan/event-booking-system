package com.jonet.eventbooking.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.request.user.UserRequest;
import com.jonet.eventbooking.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {
	private final UserService userService;
	
//	@PostMapping("/staff")
//	public ResponseEntity<String> create(@Valid @RequestBody UserRequest userRequest) {
//		userService.create(userRequest);
//		return ResponseEntity.status(HttpStatus.CREATED).build();
//	}
	
	@PutMapping("/staff")
	public ResponseEntity<String> changePassword(@Valid @RequestBody UserRequest userRequest) {
		userService.changePassword(userRequest);
		return ResponseEntity.ok("update successfully!");
	} 
	
	@DeleteMapping("/staff/{ids}")
	public ResponseEntity<String> delete(@PathVariable List<UUID> ids) {
//		userService.delete(ids);
		return ResponseEntity.ok("delete successfully!");
	}
}

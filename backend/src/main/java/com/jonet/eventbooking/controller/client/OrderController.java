package com.jonet.eventbooking.controller.client;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.service.OrderService;
import com.jonet.eventbooking.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class OrderController {
	private final OrderService orderService;
	private final PaymentService paymentService;
	
	@PostMapping
	public ResponseEntity<String> order(@RequestHeader("Idempotency-Key") String idempotencyKey,
			@Valid @RequestBody OrderRequest orderRequest, @AuthenticationPrincipal UUID userId,
			HttpServletRequest request) {
		return ResponseEntity.ok(paymentService.getPayment(idempotencyKey, orderRequest, request, userId));
	}

	@GetMapping("/my-tickets")
	public ResponseEntity<?> myTickets(Authentication authentication) {
		UUID userId = UUID.fromString(authentication.getName());
		return ResponseEntity.ok(orderService.myTickets(userId));
	}

	@GetMapping("/{id}/status")
	public ResponseEntity<?> getStatusOrder(@PathVariable String id) {
		UUID orderId = UUID.fromString(id.substring(0, 36));
		return ResponseEntity.ok(orderService.getOrderStatus(orderId));
	}
}

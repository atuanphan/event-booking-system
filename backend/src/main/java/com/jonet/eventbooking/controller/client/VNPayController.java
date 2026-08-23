package com.jonet.eventbooking.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;
import com.jonet.eventbooking.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class VNPayController {
	private final OrderService orderService;
	
	@GetMapping("/success")
	public ResponseEntity<?> success(VNPayReturnRequest request) {
		orderService.updateOrderStatus(request);
		return ResponseEntity.ok("pay successfully!");
	} 
}

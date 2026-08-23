package com.jonet.eventbooking.controller.client;

import java.io.UnsupportedEncodingException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.service.OrderService;
import com.jonet.eventbooking.service.impl.VNPayServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;
	private final VNPayServiceImpl vnpService;
	
	@PostMapping
	public ResponseEntity<String> order(@RequestBody OrderRequest orderRequest) throws UnsupportedEncodingException {
		orderService.createOrder(orderRequest);
		return ResponseEntity.ok(vnpService.createPaymentUrl(orderRequest));
	}
}

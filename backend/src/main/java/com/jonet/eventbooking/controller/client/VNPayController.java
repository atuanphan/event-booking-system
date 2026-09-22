package com.jonet.eventbooking.controller.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;
import com.jonet.eventbooking.dto.response.payment.VNPayIpnResponse;
import com.jonet.eventbooking.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/payment/vnpay")
@RequiredArgsConstructor
@Slf4j 
public class VNPayController {
	private final OrderService orderService;

	@GetMapping("/ipn")
	public ResponseEntity<VNPayIpnResponse> ipnUrl(VNPayReturnRequest request) {
		try {
			VNPayIpnResponse response = orderService.processVNpayIpn(request);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("VNPay IPN xử lý lỗi. vnp_TxnRef={}", request.getVnp_TxnRef(), e);
            return ResponseEntity.ok(VNPayIpnResponse.unknownError());
		}
	}
	
	@GetMapping("/callback")
	public ResponseEntity<?> result(VNPayReturnRequest request) {
		return ResponseEntity.ok(orderService.result(request));
	} 
}

package com.jonet.eventbooking.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.UUID;

import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;
import com.jonet.eventbooking.dto.response.payment.VNPayQueryResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
	String createPaymentUrl(UUID orderId, HttpServletRequest request) throws UnsupportedEncodingException;
	boolean calculateInboundHash(VNPayReturnRequest request);
	VNPayQueryResponse queryTransaction( UUID orderId, LocalDateTime orderCreatedAt, String orderInfo, String clientIp);
}

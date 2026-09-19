package com.jonet.eventbooking.service;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
	String createPaymentUrl(UUID orderId, HttpServletRequest request) throws UnsupportedEncodingException;
	boolean calculateInboundHash(VNPayReturnRequest request);
}

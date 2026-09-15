package com.jonet.eventbooking.service;

import java.io.UnsupportedEncodingException;

import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
	String createPaymentUrl(OrderRequest orderRequest, HttpServletRequest request) throws UnsupportedEncodingException;
	boolean calculateInboundHash(VNPayReturnRequest request);
}

package com.jonet.eventbooking.service;

import java.util.UUID;

import com.jonet.eventbooking.dto.request.order.OrderRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    String getPayment(String idempotencyKey, OrderRequest orderRequest, HttpServletRequest request, UUID userId);
}

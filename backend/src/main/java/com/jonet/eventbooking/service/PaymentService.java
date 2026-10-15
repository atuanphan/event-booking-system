package com.jonet.eventbooking.service;

import com.jonet.eventbooking.dto.request.order.OrderRequest;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    String getPayment(OrderRequest orderRequest, HttpServletRequest request);
}

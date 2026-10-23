package com.jonet.eventbooking.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.service.MomoService;
import com.jonet.eventbooking.service.OrderService;
import com.jonet.eventbooking.service.PaymentService;
import com.jonet.eventbooking.service.VNPayService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class PaymentServiceImpl implements PaymentService{
    private final OrderService orderService;
    private final VNPayService vnPayService;
    private final MomoService momoService;

    @Override
    public String getPayment(String idempotencyKey, OrderRequest orderRequest, HttpServletRequest request, UUID userId) {
        UUID orderId = orderService.createOrder(idempotencyKey, orderRequest, userId);
        switch (orderRequest.getPaymentMethod()) {
            case VNPAY : return vnPayService.createPaymentUrl(orderId, request);
            case MOMO : return momoService.createPaymentUrl(orderId);
            default : throw new IllegalArgumentException("Payment method invalid");
        }
    }

}

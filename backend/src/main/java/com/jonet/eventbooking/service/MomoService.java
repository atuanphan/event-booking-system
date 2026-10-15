package com.jonet.eventbooking.service;

import java.util.UUID;

import com.jonet.eventbooking.dto.request.payment.MomoReturnRequest;

public interface MomoService {
    String createPaymentUrl(UUID orderId);
    void handleIpn(MomoReturnRequest request);
}

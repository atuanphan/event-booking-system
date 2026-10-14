package com.jonet.eventbooking.service;

import java.util.UUID;

public interface MomoService {
    String createPaymentUrl(UUID orderId);
}

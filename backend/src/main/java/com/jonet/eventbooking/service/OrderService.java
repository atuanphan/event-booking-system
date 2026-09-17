package com.jonet.eventbooking.service;

import java.util.UUID;

import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;

public interface OrderService {
	public UUID createOrder(OrderRequest orderRequest);
	void updateOrderStatus(VNPayReturnRequest request);
	void scanExpiredOrders();
}

package com.jonet.eventbooking.service;

import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;

public interface OrderService {
	public void createOrder(OrderRequest orderRequest);
	void updateOrderStatus(VNPayReturnRequest request);
	void scanExpiredOrders();
}

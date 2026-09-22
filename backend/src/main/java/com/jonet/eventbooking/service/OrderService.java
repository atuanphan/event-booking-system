package com.jonet.eventbooking.service;

import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;
import com.jonet.eventbooking.dto.response.order.OrderResponse;
import com.jonet.eventbooking.dto.response.payment.VNPayIpnResponse;

public interface OrderService {
	public UUID createOrder(OrderRequest orderRequest);
	VNPayIpnResponse processVNpayIpn(VNPayReturnRequest request);
	void scanExpiredOrders();
	List<OrderResponse> myTickets(UUID userId);
	public String result(VNPayReturnRequest request);
}

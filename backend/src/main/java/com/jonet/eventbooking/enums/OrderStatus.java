package com.jonet.eventbooking.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
	  PENDING("Chờ thanh toán"),
    PROCESSING("Đang xử lý (Đang trên cổng thanh toán)"),
    COMPLETED("Đã thanh toán thành công"),
    CANCELLED("Đã hủy (Hoặc hết hạn)"),
    REFUNDED("Đã hoàn tiền"),
    FAILED("Thanh toán thất bại");
    
    private String name;
	
    private OrderStatus(String name) {
		this.name = name;
	}
}

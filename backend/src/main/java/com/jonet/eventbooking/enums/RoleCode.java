package com.jonet.eventbooking.enums;

import lombok.Getter;

@Getter
public enum RoleCode {
	ADMIN("Quản trị hệ thống"),
	ORGANIZER("Đơn vị tổ chức"),
	CUSTOMER("Khách hàng"),
	STAFF("Nhân viên");
	
	private String name;
	
	private RoleCode(String name) {
		this.name = name;
	}
}

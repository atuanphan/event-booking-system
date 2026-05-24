package com.jonet.eventbooking.enums;

import lombok.Getter;

@Getter
public enum EventStatus {
	UPCOMING("Sắp diễn ra"),
	ONGOING("Đang diễn ra"),
	FINISHED("Đã kết thúc"),
	DRAFT("Dự kiến"),
	CANCELLED("Đã hủy");
	
	private String name;
	
	private EventStatus(String name) {
		this.name = name;
	}
}

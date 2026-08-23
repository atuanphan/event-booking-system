package com.jonet.eventbooking.enums;

import lombok.Getter;

@Getter
public enum SeatStatus {
	AVAILABLE(""),
	HOLDING(""),
	SOLD(""),
	LOCKED("");
	
	private String name;
	private SeatStatus(String name) {
		this.name = name;
	}
}

package com.jonet.eventbooking.enums;

import lombok.Getter;

@Getter
public enum EventStatus {
	UPCOMING(""),
	ONGOING(""),
	FINISHED(""),
	DRAFT(""),
	CANCELLED("");
	
	private String name;
	
	private EventStatus(String name) {
		this.name = name;
	}
}

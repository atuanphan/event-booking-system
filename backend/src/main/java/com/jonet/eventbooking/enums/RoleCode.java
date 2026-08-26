package com.jonet.eventbooking.enums;

import lombok.Getter;

@Getter
public enum RoleCode {
	ADMIN(""),
	ORGANIZER(""),
	CUSTOMER(""),
	STAFF("");
	
	private String name;
	
	private RoleCode(String name) {
		this.name = name;
	}
}

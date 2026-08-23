package com.jonet.eventbooking.enums;

import lombok.Getter;

@Getter
public enum RoleCode {
	ROLE_ADMIN(""),
	ROLE_CUSTOMER(""),
	ROLE_STAFF("");
	
	private String name;
	
	private RoleCode(String name) {
		this.name = name;
	}
}

package com.jonet.eventbooking.service;

import com.jonet.eventbooking.entity.RoleEntity;
import com.jonet.eventbooking.enums.RoleCode;

public interface RoleService {
	public RoleEntity getRoleByCode(RoleCode code);
}

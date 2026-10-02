package com.jonet.eventbooking.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.enums.RoleCode;

public record RefreshTokenPayload(UUID id, String email, List<RoleCode> roles) implements Serializable{

}

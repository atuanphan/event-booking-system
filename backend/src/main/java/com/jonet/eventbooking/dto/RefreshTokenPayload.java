package com.jonet.eventbooking.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record RefreshTokenPayload(UUID id, String email, List<String> roles) implements Serializable{

}

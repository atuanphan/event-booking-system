package com.jonet.eventbooking.dto;

import org.springframework.http.ResponseCookie;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshResult {
    private String accessToken;
    private ResponseCookie refreshToken;
}

package com.jonet.eventbooking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties(prefix = "vnpay")
@Validated
public record VnpayProperties(
    @NotBlank String payUrl,
    @NotBlank String returnUrl,
    @NotBlank String tmnCode,
    @NotBlank String hashSecret,
    @NotBlank String apiUrl,
    @NotBlank String queryDrUrl
) {}

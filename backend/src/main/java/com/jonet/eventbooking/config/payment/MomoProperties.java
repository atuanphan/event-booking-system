package com.jonet.eventbooking.config.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@ConfigurationProperties (prefix = "momo")
@Validated 
public record MomoProperties(
    @NotBlank String partnerCode,
    @NotBlank String accessKey,
    @NotBlank String secretKey,
    @NotBlank String endpoint,
    @NotBlank String ipnUrl,
    @NotBlank String redirectUrl
) {

}

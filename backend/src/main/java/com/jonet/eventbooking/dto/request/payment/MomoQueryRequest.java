package com.jonet.eventbooking.dto.request.payment;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder
public class MomoQueryRequest {
    private String partnerCode;
    private String accessKey;
    private String requestId;
    private Long amount;
    private String orderId;
    private String orderInfo;
    private String redirectUrl;
    private String ipnUrl;
    private String requestType;
    private String extraData;
    private String lang;
    private String signature;
}

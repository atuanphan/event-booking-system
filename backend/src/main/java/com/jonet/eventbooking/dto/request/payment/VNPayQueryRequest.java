package com.jonet.eventbooking.dto.request.payment;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
public class VNPayQueryRequest {
    private String vnp_RequestId;
    private String vnp_Version;
    private String vnp_Command;
    private String vnp_TmnCode;
    private String vnp_TxnRef;
    private String vnp_OrderInfo;
    private String vnp_TransactionDate; 
    private String vnp_CreateDate; 
    private String vnp_IpAddr;
    private String vnp_SecureHash;
}
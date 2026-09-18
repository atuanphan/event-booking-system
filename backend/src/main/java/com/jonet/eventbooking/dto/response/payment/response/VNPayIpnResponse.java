package com.jonet.eventbooking.dto.response.payment.response;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
public class VNPayIpnResponse {
    private String RspCode;
    private String Message;

    public static VNPayIpnResponse invalidSignature() {
        return new VNPayIpnResponse("97", "Invalid Signature");
    }
 
    public static VNPayIpnResponse orderNotFound() {
        return new VNPayIpnResponse("01", "Order not found");
    }
 
    public static VNPayIpnResponse invalidAmount() {
        return new VNPayIpnResponse("04", "Invalid amount");
    }
 
    public static VNPayIpnResponse orderAlreadyConfirmed() {
        return new VNPayIpnResponse("02", "Order already confirmed");
    }
 
    public static VNPayIpnResponse success() {
        return new VNPayIpnResponse("00", "Confirm Success");
    }
 
    public static VNPayIpnResponse unknownError() {
        return new VNPayIpnResponse("99", "Unknown error");
    }
}

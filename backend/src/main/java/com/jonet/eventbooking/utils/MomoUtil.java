package com.jonet.eventbooking.utils;

import com.jonet.eventbooking.dto.request.payment.MomoReturnRequest;

public class MomoUtil {
    public static String getQueryParam(MomoReturnRequest request) {
        String raw = "amount=" + request.getAmount()
                + "&extraData=" + request.getExtraData()
                + "&message=" + request.getMessage()
                + "&orderId=" + request.getOrderId()
                + "&orderInfo=" + request.getOrderInfo()
                + "&orderType=" + request.getOrderType()
                + "&partnerCode=" + request.getPartnerCode()
                + "&payType=" + request.getPayType()
                + "&requestId=" + request.getRequestId()
                + "&responseTime=" + request.getResponseTime()
                + "&resultCode=" + request.getResultCode()
                + "&transId=" + request.getTransId();
        return raw;
    }
}

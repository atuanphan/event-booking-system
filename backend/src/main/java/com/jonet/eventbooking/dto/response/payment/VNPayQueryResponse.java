package com.jonet.eventbooking.dto.response.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data 
@JsonIgnoreProperties(ignoreUnknown = true)
public class VNPayQueryResponse {
    private String vnp_ResponseId;
    private String vnp_Command;
    private String vnp_ResponseCode;   // "00" = query thành công (khác với kết quả giao dịch)
    private String vnp_Message;
    private String vnp_TmnCode;
    private String vnp_TxnRef;
    private String vnp_Amount;
    private String vnp_BankCode;
    private String vnp_PayDate;
    private String vnp_TransactionNo;
    private String vnp_TransactionType;
    private String vnp_TransactionStatus; // "00" = giao dịch thành công thực sự
    private String vnp_OrderInfo;
    private String vnp_SecureHash;
 
    public boolean isTransactionSuccess() {
        return "00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus);
    }
}

package com.jonet.eventbooking.dto.request.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VNPayReturnRequest {
	private String vnp_BankCode;
	private String vnp_BankTranNo;
	private String vnp_CardType;
	private String vnp_OrderInfo;
	private String vnp_PayDate;
	private String vnp_TmnCode;
	private String vnp_TransactionStatus;
	private String vnp_TxnRef;
	private String vnp_ResponseCode;
	private String vnp_TransactionNo;
	private String vnp_SecureHash;
	private String vnp_Amount;
}

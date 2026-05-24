package com.jonet.eventbooking.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jonet.eventbooking.config.VNPayConfig;
import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;
import com.jonet.eventbooking.projections.OrderMinInfo;
import com.jonet.eventbooking.repository.OrderRepository;
import com.jonet.eventbooking.service.PaymentService;
import com.jonet.eventbooking.utils.VNPayURLUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements PaymentService {
	private final OrderRepository orderRepository;

	@Override
	public String createPaymentUrl(OrderRequest orderRequest) throws UnsupportedEncodingException {
		String vnp_Version = "2.1.0";
		String vnp_Command = "pay";
		String orderType = "100001";// lấy theo danh mục sản phẩm của vnpay
		String bankCode = "NCB";
		OrderMinInfo order = orderRepository.getOrderByUser(orderRequest.getUserId());
		UUID vnp_TxnRef = order.getId();

		String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
		long totalAmount = order.getTotalAmount().multiply(new BigDecimal("100")).longValueExact();
		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", vnp_Version);
		vnp_Params.put("vnp_Command", vnp_Command);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(totalAmount));
		vnp_Params.put("vnp_CurrCode", "VND");
		vnp_Params.put("vnp_BankCode", bankCode);
		vnp_Params.put("vnp_TxnRef", String.valueOf(vnp_TxnRef));
		vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + vnp_TxnRef);
		vnp_Params.put("vnp_OrderType", orderType);
		vnp_Params.put("vnp_Locale", "vn");
		vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
		vnp_Params.put("vnp_IpAddr", "10.40.79.198");

		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_CreateDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

		cld.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

		List fieldNames = new ArrayList(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				// Build hash data
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				// Build query
				query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();
		String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		String vnpPaymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
		return vnpPaymentUrl;
	}

	@Override
	public boolean calculateInboundHash(VNPayReturnRequest request) {
		request.setVnp_OrderInfo(request.getVnp_OrderInfo().replace(" ", "+"));
		StringBuilder builder = new StringBuilder();
		for(Map.Entry<String, String> map : VNPayURLUtils.getURL(request).entrySet()) {
			builder.append(map.getKey()).append("=").append(map.getValue()).append("&");
		}
		if(builder.length() > 0) builder.deleteCharAt(builder.length() - 1);
		String hashUrl = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, builder.toString());
		boolean check = VNPayURLUtils.verifyCallbackSignature(request.getVnp_SecureHash(), hashUrl);
		return check;
	}

}

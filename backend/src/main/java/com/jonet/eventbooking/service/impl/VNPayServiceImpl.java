package com.jonet.eventbooking.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.jonet.eventbooking.config.VNPayConfig;
import com.jonet.eventbooking.config.VnpayProperties;
import com.jonet.eventbooking.customexception.VNPayQueryDrException;
import com.jonet.eventbooking.dto.request.payment.VNPayQueryRequest;
import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;
import com.jonet.eventbooking.dto.response.payment.VNPayQueryResponse;
import com.jonet.eventbooking.repository.OrderRepository;
import com.jonet.eventbooking.repository.projections.OrderMinInfo;
import com.jonet.eventbooking.service.PaymentService;
import com.jonet.eventbooking.utils.VNPayUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j 
public class VNPayServiceImpl implements PaymentService {
	private final OrderRepository orderRepository;
	private final VNPayConfig vnPayConfig;
	private final VnpayProperties vnpayProperties;
	private final RestTemplate restTemplate = new RestTemplate();

	private static final String VNP_VERSION = "2.1.0";
	private static final String VNP_COMMAND = "pay";
	private static final DateTimeFormatter VNP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	@Override
	public String createPaymentUrl(UUID orderId, HttpServletRequest request) throws UnsupportedEncodingException {
		String orderType = "190003";// lấy theo danh mục sản phẩm của vnpay
		String bankCode = "NCB";
		OrderMinInfo order = orderRepository.getOrderById(orderId);
		UUID vnp_TxnRef = order.getId();

		String vnp_TmnCode = vnpayProperties.tmnCode();
		long totalAmount = order.getTotalAmount().multiply(new BigDecimal("100")).longValueExact();
		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", VNP_VERSION);
		vnp_Params.put("vnp_Command", VNP_COMMAND);
		vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf(totalAmount));
		vnp_Params.put("vnp_CurrCode", "VND");
		vnp_Params.put("vnp_BankCode", bankCode);
		vnp_Params.put("vnp_TxnRef", String.valueOf(vnp_TxnRef));
		vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + vnp_TxnRef);
		vnp_Params.put("vnp_OrderType", orderType);
		vnp_Params.put("vnp_Locale", "vn");
		vnp_Params.put("vnp_ReturnUrl", vnpayProperties.returnUrl());
		vnp_Params.put("vnp_IpAddr", vnPayConfig.getIpAddress(request));

		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
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
		String vnp_SecureHash = vnPayConfig.hmacSHA512(vnpayProperties.hashSecret(), hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		String vnpPaymentUrl = vnpayProperties.payUrl() + "?" + queryUrl;
		return vnpPaymentUrl;
	}

	@Override
	public boolean calculateInboundHash(VNPayReturnRequest request) {
		request.setVnp_OrderInfo(request.getVnp_OrderInfo().replace(" ", "+"));
		StringBuilder builder = new StringBuilder();
		for(Map.Entry<String, String> map : VNPayUtils.getURL(request).entrySet()) {
			builder.append(map.getKey()).append("=").append(map.getValue()).append("&");
		}
		if(builder.length() > 0) builder.deleteCharAt(builder.length() - 1);
		String hashUrl = vnPayConfig.hmacSHA512(vnpayProperties.hashSecret(), builder.toString());
		boolean check = VNPayUtils.verifyCallbackSignature(request.getVnp_SecureHash(), hashUrl);
		return check;
	}

	@Override
	public VNPayQueryResponse queryTransaction(UUID orderId, LocalDateTime orderCreatedAt, String orderInfo, String clientIp) {
		String requestId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
		String createDate = LocalDateTime.now().format(VNP_DATE_FORMAT);
		String transactionDate = orderCreatedAt.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime().format(VNP_DATE_FORMAT);
		String txnRef = orderId.toString();

		String secureHash = buildSecureHash(
				requestId, vnpayProperties.tmnCode(), txnRef, transactionDate, createDate, clientIp, orderInfo);

		VNPayQueryRequest request = VNPayQueryRequest.builder()
				.vnp_RequestId(requestId)
				.vnp_Version(VNP_VERSION)
				.vnp_Command("querydr")
				.vnp_TmnCode(vnpayProperties.tmnCode())
				.vnp_TxnRef(txnRef)
				.vnp_OrderInfo(orderInfo)
				.vnp_TransactionDate(transactionDate)
				.vnp_CreateDate(createDate)
				.vnp_IpAddr(clientIp)
				.vnp_SecureHash(secureHash)
				.build();

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<VNPayQueryRequest> entity = new HttpEntity<>(request, headers);

			ResponseEntity<VNPayQueryResponse> response = restTemplate.postForEntity(
					vnpayProperties.queryDrUrl(), entity, VNPayQueryResponse.class);

			VNPayQueryResponse body = response.getBody();
			log.info("queryDr order={} -> responseCode={}, transactionStatus={}",
					orderId,
					body != null ? body.getVnp_ResponseCode() : null,
					body != null ? body.getVnp_TransactionStatus() : null);

			return body;
		} catch (RestClientException e) {
			log.error("Gọi queryDr thất bại cho order {}", orderId, e);
			throw new VNPayQueryDrException("Không thể gọi queryDr: ");
		}
	}

	private String buildSecureHash(String requestId, String tmnCode, String txnRef,
                                     String transactionDate, String createDate,
                                     String ipAddr, String orderInfo) {
        String  hashData = String.join("|",
                requestId,
                VNP_VERSION,
                "querydr",
                tmnCode,
                txnRef,
                transactionDate,
                createDate,
                ipAddr,
                orderInfo
        );
        return vnPayConfig.hmacSHA512(vnpayProperties.hashSecret(), hashData);
    }
}

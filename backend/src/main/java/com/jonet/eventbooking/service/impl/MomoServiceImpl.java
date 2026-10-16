package com.jonet.eventbooking.service.impl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.jonet.eventbooking.config.payment.MomoProperties;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.dto.request.payment.MomoQueryRequest;
import com.jonet.eventbooking.dto.request.payment.MomoReturnRequest;
import com.jonet.eventbooking.entity.OrderEntity;
import com.jonet.eventbooking.enums.OrderStatus;
import com.jonet.eventbooking.repository.OrderRepository;
import com.jonet.eventbooking.service.MomoService;
import com.jonet.eventbooking.utils.HashUtil;
import com.jonet.eventbooking.utils.MomoUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoServiceImpl implements MomoService {
    private final MomoProperties momoProperties;
    private final OrderRepository orderRepository;
    private final RestClient restClient;

    @Override
    public String createPaymentUrl(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        BigDecimal totalAmount = order.getTotalAmount();
        if (totalAmount.compareTo(BigDecimal.valueOf(1000)) < 0
                || totalAmount.compareTo(BigDecimal.valueOf(50_000_000)) > 0) {
            throw new ArithmeticException("Amount is out of range");
        }
        Long amount = order.getTotalAmount().longValueExact();

        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toán qua ví MoMo";
        String extraData = "";
        String requestType = "captureWallet";
        String momoOrderId = orderId + "-" + System.currentTimeMillis();

        String data = "accessKey=" + momoProperties.accessKey()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + momoProperties.ipnUrl()
                + "&orderId=" + momoOrderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + momoProperties.partnerCode()
                + "&redirectUrl=" + momoProperties.redirectUrl()
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = HashUtil.hmacSha256(data, momoProperties.secretKey());

        MomoQueryRequest request = MomoQueryRequest.builder()
                .partnerCode(momoProperties.partnerCode())
                .accessKey(momoProperties.accessKey())
                .requestId(requestId)
                .amount(amount)
                .orderId(momoOrderId)
                .orderInfo(orderInfo)
                .redirectUrl(momoProperties.redirectUrl())
                .ipnUrl(momoProperties.ipnUrl())
                .extraData(extraData)
                .requestType(requestType)
                .lang("vi")
                .signature(signature)
                .build();

        Map<String, Object> response;
        try {
            response = restClient.post()
                    .uri(momoProperties.endpoint())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientResponseException e) {
            log.error("MoMo HTTP {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalStateException("Không thể tạo thanh toán MoMo", e);
        } catch (ResourceAccessException e) {
            log.error("Không kết nối được MoMo", e);
            throw new IllegalStateException("MoMo không phản hồi", e);
        }

        if (response == null || !Integer.valueOf(0).equals(response.get("resultCode"))) {
            throw new IllegalStateException(
                    "MoMo error: " + (response == null ? "no response" : response.get("message")));
        }
        return (String) response.get("payUrl");
    }

    @Override
    @Transactional
    public void handleIpn(MomoReturnRequest request) {
        String expectedSignature = HashUtil.hmacSha256(MomoUtil.getQueryParam(request), momoProperties.secretKey());
        if (!MessageDigest.isEqual(
                expectedSignature.getBytes(StandardCharsets.UTF_8),
                request.getSignature().getBytes(StandardCharsets.UTF_8))) {
            log.warn("MoMo IPN sai signature: {}", request.getSignature());
            return;
        }

        UUID orderId = UUID.fromString(request.getOrderId().substring(0, 36));
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.COMPLETED) return;

        if (order.getTotalAmount().compareTo(BigDecimal.valueOf(request.getAmount())) != 0) {
            log.error("Sai số tiền: IPN={}, order={}", request.getAmount(), order.getTotalAmount());
            return;
        }

        if (request.getResultCode() == 0) {
            order.setStatus(OrderStatus.COMPLETED);
        } else {
            order.setStatus(OrderStatus.FAILED);
            log.info("MoMo thất bại: {} - {}", request.getResultCode(), request.getMessage());
        }
        orderRepository.save(order);
    }

}

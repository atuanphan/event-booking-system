package com.jonet.eventbooking.components;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jonet.eventbooking.customexception.VNPayQueryDrException;
import com.jonet.eventbooking.dto.response.payment.VNPayQueryResponse;
import com.jonet.eventbooking.entity.OrderEntity;
import com.jonet.eventbooking.entity.OrderItemsEntity;
import com.jonet.eventbooking.enums.OrderStatus;
import com.jonet.eventbooking.repository.OrderItemRepository;
import com.jonet.eventbooking.repository.OrderRepository;
import com.jonet.eventbooking.service.PaymentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@Slf4j 
@RequiredArgsConstructor 
public class ExpiredOrderCleanupJob {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final PaymentService paymentService;

    @Value("${server.ip}")
    private String SERVER_IP;

    private String REDIS_KEY_PREFIX = "ticket:stock:";

    @Scheduled(fixedDelay = 60000)
    public void cleanupExpiredOrders() {
        List<OrderEntity> expiredOrders = orderRepository
                .findByStatusAndExpiresAtBefore(OrderStatus.PENDING.name(), LocalDateTime.now(), PageRequest.of(0, 200));

        if (expiredOrders.isEmpty()) {
            return;
        }

        log.info("Tìm thấy {} order PENDING đã hết hạn, bắt đầu xử lý", expiredOrders.size());

        for (OrderEntity order : expiredOrders) {
            try {
                reconcileAndCancelIfNeeded(order);
            } catch (Exception e) {
                log.error("Lỗi khi hủy order hết hạn {}", order.getId(), e);
            }
        }
    }

     @Transactional 
    public void cancelExpiredOrder(UUID orderId) {
        OrderEntity order = orderRepository.findByIdForUpdate(orderId).orElse(null);
        if (order == null) {
            return;
        }
 
        // Double-check lại status sau khi lock, phòng trường hợp IPN vừa
        // xử lý xong ngay trước khi job kịp lock được order này.
        if (!OrderStatus.PENDING.name().equals(order.getStatus())) {
            log.info("Order {} đã được xử lý (status={}) trước khi job kịp hủy, bỏ qua",
                    orderId, order.getStatus());
            return;
        }
 
        orderRepository.updateOrderStatusById(orderId, OrderStatus.CANCELLED.name());
 
        List<OrderItemsEntity> items = orderItemRepository.findByOrderId(orderId);
        items.forEach(item ->
                redisTemplate.opsForValue().increment(
                        REDIS_KEY_PREFIX + item.getTicketType().getId(),
                        item.getQuantity())
        );
 
        log.info("Đã hủy order hết hạn {} và hoàn {} loại vé", orderId, items.size());
    }

    private void reconcileAndCancelIfNeeded(OrderEntity order) {
        VNPayQueryResponse queryResult;
        try {
            queryResult = paymentService.queryTransaction(
                    order.getId(), order.getExpiresAt(), "Thanh toan don hang", SERVER_IP);
        } catch (VNPayQueryDrException e) {
            // Không hỏi được VNPay (lỗi mạng/timeout) - SKIP order này ở lần
            // chạy hiện tại, không hủy vội. Thử lại ở lần quét sau.
            log.warn("Không thể đối soát order {}, bỏ qua lần này: {}",
                    order.getId(), e.getMessage());
            return;
        }
 
        if (queryResult != null && queryResult.isTransactionSuccess()) {
            // VNPay xác nhận giao dịch ĐÃ thành công - không hủy, mà set
            // COMPLETED giống hệt logic IPN, dùng chung method idempotent.
            log.warn("Order {} đã hết hạn giữ chỗ nhưng VNPay xác nhận đã thanh toán " +
                    "thành công - complete lại thay vì hủy", order.getId());
            completeOrderFromReconciliation(order.getId());
            return;
        }
 
        // VNPay xác nhận KHÔNG có giao dịch thành công -> an tâm hủy + hoàn vé.
        cancelExpiredOrder(order.getId());
    }
 
    @Transactional
    public void completeOrderFromReconciliation(UUID orderId) {
        OrderEntity order = orderRepository.findByIdForUpdate(orderId).orElse(null);
        if (order == null || !OrderStatus.PENDING.name().equals(order.getStatus())) {
            return; // đã được xử lý bởi luồng khác (IPN) trong lúc chờ queryDr trả về
        }
        orderRepository.updateOrderStatusById(orderId, OrderStatus.COMPLETED.name());
        log.info("Order {} được complete qua đối soát queryDr", orderId);
    }
 
}

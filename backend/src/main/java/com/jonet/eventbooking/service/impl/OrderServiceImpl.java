package com.jonet.eventbooking.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.converter.OrderMapper;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;
import com.jonet.eventbooking.dto.response.order.OrderResponse;
import com.jonet.eventbooking.dto.response.payment.response.VNPayIpnResponse;
import com.jonet.eventbooking.entity.OrderEntity;
import com.jonet.eventbooking.entity.OrderItemsEntity;
import com.jonet.eventbooking.entity.TicketTypeEntity;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.OrderStatus;
import com.jonet.eventbooking.repository.OrderItemRepository;
import com.jonet.eventbooking.repository.OrderRepository;
import com.jonet.eventbooking.repository.TicketTypeRepository;
import com.jonet.eventbooking.service.OrderService;
import com.jonet.eventbooking.service.PaymentService;
import com.jonet.eventbooking.service.TicketTypeService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j 
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final TicketTypeRepository ticketTypeRepository;
	private final PaymentService paymentService;
	private final TicketTypeService ticketTypeService;
	private final StringRedisTemplate redisTemplate;
	private final OrderItemRepository orderItemRepository;
	private final String redisKey = "ticket:stock:";
	private final OrderMapper orderMapper;

	@Override
	public UUID createOrder(OrderRequest orderRequest) {
		OrderEntity orderEntity = OrderEntity.builder().user(new UserEntity(orderRequest.getUserId()))
				.status(orderRequest.getStatus()).expiresAt(LocalDateTime.now().plusMinutes(15)).build();
		List<OrderItemsEntity> orderItemsEntities = orderRequest.getOrderItems().stream().map(req -> {
			TicketTypeEntity ticket = ticketTypeRepository.findById(req.getTicketTypeId())
					.orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
			ticketTypeService.updateAvailableQuantity(ticket.getId(), req.getQuantity());
			return OrderItemsEntity.builder().order(orderEntity).ticketType(ticket).price(ticket.getPrice())
					.quantity(req.getQuantity()).build();
		}).collect(Collectors.toList());

		BigDecimal totalAmount = orderItemsEntities.stream()
				.map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
				
		orderEntity.setOrderItems(orderItemsEntities);
		orderEntity.setTotalAmount(totalAmount);
		orderRepository.save(orderEntity);

		return orderEntity.getId();
	}

	@Override
	public void scanExpiredOrders() {
		orderRepository.deleteByStatus(OrderStatus.CANCELLED.name());
	}

	@Override
	public VNPayIpnResponse processVNpayIpn(VNPayReturnRequest request) {
		UUID orderId;
		try {
			orderId = UUID.fromString(request.getVnp_TxnRef());
		} catch (IllegalArgumentException e) {
			log.warn("VNPay IPN: vnp_TxnRef không hợp lệ: {}", request.getVnp_TxnRef());
            return VNPayIpnResponse.orderNotFound();
		}

		OrderEntity order = orderRepository.findByIdForUpdate(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Order Not Found!"));
		if (order == null) {
            log.warn("VNPay IPN: không tìm thấy order {}", orderId);
            return VNPayIpnResponse.orderNotFound();
        }
		
		boolean validSignature = paymentService.calculateInboundHash(request);
		if (!validSignature) {
            log.warn("VNPay IPN: sai chữ ký cho order {}", orderId);
            return VNPayIpnResponse.invalidSignature();
        }

		if (!OrderStatus.PENDING.name().equals(order.getStatus())) {
            log.info("VNPay IPN: order {} đã ở trạng thái {}, bỏ qua xử lý lại",
                    orderId, order.getStatus());
            return VNPayIpnResponse.orderAlreadyConfirmed();
        }

		BigDecimal vnpAmount;
        try {
            vnpAmount = new BigDecimal(request.getVnp_Amount())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            log.warn("VNPay IPN: vnp_Amount không hợp lệ: {}", request.getVnp_Amount());
            return VNPayIpnResponse.invalidAmount();
        }
 
        if (order.getTotalAmount().compareTo(vnpAmount) != 0) {
            log.warn("VNPay IPN: sai số tiền cho order {}. Expected={}, Got={}",
                    orderId, order.getTotalAmount(), vnpAmount);
            return VNPayIpnResponse.invalidAmount();
        }

		if ("00".equals(request.getVnp_ResponseCode())) {
            orderRepository.updateOrderStatusById(orderId, OrderStatus.COMPLETED.name());
            orderItemRepository.findByOrderId(orderId).forEach(item ->
                    ticketTypeRepository.updateAvailableQuantity(
                            item.getQuantity(), item.getTicketType().getId())
            );
            log.info("VNPay IPN: order {} COMPLETED", orderId);
        } else {
            orderRepository.updateOrderStatusById(orderId, OrderStatus.CANCELLED.name());
            orderItemRepository.findByOrderId(orderId).forEach(item ->
                    redisTemplate.opsForValue().increment(
                            redisKey + item.getTicketType().getId(),
                            item.getQuantity())
            );
            log.info("VNPay IPN: order {} CANCELLED, responseCode={}",
                    orderId, request.getVnp_ResponseCode());
        }

		return VNPayIpnResponse.success();
	}

	@Override
	public List<OrderResponse> myTickets(UUID userId) {
		List<OrderEntity> orderEntities = orderRepository.findByUserId(userId);
		return orderMapper.toResponseList(orderEntities);
	}

	@Override
	public String result(VNPayReturnRequest request) {
		UUID orderId;
        try {
            orderId = UUID.fromString(request.getVnp_TxnRef());
        } catch (IllegalArgumentException e) {
            return "INVALID_ORDER_ID";
        }
 
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order Not Found!"));
		return order.getStatus();
	}

}

package com.jonet.eventbooking.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;
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

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final TicketTypeRepository ticketTypeRepository;
	private final PaymentService paymentService;
	private final TicketTypeService ticketTypeService;
	private final StringRedisTemplate redisTemplate;
	private final OrderItemRepository orderItemRepository;
	private final String redisKey = "ticket:stock:";

	@Override
	public void createOrder(OrderRequest orderRequest) {
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
	}

	@Override
	public void scanExpiredOrders() {
		orderRepository.deleteByStatus(OrderStatus.CANCELLED.name());
	}

	@Override
	public void updateOrderStatus(VNPayReturnRequest request) {
		UUID orderId = UUID.fromString(request.getVnp_TxnRef());
		OrderEntity order = orderRepository.findById(orderId)
				.orElseThrow(() -> new EntityNotFoundException("Order Not Found!"));
		BigDecimal vnp_Amount = new BigDecimal(request.getVnp_Amount());
		BigDecimal realAmount = vnp_Amount.divide(new BigDecimal("100"));
		if ("00".equals(request.getVnp_ResponseCode()) && paymentService.calculateInboundHash(request) == true
				&& order.getTotalAmount().compareTo(realAmount) == 0
				&& order.getStatus().equals(OrderStatus.PENDING.name())) {
			orderRepository.updateOrderStatusById(order.getId(), OrderStatus.COMPLETED.name());
			orderItemRepository.findByOrderId(orderId).forEach(req -> {
				ticketTypeRepository.updateAvailableQuantity(req.getQuantity(), req.getTicketType().getId());
			});
		} else {
			orderRepository.updateOrderStatusById(orderId,
					OrderStatus.CANCELLED.name());
			orderItemRepository.findByOrderId(orderId).forEach(req -> {
				redisTemplate.opsForValue().increment(redisKey + req.getTicketType().getId(), req.getQuantity());
			});

		}
	}

}

package com.jonet.eventbooking.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.jonet.eventbooking.converter.OrderMapper;
import com.jonet.eventbooking.customexception.BadRequestException;
import com.jonet.eventbooking.customexception.DuplicateIdempotencyKeyException;
import com.jonet.eventbooking.customexception.EntityNotFoundException;
import com.jonet.eventbooking.customexception.InvalidQuantityException;
import com.jonet.eventbooking.customexception.OrderItemRequiredException;
import com.jonet.eventbooking.dto.MonthlyTicketDTO;
import com.jonet.eventbooking.dto.request.order.OrderItemsRequest;
import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.dto.request.order.OrderSearchRequest;
import com.jonet.eventbooking.dto.request.payment.VNPayReturnRequest;
import com.jonet.eventbooking.dto.response.order.OrderListResponse;
import com.jonet.eventbooking.dto.response.order.OrderResponse;
import com.jonet.eventbooking.dto.response.payment.VNPayIpnResponse;
import com.jonet.eventbooking.entity.OrderEntity;
import com.jonet.eventbooking.entity.OrderItemsEntity;
import com.jonet.eventbooking.entity.TicketTypeEntity;
import com.jonet.eventbooking.entity.UserEntity;
import com.jonet.eventbooking.enums.OrderStatus;
import com.jonet.eventbooking.model.TicketQuantityChangedEvent;
import com.jonet.eventbooking.repository.OrderItemRepository;
import com.jonet.eventbooking.repository.OrderRepository;
import com.jonet.eventbooking.repository.TicketTypeRepository;
import com.jonet.eventbooking.repository.UserRepository;
import com.jonet.eventbooking.repository.projections.MonthlyTicketProjection;
import com.jonet.eventbooking.service.OrderService;
import com.jonet.eventbooking.service.VNPayService;
import com.jonet.eventbooking.utils.RedisUtil;
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
	private final VNPayService paymentService;
	private final TicketTypeService ticketTypeService;
	private final StringRedisTemplate redisTemplate;
	private final OrderItemRepository orderItemRepository;
	private final OrderMapper orderMapper;
	private final ApplicationEventPublisher eventPublisher;
	private final OrderItemRepository orderItemsRepository;
	private final UserRepository userRepository;
	private final SimpMessagingTemplate messagingTemplate;

	@Override
	public UUID createOrder(String idempotencyKey, OrderRequest orderRequest, UUID userId) {
		validateIdempotencyKeyFormat(idempotencyKey);

		if (!TransactionSynchronizationManager.isSynchronizationActive()) {
			throw new IllegalStateException("Running in transaction");
		}

		Map<UUID, Integer> quantities = toQuantities(orderRequest);
		Map<UUID, TicketTypeEntity> tickets = ticketTypeRepository.findAllById(quantities.keySet())
				.stream().collect(Collectors.toMap(TicketTypeEntity::getId, Function.identity()));
		if(tickets.size() != quantities.size()) {
			throw new EntityNotFoundException("One or more ticket types not found");
		}

		if (orderRepository.hasFinishedEvent(new ArrayList<>(quantities.keySet()))) {
			throw new BadRequestException("Cannot book tickets for an event that has already ended");
		}

		if(orderRepository.existsByUser_IdAndIdempotencyKey(userId, idempotencyKey)) {
			throw new DuplicateIdempotencyKeyException("Duplicate idempotency key");
		}

		Map<UUID, Integer> reserved = new HashMap<>();
		registerCompletionCallback(reserved, RedisUtil.idempotencyKey(idempotencyKey, userId));

		LocalDateTime now = LocalDateTime.now();
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found"));
		OrderEntity orderEntity = OrderEntity.builder()
				.user(user)
				.status(OrderStatus.PENDING)
				.expiresAt(now.plusMinutes(15))
				.createdAt(now)
				.idempotencyKey(idempotencyKey)
				.build();

		List<OrderItemsEntity> orderItemsEntities = quantities.entrySet().stream().map(req -> {
			TicketTypeEntity ticket = tickets.get(req.getKey());

			return OrderItemsEntity.builder()
					.order(orderEntity)
					.ticketType(ticket)
					.price(ticket.getPrice())
					.quantity(req.getValue())
					.build();
		}).collect(Collectors.toList());

		BigDecimal totalAmount = orderItemsEntities.stream()
				.map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		orderEntity.setOrderItems(orderItemsEntities);
		orderEntity.setTotalAmount(totalAmount);
		orderRepository.save(orderEntity);

		Map<UUID, Integer> available = ticketTypeService.updateAvailableQuantity(quantities);
		reserved.putAll(quantities);

		available.forEach((id, quantity) -> {
			eventPublisher.publishEvent(new TicketQuantityChangedEvent(id, quantity));
		});

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

		if (!OrderStatus.PENDING.equals(order.getStatus())) {
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
                            RedisUtil.ticketStockKey(item.getTicketType().getId()),
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
		return order.getStatus().name();
	}

	@Override
	public Page<OrderListResponse> getOrders(OrderSearchRequest orderSearchRequest, Pageable pageable) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UUID userId = UUID.fromString(authentication.getName());
		
		Page<OrderEntity> orders = orderRepository.findByOrganizer(userId, orderSearchRequest, pageable);
		return orders.map(orderMapper::toOrderListResponse);
	}

	@Override
	public List<MonthlyTicketDTO> getMonthlyTickets(UUID organizerId, int year) {
		LocalDateTime from = LocalDate.of(year, 1, 1).atStartOfDay();
		LocalDateTime to = from.plusYears(1);

		Map<Integer, Long> data = orderItemsRepository
				.ticketsSoldByMonth(organizerId, OrderStatus.COMPLETED, from, to)
				.stream()
				.collect(Collectors.toMap(MonthlyTicketProjection::getMonth,
						MonthlyTicketProjection::getSold));

		return IntStream.rangeClosed(1, 12)
				.mapToObj(m -> new MonthlyTicketDTO(m, data.getOrDefault(m, 0L)))
				.toList();
	}

	@Override
	public OrderStatus getOrderStatus(UUID id) {
		OrderEntity order = orderRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Order not found"));
		return order.getStatus();
	}
	
	private void validateIdempotencyKeyFormat(String idempotencyKey) {
		if (idempotencyKey == null || idempotencyKey.isBlank() || idempotencyKey.length() > 100) {
			throw new BadRequestException("Idempotency-Key header is required (max 100 characters)"); 
		}
	}

	private void registerCompletionCallback(Map<UUID, Integer> reserved, String idempotencyKey) {
		TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
			@Override
			public void afterCompletion(int status) {
				try {
					if (status == STATUS_COMMITTED) {
						log.info("idempotency key");
					} else if (status == STATUS_ROLLED_BACK) {
						if (!reserved.isEmpty()) {
							Map<UUID, Integer> available = ticketTypeService.releaseStock(reserved);
							available.forEach((id, quantity) -> {
								messagingTemplate.convertAndSend(
										"/topic/events/" + id + "/available", quantity);
							});
						}
					} else {
						log.error("Không xác định được kết quả transaction, KHÔNG hoàn kho. key={}, reserved={}",
								idempotencyKey, reserved);
					}
				} catch (RuntimeException e) {
					log.error("Lỗi khi dọn dẹp sau transaction. key={}, reserved={}",
							idempotencyKey, reserved, e);
				}
			}
		});
	}

	private Map<UUID, Integer> toQuantities(OrderRequest orderRequest) {
		Map<UUID, Integer> quantities = new HashMap<>();
		if (orderRequest == null || orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
			throw new OrderItemRequiredException("Order item is required");
		}
		for (OrderItemsRequest item : orderRequest.getOrderItems()) {
			if (item == null || item.getTicketTypeId() == null) {
				throw new BadRequestException("Ticket type id is required");
			}
			if (item.getQuantity() <= 0) {
				throw new InvalidQuantityException("Ticket quantity cannot be negative");
			}
			if (quantities.putIfAbsent(item.getTicketTypeId(), item.getQuantity()) != null) {
				throw new BadRequestException("Duplicate ticket type in order");
			}
		}
		return quantities;
	}
}

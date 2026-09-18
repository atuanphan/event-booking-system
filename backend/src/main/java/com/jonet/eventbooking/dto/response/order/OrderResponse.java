package com.jonet.eventbooking.dto.response.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.response.promotion.PromotionResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
@AllArgsConstructor 
public class OrderResponse {
    private UUID id;
	private BigDecimal totalAmount;
	private String status;
	private LocalDateTime expiresAt;
	private List<OrderItemsResponse> orderItems;
	private PromotionResponse promotion;
}

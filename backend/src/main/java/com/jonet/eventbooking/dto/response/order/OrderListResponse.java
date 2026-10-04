package com.jonet.eventbooking.dto.response.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.response.user.UserResponse;

import lombok.Data;

@Data
public class OrderListResponse {
    private UUID id;
    private String status;
    private BigDecimal totalAmount;
    private UserResponse user;
    private List<OrderItemsResponse> orderItems;
    private LocalDateTime createdAt;
}

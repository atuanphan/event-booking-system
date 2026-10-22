package com.jonet.eventbooking.dto.response.promotion;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionResponse {
	private UUID id;
	private String code;
	private String discountType;
	private BigDecimal discountValue;
	private Integer maxUsage;
	private Integer usedCount;
}

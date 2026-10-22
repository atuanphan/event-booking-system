package com.jonet.eventbooking.dto.request.promotion;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionRequest {
	@NotBlank(message = "code is required!")
	@Size(max = 20, message = "code must be at most 20 characters!")
	private String code;

	@NotBlank(message = "discount type is required!")
	@Pattern(regexp = "PERCENTAGE|FIXED", message = "discount type must be PERCENTAGE or FIXED!")
	private String discountType;

	@NotNull(message = "discount value is required!")
	@Positive(message = "discount value must be greater than zero!")
	private BigDecimal discountValue;

	@PositiveOrZero(message = "max usage must be zero or greater!")
	private Integer maxUsage;
}

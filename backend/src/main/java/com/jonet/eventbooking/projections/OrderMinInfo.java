package com.jonet.eventbooking.projections;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderMinInfo {
	UUID getId();
	BigDecimal getTotalAmount();
}

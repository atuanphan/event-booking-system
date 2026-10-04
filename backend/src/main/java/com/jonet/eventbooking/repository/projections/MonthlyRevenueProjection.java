package com.jonet.eventbooking.repository.projections;

import java.math.BigDecimal;

public interface MonthlyRevenueProjection {
    Integer getMonth();
    BigDecimal getRevenue();
}

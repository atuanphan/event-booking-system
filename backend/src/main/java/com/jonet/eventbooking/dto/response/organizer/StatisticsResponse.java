package com.jonet.eventbooking.dto.response.organizer;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
public class StatisticsResponse {
    private BigDecimal totalRevenue;
    private long totalTicketsSold;
    private List<MonthlyRevenueResponse> monthlyRevenue;
    private List<BestSellingEventResponse> bestSellingEvent;
    private long eventsWithTicketSales;
}

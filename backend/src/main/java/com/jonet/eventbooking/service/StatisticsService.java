package com.jonet.eventbooking.service;

import java.util.List;
import java.util.UUID;

import com.jonet.eventbooking.dto.response.organizer.MonthlyRevenueResponse;
import com.jonet.eventbooking.dto.response.organizer.StatisticsResponse;

public interface StatisticsService {
    StatisticsResponse getStatisticsForOrganizer(UUID organizerId);
    List<MonthlyRevenueResponse> getMonthlyRevenue(UUID organizerId, int year);
}

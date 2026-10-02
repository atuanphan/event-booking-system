package com.jonet.eventbooking.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

@Builder 
@Data 
public class AdminDashboardResponse {
    private long totalEvents;
    private long totalUsers;
    private long totalVenues;
}

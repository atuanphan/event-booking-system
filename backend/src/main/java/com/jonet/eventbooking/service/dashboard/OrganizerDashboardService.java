package com.jonet.eventbooking.service.dashboard;

import java.util.UUID;

import com.jonet.eventbooking.dto.response.dashboard.OrganizerDashboardResponse;

public interface OrganizerDashboardService {
    OrganizerDashboardResponse getDashboard(UUID organizerId);
}

package com.jonet.eventbooking.service.impl;

import org.springframework.stereotype.Service;

import com.jonet.eventbooking.dto.response.dashboard.AdminDashboardResponse;
import com.jonet.eventbooking.repository.EventRepository;
import com.jonet.eventbooking.repository.UserRepository;
import com.jonet.eventbooking.repository.VenueRepository;
import com.jonet.eventbooking.service.dashboard.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;

    @Override
    public AdminDashboardResponse getAdminDashboard() {
        long totalEvents = eventRepository.count();
        long totalUsers = userRepository.count();
        long totalVenues = venueRepository.count();

        return AdminDashboardResponse.builder()
                .totalEvents(totalEvents)
                .totalUsers(totalUsers)
                .totalVenues(totalVenues)
                .build();
    }

}

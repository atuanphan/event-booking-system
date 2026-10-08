package com.jonet.eventbooking.controller.organizer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.response.dashboard.OrganizerDashboardResponse;
import com.jonet.eventbooking.service.dashboard.OrganizerDashboardService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping ("/v1/organizer/dashboard")
@RequiredArgsConstructor 
@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
public class OrganizerDashboardController {
    private final OrganizerDashboardService organizerDashboardService;

    @GetMapping
    public ResponseEntity<OrganizerDashboardResponse> getDashboard(@AuthenticationPrincipal UUID organizerId) {
        return ResponseEntity.ok(organizerDashboardService.getDashboard(organizerId));
    }
    
}

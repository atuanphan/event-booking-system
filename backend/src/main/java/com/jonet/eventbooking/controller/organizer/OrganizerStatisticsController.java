package com.jonet.eventbooking.controller.organizer;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.response.organizer.StatisticsResponse;
import com.jonet.eventbooking.service.StatisticsService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

@RestController 
@RequiredArgsConstructor 
@RequestMapping("/v1/organizer/statistics")
@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
public class OrganizerStatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<StatisticsResponse> getStatistics(@AuthenticationPrincipal UUID organizerId) {
        return ResponseEntity.ok(statisticsService.getStatisticsForOrganizer(organizerId));
    }
    
}

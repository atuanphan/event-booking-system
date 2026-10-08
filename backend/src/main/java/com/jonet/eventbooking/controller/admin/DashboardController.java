package com.jonet.eventbooking.controller.admin;

import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.response.dashboard.AdminDashboardResponse;
import com.jonet.eventbooking.service.dashboard.AdminDashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/v1/admin/dashboard")
@RequiredArgsConstructor 
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {
    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public ResponseEntity<AdminDashboardResponse> getMethodName() {
        AdminDashboardResponse response = adminDashboardService.getAdminDashboard();
        return ResponseEntity.ok(response);
    }
    
}

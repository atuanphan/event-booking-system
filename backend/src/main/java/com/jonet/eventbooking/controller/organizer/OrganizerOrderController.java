package com.jonet.eventbooking.controller.organizer;

import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.request.order.OrderSearchRequest;
import com.jonet.eventbooking.dto.response.order.OrderListResponse;
import com.jonet.eventbooking.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController 
@RequiredArgsConstructor 
@RequestMapping("/v1/organizer/orders")
@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
public class OrganizerOrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderListResponse>> getOrders(@ModelAttribute OrderSearchRequest orderSearchRequest) {
        return ResponseEntity.ok()
                .body(orderService.getOrders(orderSearchRequest, PageRequest.of(orderSearchRequest.getPage() - 1, orderSearchRequest.getPageSize())));
    }
    
}

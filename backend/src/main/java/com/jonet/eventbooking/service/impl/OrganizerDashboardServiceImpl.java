package com.jonet.eventbooking.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jonet.eventbooking.converter.EventMapper;
import com.jonet.eventbooking.dto.MonthlyTicketDTO;
import com.jonet.eventbooking.dto.response.dashboard.OrganizerDashboardResponse;
import com.jonet.eventbooking.entity.EventEntity;
import com.jonet.eventbooking.enums.OrderStatus;
import com.jonet.eventbooking.repository.EventRepository;
import com.jonet.eventbooking.repository.OrderRepository;
import com.jonet.eventbooking.service.OrderService;
import com.jonet.eventbooking.service.dashboard.OrganizerDashboardService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class OrganizerDashboardServiceImpl implements OrganizerDashboardService {
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final EventMapper eventMapper;

    @Override
    public OrganizerDashboardResponse getDashboard(UUID organizerId) {
        long totalEvents = eventRepository.countOngoingByOrganizer(organizerId, LocalDateTime.now());
        long totalTicketsSold = orderRepository.totalTicketsSold(organizerId, OrderStatus.COMPLETED);   
        BigDecimal totalRevenue = orderRepository.totalRevenue(organizerId, OrderStatus.PENDING); 
        long upcomingEvents = eventRepository.countUpcomingByOrganizer(organizerId, LocalDateTime.now());
        List<MonthlyTicketDTO> ticketsByMonth = orderService.getMonthlyTickets(organizerId, LocalDateTime.now().getYear());
        List<EventEntity> events = eventRepository.findByUserId(organizerId);

        return OrganizerDashboardResponse.builder()
                .totalEvents(totalEvents)
                .totalTicketsSold(totalTicketsSold)
                .totalRevenue(totalRevenue)
                .upcomingEvents(upcomingEvents)
                .ticketsByMonth(ticketsByMonth)
                .eventResponse(events.stream().map(eventMapper::eventResponse).toList())
                .build();
    }

}

package com.jonet.eventbooking.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.dto.response.organizer.BestSellingEventResponse;
import com.jonet.eventbooking.dto.response.organizer.MonthlyRevenueResponse;
import com.jonet.eventbooking.dto.response.organizer.StatisticsResponse;
import com.jonet.eventbooking.enums.OrderStatus;
import com.jonet.eventbooking.repository.OrderItemRepository;
import com.jonet.eventbooking.repository.OrderRepository;
import com.jonet.eventbooking.repository.projections.MonthlyRevenueProjection;
import com.jonet.eventbooking.service.StatisticsService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class StatisticsServiceImpl implements StatisticsService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public StatisticsResponse getStatisticsForOrganizer(UUID organizerId) {
        BigDecimal totalRevenue = orderRepository.totalRevenue(organizerId, OrderStatus.COMPLETED);
        long totalTicketsSold = orderRepository.totalTicketsSold(organizerId, OrderStatus.COMPLETED);
        List<MonthlyRevenueResponse> monthlyRevenue = getMonthlyRevenue(organizerId, LocalDateTime.now().getYear());
        List<BestSellingEventResponse> bestSellingEvent = orderItemRepository.findTopSellingEvents(organizerId, OrderStatus.COMPLETED, PageRequest.of(0, 4))
                .stream()
                .map(projection -> BestSellingEventResponse.builder()
                        .eventName(projection.getEventName())
                        .ticketsSold(projection.getTicketsSold())
                        .ticketSalesPercentage(projection.getTicketsSold() * 100 / projection.getTotalQuantity())
                        .build())
                .toList();
        long eventsWithTicketSales = orderItemRepository.countEventsWithTicketSales(organizerId, OrderStatus.COMPLETED);
        
        return StatisticsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalTicketsSold(totalTicketsSold)
                .monthlyRevenue(monthlyRevenue)
                .bestSellingEvent(bestSellingEvent)
                .eventsWithTicketSales(eventsWithTicketSales)
                .build();
    }

    @Override
    public List<MonthlyRevenueResponse> getMonthlyRevenue(UUID organizerId, int year) {
        LocalDateTime from = LocalDate.of(year, 1, 1).atStartOfDay();
		LocalDateTime to = from.plusYears(1);

		Map<Integer, BigDecimal> data = orderRepository
				.revenueByMonth(organizerId, OrderStatus.COMPLETED, from, to)
				.stream()
				.collect(Collectors.toMap(MonthlyRevenueProjection::getMonth,
						MonthlyRevenueProjection::getRevenue));

		return IntStream.rangeClosed(1, 12)
				.mapToObj(m -> new MonthlyRevenueResponse(m, data.getOrDefault(m, BigDecimal.ZERO)))
				.toList();
    }

}

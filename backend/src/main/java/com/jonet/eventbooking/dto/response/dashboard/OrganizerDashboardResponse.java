package com.jonet.eventbooking.dto.response.dashboard;

import java.math.BigDecimal;
import java.util.List;

import com.jonet.eventbooking.dto.MonthlyTicketDTO;
import com.jonet.eventbooking.dto.response.event.EventResponse;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder 
public class OrganizerDashboardResponse {
    private long totalEvents;
    private long totalTicketsSold;
    private BigDecimal totalRevenue;
    private long totalAttendees;
    private long upcomingEvents;
    private List<MonthlyTicketDTO> ticketsByMonth;
    private List<EventResponse> eventResponse;
}

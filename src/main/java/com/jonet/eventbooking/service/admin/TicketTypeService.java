package com.jonet.eventbooking.service.admin;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.jonet.eventbooking.dto.request.ticket_type.TicketTypeRequest;
import com.jonet.eventbooking.dto.response.ticket_type.TicketTypeResponse;

public interface TicketTypeService {
	public List<TicketTypeResponse> getTicketTypes(TicketTypeRequest ticketTypeRequest, Pageable pageable);
	public int totalPage();
}

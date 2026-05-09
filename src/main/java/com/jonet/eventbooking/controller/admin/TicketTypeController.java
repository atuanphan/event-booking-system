package com.jonet.eventbooking.controller.admin;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jonet.eventbooking.dto.PagedResult;
import com.jonet.eventbooking.dto.request.ticket_type.TicketTypeRequest;
import com.jonet.eventbooking.service.admin.TicketTypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class TicketTypeController {
	private final TicketTypeService ticketTypeService;

	@GetMapping("/ticket-types")
	public ResponseEntity<PagedResult> getTicketTypes(TicketTypeRequest ticketTypeRequest) {
		return ResponseEntity.ok(PagedResult.of(
				ticketTypeService.getTicketTypes(ticketTypeRequest,
						PageRequest.of(ticketTypeRequest.getPage() - 1, ticketTypeRequest.getPageSize())),
				ticketTypeService.totalPage()));
	}
}

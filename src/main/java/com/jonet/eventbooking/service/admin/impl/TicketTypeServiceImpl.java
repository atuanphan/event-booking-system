package com.jonet.eventbooking.service.admin.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jonet.eventbooking.converter.TicketTypeMapper;
import com.jonet.eventbooking.dto.request.ticket_type.TicketTypeRequest;
import com.jonet.eventbooking.dto.response.ticket_type.TicketTypeResponse;
import com.jonet.eventbooking.entity.TicketTypeEntity;
import com.jonet.eventbooking.repository.TicketTypeRepository;
import com.jonet.eventbooking.service.admin.TicketTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketTypeServiceImpl implements TicketTypeService{
	private final TicketTypeRepository ticketTypeRepository;
	private final TicketTypeMapper ticketTypeMapper;
	
	@Override
	public List<TicketTypeResponse> getTicketTypes(TicketTypeRequest ticketTypeRequest, Pageable pageable) {
		Page<TicketTypeEntity> ticketTypes = ticketTypeRepository.findAll(pageable);
		return ticketTypes.stream().map(ticketTypeMapper::toTicketTypeResponse).toList();
	}

	@Override
	public int totalPage() {
		double total = (double)ticketTypeRepository.count() / 10;
		return (int) Math.ceil(total);
	}

}

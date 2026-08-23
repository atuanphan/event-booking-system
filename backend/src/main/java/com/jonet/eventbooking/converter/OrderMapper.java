package com.jonet.eventbooking.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jonet.eventbooking.dto.request.order.OrderItemsRequest;
import com.jonet.eventbooking.dto.request.order.OrderRequest;
import com.jonet.eventbooking.entity.OrderEntity;
import com.jonet.eventbooking.entity.OrderItemsEntity;

@Mapper(componentModel = "spring")
public interface OrderMapper {
	
	@Mapping(ignore = true, target = "order")
	@Mapping(ignore = true, target = "seatId")
	@Mapping(ignore = true, target = "ticketType")
	OrderItemsEntity toOrderItemsEntity(OrderItemsRequest orderItemsRequest);
	
	OrderEntity toOrderEntity(OrderRequest orderRequest);

}

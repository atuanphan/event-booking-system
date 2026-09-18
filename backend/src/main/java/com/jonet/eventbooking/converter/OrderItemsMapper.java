package com.jonet.eventbooking.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jonet.eventbooking.dto.response.order.OrderItemsResponse;
import com.jonet.eventbooking.entity.OrderItemsEntity;

@Mapper(componentModel = "spring", uses = EventSummaryMapper.class)
public interface OrderItemsMapper {

    @Mapping(source = "ticketType.event", target = "event")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(entity))")
    OrderItemsResponse toResponse(OrderItemsEntity entity);

    List<OrderItemsResponse> toResponseList(
        List<OrderItemsEntity> entities
    );

    default java.math.BigDecimal calculateSubtotal(OrderItemsEntity entity) {
        if (entity.getPrice() == null || entity.getQuantity() == null) {
            return null;
        }
        return entity.getPrice().multiply(java.math.BigDecimal.valueOf(entity.getQuantity()));
    }
}
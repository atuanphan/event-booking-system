package com.jonet.eventbooking.dto.request.order;

import java.time.LocalDate;
import java.util.UUID;

import com.jonet.eventbooking.dto.AbstractDTO;
import com.jonet.eventbooking.enums.OrderStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data 
@EqualsAndHashCode(callSuper = false)
public class OrderSearchRequest extends AbstractDTO{
    private UUID eventId;
    private OrderStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
}

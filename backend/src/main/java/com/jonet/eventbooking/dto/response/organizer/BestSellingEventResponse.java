package com.jonet.eventbooking.dto.response.organizer;

import lombok.Builder;
import lombok.Data;

@Data 
@Builder
public class BestSellingEventResponse {
    private String eventName;   
    private long ticketsSold;
    private long ticketSalesPercentage;
}

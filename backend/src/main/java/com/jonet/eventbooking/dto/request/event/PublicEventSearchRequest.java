package com.jonet.eventbooking.dto.request.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.jonet.eventbooking.dto.AbstractDTO;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Builder 
@Data 
@AllArgsConstructor
@NoArgsConstructor
public class PublicEventSearchRequest extends AbstractDTO{
    private UUID id;
	private String name;
	private String venue;
    private Integer price;
	private LocalDateTime startTime;
}

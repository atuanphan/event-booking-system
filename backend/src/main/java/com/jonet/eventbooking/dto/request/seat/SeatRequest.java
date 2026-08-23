package com.jonet.eventbooking.dto.request.seat;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatRequest {
	private UUID id;
	private String seatRow;
	private String seatNumber;
	private String status;
	private Integer version;
}

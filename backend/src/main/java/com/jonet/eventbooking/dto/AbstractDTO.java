package com.jonet.eventbooking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractDTO {
	private int page = 1;
	private int pageSize = 10;
}

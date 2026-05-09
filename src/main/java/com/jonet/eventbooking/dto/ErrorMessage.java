package com.jonet.eventbooking.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
	private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, Object> errors;
}
